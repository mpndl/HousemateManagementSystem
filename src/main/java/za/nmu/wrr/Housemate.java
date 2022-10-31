package za.nmu.wrr;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Housemate {
    public StringProperty housemateID = new SimpleStringProperty();
    public StringProperty username = new SimpleStringProperty();
    public StringProperty firstName = new SimpleStringProperty();
    public StringProperty lastName = new SimpleStringProperty();
    public StringProperty password = new SimpleStringProperty();
    public StringProperty phoneNumber = new SimpleStringProperty();
    public IntegerProperty isLeader = new SimpleIntegerProperty();
    public Housemate() {}
    public Housemate(String housemateID, String username, String firstName, String lastName, String password, String phoneNumber, Integer isLeader) {
        this.housemateID.setValue(housemateID);
        this.username.setValue(username);
        this.firstName.setValue(firstName);
        this.lastName.setValue(lastName);
        this.password.setValue(password);
        this.phoneNumber.setValue(phoneNumber);
        this.isLeader.setValue(isLeader);
    }

    public StringProperty housemateIDProperty() {
        return housemateID;
    }
    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public StringProperty passwordProperty() {
        return new SimpleStringProperty("*****");
    }

    public StringProperty phoneNumberProperty() {
        return phoneNumber;
    }

    public IntegerProperty isLeaderProperty() {
        return isLeader;
    }
}
