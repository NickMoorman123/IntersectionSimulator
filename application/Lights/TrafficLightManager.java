package Lights;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.shape.Circle;

public abstract class TrafficLightManager {
    private static boolean[] trafficLights = {true, true};
    private static int currentTrafficLight = (int) Math.floor(Math.random() * 2.0);
    private static boolean lastChangeWasStop = true;

    private static TrafficLightUI northboundLights;
    private static TrafficLightUI eastboundLights;
    private static TrafficLightUI southboundLights;
    private static TrafficLightUI westboundLights;

    private final static long STOP_DURATION = 3000000000L;
    private final static long GO_DURATION = 30000000000L;

    public static void setInitialLightsRandom(Group root) {
        northboundLights = new TrafficLightUI(new Circle(335, 335, 3), new Circle(335, 345, 3), root);
        eastboundLights = new TrafficLightUI(new Circle(335, 265, 3), new Circle(345, 265, 3), root);
        southboundLights = new TrafficLightUI(new Circle(265, 265, 3), new Circle(265, 255, 3), root);
        westboundLights = new TrafficLightUI(new Circle(265, 335, 3), new Circle(255, 335, 3), root);
    }

    public static boolean maybeChangeLights(long nanoseconds) {
        if (!lastChangeWasStop && nanoseconds > GO_DURATION) {
            trafficLights[currentTrafficLight] = true;
            currentTrafficLight = (currentTrafficLight + 1) % 2;
            lastChangeWasStop = true;
            Platform.runLater(() -> updateUI());
            return true;
        } else if (lastChangeWasStop && nanoseconds > STOP_DURATION) {
            trafficLights[currentTrafficLight] = false;
            lastChangeWasStop = false;
            Platform.runLater(() -> updateUI());
            return true;
        }
        return false;
    }

    private static void updateUI() {
        northboundLights.changeLights(northSouthRed());
        southboundLights.changeLights(northSouthRed());
        eastboundLights.changeLights(eastWestRed());
        westboundLights.changeLights(eastWestRed());
    }

    public static boolean northSouthRed() {
        return trafficLights[0];
    }

    public static boolean eastWestRed() {
        return trafficLights[1];
    }
}
