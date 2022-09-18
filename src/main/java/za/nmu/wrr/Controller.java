package za.nmu.wrr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Controller {
    protected final Database database = new Database();
    protected StringProperty disableClearProperty = new SimpleStringProperty();
    protected static Housemate loggedInUser;
    public Controller() {}

    public Controller(Stage dashboardStage, Scene dashboardScene, Stage vhStage) {
        vhStage.showAndWait();
    }

    protected EventHandler<KeyEvent> maxLength(final Integer i) {
        return new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent arg0) {
                TextField tx = (TextField) arg0.getSource();
                if (tx.getText().length() >= i) {
                    arg0.consume();
                }
            }
        };
    }

    protected void setupDisableFuncs(Button btnFunc1, Button btnFunc2, CheckBox cb,TextField... textFields) {
        btnFunc1.setDisable(true);
        btnFunc2.setDisable(true);
        for(TextField textField: textFields) {
            textField.textProperty().addListener((observableValue, s, t1) -> {
                disableClearProperty.setValue(observableValue.getValue());
            });
        }
        disableClearProperty.addListener((observableValue, s, t1) -> {
            String value = observableValue.getValue().replace("null", "");
            if(value.length() > 0 && cb != null && !cb.isSelected()) {
                btnFunc1.setDisable(false);
                btnFunc2.setDisable(false);
            }
            else {
                btnFunc1.setDisable(true);
                btnFunc2.setDisable(true);
            }
        });
    }

    protected void setupDisableFuncsValidateUser(Button btnFunc1, Button btnFunc2,TextField... textFields) {
        btnFunc1.setDisable(true);
        btnFunc2.setDisable(true);
        for(TextField textField: textFields) {
            textField.textProperty().addListener((observableValue, s, t1) -> {
                disableClearProperty.setValue(observableValue.getValue());
            });
        }
        disableClearProperty.addListener((observableValue, s, t1) -> {
            String value = observableValue.getValue().replace("null", "");
            if(value.length() > 0) {
                btnFunc1.setDisable(false);
                btnFunc2.setDisable(false);
            }
            else {
                btnFunc1.setDisable(true);
                btnFunc2.setDisable(true);
            }
        });
    }

    protected void setupProfile() {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate WHERE firstName = '" + loggedInUser.firstName.getValue() + "' AND lastName = '" + loggedInUser.lastName.getValue() + "'");
        try {
            while (rs.next()) {
                loggedInUser.housemateID.setValue(rs.getString("HousemateID"));
                loggedInUser.password.setValue(rs.getString("password"));
                loggedInUser.phoneNumber.setValue(rs.getString("phoneNumber"));
                loggedInUser.isLeader.setValue(rs.getInt("isLeader"));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
