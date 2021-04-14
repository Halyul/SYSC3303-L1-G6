package project.elevator.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.*;

import project.elevator.Elevator;
import project.utils.Database;

class ElevatorTest {
    private Elevator e, e1;
    
    @BeforeEach
    public void setUp() throws Exception {
        InetAddress schedulerAddress = null;
        try {
	   		schedulerAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
        this.e = new Elevator(1, 1, 7, 0, schedulerAddress, 12000);
        this.e1 = new Elevator(2, 1, 7, 0, schedulerAddress, 12000);
    }
    
    @Test
    @DisplayName("by default should be Stationary")   
    public void testStationary() {
        assertEquals("Stationary", e.getState(), "The elevator not in Stationary");
    }

    @Test
    @DisplayName("Test multiple cars scheduling")
    public void testMultipleCars() throws InterruptedException {
        byte[] eString = "id:1;state:Move;floor:4;".getBytes();
        byte[] e1String = "id:2;state:Move;floor:1;".getBytes();

        e.put(eString);
        e1.put(e1String);

        e.execute(); // 1st to 2nd floor
        e1.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("Stop", e1.getState(), "The elevator not in Stop");
        e.execute(); // 2nd to 3rd floor
        e1.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("OpenDoor", e1.getState(), "The elevator not in OpenDoor");
        e.execute(); // 3rf to 4th floor
        e1.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("CloseDoor", e1.getState(), "The elevator not in CloseDoor");

        e1String = "id:2;state:Move;floor:3;".getBytes();
        e1.put(e1String);

        e.execute(); // stop at 4th
        e1.execute();
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        assertEquals("Move", e1.getState(), "The elevator not in Move");
        e.execute();
        e1.execute(); // 2nd to 3rd floor
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        assertEquals("Move", e1.getState(), "The elevator not in Move");
        e.execute();
        e1.execute(); // stop at 3rd
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        assertEquals("Stop", e1.getState(), "The elevator not in Stop");

        eString = "state:Move;floor:1;".getBytes();
        e.put(eString);

        e.execute(); // 4th to 3rd floor
        e1.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("OpenDoor", e1.getState(), "The elevator not in OpenDoor");
        e.execute(); // 3rd to 2nd floor
        e1.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("CloseDoor", e1.getState(), "The elevator not in CloseDoor");
        e.execute(); // 2nd to 1st floor
        e1.execute();

        assertEquals("Move", e.getState(), "The elevator not in Move");
        assertEquals("Stationary", e1.getState(), "The elevator not in Stationary");
        e.execute(); // stop at 1st
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        e.execute();
        assertEquals("Stationary", e.getState(), "The elevator not in Stationary");
    }
    
    @Test
    @DisplayName("Move to different floor to pick up user and move to 1")   
    public void testMoveToDifferentFloorPickUpMoveTo1() {
        byte[] string = "state:Move;floor:4;".getBytes();
        e.put(string);
        e.execute(); // 1st to 2nd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 3rf to 4th floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // stop at 4th
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        e.execute();
        string = "state:Move;floor:1;".getBytes();
        e.put(string);
        e.execute(); // 4th to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 3rd to 2nd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 1st floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // stop at 1st
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        e.execute();
        assertEquals("Stationary", e.getState(), "The elevator not in Stationary");
    }
    
    @Test
    @DisplayName("Same floor pick up user and move to 3rd floor")   
    public void testSameFloorPickUpMoveTo3() {
        byte[] string = "state:Move;floor:1;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        string = "state:Move;floor:3;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // stop at 3rd
        assertEquals("Stop", e.getState(), "The elevator not in Stop");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        e.execute();
        assertEquals("Stationary", e.getState(), "The elevator not in Stationary");
    }

    @Test
    @DisplayName("Simulate stuckBetweenFloors")
    public void testSimulateStuckBetweenFloors() {
        byte[] string = "state:stuckBetweenFloors;floor:3;".getBytes();
        e.put(string);
        e.execute();
        // start moving
        assertEquals("Move", e.getState(), "The elevator not in Error");
        // immediately stuck
        e.execute();
        assertEquals("Error", e.getState(), "The elevator not in Move");
    }

    @Test
    @DisplayName("Simulate arrivalSensorFailed before CloseDoor")
    public void testSimulateArrivalSensorFailedBeforeCloseDoor() {
        byte[] string = "state:arrivalSensorFailed;floor:3;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // should go pass 3
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // then stop at 4
        assertEquals("Stop", e.getState(), "The elevator not in Move");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        // the scheduler should know the arrival sensor at 3th floor has failed
        // and the timer should have expired
        string = "state:Error;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Error", e.getState(), "The elevator not in Error");
    }

    @Test
    @DisplayName("Simulate arrivalSensorFailed at CloseDoor")
    public void testSimulateArrivalSensorFailedAtCloseDoor() {
        byte[] string = "state:arrivalSensorFailed;floor:3;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // should go pass 3
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // then stop at 4
        assertEquals("Stop", e.getState(), "The elevator not in Move");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        // the scheduler should know the arrival sensor at 3th floor has failed
        // and the timer should have expired
        string = "state:Error;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("Error", e.getState(), "The elevator not in Error");
    }

    @Test
    @DisplayName("Simulate doorStuckAtOpen")
    public void testSimulateDoorStuckAtOpen() {
        byte[] string = "state:doorStuckAtOpen;floor:3;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // then stop at 3
        assertEquals("Stop", e.getState(), "The elevator not in Move");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("CloseDoor", e.getState(), "The elevator not in CloseDoor");
        e.execute();
        assertEquals("Error", e.getState(), "The elevator not in Error");
    }

    @Test
    @DisplayName("Simulate doorStuckAtClose")
    public void testSimulateDoorStuckAtClose() {
        byte[] string = "state:doorStuckAtClose;floor:3;".getBytes();
        e.put(string);
        e.execute();
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // 2nd to 3rd floor
        assertEquals("Move", e.getState(), "The elevator not in Move");
        e.execute(); // then stop at 3
        assertEquals("Stop", e.getState(), "The elevator not in Move");
        e.execute();
        assertEquals("OpenDoor", e.getState(), "The elevator not in OpenDoor");
        e.execute();
        assertEquals("Error", e.getState(), "The elevator not in Error");
    }
}
