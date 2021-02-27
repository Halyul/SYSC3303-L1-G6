package project.floor.src;

public class FloorButton {
	//FloorLamp = 1 if on, 0 if off
	private boolean FloorLamp = false;
	
	public FloorButton() {
		
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
    
    /**
     * Get the current state of the lamp
     * @return as described above
     */
    public boolean getState() {
    	return this.FloorLamp;
    }
}
