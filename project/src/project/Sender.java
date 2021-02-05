package project;

import java.net.InetAddress;

public class Sender {
	private InetAddress address;
    private int port;
    
    private Database database;
    
	/**
     * Reserve for UDP
     * @param address
     * @param port
     */
    public Sender(InetAddress address, int port) {
        
    }
    
    public Sender(Database database) {
        this.database = database;
    }
    
    /**
     * Send the message to the host.
     * @param role the role of the subsystem
     * @param time epoch seconds of the message
     * @param floor for elevator, the next floor the elevator should go to
     * 					if currentFloor == floor, means the door should open
     * 				for floor, 
     * @param identifier the identity number
     * @param button for elevator, the button pressed in the car, 0 for nothing
     * 				 for floor, up -> 1, down -> 0
     * @param state for elevator, "moving", "waiting"
     * 				for floor, 
     * @return true if the message is successfully send, false otherwise
     */
    public Boolean send(String role, long time, int floor, int identifier, int button, String state) {
        String message = "role:" + role + ";time:" + time +";floor:" + floor + ";identifier:" + identifier + ";button:" + button + ";state:" + state + ";";
        byte[] messageBytes = message.getBytes();
        // should send the message to the host here
        database.put(messageBytes);
        return true;
    }
}
