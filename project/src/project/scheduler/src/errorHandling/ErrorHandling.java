/*
    Author: Zijun Hu

    This is the class for handling elevator errors using by the scheduler.
 */

package project.scheduler.src.errorHandling;

import project.scheduler.src.ElevatorStatusArrayList;

import java.util.ArrayList;
import java.util.TimerTask;

public class ErrorHandling {
    private final ArrayList<TaskTimer> timerArrayList = new ArrayList<>();

    public ErrorHandling() {
    }

    /**
     * Start a timer for elevator
     *
     * @param elevatorStatusArrayList Elevator status arraylist
     * @param id                      id of elevator
     */
    public void startTimer(ElevatorStatusArrayList elevatorStatusArrayList, int id) {
        TaskTimer taskTimer = new TaskTimer(id);
        TimerTask task = new ErrorHandlingTask(elevatorStatusArrayList, id);
        taskTimer.schedule(task, 5000);
        timerArrayList.add(taskTimer);
    }

    /**
     * Stop the timer for elevator with id
     *
     * @param id elevator id
     */
    public void cancelTimer(int id) {
        if (timerExist(id)) {
            TaskTimer timeToRemove = null;
            for (TaskTimer t : timerArrayList) {
                timeToRemove = t;
                if (t.getId() == id) {
                    t.cancel();
                    break;
                }
            }

            if (timeToRemove != null) {
                this.timerArrayList.remove(timeToRemove);
            }
        }
    }

    /**
     * Restart the timer for the elevator with id
     *
     * @param elevatorStatusArrayList Elevator status arraylist
     * @param id                      elevator id
     */
    public void restartTimer(ElevatorStatusArrayList elevatorStatusArrayList, int id) {
        this.cancelTimer(id);
        this.startTimer(elevatorStatusArrayList, id);
    }

    /**
     * Check if timer already exist
     *
     * @param id elevator id
     * @return if timer exist
     */
    public boolean timerExist(int id) {
        for (TaskTimer t : timerArrayList) {
            if (t.getId() == id) {
                return true;
            }
        }
        return false;
    }

}
