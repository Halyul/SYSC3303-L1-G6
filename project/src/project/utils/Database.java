package project.utils;

import java.util.ArrayList;

public class Database{
    private ArrayList<byte[]> messageList = new ArrayList<byte[]>();
    
    public Database() {}
    
    /**
     * add a message into the list
     * @param message the message to be added
     */
    public synchronized void put(byte[] message) {
        this.messageList.add(message);
        notifyAll();
    }
    
    /**
     * get a message from the list
     * @return the first message the in list in bytes
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
