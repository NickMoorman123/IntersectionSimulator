package Cars;

import Lights.TrafficLightManager;
import UIConstants.UIConstants;
import javafx.scene.shape.Rectangle;

public class SouthboundCar extends Car {
    public SouthboundCar(SouthboundCar inFront) {
        super(new Rectangle(UIConstants.LANE_POSITION_1, -CAR_LENGTH, CAR_WIDTH, CAR_LENGTH), inFront);
        position = -CAR_LENGTH;
    }

    @Override
    protected void updatePosition(long nanoseconds) {
        position = position + determinePixelsToMove(nanoseconds);
    }

    @Override
    protected boolean wontCrash() {
        return carInFront.position - 20.0 > position;
    }

    @Override
    protected boolean atRedLight() {
        return TrafficLightManager.northSouthRed() && 252.0 <= position && position <= 262.0;
    }

    @Override
    protected boolean determineOffScreen() {
        return position >= UIConstants.SCREEN_SIZE + CAR_LENGTH;
    }

    @Override
    protected void doUIUpdate() {
        setY();
    }
}
