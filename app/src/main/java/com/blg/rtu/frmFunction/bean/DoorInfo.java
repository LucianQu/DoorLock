package com.blg.rtu.frmFunction.bean;

import java.io.Serializable;

/**************************************************************************
*
*  File name: DoorInfo.java
*  Date: 2018/6/27 18:44
*  Copyright (c) 2013-2018 by Automic.
*  Author: Lucian_qls
*  E-mail: 1017557706@qq.com
*
*  Function:
*   .
 * ChangeLog:
 * v1.0
**************************************************************************/

public class DoorInfo implements Serializable {
    private String dtuId ;
    private DoorStatus rltState ;
    private String succ ;
    private String error ;

    public String getDtuId() {
        return dtuId;
    }

    public void setDtuId(String dtuId) {
        this.dtuId = dtuId;
    }

    public DoorStatus getRltState() {
        return rltState;
    }

    public void setRltState(DoorStatus rltState) {
        this.rltState = rltState;
    }

    public String getSucc() {
        return succ;
    }

    public void setSucc(String succ) {
        this.succ = succ;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
