package project.elevator.src;

public class ElevatorButton {
    // floor number
    private int number;
    
    public ElevatorButton(int number) {
        this.number = number;
    }
    
    /**
     * The button is pressed
     */
    public void press() {
        
    }
    
    /**
     * Get the number of the button
     * @return as described above
     */
    public int getNumber() {
    	return this.number;
    }
}
