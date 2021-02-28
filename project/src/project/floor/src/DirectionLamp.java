package project.floor.src;

public class DirectionLamp {
    // light status
    private Boolean isOn = false;
    
    public DirectionLamp() {
        
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
    
    /**
     * Get the current state of the lamp
     * @return as described above
     */
    public boolean getState() {
    	return this.isOn;
    }
}
