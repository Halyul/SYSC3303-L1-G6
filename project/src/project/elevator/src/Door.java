package project.elevator.src;

public class Door {
    // The time the doors take to open and close in seconds
    private static final double doorTime = 9.3; 
    private boolean isOpened = false;
    private boolean stuckAtOpen;
    private boolean stuckAtClose;
    
    public Door(boolean stuckAtOpen, boolean stuckAtClose) {
        this.stuckAtOpen = stuckAtOpen;
        this.stuckAtClose = stuckAtClose;
        if (stuckAtOpen) {
            this.isOpened = true;
        }
        if (stuckAtClose) {
            this.isOpened = false;
        }
    }
    
    /**
     * To simulate the action of opening the door
     */
    public boolean open() {
        if (!stuckAtClose) {
            if (!isOpened) {
                System.out.println(Thread.currentThread().getName() + ": Door is opening.");
                try {
                    Thread.sleep((long) (1000 * this.doorTime / 2));
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                this.isOpened = true;
                System.out.println(Thread.currentThread().getName() + ": Door opened.");
            } else {
                System.out.println(Thread.currentThread().getName() + ": Door already opened.");
            }
            return true;
        } else {
            System.out.println(Thread.currentThread().getName() + ": Door is unable to open.");
            return false;
        }
    }
    
    /**
     * To simulate the action of closing the door
     */
    public boolean close() {
        if (!stuckAtOpen) {
            if (isOpened) {
                System.out.println(Thread.currentThread().getName() + ": Door is closing.");
                try {
                    Thread.sleep((long) (1000 * this.doorTime / 2));
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                this.isOpened = false;
                System.out.println(Thread.currentThread().getName() + ": Door closed.");
            } else {
                System.out.println(Thread.currentThread().getName() + ": Door already closed.");
            }
            return true;
        } else {
            System.out.println(Thread.currentThread().getName() + ": Door is unable to close.");
            return false;
        }
    }
    
    /**
     * get the state of the door
     * @return as described above
     */
    public boolean getState() {
        return this.isOpened;
    }
    
    /**
     * Check if the door is stuck at open
     * @return as described above
     */
    public boolean getStuckAtOpen() {
        return this.stuckAtOpen;
    }
    
    /**
     * Check if the door is stuck at close
     * @return as described above
     */
    public boolean getStuckAtClose() {
        return this.stuckAtClose;
    }
}
