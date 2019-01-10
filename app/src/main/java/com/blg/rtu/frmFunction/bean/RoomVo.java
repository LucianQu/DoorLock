package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;
import java.util.List;

public class RoomVo implements Serializable {
    private int defaultRoom;
    private List<DeviceVo> devices;
    private long homeId;
    private String imageName;
    private String name;
    private String remark;
    private long tid;

    public int getDefaultRoom() {
        return defaultRoom;
    }

    public void setDefaultRoom(int defaultRoom) {
        this.defaultRoom = defaultRoom;
    }

    public List<DeviceVo> getDevices() {
        return devices;
    }

    public void setDevices(List<DeviceVo> devices) {
        this.devices = devices;
    }

    public long getHomeId() {
        return homeId;
    }

    public void setHomeId(long homeId) {
        this.homeId = homeId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }
}
