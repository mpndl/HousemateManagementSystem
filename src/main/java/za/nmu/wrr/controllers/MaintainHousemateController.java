package za.nmu.wrr.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import za.nmu.wrr.models.Housemate;
import za.nmu.wrr.circle.CircleView;

import java.sql.ResultSet;
import java.util.*;

public class MaintainHousemateController extends Controller {
    public final static String v = "Maintain Housemates";
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    public final String ADD = "add_mh_";
    public final String EDIT = "edit_mh_";
    public final String REMOVE = "remove_mh_";
    private Scene dashboardScene;
    private Stage mhStage;
    public boolean linked = false;

    public MaintainHousemateController(){}
    public MaintainHousemateController(Scene dashboardScene,Stage mhStage) {
        this.dashboardScene = dashboardScene;
        this.mhStage = mhStage;
        linksRunnable.add(() ->  setupDashboardLinks(dashboardScene));
    }

    private void removeHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+ REMOVE + "table");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "housemateid");
        TextField tfUsername = (TextField) mhStage.getScene().lookup("#"+ REMOVE + "username");
        tfUsername.setDisable(true);
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
                cbIsLeader.setSelected(temp.isLeader.getValue() != 0);
                tfUsername.setText(temp.username.getValue());
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
            }
        });


        Button btnRemove = (Button) mhStage.getScene().lookup("#"+ REMOVE + "remove");
        btnRemove.disableProperty().bind(Bindings.createBooleanBinding(() -> tvHousemates.getSelectionModel().getSelectedItem() == null,
                tvHousemates.getSelectionModel().selectedItemProperty()));

        btnRemove.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.username.setValue(tfUsername.getText());
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
                    tfUsername.setText("");
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

    private void linkToScene(Stage mhStage, String n, String f) {
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+n+"housemateid");
        TextField tfUsername = (TextField) mhStage.getScene().lookup("#"+n+"username");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+n+"firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+n+"lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+n+"phonenumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+n+"password");
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+n+"table");

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
            if (!Objects.equals(n, REMOVE)) {
                if (temp != null) {
                    tfUsername.setDisable(false);
                    tfFirstname.setDisable(false);
                    tfLastname.setDisable(false);
                    tfPhoneNumber.setDisable(false);
                    tfPassword.setDisable(false);
                } else {
                    tfUsername.setDisable(true);
                    tfFirstname.setDisable(true);
                    tfLastname.setDisable(true);
                    tfPhoneNumber.setDisable(true);
                    tfPassword.setDisable(true);
                }
            }
        });

        Button btnCancel = (Button) mhStage.getScene().lookup("#"+n+"cancel");
        btnCancel.setOnAction(event -> {
            mhStage.close();
        });

        Button btnClear = (Button) mhStage.getScene().lookup("#"+n+"clear");
        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> !(!tfUsername.getText().isEmpty() || !tfFirstname.getText().isEmpty()
                        || !tfLastname.getText().isEmpty() || !tfPhoneNumber.getText().isEmpty()
                        || !tfPassword.getText().isEmpty()), tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));

        btnClear.setOnAction(event -> {
            tfHousemateID.setText("");
            tfUsername.setText("");
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
        TextField tfUsername = (TextField) mhStage.getScene().lookup("#"+ EDIT + "username");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+ EDIT + "firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+ EDIT + "lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+ EDIT + "phonenumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+ EDIT + "password");
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#"+ EDIT + "leader");
        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                cbIsLeader.setSelected(temp.isLeader.getValue() != 0);
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfUsername.setText(temp.username.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
            }
        });

        Button btnEdit = (Button) mhStage.getScene().lookup("#"+ EDIT + "edit");
        Button btnClear = (Button) mhStage.getScene().lookup("#"+ EDIT + "clear");

        ValidateHousemateController.validateEdit(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnEdit, tvHousemates);

        btnEdit.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.username.setValue(tfUsername.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.setValue(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if (index != -1) {
                database.executeUpdate("UPDATE Housemate SET username = '" + housemate.username.getValue() + "', firstName = '" + housemate.firstName.getValue() + "', lastName = '" + housemate.lastName.getValue() + "', password = '" + housemate.password.getValue() + "', phoneNumber = '" + housemate.phoneNumber.getValue() + "' WHERE housemateID = '" + housemate.housemateID.getValue() + "'");
                housemates.set(index, housemate);
                tvHousemates.getSelectionModel().select(housemate);
                tfFirstname.setText(housemate.firstName.getValue());
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
        TextField tfUsername = (TextField) mhStage.getScene().lookup("#"+ ADD + "username");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+ ADD + "firstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+ ADD + "lastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+ ADD + "phonenumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+ ADD + "password");

        Button btnClear = (Button) mhStage.getScene().lookup("#"+ ADD + "clear");
        Button btnAdd = (Button) mhStage.getScene().lookup("#"+ ADD + "add");

        ValidateHousemateController.validateRegister(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnAdd);

        btnAdd.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.username.setValue(tfUsername.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int id = database.executeInsert("INSERT INTO Housemate(username, firstName, lastName, password, phoneNumber) VALUES('" + housemate.username.getValue() + "', '" + housemate.firstName.getValue() + "', '" + housemate.lastName.getValue() + "', '" + housemate.password.getValue() + "', '" + housemate.phoneNumber.getValue() + "')");
            if (id != -1)
                housemate.housemateID.setValue(id + "");
            tfHousemateID.setText("");
            tfUsername.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            //tvHousemates.getSelectionModel().clearSelection();
            housemates.add(housemate);
        });
    }

    public void setupHousemates() {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate WHERE NOT housemateID = " + loggedInUser.housemateID.getValue());
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

    private void onDashboardLinkClicked(CircleView cv) {
        if (!linked) {
            setupHousemates();
            // Add
            linkToScene(mhStage, ADD, "add");
            addHousemates(mhStage);
            // Edit
            linkToScene(mhStage, EDIT, "edit");
            editHousemates(mhStage);
            // Remove
            linkToScene(mhStage, REMOVE, "remove");
            removeHousemates(mhStage);
            linked = true;
        }
        if (cv == null)mhStage.showAndWait();
        else if (!cv.dragging()) mhStage.showAndWait();
    }

    private void setupDashboardLinks(Scene dashboardScene) {
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#mh_dashboard");
        links.put(v, cv -> {
            if (loggedInUser.isLeader.getValue() == 0) {
                disavail(hpMaintainHousemates);
                cv.disable();
            }
            else {
                onDashboardLinkClicked(cv);
            }
        });

        if (loggedInUser.isLeader.getValue() == 0) disavail(hpMaintainHousemates);
        else {
            avail(hpMaintainHousemates);
            hpMaintainHousemates.setOnAction(event -> onDashboardLinkClicked(null));
        }
    }
}
