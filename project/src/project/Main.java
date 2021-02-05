package project;

public class Main {
	/**
     * Start the program.
     * @param args
     */
	public static void main(String[] args) {
		// threads
        Thread elevatorThread, schedulerThread;
        DataBase db = new DataBase();
        // Elevator subsystem
        Elevator elevator = new Elevator(1, 1, db);
        Scheduler scheduler = new Scheduler(db, elevator);
        
        // new threads
        elevatorThread = new Thread(elevator, "Elevator 1");
        schedulerThread = new Thread(scheduler, "Scheduler");
        
        // start threads
        elevatorThread.start();
        schedulerThread.start();
	}
}
