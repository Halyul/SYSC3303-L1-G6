package project.elevator.src;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Door {
    // The time the doors take to open and close in seconds
    private static final double doorTime = 9.3; 
    private boolean isOpened = false;

    public Door() { }
    
    /**
     * To simulate the action of opening the door
     * @param stuckAtClose true to set the door to be not able to open
     * @return true if the door is working properly, false otherwise
     */
    public boolean open(boolean stuckAtClose) {
        if (!stuckAtClose) {
            if (!isOpened) {
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door is opening.");
                try {
                    Thread.sleep((long) (1000 * this.doorTime / 2));
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                this.isOpened = true;
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door opened.");
            } else {
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door already opened.");
            }
            return true;
        } else {
            System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door is unable to open.");
            return false;
        }
    }
    
    /**
     * To simulate the action of closing the door
     * @param stuckAtOpen true to set the door to be not able to close
     * @return true if the door is working properly, false otherwise
     */
    public boolean close(boolean stuckAtOpen) {
        if (!stuckAtOpen) {
            if (isOpened) {
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door is closing.");
                try {
                    Thread.sleep((long) (1000 * this.doorTime / 2));
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                this.isOpened = false;
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door closed.");
            } else {
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door already closed.");
            }
            return true;
        } else {
            System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": Door is unable to close.");
            return false;
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
    
    /**
     * get the state of the door
     * @return as described above
     */
    public boolean getState() {
        return this.isOpened;
    }

}
