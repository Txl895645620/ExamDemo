package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.Server;
import com.migu.schedule.info.Task;
import com.migu.schedule.info.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/*
*类名和方法不能修改
 */
public class Schedule {

    List<Server> RunningServers = new ArrayList<Server>();
    List<Task> RunningTasks = new ArrayList<Task>();
    List<Task> HangUpTasks = new ArrayList<Task>();
    List<Server> HangUpServers = new ArrayList<Server>();

    public int init() {
        // TODO 方法未实现
        RunningServers.clear();
        RunningTasks.clear();
        HangUpTasks.clear();
        HangUpServers.clear();
        if(RunningServers.size()==0 && RunningTasks.size()==0 &&
                HangUpServers.size()==0 && HangUpTasks.size()==0)
        {
            return ReturnCodeKeys.E001;
        }
        return ReturnCodeKeys.E000;
    }


    public int registerNode(int nodeId) {
        // TODO 方法未实现
        if(nodeId<=0)
        {
            return ReturnCodeKeys.E004;
        }
        Server server = getRunningServer(nodeId);
        if(server == null)
        {
            server = getHangUpServer(nodeId);
        }
        if(server != null)
        {
            return ReturnCodeKeys.E005;
        }
        if(server == null)
        {
            HangUpServers.add(new Server(nodeId));
            return ReturnCodeKeys.E003;
        }

        return ReturnCodeKeys.E000;
    }

    public int unregisterNode(int nodeId) {
        // TODO 方法未实现
        if(nodeId<=0)
        {
            return ReturnCodeKeys.E004;
        }
        Server server = getRunningServer(nodeId);
        if(server == null)
        {
            server = getHangUpServer(nodeId);
        }
        if(server == null)
        {
            return ReturnCodeKeys.E007;
        }
        if(server != null)
        {
            if(server.tasks.size() >0 )
            {
                HangUpTasks.addAll(server.tasks);
                for(int i=0;i<HangUpTasks.size();++i)
                {
                    HangUpTasks.get(i).nodeId = -1;
                }
            }
            deleteServer(nodeId);
            return ReturnCodeKeys.E006;
        }
        return ReturnCodeKeys.E000;
    }


    public int addTask(int taskId, int consumption) {
        // TODO 方法未实现
        if(taskId <= 0)
        {
            return ReturnCodeKeys.E009;
        }
        Task temp = getRunningTask(taskId);
        if(temp == null)
        {
            temp = getHangUpTask(taskId);
        }
        if(temp != null)
        {
            return ReturnCodeKeys.E010;
        }
        if(temp == null)
        {
            Task task = new Task(taskId,consumption);
            HangUpTasks.add(task);
            return ReturnCodeKeys.E008;
        }

        return ReturnCodeKeys.E000;
    }


    public int deleteTask(int taskId) {
        // TODO 方法未实现
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        boolean success = false;
        for (int i = 0; i < HangUpTasks.size(); ++i)
        {
            if(HangUpTasks.get(i).getTaskId() == taskId)
            {
                HangUpTasks.remove(i);
                success = true;
                break;
            }
        }
        for(int i = 0;i<RunningTasks.size();++i)
        {
            if(RunningTasks.get(i).getTaskId() == taskId)
            {
                RunningTasks.remove(i);
                success = true;
                break;
            }
        }
        for(int i = 0;i<RunningServers.size();++i)
        {
            for(int j=0;j<RunningServers.get(i).tasks.size();++j)
            {
                if(RunningServers.get(i).tasks.get(j).getTaskId() == taskId)
                {
                    RunningServers.get(i).tasks.remove(j);
                    success = true;
                    break;
                }
            }
        }
        for(int i = 0;i<HangUpServers.size();++i)
        {
            for(int j=0;j<HangUpServers.get(i).tasks.size();++j)
            {
                if(HangUpServers.get(i).tasks.get(j).getTaskId() == taskId)
                {
                    HangUpServers.get(i).tasks.remove(j);
                    success = true;
                    break;
                }
            }
        }
        if(success)
        {
            return ReturnCodeKeys.E011;
        }
        if(!success)
        {
            return ReturnCodeKeys.E012;
        }
        return ReturnCodeKeys.E000;
    }


    public int scheduleTask(int threshold) {
        // TODO 方法未实现
        if(threshold<=0)
        {
            return ReturnCodeKeys.E002;
        }

        List<Server> servers = new ArrayList<Server>();
        servers.addAll(RunningServers);
        servers.addAll(HangUpServers);

        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(RunningTasks);
        tasks.addAll(HangUpTasks);

        getBest(servers,tasks);
        if(lowThen(threshold,servers))
        {
            return ReturnCodeKeys.E013;
        }
        if(!lowThen(threshold,servers))
        {
            return ReturnCodeKeys.E014;
        }
        return ReturnCodeKeys.E000;
    }


    public int queryTaskStatus(List<TaskInfo> tasks) {
        // TODO 方法未实现
        if(tasks == null)
        {
            return ReturnCodeKeys.E016;
        }
        tasks.clear();
        for(int i=0;i<HangUpTasks.size();++i)
        {
            TaskInfo taskInfo = new TaskInfo();
            taskInfo.setTaskId(HangUpTasks.get(i).getTaskId());
            taskInfo.setNodeId(-1);
            tasks.add(taskInfo);
        }
        for(int i=0;i<RunningServers.size();++i)
        {
            for(int j=0;j<RunningServers.get(i).tasks.size();++j)
            {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setNodeId(RunningServers.get(i).getNoteId());
                taskInfo.setTaskId(RunningServers.get(i).tasks.get(j).getTaskId());
                tasks.add(taskInfo);
            }
        }
        for(int i=0;i<tasks.size()-1;++i)
        {
            for(int j=i+1;j<tasks.size();++j)
            {
                if(tasks.get(i).getTaskId()> tasks.get(j).getTaskId())
                {
                    TaskInfo temp = tasks.get(i);
                    tasks.set(i,tasks.get(j));
                    tasks.set(j,temp);
                }
            }
        }
        if(tasks.size() > 0)
        {
            return ReturnCodeKeys.E015;
        }
        return ReturnCodeKeys.E000;
    }

    public Server getRunningServer(int nodeId){
        for(int i=0;i<RunningServers.size();++i)
        {
            if(RunningServers.get(i).getNoteId() == nodeId)
            {
                return RunningServers.get(i);
            }
        }
        return null;
    }
    public Server getHangUpServer(int nodeId){
        for(int i=0;i<HangUpServers.size();++i)
        {
            if(HangUpServers.get(i).getNoteId() == nodeId)
            {
                return HangUpServers.get(i);
            }
        }
        return null;
    }
    public void deleteServer(int nodeId){
        for(int i=0;i<RunningServers.size();++i)
        {
            if(RunningServers.get(i).getNoteId() == nodeId)
            {
                RunningServers.remove(i);
                return ;
            }
        }
        for(int i=0;i<HangUpServers.size();++i)
        {
            if(HangUpServers.get(i).getNoteId() == nodeId)
            {
                HangUpServers.remove(i);
                return ;
            }
        }
        return;
    }
    public Task getRunningTask(int taskId)
    {
        for(int i=0;i<RunningTasks.size();++i)
        {
            if(RunningTasks.get(i).getTaskId() == taskId)
            {
                return RunningTasks.get(i);
            }
        }
        return null;
    }
    public Task getHangUpTask(int taskId)
    {
        for(int i=0;i<HangUpTasks.size();++i)
        {
            if(HangUpTasks.get(i).getTaskId() == taskId)
            {
                return HangUpTasks.get(i);
            }
        }
        return null;
    }
    public void getBest(List<Server> servers,List<Task> tasks){
        if(servers.size()==1)
        {
            servers.get(0).tasks.addAll(tasks);
        }

        for(int i=0;i<servers.size()-1;++i)
        {
            for(int j=i+1;j<servers.size();++j)
            {
                if(servers.get(i).getNoteId()<servers.get(j).getNoteId())
                {
                    Server temp = servers.get(i);
                    servers.set(i,servers.get(j));
                    servers.set(j,temp);
                }
            }
        }
        for(int i=0;i<tasks.size()-1;++i)
        {
            for(int j=i+1;j<tasks.size();++j)
            {
                if(tasks.get(i).getTaskId()> tasks.get(j).getTaskId())
                {
                    Task temp = tasks.get(i);
                    tasks.set(i,tasks.get(j));
                    tasks.set(j,temp);
                }
            }
        }

        for(int i=0;i<tasks.size();++i)
        {
            Server temp1 = servers.get(0);
            Server temp2 = servers.get(1);
            for(int j=2;j<servers.size();++j)
            {
                if(servers.get(j).totalCost()<temp1.totalCost() || servers.get(j).totalCost()<temp2.totalCost())
                {
                    if(temp1.totalCost() < temp2.totalCost())
                    {
                        temp2 = servers.get(j);
                    }
                    else
                    {
                        temp1 = servers.get(j);
                    }
                }
            }
            if(temp1.totalCost()==0)
            {
                temp1.tasks.add(tasks.get(i));
                continue;
            }
            if(temp2.totalCost() ==0)
            {
                temp2.tasks.add(tasks.get(i));
                continue;
            }
            if(temp1.totalCost()>temp2.totalCost() && temp1.totalCost()>tasks.get(i).getConsumption())
            {
                temp2.tasks.add(tasks.get(i));
                continue;
            }
            if(temp2.totalCost()>temp1.totalCost() && temp2.totalCost()>tasks.get(i).getConsumption())
            {
                temp1.tasks.add(tasks.get(i));
                continue;
            }
            else
            {
                temp1.tasks.addAll(temp2.tasks);
                temp2.tasks.clear();
                temp2.tasks.add(tasks.get(i));
            }
        }
    }

    public boolean lowThen(int maxDelta,List<Server> servers)
    {
        if(servers.size()==1)
        {
            return true;
        }
        int min = 0;
        int max = 1;
        if(servers.size()>1)
        {
            if(servers.get(0).totalCost()>servers.get(1).totalCost())
            {
                min = 1;
                max = 0;
            }
            for(int i=2;i<servers.size();++i)
            {
                if(servers.get(i).totalCost()<servers.get(min).totalCost())
                {
                    min = i;
                }
                else if(servers.get(i).totalCost()>servers.get(max).totalCost())
                {
                    max = i;
                }
            }

        }
        if(servers.get(max).totalCost() - servers.get(min).totalCost() <= maxDelta)
        {
            return true;
        }
        return false;
    }
}
