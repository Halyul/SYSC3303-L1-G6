package project.floor.src;

import project.utils.*;

import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;


public class GUI implements Runnable {
	//Array containing JLabels to be changed via info, each elevators respective info
	//is stored at index (elevatorNumber-1)*2
	private volatile ArrayList<JLabel> ElevatorInfo = new ArrayList<JLabel>();
	private volatile static ArrayList<byte[]> messages = new ArrayList<byte[]>();
	
	private Parser parser = new Parser();
	/*
	 * GUI constructor
	 * @param numberOfElevators number of elevators in the system
	 */
	public GUI(int numberOfElevators) {
    	JFrame frame = new JFrame("Concierge System");
    	frame.setLayout(new GridLayout(numberOfElevators, 3));
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(350,500);
    	frame.setVisible(true);
    	for(int i = 1; i <= numberOfElevators; i++) {
    		if(i == 1) {
        		frame.add(new JLabel("Elevator Number"));
        		frame.add(new JLabel("Current Floor"));
        		frame.add(new JLabel("Error"));
    		}
    		else {
        		JLabel elevatorNum = new JLabel("Elevator " + i);
        		JLabel currFloor = new JLabel("1");
        		JLabel errorType = new JLabel("N/A");
        		this.ElevatorInfo.add(currFloor);
        		this.ElevatorInfo.add(errorType);
        		frame.add(elevatorNum);
        		frame.add(currFloor);
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
	 * Used to update gui dynamically. If there is information relevant
	 * to the GUI in the message list, updates JLabels to reflect this info
	 */
	private void updateGUI() {
		if(messages.size() != 0) {
			this.parser.parse(messages.get(0));
			messages.remove(0);
			if(parser.getRole().equals("Elevator")) {
				int elevatorNum = parser.getIdentifier() - 1;
				//Set current floor number
				this.ElevatorInfo.get(elevatorNum*2).setText(""+parser.getFloor());
				if(!parser.getError().equals("")) {
					//Set error
					this.ElevatorInfo.get((elevatorNum*2)+1).setText(""+parser.getError());
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
