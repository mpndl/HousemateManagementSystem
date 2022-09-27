package za.nmu.wrr;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ManageChoreController extends Controller {
    private ObservableList<Chore> chores = FXCollections.observableArrayList();
    private final String ADD = "add_mc_";
    private final String EDIT = "edit_mc_";
    private final String REMOVE = "remove_mc_";
    private Scene dashboardScene;
    private Stage mcStage;
    private boolean linked = false;
    public ManageChoreController(){}
    public ManageChoreController(Scene dashboardScene,Stage mcStage) {
        this.dashboardScene = dashboardScene;
        this.mcStage = mcStage;
        setupDashboardLinks(dashboardScene, mcStage);
    }

    private void removeChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup("#"+ REMOVE + "table");
        TextField tfChoreID = (TextField) mcStage.getScene().lookup("#"+ REMOVE + "choreid");
        TextField tfAreaName = (TextField) mcStage.getScene().lookup("#"+ REMOVE + "areaname");
        TextArea taDescription = (TextArea) mcStage.getScene().lookup("#"+ REMOVE + "description");
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup("#"+ REMOVE + "completed");
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup("#"+ REMOVE + "datacompleted");

        Button btnRemove = (Button) mcStage.getScene().lookup("#"+ REMOVE + "remove");

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if(temp != null) {
                btnRemove.setDisable(false);
                cbCompleted.setSelected(temp.isCompleted.getValue() != 0);
                tfChoreID.setText(temp.choreID.getValue());
                tfAreaName.setText(temp.areaName.getValue());
                taDescription.setText(temp.description.getValue());
                cbCompleted.setText(temp.isCompleted.getValue() + "");
                if (dpDateCompleted != null && temp.dateCompleted.getValue() != null) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(temp.dateCompleted.getValue(), dateTimeFormatter);
                    dpDateCompleted.setValue(localDate);
                }
                else if(dpDateCompleted != null && temp.dateCompleted.getValue() == null) dpDateCompleted.setValue(null);
            }
            else btnRemove.setDisable(true);
        });

        btnRemove.setOnAction(event -> {
            Chore chore = new Chore();
            chore.choreID.setValue(tfChoreID.getText());
            chore.description.setValue(taDescription.getText());
            if(cbCompleted.isSelected())
                chore.isCompleted.setValue(1);
            else
                chore.isCompleted.setValue(0);
            chore.areaName.setValue(tfAreaName.getText());

            int index = getChoreIndex(chore.choreID.getValue());
            if(index != -1) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Remove Chore");
                alert.setHeaderText("Remove " + "[ID="+chore.choreID.getValue()+"] " + chore.description.getValue());
                alert.setContentText("Are you sure you want to remove chore?");

                ButtonType btRemove = new ButtonType("Remove");
                ButtonType btCancel = new ButtonType("Cancel");

                alert.getButtonTypes().setAll(btRemove, btCancel);
                Optional<ButtonType> result = alert.showAndWait();

                if(result.get() == btRemove) {

                    chores.remove(index);

                    database.executeUpdate("DELETE FROM Swap WHERE choreID = '" + chore.choreID.getValue() + "' AND housemateID = " + loggedInUser.housemateID.getValue());
                    database.executeUpdate("DELETE FROM Chore WHERE choreID = '" + chore.choreID.getValue() + "'");

                    tfChoreID.setText("");
                    taDescription.setText("");
                    cbCompleted.setSelected(false);
                    if (dpDateCompleted != null)
                    dpDateCompleted.setValue(null);
                    cbCompleted.setSelected(false);
                    tvChores.getSelectionModel().clearSelection();
                }
                else
                    alert.close();
            }
        });
    }

    private void editChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup("#"+ EDIT + "table");
        TextField tfChoreID = (TextField) mcStage.getScene().lookup("#"+ EDIT + "choreid");
        TextField tfAreaName = (TextField) mcStage.getScene().lookup("#"+ EDIT + "areaname");
        TextArea taDescription = (TextArea) mcStage.getScene().lookup("#"+ EDIT + "description");
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup("#"+ EDIT + "completed");
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup("#"+ EDIT + "datecompleted");

        dpDateCompleted.disableProperty().bind(cbCompleted.selectedProperty().not());

        AtomicReference<String> areaName = new AtomicReference<>("");
        AtomicReference<String> description = new AtomicReference<>("");
        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicReference<LocalDate> dateCompleted = new AtomicReference<>();

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if(temp != null) {
                cbCompleted.setSelected(temp.isCompleted.getValue() != 0);
                tfChoreID.setText(temp.choreID.getValue());
                tfAreaName.setText(temp.areaName.getValue());
                taDescription.setText(temp.description.getValue());
                dateCompleted.set(null);
                if (temp.dateCompleted.getValue() != null) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(temp.dateCompleted.getValue().replace("\'",""), dateTimeFormatter);
                    dateCompleted.set(localDate);
                    dpDateCompleted.setValue(localDate);
                }

                areaName.set(temp.areaName.getValue());
                description.set(temp.description.getValue());
                completed.set(temp.isCompleted.getValue() != 0);

                tfAreaName.setDisable(false);
                taDescription.setDisable(false);
                cbCompleted.setDisable(false);
            }
            else {
                tfAreaName.setDisable(true);
                taDescription.setDisable(true);
                cbCompleted.setDisable(true);
            }
        });

        Button btnEdit = (Button) mcStage.getScene().lookup("#"+ EDIT + "edit");

        btnEdit.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            Chore chore = tvChores.getSelectionModel().getSelectedItem();
            boolean comp = false;
            boolean sameAreaName = false;
            boolean sameDescription = false;
            boolean empty = false;
            if(tfAreaName.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Area name not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfAreaName.setStyle("-fx-border-color: red");
                empty = true;
            }
            else {
                if (tfAreaName.getText().equals(chore.areaName.getValue())) {
                    Tooltip tooltip = new Tooltip("Area name not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    tfAreaName.setTooltip(tooltip);
                    tfAreaName.setStyle("-fx-border-color: orange");
                    sameAreaName = true;
                }
                else {
                    tfAreaName.setTooltip(null);
                    tfAreaName.setStyle("-fx-border-color: green");
                }
            }
            if (taDescription.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Description not entered.");
                tooltip.setShowDelay(Duration.ONE);
                taDescription.setTooltip(tooltip);
                taDescription.setStyle("-fx-border-color: red");
                empty = true;
            }
            else {
                if (taDescription.getText().equals(chore.description.getValue())) {
                    Tooltip tooltip = new Tooltip("Description not changed.");
                    tooltip.setShowDelay(Duration.ONE);
                    taDescription.setTooltip(tooltip);
                    taDescription.setStyle("-fx-border-color: orange");
                    sameDescription = true;
                }
                else  {
                    taDescription.setTooltip(null);
                    taDescription.setStyle("-fx-border-color: green");
                }
            }
            if (cbCompleted.isSelected()) {
                try {
                    if (dpDateCompleted.getValue() == null || dpDateCompleted.getValue().toString().isEmpty())
                        throw  new DateTimeException("Date is empty");
                    dpDateCompleted.getConverter().fromString(dpDateCompleted.getValue().toString().replace("-", "/"));
                    dpDateCompleted.setStyle("-fx-border-color: green");
                    dpDateCompleted.setTooltip(null);
                }
                catch (DateTimeException e) {
                    dpDateCompleted.setStyle("-fx-border-color: red");
                    Tooltip tooltip = new Tooltip("Date not picked..");
                    tooltip.setShowDelay(Duration.ONE);
                    dpDateCompleted.setTooltip(tooltip);
                    comp = true;
                }
            }
            else {
                dpDateCompleted.setStyle("-fx-border-color: green");
                dpDateCompleted.setTooltip(null);
            }

            if (comp)
                return true;
            else if (empty)
                return true;
            else if (sameAreaName && sameDescription)
                return true;
            else {
                tfAreaName.setTooltip(null);
                tfAreaName.setStyle("-fx-border-color: green");

                taDescription.setTooltip(null);
                taDescription.setStyle("-fx-border-color: green");

                return false;
            }

        }, tfAreaName.textProperty(), taDescription.textProperty(), cbCompleted.selectedProperty(), dpDateCompleted.valueProperty()));

        btnEdit.setOnAction(event -> {
            Chore chore = new Chore();
            chore.choreID.setValue(tfChoreID.getText());
            chore.description.setValue(taDescription.getText());
            if(cbCompleted.isSelected())
                chore.isCompleted.setValue(1);
            else
                chore.isCompleted.setValue(0);
            if (dpDateCompleted.getValue() != null && !dpDateCompleted.isDisabled())
                chore.dateCompleted.setValue( "'" + dpDateCompleted.getValue().toString() + "'");
            chore.areaName.setValue(tfAreaName.getText());

            try {
                int index = getChoreIndex(chore.choreID.getValue());
                if (index != -1) {
                    database.executeUpdate("UPDATE Chore SET description = '" + chore.description.getValue() + "', isCompleted = '" + chore.isCompleted.getValue().toString() + "', dateCompleted = " + chore.dateCompleted.getValue() + ", areaName = '" + chore.areaName.getValue() + "' WHERE choreID = '" + chore.choreID.getValue() + "'");
                    chores.set(index, chore);
                    tvChores.getSelectionModel().select(chore);
                    tfAreaName.setText(chore.areaName.getValue());
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void addChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup("#"+ ADD + "table");
        TextField tfChoreID = (TextField) mcStage.getScene().lookup("#"+ ADD + "choreid");
        TextField tfAreaName = (TextField) mcStage.getScene().lookup("#"+ ADD + "areaname");
        TextArea taDescription = (TextArea) mcStage.getScene().lookup("#"+ ADD + "description");
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup("#"+ ADD + "completed");
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup("#"+ ADD + "datecompleted");

        Button btnAdd = (Button) mcStage.getScene().lookup("#"+ ADD + "add");
        Button btnClear = (Button) mcStage.getScene().lookup("#"+ ADD + "clear");
        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> tfAreaName.getText().isEmpty() && taDescription.getText().isEmpty()
                , tfAreaName.textProperty(), taDescription.textProperty()));
        CheckBox cbSelfAssign = (CheckBox) mcStage.getScene().lookup("#"+ ADD + "self_assign");

        cbSelfAssign.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return tfAreaName.getText().isEmpty() || taDescription.getText().isEmpty();
        }, tfAreaName.textProperty(), taDescription.textProperty()));

        btnAdd.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            boolean empty = false;
            if(tfAreaName.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Area name not entered.");
                tooltip.setShowDelay(Duration.ONE);
                tfAreaName.setTooltip(tooltip);
                tfAreaName.setStyle("-fx-border-color: red");
                empty = true;
            }
            else {
                tfAreaName.setTooltip(null);
                tfAreaName.setStyle("-fx-border-color: green");
            }

            if (taDescription.getText().isEmpty()) {
                Tooltip tooltip = new Tooltip("Description not entered.");
                tooltip.setShowDelay(Duration.ONE);
                taDescription.setTooltip(tooltip);
                taDescription.setStyle("-fx-border-color: red");
                empty = true;
            }
            else {
                taDescription.setStyle("-fx-border-color: green");
                taDescription.setTooltip(null);
            }

            return empty;
        }, tfAreaName.textProperty(), taDescription.textProperty()));

        btnAdd.setOnAction(event -> {
            Chore chore = new Chore();
            chore.choreID.setValue(tfChoreID.getText());
            chore.description.setValue(taDescription.getText());
            if(cbCompleted.isSelected())
                chore.isCompleted.setValue(1);
            else
                chore.isCompleted.setValue(0);
            chore.areaName.setValue(tfAreaName.getText());

            try {
                int id;
                if (loggedInUser.isLeader.getValue() == 1 && !cbSelfAssign.isSelected())
                    id = database.executeInsert("INSERT INTO Chore(description, areaName) VALUES('" + chore.description.getValue() + "', '" + chore.areaName.getValue() + "')");
                else {
                    id = database.executeInsert("INSERT INTO Chore(description, areaName) VALUES('" + chore.description.getValue() + "', '" + chore.areaName.getValue() + "')");
                    database.executeInsert("INSERT INTO Swap(housemateID, choreID) VALUES(" + loggedInUser.housemateID.getValue() + "," + id + ")");
                }
                if (id != -1)
                    chore.choreID.setValue(id + "");
                tfChoreID.setText("");
                taDescription.setText("");
                cbCompleted.setSelected(false);
                dpDateCompleted.setValue(null);
                tfAreaName.setText("");
                chores.add(chore);
                tvChores.getSelectionModel().clearSelection();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void linkToScene(Stage mcStage, String n) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup("#"+ n + "table");
        TextField tfChoreID = (TextField) mcStage.getScene().lookup("#"+ n + "choreid");
        TextField tfAreaName = (TextField) mcStage.getScene().lookup("#"+ n + "areaname");
        TextArea taDescription = (TextArea) mcStage.getScene().lookup("#"+ n + "description");
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup("#"+ n + "completed");
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup("#"+ n + "datecompleted");

        TableColumn tcChoreID = new TableColumn("Chore ID");
        tcChoreID.setCellValueFactory(new PropertyValueFactory<>("choreID"));
        tcChoreID.setPrefWidth(100);

        TableColumn tcAreaName = new TableColumn("Area Name");
        tcAreaName.setCellValueFactory(new PropertyValueFactory("areaName"));
        tcAreaName.setPrefWidth(100);

        TableColumn tcDescription = new TableColumn("Description");
        tcDescription.setCellValueFactory(new PropertyValueFactory("description"));
        tcDescription.setPrefWidth(100);

        TableColumn tcCompleted = new TableColumn("Completed");
        tcCompleted.setCellValueFactory(new PropertyValueFactory("isCompleted"));
        tcCompleted.setPrefWidth(100);

        TableColumn tcDateCompleted = new TableColumn("Date Completed");
        tcDateCompleted.setCellValueFactory(new PropertyValueFactory("dateCompleted"));
        tcDateCompleted.setPrefWidth(100);

        tvChores.getColumns().addAll(tcChoreID, tcAreaName, tcDescription, tcCompleted, tcDateCompleted);

        tvChores.setItems(chores);

        Button btnCancel = (Button) mcStage.getScene().lookup("#"+n+"cancel");
        btnCancel.setOnAction(event -> {
            mcStage.close();
        });

        Button btnClear = (Button) mcStage.getScene().lookup("#"+n+"clear");
        btnClear.setOnAction(event -> {
            tfChoreID.setText("");
            tfChoreID.setText("");
            taDescription.setText("");
            cbCompleted.setSelected(false);
            dpDateCompleted.setValue(null);
            tfAreaName.setText("");
            tvChores.getSelectionModel().clearSelection();
        });

        CheckBox cbSelfAssign = (CheckBox) mcStage.getScene().lookup("#" + ADD + "self_assign");
        if (loggedInUser.isLeader.getValue() == 1)
            cbSelfAssign.setVisible(true);
    }

    private int getChoreIndex(String id) {
        for (int i = 0; i < chores.size(); i++) {

            if(chores.get(i).choreID.getValue() != null && chores.get(i).choreID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    private void setupChores() {
        ResultSet rs;
        if (loggedInUser.isLeader.getValue() == 0)
            rs = database.executeQuery("SELECT Chore.choreID, Chore.description, Chore.isCompleted, Chore.dateCompleted, Chore.areaName FROM Chore INNER JOIN Swap ON Chore.choreID = Swap.choreID WHERE HousemateID = " + loggedInUser.housemateID.getValue());
        else
            rs = database.executeQuery("SELECT Chore.choreID, Chore.description, Chore.isCompleted, Chore.dateCompleted, Chore.areaName FROM Chore FULL JOIN Swap ON Chore.choreID = Swap.choreID");
        try {
            while (rs.next()) {
                Chore chore = new Chore();
                chore.choreID.setValue(rs.getString("choreID"));
                chore.description.setValue(rs.getString("description"));
                chore.isCompleted.setValue(Integer.parseInt(rs.getString("isCompleted")));
                chore.dateCompleted.setValue(rs.getString("dateCompleted"));
                chore.areaName.setValue(rs.getString("areaName"));

                chores.add(chore);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupDashboardLinks(Scene dashboardScene, Stage mcStage) {
        Hyperlink hpManageChore = (Hyperlink) dashboardScene.lookup("#mc_dashboard");
        if (loggedInUser != null) {
            if (loggedInUser.isLeader.getValue() == 0)
                hpManageChore.setDisable(true);
            else {
                hpManageChore.setOnAction(event -> {
                    if (!linked) {
                        setupChores();
                        // Add
                        linkToScene(mcStage, ADD);
                        addChore(mcStage);
                        // Edit
                        linkToScene(mcStage, EDIT);
                        editChore(mcStage);
                        // Remove
                        linkToScene(mcStage, REMOVE);
                        removeChore(mcStage);
                        linked = true;
                    }
                    mcStage.showAndWait();
                });
            }
        }
        else
            hpManageChore.setDisable(true);
    }
}
