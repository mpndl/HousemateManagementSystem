package za.nmu.wrr;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ValidateHousemateController extends Controller {
    private final ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String LOGIN = "login_vh_";
    private final String REGISTER = "register_vh_";
    private Stage dashboardStage;

    public ValidateHousemateController(){}
    public ValidateHousemateController(Stage dashboardStage, Stage vhStage) {
        this.dashboardStage = dashboardStage;
        // Login
        linkToLoginScene(vhStage);
        login(vhStage);
        // Register
        linkToRegisterScene(vhStage);
        register(vhStage);
    }

    private void login(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup("#"+ LOGIN + "username");
        TextField tfPassword = (TextField) vhStage.getScene().lookup("#"+ LOGIN + "password");

        Button btnLogin = (Button) vhStage.getScene().lookup("#"+ LOGIN + "login");
        validateLogin(tfUsername, tfPassword, btnLogin);
        Button btnClear = (Button) vhStage.getScene().lookup("#"+ LOGIN + "clear");
        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> !(!tfPassword.getText().isEmpty() || !tfUsername.getText().isEmpty()),
                tfUsername.textProperty(), tfPassword.textProperty()));

        btnLogin.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.username.setValue(tfUsername.getText());
            housemate.password.setValue(tfPassword.getText());

            Housemate temp = new Housemate();
            ResultSet rs = database.executeQuery("SELECT * FROM Housemate WHERE username = '" + housemate.username.getValue() + "'");
            try {
                while (rs.next()) {
                    temp.housemateID.setValue(rs.getString("housemateID"));
                    temp.username.setValue(rs.getString("username"));
                    temp.firstName.setValue(rs.getString("firstName"));
                    temp.lastName.setValue(rs.getString("lastName"));
                    temp.password.setValue(rs.getString("password"));
                    temp.phoneNumber.setValue(rs.getString("phoneNumber"));
                    temp.isLeader.setValue(rs.getInt("isLeader"));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if(temp.username.getValue() != null && temp.username.getValue().equals(housemate.username.getValue()) && temp.password.getValue().equals(housemate.password.getValue())) {
                if(temp.password.getValue().equals(housemate.password.getValue())) {
                    loggedInUser = temp;
                    dashboardStage.show();
                    vhStage.close();
                }
                else
                    alert(Alert.AlertType.ERROR, "Login Error", "Username/Password combination incorrect");
            }
            else
                alert(Alert.AlertType.ERROR, "Login Error", "Username/Password combination incorrect");
        });
    }

    private void register(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "username");
        TextField tfFirstname = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "firstname");
        TextField tfLastname = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "lastname");
        TextField tfPhoneNumber = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "phonenumber");
        TextField tfPassword = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "password");

        Button btnRegister = (Button) vhStage.getScene().lookup("#"+ REGISTER + "register");
        Button btnClear = (Button) vhStage.getScene().lookup("#"+ REGISTER + "clear");

        validate2(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnRegister);

        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> !(!tfPassword.getText().isEmpty() || !tfUsername.getText().isEmpty()),
                tfUsername.textProperty(), tfPassword.textProperty()));

        btnRegister.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.username.setValue(tfUsername.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            try {
                Integer.parseInt(housemate.phoneNumber.getValue());
                if(housemate.phoneNumber.getValue().length() == 10) {
                    if (database.getQueryRowCount("SELECT * FROM Housemate WHERE username = '" + housemate.username.getValue() + "'") == 0) {
                        database.executeUpdate("INSERT INTO Housemate(username, firstName, lastName, password, phoneNumber) VALUES('" + housemate.username.getValue() + "', '" + housemate.firstName.getValue() + "', '" + housemate.lastName.getValue() + "', '" + housemate.password.getValue() + "', '" + housemate.phoneNumber.getValue() + "')");
                        housemates.add(housemate);
                        loggedInUser = housemate;
                        dashboardStage.show();
                        vhStage.close();
                    }
                    else {
                        alert(Alert.AlertType.ERROR, "Username error", "Housemate with that username already exists");
                    }
                }
                else
                    throw new Exception();
            }
            catch (Exception e) {
                alert(Alert.AlertType.ERROR, "Number format error", "Check phone number format");
            }
        });
    }

    static void validateLogin(TextField tfUsername, TextField tfPassword, Button btnFunc) {
        btnFunc.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    if (tfUsername.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfUsername.setTooltip(tooltip);
                        tfUsername.setStyle("-fx-border-color: red");
                    }
                    else {
                        tfUsername.setTooltip(null);
                        tfUsername.setStyle("-fx-border-color: green");
                    }

                    if (tfPassword.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPassword.setTooltip(tooltip);
                        tfPassword.setStyle("-fx-border-color: red");
                        return true;
                    }
                    else {
                        tfPassword.setTooltip(null);
                        tfPassword.setStyle("-fx-border-color: green");
                    }
                    return false;
                }
                , tfUsername.textProperty(), tfPassword.textProperty()));
    }

    static void validateProfile(TextField tfUsername, TextField tfFirstname, TextField tfLastname, TextField tfPhoneNumber, TextField tfPassword, Button btnFunc) {
        btnFunc.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    String username = loggedInUser.username.getValue();
                    String firstname = loggedInUser.firstName.getValue();
                    String lastname = loggedInUser.lastName.getValue();
                    String phoneNumber = loggedInUser.phoneNumber.getValue();
                    String password = loggedInUser.password.getValue();
                    int warningCount = 0;

                    if (tfUsername.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfUsername.setTooltip(tooltip);
                        tfUsername.setStyle("-fx-border-color: red");
                    }
                    else {
                        if (tfUsername.getText().equals(username)) {
                            tfUsername.setStyle("-fx-border-color: orange");
                            Tooltip tooltip = new Tooltip("Username has not changed.");
                            tooltip.setShowDelay(Duration.ONE);
                            tfUsername.setTooltip(tooltip);
                            warningCount++;
                        }
                        else {
                            tfUsername.setTooltip(null);
                            tfUsername.setStyle("-fx-border-color: green");
                        }
                    }
                    if (tfFirstname.getText().isEmpty()) {
                        Tooltip tooltip = new Tooltip("Firstname was not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfFirstname.setTooltip(tooltip);
                        tfFirstname.setStyle("-fx-border-color: red");
                        return true;
                    }
                    else {
                        if (tfFirstname.getText().equals(firstname)) {
                            Tooltip tooltip = new Tooltip("Firstname has not changed.");
                            tooltip.setShowDelay(Duration.ONE);
                            tfFirstname.setStyle("-fx-border-color: orange");
                            tfFirstname.setTooltip(tooltip);
                            warningCount++;
                        }
                        else {
                            tfFirstname.setTooltip(null);
                            tfFirstname.setStyle("-fx-border-color: green");
                        }
                    }
                    if (tfLastname.getText().isEmpty()) {
                        Tooltip tooltip = new Tooltip("Lastname was not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfLastname.setTooltip(tooltip);
                        tfLastname.setStyle("-fx-border-color: red");
                        return true;
                    }
                    else {
                        if (tfLastname.getText().equals(lastname)) {
                            Tooltip tooltip = new Tooltip("Lastname has not changed.");
                            tooltip.setShowDelay(Duration.ONE);
                            tfLastname.setTooltip(tooltip);
                            tfLastname.setStyle("-fx-border-color: orange");
                            warningCount++;
                        }
                        else {
                            tfLastname.setTooltip(null);
                            tfLastname.setStyle("-fx-border-color: green");
                        }
                    }
                    try {
                        Integer.parseInt(tfPhoneNumber.getText());
                        if (tfPhoneNumber.getText().length() != 10)
                            throw new NumberFormatException();

                        if (tfPhoneNumber.getText().equals(phoneNumber)) {
                            Tooltip tooltip = new Tooltip("Phone number has not changed.");
                            tooltip.setShowDelay(Duration.ONE);
                            tfPhoneNumber.setTooltip(tooltip);
                            tfPhoneNumber.setStyle("-fx-border-color: orange");
                            warningCount++;
                        }
                        else {
                            tfPhoneNumber.setTooltip(null);
                            tfPhoneNumber.setStyle("-fx-border-color: green");
                        }
                    }
                    catch (NumberFormatException e) {
                        Tooltip tooltip = new Tooltip("Invalid phone number.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPhoneNumber.setTooltip(tooltip);
                        tfPhoneNumber.setStyle("-fx-border-color: red");
                        return true;
                    }
                    if (tfPassword.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPassword.setTooltip(tooltip);
                        tfPassword.setStyle("-fx-border-color: red");
                        return true;
                    }
                    else {
                        if (tfPassword.getText().equals(password)) {
                            Tooltip tooltip = new Tooltip("Password has not changed.");
                            tooltip.setShowDelay(Duration.ONE);
                            tfPassword.setTooltip(tooltip);
                            tfPassword.setStyle("-fx-border-color: orange");
                            warningCount++;
                        }
                        else {
                            tfPassword.setTooltip(null);
                            tfPassword.setStyle("-fx-border-color: green");
                        }
                    }

                    if (!(warningCount > 0) || !(warningCount < 5)) return true;
                    return false;
                }
                , tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));
    }

    static void validate2(TextField tfUsername, TextField tfFirstname, TextField tfLastname, TextField tfPhoneNumber, TextField tfPassword, Button btnFunc) {
        btnFunc.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            if (tfUsername.getText().length() < 4) {
                Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                tooltip.setShowDelay(Duration.ONE);
                tfUsername.setTooltip(tooltip);
                tfUsername.setStyle("-fx-border-color: red");
            }
            else {
                tfUsername.setTooltip(null);
                tfUsername.setStyle("-fx-border-color: green");
            }
            if (tfFirstname.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Firstname was not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfFirstname.setTooltip(tooltip);
                tfFirstname.setStyle("-fx-border-color: red");
                return true;
            }
            else {
                tfFirstname.setTooltip(null);
                tfFirstname.setStyle("-fx-border-color: green");
            }
            if (tfLastname.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Lastname was not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfLastname.setTooltip(tooltip);
                tfLastname.setStyle("-fx-border-color: red");
                return true;
            }
            else {
                tfLastname.setTooltip(null);
                tfLastname.setStyle("-fx-border-color: green");
            }
            try {
                Integer.parseInt(tfPhoneNumber.getText());
                if (tfPhoneNumber.getText().length() != 10)
                    throw new NumberFormatException();
                tfPhoneNumber.setTooltip(null);
                tfPhoneNumber.setStyle("-fx-border-color: green");
            }
            catch (NumberFormatException e) {
                Tooltip tooltip = new Tooltip("Invalid phone number.");
                tooltip.setShowDelay(Duration.ONE);
                tfPhoneNumber.setTooltip(tooltip);
                tfPhoneNumber.setStyle("-fx-border-color: red");
                return true;
            }
            if (tfPassword.getText().length() < 4) {
                Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                tooltip.setShowDelay(Duration.ONE);
                tfPassword.setTooltip(tooltip);
                tfPassword.setStyle("-fx-border-color: red");
                return true;
            }
            else {
                tfPassword.setTooltip(null);
                tfPassword.setStyle("-fx-border-color: green");
            }
           return false;
                }
                , tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));
    }

    private void alert(Alert.AlertType type, String title, String header) {
        if(type == Alert.AlertType.ERROR) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(header);
            alert.showAndWait();
        }
    }

    private void linkToLoginScene(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup("#"+LOGIN+"username");
        TextField tfPassword = (TextField) vhStage.getScene().lookup("#"+LOGIN+"password");

        Button btnCancel = (Button) vhStage.getScene().lookup("#"+LOGIN+"cancel");
        btnCancel.setOnAction(event -> {
            vhStage.close();
        });

        Button btnLogin = (Button) vhStage.getScene().lookup("#"+ LOGIN + "login");
        btnLogin.setDisable(true);
        Button btnClear = (Button) vhStage.getScene().lookup("#"+LOGIN+"clear");
        btnClear.setDisable(true);
        btnClear.setOnAction(event -> {
            tfUsername.setText("");
            tfPassword.setText("");
            btnLogin.setDisable(true);
            btnClear.setDisable(true);
        });
    }

    private void linkToRegisterScene(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup("#"+REGISTER+"username");
        TextField tfFirstname = (TextField) vhStage.getScene().lookup("#"+REGISTER+"firstname");
        TextField tfLastname = (TextField) vhStage.getScene().lookup("#"+REGISTER+"lastname");
        TextField tfPhoneNumber = (TextField) vhStage.getScene().lookup("#"+REGISTER+"phonenumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) vhStage.getScene().lookup("#"+REGISTER+"password");
        Button btnCancel = (Button) vhStage.getScene().lookup("#"+REGISTER+"cancel");
        btnCancel.setOnAction(event -> {
            vhStage.close();
        });

        Button btnRegister = (Button) vhStage.getScene().lookup("#"+ REGISTER + "register");
        btnRegister.setDisable(true);
        Button btnClear = (Button) vhStage.getScene().lookup("#"+REGISTER+"clear");
        btnClear.setDisable(true);
        btnClear.setOnAction(event -> {
            tfUsername.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            btnRegister.setDisable(true);
            btnClear.setDisable(true);
        });
    }

    private int getHousemateIndexWithID(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    private Housemate getHousemate(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return housemates.get(i);
        }
        return null;
    }

    private Housemate getHousemateWithUsername(String username) {
        for (Housemate housemate : housemates) {
            System.out.println(housemate.username.getValue());
            if (housemate.username.getValue() != null && housemate.username.getValue().equals(username))
                return housemate;
        }
        return null;
    }

    private int getHousemateIndexWithUsername(String username) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).username.getValue() != null && housemates.get(i).username.getValue().equals(username))
                return i;
        }
        return -1;
    }
}
