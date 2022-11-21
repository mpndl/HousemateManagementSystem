package za.nmu.wrr.circle;

import javafx.beans.property.*;

public class Circle {
    public StringProperty labelProperty = new SimpleStringProperty();
    public DoubleProperty xProperty =  new SimpleDoubleProperty();
    public DoubleProperty yProperty = new SimpleDoubleProperty();
    public BooleanProperty isSelectedProperty = new SimpleBooleanProperty();

    public Circle(double x, double y) {
        xProperty.setValue(x);
        yProperty.setValue(y);
    }
    public Circle(String label) {
        this.labelProperty.setValue(label);
    }

    @Override
    public String toString() {
        return "Circle{" +
                "labelProperty=" + labelProperty.getValue() +
                ", xProperty=" + xProperty.getValue() +
                ", yProperty=" + yProperty.getValue() +
                ", isSelectedProperty=" + isSelectedProperty.getValue() +
                '}';
    }
}
