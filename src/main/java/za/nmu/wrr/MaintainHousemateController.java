package za.nmu.wrr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Optional;

public class MaintainHousemateController extends Controller {
    private final Database database = new Database();
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String ADD = "add_mh_";
    private final String EDIT = "edit_mh_";
    private final String REMOVE = "remove_mh_";
    public MaintainHousemateController(){}
    public MaintainHousemateController(Scene dashboardScene,Stage mhStage) {
        setupDashboardLinks(dashboardScene, mhStage);
        setupHousemates();
        // Add
        linkToScene(mhStage, ADD);
        addHousemates(mhStage);
        // Edit
        linkToScene(mhStage, EDIT);
        editHousemates(mhStage);
        // Remove
        linkToScene(mhStage, REMOVE);
        removeHousemates(mhStage);
    }

    private void removeHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+ REMOVE + "table");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "housemateid");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "firstname");
        tfFirstname.setDisable(true);
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "lastname");
        tfLastname.setDisable(true);
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "phonenumber");
        tfPhoneNumber.setDisable(true);
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "password");
        tfPassword.setDisable(true);
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#"+ REMOVE + "leader");
        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
                cbIsLeader.setDisable(temp.isLeader.getValue() == 0);
            }
        });

        Button btnRemove = (Button) mhStage.getScene().lookup("#"+ REMOVE + "remove");
        btnRemove.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if(index != -1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Remove Housemate");
                alert.setHeaderText("Remove " + "[ID="+housemate.housemateID.getValue()+"] " + housemate.firstName.getValue() + " " + housemate.lastName.getValue());
                alert.setContentText("Are you sure you want to remove housemate?");

                ButtonType btRemove = new ButtonType("Remove");
                ButtonType btCancel = new ButtonType("Cancel");

                alert.getButtonTypes().setAll(btRemove, btCancel);
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == btRemove) {

                    housemates.remove(index);

                    database.executeUpdate("DELETE FROM Housemate WHERE housemateID = '" + housemate.housemateID.getValue() + "'");

                    tfHousemateID.setText("");
                    tfFirstname.setText("");
                    tfLastname.setText("");
                    tfPhoneNumber.setText("");
                    tfPassword.setText("");
                    tvHousemates.getSelectionModel().clearSelection();
                }
                else
                    alert.close();
            }
        });
    }

    private void linkToScene(Stage mhStage, String n) {
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+n+"housemateid");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+n+"firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+n+"lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+n+"phonenumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+n+"password");
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#"+n+"leader");

        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+n+"table");

        TableColumn tcHousemateID = new TableColumn("Housemate ID");
        tcHousemateID.setCellValueFactory(new PropertyValueFactory<>("housemateID"));
        tcHousemateID.setPrefWidth(100);

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

        tvHousemates.getColumns().addAll(tcHousemateID, tcFirstname, tcLastname, tcPhoneNumber, tcPassword, tcIsLeader);

        tvHousemates.setItems(housemates);

        Button btnCancel = (Button) mhStage.getScene().lookup("#"+n+"cancel");
        btnCancel.setOnAction(event -> {
            mhStage.close();
        });

        Button abtnClear = (Button) mhStage.getScene().lookup("#"+n+"clear");
        abtnClear.setOnAction(event -> {
            tfHousemateID.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            tvHousemates.getSelectionModel().clearSelection();
        });
    }

    private void editHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+ EDIT + "table");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+ EDIT + "housemateid");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+ EDIT + "firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+ EDIT + "lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+ EDIT + "phonenumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+ EDIT + "password");
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#"+ EDIT + "leader");
        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
                cbIsLeader.setDisable(temp.isLeader.getValue() == 0);
            }
        });

        Button btnEdit = (Button) mhStage.getScene().lookup("#"+ EDIT + "edit");
        btnEdit.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if(index != -1) {
                housemates.set(index, housemate);

                database.executeUpdate("UPDATE Housemate SET firstName = '" + housemate.firstName.getValue() + "', lastName = '" + housemate.lastName.getValue() + "', password = '" + housemate.password.getValue() + "', phoneNumber = '" + housemate.phoneNumber.getValue() + "' WHERE housemateID = '" + housemate.housemateID.getValue() + "'");
            }
        });
    }

    private int getHousemateIndex(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    public void addHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+ ADD + "table");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+ ADD + "housemateid");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+ ADD + "firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+ ADD + "lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+ ADD + "phonenumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+ ADD + "password");

        Button btnAdd = (Button) mhStage.getScene().lookup("#"+ ADD + "add");
        btnAdd.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int id = database.executeInsert("INSERT INTO Housemate(firstName, lastName, password, phoneNumber) VALUES('"+housemate.firstName.getValue()+"', '"+housemate.lastName.getValue()+"', '"+housemate.password.getValue()+"', '"+housemate.phoneNumber.getValue()+"')");
            if(id != -1)
                housemate.housemateID.setValue(id + "");
            tfHousemateID.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            tvHousemates.getSelectionModel().clearSelection();
            housemates.add(housemate);
        });
    }

    private void setupHousemates() {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate");
        try {
            while (rs.next()) {
                Housemate housemate = new Housemate();
                housemate.housemateID.setValue(rs.getString("HousemateID"));
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

    private void setupDashboardLinks(Scene dashboardScene, Stage mhStage) {
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#mh_dashboard");
        hpMaintainHousemates.setOnAction(event -> {
            mhStage.showAndWait();
        });
    }
}
