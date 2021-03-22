package project.scheduler.src.errorHandling;
import project.scheduler.src.ElevatorStatusArrayList;
import project.utils.Sender;

import java.util.TimerTask;

class ErrorHandlingTask extends TimerTask {
    int id;
    ElevatorStatusArrayList elevatorStatusArrayList;
    private Sender sender;

    public ErrorHandlingTask(ElevatorStatusArrayList elevatorStatusArrayList, int id){
        this.elevatorStatusArrayList = elevatorStatusArrayList;
        this.id = id;
    }

    public void run() {
        System.out.println("Times up, error detected on Elevator " + id);
        this.elevatorStatusArrayList.addErrorElevator(id);
    }
}