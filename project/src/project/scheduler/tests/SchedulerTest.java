/*
    Author: Zijun Hu
    This is the JUnit test case for Scheduler, mainly test the state changing this the Scheduler.
 */
package project.scheduler.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import project.floor.Floor;
import project.elevator.Elevator;
import project.scheduler.Scheduler;
import project.scheduler.src.SchedulerState;
import project.utils.Database;

class SchedulerTest {
    Database db = new Database();
    Elevator elevator = new Elevator(1, 1, 7, 0, false, false, false, db);
    Floor floor = new Floor(7, 7, db);
    Scheduler scheduler = new Scheduler(db, elevator, floor, 7);

    @Test
    void WaitMessageTest() {
        String message = "role:Floor;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        Assertions.assertEquals(SchedulerState.WaitMessage ,scheduler.getState());
    }

    @Test
    void InstructElevatorTest() throws Exception {
        String message = "role:Floor;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        scheduler.execute();
        Assertions.assertEquals(SchedulerState.parseFloorMessage ,scheduler.getState());
    }

    @Test
    void UpdateSubsystemTest() throws Exception {
        String message = "role:Elevator;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        scheduler.execute();
        Assertions.assertEquals(SchedulerState.parseElevatorMessage ,scheduler.getState());
    }
}