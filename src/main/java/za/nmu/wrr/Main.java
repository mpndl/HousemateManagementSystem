package za.nmu.wrr;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage dashboardStage) throws Exception {
        // Validate Housemate View/Controller
        Stage vhStage = createValidateHousemateView(dashboardStage);
        new ValidateHousemateController(dashboardStage, vhStage);

        // Dashboard/Main View/Controller
        FXMLLoader dashboardView = new FXMLLoader();
        dashboardView.setLocation(getClass().getResource("dashBoardView.fxml"));
        Scene dashboardScene = new Scene(dashboardView.load());
        Controller controller = new Controller(dashboardStage, dashboardScene, vhStage);

        // Maintain Housemates View/Controller
        Stage mhStage = createMaintainHousemateView(dashboardStage);
        new MaintainHousemateController(dashboardScene, mhStage);

        // Maintain Profile View/Controller
        Stage mpStage = createMaintainProfileView(dashboardStage);
        new MaintainProfileController(dashboardScene, mpStage);

        // Manage Chore View/Controller
        Stage mcStage = createManageChoreView(dashboardStage);
        new ManageChoreController(dashboardScene, mcStage);

        // Assign Chore View/Controller
        Stage acStage = createAssignChoreView(dashboardStage);
        new AssignChoreController(dashboardScene, acStage);

        // View Housemates View/Controller
        Stage vhStage2 = createViewHousematesView(dashboardStage);
        new ViewHousematesController(dashboardScene, vhStage2);

        // View Resources View/Controller
        Stage vrStage = createViewResourcesView(dashboardStage);
        new ViewResourcesController(dashboardScene, vrStage);

        dashboardStage.setScene(dashboardScene);
        dashboardStage.setTitle("Dashboard");
        dashboardStage.setWidth(600);
        dashboardStage.setHeight(350);
        dashboardStage.setResizable(false);
        dashboardStage.setOnCloseRequest(event -> {
            controller.database.disconnectFromDB();
        });
        Rectangle2D rectangle2D = Screen.getPrimary().getVisualBounds();
        dashboardStage.setX((rectangle2D.getWidth() - dashboardStage.getWidth()) / 2);
        dashboardStage.setY((rectangle2D.getHeight() - dashboardStage.getHeight()) / 2);
    }

    public Stage createAssignChoreView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("assignChoreView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("Assign Chore");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
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

    public Stage createViewHousematesView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("viewHousematesView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("View Housemates");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UTILITY);
        stage.initOwner(owner);

        return stage;
    }

    public Stage createViewResourcesView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("viewResourcesView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("View Resources");

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

    public Stage createValidateHousemateView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("validateHousemateView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("Login/Register");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        return stage;
    }

    public Stage createManageChoreView(Stage owner) throws IOException {
        Stage stage = new Stage();

        FXMLLoader mhLoader = new FXMLLoader();
        mhLoader.setLocation(getClass().getResource("manageChoreView.fxml"));

        Scene scene = new Scene(mhLoader.load());

        stage.setScene(scene);
        stage.setTitle("Manage Chore");

        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        return stage;
    }
}
