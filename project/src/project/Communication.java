package project;

import java.net.DatagramPacket;
import java.net.InetAddress;

/**
 * The data struct that communicate with each subsystem
 * @author Haoyu Xu
 *
 */
public class Communication {
	private InetAddress address;
	private int port;
	
	// role of the subsystem: "scheduler", "elevator", "floor"
	private String role;
	// time of the message
	private long time;
	// for elevator, the next floor the elevator should go to
	// for floor, 
	// if the value is 0, it is meant a status check
	private int floor;
	// the identity number
	private int number;
	// for elevator, the button pressed in the car, 0 for nothing
	// for floor, up -> 1, down -> 0
	private int button;
	// the state 
	// for elevator, "moving", "waiting"
	// for floor, 
	private String state;
	
	/**
	 * Reserve for UDP
	 * @param address
	 * @param port
	 */
	public Communication(InetAddress address, int port) {
		
	}
	
	public Communication() {
		
	}
	
	/**
	 * Send the message to the host.
	 * @param role role of the subsystem
	 * @param time time of the message
	 * @param floor see above
	 * @param number the identity number
	 * @param button see above
	 * @param state see above
	 * @return true if the message is successfully send, false otherwise
	 */
	public Boolean send(String role, long time, int floor, int number, int button, String state) {
		this.role = role;
		this.time = time;
		this.floor = floor;
		this.number = number;
		this.button = button;
		this.state = state;
		String message = "role:" + this.role + "time:" + this.time +";floor:" + this.floor + ";number:"
						+ this.number + ";button:" + this.button + ";state:" + this.state + ";";
		byte[] messageBytes = message.getBytes();
		// should send the message to the host here
		return true;
	}
	
	/**
	 * Receive a message from the host
	 * @return true if the message is successfully received, false otherwise
	 */
	public Boolean get() {
		// should get the message from the host
		// received message should not include role and number
		String b = "time:" + this.time +";floor:" + this.floor + ";";
		byte[] a = b.getBytes();
		String message = new String(a);
		String[] messageArray = message.split(";");
		for (String item: messageArray) {
			String[] itemArray = item.split(":");
			String key = itemArray[0];
			String value = itemArray[1];
			if (key.equals("floor")) {
				this.floor = Integer.parseInt(value);
			}
		}
		return true;
	}
	
	/**
	 * get the floor the elevator needs to go to from the elevator
	 * @return the floor the elevator should go to
	 */
	public int getFloor() {
		return this.floor;
	}
}
