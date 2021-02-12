package project.backup;
import project.elevator.*;
import project.elevator.src.ArrivalSensor;
import project.elevator.src.Door;
import project.elevator.src.ElevatorButton;
import project.elevator.src.ElevatorLamp;
import project.elevator.src.Motor;
import project.utils.Parser;
import project.utils.Sender;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

/**
 * Elevator subsystem
 * @author Haoyu Xu
 *
 */
public class ElevatorSubsystem implements Runnable {
    // The number of ground floors
    private static final int totalGroundFloors = 7; 
    // The number of underground floors
    private static final int totalUndergroundFloors = 0; 
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
    
    public ElevatorSubsystem(int identifier, int currentFloor, Database database) {
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
}
