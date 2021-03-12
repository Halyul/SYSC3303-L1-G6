package project.floor.src;

public class FloorButton {
	//FloorLamp = 1 if on, 0 if off
	private boolean FloorLamp = false;
	private int floor;
	private boolean dir;	//True for up, false for down
	
	public FloorButton(int floor, boolean dir) {
		this.floor = floor;
		this.dir = dir;
	}
    /**
     * Turn on the light
     */
    public void on() {
        this.FloorLamp = true;
    }
    
    /**
     * Turn off the light
     */
    public void off() {
        this.FloorLamp = false;
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
    	return this.FloorLamp;
    }
}
