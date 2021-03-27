/*
    Author: Zijun Hu

    This is the class for the task timer used by error handling.
 */

package project.scheduler.src.errorHandling;

import java.util.Timer;
import java.util.TimerTask;

public class TaskTimer {
    int id;
    Timer timer;
    boolean hasStarted = false;

    /**
     * Initialization
     *
     * @param id elevator id(same as timer id)
     */
    public TaskTimer(int id) {
        this.id = id;
        this.timer = new Timer();
    }

    /**
     * Schedule a timer
     *
     * @param timerTask the task to do when times up
     * @param delay     delay to set on timer
     */
    public void schedule(TimerTask timerTask, int delay) {
        this.timer.schedule(timerTask, delay);
        this.hasStarted = true;
    }

    /**
     * Cancel the timer
     */
    public void cancel() {
        this.timer.cancel();
        this.hasStarted = false;
    }

    /**
     * Get the id for the timer
     *
     * @return the id of elevator
     */
    public int getId() {
        return this.id;
    }

    /**
     * Check if the timer is running
     *
     * @return if the timer is on
     */
    public boolean hasTimerRunning() {
        return this.hasStarted;
    }
}
