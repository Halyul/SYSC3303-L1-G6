/*
    Author: Zijun Hu

    This is the test class for Class ElevatorStatus.
 */
package project.scheduler.src;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ElevatorStatusTest {
    ElevatorStatus e = new ElevatorStatus();

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
        e.addNextStop(15);
        e.addNextStop(7);
        e.addNextStop(14);
        Assertions.assertEquals(15,e.popNextStop());
        Assertions.assertEquals(7,e.popNextStop());
        Assertions.assertEquals(14,e.popNextStop());
    }

    @Test
    void popNextStop() {
        e.addNextStop(15);
        e.addNextStop(7);
        e.addNextStop(14);
        e.popNextStop();    //pop floor 15
        Assertions.assertEquals(7,e.popNextStop());
        Assertions.assertEquals(14,e.popNextStop());
    }

    @Test
    void actionListEmpty() {
        e.addNextStop(15);
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