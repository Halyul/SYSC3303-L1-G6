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
        Assertions.assertEquals("Idle", e.getCurrentStatus());
    }

    @Test
    void getCurrentStatusTest() {
        e.setCurrentStatus("Stop");
        Assertions.assertEquals("Stop", e.getCurrentStatus());
    }

    @Test
    void setCurrentActionTest() {
        e.setCurrentAction(7);
        Assertions.assertEquals(7, e.getCurrentAction());
    }

    @Test
    void getCurrentActionTest() {
        e.setCurrentAction(5);
        Assertions.assertNotEquals(3, e.getCurrentAction());
    }

    @Test
    void setCurrentLocationTest() {
        e.setCurrentLocation(15);
        Assertions.assertEquals(15, e.getCurrentLocation());
    }

    @Test
    void getCurrentLocationTest() {
        e.setCurrentLocation(15);
        Assertions.assertNotEquals(7, e.getCurrentLocation());
    }

    @Test
    void setDirectionTest() {
        e.setDirection(0);
        Assertions.assertNotEquals(1, e.getDirection());
    }

    @Test
    void getDirectionTest() {
        e.setDirection(1);
        Assertions.assertEquals(1, e.getDirection());
    }

    @Test
    void getIdTest() {
        Assertions.assertEquals(1, e.getId());
    }

    @Test
    void addLastActionTest() {
        e.addLastAction(15);
        e.addLastAction(7);
        e.addLastAction(14);
        Assertions.assertEquals(15, e.popNextStop());
        Assertions.assertEquals(7, e.popNextStop());
        Assertions.assertEquals(14, e.popNextStop());
    }

    @Test
    void popNextStopTest() {
        e.addLastAction(15);
        e.addLastAction(7);
        e.addLastAction(14);
        e.popNextStop();    //pop floor 15
        Assertions.assertEquals(7, e.popNextStop());
        Assertions.assertEquals(14, e.popNextStop());
    }

    @Test
    void getLastActionTest() {
        e.addLastAction(15);
        e.addLastAction(7);
        e.addLastAction(14);
        Assertions.assertEquals(14, e.getLastAction());
    }

    @Test
    void actionListEmptyTest() {
        e.addLastAction(15);
        Assertions.assertFalse(e.actionListEmpty());

        e.popNextStop();
        Assertions.assertTrue(e.actionListEmpty());
    }

    @Test
    void isIdleTest() {
        e.setCurrentStatus("Idle");
        Assertions.assertTrue(e.isIdle());

        e.setCurrentStatus("Stop");
        Assertions.assertFalse(e.isIdle());
    }
}