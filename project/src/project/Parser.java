package project;

public class Parser {
    // role of the subsystem: "scheduler", "elevator", "floor"
    private String role;
    // the identity number
    private int identifier;
    // for elevator, the next floor the elevator should go to
    // if currentFloor == floor, means the door should open
    // for floor, 
    // if the value is 0, it is meant a status check
    private int floor;
    // up -> 1, down -> 0, undefined -> -1
    private int direction;
    // for elevator, the button pressed in the car, 0 for nothing
    private int button;
    // time of the message
    private long time;
    
    public Parser() {
        
    }
    
    public Parser(byte[] inputMessage) {
        parse(inputMessage);
    }
    
    /**
     * Parse the message
     * TODO: error handling
     * @param inputMessage the received message
     * @return if the input message is successfully parsed
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
            } else if (key.equals("identifier")) {
                this.identifier = Integer.parseInt(value);
            } else if (key.equals("button")) {
                this.button = Integer.parseInt(value);
            } else if (key.equals("direction")) {
                this.direction = Integer.parseInt(value);
            }
        }
        return true;
    }
    
    /**
     * get the sender of the message
     * @return as described above
     */
    public String getRole() {
        return this.role;
    }
    
    /**
     * get the time of the message
     * @return as described above
     */
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
    
    /**
     * get the id of the sender
     * @return as described above
     */
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * get the button pressed in the car
     * @return as described above
     */
    public int getButton() {
        return this.button;
    }
    
    /**
     * get the direction
     * @return as described above
     */
    public int getDirection() {
        return this.direction;
    }
}
