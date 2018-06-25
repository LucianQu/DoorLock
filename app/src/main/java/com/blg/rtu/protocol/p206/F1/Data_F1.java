package com.blg.rtu.protocol.p206.F1;


public class Data_F1 {

	private int doorOpen;
	private int jiaQuan ;
	private int doorAlarmPower ;
	private int doorAlarmClose ;
	private int lockStatus ;
	private int lockInit ;
	private int lockAlarm ;
	private int lockPower ;

	public String toString(){
		String s = "\n" ;
			s += "智能门应答：" + "\n" +
					" 门开关角度：" + doorOpen + "\n" +
					" 甲醛浓度：" + jiaQuan + "\n" +
					" 门报警（电池欠压）：" + doorAlarmPower+ "\n" +
					" 门报警（关门故障）：" + doorAlarmClose+ "\n" +
					" 锁状态：" + lockStatus+ "\n" +
					" 锁原点：" + lockInit+ "\n" +
					" 锁报警：" + lockAlarm+ "\n" +
					" 锁电源：" + lockPower+ "\n"
					;
		return s ;
	}

	public int getDoorOpen() {
		return doorOpen;
	}

	public void setDoorOpen(int doorOpen) {
		this.doorOpen = doorOpen;
	}

	public int getJiaQuan() {
		return jiaQuan;
	}

	public void setJiaQuan(int jiaQuan) {
		this.jiaQuan = jiaQuan;
	}

	public int getDoorAlarmPower() {
		return doorAlarmPower;
	}

	public void setDoorAlarmPower(int doorAlarmPower) {
		this.doorAlarmPower = doorAlarmPower;
	}

	public int getDoorAlarmClose() {
		return doorAlarmClose;
	}

	public void setDoorAlarmClose(int doorAlarmClose) {
		this.doorAlarmClose = doorAlarmClose;
	}

	public int getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}

	public int getLockInit() {
		return lockInit;
	}

	public void setLockInit(int lockInit) {
		this.lockInit = lockInit;
	}

	public int getLockAlarm() {
		return lockAlarm;
	}

	public void setLockAlarm(int lockAlarm) {
		this.lockAlarm = lockAlarm;
	}

	public int getLockPower() {
		return lockPower;
	}

	public void setLockPower(int lockPower) {
		this.lockPower = lockPower;
	}
}
