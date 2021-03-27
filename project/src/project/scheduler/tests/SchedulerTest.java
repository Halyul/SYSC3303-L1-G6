/*
    Author: Zijun Hu
    This is the JUnit test case for Scheduler, mainly test the state changing this the Scheduler.
 */

package project.scheduler.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.scheduler.Scheduler;
import project.scheduler.src.SchedulerState;
import project.utils.Database;

import java.net.InetAddress;
import java.net.UnknownHostException;

class SchedulerTest {
    int schedulerPort = 12000;
    Database db = new Database();

    Scheduler scheduler = new Scheduler(db, 1, 7, InetAddress.getLocalHost(), schedulerPort);

    SchedulerTest() throws UnknownHostException {
    }

    @Test
    @DisplayName("Test if the state, WaitMessage, set correctly.")
    void WaitMessageTest() {
        String message = "role:Floor;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        Assertions.assertEquals(SchedulerState.WaitMessage, scheduler.getState());
    }

    @Test
    @DisplayName("Test if the state, parseFloorMessage, set correctly.")
    void parseFloorMessageTest() {
        String message = "role:Floor;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        scheduler.execute();
        Assertions.assertEquals(SchedulerState.parseFloorMessage, scheduler.getState());
    }

    @Test
    @DisplayName("Test if the state, parseElevatorMessage, set correctly.")
    void parseElevatorMessageTest() {
        String message = "role:Elevator;id:7;state:Reading;direction:1;floor:4;time:10432800000;type:sendInput;";
        byte[] messageBytes = message.getBytes();
        db.put(messageBytes);
        scheduler.execute();
        Assertions.assertEquals(SchedulerState.parseElevatorMessage, scheduler.getState());
    }
}