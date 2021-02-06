package project;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Elevator subsystem
 * @author Haoyu Xu
 *
 */
public class Elevator implements Runnable {
    // The number of ground floors
    private static final int totalGroundFloors = 7; 
    // The number of underground floors
    private static final int totalUndergroundFloors = 0; 
    // The height of each floor in meters
    private static final double floorHeight = 3.57; 
    // the delay between retries
    private static final long delay = 250; 
    
    private Sender sender;
    private Parser parser = new Parser();
    private Door door = new Door();
    private Motor motor = new Motor();
    // buttons of each floor in the car
    private ArrayList<ElevatorButton> buttons = new ArrayList<ElevatorButton>();
    // each lamp under the button
    private ArrayList<ElevatorLamp> buttonLamps = new ArrayList<ElevatorLamp>();
    // floor indicators
    private ArrayList<ElevatorLamp> floorLamps = new ArrayList<ElevatorLamp>();
    // arrival sensors in the shaft
    private ArrayList<ArrivalSensor> arrivalSensors = new ArrayList<ArrivalSensor>();
    // direction indicators
    private DirectionLamp upLamp = new DirectionLamp();
    private DirectionLamp downLamp = new DirectionLamp();
    private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
    // id of the elevator
    private int identifier;
    // the floor the elevator initially stays
    private int currentFloor;
    private ArrayList<Integer> nextFloors = new ArrayList<Integer>();
    
    public Elevator(int identifier, int currentFloor, Database database) {
        this.identifier = identifier;
        this.currentFloor = currentFloor;
        this.sender = new Sender(database);
        for(int i = this.totalUndergroundFloors; i <= this.totalGroundFloors; i++) {
            if (i != 0) {
                buttons.add(new ElevatorButton(i));
                buttonLamps.add(new ElevatorLamp(i));
                floorLamps.add(new ElevatorLamp(i));
                arrivalSensors.add(new ArrivalSensor(i));
            }
        }
        floorLamps.get(buttonIndex(this.currentFloor)).on();
    }
    
    /**
     * Move to destination floor
     * @param toFloor the destination floor
     */
    private void move(int toFloor) {
        System.out.println(Thread.currentThread().getName() + ": Moving to " + toFloor + " floor.");
        int difference = toFloor - currentFloor;
        double speed = 0;
        DirectionLamp directionLamp = null;
        if (difference > 0) {
            // going up
            directionLamp = this.upLamp;
            directionLamp.on();
            motor.up();
            for (int i = this.currentFloor + 1; i <= toFloor; i++) {
                speed = arrivalSensors.get(buttonIndex(i)).check(speed, motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), toFloor);
                floorLamps.get(buttonIndex(i - 1)).off();
                floorLamps.get(buttonIndex(i)).on();
                this.currentFloor++;
                send(1, 0, true);
            }
        } else if (difference < 0) {
            // going down
            directionLamp = this.downLamp;
            directionLamp.on();
            motor.down();
            for (int i = this.currentFloor - 1; i >= toFloor; i--) {
                speed = arrivalSensors.get(buttonIndex(i)).check(speed, motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), toFloor);
                floorLamps.get(buttonIndex(i + 1)).off();
                floorLamps.get(buttonIndex(i)).on();
                this.currentFloor--;
                send(0, 0, true);
            }
        }
        motor.stop();
        directionLamp.off();
        System.out.println(Thread.currentThread().getName() + ": arrived at " + toFloor + " floor.");
        send(-1, 0, false);
        door.toggle();
    }
    
    /**
     * report current status to the scheduler
     */
    private void status() {
        send(-1, 0, false);
    }
    
    /**
     * A button is pressed
     * Not used in iteration 1
     * @param button the button pressed in the car
     */
    private void press(int button) {
        if (this.totalUndergroundFloors < button && button != 0 && button <= totalGroundFloors && button != this.currentFloor) {
            send(-1 ,button, false);
            if (button > 0) {
                buttons.get(buttonIndex(button)).press();
                buttonLamps.get(buttonIndex(button)).on();
            } else {
                buttons.get(buttonIndex(button)).press();
            }
        } else {
            
        }
    }
    
    /**
     * Calculate the index of the button in this.buttons
     * eg. the building has 7 floors (1F - 7F), then first floor -> index 0
     * eg. the building has underground floors (-2F - 7F), then -2F -> index 0,
     * 1F -> index 2
     * @param button the actual number of the button
     * @return the index of the button
     */
    private int buttonIndex(int button) {
        int index = 0;
        if (button > 0) {
            index = - this.totalUndergroundFloors + button - 1;
        } else {
            index = button - this.totalUndergroundFloors;
        }
        return index;
    }
    
    /**
     * Send the message to the Scheduler
     * @param button the button pressed in the car
     * @param state the status of the elevator
     * @param noRetry when the message does not reach the host, retry or not
     */
    private void send(int direction, int button, Boolean noRetry) {
        Boolean isSent = false;
        while(!isSent) {
            isSent = sender.send("elevator", this.identifier, this.currentFloor, direction, button, getTime());
            if (!isSent && !noRetry) {
                try {
                    Thread.sleep(this.delay);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * Get message from the Scheduler
     */
    private void get() {
        if (this.messages.size() != 0) {
            this.parser.parse(messages.get(0));
            int nextFloor = parser.getFloor();
            messages.remove(0);
            if (nextFloor == 0) {
                status();
            } else if (nextFloor == this.currentFloor) {
                door.toggle();
            } else {
                move(nextFloor);
            }
        }
    }
    
    /**
     * For iteration 1, Scheduler sends the message to here
     * 
     * @param inputMessage the message
     */
    public void put(byte[] inputMessage) {
        this.messages.add(inputMessage);
    }
    
    /**
     * Get current time in epoch seconds
     * @return as described above
     */
    private long getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
    
    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while(true) {
            get();
        }
    }
    
    private class Door {
        // The time the doors take to open and close in seconds
        private static final double doorTime = 9.3; 
        
        public Door() {
            
        }
        
        /**
         * To simulate the action of opening and closing the door
         */
        private void toggle() {
            System.out.println(Thread.currentThread().getName() + ": Door is opened.");
            try {
                Thread.sleep((long) (1000 * this.doorTime));
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName() + ": Door is closed.");
        }
    }
    
    private class Motor {
        // The maximum speed of the elevator in m/s, 1.3 * 1.1
        private static final double maxSpeed = 1.43; 
        // The acceleration of the elevator in m/s^2
        // one floor uses different acceleration, like 0.1
        private static final double acceleration = 0.6;
        // The time the elevator takes to acceleration to maxSpeed and deceleration from maxSpeed in seconds
        // 1.43 / 0.6 = 2.383
        private final double accelerationTime = maxSpeed / acceleration;
        // The displacement the elevator takes to accelerate to maxSpeed and deceleration from maxSpeed
        // 0.5 * 0.6 * 2.383 ^ 2 = 1.7
        private final double accelerationDisplacement = 0.5 * acceleration * Math.pow(accelerationTime, 2);
        // 0 for stationary, 1 for up, -1 for down
        private int direction = 0;
        
        public Motor() {
            
        }
        
        /**
         * To simulate the movement of moving between floors
         * Not used. For correctness checking
         * @param displacement the distance the elevator should move
         */
        private void move(double displacement) {
            double movementTime;
            if (displacement < this.accelerationDisplacement * 2) {
                movementTime = Math.sqrt(2 * displacement / this.acceleration);
            } else {
                movementTime = (displacement - this.accelerationDisplacement * 2) / this.maxSpeed + this.accelerationTime * 2;
            }
            System.out.println(Thread.currentThread().getName() + ": " + displacement + "m; " + movementTime + " seconds.");
        }
        
        /**
         * To simulate the up movement of moving between floors
         */
        public void up() {
            this.direction = 1;
            start();
        }
        
        /**
         * To simulate the down movement of moving between floors
         */
        public void down() {
            this.direction = -1;
            start();
        }
        
        /**
         * To simulate the stop
         */
        public void stop() {
            this.direction = 0;
        }
        
        /**
         * start the motor
         */
        private void start() {
            
        }
        
        /**
         * get the maximum speed of the motor
         * @return as described above
         */
        public double getMaxSpeed() {
            return this.maxSpeed;
        }
        
        /**
         * get the displacement the elevator takes to accelerate to maxSpeed 
         * and deceleration from maxSpeed
         * @return as described above
         */
        public double getAccelerationDisplacement() {
            return this.accelerationDisplacement;
        }
        
        /**
         * get the time the elevator takes to acceleration to maxSpeed and 
         * deceleration from maxSpeed in seconds
         * @return as described above
         */
        public double getAccelerationTime() {
            return this.accelerationTime;
        }
    }
    
    private class ElevatorButton {
        // floor number
        private int number;
        
        public ElevatorButton(int number) {
            this.number = number;
        }
        
        /**
         * The button is pressed
         */
        public void press() {
            
        }
    }
    
    private class ElevatorLamp {
        // floor number
        private int number;
        // light status
        private Boolean isOn = false;
        
        public ElevatorLamp(int number) {
            this.number = number;
        }
        
        /**
         * Turn on the light
         */
        public void on() {
            this.isOn = true;
            System.out.println(Thread.currentThread().getName() + ": The light of " + this.number + " floor is on.");
        }
        
        /**
         * Turn off the light
         */
        public void off() {
            this.isOn = false;
            System.out.println(Thread.currentThread().getName() + ": The light of " + this.number + " floor is off.");
        }
    }
    
    private class ArrivalSensor {
        // The height of each floor in meters
        private static final double floorHeight = 3.57; 
        // floor number
        private int number;
        
        public ArrivalSensor(int number) {
            this.number = number;
        }
        /**
         * Check if the elevator arrives at current this floor
         * SHOULD USE A REAL HARDWARE to notify the elevator
         * The method simulate this behavior. 
         * The arrival sensor calculates the time the elevator arrives,
         * and then sleeps. When the elevator exits from this method,
         * means the elevator arrived at this floor.
         * If the maximum speed and acceleration changed in the Motor class,
         * this method should be rewrited.
         * The logic is based on maximum speed of 1.43 m/s and 
         * acceleration 0.6 m/s^2
         * Correctness can be checked using Motor.move(displacement)
         * @param speed initial speed of the elevator
         * @param maxSpeed maximum speed of the elevator
         * @param accelerationDisplacement The displacement the elevator takes 
         * 	to accelerate to maxSpeed and deceleration from maxSpeed
         * @param accelerationTime The time the elevator takes to acceleration to maxSpeed and deceleration from maxSpeed in seconds
         * @param toFloor the destination floor
         * @return new speed of the elevator
         */
        public double check(double speed, double maxSpeed, double accelerationDisplacement, double accelerationTime, int toFloor) {
            double movementTime;
            // based on the data we have, the elevator can accelerate and decelerate within one floor
            // then the elevator will reach max speed
            if (speed == 0) {
                // stationary to move
                if (toFloor == this.number) {
                    // to the adjacent floor
                    movementTime = (this.floorHeight - accelerationDisplacement * 2) / maxSpeed + accelerationTime * 2;
                    speed = 0;
                } else {
                    // accelerate to constant
                    movementTime = accelerationTime + (this.floorHeight - accelerationDisplacement) / maxSpeed;
                    speed = maxSpeed;
                }
            } else {
                // move to stationary, or keep constant speed
                if (toFloor == this.number) {
                    // constant to decelerate
                    movementTime = accelerationTime + (this.floorHeight - accelerationDisplacement) / maxSpeed;
                    speed = 0;
                } else {
                    // keep constant speed
                    movementTime = this.floorHeight / maxSpeed;
                    speed = maxSpeed;
                }
            }
            try {
                Thread.sleep((long) (movementTime * 1000));
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return speed;
        }
    }
}
