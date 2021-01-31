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
	
	private Communication c = new Communication();
	private Door door = new Door();
	private Motor motor = new Motor();
	private ArrayList<ElevatorButton> buttons = new ArrayList<ElevatorButton>();
	private ArrayList<ElevatorButton> floorLamps = new ArrayList<ElevatorButton>();
	private ArrayList<ArrivalSensor> arrivalSensors = new ArrayList<ArrivalSensor>();
	private ArrayList<ElevatorLamp> elevatorLamps = new ArrayList<ElevatorLamp>();
	// private DirectionLamp upLamp = new DirectionLamp();
	// private DirectionLamp downLamp = new DirectionLamp();
	
	private int number;
	private int currentFloor;
	private ArrayList<Integer> nextFloors = new ArrayList<Integer>();
	
	public Elevator(int number, int currentFloor) {
		this.number = number;
		this.currentFloor = currentFloor;
		for(int i = this.totalUndergroundFloors; i <= this.totalGroundFloors; i++) {
			if (i != 0) {
				buttons.add(new ElevatorButton(i));
				floorLamps.add(new ElevatorButton(i));
				arrivalSensors.add(new ArrivalSensor(i));
				elevatorLamps.add(new ElevatorLamp(i));
			}
		}
	}
	
	/**
	 * Move to next floor
	 * TODO: use arrival sensors
	 * @param nextFloor the floor the elevator will go to
	 */
	private void move(int nextFloor) {
		send(0, "moving", false);
		System.out.println(Thread.currentThread().getName() + ": Moving to " + nextFloor + " floor.");
		int difference = nextFloor - currentFloor;
		// DirectionLamp lamp = (difference > 0) ? this.upLamp : this.downLamp;
		// lamp.on();
		double displacement = Math.abs(difference) * this.floorHeight;
		motor.move(displacement);
		this.currentFloor = nextFloor;
		buttons.get(buttonIndex(this.currentFloor)).off();
		elevatorLamps.get(buttonIndex(this.currentFloor)).off();
		// lamp.off();
		System.out.println(Thread.currentThread().getName() + ": arrived at " + nextFloor + " floor.");
		send(0, "waiting", false);
		door.toggle();
	}
	
	/**
	 * report current status to the scheduler
	 */
	private void status() {
		send(0, "waiting", false);
	}
	
	/**
	 * A button is pressed
	 * Not used in iteration 1
	 * @param button the button pressed in the car
	 */
	private void press(int button) {
		if (this.totalUndergroundFloors < button && button != 0 && button <= totalGroundFloors && button != this.currentFloor) {
			send(button, "waiting", false);
			if (button > 0) {
				buttons.get(buttonIndex(button)).press();
				elevatorLamps.get(buttonIndex(button)).on();
			} else {
				buttons.get(buttonIndex(button)).press();
			}
		} else {
			
		}
	}
	
	/**
	 * Calculate the index of the button in this.buttons
	 * @param button
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
	 */
	private void send(int button, String state, Boolean noRetry) {
		Boolean isSent = false;
		while(!isSent) {
			isSent = c.send("elevator", getTime(), this.currentFloor, this.number, button, state);
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
		Boolean isReceived = false;
		while(!isReceived) {
			isReceived = c.get();
			if (!isReceived) {
				try {
				    Thread.sleep(this.delay);
				} catch(InterruptedException e) {
				    Thread.currentThread().interrupt();
				}
			}
		}
		int nextFloor = c.getFloor();
		if (nextFloor == 0) {
			status();
		} else if (nextFloor == this.currentFloor) {
			door.toggle();
		} else {
			move(nextFloor);
		}
	}
	
	/**
	 * Get current time in epoch seconds
	 * @return long current time in epoch seconds
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
//    	while(true) {
//    		get();
//    	}
    	move(7);
    	move(6);
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
    		System.out.println(Thread.currentThread().getName() + ": Door is opening.");
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
    	// The time the elevator takes to acceleration to maxSpeed and deacceleration from maxSpeed in seconds
    	private final double accelerationTime = maxSpeed / acceleration;
    	// The displacement the elevator takes to accelerate to maxSpeed and deacceleration from maxSpeed
    	private final double accelerationDisplacement = 0.5 * acceleration * Math.pow(accelerationTime, 2);
    	// 0 for stationary, 1 for up, 0 for down
    	private int direction = 0;
    	private double currentSpeed = 0;
    	
    	public Motor() {
    		
    	}
    	
    	/**
    	 * To simulate the movement of moving between floors
    	 */
    	public void move(double displacement) {
    		double movementTime;
			if (displacement < this.accelerationDisplacement * 2) {
				movementTime = Math.sqrt(2 * displacement / this.acceleration);
			} else {
				movementTime = (displacement - this.accelerationDisplacement * 2) / this.maxSpeed + this.accelerationTime * 2;
			}
			System.out.println(Thread.currentThread().getName() + ": " + displacement + "m; " + movementTime + " seconds.");
			try {
			    Thread.sleep((long) (movementTime * 1000));
			} catch(InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
    	}
    	
    	/**
    	 * Update the status from the elevator
    	 */
    	public void update(double thmSpeed) {
    		this.currentSpeed = (thmSpeed > this.maxSpeed) ? this.maxSpeed : thmSpeed;
    	}
    	
    	/**
    	 * To simulate the up movement of moving between floors
    	 */
    	public double up() {
    		this.direction = 1;
    		start();
    		return this.currentSpeed;
    	}
    	
    	/**
    	 * To simulate the down movement of moving between floors
    	 */
    	public double down() {
    		this.direction = 0;
    		start();
    		return this.currentSpeed;
    	}
    	
    	/**
    	 * To simulate the stop
    	 */
    	public void stop() {
    		this.direction = 0;
    		this.currentSpeed = 0;
    	}
    	
    	private void start() {
    		
    	}
    	
    	public double getAcceleration() {
    		return this.acceleration;
    	}
    	
    	public double getAccelerationDisplacement() {
    		return this.accelerationDisplacement;
    	}
    	
    	public double getAccelerationTime() {
    		return this.accelerationTime;
    	}
    }
    
    private class ElevatorButton {
    	private int number;
    	
    	public ElevatorButton(int number) {
    		this.number = number;
    	}
    	
    	/**
    	 * The button is pressed
    	 */
    	public void press() {
    		
    	}
    	
    	/**
    	 * Turn off the light when the elevator arrives
    	 */
    	public void off() {
    		
    	}
    }
    
    private class ElevatorLamp {
    	private int number;
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
    	private int number;
    	
    	// USE A REAL HARDWARE PLEASE
    	// Notes: elevator passes in speed, acceleration, and floor number
    	// arrival sensor calculates the time, and then thread sleep
    	// when done, return the theoretical speed of the elevator.
    	// The elevator knows it arrives at certain floor, send a call
    	// to the scheduler (maybe, without retry). then make calls to 
    	// next arrival sensor. if the theoretical speed is higher than 
    	// the max speed, the max speed.
    	public ArrivalSensor(int number) {
    		this.number = number;
    	}
    	
    	public void check() {
    		
    	}
    }
}
