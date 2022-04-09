package application;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.application.Application;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextAlignment;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
    private boolean northSouthRed = true;
    private boolean eastWestRed = true;
    private Car lastNBCar = null;
    private Car lastEBCar = null;
    private Car lastSBCar = null;
    private Car lastWBCar = null;
    private Group root;
    private Random rand = new Random();
    Runnable northSouthGo;
    Runnable eastWestGo;
    Runnable allStop;
	
	@Override
	public void start(Stage stage) {
		try {
			stage.setTitle("Intersection Simulator");
			
			InputStream stream = new FileInputStream("src/intersection.png");
			Image image = new Image(stream);
			ImageView crossing = new ImageView(image);
            crossing.setSmooth(true);
            crossing.setFitHeight(600);
            crossing.setFitWidth(600);
            
            //traffic lights
            Rectangle NBBox = new Rectangle(330, 330, 10, 20);
            NBBox.setFill(Color.color(0.2, 0.2, 0.2));
            Circle NBRed = new Circle(335, 335, 3);
            Circle NBGreen = new Circle(335, 345, 3);
            
            Rectangle EBBox = new Rectangle(330, 260, 20, 10);
            EBBox.setFill(Color.color(0.2, 0.2, 0.2));
            Circle EBRed = new Circle(335, 265, 3);
            Circle EBGreen = new Circle(345, 265, 3);
            
            Rectangle SBBox = new Rectangle(260, 250, 10, 20);
            SBBox.setFill(Color.color(0.2, 0.2, 0.2));
            Circle SBRed = new Circle(265, 265, 3);
            Circle SBGreen = new Circle(265, 255, 3);
            
            Rectangle WBBox = new Rectangle(250, 330, 20, 10);
            WBBox.setFill(Color.color(0.2, 0.2, 0.2));
            Circle WBRed = new Circle(265, 335, 3);
            Circle WBGreen = new Circle(255, 335, 3);
            
            northSouthGo = () -> {
                NBRed.setFill(Color.color(0.7, 0, 0));
                NBGreen.setFill(Color.color(0, 1, 0));
                SBRed.setFill(Color.color(0.7, 0, 0));
                SBGreen.setFill(Color.color(0, 1, 0));
            };
            eastWestGo = () -> {
                EBRed.setFill(Color.color(0.7, 0, 0));
                EBGreen.setFill(Color.color(0, 1, 0));
                WBRed.setFill(Color.color(0.7, 0, 0));
                WBGreen.setFill(Color.color(0, 1, 0));
            };
            allStop = () -> {
                NBRed.setFill(Color.color(1, 0, 0));
                NBGreen.setFill(Color.color(0, 0.5, 0));
                EBRed.setFill(Color.color(1, 0, 0));
                EBGreen.setFill(Color.color(0, 0.5, 0));
                SBRed.setFill(Color.color(1, 0, 0));
                SBGreen.setFill(Color.color(0, 0.5, 0));
                WBRed.setFill(Color.color(1, 0, 0));
                WBGreen.setFill(Color.color(0, 0.5, 0));
            };
            allStop.run();
            
			Label signature = new Label(" Made by Nicholas Moorman\n nicholas.v.moorman@gmail.com");
			signature.setTextAlignment(TextAlignment.CENTER);
            signature.setLayoutX(5);
            signature.setLayoutY(565);
            
			root = new Group(crossing, signature, NBBox, NBRed, NBGreen, EBBox, EBRed, EBGreen, SBBox, SBRed, SBGreen, WBBox, WBRed, WBGreen);
            
			Scene intersection = new Scene(root, 600, 600);
			stage.setScene(intersection);
			stage.setResizable(false);
			stage.show();
			
			if (Math.random() < 0.5) {
                northSouthGo.run();
                new NSLightService().start();
            } else {
                eastWestGo.run();
                new EWLightService().start();
            }
            
            new addCarService().start();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
    
    //the NS and EW light changing services are separate so that we can randomly choose one to be first
    private class NSLightService extends Service<Void> {
        private NSLightService() {
            setOnSucceeded((WorkerStateEvent e) -> {
                allStop.run();
                new RedLightService(false).start();
            });
        }

        @Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    northSouthRed = false;
                    Thread.sleep(30000);
                    northSouthRed = true;
                    return null;
                }
            };
        }
    }
    
    private class EWLightService extends Service<Void> {
        private EWLightService() {
            setOnSucceeded((WorkerStateEvent e) -> {
                allStop.run();
                new RedLightService(true).start();
            });
        }

        @Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    eastWestRed = false;
                    Thread.sleep(30000);
                    eastWestRed = true;
                    return null;
                }
            };
        }
    }
    
    private class RedLightService extends Service<Void> {
        private RedLightService(boolean NSNext) {
            setOnSucceeded((WorkerStateEvent e) -> {
                if (NSNext) {
                    northSouthGo.run();
                    new NSLightService().start();
                } else {
                    eastWestGo.run();
                    new EWLightService().start();
                }
            });
        }
        
        @Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    Thread.sleep(3000);
                    return null;
                }
            };
        }
    }
    
    //adds a car in a random direction after a random delay
    private class addCarService extends Service<Void> {
        private addCarService() {
            setOnSucceeded((WorkerStateEvent e) -> {
                switch (rand.nextInt(4)) {
                    case 0 -> {
                        Car NBCar = new Car("northbound", lastNBCar);
                        root.getChildren().add(NBCar);
                        lastNBCar = NBCar;
                    }
                    case 1 -> {
                        Car EBCar = new Car("eastbound", lastEBCar);
                        root.getChildren().add(EBCar);
                        lastEBCar = EBCar;
                    }
                    case 2 -> {
                        Car SBCar = new Car("southbound", lastSBCar);
                        root.getChildren().add(SBCar);
                        lastSBCar = SBCar;
                    }
                    case 3 -> {
                        Car WBCar = new Car("westbound", lastWBCar);
                        root.getChildren().add(WBCar);
                        lastWBCar = WBCar;
                    }
                }  
                new addCarService().start();
            });
        }
        
        @Override
        public Task<Void> createTask() {
            return new Task<Void>() {
                @Override
                protected Void call() throws InterruptedException {
                    //Generates inter-arrival times to form a Poisson Process with a rate of 45 cars/minute
                    Thread.sleep((long) (Math.log(Math.random()) * (- 60000.0d / 45.0d)));
                    return null;
                }
            };
        }
    };
    
    private class Car extends Rectangle {
        private final Double speed = 50.0d + (Math.random() * 10.0d - 5.0d);
        private Consumer<Long> drive;
        private BooleanSupplier canGo;
        private Predicate<Integer> offScreen;
        private Runnable leftScreen = () -> root.getChildren().remove(this);
        private Car inFront;
        
        public Car(String direction, Car nextCar) {
            inFront = nextCar;
            switch (direction) {
                case "northbound" -> {
                    setX(305);
                    setY(600);
                    setHeight(15);
                    setWidth(11);
                    drive =     time -> {if (time > 0) setY(getY() - speed * (Double) ((System.currentTimeMillis() - time) / 1000.0D));};
                    canGo =     () -> (inFront == null ? true : inFront.getY() + 20.0 < getY())
                                    && !(northSouthRed && 325 <= getY() && getY() <= 335);
                    offScreen = amount -> getY() <= -amount;
                }
                case "eastbound" -> {
                    setX(-15);
                    setY(305);
                    setHeight(11);
                    setWidth(15);
                    drive =     time -> {if (time > 0) setX(getX() + speed * (Double) ((System.currentTimeMillis() - time) / 1000.0D));};
                    canGo =     () -> (inFront == null ? true : inFront.getX() - 20.0 > getX())
                                    && !(250 <= getX() && eastWestRed && getX() <= 260);
                    offScreen = amount -> getX() >= 585 + amount;
                }
                case "southbound" -> {
                    setX(285);
                    setY(-15);
                    setHeight(15);
                    setWidth(11);
                    drive =     time -> {if (time > 0) setY(getY() + speed * (Double) ((System.currentTimeMillis() - time) / 1000.0D));};
                    canGo =     () -> (inFront == null ? true : inFront.getY() - 20.0 > getY())
                                    && !(northSouthRed && 252 <= getY() && getY() <= 262);
                    offScreen = amount -> getY() >= 585 + amount;
                }
                case "westbound" -> {
                    setX(600);
                    setY(285);
                    setHeight(11);
                    setWidth(15);
                    drive =     time -> {if (time > 0) setX(getX() - speed * (Double) ((System.currentTimeMillis() - time) / 1000.0D));};
                    canGo =     () -> (inFront == null ? true : inFront.getX() + 20.0 < getX())
                                    && !(eastWestRed && 325 <= getX() && getX() <= 335);
                    offScreen = amount -> getX() <= -amount;
                }
            }
            
            setFill(Color.color(Math.random() * 0.5 + 0.25, Math.random() * 0.5 + 0.25, Math.random() * 0.5 + 0.25));
            new DriveService().start();
        }
        
        //as long as we are on the screen, move if we can
        //when we get off screen, nullify the reference to the car in front
        //keep moving until there is room for the car behind to get off screen too, then remove itself
        private class DriveService extends Service<Long> {
            private DriveService() {
                setOnSucceeded((WorkerStateEvent e) -> {
                    try {
                        drive.accept((Long) e.getSource().getValue());
                        new DriveService().start();
                    } catch (Exception ex) {
                        leftScreen.run();
                    }
                });
            }
            
            @Override
            public Task<Long> createTask() {
                return new Task<Long>() {
                    @Override
                    protected Long call() throws InterruptedException {
                        Long time = System.currentTimeMillis();
                        if (!offScreen.test(15)) {
                            if (canGo.getAsBoolean()) 
                                return time;
                            else
                                return 0L;
                        } else {
                            inFront = null;
                            if (!offScreen.test(50))
                                return time;
                            else
                                return null;
                        }
                    }
                };
            }
        }
    }
	
	public static void main(String[] args) {
		launch(args);
	}
}
