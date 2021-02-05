package project;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Scheduler implements Runnable {
	private Database db = new Database();
	private Parser parser = new Parser();
	private Sender sender = new Sender(db);
	private Elevator elevator_1;
//	private Floor floor_1;
	
	public Scheduler(Database db, Elevator elevator) {
		this.db = db;
		this.elevator_1 = elevator;
	}
	
	private void sendMessage() {
		byte[] message = this.db.get();
		parser.parse(message);
		if(parser.getRole().equals("floor")) {
			elevator_1.put(message);
			System.out.println(Thread.currentThread().getName() + " - Send message from floor to elevator - " + new String (message));
		}else if (parser.getRole().equals("elevator")) {
//			floor_1.put(message);
			System.out.println(Thread.currentThread().getName() + " - Send message from elevator to floor - " + new String (message));
		}
	}
	
	public void run() {
		while(true) {
			this.sendMessage();
		}
	}
	
	/**
     * Get current time in epoch seconds
     * @return as described above
     */
    private long getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }
}
