package project.floor.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.floor.src.DirectionLamp;

class DirectionLampTest {
    private DirectionLamp e;
    /**
    *   setUp() sets up the object for error test
    */
    @BeforeEach
    public void setUp() throws Exception {
        this.e = new DirectionLamp(1, true);
    }
    
    @Test
    @DisplayName("Set the on/off state of the lamp should work")   
    public void testSetState() {
        e.on();
        assertTrue(e.getState(), "Setting the on state should work");

        e.off();
        assertFalse(e.getState(), "Setting the off state should work");
    }

}
