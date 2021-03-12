package project.floor;

import java.net.*;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import project.utils.*;
import project.floor.src.Receiver;
import project.floor.src.*;

public class Floor implements Runnable{
	//Number of the top floor
	private int topFloor;
	private double baseTime = 0;

	private Sender sender = new Sender();
	private Parser parser = new Parser();

	private InetAddress schedulerAddress;
	private int schedulerPort;
	
	private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
	private ArrayList<FloorButton> floorButtons = new ArrayList<FloorButton>();
	private ArrayList<DirectionLamp> dirLamps = new ArrayList<DirectionLamp>();
	
	/**
	 * Floor constructor
	 * @param topFloor number of the top floor
	 * @param schedulerAddress address of the scheduler that the system will send messages to
	 * @param port Floor systems port number
	 * 
	 */
	public Floor(int topFloor, InetAddress schedulerAddress, int port) {
		this.topFloor = topFloor;
		//Doing this will make it so that the length of the FloorButton array will always be (topFloor*2) - 2
		for(int i = 0; i < this.topFloor; i++) {	
			if(i == 0)	//If bottom floor
				floorButtons.add(new FloorButton(i+1, true));	//Only add up button
			else if(i == this.topFloor-1)	//if top floor
				floorButtons.add(new FloorButton(i+1, false));	//Only add down button
			else {	//Else
				//Add up and down button
				floorButtons.add(new FloorButton(i+1, true));
				floorButtons.add(new FloorButton(i+1, false));
			}
			dirLamps.add(new DirectionLamp(i+1, true));
			dirLamps.add(new DirectionLamp(i+1, false));
			
		}
		this.schedulerAddress = schedulerAddress;
		this.schedulerPort = port;
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
			int currFloor = parser.getFloor();
			if (parser.getRole().equals("Elevator") && parser.getState().equals("Stop")) {
				
				if(currFloor == 1)// If bottom floor
					floorButtons.get(0).off();
				else if(currFloor == this.topFloor)	//If top floor
					floorButtons.get((currFloor*2)-3).off();
				else {
					floorButtons.get((currFloor*2)-3).off();
					floorButtons.get((currFloor*2)-2).off();
				}
			}
			//Following ifs set the direction of the DirectionLamp
			if(parser.getRole().equals("Elevator")) {
				for(int i = 0; i < (currFloor*2)-2; i++) {

					if(parser.getDirection() == 1) {
						if(dirLamps.get(i).getDirection())
							dirLamps.get(i).on();
						else
							dirLamps.get(i).off();
					}
					else if(parser.getDirection() == 0) {
						if(dirLamps.get(i).getDirection())
							dirLamps.get(i).off();
						else
							dirLamps.get(i).on();
					}
					else {
						dirLamps.get(i).off();
					}
				}
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
		// Haoyu Xu: updated Sender
		// role:Floor;id:<current floor number>;state:<your own definition>;direction:<1/up or 0/down>;floor:<button pressed in the car>;time:<time>
		String revMsg = sender.sendInput(currentFloor, state, direction, CarButton, time, schedulerAddress, this.schedulerPort);
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
		
		return (hour * 60) + minute + (seconds / 100); //Returns the time in a format where one hour = 60 seconds, one minute = 1 second and 1 second = 0.01 second
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
				int currentFloor = Integer.parseInt(individualIns[1]); // get the floor the user currently at
				if(individualIns[2].equals("Up")) {	//If passengers wants to go up
					dir = 1;
					if(currentFloor != topFloor && currentFloor != 1) { //If not top of bottom floor
						if(!floorButtons.get((currentFloor*2)-3).getState()) {
							//Checks if the button is already on
							floorButtons.get((currentFloor*2)-3).on();
						}
					}
					else {
						if(!floorButtons.get(0).getState()) {
							//Checks if the button is already on
							floorButtons.get(0).on();
						}
					}
				}
				else if(individualIns[2].equals("Down")) { //If passengers wants to go down
					dir = 0;
					if(currentFloor != topFloor && currentFloor != 1) { //If not top of bottom floor
						if(!floorButtons.get((currentFloor*2)-2).getState()) {	//Checks if the button is already on
							floorButtons.get((currentFloor*2)-2).on();
						}
					}
					else {
						if(!floorButtons.get((currentFloor*2)-3).getState()) { //Checks if the button is already on
							floorButtons.get((currentFloor*2)-3).on();
						}
					}
				}
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
	}
	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		ReadInput("floorInput.txt");
		while(true) {
			get();
			
		}
	}
	
	public static void main(String args[]) {
		InetAddress schedulerAddress = null;
	   	try {
	   		schedulerAddress = InetAddress.getLocalHost();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.exit(1);
			}
	   	Floor floor = new Floor(7, schedulerAddress, 12000);
	   	Thread floorThread = new Thread(floor, "Floor");
	   	floorThread.start();
	   	Receiver r = new Receiver(schedulerAddress, 12000);
	   	Thread receiverThread = new Thread(r, "Receiver");
	   	receiverThread.start();
	}
}
