package za.nmu.wrr.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import za.nmu.wrr.models.Housemate;
import za.nmu.wrr.circle.CircleView;

import java.util.Optional;

public class MaintainProfileController extends Controller {
    public final static String v = "Maintain Profile";
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String EDIT = "edit_mp_";
    private final String DELETE = "delete_mp_";
    private Scene dashboardScene;
    private Stage mpStage;
    public MaintainProfileController(){}
    public MaintainProfileController(Scene dashboardScene,Stage mpStage) {
        this.dashboardScene = dashboardScene;
        this.mpStage = mpStage;
        linksRunnable.add(() -> setupDashboardLinks(dashboardScene));
    }

    private void deleteProfile(Stage mpStage) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup(getID(DELETE, "housemateid"));
        tfHousemateID.setText(loggedInUser.housemateID.getValue());
        TextField tfUsername = (TextField) mpStage.getScene().lookup(getID(DELETE, "username"));
        tfUsername.setText(loggedInUser.username.getValue());
        tfUsername.setDisable(true);
        TextField tfFirstname = (TextField) mpStage.getScene().lookup(getID(DELETE, "firstname"));
        tfFirstname.setText(loggedInUser.firstName.getValue());
        tfFirstname.setDisable(true);
        TextField tfLastname = (TextField) mpStage.getScene().lookup(getID(DELETE, "lastname"));
        tfLastname.setText(loggedInUser.lastName.getValue());
        tfLastname.setDisable(true);
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup(getID(DELETE, "phonenumber"));
        tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
        tfPhoneNumber.setDisable(true);
        TextField tfPassword = (TextField) mpStage.getScene().lookup(getID(DELETE, "password"));
        tfPassword.setText(loggedInUser.password.getName());
        tfPassword.setDisable(true);
        CheckBox cbIsLeader = (CheckBox) mpStage.getScene().lookup(getID(DELETE, "leader"));
        cbIsLeader.setSelected(loggedInUser.isLeader.getValue() != 0);

        Button btnDelete = (Button) mpStage.getScene().lookup(getID(DELETE, "delete"));
        btnDelete.setOnAction(event -> {
            if (loggedInUser.isLeader.getValue() == 0) {
                Housemate housemate = new Housemate();
                housemate.housemateID.setValue(tfHousemateID.getText());
                housemate.firstName.setValue(tfFirstname.getText());
                housemate.username.setValue(tfUsername.getText());
                housemate.lastName.setValue(tfLastname.getText());
                housemate.phoneNumber.setValue(tfPhoneNumber.getText());
                housemate.password.setValue(tfPassword.getText());
                housemate.isLeader.set(0);

                int index = getHousemateIndex(housemate.housemateID.getValue());
                if (index != -1) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirm Delete Profile");
                    alert.setHeaderText("Are you sure you want to delete your profile?");

                    ButtonType btDelete = new ButtonType("Delete");
                    ButtonType btCancel = new ButtonType("Cancel");

                    alert.getButtonTypes().setAll(btDelete, btCancel);
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == btDelete) {

                        housemates.remove(index);

                        database.executeUpdate("DELETE FROM Housemate WHERE housemateID = '" + loggedInUser.housemateID.getValue() + "'");

                        tfHousemateID.setText("");
                        tfUsername.setText("");
                        tfFirstname.setText("");
                        tfLastname.setText("");
                        tfPhoneNumber.setText("");
                        tfPassword.setText("");
                    } else
                        alert.close();
                }
            }
        });
    }

    private void linkToScene(Stage mpStage, String tab) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup(getID(tab, "housemateid"));
        TextField tfUsername = (TextField) mpStage.getScene().lookup(getID(tab, "username"));
        TextField tfFirstname = (TextField) mpStage.getScene().lookup(getID(tab, "firstname"));
        TextField tfLastname = (TextField) mpStage.getScene().lookup(getID(tab, "lastname"));
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup(getID(tab, "phonenumber"));
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) mpStage.getScene().lookup(getID(tab, "password"));

        Button btnCancel = (Button) mpStage.getScene().lookup(getID(tab, "cancel"));
        btnCancel.setOnAction(event -> {
            mpStage.close();
            tfUsername.setText(loggedInUser.username.getValue());
            tfFirstname.setText(loggedInUser.firstName.getValue());
            tfLastname.setText(loggedInUser.lastName.getValue());
            tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
            tfPassword.setText(loggedInUser.password.getValue());

        });

        Button btnClear = (Button) mpStage.getScene().lookup(getID(tab, "clear"));
        btnClear.setOnAction(event -> {
            tfHousemateID.setText("");
            tfUsername.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
        });
    }

    private void editProfile(Stage mpStage) {
        TextField tfHousemateID = (TextField) mpStage.getScene().lookup(getID(EDIT, "housemateid"));
        tfHousemateID.setText(loggedInUser.housemateID.getValue());
        TextField tfUsername = (TextField) mpStage.getScene().lookup(getID(EDIT, "username"));
        tfUsername.setText(loggedInUser.username.getValue());
        TextField tfFirstname = (TextField) mpStage.getScene().lookup(getID(EDIT, "firstname"));
        tfFirstname.setText(loggedInUser.firstName.getValue());
        TextField tfLastname = (TextField) mpStage.getScene().lookup(getID(EDIT, "lastname"));
        tfLastname.setText(loggedInUser.lastName.getValue());
        TextField tfPhoneNumber = (TextField) mpStage.getScene().lookup(getID(EDIT, "phonenumber"));
        tfPhoneNumber.setText(loggedInUser.phoneNumber.getValue());
        TextField tfPassword = (TextField) mpStage.getScene().lookup(getID(EDIT, "password"));
        tfPassword.setText(loggedInUser.password.getValue());
        CheckBox cbIsLeader = (CheckBox) mpStage.getScene().lookup(getID(EDIT, "leader"));
        cbIsLeader.setSelected(loggedInUser.isLeader.getValue() != 0);

        Button btnEdit = (Button) mpStage.getScene().lookup(getID(EDIT, "edit"));
        Button btnClear = (Button) mpStage.getScene().lookup(getID(EDIT, "clear"));

        validate(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnEdit);

        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> !(!tfUsername.getText().isEmpty() || !tfFirstname.getText().isEmpty()
                        || !tfLastname.getText().isEmpty() || !tfPhoneNumber.getText().isEmpty()
                        || !tfPassword.getText().isEmpty()), tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));

        btnEdit.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.username.setValue(tfUsername.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            database.executeUpdate("UPDATE Housemate SET username = '" + housemate.username.getValue() + "', firstName = '" + housemate.firstName.getValue() + "', lastName = '" + housemate.lastName.getValue() + "', password = '" + housemate.password.getValue() + "', phoneNumber = '" + housemate.phoneNumber.getValue() + "' WHERE housemateID = '" + housemate.housemateID.getValue() + "'");
            loggedInUser = housemate;
            tfFirstname.setText(housemate.firstName.getValue());
        });
    }

    private void validate(TextField tfUsername, TextField tfFirstname, TextField tfLastname, TextField tfPhoneNumber, TextField tfPassword, Button btnFunc) {
        ValidateHousemateController.validateProfile(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnFunc);
    }

    private int getHousemateIndex(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    private void onDashboardLinkClicked(CircleView cv) {
        // Edit
        linkToScene(mpStage, EDIT);
        editProfile(mpStage);
        // Delete
        linkToScene(mpStage, DELETE);
        deleteProfile(mpStage);
        if (cv == null) mpStage.showAndWait();
        else {
            if (!cv.dragging()) mpStage.showAndWait();
        }
    }

        private void setupDashboardLinks(Scene dashboardScene) {
        links.put(v, this::onDashboardLinkClicked);

        Hyperlink hpMaintainProfile = (Hyperlink) dashboardScene.lookup("#mp_dashboard");
        avail(hpMaintainProfile);
        hpMaintainProfile.setOnAction(event -> onDashboardLinkClicked(null));
    }
}
