package com.migu.schedule.info;

public class Task {
    private int taskId;
    private int consumption;
    public int nodeId = -1;
    public int getTaskId(){  return taskId; }
    public void setTaskId(int taskId)
    {
        this.taskId = taskId;
    }

    public int getConsumption() {
        return consumption;
    }

    public Task(int taskId, int consumption){
        this.taskId = taskId;
        this.consumption = consumption;
    }
}
