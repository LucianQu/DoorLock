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

public class DoorStatus implements Serializable {
   private float hcho ;
   private int doorState ;
   private int angle ;
   private int lockMark ;
   private int lockState ;
   private byte[] lockStates ;
   private int powerMark ;
   private int powerState ;
   private int warnMark ;
   private int warnState ;
   private byte[] warnStates ;

   public float getHcho() {
      return hcho;
   }

   public void setHcho(float hcho) {
      this.hcho = hcho;
   }

   public int getDoorState() {
      return doorState;
   }

   public void setDoorState(int doorState) {
      this.doorState = doorState;
   }

   public int getAngle() {
      return angle;
   }

   public void setAngle(int angle) {
      this.angle = angle;
   }

   public int getLockMark() {
      return lockMark;
   }

   public void setLockMark(int lockMark) {
      this.lockMark = lockMark;
   }

   public int getLockState() {
      return lockState;
   }

   public void setLockState(int lockState) {
      this.lockState = lockState;
   }

   public byte[] getLockStates() {
      return lockStates;
   }

   public void setLockStates(byte[] lockStates) {
      this.lockStates = lockStates;
   }

   public int getPowerMark() {
      return powerMark;
   }

   public void setPowerMark(int powerMark) {
      this.powerMark = powerMark;
   }

   public int getPowerState() {
      return powerState;
   }

   public void setPowerState(int powerState) {
      this.powerState = powerState;
   }

   public int getWarnMark() {
      return warnMark;
   }

   public void setWarnMark(int warnMark) {
      this.warnMark = warnMark;
   }

   public int getWarnState() {
      return warnState;
   }

   public void setWarnState(int warnState) {
      this.warnState = warnState;
   }

   public byte[] getWarnStates() {
      return warnStates;
   }

   public void setWarnStates(byte[] warnStates) {
      this.warnStates = warnStates;
   }
}
