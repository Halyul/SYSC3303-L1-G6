/*
  Author: Zijun Hu

  This is the class for scheduler. The scheduler handle the message from the Elevator/floor subsystem.
 */

package project.scheduler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.util.Collections;

import project.utils.*;

import project.scheduler.src.*;


public class Scheduler implements Runnable {
    private Database db = new Database();
    private Sender sender = new Sender();
    private ArrayList<ElevatorStatus> elevatorStatusArrayList = new ArrayList<ElevatorStatus>();
    private int totalFloorNumber;
    private SchedulerState schedulerState;
    private Parser parser = new Parser();
    private InetAddress systemAddress;
    private int elevatorPort, floorPort;

    public Scheduler(Database db, int totalElevatorNumber, int totalFloorNumber, InetAddress address, int defaultPort) {
        this.db = db;
        this.systemAddress = address;
        this.elevatorPort = defaultPort + 100;
        this.floorPort = defaultPort + 200;
        this.schedulerState = SchedulerState.WaitMessage;
        for (int i = 1; i <= totalElevatorNumber; i++) {
            this.elevatorStatusArrayList.add(new ElevatorStatus(i));
        }
        this.totalFloorNumber = totalFloorNumber;
    }

    /**
     * Forward the message to correct subsystem
     */
    public void execute(){
        if (this.schedulerState == SchedulerState.WaitMessage) {
            getNextMessage();
        } else if (this.schedulerState == SchedulerState.parseFloorMessage) {
            this.parseFloorMessage();
            this.schedulerState = SchedulerState.WaitMessage;
        } else if (this.schedulerState == SchedulerState.parseElevatorMessage) {
            this.parseElevatorMessage();
            this.schedulerState = SchedulerState.WaitMessage;
        }
    }


    /**
     * Handle the message from floor subsystem
     *
     * @throws Exception in case sender throw an error
     */
    private void parseFloorMessage(){
        int userLocation = this.parser.getIdentifier();
        int userDest = this.parser.getFloor();
        int distance = this.totalFloorNumber;
        int bestElevatorToMoveId = 0;

        for (ElevatorStatus e : this.elevatorStatusArrayList) {
            boolean fit = false;
            if (e.getCurrentStatus().equals("Idle")) {      // elevator in idle state
                fit = true;
            } else {        // elevator is current moving
                if (this.parser.getDirection() == e.getDirection() && this.inPickUpRange(e.getDirection(), e.getCurrentLocation(), e.getLastAction(), userLocation)) {
                    fit = true;
                }
            }
            if (fit) {      // this elevator is available to pick the user
                if (distance > Math.abs(e.getCurrentLocation() - userLocation)) {
                    bestElevatorToMoveId = e.getId();
                    distance = e.getCurrentLocation() - userLocation;
                }
            }
        }

        if (bestElevatorToMoveId == 0) {  // there is no any fit elevator, reformat the message, and put back to the database
            byte[] ogMessage = this.parser.formatMessage();
            this.db.put(ogMessage);
        } else {                         // found a best fit elevator, start instruct elevator
            this.startInstructElevator(bestElevatorToMoveId, userLocation, userDest);
        }
    }

    /**
     * Start Instruct the elevator
     *
     * @param elevatorToMove id of elevator to move
     * @param userLocation   the location of user
     * @param userDest       user's destination
     */
    private void startInstructElevator(int elevatorToMove, int userLocation, int userDest){
        ElevatorStatus currentElevatorStatus = this.elevatorStatusArrayList.get(elevatorToMove - 1);
        ArrayList<Integer> nextActionList = currentElevatorStatus.getNextActionList();

        if (currentElevatorStatus.getCurrentStatus().equals("Idle")) {
            // elevator is in idle state, instruct elevator to pickup user at the user location

            System.out.println("Scheduler: elevator_" + elevatorToMove + " Idle - Message: " + "Move to:" + userLocation);

            this.sender.sendFloor("elevator", elevatorToMove, "Move", userLocation, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction
            currentElevatorStatus.setCurrentAction(userLocation);       // update the local currentAction to user's location
        } else {                                                        //elevator is running
            if (this.isPrime(currentElevatorStatus.getDirection(), currentElevatorStatus.getCurrentAction(), userLocation)) {
                // user's location is prime than the current action, instruct elevator to pickup user at the user location
                int oldCurrentAction = currentElevatorStatus.getCurrentAction();

                System.out.println("Scheduler: elevator_" + elevatorToMove + " moving, new task - Message: " + "Move to:" + userLocation);

                this.sender.sendFloor("elevator", elevatorToMove, "Move", userLocation, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction
                currentElevatorStatus.setCurrentAction(userLocation);       // update the local currentAction to user's location

                nextActionList.add(oldCurrentAction);  // add lower prime action(old current action) back to action list
            } else {
                // user's location isn't prime than the current action
                if (!nextActionList.contains(userLocation))
                    nextActionList.add(userLocation);   // Add user's location to nextActionList
            }
        }

        if (!nextActionList.contains(userDest)) {   // Add user destination to nextActionList if there is no duplicate one
            nextActionList.add(userDest);
        }

        //sort nextActionList
        if (currentElevatorStatus.getDirection() == 1) {            // elevator's direction up, sort the actionList from low to high
            Collections.sort(nextActionList);
        } else if (currentElevatorStatus.getDirection() == 0) {     // elevator's direction down, sort the actionList from high to low
            Collections.sort(nextActionList);
            Collections.reverse(nextActionList);
        }

        // Update Local Elevator Status
        currentElevatorStatus.setCurrentStatus("Move");
        currentElevatorStatus.setNextActionList(nextActionList);
        this.elevatorStatusArrayList.set(elevatorToMove - 1, currentElevatorStatus);
    }

    /**
     * Handle the message from elevator subsystem
     *
     * @throws Exception in case sender throw an error
     */
    private void parseElevatorMessage(){
        this.updateElevatorStatus();
        this.updateFloorSubsystem();
    }


    /**
     * Update Elevator Statues
     *
     * @throws Exception in case sender throw an error
     */
    private void updateElevatorStatus(){
        int elevatorID = this.parser.getIdentifier();    //Elevator start from 1 in Elevator class
        ElevatorStatus currentElevatorStatus = elevatorStatusArrayList.get(elevatorID - 1);

        if (currentElevatorStatus.getCurrentAction() == this.parser.getFloor() && this.parser.getState().equals("OpenDoor")) { // Elevator Stop at the target floor
            if (!currentElevatorStatus.actionListEmpty()) {         // action list not empty, the elevator still have next action to do
                int nextFloor = currentElevatorStatus.popNextStop();

                System.out.println("Scheduler: elevator_" + elevatorID + " arrived, next task - Message: " + "Move to:" + nextFloor);
                sender.sendFloor("elevator", elevatorID, "Move", nextFloor, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction
                currentElevatorStatus.setCurrentAction(nextFloor);    // Update elevator's current action
            }
        }

        // Update Local Elevator Status
        currentElevatorStatus.setCurrentStatus(this.parser.getState());
        currentElevatorStatus.setDirection(this.parser.getDirection());
        currentElevatorStatus.setCurrentLocation(this.parser.getFloor());
        elevatorStatusArrayList.set(elevatorID - 1, currentElevatorStatus);
    }

    /**
     * Sending update information to Floor subsystem
     *
     * @throws Exception in case sender throw an error
     */
    private void updateFloorSubsystem(){
        sender.sendElevatorState(this.parser.getRole(), this.parser.getIdentifier(), this.parser.getState(), this.parser.getFloor(), this.parser.getDirection(), this.getTime(), this.systemAddress, this.floorPort);
    }

    /**
     * Get next message from database, then parse the message using parser, return the parser
     */
    private void getNextMessage() {
        byte[] inputMessage = this.db.get();
        this.parser = new Parser(inputMessage);
        if (this.parser.getRole().equals("Floor")) {
            this.schedulerState = SchedulerState.parseFloorMessage;
        } else if (this.parser.getRole().equals("Elevator")) {
            this.schedulerState = SchedulerState.parseElevatorMessage;
        } else {
            this.schedulerState = SchedulerState.WaitMessage;            // ignore message
        }
    }

    /**
     * check if the user's location is in a reasonable pickup range of the elevator
     *
     * @param direction       elevator direction
     * @param currentLocation elevator's current location
     * @param lastAction      the last stop in the action list
     * @param userLocation    user's location
     * @return boolean in reasonable pickup range or not
     */
    private boolean inPickUpRange(int direction, int currentLocation, int lastAction, int userLocation) {
        if (direction == 1) {
            return (userLocation >= currentLocation + 2) && (userLocation < lastAction);
        } else if (direction == 0) {
            return (userLocation > lastAction) && (userLocation <= currentLocation - 2);
        } else {
            return false;
        }
    }

    /**
     * Check if the userLocation is primer than the currentAction
     * (if the elevator will reach the user's location earlier than the currentAction floor)
     *
     * @param direction     elevator direction
     * @param currentAction floor that elevator current going
     * @param userLocation  user's location
     * @return if the userLocation is prime than currentAction
     */
    private boolean isPrime(int direction, int currentAction, int userLocation) {
        if (direction == 1) {
            return (userLocation < currentAction);
        } else if (direction == 0) {
            return (userLocation > currentAction);
        } else {
            return false;
        }
    }


    /**
     * Get localtime in UTC timezone
     *
     * @return localTime in UTC timezone
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
            this.execute();
        }
    }

    /**
     * Method create for test purpose
     *
     * @return return current state
     */
    public SchedulerState getState() {
        return this.schedulerState;
    }

    public static void main(String[] args) {
        InetAddress schedulerAddress = null;
        int schedulerPort = 12000;
        Database db = new Database();

        try {
            schedulerAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scheduler scheduler = new Scheduler(db, 4, 7, schedulerAddress, schedulerPort);
        Thread schedulerThread = new Thread(scheduler, "Scheduler ");
        schedulerThread.start();

        Receiver r = new Receiver(db, schedulerPort);
        Thread receiverThread = new Thread(r, "Receiver");
        receiverThread.start();
    }
}
