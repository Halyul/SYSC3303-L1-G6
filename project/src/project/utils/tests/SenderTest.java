package project.utils.tests;
import java.net.InetAddress;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.*;

class SenderTest {
	private Sender s;
	private Database db;
	private String role = "test";
	private int id = 0;
	private String state = "test";
	private long time = 1234567890;
	private int  direction = 1;
	private InetAddress a;
	private int p;
	
	@BeforeEach
    public void setUp() throws Exception {
		this.db = new Database();
        this.s = new Sender(this.db);
    }
	
	@Test
    @DisplayName("sendState should work")   
    public void testSendState() {
		String string = "role:test;id:0;state:test;time:1234567890;type:sendState;";
		s.sendState(role, id, state, time, a, p);
		assertEquals(string, new String(db.get()), "The Sender can send a message using sendState");
    }
	
	@Test
    @DisplayName("sendDirection should work")   
    public void testSendDirection() {
		String string = "role:test;id:0;state:test;direction:1;time:1234567890;type:sendDirection;";
		s.sendDirection(role, id, state, direction, time, a, p);
		assertEquals(string, new String(db.get()), "The Sender can send a message using sendDirection");
    }
	
	@Test
    @DisplayName("sendFloor should work")   
    public void testSendFloor() {
		String string = "role:test;id:0;state:test;floor:1;time:1234567890;type:sendFloor;";
		s.sendFloor(role, id, state, direction, time, a, p);
		assertEquals(string, new String(db.get()), "The Sender can send a message using sendFloor");
    }
	
	@Test
    @DisplayName("sendInput should work")   
    public void testSendInput() {
		String string = "role:Floor;id:0;state:test;direction:1;floor:1;time:1234567890;type:sendInput;";
		s.sendInput(id, state, direction, direction, time, a, p);
		assertEquals(string, new String(db.get()), "The Sender can send a message using sendInput");
    }
	
	@Test
    @DisplayName("sendError should work")   
    public void testSendError() {
		String string = "role:test;id:0;error:test;floor:1;time:1234567890;type:sendError;";
		s.sendError(role, id, state, direction, time, a, p);
		assertEquals(string, new String(db.get()), "The Sender can send a message using sendError");
    }

}
