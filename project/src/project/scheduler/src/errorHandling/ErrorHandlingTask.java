/*
    Author: Zijun Hu

    This is the class for tasks to perform when elevator error occurs
 */

package project.scheduler.src.errorHandling;

import project.scheduler.src.ElevatorStatusArrayList;
import project.utils.Sender;

import java.util.TimerTask;

public class ErrorHandlingTask extends TimerTask {
    int id;
    ElevatorStatusArrayList elevatorStatusArrayList;

    /**
     * Initialization
     *
     * @param elevatorStatusArrayList Elevator status arraylist
     * @param id                      Elevator id
     */
    public ErrorHandlingTask(ElevatorStatusArrayList elevatorStatusArrayList, int id) {
        this.elevatorStatusArrayList = elevatorStatusArrayList;
        this.id = id;
    }

    /**
     * Task to do when error occurs
     */
    public void run() {
        System.out.println("Scheduler: elevator_" + id + " errored" + " - Reason: Timer Expired");
        this.elevatorStatusArrayList.addErrorElevator(id);
    }
}