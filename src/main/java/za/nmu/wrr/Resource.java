package za.nmu.wrr;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Resource {
    public StringProperty resourceName = new SimpleStringProperty();
    public IntegerProperty isFinished = new SimpleIntegerProperty();
    public StringProperty housemateID = new SimpleStringProperty();

    public Resource(String resourceName, Integer isFinished, String housemateID) {
        this.resourceName.setValue(resourceName);
        this.isFinished.setValue(isFinished);
        this.housemateID.setValue(housemateID);
    }

    @Override
    public String toString() {
        return "resourceName=" + resourceName.getValue() +
                ", " +
                "isFinished=" + isFinished.getValue();
    }

    public StringProperty resourceNameProperty() {
        return resourceName;
    }

    public IntegerProperty isFinishedProperty() {
        return isFinished;
    }

    public StringProperty housemateIDProperty() {
        return housemateID;
    }
}
