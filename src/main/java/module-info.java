module za.nmu.wrr {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires jtds;

    opens za.nmu.wrr to javafx.fxml;
    exports za.nmu.wrr;
    exports za.nmu.wrr.controllers;
    opens za.nmu.wrr.controllers to javafx.fxml;
    exports za.nmu.wrr.models;
    opens za.nmu.wrr.models to javafx.fxml;
    exports za.nmu.wrr.database;
    opens za.nmu.wrr.database to javafx.fxml;
}