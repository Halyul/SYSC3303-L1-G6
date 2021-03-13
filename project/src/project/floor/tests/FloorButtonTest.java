package project.floor.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.floor.src.FloorButton;

public class FloorButtonTest {
	private FloorButton e;
	@BeforeEach
	public void setUp() throws Exception{
		this.e = new FloorButton(1, true);
	}
	
    @Test
    @DisplayName("Set the on/off state of the Floor button lamp should work")   
    public void testSetState() {
        e.on();
        assertTrue(e.getState(), "Setting the on state should work");

        e.off();
        assertFalse(e.getState(), "Setting the off state should work");
    }
}
