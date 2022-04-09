## Intersection Simulator

This project was created entirely for the purpose of getting to play with threads. Very inefficiently, each of the calculations to "drive" each of the "cars" operates on its own thread. There are also threads to control the changing of traffic lights and adding of cars. The `Service` and `Task` classes are utilized to ensure the JavaFX UI is updated in a thread-safe way.

Which set of traffic lights start out green is chosen randomly, and the lights cycle for 30 seconds each with 3 seconds in between of both directions being red.

Cars are added to a random choice of the northbound, eastbound, southbound, and westbound directions. A reference to the last car added to each is kept. The delay between cars appearing on the screen is randomly chosen according to a Poisson Process with a rate of 45 cars per minute to add a bit of realism. (This means that each of the four lanes follows a Poisson Process with a rate of 11.25 cars per minute.) In a Poisson Process, we know the average number of occurrences per time interval (or equivalently we know the average inter-arrival time) but the actual number can vary widely. In addition, we know that arrivals are independent of one another and can't happen at exactly the same time. [More info](https://towardsdatascience.com/the-poisson-distribution-and-poisson-process-explained-4e2cb17d459#:~:text=A%20Poisson%20Process%20is%20a,time%20between%20events%20is%20memoryless)

Each direction of cars operates as a linked list, because each car must know the location of the next in order to not crash into it. The methods for driving, checking whether it can drive, etc. are stored as `Consumer`, `BooleanSupplier`, etc. and instantiated based on the direction that the car is initilized with, so that we do not have to continue to check which direction we are supposed to go. (I might have set up the constructor to take these as direct inputs rather than switching on an intermediary String identifier, but they needed to use the `setX()`, `getX()` etc. methods of `Rectangle` and reference the car in front as well.) Once a car leaves the screen, it forgets the car in front of it in order to no longer consider its location, and then once it has made it far enough off screen that the car behind it can also come off screen, the drive process ends and it removes itself from the root Group in order to not clog up the UI. The car's color is also randomly chosen.

JavaFX is required to build: https://openjfx.io/
