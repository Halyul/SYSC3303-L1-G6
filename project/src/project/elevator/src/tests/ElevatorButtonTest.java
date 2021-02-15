package project.elevator.src.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import project.elevator.src.ElevatorButton;

class ElevatorButtonTest {
	private ElevatorButton e;
	
	@BeforeEach
    public void setUp() throws Exception {
        this.e = new ElevatorButton(1);
    }
	
	@Test
    public void testNumber() {
		assertEquals(1, e.getNumber());
    }

}
