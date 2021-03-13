package project.utils;

public class Parser {
    // role of the subsystem: "scheduler", "elevator", "floor"
    private String role;
    // the identity number
    private int identifier;
    // for elevator, the next floor the elevator should go to
    // if currentFloor == floor, means the door should open
    // for floor, 
    // if the value is 0, it is meant a status check
    private String state;
    // up -> 1, down -> 0, undefined -> -1
    private int direction;
    // Sender.sendFloor
    private int floor;
    // time of the message
    private long time;
    // type of the sender
    private String type;
    // error message
    private String error;

    private byte[] ogBytes;

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
        this.ogBytes = inputMessage;
        String message = new String(inputMessage);
        logic(message);
        return true;
    }

    public Boolean parse(String inputMessage) {
        logic(inputMessage);
        return true;
    }

    private void logic(String message) {
        String[] messageArray = message.split(";");
        for (String item: messageArray) {
            String[] itemArray = item.split(":");
            String key = itemArray[0];
            String value = itemArray[1];
            if (key.equals("role")) {
                this.role = value;
            } else if (key.equals("id")) {
                this.identifier = Integer.parseInt(value);
            } else if (key.equals("state")) {
                this.state = value;
            } else if (key.equals("direction")) {
                this.direction = Integer.parseInt(value);
            } else if (key.equals("floor")) {
                this.floor = Integer.parseInt(value);
            } else if (key.equals("time")) {
                this.time = Long.parseLong(value);
            } else if (key.equals("type")) {
                this.type = value;
            } else if (key.equals("error")) {
                this.error = value;
            }
        }
    }

    /**
     * get the sender of the message
     * @return as described above
     */
    public String getRole() {
        return this.role;
    }

    /**
     * get the id of the sender
     * @return as described above
     */
    public int getIdentifier() {
        return this.identifier;
    }

    /**
     * get state of the sender
     * @return as described above
     */
    public String getState() {
        return this.state;
    }

    /**
     * get the direction
     * @return as described above
     */
    public int getDirection() {
        return this.direction;
    }

    /**
     * get the destination floor the elevator needs to go to from the elevator
     * @return as described above
     */
    public int getFloor() {
        return this.floor;
    }

    /**
     * get the time of the message
     * @return as described above
     */
    public long getTime() {
        return this.time;
    }

    /**
     * get the error if any
     * @return as described above
     */
    public String getError() {
        return this.error;
    }

    public byte[] formatMessage() {
        return this.ogBytes;
    }
}
