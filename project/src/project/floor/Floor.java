package project.floor;

import java.net.InetAddress;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import project.floor.src.*;
import project.utils.Parser;

import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

import project.utils.*;

/*
 * 
 * 
 * */

public class Floor implements Runnable {
	//Current floor Number
	private int floorNumber;
	//Number of the top floor
	private int topFloor;
	private double baseTime = 0;

	private Sender sender;
	private Parser parser = new Parser();

	private InetAddress schedulerAddress;
	private int schedulerPort;
	
	private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
	private volatile ArrayList<Integer> floorQueue;	//0 if passenger wants to go down, 1 if up
	private FloorButton upButton;
	private FloorButton downButton;
	private DirectionLamp upLamp;
	private DirectionLamp downLamp;
	
	/**
	 * Floor constructor
	 * @param floorNumber number of the current floor
	 * @param topFloor number of the top floor
	 * @param database database where messages are sent
	 */
	public Floor(int floorNumber, int topFloor, Database database, InetAddress schedulerAddress, int port) {
		this.floorNumber = floorNumber;
		this.topFloor = topFloor;
		this.sender = new Sender(database);
		this.floorQueue = new ArrayList<Integer>();
		//If bottom floor, initialize one set of up lamps and buttons
		if(floorNumber == 0) {
			this.upButton = new FloorButton();
		}
		//If top floor, initialize one set of down lamps and buttons
		else if(floorNumber == topFloor) {
			this.downButton = new FloorButton();
		}
		//else, initialize up/down lamps and buttons
		else {
			this.upButton = new FloorButton();
			this.downButton = new FloorButton();
		}

		this.upLamp = new DirectionLamp();
		this.downLamp = new DirectionLamp();
		this.schedulerAddress = schedulerAddress;
		this.schedulerPort = port;
	}
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		ReadInput("floorInput.txt");
		while(true) {
			get();
		}
	}

	/**
	 * Get receives messages from the scheduler
	 */
	private void get() {
		//If there is a message
		if (this.messages.size() != 0) {
			this.parser.parse(messages.get(0)); // Parse message
			messages.remove(0);
			//if elevator reaches destination floor
			if (parser.getRole().equals("Elevator") && parser.getState().equals("Stop") && parser.getFloor() == this.floorNumber) {
				//If the floor is neither the top or bottom floor
				if(this.floorNumber != topFloor && this.floorNumber != 0) {
					//Turn off both button lights
					this.upButton.off();
					this.downButton.off();
				}
				//else if it is the top floor,
				else if(this.floorNumber == topFloor) {
					//turn off downButton light
					this.downButton.off();
				}
				//elseif it is the bottom floor
				else if(this.floorNumber == 0) {
					//turn off upButton light
					this.upButton.off();
				}
			}
			//Following ifs set the direction of the DirectionLamp
			if(parser.getDirection() == 1) {
				upLamp.on();
				downLamp.off();
			}
			else if(parser.getDirection() == 0) {
				upLamp.off();
				downLamp.on();
			}
			else {
				upLamp.off();
				downLamp.off();
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
	 * Send the message to the Scheduler
	 * @param time the time from the input file in epoch seconds
	 * @param currentFloor current floor number
	 * @param direction the direction the elevator is going (up/down)
	 * @param CarButton The destination floor chosen by the passenger
	 * @param state the status of the floor
	 */
	private void send(long time, int currentFloor, int direction, int CarButton, String state) {
		Boolean isSent = false;
		// Haoyu Xu: updated Sender
		// role:Floor;id:<current floor number>;state:<your own definition>;direction:<1/up or 0/down>;floor:<button pressed in the car>;time:<time>
		String revMsg = sender.sendInput(currentFloor, state, direction, CarButton, time);
//		while(!isSent) {
//			isSent = sender.send("floor", this.floorNumber, this.floorNumber, direction, CarButton, time);
//		}
	}
	
	
	/*
	 * Used to convert input button presses to a reasonable time
	 * @param inputTime the time when a button is pressed
	 * */
	private double floorTime(String inputTime) {
		String time = inputTime.toString();
		double hour = Double.parseDouble(time.substring(0,2));
		double minute = Double.parseDouble(time.substring(3,5));
		double seconds = Double.parseDouble(time.substring(6,8));
		
		return (hour * 60 * 60 + minute * 60 + seconds) / 100; //Returns the time in a HHMM.SS format in seconds
	}
	
	/**
	 * Reads an input file containing the time, floor, direction and destination
	 * of a passenger calling an elevator from a floor
	 * @param inputFile name of the input file
	 */
	public void ReadInput(String inputFile) {
		try {
			File inFile = new File(inputFile);
			Scanner inReader = new Scanner(inFile);
			//While the text file has another line
			while (inReader.hasNextLine()) {
				String ins = inReader.nextLine();		//Goes to the next line, storing the current line in ins
				String[] individualIns = ins.split("\\s+");		//Split the instructions at whitespace characters
				int dir = 0;
				if(individualIns[2].equals("Up")) {	//If passengers wants to go up
					dir = 1;
					if(this.floorNumber != topFloor) {
						if(!this.upButton.getState()) {
							//Checks if the button is already on
							this.upButton.on();
						}
					}
				}
				else if(individualIns[2].equals("Down")) { //If passengers wants to go down
					dir = 0;
					if(this.floorNumber != 0) {
						if(!this.downButton.getState()) {	//Checks if the button is already on
							this.downButton.on();
						}
					}
				}
				int currentFloor = Integer.parseInt(individualIns[1]); // get the floor the user currently at
				int destFloor = Integer.parseInt(individualIns[3]);	//Stores destination floor
				SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS.S");	//Used for epoch time conversion
				try {
					Date currTime = time.parse(individualIns[0]);
					double inputTime = floorTime(individualIns[0]);
					if(baseTime == 0) {
						baseTime = inputTime;
					}
					//TODO: Send message and add people to queue based on time
					Thread.sleep((long) (inputTime - baseTime));
					send(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), currentFloor, dir, destFloor, "Reading");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

			}
			inReader.close();
		}	catch (FileNotFoundException e) {
			System.out.println(Thread.currentThread().getName() + ": File not found");
		}
		
		
		
		//Legacy code
//		try {
//			File inFile = new File(inputFile);
//			Scanner inReader = new Scanner(inFile);
//			//While the text file has another line
//			while (inReader.hasNextLine()) {
//				String ins = inReader.nextLine();		//Goes to the next line, storing the current line in ins
//				String[] individualIns = ins.split("\\s+");		//Split the instructions at whitespace characters
//				int dir = 0;
//				int currentFloor = Integer.parseInt(individualIns[1]); // get the floor the user currently at
//				if(individualIns[2].equals("Up")) {	//If passengers wants to go up
//					dir = 1;
//					if(this.floorNumber != topFloor) {
//						if(!this.upButton.getState()) {
//							//Checks if the button is already on
//							this.upButton.on();
//						}
//					}
//				}
//				else if(individualIns[2].equals("Down")) { //If passengers wants to go down
//					dir = 0;
//					if(this.floorNumber != 0) {
//						if(!this.downButton.getState()) {	//Checks if the button is already on
//							this.downButton.on();
//						}
//					}
//				}
//				if(currentFloor == this.floorNumber)
//					floorQueue.add(dir);
//				int destFloor = Integer.parseInt(individualIns[3]);	//Stores destination floor
//				SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS.S");	//Used for epoch time conversion
//				//Try parsing the time and converting it to epoch time
//				try {
//					Date currTime = time.parse(individualIns[0]);
//					LocalDate currDate = LocalDate.now();
//					LocalTime baseTime = LocalTime.MIDNIGHT;
//					long epochTime = currTime.getTime() + currDate.toEpochSecond(baseTime, ZoneOffset.UTC);
//					send(epochTime, currentFloor, dir, destFloor, "Reading");
//				}	catch(ParseException e) {
//					e.printStackTrace();
//				}
//				Thread.sleep(1000);
//			}
//			inReader.close();
//		}	catch (FileNotFoundException | InterruptedException e) {
//			System.out.println(Thread.currentThread().getName() + ": File not found");
//		}
	}
	
	public static void main(String args[]) {
		
	}
}
