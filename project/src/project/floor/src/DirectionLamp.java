package project.floor.src;

public class DirectionLamp {
    // light status
    private Boolean isOn = false;
    private int floor;
    private boolean dir;	//True for up, false for down
    
    public DirectionLamp(int floor, boolean dir) {
		this.floor = floor;
		this.dir = dir;
    }
    
    /**
     * Turn on the light
     */
    public void on() {
        this.isOn = true;
    }
    
    /**
     * Turn off the light
     */
    public void off() {
        this.isOn = false;
    }
    
    public boolean getDirection(){
    	return this.dir;
    }
    
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
