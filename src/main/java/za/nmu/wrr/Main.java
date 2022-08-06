package za.nmu.wrr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage dashboardStage) throws Exception {
        // Dashboard/Main View/Controller
        FXMLLoader dashboardView = new FXMLLoader();
        dashboardView.setLocation(getClass().getResource("view.fxml"));
        Scene dashboardScene = new Scene(dashboardView.load());
        Controller controller = new Controller();

        // Maintain Housemates View/Controller
        Stage mhStage = createMaintainHousemateView(dashboardStage);
        new MaintainHousemateController(dashboardScene, mhStage);

        // Maintain Profile View/Controller
        Stage mpStage = createMaintainProfileView(dashboardStage);
        new MaintainProfileController(dashboardScene, mpStage);

        dashboardStage.setScene(dashboardScene);
        dashboardStage.setTitle("Dashboard");
        dashboardStage.setWidth(600);
        dashboardStage.setHeight(350);
        dashboardStage.setResizable(false);
        dashboardStage.setOnCloseRequest(event -> {
            controller.database.disconnectFromDB();
        });
        dashboardStage.show();
    }

    public Stage createMaintainHousemateView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("maintainHousemateView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("Maintain Housemates");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }

    public Stage createMaintainProfileView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("maintainProfileView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("Maintain Profile");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }
}
