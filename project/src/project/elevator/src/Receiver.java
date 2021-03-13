package project.elevator.src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import project.utils.*;
import project.elevator.Elevator;

public class Receiver implements Runnable {
	private ArrayList<Elevator> elevators;
    private DatagramSocket receiveSocket;
    private DatagramPacket receivePacket;
    private boolean isDebug;
    private byte[] debugMessage;
	
	public Receiver(ArrayList<Elevator> elevators, int port) {
		this.elevators = elevators;
		try {
			this.receiveSocket = new DatagramSocket(port + 100);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public Receiver(boolean isDebug) {
		this.isDebug = isDebug;
	}
	
	/**
	 * Receive a message
	 */
	private void execute() {
		byte data[] = new byte[5000];
	    this.receivePacket = new DatagramPacket(data, data.length);
	    try {
	    	this.receiveSocket.receive(this.receivePacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	    parse();
	}
	
	/**
	 * Parse a message
	 */
	private void parse() {
		int messageLength = receivePacket.getLength();
		byte[] message = new byte[messageLength];
		if (messageLength >= 0)
			System.arraycopy(receivePacket.getData(), 0, message, 0, messageLength);        // Intercept the required part
//		System.out.println(new String(message));
		Parser p = new Parser(message);
		int id = p.getIdentifier();
		if (!this.isDebug) {
			this.elevators.get(id - 1).put(message);
		} else {
			this.debugMessage = message;
		}
	}
	
	/**
	 * For unit test only, generate a new packet from messageBytes and parse it
	 * @param messageBytes the input message
	 * @return parse message
	 */
	public byte[] debug(byte[] messageBytes) {
		this.receivePacket = new DatagramPacket(messageBytes, messageBytes.length);
		parse();
		return this.debugMessage;
		
	}

	@Override
	public void run() {
		while(true) {
			execute();
		}
	}
}
