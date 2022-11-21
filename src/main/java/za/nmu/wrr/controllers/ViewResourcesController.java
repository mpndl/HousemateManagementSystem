package za.nmu.wrr.controllers;

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
import za.nmu.wrr.models.Chore;
import za.nmu.wrr.models.Housemate;
import za.nmu.wrr.models.Resource;
import za.nmu.wrr.circle.CircleView;

import java.io.IOException;
import java.sql.ResultSet;

public class ViewResourcesController extends Controller {
    public final static String v = "View Resources";
    private final ObservableList<Resource> resources = FXCollections.observableArrayList();
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private ObservableList<Chore> chores = FXCollections.observableArrayList();
    private final String VIEW = "view_vr_";
    private final String HVIEW = "view_vh_";
    private Scene dashboardScene;
    private Stage vrStage;
    private boolean linked = false;
    public ViewResourcesController() {}
    public ViewResourcesController(Scene dashboardScene,Stage vrStage) {
        this.dashboardScene = dashboardScene;
        this.vrStage = vrStage;
        linksRunnable.add(() -> setupDashboardLinks(dashboardScene));
    }

    private void onDashboardLinkClicked(CircleView cv) {
        if (loggedInUser != null) {
            if (!linked) {
                setupResources();
                linkToScene(vrStage);
                linked = true;
            }
            if (cv == null) vrStage.showAndWait();
            else if (!cv.dragging()) vrStage.showAndWait();
        }
    }

    private void setupDashboardLinks(Scene dashboardScene) {
        links.put(v, this::onDashboardLinkClicked);

        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#vr_dashboard");
        avail(hpMaintainHousemates);
        hpMaintainHousemates.setOnAction(event -> {
            onDashboardLinkClicked(null);
        });
    }

    private void setupResources() {
        ResultSet rs = database.executeQuery("SELECT * FROM Resource");
        try {
            while (rs.next()) {
                Resource resource = new Resource(rs.getString("resourceName"), Integer.parseInt(rs.getString("isFinished")),
                        rs.getString("housemateID"));
                resources.add(resource);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void linkToScene(Stage vrStage) {
        TableView<Resource> tvResources = (TableView<Resource>) vrStage.getScene().lookup(getID(VIEW, "table"));

        TableColumn tcResourceName = new TableColumn("Resource Name");
        tcResourceName.setCellValueFactory(new PropertyValueFactory("resourceName"));
        tcResourceName.setPrefWidth(100);

        TableColumn tcIsFinished = new TableColumn("Is Finished");
        tcIsFinished.setCellValueFactory(new PropertyValueFactory("isFinished"));
        tcIsFinished.setPrefWidth(100);

        TableColumn tcHousemateID = new TableColumn("Housemate ID");
        tcHousemateID.setCellValueFactory(new PropertyValueFactory("housemateID"));
        tcHousemateID.setPrefWidth(100);

        tvResources.getColumns().addAll(tcResourceName, tcIsFinished, tcHousemateID);

        tvResources.setItems(resources);

        Button btnViewChores = (Button) vrStage.getScene().lookup(getID(VIEW, "view_chores"));
        Button btnViewHousemates = (Button) vrStage.getScene().lookup(getID(VIEW, "view_housemates"));
        btnViewChores.disableProperty().bind(Bindings.createBooleanBinding(() -> tvResources.getSelectionModel().getSelectedItem() == null, tvResources.getSelectionModel().selectedItemProperty()));
        btnViewHousemates.disableProperty().bind(Bindings.createBooleanBinding(() -> tvResources.getSelectionModel().getSelectedItem() == null, tvResources.getSelectionModel().selectedItemProperty()));


        Button btnCancel = (Button) vrStage.getScene().lookup(getID(VIEW, "cancel"));
        btnCancel.setOnAction(event -> {
            vrStage.close();
        });

        btnViewHousemates.setOnAction(actionEvent -> {
            try {
                Stage stage = createViewResourceHousemate(vrStage, tvResources.getSelectionModel().getSelectedItem());
                setupHousemate(tvResources.getSelectionModel().getSelectedItem());
                linkToResourceHousemateScene(stage);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        btnViewChores.setOnAction(actionEvent -> {
            try {
                Stage stage = createViewResourceChores(vrStage, tvResources.getSelectionModel().getSelectedItem());
                setupChores(tvResources.getSelectionModel().getSelectedItem());
                linkToResourceChoreScene(stage);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void linkToResourceChoreScene(Stage vrcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) vrcStage.getScene().lookup(getID(VIEW, "table"));
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

        Button btnCancel = (Button) vrcStage.getScene().lookup(getID(VIEW, "cancel"));
        btnCancel.setOnAction(event -> {
            vrcStage.close();
        });
    }

    private void linkToResourceHousemateScene(Stage vrhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) vrhStage.getScene().lookup(getID(HVIEW, "table"));

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

        Button btnCancel = (Button) vrhStage.getScene().lookup(getID(HVIEW, "cancel"));
        btnCancel.setOnAction(event -> {
            vrhStage.close();
        });
    }

    private void setupChores(Resource resource) {
        ResultSet rs;
        chores = FXCollections.observableArrayList();
        rs = database.executeQuery("SELECT * FROM Chore INNER JOIN Usage ON Chore.choreID = Usage.choreID WHERE Usage.resourceName = '" + resource.resourceName.getValue() + "'");

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

    private void setupHousemate(Resource resource) {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate INNER JOIN Resource ON Housemate.housemateID = Resource.housemateID WHERE Housemate.housemateID = " + resource.housemateID.getValue());
        housemates = FXCollections.observableArrayList();
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

    private Stage createViewResourceChores(Stage owner, Resource selectedItem) throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("resourceChoresView.fxml"));

        Scene scene = new Scene(loader.load());

        Label lblHousemate = (Label) scene.lookup("#label_chores");
        lblHousemate.setText("View chores for " + selectedItem.resourceName.getValue());

        stage.setScene(scene);
        stage.setTitle("View chores for " + selectedItem.resourceName.getValue());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }

    private Stage createViewResourceHousemate(Stage owner, Resource selectedItem) throws IOException {
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("viewHousematesView.fxml"));

        Scene scene = new Scene(loader.load());

        Label lblHousemate = (Label) scene.lookup("#lbl_housemates");
        lblHousemate.setText("View Housemate for " + selectedItem.resourceName.getValue());

        stage.setScene(scene);
        stage.setTitle("View housemate for " + selectedItem.resourceName.getValue());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }
}
