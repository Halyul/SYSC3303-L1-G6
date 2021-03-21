package project.scheduler.src.errorHandling;

import project.scheduler.src.ElevatorStatusArrayList;

import java.util.ArrayList;
import java.util.TimerTask;

public class ErrorHandling {
    private final ArrayList<TaskTimer> timerArrayList = new ArrayList<>();

    public ErrorHandling(){}

    public void startTimer(ElevatorStatusArrayList elevatorStatusArrayList, int id){
        TaskTimer taskTimer = new TaskTimer(id);
        TimerTask task = new ErrorHandlingTask(elevatorStatusArrayList, id);
        taskTimer.schedule(task,5000);
        timerArrayList.add(taskTimer);
        System.out.println("Timer Start: " + id);
    }

    public void cancelTimer(int id){
        if(timerExist(id)) {
            TaskTimer timeToRemove = null;
            for (TaskTimer t : timerArrayList) {
                timeToRemove = t;
                if (t.getId() == id) {
                    t.cancel();
                    break;
                }
            }

            if (timeToRemove != null) {
                this.timerArrayList.remove(timeToRemove);
                System.out.println("Timer canceled: " + timeToRemove.getId());
                System.out.println("cancelTimer: Timer list size" + this.timerArrayList.size());
            }
        }else{
            System.out.println("Timer isn't exist!");
        }
    }

    public void restartTimer(ElevatorStatusArrayList elevatorStatusArrayList, int id){
        this.cancelTimer(id);
        this.startTimer(elevatorStatusArrayList,id);
        System.out.println("Timer list size" + this.timerArrayList.size());
    }

    private boolean timerExist(int id){
        for (TaskTimer t : timerArrayList){
            if(t.getId() == id){
                return true;
            }
        }
        return false;
    }

}
