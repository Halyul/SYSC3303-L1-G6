package project.elevator;
import java.net.*;
import java.time.*;
import java.util.*;

import project.elevator.src.*;
import project.utils.*;

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
    private boolean stuckBetweenFloors = false;
    // simulate arrival sensor has failed
    private boolean arrivalSensorFailed = false;
    private boolean isSimulated = false;
    // simulate door stuck at open;
    private boolean doorStuckAtOpen = false;
    // simulate door stuck at close;
    private boolean doorStuckAtClose = false;
    
    private InetAddress schedulerAddress;
    private int schedulerPort;
    
    private Sender sender;
    private Parser parser = new Parser();
    private Door door;
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
    // listen to scheduler message
    private volatile DataStruct schedulerCommand = new DataStruct();
    // error message
    private String errorMessage;
    // idle message state
    private boolean isSentIdleMessage = false;
    
    /**
     * Elevator constructor
     * @param identifier the id of the elevator
     * @param currentFloor initial floor
     * @param totalGroundFloors The number of ground floors
     * @param totalUndergroundFloors The number of underground floors
     * @param schedulerAddress the address of the scheduler
     * @param port the port of the scheduler
     */
    public Elevator(int identifier, int currentFloor, int totalGroundFloors, int totalUndergroundFloors, InetAddress schedulerAddress, int port) {
        // needs update here
        this.sender = new Sender();
        
        this.totalGroundFloors = totalGroundFloors;
        this.totalUndergroundFloors = totalUndergroundFloors;
        this.identifier = identifier;
        this.currentFloor = currentFloor;
        this.door = new Door();
        for(int i = this.totalUndergroundFloors; i <= this.totalGroundFloors; i++) {
            if (i != 0) {
                buttons.add(new ElevatorButton(i));
                buttonLamps.add(new ElevatorLamp(i));
                floorLamps.add(new ElevatorLamp(i));
                arrivalSensors.add(new ArrivalSensor(i));
            }
        }
        floorLamps.get(buttonIndex(this.currentFloor)).on();
        this.schedulerAddress = schedulerAddress;
        this.schedulerPort = port;
    }
    
    /**
     * start the elevator
     */
    public void execute() {
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
    
    /**
     * Add scheduler command to the list
     * 
     * @param inputMessage the message
     */
    public void put(byte[] inputMessage) {
        this.parser.parse(inputMessage);
        this.schedulerCommand.setState(this.parser.getState(), this.parser.getFloor());
        System.out.println("Elevator " + this.identifier + ": receive a task: " + new String(inputMessage));
    }
    
    /**
     * State Stationary, the elevator has nothing to do
     * @return next state
     */
    private State stationary() {
    	if (!this.isSentIdleMessage) {
    		String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "Idle", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
    		this.isSentIdleMessage = true;
            System.out.println("Elevator " + this.identifier + ": Current stationary");
    	}
        if (this.schedulerCommand.isWaiting()) {
            String state = this.schedulerCommand.getState();
            if (state.equals("Check")) {
                String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "Idle", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
            } else if (state.equals("Move")) {
                // inject faults
                String error = this.schedulerCommand.getError();
                if (error.equals("stuckBetweenFloors")) {
                    this.stuckBetweenFloors = true;
                } else if (error.equals("arrivalSensorFailed")) {
                    this.arrivalSensorFailed = true;
                } else if (error.equals("doorStuckAtOpen")) {
                    this.doorStuckAtOpen = true;
                } else if (error.equals("doorStuckAtClose")) {
                    this.doorStuckAtClose = true;
                }

                int destFloor = this.schedulerCommand.getFloor();
                this.destFloor = destFloor;
                if (destFloor == this.currentFloor) {
                	// same floor, use State Stop because the elevator need to clean up the command
                	this.isSentIdleMessage = false;
                	return State.Stop;
                }
                int difference = this.destFloor - this.currentFloor;
                if (difference > 0) {
                    // going up
                    this.direction = 1;
                } else if (difference < 0) {
                    this.direction = 0;
                }
                String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "Move", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
                this.isSentIdleMessage = false;
                return State.Move;
            } else if (state.equals("Error")) {
                this.errorMessage = "schedulerReportedError";
                return State.OpenDoor;
            }
        }
        return State.Stationary;
    }
    
    /**
     * State OpenDoor, the elevator opens the door
     * @return next state
     */
    private State openDoor() {
        boolean isOpened = door.open(this.doorStuckAtClose);
        if (!isOpened) {
            this.errorMessage = "doorStuckAtOpen";
            return State.Error;
        }
        String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "OpenDoor", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
        parser.parse(revMsg);
        String state = parser.getState();
        if (state.equals("Received")) {
            if (this.schedulerCommand.getState().equals("Error")) {
                this.errorMessage = "schedulerReportedError";
                return State.Error;
            }
            return State.CloseDoor;
        } else {
            return State.Error;
        }
    }
    
    /**
     * State CloseDoor, the elevator closes the door
     * @return next state
     */
    private State closeDoor() {
        boolean isClosed = door.close(this.doorStuckAtOpen);
        if (!isClosed) {
            this.errorMessage = "doorStuckAtClose";
            return State.Error;
        }
        String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "CloseDoor", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort); // get next step
        parser.parse(revMsg);
        String state = parser.getState();
        if (state.equals("Received")) {
            if (!this.schedulerCommand.isWaiting()) {
                return State.Stationary;
            } else {
                if (this.schedulerCommand.getState().equals("Error")) {
                    this.errorMessage = "schedulerReportedError";
                    return State.OpenDoor;
                }
                this.destFloor = this.schedulerCommand.getFloor();
                return State.Move;
            }
        }
        return State.Error;
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
            if (this.stuckBetweenFloors) {
                this.errorMessage = "stuckBetweenFloors";
                return state.Error;
            }
            this.speed = arrivalSensors.get(buttonIndex(this.currentFloor + 1)).check(motor.getSpeed(), motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), this.destFloor);
            this.currentFloor++;
            if (this.currentFloor == this.destFloor && this.arrivalSensorFailed) {
                if (this.isSimulated) {
                    floorLamps.get(buttonIndex(this.currentFloor - 2)).off();
                    floorLamps.get(buttonIndex(this.currentFloor)).on();
                }
            } else {
                floorLamps.get(buttonIndex(this.currentFloor - 1)).off();
                floorLamps.get(buttonIndex(this.currentFloor)).on();
            }
        } else if (difference < 0) {
            // going down
            directionLamp = this.downLamp;
            this.direction = 0;
            directionLamp.on();
            motor.down(this.speed);
            this.speed = arrivalSensors.get(buttonIndex(this.currentFloor - 1)).check(motor.getSpeed(), motor.getMaxSpeed(), motor.getAccelerationDisplacement(), motor.getAccelerationTime(), this.destFloor);
            if (this.stuckBetweenFloors) {
                this.errorMessage = "stuckBetweenFloors";
                return state.Error;
            }
            this.currentFloor--;
            if (this.currentFloor == this.destFloor && this.arrivalSensorFailed) {
                if (this.isSimulated) {
                    floorLamps.get(buttonIndex(this.currentFloor + 2)).off();
                    floorLamps.get(buttonIndex(this.currentFloor)).on();
                }
            } else {
                floorLamps.get(buttonIndex(this.currentFloor + 1)).off();
                floorLamps.get(buttonIndex(this.currentFloor)).on();
            }
        }
        
        if (this.currentFloor == this.destFloor) {
            if (this.arrivalSensorFailed && !this.isSimulated) {
                // simulate the arrival sensor at destFloor has failed
                // the elevator should go pass this floor, and reach next floor
                if (difference > 0) {
                    this.destFloor++;
                } else if (difference < 0) {
                    this.destFloor--;
                }
                this.speed = motor.getMaxSpeed();
                this.isSimulated = true;
                return State.Move;
            }
            return State.Stop;
        }
        String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "Move", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
        parser.parse(revMsg);
        String state = parser.getState();
        if (state.equals("Received")) {
            int newDestFloor = this.schedulerCommand.getFloor();
            this.destFloor = newDestFloor;
            return State.Move;
        } else {
            return State.Error;
        }
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
        System.out.println(Thread.currentThread().getName() + ": stop at " + this.destFloor + " floor.");
        this.schedulerCommand.finished();
        String revMsg = sender.sendElevatorState(this.getClass().getSimpleName(), this.identifier, "Stop", this.currentFloor, this.direction, getTime(), schedulerAddress, this.schedulerPort);
        parser.parse(revMsg);
        String state = parser.getState();
        if (state.equals("Received")) {
            return State.OpenDoor;
        } else {
            return State.Error;
        }
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
            String revMsg = sender.sendError(this.getClass().getSimpleName(), this.identifier, this.errorMessage, this.currentFloor, getTime(), schedulerAddress, this.schedulerPort);
            parser.parse(revMsg);
            String state = parser.getState();
            if (state.equals("Received")) {
                break;
            }
            try {
                Thread.sleep((long) (500));
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
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
     * @return the state name in string
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
   @Override
    public void run() {
        while(true) {
            execute();
        }
    }
    
   public static void main(String args[]) {
	   	InetAddress schedulerAddress = null;
	   	int numberOfElevators = 4;
	   	ArrayList<Elevator> elevators = new ArrayList<Elevator>();
	   	ArrayList<Thread> elevatorThreads = new ArrayList<Thread>();

	   	try {
	   		schedulerAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
	   	for (int i = 0; i < numberOfElevators; i++) {
	   		Elevator elevator = new Elevator((i + 1), 1, 7, 0, schedulerAddress, 12000);
	   		elevators.add(elevator);
	   		Thread elevatorThread = new Thread(elevator, "Elevator " + (i + 1));
	   		elevatorThreads.add(elevatorThread);
	   		elevatorThread.start();
	   	}
	   	Receiver r = new Receiver(elevators, 12000);
	   	Thread receiverThread = new Thread(r, "Receiver");
	   	receiverThread.start();
   }
}
