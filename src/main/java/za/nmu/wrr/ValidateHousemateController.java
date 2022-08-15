package za.nmu.wrr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;

public class ValidateHousemateController extends Controller {
    private final ObservableList<Housemate> housemates = FXCollections.observableArrayList();
    private final String LOGIN = "login_vh_";
    private final String REGISTER = "register_vh_";

    public ValidateHousemateController(){}
    public ValidateHousemateController(Stage dashboardStage, Stage vhStage) {
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
        Button btnClear = (Button) vhStage.getScene().lookup("#"+ LOGIN + "clear");
        btnClear.setDisable(true);
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
            if(temp.username.getValue().equals(housemate.username.getValue()) && temp.password.getValue().equals(housemate.password.getValue())) {
                if(temp.password.getValue().equals(housemate.password.getValue())) {
                    loggedInUser = temp;
                    vhStage.close();
                }
                else
                    alert(Alert.AlertType.ERROR, "Login Error", "Username/Password combination incorrect");
            }
            else
                alert(Alert.AlertType.ERROR, "Login Error", "Username/Password combination incorrect");
        });
        btnClear = (Button) vhStage.getScene().lookup("#"+ REGISTER + "clear");
        setupDisableFuncsValidateUser(btnLogin, btnClear, tfUsername, tfPassword);
    }

    private void register(Stage vhStage) {
        TextField tfUsername = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "username");
        TextField tfFirstname = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "firstname");
        TextField tfLastname = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "lastname");
        TextField tfPhoneNumber = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "phonenumber");
        TextField tfPassword = (TextField) vhStage.getScene().lookup("#"+ REGISTER + "password");

        Button btnRegister = (Button) vhStage.getScene().lookup("#"+ REGISTER + "register");
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

        Button btnClear = (Button) vhStage.getScene().lookup("#"+ REGISTER + "clear");
        setupDisableFuncsValidateUser(btnRegister, btnClear, tfUsername, tfFirstname, tfLastname, tfPhoneNumber,tfPassword);
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
