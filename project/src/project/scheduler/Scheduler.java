/*
  Author: Zijun Hu

  This is the class for scheduler. The scheduler receive messages from Elevator/floor subsystem and deal with them.
 */

package project.scheduler;

import java.util.ArrayList;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.Collections;

import project.floor.Floor;
import project.utils.Sender;
import project.utils.Parser;
import project.utils.Database;

import project.elevator.*;
import project.scheduler.src.*;

public class Scheduler implements Runnable {
    private Database db = new Database();
    private Sender sender = new Sender(db);
    private ArrayList<ElevatorStatus> elevatorStatusArrayList = new ArrayList<ElevatorStatus>();
    private int totalFloorNumber;
    private Elevator elevator_1;  // need to remove later
    private Floor floor_1;    // need to remove later
    private SchedulerState schedulerState;
    private Parser parser = new Parser();

    public Scheduler(Database db, int totalElevatorNumber, int totalFloorNumber) {
        this.db = db;
        this.schedulerState = SchedulerState.WaitMessage;
        for (int i = 1; i <= totalElevatorNumber; i++) {
            elevatorStatusArrayList.add(new ElevatorStatus(i));
        }
        this.totalFloorNumber = totalFloorNumber;
    }

    public Scheduler(Database db, Elevator elevator, Floor floor, int totalFloorNumber) {// need to remove later
        this.db = db;
        this.schedulerState = SchedulerState.WaitMessage;
        elevatorStatusArrayList.add(new ElevatorStatus(1));
        this.elevator_1 = elevator;
        this.floor_1 = floor;
        this.totalFloorNumber = totalFloorNumber;
    }

    /**
     * Forward the message to correct subsystem
     */
    public void execute() throws Exception {
        if (this.schedulerState == SchedulerState.WaitMessage) {
            getNextMessage();
        } else if (this.schedulerState == SchedulerState.parseMessageFromFloor) {
            this.parseMessageFromFloor();
            this.schedulerState = SchedulerState.WaitMessage;
        } else if (this.schedulerState == SchedulerState.parseMessageFromElevator) {
            this.parseMessageFromElevator();
            this.schedulerState = SchedulerState.WaitMessage;
        }
    }

    /**
     * Send instruction to elevator
     */
    private void parseMessageFromFloor() throws Exception {
        int userLocation = this.parser.getIdentifier();
        int userDest = this.parser.getFloor();
        int distance = this.totalFloorNumber;
        int bestElevatorToMoveId = 0;

        for (ElevatorStatus e : this.elevatorStatusArrayList) {
            boolean fit = false;
            if (e.getCurrentStatus().equals("Idle")) {
                fit = true;
            } else {
                if (this.parser.getDirection() == e.getDirection() && this.inPickUpRange(e.getDirection(), e.getCurrentLocation(), e.getLastAction(), userLocation)) {
                    fit = true;
                }
            }
            if(fit){
                if(distance > Math.abs(e.getCurrentLocation() - userLocation)){
                    bestElevatorToMoveId = e.getId();
                }
            }
        }

        if(bestElevatorToMoveId == 0){  // not fit elevator, reformat the message, and put back to the database
            byte[] ogMessage = this.parser.formatMessage();
            this.db.put(ogMessage);
        }else {                         // there is a fir elevator, start instruct elevator
            this.startInstructElevator(bestElevatorToMoveId, userLocation, userDest);
        }
    }

    /**
     * Start Instruct an elevator
     *
     * @param elevatorToMove id of elevator to move
     * @param userLocation the location of user
     * @param userDest  user's destination
     */
    private void startInstructElevator(int elevatorToMove, int userLocation, int userDest) throws Exception {
        ElevatorStatus currentElevatorStatus = this.elevatorStatusArrayList.get(elevatorToMove - 1);
        ArrayList<Integer> nextActionList = currentElevatorStatus.getNextActionList();

        if (currentElevatorStatus.getCurrentStatus().equals("Idle")){   //elevator Idle
            String message = "state:Move" + ";floor:" + userLocation;        // need to remove later
            System.out.println("S: elevator Idle" + message);
            this.elevator_1.put(message.getBytes());    // need to remove later
//            this.sender.sendFloor("elevator", elevatorToMove, "Move", userLocation, this.getTime(), InetAddress.getLocalHost(), 12000);
            currentElevatorStatus.setCurrentAction(userLocation);       // set current action to user's location
        }else {     //elevator is running
            if(this.isPrime(currentElevatorStatus.getDirection(),currentElevatorStatus.getCurrentAction(),userLocation)){   // user's location is prime than current action
                int oldCurrentAction = currentElevatorStatus.getCurrentAction();

                String message = "state:Move" + ";floor:" + userLocation;        // need to remove later
                System.out.println("S: elevator running, new task" + message);
                this.elevator_1.put(message.getBytes());    // need to remove later
//                this.sender.sendFloor("elevator", elevatorToMove, "Move", userLocation, this.getTime(), InetAddress.getLocalHost(), 12000);
                currentElevatorStatus.setCurrentAction(userLocation);       // change current action to user's location

                nextActionList.add(oldCurrentAction);  // add old current action to next action list
            }else{          // user's location isn't prime than current action
                if(!nextActionList.contains(userLocation))
                    nextActionList.add(userLocation);   // Add user current location to nAL
            }
        }

        if(!nextActionList.contains(userDest)) {
            nextActionList.add(userDest);   // Add user dest to nAL
        }
        if(currentElevatorStatus.getDirection() == 1){          // elevator going up
            Collections.sort(nextActionList);   // sort action list from low to high
        }else if(currentElevatorStatus.getDirection() == 0){    // elevator going down
            Collections.sort(nextActionList);
            Collections.reverse(nextActionList);    // sort action list from high to low
        }

        currentElevatorStatus.setCurrentStatus("Move");
//        if(currentElevatorStatus.getCurrentAction() < currentElevatorStatus.getCurrentLocation()) {
//            currentElevatorStatus.setDirection(0);
//        }else if(currentElevatorStatus.getCurrentAction() > currentElevatorStatus.getCurrentLocation()){
//            currentElevatorStatus.setDirection(1);
//        }else{
//            currentElevatorStatus.setDirection(-1);
//        }
        currentElevatorStatus.setNextActionList(nextActionList);    // renew action list in elevator state class
        this.elevatorStatusArrayList.set(elevatorToMove - 1, currentElevatorStatus);  // Update Local Elevator Status
    }

    private void parseMessageFromElevator() throws Exception {
        this.updateElevatorStatus();
        this.updateFloorSubsystem();
    }

    /**
     * Sending update information to elevator subsystem
     */
    private void updateElevatorStatus() throws Exception {
        int elevatorID = this.parser.getIdentifier();    //Elevator start from 1 in Elevator class
        ElevatorStatus currentElevatorStatus = elevatorStatusArrayList.get(elevatorID - 1);

        if (currentElevatorStatus.getCurrentAction() == this.parser.getFloor() && this.parser.getState().equals("OpenDoor")) { // Elevator Stop at the target floor
            if (!currentElevatorStatus.actionListEmpty()) {   // Still got action to do
                int nextFloor = currentElevatorStatus.popNextStop();    // get next action

                String message = "state:Move" + ";floor:" + nextFloor;        // Formal the message for elevator's next action
                System.out.println("S: elevator arrived, next task" + message);
                this.elevator_1.put(message.getBytes());    // Sending next action to elevator
//                sender.sendFloor("elevator", elevatorID, "Move", nextFloor, this.getTime(), InetAddress.getLocalHost(), 12000);

                currentElevatorStatus.setCurrentAction(nextFloor);    // Update elevator's current action
            }
        }

        currentElevatorStatus.setCurrentLocation(this.parser.getFloor());
        currentElevatorStatus.setCurrentStatus(this.parser.getState());
        currentElevatorStatus.setDirection(this.parser.getDirection());

        elevatorStatusArrayList.set(elevatorID - 1, currentElevatorStatus);  // Update Local Elevator Status
    }


    /**
     * Sending update information to Floor subsystem
     */
    private void updateFloorSubsystem() throws Exception {
//        sender.sendDirection("scheduler", 0, "Move", this.parser.getDirection(), this.getTime(), InetAddress.getLocalHost(), 12000);
    }

    /**
     * Get next message from database, then parse the message using parser, return the parser
     */
    private void getNextMessage() {
        byte[] inputMessage = this.db.get();
        this.parser = new Parser(inputMessage);
        if (this.parser.getRole().equals("Floor")) {
            this.schedulerState = SchedulerState.parseMessageFromFloor;
        } else if (this.parser.getRole().equals("Elevator")) {
            this.schedulerState = SchedulerState.parseMessageFromElevator;
        } else {
            // ignore message
            this.schedulerState = SchedulerState.WaitMessage;
        }
    }

    private boolean inPickUpRange(int direction, int currentLocation, int lastAction, int userLocation) {
        if (direction == 1) {
            return (userLocation >= currentLocation + 2) && (userLocation < lastAction);
        } else if (direction == 0) {
            return (userLocation > lastAction) && (userLocation <= currentLocation - 2);
        } else {
            return false;
        }
    }

    private boolean isPrime(int direction, int currentAction,int userLocation){
        if(direction == 1){
            return(userLocation < currentAction);
        }else if(direction == 0){
            return(userLocation > currentAction);
        }else{
            return false;
        }
    }

    /**
     *
     */
    public SchedulerState getState() {
        return this.schedulerState;
    }

    /**
     * Get current time in epoch seconds
     *
     * @return as described above
     */
    private long getTime() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.toEpochSecond(ZoneOffset.UTC);
    }

    /**
     * @see java.lang.Runnable #run()
     */
    @Override
    public void run() {
        while (true) {
            try {
                this.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
