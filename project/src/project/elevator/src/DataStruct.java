package project.elevator.src;

public class DataStruct {
    private String state = "";
    private int floor;
    private boolean iswaiting = false;
    private String error = "";
    
    public DataStruct() {
        finished();
    }
    
    public void setState(String state, int floor) {
        if (!state.equals("Move") && !state.equals("Error")) {
            this.error = state;
            state = "Move";
        }
        this.state = state;
        this.floor = floor;
        this.iswaiting = true;
    }
    
    public void finished() {
        this.state = "";
        this.floor = 0;
        this.iswaiting = false;
    }
    
    public String getState() {
        return this.state;
    }
    
    public int getFloor() {
        return this.floor;
    }
    
    public boolean isWaiting() {
        return this.iswaiting;
    }

    public String getError() {
        return this.error;
    }
}
