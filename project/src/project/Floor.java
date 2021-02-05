package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Floor implements Runnable {
	
	private int floorNumber;
	private int topFloor;
	private boolean elevatorPresent;
	
	private Communication c;
	
	private volatile ArrayList<byte[]> messages = new ArrayList<byte[]>();
	
	public Floor(int floorNumber, int topFloor, Server server) {
		this.floorNumber = floorNumber;
		this.topFloor = topFloor;
		c = new Communication(server);
		if(floorNumber == 0) {
			FloorLamp upFLamp = new FloorLamp();
			FloorButton upButton = new FloorButton();
			DirectionLamp UpDirLamp = new DirectionLamp();
		}
		else if(floorNumber == topFloor) {
			FloorLamp downFLamp = new FloorLamp();
			FloorButton downButton = new FloorButton();
			DirectionLamp downDirLamp = new DirectionLamp();
		}
		else {
			FloorLamp upFLamp = new FloorLamp();
			FloorLamp downFLamp = new FloorLamp();
			
			FloorButton upButton = new FloorButton();
			FloorButton downButton = new FloorButton();
			
			DirectionLamp UpDirLamp = new DirectionLamp();
			DirectionLamp downDirLamp = new DirectionLamp();
		}
	}
	public void run() {
		ReadInput("input.txt");
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
	
    private void get() {
    	if (this.messages.size() != 0) {
    		c.parse(messages.get(0));
            messages.remove(0);
            if (c.getFloor() == this.floorNumber) {
            	
            }
    	}
    }
    
	
	private void send(long time, int direction, int CarButton, String state) {
		Boolean isSent = false;
		while(!isSent) {
			isSent = c.send("floor", time, this.floorNumber, CarButton, direction, state);
		}
	}
	
	public void ReadInput(String inputFile) {
		try {
			File inFile = new File(inputFile);
			Scanner inReader = new Scanner(inFile);
			while (inReader.hasNextLine()) {
				String ins = inReader.nextLine();
				String[] individualIns = ins.split("\\s+");
				SimpleDateFormat time = new SimpleDateFormat("HH:MM:SS.S");
				int dir = 0;
				if(individualIns[2].equals("Up"))
					dir = 1;
				else if(individualIns[2].equals("Down"))
					dir = 0;
				int destFloor = Integer.parseInt(individualIns[3]);
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
			System.out.println("File not found");
		}
	}
}
