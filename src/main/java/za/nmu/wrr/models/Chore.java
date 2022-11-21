package za.nmu.wrr.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Chore {
    public StringProperty choreID = new SimpleStringProperty();
    public StringProperty description = new SimpleStringProperty();
    public IntegerProperty isCompleted = new SimpleIntegerProperty();
    public StringProperty dateCompleted = new SimpleStringProperty();
    public StringProperty areaName = new SimpleStringProperty();

    public Chore() {};
    public Chore(String choreID, String description, Integer isCompleted, String dateCompleted, String areaName) {
        this.choreID.setValue(choreID);
        this.description.setValue(description);
        this.isCompleted.setValue(isCompleted);
        this.dateCompleted.setValue(dateCompleted);
        this.areaName.setValue(areaName);
    }

    public StringProperty choreIDProperty() {
        return choreID;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public IntegerProperty isCompletedProperty() {
        return isCompleted;
    }

    public StringProperty dateCompletedProperty() {
        return dateCompleted;
    }

    public StringProperty areaNameProperty() {
        return areaName;
    }
}
