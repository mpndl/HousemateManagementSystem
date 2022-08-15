package za.nmu.wrr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Optional;

public class MaintainProfileController extends Controller {
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String EDIT = "edit_mp_";
    private final String DELETE = "delete_mp_";
    private Scene dashboardScene;
    private Stage mpStage;
    public MaintainProfileController(){}
    public MaintainProfileController(Scene dashboardScene,Stage mpStage) {
        this.dashboardScene = dashboardScene;
        this.mpStage = mpStage;
        setupDashboardLinks(dashboardScene, mpStage);
    }

    private void deleteProfile(Stage mpStage) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup("#"+ DELETE + "housemateid");
        tfHousemateID.setText(loggedInUser.housemateID.getValue());
        TextField tfUsername = (TextField) mpStage.getScene().lookup("#"+ DELETE + "username");
        tfUsername.setText(loggedInUser.username.getValue());
        tfUsername.setDisable(true);
        TextField tfFirstname = (TextField) mpStage.getScene().lookup("#"+ DELETE + "firstname");
        tfFirstname.setText(loggedInUser.firstName.getValue());
        tfFirstname.setDisable(true);
        TextField tfLastname = (TextField) mpStage.getScene().lookup("#"+ DELETE + "lastname");
        tfLastname.setText(loggedInUser.lastName.getValue());
        tfLastname.setDisable(true);
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup("#"+ DELETE + "phonenumber");
        tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
        tfPhoneNumber.setDisable(true);
        TextField tfPassword = (TextField) mpStage.getScene().lookup("#"+ DELETE + "password");
        tfPassword.setText(loggedInUser.password.getName());
        tfPassword.setDisable(true);
        CheckBox cbIsLeader = (CheckBox) mpStage.getScene().lookup("#"+ DELETE + "leader");
        cbIsLeader.setSelected(loggedInUser.isLeader.getValue() != 0);

        Button btnDelete = (Button) mpStage.getScene().lookup("#"+ DELETE + "delete");
        Button btnClear = (Button) mpStage.getScene().lookup("#"+ DELETE + "clear");
        btnClear.setDisable(true);
        tfHousemateID.textProperty().addListener((observableValue, s, t1) -> {
            if(observableValue.getValue().length() > 0) {
                btnDelete.setDisable(false);
            }
            else {
                btnDelete.setDisable(true);
            }
        });
        btnDelete.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.username.setValue(tfUsername.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if(index != -1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Delete Profile");
                alert.setHeaderText("Are you sure you want to delete your profile?");

                ButtonType btDelete = new ButtonType("Delete");
                ButtonType btCancel = new ButtonType("Cancel");

                alert.getButtonTypes().setAll(btDelete, btCancel);
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == btDelete) {

                    housemates.remove(index);

                    database.executeUpdate("DELETE FROM Housemate WHERE housemateID = '" + loggedInUser.housemateID.getValue() + "'");

                    tfHousemateID.setText("");
                    tfUsername.setText("");
                    tfFirstname.setText("");
                    tfLastname.setText("");
                    tfPhoneNumber.setText("");
                    tfPassword.setText("");
                }
                else
                    alert.close();
            }
        });
    }

    private void linkToScene(Stage mpStage, String n) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup("#"+n+"housemateid");
        TextField tfUsername = (TextField) mpStage.getScene().lookup("#"+n+"username");
        TextField tfFirstname = (TextField) mpStage.getScene().lookup("#"+n+"firstname");
        TextField tfLastname = (TextField) mpStage.getScene().lookup("#"+n+"lastname");
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup("#"+n+"phonenumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) mpStage.getScene().lookup("#"+n+"password");

        Button btnCancel = (Button) mpStage.getScene().lookup("#"+n+"cancel");
        btnCancel.setOnAction(event -> {
            mpStage.close();
            tfUsername.setText(loggedInUser.username.getValue());
            tfFirstname.setText(loggedInUser.firstName.getValue());
            tfLastname.setText(loggedInUser.lastName.getValue());
            tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
            tfPassword.setText(loggedInUser.password.getValue());

        });

        Button btnDelete = (Button) mpStage.getScene().lookup("#"+ DELETE + "delete");
        btnDelete.setDisable(true);
        Button abtnClear = (Button) mpStage.getScene().lookup("#"+n+"clear");
        abtnClear.setDisable(true);
        abtnClear.setOnAction(event -> {
            tfHousemateID.setText("");
            tfUsername.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            btnDelete.setDisable(true);
            abtnClear.setDisable(true);
        });
    }

    private void editProfile(Stage mpStage) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup("#"+ EDIT + "housemateid");
        tfHousemateID.setText(loggedInUser.housemateID.getValue());
        TextField tfUsername = (TextField) mpStage.getScene().lookup("#"+ EDIT + "username");
        tfUsername.setText(loggedInUser.username.getValue());
        TextField tfFirstname = (TextField) mpStage.getScene().lookup("#"+ EDIT + "firstname");
        tfFirstname.setText(loggedInUser.firstName.getValue());
        TextField tfLastname = (TextField) mpStage.getScene().lookup("#"+ EDIT + "lastname");
        tfLastname.setText(loggedInUser.lastName.getValue());
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup("#"+ EDIT + "phonenumber");
        tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
        TextField tfPassword = (TextField) mpStage.getScene().lookup("#"+ EDIT + "password");
        tfPassword.setText(loggedInUser.password.getValue());
        CheckBox cbIsLeader = (CheckBox) mpStage.getScene().lookup("#"+ EDIT + "leader");
        cbIsLeader.setSelected(loggedInUser.isLeader.getValue() != 0);

        Button btnEdit = (Button) mpStage.getScene().lookup("#"+ EDIT + "edit");
        btnEdit.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.username.setValue(tfUsername.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            try {
                Integer.parseInt(housemate.phoneNumber.getValue());
                if(housemate.phoneNumber.getValue().length() == 10) {

                    int index = getHousemateIndex(housemate.housemateID.getValue());
                    if (index != -1) {
                        housemates.set(index, housemate);
                        loggedInUser = housemate;
                        database.executeUpdate("UPDATE Housemate SET username = '" + housemate.username.getValue() + "', firstName = '" + housemate.firstName.getValue() + "', lastName = '" + housemate.lastName.getValue() + "', password = '" + housemate.password.getValue() + "', phoneNumber = '" + housemate.phoneNumber.getValue() + "' WHERE housemateID = '" + housemate.housemateID.getValue() + "'");
                    }
                }
                else
                    throw new Exception();
            }
            catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Number format error");
                alert.setHeaderText("Check phone number format");
                alert.showAndWait();
            }
        });

        Button btnClear = (Button) mpStage.getScene().lookup("#"+ EDIT + "clear");
        setupDisableFuncs(btnEdit, btnClear,null, tfFirstname, tfLastname, tfPhoneNumber,tfPassword);
    }

    private int getHousemateIndex(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    private void setupDashboardLinks(Scene dashboardScene, Stage mpStage) {
        Hyperlink hpMaintainProfile = (Hyperlink) dashboardScene.lookup("#mp_dashboard");
        hpMaintainProfile.setOnAction(event -> {
            // Edit
            linkToScene(mpStage, EDIT);
            editProfile(mpStage);
            // Delete
            linkToScene(mpStage, DELETE);
            deleteProfile(mpStage);
            mpStage.showAndWait();
        });
    }
}
