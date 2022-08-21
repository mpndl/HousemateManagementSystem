package za.nmu.wrr;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class ManageChoreController extends Controller {
    private ObservableList<Chore> chores = FXCollections.observableArrayList();
    private final String ADD = "add_mc_";
    private final String EDIT = "edit_mc_";
    private final String REMOVE = "remove_mc_";
    private Scene dashboardScene;
    private Stage mcStage;
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

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if(temp != null) {
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
        });

        Button btnRemove = (Button) mcStage.getScene().lookup("#"+ REMOVE + "remove");
        Button btnClear = (Button) mcStage.getScene().lookup("#"+ REMOVE + "clear");
        btnClear.setDisable(true);
        tfChoreID.textProperty().addListener((observableValue, s, t1) -> {
            if(observableValue.getValue().length() > 0) {
                btnRemove.setDisable(false);
            }
            else {
                btnRemove.setDisable(true);
            }
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

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if(temp != null) {
                cbCompleted.setSelected(temp.isCompleted.getValue() != 0);
                tfChoreID.setText(temp.choreID.getValue());
                tfAreaName.setText(temp.areaName.getValue());
                taDescription.setText(temp.description.getValue());
                cbCompleted.setText(temp.isCompleted.getValue() + "");
                if (temp.dateCompleted.getValue() != null) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(temp.dateCompleted.getValue(), dateTimeFormatter);
                    dpDateCompleted.setValue(localDate);
                }
            }
        });

        Button btnEdit = (Button) mcStage.getScene().lookup("#"+ EDIT + "edit");
        Button btnClear = (Button) mcStage.getScene().lookup("#"+ EDIT + "clear");

        tfAreaName.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnEdit, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

        taDescription.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnEdit, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

        cbCompleted.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            setupDisableFuncs(btnEdit, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

        dpDateCompleted.setOnAction(actionEvent -> {
            setupDisableFuncs(btnEdit, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

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
                    database.executeUpdate("UPDATE Chore SET description = '" + chore.description.getValue() + "', isCompleted = '" + chore.isCompleted.getValue() + "', dateCompleted = " + chore.dateCompleted.getValue() + ", areaName = '" + chore.areaName.getValue() + "' WHERE choreID = '" + chore.choreID.getValue() + "'");
                    chores.set(index, chore);
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

        tfAreaName.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnAdd, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

        taDescription.textProperty().addListener((observableValue, s, t1) -> {
            setupDisableFuncs(btnAdd, btnClear, tfAreaName, taDescription, cbCompleted, dpDateCompleted);
        });

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
                    int id = database.executeInsert("INSERT INTO Chore(description, areaName) VALUES('" + chore.description.getValue() + "', '" + chore.areaName.getValue() + "')");
                    if (id != -1)
                        chore.choreID.setValue(id + "");
                    tfChoreID.setText("");
                    taDescription.setText("");
                    cbCompleted.setSelected(false);
                    dpDateCompleted.setValue(null);
                    tfAreaName.setText("");
                    chores.add(chore);
                    tvChores.getSelectionModel().clearSelection();
                    btnAdd.setDisable(true);
                    btnClear.setDisable(true);
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

        TableColumn tcChoreID = new TableColumn("Housemate ID");
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
    }

    private void setupDisableFuncs(Button func, Button clear, TextField areaName, TextArea description, CheckBox completed, DatePicker dateCompleted) {
        if (!areaName.getText().isEmpty() || !description.getText().isEmpty()) {
            clear.setDisable(false);
            if (!areaName.getText().isEmpty() && !description.getText().isEmpty()) {
                if (completed.isSelected()) {
                    try {
                        if (dateCompleted.getEditor().getText().isEmpty())
                            throw  new DateTimeException("Date is empty");
                        dateCompleted.getConverter().fromString(dateCompleted.getEditor().getText());
                    }
                    catch (DateTimeException e) {
                        func.setDisable(true);
                        return;
                    }
                    func.setDisable(false);
                } else {
                    func.setDisable(false);
                }
            }
            else func.setDisable(true);
        }
        else {
            clear.setDisable(true);
            func.setDisable(true);
        }
    }

    private int getChoreIndex(String id) {
        for (int i = 0; i < chores.size(); i++) {

            if(chores.get(i).choreID.getValue() != null && chores.get(i).choreID.getValue().equals(id))
                return i;
        }
        return -1;
    }

    private void setupChores() {
        ResultSet rs = database.executeQuery("SELECT * FROM Chore");
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
        Hyperlink hpMaintainHousemates = (Hyperlink) dashboardScene.lookup("#mc_dashboard");
        hpMaintainHousemates.setOnAction(event -> {
            if (chores.size() == 0) {
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
            }
                mcStage.showAndWait();
        });
    }
}
