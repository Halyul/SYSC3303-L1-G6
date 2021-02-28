package project.utils.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.Parser;

class ParserTest {
	private Parser p;
	
	@BeforeEach
    public void setUp() throws Exception {
		this.p = new Parser();
    }
	
	@Test
    @DisplayName("parse string should work")   
    public void testParseString() {
		String string = "role:test;id:0;state:test;direction:1;floor:1;time:1234567890;type:test;error:test;";
		p.parse(string);
		assertEquals("test", p.getRole(), "getRole not working");
		assertEquals(0, p.getIdentifier(), "getIdentifier not working");
		assertEquals("test", p.getState(), "getState not working");
		assertEquals(1, p.getDirection(), "getDirection not working");
		assertEquals(1, p.getFloor(), "getFloor not working");
		assertEquals(1234567890, p.getTime(), "getTime not working");
		assertEquals("test", p.getType(), "getType not working");
		assertEquals("test", p.getError(), "getError not working");
    }
	
	@Test
    @DisplayName("parse bytes should work")   
    public void testParseBytes() {
		String string = "role:test;id:0;state:test;direction:1;floor:1;time:1234567890;type:test;error:test;";
		p.parse(string.getBytes());
		assertEquals("test", p.getRole(), "getRole not working");
		assertEquals(0, p.getIdentifier(), "getIdentifier not working");
		assertEquals("test", p.getState(), "getState not working");
		assertEquals(1, p.getDirection(), "getDirection not working");
		assertEquals(1, p.getFloor(), "getFloor not working");
		assertEquals(1234567890, p.getTime(), "getTime not working");
		assertEquals("test", p.getType(), "getType not working");
		assertEquals("test", p.getError(), "getError not working");
    }
}
