package com.blg.rtu.util;

import com.blg.rtu3.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimeTask {
    private Timer timer;
    private TimerTask task;
    private long time = 2000;
    private boolean status = false ;

    public MyTimeTask(long time, TimerTask task) {
        this.task = task;
        this.time = time;
        if (timer == null){
            timer=new Timer();
        }
    }
    public void start(){
        try {
            if (!status) {
                status = true ;
                if (timer != null) {
                    timer.schedule(task, 0, time);//每隔time时间段就执行一次
                }
            }
        }catch (Exception e) {
            LogUtils.e("Lucian--->", "开始定时器任务异常");
        }

    }
    public void destory(){
        try {
            if (status) {
                status = false ;
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    if (task != null) {
                        task.cancel();  //将原任务从队列中移除
                    }
                }
            }else {
                if (task != null) {
                    task.cancel();  //将原任务从队列中移除
                }
            }
        }catch (Exception e) {
            LogUtils.e("Lucian--->", "销毁定时器任务异常");
        }
    }

    public void stop(){
        try {
            if (status) {
                status = false ;
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    if (task != null) {
                        task.cancel();  //将原任务从队列中移除
                    }
                }
            }
        }catch (Exception e) {
            LogUtils.e("Lucian--->", "停止定时器任务异常");
        }
    }
}
