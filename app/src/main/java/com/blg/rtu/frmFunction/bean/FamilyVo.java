package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;
import java.util.List;

public class FamilyVo implements Serializable {
    private FamilyMemberVo admin;
    private List<FamilyMemberVo> leaguers;
    private String name;
    private List<RoomVo> rooms;
    private long tid;
    private long userId;

    public FamilyMemberVo getAdmin() {
        return admin;
    }

    public void setAdmin(FamilyMemberVo admin) {
        this.admin = admin;
    }

    public List<FamilyMemberVo> getLeaguers() {
        return leaguers;
    }

    public void setLeaguers(List<FamilyMemberVo> leaguers) {
        this.leaguers = leaguers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RoomVo> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomVo> rooms) {
        this.rooms = rooms;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
