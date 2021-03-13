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
	}
	/**
	 * Send the message to the host.
	 * @param role the role of the subsystem
	 * @param identifier the identity number
	 * @param time epoch seconds of the message
	 * @return true if the message is successfully send, false otherwise
	 */

	public String sendFloor(String role, int identifier, String state, int floor, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";floor:" + floor + ";time:" + time + ";";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendElevatorState(String role, int identifier, String state, int floor, int direction, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";floor:" + floor + ";direction:" + direction + ";time:" + time + ";";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	/**
	 * Used by Floor subsystem
	 * @return the message replied by the scheduler
	 */
	public String sendInput(int identifier, String state, int direction, int floor, long time, InetAddress address, int port) {
		String message = "role:Floor;id:" + identifier + ";state:" + state + ";direction:" + direction + ";floor:" + floor + ";time:" + time + ";";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	public String sendError(String role, int identifier, String error, int floor, long time, InetAddress address, int port) {
		String message = "role:" + role + ";id:" + identifier + ";error:" + error + ";floor:" + floor + ";time:" + time + ";";
		Boolean isSent = send(message, address, port);
		String revMessage = receive();
		return revMessage;
	}

	private Boolean send(String message, InetAddress address, int port) {
		byte[] messageBytes = message.getBytes();
//    	System.out.println(message);
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
//    	if (!this.isDebug) {
//		    try {
//		    	this.sendReceiveSocket.receive(receivePacket);
//		    } catch(IOException e) {
//		    	e.printStackTrace();
//		    	System.exit(1);
//		    }
//
//		    Parser p = new Parser(data);
//
//	    	return p.getState();
//    	} else {
//    		return "state:Received";
//    	}
		return "state:Received;";
	}
}

