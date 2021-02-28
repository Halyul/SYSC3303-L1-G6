package project;

import project.elevator.Elevator;
import project.scheduler.Scheduler;
import project.floor.Floor;
import project.utils.Database;

public class Main {
    /**
     * Start the program.
     * @param args
     */
    public static void main(String[] args) {
        // threads
        Thread elevatorThread, schedulerThread, floorThread;
        Database db = new Database();
        // Elevator subsystem
        Elevator elevator = new Elevator(1, 1, 7, 0, false, false, false, db);
        // Floor subsystem
        Floor floor = new Floor(1, 7, db);
        // Scheduler
        Scheduler scheduler = new Scheduler(db, elevator, floor);

        // new threads
        elevatorThread = new Thread(elevator, "Elevator 1");
        floorThread = new Thread(floor, "Floor 7");
        schedulerThread = new Thread(scheduler, "Scheduler");

        // start threads
        elevatorThread.start();
        floorThread.start();
        schedulerThread.start();
    }
}
