package project;

public class Scheduler implements Runnable {
	private Server server = new Server();
	private Communication c = new Communication();
	private Elevator elevator_1;
//	private Floor floor_1;
	
	public Scheduler(Server server, Elevator elevator) {
		this.server = server;
		this.elevator_1 = elevator;
	}
	
	private void sendMessage() {
		byte[] message = this.server.get();
		c.parse(message);
		if(c.getRole() == "Floor") {
			elevator_1.put(message);
		}else if (c.getRole() == "elevator") {
//			floor_1.put(message);
			System.out.println(new String (message));
		}
	}
	
	public void run() {
		while(true) {
			this.sendMessage();
		}
	}
}
