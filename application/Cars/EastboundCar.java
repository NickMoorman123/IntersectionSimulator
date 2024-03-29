package Cars;

import Lights.TrafficLightManager;
import UIConstants.UIConstants;
import javafx.scene.shape.Rectangle;

public class EastboundCar extends Car {
    public EastboundCar(EastboundCar carInFront) {
        super(new Rectangle(-CAR_LENGTH, UIConstants.LANE_POSITION_2, CAR_LENGTH, CAR_WIDTH), carInFront);
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
        return TrafficLightManager.eastWestRed() && 250.0 <= position && position <= 260.0;
    }

    @Override
    protected boolean determineOffScreen() {
        return position >= UIConstants.SCREEN_SIZE + CAR_LENGTH;
    }

    @Override
    protected void doUIUpdate() {
        setX();
    }
}
