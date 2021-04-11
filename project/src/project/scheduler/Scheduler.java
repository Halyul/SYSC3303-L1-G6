/*
    Author: Zijun Hu

    This is the class for scheduler. The scheduler handle the message from the Elevator/floor subsystem.
 */

package project.scheduler;

import java.net.*;
import java.util.*;
import java.time.*;

import project.utils.*;
import project.scheduler.src.*;
import project.scheduler.src.errorHandling.ErrorHandling;

public class Scheduler implements Runnable {
    private Boolean firstMessage = true;
    private Boolean firstTaskAssigned = false;
    long startedTime = 0L;
    private final Database db;
    private final Sender sender = new Sender();
    private final ElevatorStatusArrayList elevatorStatusArrayList = new ElevatorStatusArrayList();
    private final ArrayList<ElevatorMeasurement> elevatorMeasurementArrayList = new ArrayList<>();
    private final ErrorHandling errorHandling = new ErrorHandling();
    private final int totalFloorNumber;
    private SchedulerState schedulerState;
    private Parser parser = new Parser();
    private final InetAddress systemAddress;
    private final int elevatorPort;
    private final int floorPort;

    /**
     * initialization
     *
     * @param db                  message data base
     * @param totalElevatorNumber total elevator number
     * @param totalFloorNumber    total floor number
     * @param address             default IP address
     * @param defaultPort         default port number
     */
    public Scheduler(Database db, int totalElevatorNumber, int totalFloorNumber, InetAddress address, int defaultPort) {
        this.db = db;
        this.systemAddress = address;
        this.elevatorPort = defaultPort + 100;
        this.floorPort = defaultPort + 200;
        this.schedulerState = SchedulerState.WaitMessage;
        for (int i = 1; i <= totalElevatorNumber; i++) {
            this.elevatorStatusArrayList.addElevator(new ElevatorStatus(i));
            this.elevatorMeasurementArrayList.add(new ElevatorMeasurement(i));
        }
        this.totalFloorNumber = totalFloorNumber;
    }

    /**
     * Forward the message to correct subsystem
     */
    public void execute() {
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
     */
    private void parseFloorMessage() {
        this.firstTaskAssigned = true;
        int userLocation = this.parser.getIdentifier();
        int userDest = this.parser.getFloor();
        String state = this.parser.getState();
        int distance = this.totalFloorNumber;
        int bestElevatorToMoveId = 0;

        for (ElevatorStatus e : this.elevatorStatusArrayList.getList()) {
            if (!this.elevatorStatusArrayList.ifElevatorError(e.getId())) {  // check if elevator in error state
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
        }

        if (bestElevatorToMoveId == 0) {  // there is no any fit elevator, reformat the message, and put back to the database
            byte[] ogMessage = this.parser.formatMessage();
            this.db.put(ogMessage);
        } else {                         // found a best fit elevator, start instruct elevator
            this.startInstructElevator(bestElevatorToMoveId, userLocation, userDest, state);
        }
    }

    /**
     * Start Instruct the elevator
     *
     * @param elevatorToMove id of elevator to move
     * @param userLocation   the location of user
     * @param userDest       user's destination
     * @param state          elevator state
     */
    private void startInstructElevator(int elevatorToMove, int userLocation, int userDest, String state) {
        ElevatorStatus currentElevatorStatus = this.elevatorStatusArrayList.getElevator(elevatorToMove - 1);
        ArrayList<Integer> nextActionList = currentElevatorStatus.getNextActionList();

        if (currentElevatorStatus.getCurrentStatus().equals("Idle")) {
            // elevator is in idle state, instruct elevator to pickup user at the user location
            System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": elevator_" + elevatorToMove + " Idle" + " - Current location: " + currentElevatorStatus.getCurrentLocation() + " - Instruction: " + "Move to: " + userLocation);
            this.sender.sendFloor("elevator", elevatorToMove, state, userLocation, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction
            errorHandling.startTimer(this.elevatorStatusArrayList, elevatorToMove);   // restart timer

            currentElevatorStatus.setCurrentAction(userLocation);       // update the local currentAction to user's location
        } else {                                                        //elevator is running
            if (this.isPrime(currentElevatorStatus.getDirection(), currentElevatorStatus.getCurrentAction(), userLocation)) {
                // user's location is prime than the current action, instruct elevator to pickup user at the user location
                int oldCurrentAction = currentElevatorStatus.getCurrentAction();

                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": elevator_" + elevatorToMove + " Moving" + " - Current location: " + currentElevatorStatus.getCurrentLocation() + " - Instruction: " + "Move to: " + userLocation);
                this.sender.sendFloor("elevator", elevatorToMove, state, userLocation, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction
                errorHandling.restartTimer(this.elevatorStatusArrayList, elevatorToMove);  // restart timer

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
        currentElevatorStatus.setCurrentStatus(state);
        currentElevatorStatus.setNextActionList(nextActionList);
        this.elevatorStatusArrayList.setElevator(elevatorToMove - 1, currentElevatorStatus);
    }

    /**
     * Handle the message from elevator subsystem
     */
    private void parseElevatorMessage() {
        this.measureTime();
        this.updateElevatorStatus();
        this.updateFloorSubsystem();
    }

    /**
     * Measure the time elapsed for each action
     */
    private void measureTime() {
        long messageParserTime = System.nanoTime();
        int elevatorID = this.parser.getIdentifier();
        ElevatorMeasurement elevatorMeasurement = this.elevatorMeasurementArrayList.get(elevatorID - 1);
        Long lastTimeStamp = elevatorMeasurement.getLastTimeStamp();

        if (lastTimeStamp != null) {
            long timeElapsed = (messageParserTime - lastTimeStamp) / 1000000;   // calculate time elapsed in millisecond
            if(timeElapsed != 0) {
                elevatorMeasurement.addTime(this.parser.getState(), timeElapsed);
            }
        }

        elevatorMeasurement.setLastTimeStamp(messageParserTime);    // renew the last time stamp
    }

    /**
     * Update Elevator Statues
     */
    private void updateElevatorStatus() {
        int elevatorID = this.parser.getIdentifier();    //Elevator start from 1 in Elevator class
        ElevatorStatus currentElevatorStatus = elevatorStatusArrayList.getElevator(elevatorID - 1);

        if (!this.elevatorStatusArrayList.ifElevatorError(elevatorID)) {
            if (this.parser.getState().equals("Idle")) {      // Idle state, stop timer
                this.errorHandling.cancelTimer(elevatorID);
            } else if (this.parser.getState().equals("Error")) {
                System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": elevator_" + elevatorID + " errored" + " - Current location: " + currentElevatorStatus.getCurrentLocation() + " - Reason: " + this.parser.getError());
                this.errorHandling.cancelTimer(elevatorID);
                this.elevatorStatusArrayList.addErrorElevator(elevatorID);      // set local elevator status to error
            } else {  // receive elevator message, restart timer
                this.errorHandling.restartTimer(this.elevatorStatusArrayList, elevatorID);
            }

            if (currentElevatorStatus.getCurrentAction() == this.parser.getFloor() && this.parser.getState().equals("OpenDoor")) { // Elevator Stop at the target floor
                if (!currentElevatorStatus.actionListEmpty()) {         // action list not empty, the elevator still have next action to do
                    int nextFloor = currentElevatorStatus.popNextStop();

                    System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": elevator_" + elevatorID + " arrived" + " - Current location: " + currentElevatorStatus.getCurrentLocation() + " - Instruction: " + "Move to: " + nextFloor);
                    this.sender.sendFloor("elevator", elevatorID, "Move", nextFloor, this.getTime(), this.systemAddress, this.elevatorPort);   // sends instruction

                    currentElevatorStatus.setCurrentAction(nextFloor);    // Update elevator's current action
                }
            }

            // Update Local Elevator Status
            currentElevatorStatus.setCurrentStatus(this.parser.getState());
            currentElevatorStatus.setDirection(this.parser.getDirection());
            currentElevatorStatus.setCurrentLocation(this.parser.getFloor());
            elevatorStatusArrayList.setElevator(elevatorID - 1, currentElevatorStatus);

            if(this.elevatorStatusArrayList.allElevatorIdle() && this.db.isEmpty() && this.firstTaskAssigned){  // check if all the tasks finished
                this.outputTimeCosts();
            }
        } else {
            this.sender.sendError("elevator", elevatorID, "InErrorList", this.parser.getFloor(), this.getTime(), this.systemAddress, this.elevatorPort);   // Tell elevator, it is
        }
    }

    /**
     * Sending update information to Floor subsystem
     */
    private void updateFloorSubsystem() {
        if (this.parser.getState().equals("Error")) {
            sender.sendError(this.parser.getRole(), this.parser.getIdentifier(), this.parser.getError(), this.parser.getFloor(), this.getTime(), this.systemAddress, this.floorPort);
        } else {
            sender.sendElevatorState(this.parser.getRole(), this.parser.getIdentifier(), this.parser.getState(), this.parser.getFloor(), this.parser.getDirection(), this.getTime(), this.systemAddress, this.floorPort);
        }
    }

    /**
     * Print out the time cost and mean for each action in console output
     */
    private void outputTimeCosts(){
        long endedTime = System.nanoTime();
        long timeElapsed = (endedTime - this.startedTime) / 1000000;

        System.out.println("\nThe total time used to finish all tasks is: " + timeElapsed + " ms.");

        for (ElevatorMeasurement elevatorMeasurement : elevatorMeasurementArrayList){   // print out average for each elevator
            elevatorMeasurement.printOutAverage();
        }
    }

    /**
     * Get next message from database, then parse the message using parser, return the parser
     */
    private void getNextMessage() {
        if(this.firstMessage){      // do the timestamp if this message is the first message
            this.startedTime = System.nanoTime();
            this.firstMessage = false;
        }
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
        System.out.println(getTime() + " - " + Thread.currentThread().getName() + ": running.");
        while (true) {
            try {
                this.execute();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
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

    /**
     * Main process to start the Scheduler
     *
     * @param args No args needed
     */
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

        Scheduler scheduler = new Scheduler(db, 4, 22, schedulerAddress, schedulerPort);
        Thread schedulerThread = new Thread(scheduler, "Scheduler ");
        schedulerThread.start();

        Receiver r = new Receiver(db, schedulerPort);
        Thread receiverThread = new Thread(r, "Receiver");
        receiverThread.start();
    }
}
