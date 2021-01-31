package project;

public class Main {
	/**
     * Start the program.
     * @param args
     */
	public static void main(String[] args) {
		// threads
        Thread elevatorThread;
        // Elevator subsystem
        Elevator elevator = new Elevator(1, 1);
        
        // new threads
        elevatorThread = new Thread(elevator, "Elevator 1");
        
        // start threads
        elevatorThread.start();
	}
}
