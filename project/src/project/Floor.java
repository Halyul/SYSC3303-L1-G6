package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Floor implements Runnable {
	//Current floor Number
	private int floorNumber;
	//Number of the top floor
	private int topFloor;
	
	private Sender sender;
	private Parser parser = new Parser();
	private DirectionLamp upLamp = new DirectionLamp();
	private DirectionLamp downLamp = new DirectionLamp();
	
	private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
	
	public Floor(int floorNumber, int topFloor, Database database) {
		this.floorNumber = floorNumber;
		this.topFloor = topFloor;
		this.sender = new Sender(database);
		//If bottom floor, initialize one set of up lamps and buttons
		if(floorNumber == 0) {
			FloorLamp upFLamp = new FloorLamp();
			FloorButton upButton = new FloorButton();
			DirectionLamp UpDirLamp = new DirectionLamp();
		}
		//If top floor, initialize one set of down lamps and buttons
		else if(floorNumber == topFloor) {
			FloorLamp downFLamp = new FloorLamp();
			FloorButton downButton = new FloorButton();
			DirectionLamp downDirLamp = new DirectionLamp();
		}
		//else, initialize up/down lamps and buttons
		else {
			FloorLamp upFLamp = new FloorLamp();
			FloorLamp downFLamp = new FloorLamp();
			
			FloorButton upButton = new FloorButton();
			FloorButton downButton = new FloorButton();
			
			DirectionLamp UpDirLamp = new DirectionLamp();
			DirectionLamp downDirLamp = new DirectionLamp();
		}
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
	
	private class FloorButton {
		
	}
	
	private class FloorLamp {
		
		public FloorLamp() {
			
		}
		
		private boolean currStatus = false;
		public void toggle() {
			
		}
	}
	/**
	 * Get message from the scheduler
	 */
    private void get() {
    	//If there is a message
    	if (this.messages.size() != 0) {
    		this.parser.parse(messages.get(0)); // Parse message
            messages.remove(0);
            //If the elevator is going up
            if (parser.getFloor() > this.floorNumber) {
            	System.out.println(Thread.currentThread().getName() + ": Direction lamp on floor "+this.floorNumber+" is pointing up");
            }
            //else if the elevator is going down
            else if (parser.getFloor() < this.floorNumber) {
            	System.out.println(Thread.currentThread().getName() + ": Direction lamp on floor "+this.floorNumber+" is down");
            }
            //Else the elevator is at its destination
            else {
            	System.out.println(Thread.currentThread().getName() + ": Elevator has arrived on floor "+this.floorNumber);
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
     * @param direction the direction the elevator is going (up/down)
     * @param CarButton The destination floor chosen by the passenger
     * @param state the status of the floor 
     */
	private void send(long time, int direction, int CarButton, String state) {
		Boolean isSent = false;
		while(!isSent) {
			isSent = sender.send("floor", this.floorNumber, this.floorNumber, direction, CarButton, time);
		}
	}
	
    /**
     * Reads an input file containing the time, floor, direction and destination 
     * of a passenger calling an elevator from a floor
     * @param inputFile name of the input file
     */
	public void ReadInput(String inputFile) {
		//Try opening the file of name inputFile
		try {
			File inFile = new File(inputFile);
			Scanner inReader = new Scanner(inFile);
			//While the text file has another line
			while (inReader.hasNextLine()) {		
				String ins = inReader.nextLine();		//Goes to the next line, storing the current line in ins
				String[] individualIns = ins.split("\\s+");		//Split the instructions at whitespace characters
				int dir = 0;
				if(individualIns[2].equals("Up"))	//If passengers wants to go up
					dir = 1;
				else if(individualIns[2].equals("Down")) //If passengers wants to go down
					dir = 0;
				int destFloor = Integer.parseInt(individualIns[3]);	//Stores destination floor 
				SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS.S");		//Used for epoch time conversion
				//Try parsing the time and converting it to epoch time
				try {
					Date currTime = time.parse(individualIns[0]);
					long epochTime = currTime.getTime();
					send(epochTime, dir, destFloor, "Reading");
				}	catch(ParseException e) {
					e.printStackTrace();
				}
			}
			
			inReader.close();
		}	catch (FileNotFoundException e) {
			System.out.println(Thread.currentThread().getName() + ": File not found");
		}
	}
}
