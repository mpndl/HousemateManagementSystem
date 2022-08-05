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
        setupHousemates();
        // Add
        linkToScene(mhStage, "a");
        addHousemates(mhStage);
        // Edit
        linkToScene(mhStage, "e");
        editHousemates(mhStage);
        // Remove
        linkToScene(mhStage, "r");
        removeHousemates(mhStage);

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

    private void removeHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#rmhTable");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#rmhHousemateID");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#rmhFirstname");
        tfFirstname.setDisable(true);
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#rmhLastname");
        tfLastname.setDisable(true);
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#rmhPhoneNumber");
        tfPhoneNumber.setDisable(true);
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#rmhPassword");
        tfPassword.setDisable(true);
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#rmhIsLeader");
        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
                cbIsLeader.setDisable(temp.isLeader.getValue() == 0);
                Button btnRemove = (Button) mhStage.getScene().lookup("#rmhRemove");
            }
        });

        Button btnRemove = (Button) mhStage.getScene().lookup("#rmhRemove");
        btnRemove.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if(index != -1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Remove Housemate");
                alert.setHeaderText("Remove " + "[ID="+housemate.housemateID.getValue()+"] " + housemate.firstName.getValue() + " " + housemate.lastName.getValue());
                alert.setContentText("Are you sure you want to remove housemate?");

                ButtonType btRemove = new ButtonType("Remove");
                ButtonType btCancel = new ButtonType("Cancel");

                alert.getButtonTypes().setAll(btRemove, btCancel);
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == btRemove) {

                    housemates.remove(index);

                    database.executeUpdate("DELETE FROM Housemate WHERE housemateID = '" + housemate.housemateID.getValue() + "'");

                    tfHousemateID.setText("");
                    tfFirstname.setText("");
                    tfLastname.setText("");
                    tfPhoneNumber.setText("");
                    tfPassword.setText("");
                    tvHousemates.getSelectionModel().clearSelection();
                }
                else
                    alert.close();
            }
        });
    }

    private void setupDashboardLinks(Scene dashboardScene, Stage mhStage) {
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#dbMaintainHousemates");
        hpMaintainHousemates.setOnAction(event -> {
            mhStage.showAndWait();
        });
    }

    private void linkToScene(Stage mhStage, String n) {
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#"+n+"mhHousemateID");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#"+n+"mhFirstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#"+n+"mhLastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#"+n+"mhPhoneNumber");
        tfPhoneNumber.addEventFilter(KeyEvent.KEY_TYPED, maxLength(10));
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#"+n+"mhPassword");
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#"+n+"mhIsLeader");

        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#"+n+"mhTable");

        TableColumn tcHousemateID = new TableColumn("Housemate ID");
        tcHousemateID.setCellValueFactory(new PropertyValueFactory<>("housemateID"));
        tcHousemateID.setPrefWidth(100);

        TableColumn tcFirstname = new TableColumn("First Name");
        tcFirstname.setCellValueFactory(new PropertyValueFactory("firstName"));
        tcFirstname.setPrefWidth(100);

        TableColumn tcLastname = new TableColumn("Last Name");
        tcLastname.setCellValueFactory(new PropertyValueFactory("lastName"));
        tcLastname.setPrefWidth(100);

        TableColumn tcPhoneNumber = new TableColumn("Phone Number");
        tcPhoneNumber.setCellValueFactory(new PropertyValueFactory("phoneNumber"));
        tcPhoneNumber.setPrefWidth(100);
        TableColumn tcPassword = new TableColumn("Password");
        tcPassword.setCellValueFactory(new PropertyValueFactory("password"));
        tcPassword.setPrefWidth(100);

        TableColumn tcIsLeader = new TableColumn("Leader");
        tcIsLeader.setCellValueFactory(new PropertyValueFactory<>("isLeader"));
        tcIsLeader.setPrefWidth(100);

        tvHousemates.getColumns().addAll(tcHousemateID, tcFirstname, tcLastname, tcPhoneNumber, tcPassword, tcIsLeader);

        tvHousemates.setItems(housemates);

        Button btnCancel = (Button) mhStage.getScene().lookup("#"+n+"mhCancel");
        btnCancel.setOnAction(event -> {
            mhStage.close();
        });

        Button abtnClear = (Button) mhStage.getScene().lookup("#"+n+"mhClear");
        abtnClear.setOnAction(event -> {
            tfHousemateID.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            tvHousemates.getSelectionModel().clearSelection();
        });
    }

    private void editHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#emhTable");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#emhHousemateID");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#emhFirstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#emhLastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#emhPhoneNumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#emhPassword");
        CheckBox cbIsLeader = (CheckBox) mhStage.getScene().lookup("#emhIsLeader");
        tvHousemates.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Housemate temp = observableValue.getValue();
            if(temp != null) {
                tfHousemateID.setText(temp.housemateID.getValue());
                tfFirstname.setText(temp.firstName.getValue());
                tfLastname.setText(temp.lastName.getValue());
                tfPhoneNumber.setText(temp.phoneNumber.getValue());
                tfPassword.setText(temp.password.getValue());
                cbIsLeader.setDisable(temp.isLeader.getValue() == 0);
            }
        });

        Button btnEdit = (Button) mhStage.getScene().lookup("#emhEdit");
        btnEdit.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.housemateID.setValue(tfHousemateID.getText());
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int index = getHousemateIndex(housemate.housemateID.getValue());
            if(index != -1) {
                housemates.set(index, housemate);

                database.executeUpdate("UPDATE Housemate SET firstName = '" + housemate.firstName.getValue() + "', lastName = '" + housemate.lastName.getValue() + "', password = '" + housemate.password.getValue() + "', phoneNumber = '" + housemate.phoneNumber.getValue() + "' WHERE housemateID = '" + housemate.housemateID.getValue() + "'");
            }
        });
    }

    private int getHousemateIndex(String id) {
        for (int i = 0; i < housemates.size(); i++) {

            if(housemates.get(i).housemateID.getValue() != null && housemates.get(i).housemateID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    public void addHousemates(Stage mhStage) {
        TableView<Housemate> tvHousemates = (TableView<Housemate>) mhStage.getScene().lookup("#amhTable");
        TextField tfHousemateID = (TextField) mhStage.getScene().lookup("#amhHousemateID");
        TextField tfFirstname = (TextField) mhStage.getScene().lookup("#amhFirstname");
        TextField tfLastname = (TextField) mhStage.getScene().lookup("#amhLastname");
        TextField tfPhoneNumber = (TextField) mhStage.getScene().lookup("#amhPhoneNumber");
        TextField tfPassword = (TextField) mhStage.getScene().lookup("#amhPassword");

        Button btnAdd = (Button) mhStage.getScene().lookup("#amhAdd");
        btnAdd.setOnAction(event -> {
            Housemate housemate = new Housemate();
            housemate.firstName.setValue(tfFirstname.getText());
            housemate.lastName.setValue(tfLastname.getText());
            housemate.phoneNumber.setValue(tfPhoneNumber.getText());
            housemate.password.setValue(tfPassword.getText());
            housemate.isLeader.set(0);

            int id = database.executeInsert("INSERT INTO Housemate(firstName, lastName, password, phoneNumber) VALUES('"+housemate.firstName.getValue()+"', '"+housemate.lastName.getValue()+"', '"+housemate.password.getValue()+"', '"+housemate.phoneNumber.getValue()+"')");
            if(id != -1)
                housemate.housemateID.setValue(id + "");
            tfHousemateID.setText("");
            tfFirstname.setText("");
            tfLastname.setText("");
            tfPhoneNumber.setText("");
            tfPassword.setText("");
            tvHousemates.getSelectionModel().clearSelection();
            housemates.add(housemate);
        });
    }

    private void setupHousemates() {
        ResultSet rs = database.executeQuery("SELECT * FROM Housemate");
        try {
            while (rs.next()) {
                Housemate housemate = new Housemate();
                housemate.housemateID.setValue(rs.getString("HousemateID"));
                housemate.firstName.setValue(rs.getString("firstName"));
                housemate.lastName.setValue(rs.getString("lastName"));
                housemate.password.setValue(rs.getString("password"));
                housemate.phoneNumber.setValue(rs.getString("phoneNumber"));
                housemate.isLeader.setValue(rs.getInt("isLeader"));

                housemates.add(housemate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
