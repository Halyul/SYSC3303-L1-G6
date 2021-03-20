package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import project.elevator.src.Door;

class DoorTest {
    private Door d;
    
    @BeforeEach
    public void setUp() throws Exception {
        this.d = new Door();
    }
    
    @Test
    @DisplayName("Set the open/close of the door should work")   
    public void testSetState() {
        d.open(false);
        assertTrue(d.getState(), "Setting open should work");
        
        d.close(false);
        assertFalse(d.getState(), "Setting close should work");
    }
    
    @Test
    @DisplayName("Set the stuck state of the door should work")   
    public void testSetStuck() {
        Door door = new Door();
        door.close(true);
        assertFalse(door.getState(), "Stuck at open should work");
        
        door = new Door();
        door.open(true);
        assertFalse(door.getState(), "Stuck at close should work");
    }

}
