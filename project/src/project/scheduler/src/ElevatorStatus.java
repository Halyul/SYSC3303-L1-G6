/*
  Author: Zijun Hu

  This is a class to record elevator stats locally for Scheduler
 */

package project.scheduler.src;
import project.utils.Sender;

import java.net.InetAddress;
import java.util.ArrayList;

public class ElevatorStatus {
    private String currentStatus;
    private int currentAction;
    private int currentLocation;
//    private int currentDirection;        // -1 None; 0 Down; 1 Up
    private ArrayList<Integer> nextActionList = new ArrayList<Integer>();

    public ElevatorStatus() {
        this.currentStatus = "Idle";
        this.getStates();
    }

    public void getStates() {
    }

    /**
     * Set elevator's current status
     */
    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    /**
     * Get elevator's current status
     */
	public String getCurrentStatus() {
		return this.currentStatus;
	}

    /**
     * Set elevator's current action (Ex. floor going to)
     */
	public void setCurrentAction( int currentAction){
		this.currentAction = currentAction;
	}

    /**
     * Get elevator's current action (Ex. floor going to)
     */
	public int getCurrentAction(){
		return this.currentAction;
	}

    /**
     * Set elevator's current location (Ex. floor elevator current at)
     */
	public void setCurrentLocation(int currentLocation) {
		this.currentLocation = currentLocation;
	}

    /**
     * Get elevator's current location
     */
    public int getCurrentLocation() {
        return this.currentLocation;
    }

    /**
     * Get elevator's direction (Ex. Up or down)
     */
    public int getDirection() {
        return this.currentLocation;
    }


    /**
     * Add a floor number that need stop to the back to next action list
     */
    public void addNextStop(int floorNum){
    	this.nextActionList.add(floorNum);
	}

    /**
     * Pop the first floor number that need stop from next action list
     */
	public int popNextStop(){
        int nextStop = this.nextActionList.get(0);
        this.nextActionList.remove(0);
        return nextStop;
    }

    public boolean actionListEmpty(){
        return this.nextActionList.size() == 0;
    }

    public boolean isIdle(){
        return this.currentStatus.equals("Idle");
    }
}
