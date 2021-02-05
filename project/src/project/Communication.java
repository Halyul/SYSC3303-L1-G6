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
    // if currentFloor == floor, means the door should open
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
    private Server s;
    
    /**
     * Reserve for UDP
     * @param address
     * @param port
     */
    public Communication(InetAddress address, int port) {
        
    }
    
    public Communication() {
        
    }
    
    public Communication(Server s) {
        this.s = s;
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
        String message = "role:" + role + ";time:" + time +";floor:" + floor + ";number:" + number + ";button:" + button + ";state:" + state + ";";
        byte[] messageBytes = message.getBytes();
        // should send the message to the host here
        s.put(messageBytes);
        return true;
    }
    
    /**
     * Receive a message from the host
     * @return true if the message is successfully received, false otherwise
     */
    public Boolean get() {
        // should get the message from the host
        // received message should not include role and number
        String b = "role:elevator;time:1612045259;floor:7;number:1;button:0;state:waiting;";
        byte[] a = b.getBytes();
        parse(a);
        return true;
    }

    /**
     * Parse the message
     * TODO: error handling
     * @param inputMessage the received message
     */
    public Boolean parse(byte[] inputMessage) {
        String message = new String(inputMessage);
        String[] messageArray = message.split(";");
        for (String item: messageArray) {
            String[] itemArray = item.split(":");
            String key = itemArray[0];
            String value = itemArray[1];
            if (key.equals("role")) {
                this.role = value;
            } else if (key.equals("time")) {
                this.time = Long.parseLong(value);
            } else if (key.equals("floor")) {
                this.floor = Integer.parseInt(value);
            } else if (key.equals("number")) {
                this.number = Integer.parseInt(value);
            } else if (key.equals("button")) {
                this.button = Integer.parseInt(value);
            } else if (key.equals("state")) {
                this.state = value;
            }
        }
        return true;
    }

    public String getRole() {
        return this.role;
    }

    public long getTime() {
        return this.time;
    }
    
    /**
     * get the destination floor the elevator needs to go to from the elevator
     * @return as described above
     */
    public int getFloor() {
        return this.floor;
    }

    public int getNumber() {
        return this.number;
    }

    public int getButton() {
        return this.button;
    }

    public String getState() {
        return this.state;
    }
}
