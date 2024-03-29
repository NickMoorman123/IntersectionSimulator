package Cars;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class Car {
    private Rectangle carRectangle;
    protected Car carInFront;
    protected final double speed;
    protected double position;

    protected static final double CAR_LENGTH = 15.0d;
    protected static final double CAR_WIDTH = 11.0d;
    
    protected Car(Rectangle rectangle, Car inFront) {
        carRectangle = rectangle;
        carInFront = inFront;
        speed = 3.0d + (Math.random() - 0.2d);
    }

    protected void addRectangleToUI(Group root) {
        carRectangle.setFill(Color.color(Math.random() * 0.5 + 0.25, Math.random() * 0.5 + 0.25, Math.random() * 0.5 + 0.25));
        root.getChildren().add(carRectangle);
    }

    public synchronized void calculatePosition(long time) {
        if (!canGo()) {
            return;
        }
        updatePosition(time);
    }

    protected abstract void updatePosition(long time);

    protected double determinePixelsToMove(double nanoseconds) {
        return speed * (nanoseconds / 1000000000.0);
    }

    protected boolean canGo() {
        if (noCarInFront()) {
            return !atRedLight();
        }
        return wontCrash() && !atRedLight();
    }

    protected abstract boolean wontCrash();

    protected abstract boolean atRedLight();

    protected boolean noCarInFront() {
        if (carInFront == null) {
            return true;
        }
        if (carInFront.determineOffScreen()) {
            carInFront = null;
            return true;
        }
        return false;
    }

    protected abstract boolean determineOffScreen();

    public synchronized void updateUI() {
        doUIUpdate();
    }

    protected abstract void doUIUpdate();

    protected void setX() {
        carRectangle.setX(position);
    }

    protected void setY() {
        carRectangle.setY(position);
    }

    public synchronized boolean offScreen() {
        return determineOffScreen();
    }

    public synchronized void removeRectangleFromUI(Group root) {
        root.getChildren().remove(carRectangle);
    }
}