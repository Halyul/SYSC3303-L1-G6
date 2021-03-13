package project.utils;

import java.io.*;
import java.net.*;

import project.utils.Database;

public class Sender {
	// udp retry times
	private int retryTimes;
	private boolean isDebug;
	private DatagramSocket sendReceiveSocket;

	/**
	 * Reserve for UDP
	 * @param address
	 * @param port
	 */
	public Sender() {
		try {
			this.sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
	    }
	}
	
	public Sender(boolean isDebug) {
		this.isDebug = isDebug;
		try {
			this.sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
	    }
	}
	/**
	 * Send the message to the host.
	 * @param role the role of the subsystem
	 * @param identifier the identity number
	 * @param floor for elevator, the next floor the elevator should go to
	 * 					if currentFloor == floor, means the door should open
	 * 				for floor, the current floor
	 * @param direction up -> 1, down -> 0, undefined -> -1
	 * @param button for elevator, the button pressed in the car, 0 for nothing
	 * @param time epoch seconds of the message
	 * @return true if the message is successfully send, false otherwise
	 */

	public String sendState(String role, int identifier, String state, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";time:" + time + ";type:sendState;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendDirection(String role, int identifier, String state, int direction, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";direction:" + direction + ";time:" + time + ";type:sendDirection;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendFloor(String role, int identifier, String state, int floor, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";floor:" + floor + ";time:" + time + ";type:sendFloor;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendEelvatorState(String role, int identifier, String state, int floor, int direction, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";floor:" + floor + ";direction:" + direction + ";time:" + time + ";type:sendEelvatorState;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	/**
	 * Used by Floor subsystem
	 * @return the message replied by the scheduler
	 */
	public String sendInput(int identifier, String state, int direction, int floor, long time, InetAddress address, int port) {
		String message = "role:Floor;id:" + identifier + ";state:" + state + ";direction:" + direction + ";floor:" + floor + ";time:" + time + ";type:sendInput;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendError(String role, int identifier, String error, int floor, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";error:" + error + ";floor:" + floor + ";time:" + time + ";type:sendError;";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	private Boolean send(String message, InetAddress address, int port) {
		byte[] messageBytes = message.getBytes();
    	System.out.println(message);
    	DatagramPacket sendPacket = new DatagramPacket(messageBytes, messageBytes.length, address, port);
    	
    	if (!this.isDebug) {
    		try {
        		this.sendReceiveSocket.send(sendPacket);
    	    } catch (IOException e) {
    	    	e.printStackTrace();
    	    	System.exit(1);
    	    }
    	}
		
		return true;
	}

	private String receive() {
		byte data[] = new byte[100];
    	DatagramPacket receivePacket = new DatagramPacket(data, data.length);
    	if (!this.isDebug) {
		    try {
		    	this.sendReceiveSocket.receive(receivePacket);
		    } catch(IOException e) {
		    	e.printStackTrace();
		    	System.exit(1);
		    }
		    
		    Parser p = new Parser(data);
		    
	    	return p.getState();
    	} else {
    		return "state:Received";
    	}
	}
}

