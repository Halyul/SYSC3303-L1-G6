/*
    Author: Zijun Hu

    This is the class to storage all the status of elevators; also include the elevators in error status.
 */

package project.scheduler.src;

import java.util.ArrayList;

public class ElevatorStatusArrayList {
    private final ArrayList<ElevatorStatus> elevatorStatusArrayList = new ArrayList<>();
    private final ArrayList<Integer> errorElevatorArrayList = new ArrayList<>();

    public ElevatorStatusArrayList() {
    }

    /**
     * Add elevator status to elevator status arraylist
     *
     * @param e elevatorStatus
     */
    public synchronized void addElevator(ElevatorStatus e) {
        this.elevatorStatusArrayList.add(e);
    }

    /**
     * Return the ArrayList of elevator status
     *
     * @return ArrayList of ElevatorStatus
     */
    public synchronized ArrayList<ElevatorStatus> getList() {
        return this.elevatorStatusArrayList;
    }

    /**
     * Return the elevator status using input id
     *
     * @param id elevator id
     * @return elevator status
     */
    public synchronized ElevatorStatus getElevator(int id) {
        return this.elevatorStatusArrayList.get(id);
    }

    /**
     * Set the elevator status using input id
     *
     * @param id elevator id
     * @param e  elevator status
     */
    public synchronized void setElevator(int id, ElevatorStatus e) {
        this.elevatorStatusArrayList.set(id, e);
    }

    /**
     * Add error elevator into error elevator arraylist
     *
     * @param id the id of error elevator
     */
    public synchronized void addErrorElevator(int id) {
        this.errorElevatorArrayList.add(id);
    }

    /**
     * Check if elevator id inside error elevator arraylist
     *
     * @param id the id of elevator to be checked
     * @return if elevator is in error status
     */
    public synchronized Boolean ifElevatorError(int id) {
        return this.errorElevatorArrayList.contains(id);
    }

    /**
     * check if all elevator is in idle or error state
     *
     * @return true if all elevator is in idle or error state
     */
    public synchronized Boolean allElevatorIdle() {
        for (ElevatorStatus e : elevatorStatusArrayList) {
            if (!e.getCurrentStatus().equals("Idle") && !e.getCurrentStatus().equals("Error")) {
                return false;
            }
        }
        return true;
    }
}
