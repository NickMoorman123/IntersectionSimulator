import java.io.FileInputStream;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import Cars.Car;
import Cars.CarFactory;
import Lights.TrafficLightManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
    private Group root;
    private ConcurrentLinkedDeque<Car> carList = new ConcurrentLinkedDeque<Car>();
    public static double SCREEN_SIZE = 600;
    
    private long lastCarAdd = 0;
    private long lastLightChange = 0;
    private long carDelay = 0;
	
	@Override
	public void start(Stage stage) {
		try {
			stage.setTitle("Intersection Simulator");
			
			ImageView crossing = new ImageView(new Image(new FileInputStream("intersection.png")));
            crossing.setSmooth(true);
            crossing.setFitHeight(600);
            crossing.setFitWidth(600);
            
			Label signature = new Label("Made by Nicholas Moorman\nnicholas.v.moorman@gmail.com");
			signature.setTextAlignment(TextAlignment.CENTER);
            signature.setLayoutX(5);
            signature.setLayoutY(560);
            
            Rectangle rectangleNorthbound = new Rectangle(330, 330, 10, 20);
            rectangleNorthbound.setFill(Color.color(0.2, 0.2, 0.2));
            Rectangle rectangleEastbound = new Rectangle(330, 260, 20, 10);
            rectangleEastbound.setFill(Color.color(0.2, 0.2, 0.2));
            Rectangle rectangleSouthbound = new Rectangle(260, 250, 10, 20);
            rectangleSouthbound.setFill(Color.color(0.2, 0.2, 0.2));
            Rectangle rectangleWestbound = new Rectangle(250, 330, 20, 10);
            rectangleWestbound.setFill(Color.color(0.2, 0.2, 0.2));

            root = new Group(crossing, signature, rectangleNorthbound, rectangleEastbound, rectangleSouthbound, rectangleWestbound);

            TrafficLightManager.setInitialLightsRandom(root);
            
			Scene intersection = new Scene(root, 600, 600);
			stage.setScene(intersection);
			stage.setResizable(false);
			stage.show();

            animationTimer.start();

            ScheduledExecutorService trafficScheduler = Executors.newSingleThreadScheduledExecutor();
            ExecutorService trafficExecutor = Executors.newVirtualThreadPerTaskExecutor();
            trafficScheduler.scheduleAtFixedRate(() -> trafficExecutor.submit(() -> calculateTraffic()), 0, 15, TimeUnit.MILLISECONDS);

            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent arg0) {
                    trafficScheduler.close();
                }
            });
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

    private void calculateTraffic() {
        long time = System.nanoTime();
        if (TrafficLightManager.maybeChangeLights(time - lastLightChange)) {
            lastLightChange = time;
        }
        
        for (Car car : carList) {
            car.calculatePosition(time - lastCarAdd);
        }
        
        if (time > lastCarAdd + carDelay) {
            carList.addFirst(CarFactory.getRandomCar(root));
            carDelay = getNewCarDelay();
            lastCarAdd = time;
        }
    }

    AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long time) {
            trafficUIUpdate();
        }
    };

    private void trafficUIUpdate() {
        for (Car car : carList) {
            car.updateUI();
        }
    
        if (!carList.isEmpty() && carList.getLast().offScreen()) {
            carList.getLast().removeRectangleFromUI(root);
            carList.removeLast();
        }
    }

    private long getNewCarDelay() {
        //Generates inter-arrival times in nanoseconds to form a Poisson Process with a rate of 45 cars/minute
        return (long) (Math.log(Math.random()) * (-60000000000.0d / 45.0d));
    }

	public static void main(String[] args) {
		launch(args);
	}
}
