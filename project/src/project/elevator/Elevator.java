package project.elevator;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import project.DirectionLamp;
import project.elevator.src.*;
import project.utils.Database;
import project.utils.Parser;
import project.utils.Sender;

public class Elevator implements Runnable {
	private State state = State.Stationary;
	
	// floor height
	private static final double floorHeight = 3.57; 
	// The number of ground floors
    private int totalGroundFloors; 
    // The number of underground floors
    private int totalUndergroundFloors; 
    // id of the elevator
    private int identifier;
    // the floor the elevator initially stays
    private int currentFloor;
    // the dest floor
    private int destFloor;
    // direction
    private int direction = -1;
    // speed
    private double speed = 0;
    // simulate stuck between floors
    private boolean stuckBetweenFloors;
    
    private Sender sender;
    private Parser parser = new Parser();
    private Door door;
    private Motor motor = new Motor();
    private Database db;
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
    // listen to scheduler message
    private volatile ArrayList<byte[]> schedulerCommands = new ArrayList<byte[]>();
    // error message
    private String errorMessage;
	
    /**
     * Elevator constructor
     * @param identifier the id of the elevator
     * @param currentFloor initial floor
     * @param totalGroundFloors The number of ground floors
     * @param totalUndergroundFloors The number of underground floors
     * @param doorStuckAtOpen if the door is stuck at open
     * @param doorStuckAtClose if the door is stuck at close
     * @param stuckBetweenFloors if the elevator is stuck between floors
     * @param db the database
     */
	public Elevator(int identifier, int currentFloor, int totalGroundFloors, int totalUndergroundFloors, boolean doorStuckAtOpen, boolean doorStuckAtClose, boolean stuckBetweenFloors, Database db) {
		this.sender = new Sender(db);
		
		this.totalGroundFloors = totalGroundFloors;
		this.totalUndergroundFloors = totalUndergroundFloors;
		this.identifier = identifier;
        this.currentFloor = currentFloor;
        this.door = new Door(doorStuckAtOpen, doorStuckAtClose);
        this.stuckBetweenFloors = stuckBetweenFloors;
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
	 * start the elevator
	 */
	public void execute() {
		while(true) {
			if (this.state == State.Stationary) {
				this.state = stationary();
			} else if (this.state == State.OpenDoor) {
				this.state = openDoor();
			} else if (this.state == State.CloseDoor) {
				this.state = closeDoor();
			} else if (this.state == State.Move) {
				this.state = move();
			} else if (this.state == State.Stop) {
				this.state = stop();
			} else {
				error();
				System.exit(-1);
			}
		}
	}
	
	/**
     * For iteration 1 and 2, Scheduler sends the message to here
     * 
     * @param inputMessage the message
     */
    public void put(byte[] inputMessage) {
        this.schedulerCommands.add(inputMessage);
    }
	
    /**
     * State Stationary, the elevator has nothing to do
     * @return next state
     */
	private State stationary() {
		// Future notes: when the receiver receive a message, put it into Database
		// then get it from Database
		
		if (this.schedulerCommands.size() != 0) {
            this.parser.parse(this.schedulerCommands.get(0));
            this.schedulerCommands.remove(0);
//            String state = parser.getState();
//            if (state.equals("Check")) {
//            	String revMsg = sender.sendFloor(this.getClass().getSimpleName(), this.identifier, "Check", this.currentFloor, getTime());
//            } else if (state.equals("OpenDoor")) {
//            	return State.OpenDoor;
//            } else if (state.equals("Move")) {
//            	int destFloor = parser.getFloor();
//            	String revMsg = sender.sendFloor(this.getClass().getSimpleName(), this.identifier, "Move", this.currentFloor, getTime());
//            	return State.Move;
//            }
            // legacy code
            int destFloor = parser.getFloor();
            if (destFloor == this.currentFloor) {
                // state OpenDoor
            	return State.OpenDoor;
            } else {
            	this.destFloor = destFloor;
            	// state Move
            	String revMsg = sender.sendFloor(this.getClass().getSimpleName(), this.identifier, "Move", this.currentFloor, getTime());
            	return State.Move;
            }
        }
		return State.Stationary;
	}
	
	/**
	 * State OpenDoor, the elevator opens the door
	 * @return next state
	 */
	private State openDoor() {
		door.open();
		String revMsg = sender.sendState(this.getClass().getSimpleName(), this.identifier, "OpenDoor", getTime());
//		parser.parse(revMsg);
//		String state = parser.getState();
//		if (state.equals("Received")) {
//			return State.CloseDoor;
//		} else {
//			return State.Error;
//		}
		// legacy code
		return State.CloseDoor;
	}
	
	/**
	 * State CloseDoor, the elevator closes the door
	 * @return next state
	 */
	private State closeDoor() {
		door.close();
		String revMsg = sender.sendState(this.getClass().getSimpleName(), this.identifier, "CloseDoor", getTime()); // get next step
//		parser.parse(revMsg);
//		String state = parser.getState();
//		if (state.equals("Received")) {
//			// get the message from the database and update the state
//			if (state.equals("Move")) {
//				this.destFloor = parser.getFloor();
//				return State.Move;
//			} else if (state.equals("Stationary")) {
//				return State.Stationary;
//			}
//		}
//		return State.Error;
		// legacy code
		return State.Stationary;
	}
	
	/**
	 * State Move, the elevator moves between floors
	 * @return next state
	 */
	private State move() {
		DirectionLamp directionLamp = null;
		int difference = this.destFloor - this.currentFloor;
		if (difference > 0) {
            // going up
            directionLamp = this.upLamp;
            this.direction = 1;
            directionLamp.on();
            motor.up(this.speed);
            
            this.speed = arrivalSensors.get(buttonIndex(this.currentFloor)).check(motor.getSpeed(), motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), this.destFloor);
            this.currentFloor++;
            floorLamps.get(buttonIndex(this.currentFloor - 1)).off();
            floorLamps.get(buttonIndex(this.currentFloor)).on();
        } else if (difference < 0) {
            // going down
            directionLamp = this.downLamp;
            this.direction = 0;
            directionLamp.on();
            motor.down(this.speed);
            
            this.speed = arrivalSensors.get(buttonIndex(this.currentFloor)).check(motor.getSpeed(), motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), this.destFloor);
            this.currentFloor--;
            floorLamps.get(buttonIndex(this.currentFloor + 1)).off();
            floorLamps.get(buttonIndex(this.currentFloor)).on();
        }
		
		if (this.currentFloor == this.destFloor) {
			return State.Stop;
		}
		String revMsg = sender.sendFloor(this.getClass().getSimpleName(), this.identifier, "Move", this.currentFloor, getTime());
		parser.parse(revMsg);
		String state = parser.getState();
//		get the message from the database and update the state
//		if (state.equals("Received")) {
//			// update the dest floor if available
//			int newDestFloor = parser.getFloor();
//			this.destFloor = newDestFloor;
//			return State.Move;
//		} else {
//			this.schedulerNotReachable = true;
//			return State.Error;
//		}
		// legacy code
		return State.Move;
	}
	
	/**
	 * State Stop, the elevator stops at a certain floor
	 * @return next state
	 */
	private State stop() {
		motor.stop();
        if (this.direction == 1) {
        	this.upLamp.off();
        } else if (this.direction == 0) {
        	this.downLamp.off();
        }
        this.speed = 0;
		this.direction = -1;
        System.out.println(Thread.currentThread().getName() + ": arrived at " + this.destFloor + " floor.");
		
        String revMsg = sender.sendFloor(this.getClass().getSimpleName(), this.identifier, "Stop", this.currentFloor, getTime());
//        parser.parse(revMsg);
//        String state = parser.getState();
//        if (state.equals("Received")) {
//        	return State.OpenDoor;
//        } else {
//        	this.schedulerNotReachable = true;
//			return State.Error;
//        }
        // legacy code
        return State.OpenDoor;
	}
	
	/**
	 * State Error, the elevator errors out
	 * The elevator will try to contact the Scheduler about the error and then shutdown for safety reasons
	 */
	private void error() {
		if(this.state == State.Move) {
            motor.stop();
		}
		while(true) {
			try {
	            Thread.sleep((long) (500));
	        } catch(InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
			String revMsg = sender.sendError(this.getClass().getSimpleName(), this.identifier, this.errorMessage, this.currentFloor, getTime());
			parser.parse(revMsg);
			String state = parser.getState();
			if (state.equals("Received")) {
				break;
			}
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
     * Get current time in epoch seconds
     * @return as described above
     */
    private long getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
    
    /**
     * For Unit testing, get current state of the elevator
     * @return
     */
    public String getState() {
    	if (this.state == State.Stationary) {
    		return "Stationary";
    	} else if (this.state == State.OpenDoor) {
    		return "OpenDoor";
    	} else if (this.state == State.CloseDoor) {
    		return "CloseDoor";
    	} else if (this.state == State.Move) {
    		return "Move";
    	} else if (this.state == State.Stop) {
    		return "Stop";
    	} else {
    		return "Error";
    	}
    }
	
	/**
     * @see java.lang.Runnable#run()
     */
//    @Override
    public void run() {
        while(true) {
            execute();
        }
    }
    
    public static void main(String args[]) {
    	
	}
}
