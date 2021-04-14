package project.floor.src;

public class DirectionLamp {
    // light status
    private Boolean isOn = false;
    private int floor;
    private boolean dir;	//True for up, false for down
    
	/**
	*	DirectionLamp constructor
	*	@param floor floor which the directionLamp is found at
	*	@param dir true if up, false if down
	*/
    public DirectionLamp(int floor, boolean dir) {
		this.floor = floor;
		this.dir = dir;
    }
    
    /**
     * on() usedto turn on the light
     */
    public void on() {
        this.isOn = true;
    }
    
    /**
     * off() used to turn off the light
     */
    public void off() {
        this.isOn = false;
    }
    /**
     * getDirection() used to get the direction of the lamp
     *
     * @return direction of the lamp
     */
    public boolean getDirection(){
    	return this.dir;
    }
    /**
     * getFloor() used to get the floor where the lamp is
     *
     * @return floor of the lamp
     */
    public int getFloor() {
    	return this.floor;
    }
    
    /**
     * Get the current state of the lamp
     * @return as described above
     */
    public boolean getState() {
    	return this.isOn;
    }
}
