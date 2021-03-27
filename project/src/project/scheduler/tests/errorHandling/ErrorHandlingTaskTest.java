/*
    Author: Zijun Hu

    This is the test case for class ErrorHandlingTask
 */

package project.scheduler.tests.errorHandling;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.scheduler.src.*;
import project.scheduler.src.errorHandling.*;

public class ErrorHandlingTaskTest {
    private final ElevatorStatusArrayList elevatorStatusArrayList = new ElevatorStatusArrayList();
    private final ErrorHandlingTask errorHandlingTask = new ErrorHandlingTask(this.elevatorStatusArrayList, 1);

    @Test
    void errorHandlingTaskTest() {
        elevatorStatusArrayList.addElevator(new ElevatorStatus(1));
        errorHandlingTask.run();
        Assertions.assertTrue(elevatorStatusArrayList.ifElevatorError(1));
    }
}
