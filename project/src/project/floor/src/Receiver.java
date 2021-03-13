package project.floor.src;

import project.floor.Floor;
import java.io.*;
import java.net.*;

public class Receiver implements Runnable {
	private Floor floor;
    private DatagramSocket receiveSocket;
    private DatagramPacket receivePacket;
    private boolean isDebug;
    private byte[] debugMessage;
    
    public Receiver(Floor floor, int port) {
    	this.floor = floor;
		try {
			this.receiveSocket = new DatagramSocket(port + 200);
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
	    	parse();
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
		System.out.println(new String(message));
		if (!this.isDebug) {
			floor.put(message);
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
