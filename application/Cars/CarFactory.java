package Cars;

import javafx.application.Platform;
import javafx.scene.Group;

public abstract class CarFactory {
    private static NorthboundCar previousNorthboundCar;
    private static EastboundCar previousEastboundCar;
    private static SouthboundCar previousSouthboundCar;
    private static WestboundCar previousWestboundCar;

    public static Car getRandomCar(Group root) {
        Car car = switch (getRandIntZeroToThree()) {
            case 0  -> getNorthboundCar();
            case 1  -> getEastboundCar();
            case 2  -> getSouthboundCar();
            default -> getWestboundCar();
        };
        Platform.runLater(() -> car.addRectangleToUI(root));
        return car;
    }

    private static int getRandIntZeroToThree() {
        return (int) Math.floor(Math.random() * 4.0d);
    }

    private static Car getNorthboundCar() {
        NorthboundCar northboundCar = new NorthboundCar(previousNorthboundCar);
        previousNorthboundCar = northboundCar;
        return northboundCar;
    }

    private static Car getEastboundCar() {
        EastboundCar eastboundCar = new EastboundCar(previousEastboundCar);
        previousEastboundCar = eastboundCar;
        return eastboundCar;
    }

    private static Car getSouthboundCar() {
        SouthboundCar southboundCar = new SouthboundCar(previousSouthboundCar);
        previousSouthboundCar = southboundCar;
        return southboundCar;
    }

    private static Car getWestboundCar() {
        WestboundCar westboundCar = new WestboundCar(previousWestboundCar);
        previousWestboundCar = westboundCar;
        return westboundCar;
    }
}
