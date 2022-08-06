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
        // Dashboard
        FXMLLoader dashboardView = new FXMLLoader();
        dashboardView.setLocation(getClass().getResource("view.fxml"));
        Scene dashboardScene = new Scene(dashboardView.load());

        // Maintain Housemates View
        Stage mhStage = createMaintainHousemateView(dashboardStage);

        // Main Controller
        new Controller(dashboardScene, mhStage);

        // Maintain Housemate Controller
        new MaintainHousemateController(mhStage);

        dashboardStage.setScene(dashboardScene);
        dashboardStage.setTitle("Dashboard");
        dashboardStage.setWidth(600);
        dashboardStage.setHeight(350);
        dashboardStage.setResizable(false);
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

    private BorderPane createMHContent(String n, String func) {
        // Top
        Label lblAddHousemates = new Label("Add Housemates");
        lblAddHousemates.setStyle("-fx-font-weight: bold");

        // Left
        Label lblHousemateID = new Label("Housemate ID: ");
        TextField tfHousemateID = new TextField();
        tfHousemateID.setId(n + "mhHousemateID");
        tfHousemateID.setDisable(true);

        Label lblFirstname = new Label("First Name: ");
        TextField tfFirstname = new TextField();
        tfFirstname.setId(n + "mhFirstname");

        Label lblLastname = new Label("Last Name: ");
        TextField tfLastname = new TextField();
        tfLastname.setId(n + "mhLastname");

        Label lblPhoneNumber = new Label("Phone Number");
        TextField tfPhoneNumber = new TextField();
        tfPhoneNumber.setId(n + "mhPhoneNumber");

        Label lblPassword = new Label("Password: ");
        TextField tfPassword = new TextField();
        tfPassword.setId(n + "mhPassword");

        CheckBox cbIsLeader = new CheckBox("Leader: ");
        cbIsLeader.setId(n + "mhIsLeader");
        cbIsLeader.setDisable(true);

        GridPane gpLeft = new GridPane();
        gpLeft.setHgap(5);
        gpLeft.setVgap(5);
        gpLeft.setPadding(new Insets(5));

        gpLeft.add(lblHousemateID, 0, 0);
        gpLeft.add(tfHousemateID, 1, 0);

        gpLeft.add(lblFirstname, 0, 1);
        gpLeft.add(tfFirstname, 1, 1);

        gpLeft.add(lblLastname, 0, 2);
        gpLeft.add(tfLastname, 1, 2);

        gpLeft.add(lblPhoneNumber, 0, 3);
        gpLeft.add(tfPhoneNumber, 1, 3);

        gpLeft.add(lblPassword, 0, 4);
        gpLeft.add(tfPassword, 1, 4);

        gpLeft.add(cbIsLeader, 1, 5);

        // Right
        TableView<Housemate> tvHousemates = new TableView<>();
        tvHousemates.setId(n + "mhTable");

        Button btnFunc = new Button(func);
        btnFunc.setId(n + "mh" + func);

        Button btnCancel = new Button("Cancel");
        btnCancel.setId(n + "mhCancel");

        Button btnClear = new Button("Clear");
        btnClear.setId(n + "mhClear");

        HBox ahbButtons = new HBox(btnFunc, btnCancel, btnClear);

        VBox vbRight = new VBox(tvHousemates, ahbButtons);

        BorderPane bpContent = new BorderPane();
        bpContent.setTop(lblAddHousemates);
        bpContent.setLeft(gpLeft);
        bpContent.setRight(vbRight);
        bpContent.setPadding(new Insets(10));

        return bpContent;
    }
}
