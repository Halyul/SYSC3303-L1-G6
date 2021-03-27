/*
    Author: Zijun Hu

    This is a class to record elevator stats locally for Scheduler
 */

package project.scheduler.src;

import java.util.ArrayList;

public class ElevatorStatus {
    private final int id;
    private String currentStatus;
    private int currentAction;
    private int currentLocation;
    private int currentDirection;        // -1 none; 0 Down; 1 Up
    private ArrayList<Integer> nextActionList = new ArrayList<>();

    public ElevatorStatus(int id) {
        this.id = id;
        this.currentStatus = "Idle";
        this.currentDirection = -1;
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
    public void setCurrentAction(int currentAction) {
        this.currentAction = currentAction;
    }

    /**
     * Get elevator's current action (Ex. floor going to)
     */
    public int getCurrentAction() {
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
     * Get elevator's current direction
     */
    public void setDirection(int currentDirection) {
        this.currentDirection = currentDirection;
    }

    /**
     * Get elevator's direction (Ex. Up or down)
     */
    public int getDirection() {
        return this.currentDirection;
    }

    /**
     * Get elevator id
     *
     * @return elevator id
     */
    public int getId() {
        return this.id;
    }

    /**
     * Add a floor number that need stop to the back to next action list
     */
    public void addLastAction(int floorNum) {
        this.nextActionList.add(floorNum);
    }

    /**
     * Get the elevator's next action list
     */
    public ArrayList<Integer> getNextActionList() {
        return this.nextActionList;
    }

    /**
     * Set the elevator's next action list
     */
    public void setNextActionList(ArrayList<Integer> nextActionList) {
        this.nextActionList = nextActionList;
    }

    /**
     * Pop the first floor number that need stop from next action list
     */
    public int popNextStop() {
        int nextStop = this.nextActionList.get(0);
        this.nextActionList.remove(0);
        return nextStop;
    }

    /**
     * Get the last action of the elevator
     */
    public int getLastAction() {
        if (this.nextActionList.size() == 0) {
            return this.currentAction;
        }
        return this.nextActionList.get(this.nextActionList.size() - 1);
    }

    /**
     * Check if the next action list is empty
     */
    public boolean actionListEmpty() {
        return this.nextActionList.size() == 0;
    }

    /**
     * Check if the elevator is Idle
     */
    public boolean isIdle() {
        return this.currentStatus.equals("Idle");
    }
}
