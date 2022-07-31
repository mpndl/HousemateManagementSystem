module za.nmu.wrr {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.sql;
    requires jtds;

    opens za.nmu.wrr to javafx.fxml;
    exports za.nmu.wrr;
}