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
	private DatagramSocket receiveSocket;
	
	@BeforeEach
    public void setUp() {
        this.s = new Sender(true);
        try {
    		this.a = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(1);
		}
        try {
			this.receiveSocket = new DatagramSocket(p);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
    }
	
	@AfterEach
	public void tearDown() {
		this.receiveSocket.close();
	}
	
	@Test
    @DisplayName("sendFloor should work")   
    public void testSendFloor() {
		String string = "role:test;id:0;state:test;floor:1;time:1234567890;";
		s.sendFloor(role, id, state, direction, time, a, p);
		byte data[] = new byte[5000];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
	    try {
	    	this.receiveSocket.receive(receivePacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
		assertEquals(string, new String(receivePacket.getData(), 0, receivePacket.getLength()), "The Sender can send a message using sendFloor");
    }

	@Test
	@DisplayName("sendElevatorState should work")
	public void testSendElevatorState() {
		String string = "role:test;id:0;state:test;floor:7;time:1234567890;";
		s.sendElevatorState(role, id, state, 7, direction, time, a, p);
		byte data[] = new byte[5000];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		try {
			this.receiveSocket.receive(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		assertEquals(string, new String(receivePacket.getData(), 0, receivePacket.getLength()), "The Sender can send a message using sendFloor");
	}
	
	@Test
    @DisplayName("sendInput should work")   
    public void testSendInput() {
		String string = "role:Floor;id:0;state:test;direction:1;floor:1;time:1234567890;";
		s.sendInput(id, state, direction, direction, time, a, p);
		byte data[] = new byte[5000];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
	    try {
	    	this.receiveSocket.receive(receivePacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
		assertEquals(string, new String(receivePacket.getData(), 0, receivePacket.getLength()), "The Sender can send a message using sendInput");
    }
	
	@Test
    @DisplayName("sendError should work")   
    public void testSendError() {
		String string = "role:test;id:0;error:test;floor:1;time:1234567890;";
		s.sendError(role, id, state, direction, time, a, p);
		byte data[] = new byte[5000];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
	    try {
	    	this.receiveSocket.receive(receivePacket);
	    } catch(IOException e) {
	    	e.printStackTrace();
	    	System.exit(1);
	    }
		assertEquals(string, new String(receivePacket.getData(), 0, receivePacket.getLength()), "The Sender can send a message using sendError");
    }

}
