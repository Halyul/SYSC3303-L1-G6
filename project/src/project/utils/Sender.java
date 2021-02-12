package project.utils;

import java.net.InetAddress;

import project.utils.Database;

public class Sender {
    private InetAddress address;
    private int port;
    // udp retry times
    private int retryTimes;
    
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
     * @param identifier the identity number
     * @param floor for elevator, the next floor the elevator should go to
     * 					if currentFloor == floor, means the door should open
     * 				for floor, the current floor
     * @param direction up -> 1, down -> 0, undefined -> -1
     * @param button for elevator, the button pressed in the car, 0 for nothing
     * @param time epoch seconds of the message
     * @return true if the message is successfully send, false otherwise
     */
    
    public String sendState(String role, int identifier, String state, long time) {
    	String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";time:" + time + ";type:sendState;";
    	Boolean isSent = send(message);
    	String revMessage = receive();
    	return revMessage;
    }
    
    public String sendDirection(String role, int identifier, String state, int direction, long time) {
    	String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";direction:" + direction + ";time:" + time + ";type:sendDirection;";
    	Boolean isSent = send(message);
    	String revMessage = receive();
    	return revMessage;
    }
    
    public String sendFloor(String role, int identifier, String state, int floor, long time) {
    	String message = "role:" + role + ";id:" + identifier + ";state:" + state + ";floor:" + floor + ";time:" + time + ";type:sendFloor;";
    	Boolean isSent = send(message);
    	String revMessage = receive();
    	return revMessage;
    }
    
    /**
     * Used by Floor subsystem
     * @return the message replied by the scheduler
     */
    public String sendInput(int identifier, String state, int direction, int floor, long time) {
    	String message = "role:Floor;id:" + identifier + ";state:" + state + ";direction:" + direction + ";floor:" + floor + ";time:" + time + ";type:sendFloor;";
    	Boolean isSent = send(message);
    	String revMessage = receive();
    	return revMessage;
    }
    
    public String sendError(String role, int identifier, String error, int floor, long time) {
    	String message = "role:" + role + ";id:" + identifier + ";error:" + error + ";floor:" + floor + ";time:" + time + ";type:sendError;";
    	Boolean isSent = send(message);
    	String revMessage = receive();
    	return revMessage;
    }
    
    private Boolean send(String message) {
    	byte[] messageBytes = message.getBytes();
    	database.put(messageBytes);
        return true;
    }
    
    private String receive() {
    	return "";
    }
}

