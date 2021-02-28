/*
  Author: Zijun Hu

  This is the class for scheduler. The scheduler receive messages from Elevator/floor subsystem and deal with them.
 */

package project.scheduler;

import java.util.ArrayList;
import java.time.ZoneOffset;
import java.net.InetAddress;
import java.time.LocalDateTime;

import project.Floor;
import project.utils.Sender;
import project.utils.Parser;
import project.utils.Database;

import project.elevator.*;
import project.scheduler.src.*;

public class Scheduler implements Runnable {
    private Database db = new Database();
    private Sender sender = new Sender(db);
    private ArrayList<ElevatorStatus> elevatorStatus = new ArrayList<ElevatorStatus>();
    private int totalFloorNumber;
    private Elevator elevator_1;  // need to remove later
    private Floor floor_1;    // need to remove later
    private SchedulerState schedulerState;
    private Parser parser = new Parser();

    public Scheduler(Database db, int totalElevatorNumber, int totalFloorNumber) {
        this.db = db;
        this.schedulerState = SchedulerState.WaitMessage;
        for (int i = 1; i <= totalElevatorNumber; i++) {
            elevatorStatus.add(new ElevatorStatus());
        }
        this.totalFloorNumber = totalFloorNumber;
    }

    public Scheduler(Database db, Elevator elevator, Floor floor) {// need to remove later
        this.db = db;
        this.schedulerState = SchedulerState.WaitMessage;
        elevatorStatus.add(new ElevatorStatus());
        this.elevator_1 = elevator;
        this.floor_1 = floor;
    }

    /**
     * Forward the message to correct subsystem
     */
    public void execute() throws Exception {
        if(this.schedulerState == SchedulerState.WaitMessage) {
            getNextMessage();
        } else if(this.schedulerState == SchedulerState.InstructElevator) {
            this.instructElevator();
            this.schedulerState = SchedulerState.WaitMessage;
        } else if(this.schedulerState == SchedulerState.UpdateSubsystem) {
            this.updateElevatorStatus();
            this.updateFloorSubsystem();
            this.schedulerState = SchedulerState.WaitMessage;
        }
    }


    
    /**
     * Send instruction to elevator
     *
     */
    private void instructElevator() throws Exception {
        int userLocation = this.parser.getIdentifier();
        int userDest = this.parser.getFloor();
        int distance = this.totalFloorNumber;
        int elevatorToMove = 0;
        ElevatorStatus e = this.elevatorStatus.get(elevatorToMove);

        if(e.isIdle()) {
            String message = "state:Move" + ";floor:" + userLocation;        // need to remove later
            this.elevator_1.put(message.getBytes());    // need to remove later
            sender.sendFloor("scheduler", 0, "Move", userLocation, this.getTime(), InetAddress.getLocalHost(), 12000);
            e.setCurrentStatus("Busy");
            e.setCurrentAction(userLocation);
        }else{
            e.addNextStop(userLocation);
        }
        e.addNextStop(userDest);
    }

    /**
     * Sending update information to elevator subsystem
     *
     */
    private void updateElevatorStatus() throws Exception {
        int elevatorID = this.parser.getIdentifier() - 1;    //Elevator start from 1 in Elevator class
        ElevatorStatus currentElevatorStatus = elevatorStatus.get(elevatorID);
        if(currentElevatorStatus.getCurrentAction() == this.parser.getFloor()){ // Elevator Stop at the target floor
            if(!currentElevatorStatus.actionListEmpty()) {   // Still got action to do
                int nextFloor = currentElevatorStatus.popNextStop();    // get next action

                String message = "state:Move" + ";floor:" + nextFloor;        // Formal the message for elevator's next action
                this.elevator_1.put(message.getBytes());    // Sending next action to elevator
                sender.sendFloor("scheduler", 0, "Move", nextFloor, this.getTime(), InetAddress.getLocalHost(), 12000);

                currentElevatorStatus.setCurrentAction(nextFloor);    // Update local elevator Status
            }else{
                currentElevatorStatus.setCurrentStatus("Idle");
            }
        }
        currentElevatorStatus.setCurrentLocation(this.parser.getFloor());
        currentElevatorStatus.setCurrentStatus(this.parser.getState());
        elevatorStatus.set(elevatorID, currentElevatorStatus);  // Update Local Elevator Status
    }

    /**
     * Sending update information to Floor subsystem
     *
     */
    private void updateFloorSubsystem() throws Exception {
        sender.sendDirection("scheduler", 0, "Move", this.parser.getDirection(), this.getTime(), InetAddress.getLocalHost(), 12000);
    }

    /**
     * Get next message from database, then parse the message using parser, return the parser
     *
     */
    private void getNextMessage() {
        byte[] inputMessage = this.db.get();
        this.parser.parse(inputMessage);
        if(this.parser.getRole().equals("Floor")){
            this.schedulerState = SchedulerState.InstructElevator;
        }else if (this.parser.getRole().equals("Elevator")){
            this.schedulerState = SchedulerState.UpdateSubsystem;
        }else{
            // ignore message
            this.schedulerState = SchedulerState.WaitMessage;
        }
    }

    /**
     * @see java.lang.Runnable #run()
     */
    @Override
    public void run(){
        while(true) {
            try {
                this.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     *
     */
    public SchedulerState getState(){
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
}
