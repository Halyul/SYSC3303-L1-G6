package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import project.elevator.src.ArrivalSensor;
import project.elevator.src.Motor;

class ArrivalSensorTest {

    private ArrivalSensor a2;
    private ArrivalSensor a7;
    private Motor m;
    
    @BeforeEach
    public void setUp() throws Exception {
        this.a2 = new ArrivalSensor(2);
        this.a7 = new ArrivalSensor(7);
        this.m = new Motor();
    }
    
    @Test
    @DisplayName("The speed caculation should be correct")   
    public void testSpeed() {
        m.up(0);
        double speed = a2.check(m.getSpeed(), m.getMaxSpeed(), m.getAccelerationDisplacement(), m.getAccelerationTime(), 7);
        assertEquals(m.getMaxSpeed(), speed, "from zero speed to maximum speed");
        m.up(speed);
        assertEquals(0, a7.check(m.getSpeed(), m.getMaxSpeed(), m.getAccelerationDisplacement(), m.getAccelerationTime(), 7), "from maximum speed to zero speed");
        m.down(0);
        assertEquals(0, a2.check(m.getSpeed(), m.getMaxSpeed(), m.getAccelerationDisplacement(), m.getAccelerationTime(), 2), "from zero speed to zero speed");
    }

}
