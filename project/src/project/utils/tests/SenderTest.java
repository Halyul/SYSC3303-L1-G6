package project.utils.tests;
import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.*;

class SenderTest {
	private Sender s;
	private String role = "test";
	private int id = 0;
	private String state = "test";
	private long time = 1234567890;
	private int  direction = 1;
	private InetAddress a;
	private int p = 12101;
	
	@BeforeEach
    public void setUp() {
        this.s = new Sender(true);
    }
	
	@Test
    @DisplayName("sendFloor should work")   
    public void testSendFloor() {
		String string = "role:test;id:0;state:test;floor:1;time:1234567890;";
		String recvMsg = s.sendFloor(role, id, state, direction, time, a, p);
		assertEquals(recvMsg, "state:Received;", "The Sender can send a message using sendFloor");
    }

	@Test
	@DisplayName("sendElevatorState should work")
	public void testSendElevatorState() {
		String string = "role:test;id:0;state:test;floor:7;time:1234567890;";
		String recvMsg = s.sendElevatorState(role, id, state, 7, direction, time, a, p);
		assertEquals(recvMsg, "state:Received;", "The Sender can send a message using sendFloor");
	}
	
	@Test
    @DisplayName("sendInput should work")   
    public void testSendInput() {
		String string = "role:Floor;id:0;state:test;direction:1;floor:1;time:1234567890;";
		String recvMsg = s.sendInput(id, state, direction, direction, time, a, p);
		assertEquals(recvMsg, "state:Received;", "The Sender can send a message using sendInput");
    }
	
	@Test
    @DisplayName("sendError should work")   
    public void testSendError() {
		String string = "role:test;id:0;error:test;floor:1;time:1234567890;";
		String recvMsg = s.sendError(role, id, state, direction, time, a, p);
		assertEquals(recvMsg, "state:Received;", "The Sender can send a message using sendError");
    }

}
