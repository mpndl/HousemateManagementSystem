package za.nmu.wrr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.util.Optional;

public class Controller {
    private final Database database = new Database();
    private ObservableList<Housemate> housemates = FXCollections.observableArrayList();

    public Controller() {}
    public Controller(Scene dashboardScene, Stage mhStage) {
        setupDashboardLinks(dashboardScene, mhStage);
    }

    public EventHandler<KeyEvent> maxLength(final Integer i) {
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

    private void setupDashboardLinks(Scene dashboardScene, Stage mhStage) {
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#mh_dashboard");
        hpMaintainHousemates.setOnAction(event -> {
            mhStage.showAndWait();
        });
    }
}
