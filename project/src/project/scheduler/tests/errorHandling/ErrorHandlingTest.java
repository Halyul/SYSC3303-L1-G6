/*
    Author: Zijun Hu

    This is the test case for class ErrorHandling.
 */

package project.scheduler.tests.errorHandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.scheduler.src.*;
import project.scheduler.src.errorHandling.*;

public class ErrorHandlingTest {
    private final ErrorHandling errorHandling = new ErrorHandling();
    private final ElevatorStatusArrayList elevatorStatusArrayList = new ElevatorStatusArrayList();

    @Test
    @DisplayName("Test if the timer start correctly.")
    void startTimerTest() {
        this.elevatorStatusArrayList.addElevator(new ElevatorStatus(1));
        errorHandling.startTimer(this.elevatorStatusArrayList, 1);
        Assertions.assertTrue(errorHandling.timerExist(1));
    }

    @Test
    @DisplayName("Test if the timer cancel correctly.")
    void cancelTimerTest() {
        errorHandling.cancelTimer(1);
        Assertions.assertFalse(errorHandling.timerExist(1));
    }

    @Test
    @DisplayName("Test if the timer restart correctly.")
    void restartTimerTest() {
        errorHandling.restartTimer(elevatorStatusArrayList, 1);
        Assertions.assertTrue(errorHandling.timerExist(1));
    }
}
