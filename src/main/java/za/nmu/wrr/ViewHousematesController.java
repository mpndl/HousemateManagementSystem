package za.nmu.wrr;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.ResultSet;

public class ViewHousematesController extends Controller {
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private ObservableList<Chore> chores = FXCollections.observableArrayList();
    private final String VIEW = "view_vh_";
    private Scene dashboardScene;
    private Stage vhStage;
    private boolean linked = false;
    public ViewHousematesController() {}
    public ViewHousematesController(Scene dashboardScene,Stage vhStage) {
        this.dashboardScene = dashboardScene;
        this.vhStage = vhStage;
        setupDashboardLinks(dashboardScene, vhStage);
    }

    private void setupDashboardLinks(Scene dashboardScene, Stage vhStage) {
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#vh_dashboard");
        if (loggedInUser != null) {
            hpMaintainHousemates.setOnAction(event -> {
                if (!linked) {
                    setupHousemates();
                    linkToScene(vhStage);
                    linked = true;
                }
                vhStage.showAndWait();
            });
        }
    }

    private void setupHousemates() {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate");
        try {
            while (rs.next()) {
                Housemate housemate = new Housemate();
                housemate.housemateID.setValue(rs.getString("housemateID"));
                housemate.username.setValue(rs.getString("username"));
                housemate.firstName.setValue(rs.getString("firstName"));
                housemate.lastName.setValue(rs.getString("lastName"));
                housemate.password.setValue(rs.getString("password"));
                housemate.phoneNumber.setValue(rs.getString("phoneNumber"));
                housemate.isLeader.setValue(rs.getInt("isLeader"));

                housemates.add(housemate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void linkToScene(Stage vhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) vhStage.getScene().lookup("#"+VIEW+"table");

        TableColumn tcHousemateID = new TableColumn("Housemate ID");
        tcHousemateID.setCellValueFactory(new PropertyValueFactory<>("housemateID"));
        tcHousemateID.setPrefWidth(100);

        TableColumn tcUsername = new TableColumn("Username");
        tcUsername.setCellValueFactory(new PropertyValueFactory("username"));
        tcUsername.setPrefWidth(100);

        TableColumn tcFirstname = new TableColumn("First Name");
        tcFirstname.setCellValueFactory(new PropertyValueFactory("firstName"));
        tcFirstname.setPrefWidth(100);

        TableColumn tcLastname = new TableColumn("Last Name");
        tcLastname.setCellValueFactory(new PropertyValueFactory("lastName"));
        tcLastname.setPrefWidth(100);

        TableColumn tcPhoneNumber = new TableColumn("Phone Number");
        tcPhoneNumber.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
        tcPhoneNumber.setPrefWidth(100);
        TableColumn tcPassword = new TableColumn("Password");
        tcPassword.setCellValueFactory(new PropertyValueFactory("password"));
        tcPassword.setPrefWidth(100);

        TableColumn tcIsLeader = new TableColumn("Leader");
        tcIsLeader.setCellValueFactory(new PropertyValueFactory<>("isLeader"));
        tcIsLeader.setPrefWidth(100);

        tvHousemates.getColumns().addAll(tcHousemateID, tcUsername, tcFirstname, tcLastname, tcPhoneNumber, tcPassword, tcIsLeader);

        tvHousemates.setItems(housemates);

        Button btnViewChores = (Button) vhStage.getScene().lookup("#" + VIEW + "view_chores");
        btnViewChores.disableProperty().bind(Bindings.createBooleanBinding(() -> tvHousemates.getSelectionModel().getSelectedItem() == null, tvHousemates.getSelectionModel().selectedItemProperty()));


        Button btnCancel = (Button) vhStage.getScene().lookup("#"+VIEW+"cancel");
        btnCancel.setOnAction(event -> {
            vhStage.close();
        });

        Button btnViewResources = (Button) vhStage.getScene().lookup("#"+ VIEW + "view_resources");

        btnViewChores.setOnAction(actionEvent -> {
            try {
                setupChores(tvHousemates.getSelectionModel().getSelectedItem());
                Stage stage = createViewHousemateChore(vhStage, tvHousemates.getSelectionModel().getSelectedItem());
                linkToHousemateChoreScene(stage);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void linkToHousemateChoreScene(Stage vhcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) vhcStage.getScene().lookup("#"+ VIEW + "table");
        TableColumn tcChoreID = new TableColumn("Chore ID");
        tcChoreID.setCellValueFactory(new PropertyValueFactory<>("choreID"));
        tcChoreID.setPrefWidth(100);

        TableColumn tcAreaName = new TableColumn("Area Name");
        tcAreaName.setCellValueFactory(new PropertyValueFactory("areaName"));
        tcAreaName.setPrefWidth(100);

        TableColumn tcDescription = new TableColumn("Description");
        tcDescription.setCellValueFactory(new PropertyValueFactory("description"));
        tcDescription.setPrefWidth(100);

        TableColumn tcCompleted = new TableColumn("Completed");
        tcCompleted.setCellValueFactory(new PropertyValueFactory("isCompleted"));
        tcCompleted.setPrefWidth(100);

        TableColumn tcDateCompleted = new TableColumn("Date Completed");
        tcDateCompleted.setCellValueFactory(new PropertyValueFactory("dateCompleted"));
        tcDateCompleted.setPrefWidth(100);

        tvChores.getColumns().addAll(tcChoreID, tcAreaName, tcDescription, tcCompleted, tcDateCompleted);

        tvChores.setItems(chores);

        Button btnCancel = (Button) vhcStage.getScene().lookup("#"+VIEW+"cancel");
        btnCancel.setOnAction(event -> {
            vhcStage.close();
        });
    }

    private void setupChores(Housemate housemate) {
        ResultSet rs;
        rs = database.executeQuery("SELECT Chore.choreID, Chore.description, Chore.isCompleted, Chore.dateCompleted, Chore.areaName FROM Chore INNER JOIN Swap ON Chore.choreID = Swap.choreID WHERE HousemateID = " + housemate.housemateID.getValue());

        try {
            while (rs.next()) {
                Chore chore = new Chore();
                chore.choreID.setValue(rs.getString("choreID"));
                chore.description.setValue(rs.getString("description"));
                chore.isCompleted.setValue(Integer.parseInt(rs.getString("isCompleted")));
                chore.dateCompleted.setValue(rs.getString("dateCompleted"));
                chore.areaName.setValue(rs.getString("areaName"));

                chores.add(chore);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage createViewHousemateChore(Stage owner, Housemate selectedItem) throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("housemateChoresView.fxml"));

        Scene scene = new Scene(loader.load());

        Label lblHousemate = (Label) scene.lookup("#label_housemate");
        lblHousemate.setText("View chores for " + selectedItem.firstName.getValue() + " " + selectedItem.lastName.getValue());

        stage.setScene(scene);
        stage.setTitle("View chores for " + selectedItem.firstName.getValue() + " " + selectedItem.lastName.getValue());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }
}