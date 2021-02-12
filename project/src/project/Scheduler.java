package project;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import project.utils.Parser;
import project.utils.Database;
import project.backup.Sender;
import project.elevator.*;

public class Scheduler implements Runnable {
	private Database db = new Database();
	private Parser parser = new Parser();
	private Sender sender = new Sender(db);
	private Elevator elevator_1;
	private Floor floor_1;
	
	public Scheduler(Database db, Elevator elevator, Floor floor) {
		this.db = db;
		this.elevator_1 = elevator;
		this.floor_1 = floor;
	}
	
	/**
	 * forward the message to correct subsystem
	 */
	private void sendMessage() {
		byte[] message = this.db.get();
		parser.parse(message);
		if(parser.getRole().equals("Floor")) {
			elevator_1.put(message);
			System.out.println(Thread.currentThread().getName() + " - Send message from Floor to Elevator - " + new String (message));
		}else if (parser.getRole().equals("Elevator")) {
			floor_1.put(message);
			System.out.println(Thread.currentThread().getName() + " - Send message from Elevator to Floor - " + new String (message));
		}
	}
	
	/**
     * @see java.lang.Runnable#run()
     */
    @Override
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
