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
			}
		}
	}
	
	/**
	 * Move to next floor
	 * @param nextFloor the floor the elevator will go to
	 */
	private void move(int nextFloor) {
		send(0, "moving");
		System.out.println(Thread.currentThread().getName() + ": Moving to " + nextFloor + " floor.");
		int difference = nextFloor - currentFloor;
		// DirectionLamp lamp = (difference > 0) ? this.upLamp : this.downLamp;
		// lamp.on();
		double displacement = Math.abs(difference) * this.floorHeight;
		motor.move(displacement);
		send(0, "waiting");
		this.currentFloor = nextFloor;
		buttons.get(buttonIndex(nextFloor)).off();
		// lamp.off();
		System.out.println(Thread.currentThread().getName() + ": arrived at " + nextFloor + " floor.");
		door.toggle();
	}
	
	/**
	 * report current status to the scheduler
	 */
	private void status() {
		send(0, "waiting");
	}
	
	/**
	 * A button is pressed
	 * Not used in iteration 1
	 * @param button the button pressed in the car
	 */
	private void press(int button) {
		if (this.totalUndergroundFloors < button && button != 0 && button <= totalGroundFloors && button != this.currentFloor) {
			send(button, "waiting");
			if (button > 0) {
				buttons.get(buttonIndex(button)).press();
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
	private void send(int button, String state) {
		Boolean isSent = false;
		while(!isSent) {
			isSent = c.send("elevator", getTime(), this.currentFloor, this.number, button, state);
			if (!isSent) {
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
    	move(2);
    	move(3);
    	move(7);
    	move(1);
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
    	// The maximum speed of the elevator in m/s
    	private static final double maxSpeed = 1.3; 
    	// The acceleration of the elevator in m/s^2
    	private static final double acceleration = 0.65;
    	// The time the elevator takes to acceleration to maxSpeed and deacceleration from maxSpeed in seconds
    	private static final double accelerationTime = 2;
    	// The displacement the elevator takes to accelerate to maxSpeed and deacceleration from maxSpeed
    	private final double accelerationDisplacement = 0.5 * acceleration * Math.pow(accelerationTime, 2);
    	// 0 for stationary, 1 for up, 0 for down
    	private int direction = 0;
    	
    	public Motor() {
    		
    	}
    	
    	/**
    	 * To simulate the movement of moving between floors
    	 */
    	private void move(double displacement) {
    		double movementTime;
			if (displacement < this.accelerationDisplacement * 2) {
				movementTime = displacement / this.acceleration;
			} else {
				movementTime = (displacement - this.accelerationDisplacement * 2) / this.maxSpeed + accelerationTime * 2;
			}
			System.out.println(Thread.currentThread().getName() + ": " + displacement + "m; " + movementTime + " seconds.");
			try {
			    Thread.sleep((long) (movementTime * 1000));
			} catch(InterruptedException e) {
			    Thread.currentThread().interrupt();
			}
    	}
    	
    	/**
    	 * To simulate the up movement of moving between floors
    	 */
    	private void up() {
    		this.direction = 1;
    	}
    	
    	/**
    	 * To simulate the down movement of moving between floors
    	 */
    	private void down() {
    		this.direction = 0;
    	}
    	
    	/**
    	 * To simulate the stop
    	 */
    	private void stop() {
    		this.direction = 0;
    	}
    }
    
    private class ElevatorButton {
    	private int number;
    	private ElevatorLamp lamp;
    	
    	public ElevatorButton(int number) {
    		this.number = number;
    		this.lamp = new ElevatorLamp(number);
    	}
    	
    	/**
    	 * The button is pressed
    	 */
    	public void press() {
    		this.lamp.on();
    	}
    	
    	/**
    	 * Turn off the light when the elevator arrives
    	 */
    	public void off() {
    		this.lamp.off();
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
    	
    	public ArrivalSensor(int number) {
    		this.number = number;
    	}
    }
}
