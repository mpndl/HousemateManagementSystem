package za.nmu.wrr;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.util.ArrayList;

public class Controller {
    protected final Database database = new Database();
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
}
