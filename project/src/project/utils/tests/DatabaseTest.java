package project.utils.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import project.utils.Database;

class DatabaseTest {
	private Database db;
	
	@BeforeEach
    public void setUp() throws Exception {
		this.db = new Database();
    }
	
	@Test
    @DisplayName("put and get should work")   
    public void testPutGet() {
		String string = "role:test;id:0;state:test;direction:1;floor:1;time:1234567890;type:test;error:test;";
		db.put(string.getBytes());
		assertEquals(string, new String(db.get()), "put and get not working");
    }
}
