package za.nmu.wrr;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Housemate {
    public StringProperty housemateID = new SimpleStringProperty();
    public StringProperty firstName = new SimpleStringProperty();
    public StringProperty lastName = new SimpleStringProperty();
    public StringProperty password = new SimpleStringProperty();
    public StringProperty phoneNumber = new SimpleStringProperty();
    public IntegerProperty isLeader = new SimpleIntegerProperty();

    public StringProperty housemateIDProperty() {
        return housemateID;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public IntegerProperty isLeaderProperty() {
        return isLeader;
    }
}
