package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.elevator.src.ElevatorLamp;

class ElevatorLampTest {
	private ElevatorLamp e;
		
	@BeforeEach
    public void setUp() throws Exception {
        this.e = new ElevatorLamp(1);
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
