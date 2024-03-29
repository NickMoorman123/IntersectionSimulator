package Cars;

import Lights.TrafficLightManager;
import UIConstants.UIConstants;
import javafx.scene.shape.Rectangle;

public class WestboundCar extends Car {
    public WestboundCar(WestboundCar carInFront) {
        super(new Rectangle(UIConstants.SCREEN_SIZE, UIConstants.LANE_POSITION_1, CAR_LENGTH, CAR_WIDTH), carInFront);
        position = UIConstants.SCREEN_SIZE;
    }

    @Override
    protected void updatePosition(long nanoseconds) {
        position = position - determinePixelsToMove(nanoseconds);
    }

    @Override
    protected boolean wontCrash() {
        return carInFront.position + 20.0 < position;
    }

    @Override
    protected boolean atRedLight() {
        return TrafficLightManager.eastWestRed() && 325.0 <= position && position <= 335.0;
    }

    @Override
    protected boolean determineOffScreen() {
        return position <= - CAR_LENGTH * 2;
    }

    @Override
    protected void doUIUpdate() {
        setX();
    }
}
