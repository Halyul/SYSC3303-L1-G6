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
    
    public Receiver(InetAddress schedulerAddress, int port) {
		this.schedulerAddress = schedulerAddress;
		this.schedulerPort = port;
		try {
			this.receiveSocket = new DatagramSocket(port + 100);
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
	}
	
	private void parse() {
		byte message[] = this.receivePacket.getData();
		System.out.println(new String(message));
		floor.put(message);
	}
    
    @Override 
    public void run() {
    	while(true) {
    		execute();
    	}
    }
}