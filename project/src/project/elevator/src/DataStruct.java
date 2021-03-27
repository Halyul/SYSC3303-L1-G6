package project.elevator.src;

public class DataStruct {
    private String state = "";
    private int floor;
    private boolean iswaiting = false;
    private String error = "";
    
    public DataStruct() {
        finished();
    }

    /**
     * Set the current scheduler command
     * @param state the state of the command
     * @param floor the dest floor
     */
    public synchronized void setState(String state, int floor) {
        if (!state.equals("Move") && !state.equals("Error")) {
            this.error = state;
            state = "Move";
        }
        this.state = state;
        this.floor = floor;
        this.iswaiting = true;
        notifyAll();
    }

    /**
     * Finish a scheduler command
     */
    public synchronized void finished() {
        this.state = "";
        this.floor = 0;
        this.iswaiting = false;
    }

    /**
     * Wait for next scheduler command
     */
    public synchronized void waitForCommand() {
        while (!this.iswaiting) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
        return;
    }

    /**
     * Get the state of current command
     * @return as described
     */
    public String getState() {
        return this.state;
    }

    /**
     * Get the dest floor of current command
     * @return as described
     */
    public int getFloor() {
        return this.floor;
    }

    /**
     * Check if a scheduler command is waiting
     * @return
     */
    public boolean isWaiting() {
        return this.iswaiting;
    }

    /**
     * Get the error of current command
     * @return as described
     */
    public String getError() {
        return this.error;
    }
}
