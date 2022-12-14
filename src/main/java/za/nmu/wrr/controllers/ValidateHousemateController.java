package za.nmu.wrr.controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import za.nmu.wrr.models.Housemate;

import java.sql.ResultSet;

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
        TextField tfUsername = (TextField) vhStage.getScene().lookup(getID(LOGIN, "username"));
        TextField tfPassword = (TextField) vhStage.getScene().lookup(getID(LOGIN, "password"));

        Button btnLogin = (Button) vhStage.getScene().lookup(getID(LOGIN, "login"));
        validateLogin(tfUsername, tfPassword, btnLogin);
        Button btnClear = (Button) vhStage.getScene().lookup(getID(LOGIN, "clear"));
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
        TextField tfUsername = (TextField) vhStage.getScene().lookup(getID(REGISTER, "username"));
        TextField tfFirstname = (TextField) vhStage.getScene().lookup(getID(REGISTER, "firstname"));
        TextField tfLastname = (TextField) vhStage.getScene().lookup(getID(REGISTER, "lastname"));
        TextField tfPhoneNumber = (TextField) vhStage.getScene().lookup(getID(REGISTER, "phonenumber"));
        TextField tfPassword = (TextField) vhStage.getScene().lookup(getID(REGISTER, "password"));

        Button btnRegister = (Button) vhStage.getScene().lookup(getID(REGISTER, "register"));
        Button btnClear = (Button) vhStage.getScene().lookup(getID(REGISTER, "clear"));

        validateRegister(tfUsername, tfFirstname, tfLastname, tfPhoneNumber, tfPassword, btnRegister);

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
                    int errorCount = 0;

                    if (tfUsername.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfUsername.setTooltip(tooltip);
                        tfUsername.setStyle("-fx-border-color: red");
                        errorCount++;
                    }
                    else {
                        if (tfUsername.getText().equals(username)) {
                            tfUsername.setStyle("-fx-border-color: orange");
                            Tooltip tooltip = new Tooltip("Username not changed.");
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
                        Tooltip tooltip = new Tooltip("Firstname not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfFirstname.setTooltip(tooltip);
                        tfFirstname.setStyle("-fx-border-color: red");
                        errorCount++;
                    }
                    else {
                        if (tfFirstname.getText().equals(firstname)) {
                            Tooltip tooltip = new Tooltip("Firstname not changed.");
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
                        Tooltip tooltip = new Tooltip("Lastname not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfLastname.setTooltip(tooltip);
                        tfLastname.setStyle("-fx-border-color: red");
                        errorCount++;
                    }
                    else {
                        if (tfLastname.getText().equals(lastname)) {
                            Tooltip tooltip = new Tooltip("Lastname not changed.");
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
                            Tooltip tooltip = new Tooltip("Phone number not changed.");
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
                        errorCount++;
                    }
                    if (tfPassword.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPassword.setTooltip(tooltip);
                        tfPassword.setStyle("-fx-border-color: red");
                        errorCount++;
                    }
                    else {
                        if (tfPassword.getText().equals(password)) {
                            Tooltip tooltip = new Tooltip("Password not changed.");
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

                    if (errorCount > 0)
                        return true;
                    else if (!(warningCount > 0) || !(warningCount < 5)) return true;
                    else {
                        tfUsername.setTooltip(null);
                        tfUsername.setStyle("-fx-border-color: green");

                        tfFirstname.setTooltip(null);
                        tfFirstname.setStyle("-fx-border-color: green");

                        tfLastname.setTooltip(null);
                        tfLastname.setStyle("-fx-border-color: green");

                        tfPhoneNumber.setTooltip(null);
                        tfPhoneNumber.setStyle("-fx-border-color: green");

                        tfPassword.setTooltip(null);
                        tfPassword.setStyle("-fx-border-color: green");
                        return false;
                    }
                }
                , tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));
    }

    static void validateEdit(TextField tfUsername, TextField tfFirstname, TextField tfLastname, TextField tfPhoneNumber, TextField tfPassword, Button btnFunc, TableView<Housemate> tvHousemates) {
        btnFunc.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean sameUsername = false;
            boolean sameFirstname = false;
            boolean sameLastname = false;
            boolean samePhoneNumber = false;
            boolean samePassword = false;
            boolean invalid = false;

            Housemate housemate = tvHousemates.getSelectionModel().getSelectedItem();
            if (tfUsername.getText().length() < 4) {
                Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                tooltip.setShowDelay(Duration.ONE);
                tfUsername.setTooltip(tooltip);
                tfUsername.setStyle("-fx-border-color: red");
            } else {
                if (housemate.username.getValue().equals(tfUsername.getText())) {
                    Tooltip tooltip = new Tooltip("Username not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfUsername.setTooltip(tooltip);
                    tfUsername.setStyle("-fx-border-color: orange");
                    sameUsername = true;
                }
                else {
                    tfUsername.setTooltip(null);
                    tfUsername.setStyle("-fx-border-color: green");
                }
            }
            if (tfFirstname.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("First name not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfFirstname.setTooltip(tooltip);
                tfFirstname.setStyle("-fx-border-color: red");
                invalid = true;
            } else {
                if (tfFirstname.getText().equals(housemate.firstName.getValue())) {
                    Tooltip tooltip = new Tooltip("First name not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfFirstname.setTooltip(tooltip);
                    tfFirstname.setStyle("-fx-border-color: orange");
                    sameFirstname = true;
                }
                else {
                    tfFirstname.setTooltip(null);
                    tfFirstname.setStyle("-fx-border-color: green");
                }
            }
            if (tfLastname.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Last name not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfLastname.setTooltip(tooltip);
                tfLastname.setStyle("-fx-border-color: red");
                invalid = true;
            } else {
                if (tfLastname.getText().equals(housemate.lastName.getValue())) {
                    Tooltip tooltip = new Tooltip("Last name not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfLastname.setTooltip(tooltip);
                    tfLastname.setStyle("-fx-border-color: orange");
                    sameLastname = true;
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
                if (tfPhoneNumber.getText().equals(housemate.phoneNumber.getValue())) {
                    Tooltip tooltip = new Tooltip("Phone number not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfPhoneNumber.setTooltip(tooltip);
                    tfPhoneNumber.setStyle("-fx-border-color: orange");
                    samePhoneNumber = true;
                }
                else {
                    tfPhoneNumber.setTooltip(null);
                    tfPhoneNumber.setStyle("-fx-border-color: green");
                }
            } catch (NumberFormatException e) {
                Tooltip tooltip = new Tooltip("Invalid phone number.");
                tooltip.setShowDelay(Duration.ONE);
                tfPhoneNumber.setTooltip(tooltip);
                tfPhoneNumber.setStyle("-fx-border-color: red");
                invalid = true;
            }
            if (tfPassword.getText().length() < 4) {
                Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                tooltip.setShowDelay(Duration.ONE);
                tfPassword.setTooltip(tooltip);
                tfPassword.setStyle("-fx-border-color: red");
                invalid = true;
            } else {
                if (tfPassword.getText().equals(housemate.password.getValue())) {
                    Tooltip tooltip = new Tooltip("Password not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfPassword.setTooltip(tooltip);
                    tfPassword.setStyle("-fx-border-color: orange");
                    samePassword = true;
                } else {
                    tfPassword.setTooltip(null);
                    tfPassword.setStyle("-fx-border-color: green");
                }
            }
           if (invalid) {
               return true;
           }
           else if (sameUsername && sameFirstname && sameLastname && samePhoneNumber && samePassword) {
                return true;
           }
           else {
               tfUsername.setTooltip(null);
               tfUsername.setStyle("-fx-border-color: green");

               tfFirstname.setTooltip(null);
               tfFirstname.setStyle("-fx-border-color: green");

               tfLastname.setTooltip(null);
               tfLastname.setStyle("-fx-border-color: green");

               tfPhoneNumber.setTooltip(null);
               tfPhoneNumber.setStyle("-fx-border-color: green");

               tfPassword.setTooltip(null);
               tfPassword.setStyle("-fx-border-color: green");
               return false;
           }
                }
                , tfUsername.textProperty(), tfFirstname.textProperty(), tfLastname.textProperty()
                , tfPhoneNumber.textProperty(), tfPassword.textProperty()));
    }

    static void validateRegister(TextField tfUsername, TextField tfFirstname, TextField tfLastname, TextField tfPhoneNumber, TextField tfPassword, Button btnFunc) {
        btnFunc.disableProperty().bind(Bindings.createBooleanBinding(() -> {
                    int errorCount = 0;
                    if (tfUsername.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Username length must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfUsername.setTooltip(tooltip);
                        tfUsername.setStyle("-fx-border-color: red");
                    } else {
                        tfUsername.setTooltip(null);
                        tfUsername.setStyle("-fx-border-color: green");
                    }
                    if (tfFirstname.getText().isEmpty()) {
                        Tooltip tooltip = new Tooltip("First name not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfFirstname.setTooltip(tooltip);
                        tfFirstname.setStyle("-fx-border-color: red");
                        errorCount++;
                    } else {
                        tfFirstname.setTooltip(null);
                        tfFirstname.setStyle("-fx-border-color: green");
                    }
                    if (tfLastname.getText().isEmpty()) {
                        Tooltip tooltip = new Tooltip("Last name not entered.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfLastname.setTooltip(tooltip);
                        tfLastname.setStyle("-fx-border-color: red");
                        errorCount++;
                    } else {
                        tfLastname.setTooltip(null);
                        tfLastname.setStyle("-fx-border-color: green");
                    }
                    try {
                        Integer.parseInt(tfPhoneNumber.getText());
                        if (tfPhoneNumber.getText().length() != 10)
                            throw new NumberFormatException();
                        tfPhoneNumber.setTooltip(null);
                        tfPhoneNumber.setStyle("-fx-border-color: green");
                    } catch (NumberFormatException e) {
                        Tooltip tooltip = new Tooltip("Invalid phone number.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPhoneNumber.setTooltip(tooltip);
                        tfPhoneNumber.setStyle("-fx-border-color: red");
                        errorCount++;
                    }
                    if (tfPassword.getText().length() < 4) {
                        Tooltip tooltip = new Tooltip("Password must be at least 4 characters.");
                        tooltip.setShowDelay(Duration.ONE);
                        tfPassword.setTooltip(tooltip);
                        tfPassword.setStyle("-fx-border-color: red");
                        errorCount++;
                    } else {
                        tfPassword.setTooltip(null);
                        tfPassword.setStyle("-fx-border-color: green");
                    }
                    return errorCount > 0;
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
        TextField tfUsername = (TextField) vhStage.getScene().lookup(getID(LOGIN, "username"));
        TextField tfPassword = (TextField) vhStage.getScene().lookup(getID(LOGIN, "password"));

        Button btnCancel = (Button) vhStage.getScene().lookup(getID(LOGIN, "cancel"));
        btnCancel.setOnAction(event -> {
            vhStage.close();
        });

        Button btnLogin = (Button) vhStage.getScene().lookup(getID(LOGIN, "login"));
        btnLogin.setDisable(true);
        Button btnClear = (Button) vhStage.getScene().lookup(getID(LOGIN, "clear"));
        btnClear.setDisable(true);
        btnClear.setOnAction(event -> {
            tfUsername.setText("");
            tfPassword.setText("");
            btnLogin.setDisable(true);
            btnClear.setDisable(true);
        });
    }

    private void linkToRegisterScene(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup(getID(REGISTER, "username"));
        TextField tfFirstname = (TextField) vhStage.getScene().lookup(getID(REGISTER, "firstname"));
        TextField tfLastname = (TextField) vhStage.getScene().lookup(getID(REGISTER, "lastname"));
        TextField tfPhoneNumber = (TextField) vhStage.getScene().lookup(getID(REGISTER, "phonenumber"));
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) vhStage.getScene().lookup(getID(REGISTER, "password"));
        Button btnCancel = (Button) vhStage.getScene().lookup(getID(REGISTER, "cancel"));
        btnCancel.setOnAction(event -> {
            vhStage.close();
        });

        Button btnRegister = (Button) vhStage.getScene().lookup(getID(REGISTER, "register"));
        btnRegister.setDisable(true);
        Button btnClear = (Button) vhStage.getScene().lookup(getID(REGISTER, "clear"));
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
}
