package za.nmu.wrr.circle;

import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import za.nmu.wrr.Main;
import za.nmu.wrr.Run;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

public class CircleView extends javafx.scene.shape.Circle {
    private final Text label;
    public BooleanProperty isSelectedProperty = new SimpleBooleanProperty();
    private Cursor oldCursor;
    public Circle circle;
    private double anchorX, anchorY;
    private final DropShadow shortShadow;
    private final DropShadow longShadow;
    private final ArrayList<CircleView> circleViews;
    private final Property<Paint> colorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private final Property<CircleView> selectedNodeViewProperty = new SimpleObjectProperty<>();
    public Run onClick;
    public boolean disabled = false;
    private Point2D positionBeforeDrag = null;
    public final double radius;

    public CircleView(double x, double y, Pane pane, ArrayList<CircleView> circleViews, String s) {
        super(x, y, 0);

        this.circleViews = circleViews;

        circle = new Circle(s);
        circle.xProperty.setValue(x);
        circle.yProperty.setValue(y);

        DoubleProperty xProperty = new SimpleDoubleProperty();
        xProperty.bindBidirectional(circle.xProperty);
        DoubleProperty yProperty = new SimpleDoubleProperty();
        yProperty.bindBidirectional(circle.yProperty);

        isSelectedProperty.bindBidirectional(circle.isSelectedProperty);

        this.label = new Text();
        this.label.setBoundsType(TextBoundsType.VISUAL);

        this.label.textProperty().bindBidirectional(circle.labelProperty);
        this.radius = getWidth(this.label) / 2;

        setRadius(radius);

        this.label.layoutXProperty().bind(xProperty.subtract(this.radius));
        this.label.layoutYProperty().bind(yProperty);

        centerXProperty().bindBidirectional(xProperty);
        centerYProperty().bindBidirectional(yProperty);

        pane.getChildren().addAll(this.label, this);
        circleViews.add(this);
        label.toFront();

        setStroke(Color.WHITE);
        fillProperty().bindBidirectional(colorProperty);

        shortShadow = new DropShadow();
        shortShadow.setColor(new Color(0, 0,0, .5));
        shortShadow.setRadius(5);
        shortShadow.setOffsetX(5);
        shortShadow.setOffsetY(5);

        setEffect(shortShadow);

        longShadow = new DropShadow();
        longShadow.setColor(new Color(0, 0,0, .5));
        longShadow.setRadius(5);
        longShadow.setOffsetX(10);
        longShadow.setOffsetY(10);

        attachEventHandlers();
    }

    private double getWidth(Text text) {
        new Scene(new Group(text));
        text.applyCss();

        return text.getLayoutBounds().getWidth();
    }

    public boolean dragging() {
        return positionBeforeDrag == null || getCenterX() != positionBeforeDrag.getX()
                || getCenterY() != positionBeforeDrag.getY();
    }

    public void attachEventHandlers() {
        setOnMouseEntered(event -> {
            oldCursor = getCursor();
            setCursor(Cursor.HAND);
        });

        setOnMousePressed(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                positionBeforeDrag = new Point2D(circle.xProperty.getValue(), circle.yProperty.getValue());
                setEffect(longShadow);
                setCursor(Cursor.CLOSED_HAND);
                anchorX = event.getSceneX() - getCenterX();
                anchorY = event.getSceneY() - getCenterY();
                toFront();
                label.toFront();
            }
        });

        setOnMouseDragged(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                select();
                setCenterX(event.getSceneX() - anchorX);
                setCenterY(event.getSceneY() - anchorY);
;            }
        });

        setOnMouseReleased(event -> {
            if(event.getButton() == MouseButton.PRIMARY) {
                setEffect(shortShadow);
                setCursor(Cursor.OPEN_HAND);
            }
        });

        setOnMouseExited(event -> {
            setCursor(oldCursor);
        });

        setOnMouseClicked(event -> {
            select();
            deselectAll();
            colorProperty.setValue(Color.GAINSBORO);
            setStroke(Color.GAINSBORO);
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                if (onClick != null) onClick.run(this);
            }
        });
    }

    private void deselectAll() {
        for (CircleView circleView : circleViews)
            if(circleView != this)
                if(circleView.isSelectedProperty.getValue() && !circleView.disabled) {
                    circleView.deselect();
                    selectedNodeViewProperty.setValue(null);
                }
    }

    public void disable() {
        disabled = true;
        setOnMouseEntered(event -> {
            try {
                setCursor(new ImageCursor(new Image(Objects.requireNonNull(Main.class.getResource("images/disabled.png")).toURI().toString())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        colorProperty.setValue(Color.DARKGRAY);
        setEffect(null);
    }

    public void deselect() {
        isSelectedProperty.setValue(false);
        colorProperty.setValue(Color.WHITE);
    }

    public void select() {
        isSelectedProperty.setValue(true);
        colorProperty.setValue(Color.GAINSBORO);
        setStroke(Color.GAINSBORO);
        selectedNodeViewProperty.setValue(this);
    }
}
