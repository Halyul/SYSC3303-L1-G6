package project.elevator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import project.utils.Database;

class ElevatorTest {
	private Database db = new Database();
	private Elevator e = new Elevator(1, 1, 7, 0, false, false, false, db);
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
