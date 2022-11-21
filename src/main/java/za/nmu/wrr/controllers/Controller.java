package za.nmu.wrr.controllers;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import za.nmu.wrr.Run;
import za.nmu.wrr.database.Database;
import za.nmu.wrr.models.Housemate;
import za.nmu.wrr.circle.CircleView;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {
    @FXML
    public Pane pane;
    public VBox dashboard_links;
    public ArrayList<CircleView> circleViews = new ArrayList<>();
    public static CircleView selectedCircleView;
    public static Map<String, Run> links = new HashMap<>();
    public static List<Runnable> linksRunnable = new ArrayList<>();

    public final Database database = new Database();
    protected static Housemate loggedInUser;
    public Controller() {}

    public Controller(Stage vhStage, Stage dashboardStage) {
        dashboardStage.setOnShowing(event -> new Thread(() -> {
            try {
                Thread.sleep(500);
                linksRunnable.forEach(Runnable::run);
                Platform.runLater(() -> initializeLinkCircleViews((Pane) dashboardStage.getScene().lookup("#pane")));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start());
        vhStage.showAndWait();
    }

    protected EventHandler<KeyEvent> maxLength(final Integer i) {
        return arg0 -> {
            TextField tx = (TextField) arg0.getSource();
            if (tx.getText().length() >= i) {
                arg0.consume();
            }
        };
    }

    public void setOnMouseReleased(MouseEvent event) {
        for (CircleView circleView : circleViews) {
            if(circleView.isSelectedProperty.getValue()) circleView.deselect();
        }
    }

    public void setOnMouseClicked(MouseEvent event) {
        selectedCircleView = getCircleViewUnderCursor(event.getX(), event.getY());
    }

    public void initializeLinkCircleViews(Pane pane) {
        int linkCount = links.size();

        Set<Map.Entry<String, Run>> entrySet = links.entrySet();
        Map.Entry<String, Run> entry = nextEntry(entrySet);
        CircleView circleView = new CircleView(0, 0, pane, circleViews, entry.getKey());
        double curX = circleView.radius;
        double curY = circleView.radius;

        circleView.onClick = entry.getValue();
        entry.getValue().run(circleView);

        circleView.setCenterX(curX);
        circleView.setCenterY(curY);

        AtomicReference<Double> maxRadius = new AtomicReference<>(circleView.radius);
        Point2D point = computeBoundedPoint(pane, curX, curY, circleView.radius, maxRadius);

        curX = point.getX() + circleView.radius * 2 + 10;
        curY = point.getY();

        for (int i = 0; i < linkCount - 1; i++) {
            entry = nextEntry(entrySet);
            if (entry == null) new CircleView(curX, curY, pane, circleViews, "?");
            else {
                circleView = new CircleView(curX, curY, pane, circleViews, entry.getKey());
                circleView.onClick = entry.getValue();
                entry.getValue().run(circleView);
            }
            point = computeBoundedPoint(pane, curX, curY, circleView.radius, maxRadius);
            curX = point.getX() + circleView.radius * 2 + 10;
            curY = point.getY();
        }
    }

    private Map.Entry<String, Run> nextEntry(Set<Map.Entry<String, Run>> entrySet) {
        AtomicReference<Map.Entry<String, Run>> entry = new AtomicReference<>();
        entrySet.forEach(entry::set);
        entrySet.remove(entry.get());
        return entry.get();
    }

    private Point2D computeBoundedPoint(Pane pane, double curX, double curY, double radius, AtomicReference<Double> maxRadius) {
        double paneWidth = pane.getWidth();
        double circleDiameter = maxRadius.get() * 2;
        if (curX <= paneWidth - circleDiameter) {
            if (radius > maxRadius.get()) maxRadius.set(radius);
            return new Point2D(curX, curY);
        }
        else {
            maxRadius.set(radius);
            return new Point2D(radius, curY + circleDiameter);
        }
    }

    private CircleView getCircleViewUnderCursor(double x1, double y1) {
        for (CircleView circleView : circleViews) {
            double x2 = 0;
            double y2 = 0;
            double radius = circleView.getRadius();
            if(distance(x1, y1, x2, y2) <= radius) return circleView;
        }
        return null;
    }

    private boolean touchesOnCircles(double x1, double y1) {
        for (CircleView circleView : circleViews) {
            double x2 = circleView.getCenterX();
            double y2 = circleView.getCenterY();
            double radius = circleView.getRadius();
            if(distance(x1, y1, x2, y2) <= radius)
                return true;
        }
        return false;
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1,2) + Math.pow(y2 - y1, 2));
    }

    protected void disavail(Hyperlink hp) {
        hp.setStyle("-fx-background-color: gainsboro; -fx-text-fill: black;-fx-border-color: black; -fx-underline: false");
        hp.setPrefWidth(Double.MAX_VALUE);
        Tooltip tooltip = new Tooltip("Unavailable");
        tooltip.setShowDelay(Duration.ZERO);
        hp.setTooltip(tooltip);
    }

    protected void avail(Hyperlink hp) {
        hp.setStyle("-fx-border-color: black");
        hp.setPrefWidth(Double.MAX_VALUE);
    }
}
