package Lights;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class TrafficLightUI {
    public final Circle red;
    public final Circle green;

    public TrafficLightUI(Circle rCircle, Circle gCircle, Group root) {
        red = rCircle;
        green = gCircle;
        setRedAndGreenToStop();
        root.getChildren().addAll(red, green);
    }

    public void changeLights(boolean stop) {
        if (stop) {
            setRedAndGreenToStop();
        } else {
            setRedAndGreenToGo();
        }
    }

    private void setRedAndGreenToStop() {
        red.setFill(Color.color(1, 0, 0));
        green.setFill(Color.color(0, 0.5, 0));
    }

    private void setRedAndGreenToGo() {
        red.setFill(Color.color(0.7, 0, 0));
        green.setFill(Color.color(0, 1, 0));
    }
}
