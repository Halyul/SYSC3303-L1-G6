package project;

public class Scheduler implements Runnable {
	private DataBase db = new DataBase();
	private Communication c = new Communication();
	private Elevator elevator_1;
//	private Floor floor_1;
	
	public Scheduler(DataBase db, Elevator elevator) {
		this.db = db;
		this.elevator_1 = elevator;
	}
	
	private void sendMessage() {
		byte[] message = this.db.get();
		c.parse(message);
		if(c.getRole().equals("floor")) {
			elevator_1.put(message);
			System.out.println(Thread.currentThread().getName() + ": " + new String (message));
		}else if (c.getRole().equals("elevator")) {
//			floor_1.put(message);
		}
	}
	
	public void run() {
		while(true) {
			this.sendMessage();
		}
	}
}
