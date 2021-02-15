package project;

public class ElevatorStates {
	private int elevatorID;
	private String currentState;
	private int currentDest;
	private int userDest;
	
	public ElevatorStates(int ID) {
		this.elevatorID = ID;
	}
	
	public void setState(String state) {
		this.currentState = state;
	}
	
	public void setDest(int dest) {
		this.currentDest = dest;
	}
	
	public void setUserDest(int userDest) {
		this.userDest = userDest;
	}
}
