package project.floor;

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
import java.time.LocalTime;
import java.time.ZoneOffset;

import project.utils.*;

public class Floor implements Runnable {
	//Current floor Number
	private int floorNumber;
	//Number of the top floor
	private int topFloor;
	
	private Sender sender;
	private Parser parser = new Parser();
	
	private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
	private volatile ArrayList<String> upDownButtons = new ArrayList<String>();
	private FloorButton upButton;
	private FloorButton downButton;
	private DirectionLamp upLamp;
	private DirectionLamp downLamp;

	
	public Floor(int floorNumber, int topFloor, Database database) {
		this.floorNumber = floorNumber;
		this.topFloor = topFloor;
		this.sender = new Sender(database);
		//If bottom floor, initialize one set of up lamps and buttons
		if(floorNumber == 0) {
			FloorButton upButton = new FloorButton();
		}
		//If top floor, initialize one set of down lamps and buttons
		else if(floorNumber == topFloor) {
			FloorButton downButton = new FloorButton();
		}
		//else, initialize up/down lamps and buttons
		else {
			FloorButton upButton = new FloorButton();
			FloorButton downButton = new FloorButton();
		}
		
		DirectionLamp upLamp = new DirectionLamp();
		DirectionLamp downLamp = new DirectionLamp();
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
	 * Get message from the scheduler
	 */
    private void get() {
    	//If there is a message
    	if (this.messages.size() != 0) {
    		this.parser.parse(messages.get(0)); // Parse message
            messages.remove(0);
            //if elevator reaches destination floor
            if (parser.getRole().equals("Elevator") && parser.getState().equals("Stop") && parser.getFloor() == this.floorNumber) {
            	int arraySize = upDownButtons.size();
            	for(int i = 0; i < arraySize; i++) {
            		if(this.upDownButtons.get(i).startsWith("Floor: "+this.floorNumber)) {
            			if(this.floorNumber != topFloor && this.floorNumber != 0) {
                			this.upButton.off();
                			this.downButton.off();
            			}
            			else if(this.floorNumber == topFloor) {
            				this.downButton.off();
            			}
            			else if(this.floorNumber == 0) {
            				this.upButton.off();
            			}
            			this.upDownButtons.remove(i);
            			break;
            		}
            	}
            }
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
    
    
    private void storeButtons(String floorAndDirection) {
		if(this.upDownButtons.size() == 0) {
			this.upDownButtons.add(floorAndDirection);
		}
		else {
			Boolean duplicate = false;
			int i = 0;
			while(!duplicate) {
				if(this.upDownButtons.get(i).equals(floorAndDirection)) {
					duplicate = true;
				}
				else if(i == this.upDownButtons.size()) {
					this.upDownButtons.add(floorAndDirection);
					duplicate = true;
				}
				i++;
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
		// Haoyu Xu: updated Sender
		// role:Floor;id:<current floor number>;state:<your own definition>;direction:<1/up or 0/down>;floor:<button pressed in the car>;time:<time>
		String revMsg = sender.sendInput(this.floorNumber, state, direction, CarButton, time);
//		while(!isSent) {
//			isSent = sender.send("floor", this.floorNumber, this.floorNumber, direction, CarButton, time);
//		}
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
				if(individualIns[2].equals("Up")) {	//If passengers wants to go up
					dir = 1;
					if(this.floorNumber != topFloor)
						this.upButton.on();
					String floorAndDir = "Floor: "+ individualIns[1] + ", Up" ;
					storeButtons(floorAndDir);
				}
				else if(individualIns[2].equals("Down")) { //If passengers wants to go down
					dir = 0;
					if(this.floorNumber != 0)
						this.downButton.on();
					String floorAndDir = "Floor: "+ individualIns[1] + ", Down";
					storeButtons(floorAndDir);
				}

				int destFloor = Integer.parseInt(individualIns[3]);	//Stores destination floor 
				SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS.S");		//Used for epoch time conversion
				//Try parsing the time and converting it to epoch time
				try {
					Date currTime = time.parse(individualIns[0]);
					LocalDate currDate = LocalDate.now();
					LocalTime baseTime = LocalTime.MIDNIGHT;
					long epochTime = currTime.getTime() + currDate.toEpochSecond(baseTime, ZoneOffset.UTC);
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
