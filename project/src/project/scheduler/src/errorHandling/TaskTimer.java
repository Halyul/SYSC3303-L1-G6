package project.scheduler.src.errorHandling;

import java.util.Timer;
import java.util.TimerTask;

public class TaskTimer {
    int id;
    Timer timer;
    public TaskTimer(int id){
        this.id = id;
        this.timer = new Timer();
    }

    public void schedule(TimerTask timerTask, int delay){
        this.timer.schedule(timerTask,delay);
    }

    public void cancel(){
        this.timer.cancel();
    }

    public int getId(){
        return this.id;
    }
}
