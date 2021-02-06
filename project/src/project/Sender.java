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
     * TODO: maybe remove param state, use direction instead
     * @param role the role of the subsystem
     * @param identifier the identity number
     * @param floor for elevator, the next floor the elevator should go to
     * 					if currentFloor == floor, means the door should open
     * 				for floor, the current floor
     * @param direction up -> 1, down -> 0, undefined -> -1
     * @param button for elevator, the button pressed in the car, 0 for nothing
     * @param time epoch seconds of the message
     * @param state for elevator, "moving", "waiting"
     * 				for floor, 
     * @return true if the message is successfully send, false otherwise
     */
    public Boolean send(String role, int identifier, int floor, int direction, int button, long time, String state) {
        String message = "role:" + role + ";identifier:" + identifier + ";floor:" + floor + ";direction:" + direction + ";button:" + button + ";time:" + time + ";state:" + state + ";";
        byte[] messageBytes = message.getBytes();
        // should send the message to the host here
        database.put(messageBytes);
        return true;
    }
}
