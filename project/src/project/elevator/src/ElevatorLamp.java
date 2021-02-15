package project.elevator.src;

public class ElevatorLamp {
    // floor number
    private int number;
    // light status
    private boolean isOn = false;
    
    public ElevatorLamp(int number) {
        this.number = number;
    }
    
    /**
     * Turn on the light
     */
    public void on() {
        this.isOn = true;
        System.out.println(Thread.currentThread().getName() + ": The light of " + this.number + " floor is on.");
    }
    
    /**
     * Turn off the light
     */
    public void off() {
        this.isOn = false;
        System.out.println(Thread.currentThread().getName() + ": The light of " + this.number + " floor is off.");
    }
    
    /**
     * Get the current state of the lamp
     * @return as described above
     */
    public boolean getState() {
    	return this.isOn;
    }
}