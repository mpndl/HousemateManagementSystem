package za.nmu.wrr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AssignChoreController extends Controller {
    private ObservableList<Chore> chores = FXCollections.observableArrayList();
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String ASSIGN = "ac_";
    private Scene dashboardScene;
    private Stage acStage;
    private boolean linked = false;
    public AssignChoreController(){}
    public AssignChoreController(Scene dashboardScene,Stage acStage) {
        this.dashboardScene = dashboardScene;
        this.acStage = acStage;
        setupDashboardLinks(dashboardScene, acStage);
    }

    private void clearTables(TableView<Chore> choreTableView, TableView<Housemate> housemateTableView) {
        if (choreTableView != null) {
            chores = FXCollections.observableArrayList();
            choreTableView.setItems(chores);
        }

        if (housemateTableView != null) {
            housemates = FXCollections.observableArrayList();
            housemateTableView.setItems(housemates);
        }
    }

    private void setupChores() {
        TableView<Chore> tvChores = (TableView<Chore>) acStage.getScene().lookup("#chores_"+ ASSIGN + "table");
        clearTables(tvChores, null);
        ResultSet rs = database.executeQuery("SELECT * FROM Chore LEFT JOIN Swap ON Chore.choreID = Swap.choreID WHERE Swap.choreID IS NULL");
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

    private void setupDashboardLinks(Scene dashboardScene, Stage acStage) {
        Hyperlink hpAssignChore = (Hyperlink) dashboardScene.lookup("#ac_dashboard");
        if (loggedInUser != null) {
            if (loggedInUser.isLeader.getValue() == 0)
                hpAssignChore.setDisable(true);
            else {
                hpAssignChore.setOnAction(event -> {
                    TableView<Chore> tvChores = (TableView<Chore>) acStage.getScene().lookup("#chores_"+ ASSIGN + "table");
                    TableView<Housemate> tvHousemates = (TableView<Housemate>) acStage.getScene().lookup("#housemates_" +ASSIGN+"table");

                    clearTables(tvChores, tvHousemates);
                    setupHousemates();
                    if (!linked) {
                        linkToScene(acStage);
                        linked = true;
                    }
                    acStage.showAndWait();
                });
            }
        }
    }

    private void linkToScene(Stage acStage) {
        linkHousematesToScene(acStage);
        linkChoresToScene(acStage);

        Button btnCancel = (Button) acStage.getScene().lookup("#"+ASSIGN+"cancel");
        Button btnAssign = (Button) acStage.getScene().lookup("#"+ASSIGN+"assign");
        btnCancel.setOnAction(event -> {
            TextField tfHousemateID = (TextField) acStage.getScene().lookup("#"+ASSIGN+"housemateid");
            TextField tfUsername = (TextField) acStage.getScene().lookup("#"+ASSIGN+"username");
            TextField tfFirstname = (TextField) acStage.getScene().lookup("#"+ASSIGN+"firstname");
            TextField tfLastname = (TextField) acStage.getScene().lookup("#"+ASSIGN+"lastname");
            TextField tfPhoneNumber = (TextField) acStage.getScene().lookup("#"+ASSIGN+"phonenumber");
            TextField tfPassword = (TextField) acStage.getScene().lookup("#"+ASSIGN+"password");
            CheckBox cbIsLeader = (CheckBox) acStage.getScene().lookup("#"+ ASSIGN + "leader");

            tfHousemateID.clear();
            tfUsername.clear();
            tfFirstname.clear();
            tfLastname.clear();
            tfPhoneNumber.clear();
            tfPassword.clear();
            cbIsLeader.setSelected(false);

            acStage.close();
        });

        btnAssign.setOnAction(actionEvent -> {
            TextField tfHousemateID = (TextField) acStage.getScene().lookup("#"+ASSIGN+"housemateid");
            TextField tfChoreID = (TextField) acStage.getScene().lookup("#"+ ASSIGN + "choreid");

            database.executeInsert("INSERT INTO Swap(housemateID, choreID) VALUES(" + tfHousemateID.getText() + "," + tfChoreID.getText() + ")");
            setupChores();

            TextField tfAreaName = (TextField) acStage.getScene().lookup("#"+ ASSIGN + "areaname");
            TextArea taDescription = (TextArea) acStage.getScene().lookup("#"+ ASSIGN + "description");

            tfChoreID.setText("");
            tfAreaName.setText("");
            taDescription.setText("");

            TableView<Chore> tvChores = (TableView<Chore>) acStage.getScene().lookup("#chores_"+ ASSIGN + "table");
            tvChores.getSelectionModel().clearSelection();
        });

        TextField tfHousemateID = (TextField) acStage.getScene().lookup("#"+ASSIGN+"housemateid");
        TextField tfChoreID = (TextField) acStage.getScene().lookup("#"+ ASSIGN + "choreid");

        tfHousemateID.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnAssign, tfChoreID,tfChoreID);
        });

        tfChoreID.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnAssign, tfChoreID,tfChoreID);
        });
    }

    private void linkHousematesToScene(Stage acStage) {
        TextField tfHousemateID = (TextField) acStage.getScene().lookup("#"+ASSIGN+"housemateid");
        TextField tfUsername = (TextField) acStage.getScene().lookup("#"+ASSIGN+"username");
        TextField tfFirstname = (TextField) acStage.getScene().lookup("#"+ASSIGN+"firstname");
        TextField tfLastname = (TextField) acStage.getScene().lookup("#"+ASSIGN+"lastname");
        TextField tfPhoneNumber = (TextField) acStage.getScene().lookup("#"+ASSIGN+"phonenumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) acStage.getScene().lookup("#"+ASSIGN+"password");
        CheckBox cbIsLeader = (CheckBox) acStage.getScene().lookup("#"+ ASSIGN + "leader");
        TableView<Housemate> tvHousemates = (TableView<Housemate>) acStage.getScene().lookup("#housemates_" +ASSIGN+"table");

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

        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                cbIsLeader.setSelected(temp.isLeader.getValue() != 0);
                tfUsername.setText(temp.username.getValue());
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());

                setupChores();
            }
        });
    }

    private void linkChoresToScene(Stage acStage) {
        TableView<Chore> tvChores = (TableView<Chore>) acStage.getScene().lookup("#chores_"+ ASSIGN + "table");
        TextField tfChoreID = (TextField) acStage.getScene().lookup("#"+ ASSIGN + "choreid");
        TextField tfAreaName = (TextField) acStage.getScene().lookup("#"+ ASSIGN + "areaname");
        TextArea taDescription = (TextArea) acStage.getScene().lookup("#"+ ASSIGN + "description");
        CheckBox cbCompleted = (CheckBox) acStage.getScene().lookup("#"+ ASSIGN + "completed");
        DatePicker dpDateCompleted = (DatePicker) acStage.getScene().lookup("#"+ ASSIGN + "datecompleted");

        TableColumn tcChoreID = new TableColumn("ChoreID ID");
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
        System.out.println(chores.size());

        tvChores.setItems(chores);

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if(temp != null) {
                cbCompleted.setSelected(temp.isCompleted.getValue() != 0);
                tfChoreID.setText(temp.choreID.getValue());
                tfAreaName.setText(temp.areaName.getValue());
                taDescription.setText(temp.description.getValue());
                cbCompleted.setText(temp.isCompleted.getValue() + "");
                if (temp.dateCompleted.getValue() != null) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(temp.dateCompleted.getValue(), dateTimeFormatter);
                    dpDateCompleted.setValue(localDate);
                }
            }
        });
    }

    private void setupDisableFuncs(Button func, TextField choreID, TextField housemateID) {
        func.setDisable(choreID.getText().isEmpty() || housemateID.getText().isEmpty());
    }
}
