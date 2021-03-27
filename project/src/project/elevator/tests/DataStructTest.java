package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.elevator.src.DataStruct;

class DataStructTest {
    private DataStruct ds;
    
    @BeforeEach
    public void setUp() throws Exception {
        this.ds = new DataStruct();
    }
    
    @Test
    @DisplayName("Set the state of the datastruct should work")   
    public void testSetState() {
        ds.setState("Move", -100);
        assertEquals("Move", ds.getState(), "Setting the state not working");
        assertEquals(-100, ds.getFloor(), "Setting the floor not working");
        assertTrue(ds.isWaiting(), "Setting the isWaiting not working");
    }
    
    @Test
    @DisplayName("Reset the state of the datastruct should work")   
    public void testFinished() {
        ds.finished();
        assertEquals("", ds.getState(), "Resetting the state not working");
        assertEquals(0, ds.getFloor(), "Resetting the floor not working");
        assertFalse(ds.isWaiting(), "Resetting the isWaiting not working");
    }

    @Test
    @DisplayName("Set Error should work")
    public void testSetError() {
        ds.setState("Error", -100);
        assertEquals("Error", ds.getState(), "Setting the state not working");
        assertEquals(-100, ds.getFloor(), "Setting the floor not working");
        assertTrue(ds.isWaiting(), "Setting the isWaiting not working");
    }

    @Test
    @DisplayName("Set Other Error should work")
    public void testSetOtherError() {
        ds.setState("arrivalSensorFailed", -100);
        assertEquals("Move", ds.getState(), "Setting the state not working");
        assertEquals(-100, ds.getFloor(), "Setting the floor not working");
        assertTrue(ds.isWaiting(), "Setting the isWaiting not working");
        assertEquals("arrivalSensorFailed", ds.getError(), "Setting the error not working");
    }

}
