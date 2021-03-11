package project.elevator.tests;
import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.elevator.src.Receiver;

class ReceiverTest {
	private Receiver r;
	private String role = "test";
	private int id = 0;
	private String state = "test";
	private long time = 1234567890;
	private int  direction = 1;
	
	@BeforeEach
    public void setUp() {
        this.r = new Receiver(true);
    }
	
	@Test
    @DisplayName("parse should work")   
    public void testParse() {
		byte[] string = "role:test;id:0;state:test;time:1234567890;type:sendState;".getBytes();
		assertEquals(string, r.debug(string), "The Reciver can parse a message");
    }

}
