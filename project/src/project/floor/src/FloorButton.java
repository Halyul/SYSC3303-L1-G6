package project.floor.src;

public class FloorButton {
	//FloorLamp = 1 if on, 0 if off
	private boolean FloorLamp = false;
	private int floor;
	private boolean dir;	//True for up, false for down
	/**
	*	FloorButton constructor
	*	@param floor floor which the floor buttons are found at
	*	@param dir true if up, false if down
	*/
	public FloorButton(int floor, boolean dir) {
		this.floor = floor;
		this.dir = dir;
	}
    /**
     * on() used to turn on the floor light
     */
    public void on() {
        this.FloorLamp = true;
    }
    
    /**
     * off() used to turn off the floor light
     */
    public void off() {
        this.FloorLamp = false;
    }
    /**
     * getDirection() used to get the direction of the floor button
     *
     * @return direction of the button
     */
    public boolean getDirection(){
    	return this.dir;
    }
    /**
     * getFloor() used to get the floor where the floor button is
     *
     * @return floor of the floor button
     */
    public int getFloor() {
    	return this.floor;
    }
    
    /**
     * Get the current state of the lamp
     * @return as described above
     */
    public boolean getState() {
    	return this.FloorLamp;
    }
}
