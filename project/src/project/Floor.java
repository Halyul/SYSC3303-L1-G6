package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Floor implements Runnable {
	
	private int floorNumber;
	private int topFloor;
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

	}
	
	private class FloorButton {
		
	}
	
	private class FloorLamp {
		private boolean currStatus = false;
		
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
				long convertedTime = Long.parseLong(individualIns[0].replaceAll("[^0-9]", ""));
				int dir = 0;
				if(individualIns[2].equals("Up"))
					dir = 1;
				else if(individualIns[2].equals("Down"))
					dir = 0;
				int destFloor = Integer.parseInt(individualIns[3]);
				send(convertedTime, dir, destFloor, "reading");
			}
			
			inReader.close();
		}	catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
	}
}
