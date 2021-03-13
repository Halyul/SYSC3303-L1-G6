package project.scheduler.src;


import java.io.*;
import java.net.*;
import java.util.ArrayList;
import project.utils.*;
import project.scheduler.Scheduler;

public class Receiver implements Runnable {
	private Database db;
    private DatagramSocket receiveSocket;
    private DatagramPacket receivePacket;
    private boolean isDebug;
    private byte[] debugMessage;
	
	public Receiver(Database db, int port) {
		this.db = db;
		try {
			this.receiveSocket = new DatagramSocket(port);
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
		byte[] data = new byte[5000];
	    this.receivePacket = new DatagramPacket(data, data.length);
	    try {
	    	this.receiveSocket.receive(this.receivePacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
	}
	
	/**
	 * Parse a message
	 */
	private void parse() {
		byte[] message = this.receivePacket.getData();
		if (!this.isDebug) {
			this.db.put(message);
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