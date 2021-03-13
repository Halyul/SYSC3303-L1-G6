package project.floor.src;

import project.floor.Floor;
import java.io.*;
import java.net.*;

public class Receiver implements Runnable {
	private InetAddress schedulerAddress;
	private Floor floor;
    private int schedulerPort;
    private DatagramSocket receiveSocket;
    private DatagramPacket receivePacket;
    
    public Receiver(Floor floor, int port) {
    	this.floor = floor;
		try {
			this.receiveSocket = new DatagramSocket(port + 200);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
    
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
	
	private void parse() {
		int messageLength = receivePacket.getLength();
		byte[] message = new byte[messageLength];
		if (messageLength >= 0)
			System.arraycopy(receivePacket.getData(), 0, message, 0, messageLength);        // Intercept the required part
//		System.out.println(new String(message));
		floor.put(message);
	}
    
    @Override 
    public void run() {
    	while(true) {
    		execute();
    	}
    }
}
