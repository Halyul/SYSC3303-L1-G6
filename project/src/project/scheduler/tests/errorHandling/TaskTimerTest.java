/*
    Author: Zijun Hu

    This is the test case for class TaskTimer
 */

package project.scheduler.tests.errorHandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.scheduler.src.*;
import project.scheduler.src.errorHandling.*;

import java.util.TimerTask;

public class TaskTimerTest {
    private final ElevatorStatusArrayList elevatorStatusArrayList = new ElevatorStatusArrayList();
    private final TaskTimer taskTimer = new TaskTimer(1);
    private final TimerTask task = new ErrorHandlingTask(elevatorStatusArrayList, 1);

    @Test
    @DisplayName("Test if the timer task scheduled correctly.")
    void scheduleTest() {
        elevatorStatusArrayList.addElevator(new ElevatorStatus(1));
        taskTimer.schedule(task, 5000);
        Assertions.assertTrue(taskTimer.hasTimerRunning());
    }

    @Test
    @DisplayName("Test if the timer task canceled correctly.")
    void cancelTest() {
        taskTimer.cancel();
        Assertions.assertFalse(taskTimer.hasTimerRunning());
    }

    @Test
    @DisplayName("Test if the timer task get the correct id.")
    void getIdTest() {
        Assertions.assertEquals(1, taskTimer.getId());
    }


}
