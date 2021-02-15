package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.elevator.src.Motor;

class MotorTest {
	private Motor motor;
	
	@BeforeEach
    public void setUp() throws Exception {
        this.motor = new Motor();
    }
	
	@Test
    @DisplayName("Set the speed of the motor should work")   
    public void testSetSpeed() {
		double speed = 10.5;
		motor.up(speed);
        assertEquals(speed, motor.getSpeed(), "Setting the up speed should work");
        
        speed = 10.5;
		motor.down(speed);
        assertEquals(speed, motor.getSpeed(), "Setting the down speed should work");
        
        motor.stop();
        assertEquals(0, motor.getSpeed(), "Stop should be zero speed");
    }
	
	@Test
    @DisplayName("Set the direction of the motor should work")   
    public void testSetDirection() {
		double speed = 10.5;
		motor.up(speed);
        assertEquals(1, motor.getDirection(), "Setting the up direction should work");
        
        speed = 10.5;
		motor.down(speed);
        assertEquals(0, motor.getDirection(), "Setting the down direction should work");
        
        motor.stop();
        assertEquals(-1, motor.getDirection(), "Stop should be -1 direction");
    }
	
}
