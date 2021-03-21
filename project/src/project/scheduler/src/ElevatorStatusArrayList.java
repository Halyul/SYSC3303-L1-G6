package project.scheduler.src;

import java.util.ArrayList;

public class ElevatorStatusArrayList {
    private final ArrayList<ElevatorStatus> elevatorStatusArrayList = new ArrayList<>();
    private final ArrayList<Integer> errorElevatorArrayList = new ArrayList<>();

    public ElevatorStatusArrayList(){}

    public synchronized void addElevator(ElevatorStatus e){
        this.elevatorStatusArrayList.add(e);
    }

    public synchronized ArrayList<ElevatorStatus> getList(){
        return this.elevatorStatusArrayList;
    }

    public synchronized ElevatorStatus getElevator(int id){
        return this.elevatorStatusArrayList.get(id);
    }

    public synchronized void setElevator(int id, ElevatorStatus e){
        this.elevatorStatusArrayList.set(id,e);
    }

    public synchronized void addErrorElevator(int id){
        this.errorElevatorArrayList.add(id);
    }

    public synchronized Boolean ifElevatorError(int id){
        return this.errorElevatorArrayList.contains(id);
    }
}
