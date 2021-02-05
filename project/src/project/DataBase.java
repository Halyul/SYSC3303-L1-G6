package project;
import java.util.ArrayList;

public class DataBase{
	private ArrayList<byte[]> messageList = new ArrayList<byte[]>();
	
	public DataBase() {}
	
    /*
     * 
     */
    public synchronized void put(byte[] message) {
		this.messageList.add(message);
		notifyAll();
    }
    
    /*
     * 
     */
    public synchronized byte[] get() {
        while (messageList.size() < 1) {
            try {
                wait();
            } catch (InterruptedException e) {
            	System.out.println(e);
            }
        }
        byte[] message = this.messageList.get(0);
        messageList.remove(0);
        return message;
    }
}
