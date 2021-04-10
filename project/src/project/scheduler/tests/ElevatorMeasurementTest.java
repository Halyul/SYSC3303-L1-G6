package project.scheduler.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.scheduler.src.ElevatorMeasurement;

import java.util.ArrayList;

public class ElevatorMeasurementTest {
    ElevatorMeasurement elevatorMeasurement = new ElevatorMeasurement(1);

    @Test
    @DisplayName("Test if the timestamp added correctly.")
    void addTimeTest() {
        long timeStamp = System.nanoTime()/1000000;
        elevatorMeasurement.addTime("Move", timeStamp);

        ArrayList<Long> moveTime = new ArrayList<>();
        moveTime.add(timeStamp);

        Assertions.assertEquals(moveTime,elevatorMeasurement.getMoveTime());
    }

    @Test
    @DisplayName("Test if the last timestamp set and get correctly.")
    void setLastTimeStampTest() {
        long timeStamp = System.nanoTime()/1000000;
        elevatorMeasurement.setLastTimeStamp(timeStamp);
        Assertions.assertEquals(timeStamp,elevatorMeasurement.getLastTimeStamp());
    }
}
