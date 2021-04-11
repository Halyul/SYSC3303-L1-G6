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
	private volatile static ArrayList<byte[]> messages = new ArrayList<byte[]>();
	
	private Parser parser = new Parser();
	/*
	 * GUI constructor
	 * @param numberOfElevators number of elevators in the system
	 */
	public GUI(int numberOfElevators) {
    	JFrame frame = new JFrame("Concierge System");
    	frame.setLayout(new GridLayout(numberOfElevators+1, 5));
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(800,500);
    	frame.setVisible(true);
    	for(int i = 0; i <= numberOfElevators; i++) {
    		if(i == 0) {
        		frame.add(new JLabel("Elevator Number"));
        		frame.add(new JLabel("Current Floor"));
        		frame.add(new JLabel("State"));
        		frame.add(new JLabel("Direction"));
        		frame.add(new JLabel("Error"));
    		}
    		else {
        		JLabel elevatorNum = new JLabel("Elevator " + i);
        		JLabel currFloor = new JLabel("1");
        		JLabel currState = new JLabel("Idle");
        		JLabel currDirection = new JLabel("No direction");
        		JLabel errorType = new JLabel("N/A");
        		this.ElevatorInfo.add(currFloor);
        		this.ElevatorInfo.add(currState);
        		this.ElevatorInfo.add(currDirection);
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
	private void updateGUI() {
		if(messages.size() != 0) {
			this.parser.parse(messages.get(0));
			messages.remove(0);
			if(parser.getRole().equals("Elevator")) {
				int elevatorNum = parser.getIdentifier() - 1;
				int elevatorDir = parser.getDirection();
				String readableDir = "";
				
				//Set current floor number
				this.ElevatorInfo.get(elevatorNum*4).setText(""+parser.getFloor());
				this.ElevatorInfo.get((elevatorNum*4)+1).setText(""+parser.getState());
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
				this.ElevatorInfo.get((elevatorNum*4)+2).setText(readableDir);
				
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
