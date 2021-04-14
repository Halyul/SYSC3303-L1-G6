package project.floor.tests;

import org.junit.jupiter.api.*;

import project.floor.src.GUI;

public class GUITest {
	private GUI g;
	/**
	*	setUp() used to initialize gui object before each test
	*/
	@BeforeEach
	public void setUp() throws Exception{
		g = new GUI(1);
	}
	
	/**
	*	updateNewFloor() sends a message and checks if the GUI updates with the new message information
	*/
	@SuppressWarnings("static-access")
	@Test
    @DisplayName("Updates the JLabels in the GUI by changing floor and direction")   
	public void updateNewFloor() {
        String message = "role:Elevator;id:1;state:Moving;floor:4;direction:1;time:0;";
        byte[] messageBytes = message.getBytes();
		g.put(messageBytes);
		g.updateGUI();
		Assertions.assertEquals(g.currLabel(0, 0), "4");
		Assertions.assertEquals(g.currLabel(0, 1), "Moving");
		Assertions.assertEquals(g.currLabel(0, 2), "Up");
		
		message = "role:Elevator;id:1;state:Moving;floor:5;direction:0;time:0;";
		messageBytes = message.getBytes();
		g.put(messageBytes);
		g.updateGUI();
		Assertions.assertEquals(g.currLabel(0, 0), "5");
		Assertions.assertEquals(g.currLabel(0, 1), "Moving");
		Assertions.assertEquals(g.currLabel(0, 2), "Down");
	}
	/**
	*	updateError() sends a message with an error and checks if the GUI updates with the new message information
	*/
	@SuppressWarnings("static-access")
	@Test
    @DisplayName("Updates the JLabels in the GUI by sending error and changing states")   
	public void updateError() {
        String message = "role:Elevator;id:1;error:stuckBetweenFloors;floor:2;time:0;state:Error";
        byte[] messageBytes = message.getBytes();
		g.put(messageBytes);
		g.updateGUI();
		Assertions.assertEquals(g.currLabel(0, 1), "Error");
		Assertions.assertEquals(g.currLabel(0, 3), "Elevator is stuck between two floors");

	}

}
