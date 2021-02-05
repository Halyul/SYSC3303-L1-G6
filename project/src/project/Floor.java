package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class Floor implements Runnable {
	
	private int floorNumber;
	private int topFloor;
	private Communication c;
	
	
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
	
	public class DirectionLamp{
		
	}
	
	private void send(Boolean FloorButton, int CarButton, String state) {
		Boolean isSent = false;
		while(!isSent) {
			isSent = c.send("floor", time, floor, number, button, state);
		}
	}
	
	public ArrayList<String> ReadInput(String inputFile) {
		ArrayList<String> instructions = new ArrayList<String>();
		try {
			File inFile = new File(inputFile);
			Scanner inReader = new Scanner(inFile);
			while (inReader.hasNextLine()) {
				String ins = inReader.nextLine();
				instructions.add(ins);
			}
			
			inReader.close();
		}	catch (FileNotFoundException e) {
			System.out.println("File not found");
		}
		return instructions;
	}
}
