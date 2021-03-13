/*
    Author: Zijun Hu

    This is the test class for Class ElevatorStatus.
 */
package project.scheduler.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import project.scheduler.src.ElevatorStatus;

class ElevatorStatusTest {
    ElevatorStatus e = new ElevatorStatus(1);

    @Test
    void setCurrentStatusTest() {
        e.setCurrentStatus("Idle");
        Assertions.assertEquals("Idle",e.getCurrentStatus());
    }

    @Test
    void getCurrentStatusTest() {
        e.setCurrentStatus("Stop");
        Assertions.assertEquals("Stop",e.getCurrentStatus());
    }

    @Test
    void setCurrentActionTest() {
        e.setCurrentAction(7);
        Assertions.assertEquals(7,e.getCurrentAction());
    }

    @Test
    void getCurrentActionTest() {
        e.setCurrentAction(5);
        Assertions.assertNotEquals(3,e.getCurrentAction());
    }

    @Test
    void setCurrentLocationTest() {
        e.setCurrentLocation(15);
        Assertions.assertEquals(15,e.getCurrentLocation());
    }

    @Test
    void getCurrentLocationTest() {
        e.setCurrentLocation(15);
        Assertions.assertNotEquals(7,e.getCurrentLocation());
    }

    @Test
    void addNextStopTest() {
        e.addLastAction(15);
        e.addLastAction(7);
        e.addLastAction(14);
        Assertions.assertEquals(15,e.popNextStop());
        Assertions.assertEquals(7,e.popNextStop());
        Assertions.assertEquals(14,e.popNextStop());
    }

    @Test
    void popNextStop() {
        e.addLastAction(15);
        e.addLastAction(7);
        e.addLastAction(14);
        e.popNextStop();    //pop floor 15
        Assertions.assertEquals(7,e.popNextStop());
        Assertions.assertEquals(14,e.popNextStop());
    }

    @Test
    void actionListEmpty() {
        e.addLastAction(15);
        Assertions.assertFalse(e.actionListEmpty());

        e.popNextStop();
        Assertions.assertTrue(e.actionListEmpty());
    }

    @Test
    void isIdle() {
        e.setCurrentStatus("Idle");
        Assertions.assertTrue(e.isIdle());

        e.setCurrentStatus("Stop");
        Assertions.assertFalse(e.isIdle());
    }
}