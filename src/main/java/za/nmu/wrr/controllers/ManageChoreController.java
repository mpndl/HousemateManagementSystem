package za.nmu.wrr.controllers;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import za.nmu.wrr.models.Chore;
import za.nmu.wrr.models.Resource;
import za.nmu.wrr.circle.CircleView;

import java.sql.ResultSet;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ManageChoreController extends Controller {
    public final static String v = "Manage Chores";

    private final ObservableList<Chore> chores = FXCollections.observableArrayList();
    ObservableList<Resource> resources = FXCollections.observableArrayList();
    ObservableList<Resource> selectedResources = FXCollections.observableArrayList();
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
        linksRunnable.add(() ->  setupDashboardLinks(dashboardScene));
    }

    private void removeChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup(getID(REMOVE, "table"));
        TextField tfChoreID = (TextField) mcStage.getScene().lookup(getID(REMOVE, "choreid"));
        TextField tfAreaName = (TextField) mcStage.getScene().lookup(getID(REMOVE, "areaname"));
        TextArea taDescription = (TextArea) mcStage.getScene().lookup(getID(REMOVE, "description"));
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup(getID(REMOVE, "completed"));
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup(getID(REMOVE, "datacompleted"));
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(REMOVE, "resources"));

        Button btnRemove = (Button) mcStage.getScene().lookup(getID(REMOVE, "remove"));

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if (temp != null) {
                if (loggedInUser.isLeader.getValue() == 1)
                    setupResources(temp, REMOVE);
                else setupResources(null, REMOVE);
                if (resources.size() > 0)
                    selectResources(temp, REMOVE);
                else selectedResources.clear();
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
                } else if (dpDateCompleted != null && temp.dateCompleted.getValue() == null)
                    dpDateCompleted.setValue(null);
            } else btnRemove.setDisable(true);
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
                    ArrayList<Resource> temp = new ArrayList<>();
                    for (int i = 0; i < lvResources.getSelectionModel().getSelectedItems().size();i++) {
                        Resource curResource = lvResources.getSelectionModel().getSelectedItems().get(i);
                        Resource resource = new Resource(curResource.resourceName.getValue(), curResource.isFinished.getValue(),
                                curResource.housemateID.getValue());
                        temp.add(resource);
                    }

                    chores.remove(index);

                    database.executeUpdate("DELETE FROM Swap WHERE choreID = '" + chore.choreID.getValue() + "' AND housemateID = " + loggedInUser.housemateID.getValue());
                    database.executeUpdate("DELETE FROM Chore WHERE choreID = '" + chore.choreID.getValue() + "'");

                    for (Resource resource : temp) {
                        database.executeUpdate("DELETE FROM Usage WHERE resourceName = '" + resource.resourceName.getValue() + "'");
                    }

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

    private void setupResources(Chore chore, String tab) {
        resources.clear();
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(tab, "resources"));
        lvResources.setItems(resources);
        ResultSet rs;
        if ( chore == null)
            rs = database.executeQuery("SELECT * FROM Resource WHERE housemateID = " + loggedInUser.housemateID.getValue());
        else
            rs = database.executeQuery("SELECT * FROM Chore INNER JOIN Swap ON Chore.choreID = Swap.choreID INNER JOIN Resource ON" +
                    " Swap.housemateID = Resource.housemateID WHERE Chore.choreID = " + chore.choreID.getValue());
        try {
            while (rs.next()) {
                Resource resource = new Resource(rs.getString("resourceName"), Integer.parseInt(rs.getString("isFinished")),
                        rs.getString("housemateID"));
                resources.add(resource);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSelectedResources(Chore chore) {
        selectedResources.clear();
        ResultSet rs;
        if (loggedInUser.isLeader.getValue() == 0)
            rs = database.executeQuery("SELECT * FROM Resource INNER JOIN Usage ON Resource.resourceName = Usage.resourceName " +
                    "WHERE Usage.housemateID = " + loggedInUser.housemateID.getValue() + " AND choreID = " + chore.choreID.getValue());
        else {
            rs = database.executeQuery("SELECT * FROM Chore INNER JOIN Usage ON Chore.choreID = Usage.choreID INNER JOIN Resource" +
                    " ON Resource.resourceName = Usage.resourceName AND Resource.housemateID = Usage.housemateID WHERE Chore.choreID = " + chore.choreID.getValue());
        }
            try {
                while (rs.next()) {
                    Resource resource = new Resource(rs.getString("resourceName"), Integer.parseInt(rs.getString("isFinished")),
                            rs.getString("housemateID"));
                    selectedResources.add(resource);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void selectResources(Chore c, String tab) {
        setupSelectedResources(new Chore(c.choreID.getValue(), "", 0, "", ""));
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(tab, "resources"));

        lvResources.getSelectionModel().clearSelection();

        for (Resource oldSelectedResource: selectedResources) {
            for (int i = 0; i < lvResources.getItems().size(); i++) {
                Resource newSelectedResource = lvResources.getItems().get(i);
                if (newSelectedResource.resourceName.getValue().equals(oldSelectedResource.resourceName.getValue())) {
                    lvResources.getSelectionModel().select(i);
                }
            }
        }
    }

    private boolean sameResources() {
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(EDIT, "resources"));
        int sameCount = 0;
        if (lvResources.getSelectionModel().getSelectedItems().size() != selectedResources.size())
            return false;
        else {
            for (Resource oldSelectedResource: selectedResources) {
                for (Resource newSelectedResource: lvResources.getSelectionModel().getSelectedItems()) {
                    if (oldSelectedResource.resourceName.getValue().equals(newSelectedResource.resourceName.getValue()))
                        sameCount++;
                }
            }
            return sameCount == selectedResources.size();
        }
    }

    private void editChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup(getID(EDIT, "table"));
        TextField tfChoreID = (TextField) mcStage.getScene().lookup(getID(EDIT, "choreid"));
        TextField tfAreaName = (TextField) mcStage.getScene().lookup(getID(EDIT, "areaname"));
        TextArea taDescription = (TextArea) mcStage.getScene().lookup(getID(EDIT, "description"));
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup(getID(EDIT, "completed"));
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup(getID(EDIT, "datecompleted"));
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(EDIT, "resources"));
        lvResources.getSelectionModel().selectedItemProperty().addListener((observableValue, resource, t1) -> {
            Resource temp = observableValue.getValue();
            if (temp == null) tvChores.getSelectionModel().select(tvChores.getSelectionModel().getSelectedItem());
        });

        dpDateCompleted.disableProperty().bind(cbCompleted.selectedProperty().not());

        AtomicReference<String> areaName = new AtomicReference<>("");
        AtomicReference<String> description = new AtomicReference<>("");
        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicReference<LocalDate> dateCompleted = new AtomicReference<>();

        tvChores.getSelectionModel().selectedItemProperty().addListener((observableValue, housemate, t1) -> {
            Chore temp = observableValue.getValue();
            if (temp != null) {
                if (loggedInUser.isLeader.getValue() == 1)
                    setupResources(temp, EDIT);
                else setupResources(null, EDIT);
                if (resources.size() > 0)
                    selectResources(temp, EDIT);
                else selectedResources.clear();
                cbCompleted.setSelected(temp.isCompleted.getValue() != 0);
                tfChoreID.setText(temp.choreID.getValue());
                tfAreaName.setText(temp.areaName.getValue());
                taDescription.setText(temp.description.getValue());
                dateCompleted.set(null);
                if (temp.dateCompleted.getValue() != null) {
                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate localDate = LocalDate.parse(temp.dateCompleted.getValue().replace("'", ""), dateTimeFormatter);
                    dateCompleted.set(localDate);
                    dpDateCompleted.setValue(localDate);
                }

                areaName.set(temp.areaName.getValue());
                description.set(temp.description.getValue());
                completed.set(temp.isCompleted.getValue() != 0);

                tfAreaName.setDisable(false);
                taDescription.setDisable(false);
                cbCompleted.setDisable(false);
                lvResources.setDisable(false);
            } else {
                tfAreaName.setDisable(true);
                taDescription.setDisable(true);
                cbCompleted.setDisable(true);
                lvResources.setDisable(true);
            }
        });

        Button btnEdit = (Button) mcStage.getScene().lookup(getID(EDIT, "edit"));

        btnEdit.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            Chore chore = tvChores.getSelectionModel().getSelectedItem();
            boolean comp = false;
            boolean sameAreaName = false;
            boolean sameDescription = false;
            boolean sameResources = false;
            boolean empty = false;

            if (sameResources()) {
                Tooltip tooltip = new Tooltip("Resources not changed.");
                tooltip.setShowDelay(Duration.ONE);
                lvResources.setTooltip(tooltip);
                lvResources.setStyle("-fx-border-color: orange");
                sameResources = true;
            }
            else {
                lvResources.setTooltip(null);
                lvResources.setStyle("-fx-border-color: green");
            }

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
            else if (sameAreaName && sameDescription && sameResources)
                return true;
            else {
                tfAreaName.setTooltip(null);
                tfAreaName.setStyle("-fx-border-color: green");

                taDescription.setTooltip(null);
                taDescription.setStyle("-fx-border-color: green");

                lvResources.setTooltip(null);
                lvResources.setStyle("-fx-background-color: green");

                return false;
            }

        }, tfAreaName.textProperty(), taDescription.textProperty(), cbCompleted.selectedProperty(), dpDateCompleted.valueProperty()
        , lvResources.getSelectionModel().selectedItemProperty()));

        btnEdit.setOnAction(event -> {
                Chore chore = new Chore();
                chore.choreID.setValue(tfChoreID.getText());
                chore.description.setValue(taDescription.getText());
                if (cbCompleted.isSelected())
                    chore.isCompleted.setValue(1);
                else
                    chore.isCompleted.setValue(0);
                if (dpDateCompleted.getValue() != null && !dpDateCompleted.isDisabled())
                    chore.dateCompleted.setValue("'" + dpDateCompleted.getValue().toString() + "'");
                chore.areaName.setValue(tfAreaName.getText());

            ArrayList<Resource> temp = new ArrayList<>();
            for (int i = 0; i < lvResources.getSelectionModel().getSelectedItems().size();i++) {
                Resource curResource = lvResources.getSelectionModel().getSelectedItems().get(i);
                Resource resource = new Resource(curResource.resourceName.getValue(), curResource.isFinished.getValue(),
                        curResource.housemateID.getValue());
                temp.add(resource);
            }

                try {
                    int index = getChoreIndex(chore.choreID.getValue());
                    if (index != -1) {
                        database.executeUpdate("UPDATE Chore SET description = '" + chore.description.getValue() + "', isCompleted = '" + chore.isCompleted.getValue().toString() + "', dateCompleted = " + chore.dateCompleted.getValue() + ", areaName = '" + chore.areaName.getValue() + "' WHERE choreID = '" + chore.choreID.getValue() + "'");
                        chores.set(index, chore);
                        tvChores.getSelectionModel().select(chore);
                        tfAreaName.setText(chore.areaName.getValue());
                    }
                    if (index != -1) {
                        for (Resource resource : lvResources.getItems()) {
                            database.executeUpdate("DELETE FROM Usage WHERE resourceName = '" + resource.resourceName.getValue() + "' AND choreID = " + chore.choreID.getValue());
                        }

                        if (temp.size() > 0)
                            selectedResources.clear();

                        for (Resource resource : temp) {
                            database.executeInsert("INSERT INTO Usage(choreID, resourceName, housemateID)" +
                                    "VALUES(" + chore.choreID.getValue() + ", '" + resource.resourceName.getValue() + "', " + resource.housemateID.getValue() + ")");
                            selectedResources.add(resource);
                        }
                        tvChores.getSelectionModel().clearSelection();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    public void addChore(Stage mcStage) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup(getID(ADD, "table"));
        TextField tfChoreID = (TextField) mcStage.getScene().lookup(getID(ADD, "choreid"));
        TextField tfAreaName = (TextField) mcStage.getScene().lookup(getID(ADD, "areaname"));
        TextArea taDescription = (TextArea) mcStage.getScene().lookup(getID(ADD, "description"));
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup(getID(ADD, "completed"));
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup(getID(ADD, "datecompleted"));
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(ADD, "resources"));

        Button btnAdd = (Button) mcStage.getScene().lookup(getID(ADD, "add"));
        Button btnClear = (Button) mcStage.getScene().lookup(getID(ADD, "clear"));
        btnClear.disableProperty().bind(Bindings.createBooleanBinding(() -> tfAreaName.getText().isEmpty() && taDescription.getText().isEmpty()
                , tfAreaName.textProperty(), taDescription.textProperty()));
        CheckBox cbSelfAssign = (CheckBox) mcStage.getScene().lookup(getID(ADD, "self_assign"));

        if (loggedInUser.isLeader.getValue() == 0) setupResources(null, ADD);

        cbSelfAssign.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            return tfAreaName.getText().isEmpty() || taDescription.getText().isEmpty();
        }, tfAreaName.textProperty(), taDescription.textProperty()));

        cbSelfAssign.setOnAction(actionEvent -> {
            if (cbSelfAssign.isSelected()) {
                setupResources(null, ADD);
            }
            else resources.clear();
            lvResources.setItems(resources);
            if (resources.size() > 0)
                lvResources.getSelectionModel().select(0);
        });

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
        }, tfAreaName.textProperty(), taDescription.textProperty(), lvResources.getSelectionModel().selectedItemProperty()));



        btnAdd.setOnAction(event -> {
                Chore chore = new Chore();
                chore.choreID.setValue(tfChoreID.getText());
                chore.description.setValue(taDescription.getText());
                if (cbCompleted.isSelected())
                    chore.isCompleted.setValue(1);
                else
                    chore.isCompleted.setValue(0);
                chore.areaName.setValue(tfAreaName.getText());

            ArrayList<Resource> temp = new ArrayList<>();
            for (int i = 0; i < lvResources.getSelectionModel().getSelectedItems().size();i++) {
                Resource curResource = lvResources.getSelectionModel().getSelectedItems().get(i);
                Resource resource = new Resource(curResource.resourceName.getValue(), curResource.isFinished.getValue(),
                        curResource.housemateID.getValue());
                temp.add(resource);
            }

                try {
                    int id;
                    if (loggedInUser.isLeader.getValue() == 1 && !cbSelfAssign.isSelected())
                        id = database.executeInsert("INSERT INTO Chore(description, areaName) VALUES('" + chore.description.getValue() + "', '" + chore.areaName.getValue() + "')");
                    else {
                        id = database.executeInsert("INSERT INTO Chore(description, areaName) VALUES('" + chore.description.getValue() + "', '" + chore.areaName.getValue() + "')");
                        database.executeInsert("INSERT INTO Swap(housemateID, choreID) VALUES(" + loggedInUser.housemateID.getValue() + "," + id + ")");
                    }

                    if (id != -1) {
                        ObservableList<Resource> selectedItems = lvResources.getSelectionModel().getSelectedItems();
                        for (Resource resource : selectedItems) {
                            database.executeInsert("INSERT INTO Usage(choreID, resourceName, housemateID)" +
                                    "VALUES(" + id + ", '" + resource.resourceName.getValue() + "', "+ resource.housemateID.getValue() +")");
                        }
                    }

                    if (id != -1) {
                        for (Resource resource : temp) {
                            database.executeUpdate("DELETE FROM Usage WHERE resourceName = '" + resource.resourceName.getValue() + "' AND choreID = " + id);
                        }

                        if (temp.size() > 0)
                            selectedResources.clear();

                        for (Resource resource : temp) {
                            database.executeInsert("INSERT INTO Usage(choreID, resourceName, housemateID)" +
                                    "VALUES(" + id + ", '" + resource.resourceName.getValue() + "', " + resource.housemateID.getValue() + ")");
                            selectedResources.add(resource);
                        }
                        tvChores.getSelectionModel().clearSelection();
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }

    private void linkToScene(Stage mcStage, String tab) {
        TableView<Chore> tvChores = (TableView<Chore>) mcStage.getScene().lookup(getID(tab, "table"));
        TextField tfChoreID = (TextField) mcStage.getScene().lookup(getID(tab, "choreid"));
        TextField tfAreaName = (TextField) mcStage.getScene().lookup(getID(tab, "areaname"));
        TextArea taDescription = (TextArea) mcStage.getScene().lookup(getID(tab, "description"));
        CheckBox cbCompleted = (CheckBox) mcStage.getScene().lookup(getID(tab, "completed"));
        DatePicker dpDateCompleted = (DatePicker) mcStage.getScene().lookup(getID(tab, "datecompleted"));
        ListView<Resource> lvResources = (ListView<Resource>) mcStage.getScene().lookup(getID(tab, "resources"));
        lvResources.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Callback<Resource, Observable[]> extractor = resource -> new Observable[] {resource.resourceNameProperty(),
                resource.isFinishedProperty(), resource.housemateIDProperty()};

        resources = FXCollections.observableArrayList(extractor);

        lvResources.setItems(resources);

        lvResources.getSelectionModel().selectFirst();

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

        Button btnCancel = (Button) mcStage.getScene().lookup(getID(tab, "cancel"));
        btnCancel.setOnAction(event -> {
            mcStage.close();
        });

        Button btnClear = (Button) mcStage.getScene().lookup(getID(tab, "clear"));
        btnClear.setOnAction(event -> {
            tfChoreID.setText("");
            tfChoreID.setText("");
            taDescription.setText("");
            cbCompleted.setSelected(false);
            dpDateCompleted.setValue(null);
            tfAreaName.setText("");
            tvChores.getSelectionModel().clearSelection();
        });

        CheckBox cbSelfAssign = (CheckBox) mcStage.getScene().lookup(getID(ADD, "self_assign"));
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

    private void onDashboardLinkClicked(CircleView cv) {
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
        if (cv == null) mcStage.showAndWait();
        else if (!cv.dragging()) mcStage.showAndWait();
    }

    private void setupDashboardLinks(Scene dashboardScene) {
        Hyperlink hpManageChore = (Hyperlink) dashboardScene.lookup("#mc_dashboard");
        links.put(v, this::onDashboardLinkClicked);

        avail(hpManageChore);
        hpManageChore.setOnAction(event -> onDashboardLinkClicked(null));
    }
}
