package project.floor.src;

import project.utils.*;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class GUI implements Runnable {
	//Array containing JLabels to be changed via info, each elevators respective info
	//is stored at index (elevatorNumber-1)*4 +1, 2, 3
	private volatile ArrayList<JLabel> ElevatorInfo = new ArrayList<JLabel>();
	//Stores messages received from scheduler
	private volatile static ArrayList<byte[]> messages = new ArrayList<byte[]>();
	//Stores previous direction of elevator
	private volatile ArrayList<Integer> prevDir = new ArrayList<Integer>();
	
	private Parser parser = new Parser();
	/*
	 * GUI constructor
	 * @param numberOfElevators number of elevators in the system
	 */
	public GUI(int numberOfElevators) {
    	JFrame frame = new JFrame("Concierge System");
    	frame.setLayout(new GridLayout(numberOfElevators+1, 5));	//Creates rows based on number of elevators
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		//Close program on clicking X
    	frame.setSize(800,500);		//Set size
    	frame.setVisible(true);		//Makes GUI visible
    	for(int i = 0; i <= numberOfElevators; i++) {
    		if(i == 0) {	//If first row, add titles
        		frame.add(new JLabel("Elevator Number"));
        		frame.add(new JLabel("Current Floor"));
        		frame.add(new JLabel("State"));
        		frame.add(new JLabel("Direction"));
        		frame.add(new JLabel("Error"));
    		}
    		else {	//Else, add pertinent information for each elevator
        		JLabel elevatorNum = new JLabel("Elevator " + i);
        		JLabel currFloor = new JLabel("1");
        		JLabel currState = new JLabel("Idle");
        		JLabel currDirection = new JLabel("No direction");
        		JLabel errorType = new JLabel("N/A");
        		this.ElevatorInfo.add(currFloor);
        		this.ElevatorInfo.add(currState);
        		this.ElevatorInfo.add(currDirection);
        		this.prevDir.add(-1);
        		this.ElevatorInfo.add(errorType);
        		frame.add(elevatorNum);
        		frame.add(currFloor);
        		frame.add(currState);
        		frame.add(currDirection);
        		frame.add(errorType);
    		}
    	}
	}
	/*
	 * put() Stores message in a byte array 
	 * @param inputMessage message in question
	 */
	public static void put(byte[] inputMessage) {
		messages.add(inputMessage);
	}
	/*
	 * currLabel() Used to select wanted string from jlabel
	 * for JUnit test
	 * @param elevatorNum elevator identifier
	 * @param wantedText 0 if floor, 1 if state, 2 if Direction, 3 if error
	 * @return String from JLabel
	 */
	public String currLabel(int elevatorNum, int wantedText) {
		switch(wantedText) {
			case 0:	//Floor
				return this.ElevatorInfo.get(elevatorNum*4).getText();
			case 1:	//State
				return this.ElevatorInfo.get((elevatorNum*4)+1).getText();
			case 2:	//Direction
				return this.ElevatorInfo.get((elevatorNum*4)+2).getText();
			case 3:	//Error
				return this.ElevatorInfo.get((elevatorNum*4)+3).getText();
			default:
				return "Wrong parameter";
		}
	}
	/*
	 * Resolves error message into a human readable format
	 * @param error error message
	 */
	private String resolveError(String error) {
		String readableError = "";
		switch(error){
		case "stuckBetweenFloors":
			readableError = "Elevator is stuck between two floors";
			break;
		case "arrivalSensorFailed":
			readableError = "Arrival sensor failed";
			break;
		case "doorStuckAtOpen":
			readableError = "Elevator doors stuck open";
			break;
		case "doorStuckAtClose":
			readableError = "Elevator doors stuck closed";
			break;
		case "schedulerReportedError":
			readableError = "Scheduler reported an error";
			break;
		}
		return readableError;
	}
	
	/*
	 * Used to update gui dynamically. If there is information relevant
	 * to the GUI in the message list, updates JLabels to reflect this info
	 */
	public synchronized void updateGUI() {
		if(messages.size() != 0) {	//If message in queue
			this.parser.parse(messages.get(0));	//Parse
			messages.remove(0);	//remove message from queue
			if(parser.getRole().equals("Elevator")) {	//If message is from elevator
				int elevatorNum = parser.getIdentifier() - 1;
				int elevatorDir = parser.getDirection();
				String readableDir = "";
				
				//Set current floor number
				this.ElevatorInfo.get(elevatorNum*4).setText(""+parser.getFloor());	//update floor
				this.ElevatorInfo.get((elevatorNum*4)+1).setText(""+parser.getState());	//update state

				if(this.prevDir.get(elevatorNum) != elevatorDir) {	//If direction of the elevator has changed
					switch(elevatorDir) {
					case 0:
						readableDir = "Down";
						break;
					case 1:
						readableDir = "Up";
						break;
					default:
						readableDir = "No direction";
						break;
					}
					this.prevDir.set(elevatorNum, elevatorDir);
					this.ElevatorInfo.get((elevatorNum*4)+2).setText(readableDir);	//Update direction on GUI
				}
				//If the message contains an error
				if(parser.getState().equals("Error")) {	
					//Set error
					this.ElevatorInfo.get((elevatorNum*4)+3).setText(resolveError(parser.getError()));
				}
			}
		}
	}
	
	@Override
	public void run() {
		while(true) {
			updateGUI();
		}
	}
}
