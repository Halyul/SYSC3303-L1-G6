/*
    Author: Zijun Hu

    This is the class for scheduler to do the measurement for the elevator.
    Unit: millisecond(ms)
 */
package project.scheduler.src;

import project.utils.Calculator;
import java.util.ArrayList;

public class ElevatorMeasurement {
    private final int elevatorId;
    private Long lastTimeStamp = 0L;

    private final ArrayList<Long> moveTime = new ArrayList<>();
    private final ArrayList<Long> stopTime = new ArrayList<>();
    private final ArrayList<Long> openDoorTime = new ArrayList<>();
    private final ArrayList<Long> closeDoorTime = new ArrayList<>();

    public ElevatorMeasurement(int elevatorId){
        this.elevatorId = elevatorId;
    }

    /**
     * record the time elapsed into the array list
     * @param type  action type
     * @param timeElapsed   time elapsed
     */
    public void addTime(String type, long timeElapsed) {
        switch (type) {
            case "Move" -> this.moveTime.add(timeElapsed);
            case "Stop" -> this.stopTime.add(timeElapsed);
            case "OpenDoor" -> this.openDoorTime.add(timeElapsed);
            case "CloseDoor" -> this.closeDoorTime.add(timeElapsed);
        }
    }

    /**
     * Set the last timestamp of the last action
     * @param lastTimeStamp the last timestamp of the last action from elevator
     */
    public void setLastTimeStamp(Long lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    /**
     * Get the last timestamp
     * @return  last timestamp
     */
    public Long getLastTimeStamp(){
        return this.lastTimeStamp;
    }

    /**
     * Printout average time elapsed in console output
     */
    public void printOutAverage(){
        Calculator c = new Calculator();
        Long moveMean = c.mean(this.moveTime);
        double moveCI = c.confidenceInterval(this.moveTime, 99.9);
        Long stopMean = c.mean(this.stopTime);
        double stopCI = c.confidenceInterval(this.stopTime, 99.9);
        Long openDoorMean = c.mean(this.openDoorTime);
        double openDoorCI = c.confidenceInterval(this.openDoorTime, 99.9);
        Long closeDoorMean = c.mean(this.closeDoorTime);
        double closeDoorCI = c.confidenceInterval(this.closeDoorTime, 99.9);

        System.out.println("\nFor elevator " + this.elevatorId + ": ");
        System.out.println("The mean of time elapsed on moving between two floors is: " + moveMean + " ± " + moveCI + " ms with 99.9% confidence interval.");
        System.out.println("The mean of time elapsed on stopping the elevator is: " + stopMean + " ± " + stopCI + " ms with 99.9% confidence interval.");
        System.out.println("The mean of time elapsed on open the elevator door is: " + openDoorMean + " ± " + openDoorCI + " ms with 99.9% confidence interval.");
        System.out.println("The mean of time elapsed on close the elevator door is: " + closeDoorMean + " ± " + closeDoorCI + " ms with 99.9% confidence interval.");
    }

    /**
     * get arraylist of moveTime; for test purpose
     * @return moveTime
     */
    public ArrayList<Long> getMoveTime(){
        return moveTime;
    }

}
