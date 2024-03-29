package Cars;

import Lights.TrafficLightManager;
import UIConstants.UIConstants;
import javafx.scene.shape.Rectangle;

public class NorthboundCar extends Car {
    public NorthboundCar(NorthboundCar carInFront) {
        super(new Rectangle(UIConstants.LANE_POSITION_2, UIConstants.SCREEN_SIZE, CAR_WIDTH, CAR_LENGTH), carInFront);
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
        return TrafficLightManager.northSouthRed() && 325.0 <= position && position <= 335.0;
    }

    @Override
    protected boolean determineOffScreen() {
        return position <= - CAR_LENGTH * 2;
    } 

    @Override
    protected void doUIUpdate() {
        setY();
    }  
}
