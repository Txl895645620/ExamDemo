package com.migu.schedule.info;

import java.util.ArrayList;

public class Server {
    private int nodeId;
    public ArrayList<Task> tasks = new ArrayList<Task>();
    public Server(int nodeId){
        this.nodeId = nodeId;
    }
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }
    public int getNoteId(){
        return this.nodeId;
    }
    public int totalCost(){
        int total = 0;
        for(int i=0;i<tasks.size();++i)
        {
            total += tasks.get(i).getConsumption();
        }
        return total;
    }
}
