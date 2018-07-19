package com.blg.rtu.protocol.p206.F3;


public class Data_F3 {
	private int jiaQuan ; //3byte 1-3	甲醛
	/*单位：mg/m³ 小数点3位（无符号整型）
	a)	例： 1.234mg/m³  发送数据为：0x04D2 （低字节在前）
	*/
	private int doorStatus ; 	//1byte 门状态 //2.	门状态 ：0x01 开门 0x02 关门  0x03 停止
	private int doorOpen; 	//门角度 0-180  //3.	门开关角度：1字节 0度~180度
	private int lockFlag; //锁标记 固定04  //4.	锁标记：1字节 固定0x04
	private int lockStatus ; //锁状态 d2 有电1 没电0 在原点1 不在原点0， 开锁1 关锁0
	//锁状态 数据定义
	//数值	    D7	D6	D5	D4	D3	D2	      D1	     D0
	//     1	N	N	N	N	N	锁有电	锁在原点	    开锁
	//     0	N	N	N	N	N	锁无电	锁不在原点	关锁

	private int powerFlag ; //电源标记 固定05  //6.	电源标记：1字节 固定为0x05
	private int powerStatus ; //电源状态  //a） 0x00 系统没电  b） 0x01 系统有电
	private int alarmFlag;	//报警标记 固定06  8.	报警标记：1字节 固定为0x06
	private int alarmStaus;	//报警状态
	//电源状态 数据定义
	//数值	D7	D6	D5	D4	D3	D2		D1	D0
	//	1	N	N	N	N	N	门异常	过流	欠压
	//	0	N	N	N	N	N	门正常	正常	正常
	private boolean hasPower ;  //锁有电
	private boolean isLockInitPosition ;  //锁在原点
	private boolean isOpenLock ; //开锁
	private boolean isDoorNormal ; //门正常
	private boolean isNormalCurrent ; //正常电流
	private boolean isNormalPower; // 正常电压

	public String toString(){
		String s = "\n" ;
			s += "智能门应答：" + "\n" +
					" 甲醛浓度：" + jiaQuan + "\n" +
					" 门状态：" + doorStatus + "\n" +
					" 门开关角度：" + doorOpen + "\n" +
					" 锁标记：" + lockFlag + "\n" +
					" 锁状态：" + lockStatus + "\n" +
					" 电源标记：" + powerFlag+ "\n" +
					" 电源状态：" + powerStatus + "\n" +
					" 报警标记：" + alarmFlag+ "\n" +
					" 报警状态：" + alarmStaus+ "\n"
					;
		return s ;
	}

	public boolean isHasPower() {
		return hasPower;
	}

	public void setHasPower(boolean hasPower) {
		this.hasPower = hasPower;
	}

	public boolean isLockInitPosition() {
		return isLockInitPosition;
	}

	public void setLockInitPosition(boolean lockInitPosition) {
		isLockInitPosition = lockInitPosition;
	}

	public boolean isOpenLock() {
		return isOpenLock;
	}

	public void setOpenLock(boolean openLock) {
		isOpenLock = openLock;
	}

	public boolean isDoorNormal() {
		return isDoorNormal;
	}

	public void setDoorNormal(boolean doorNormal) {
		isDoorNormal = doorNormal;
	}

	public boolean isNormalCurrent() {
		return isNormalCurrent;
	}

	public void setNormalCurrent(boolean normalCurrent) {
		isNormalCurrent = normalCurrent;
	}

	public boolean isNormalPower() {
		return isNormalPower;
	}

	public void setNormalPower(boolean lowPower) {
		isNormalPower = lowPower;
	}

	public int getJiaQuan() {
		return jiaQuan;
	}

	public void setJiaQuan(int jiaQuan) {
		this.jiaQuan = jiaQuan;
	}

	public int getDoorStatus() {
		return doorStatus;
	}

	public void setDoorStatus(int doorStatus) {
		this.doorStatus = doorStatus;
	}

	public int getDoorOpen() {
		return doorOpen;
	}

	public void setDoorOpen(int doorOpen) {
		this.doorOpen = doorOpen;
	}

	public int getLockFlag() {
		return lockFlag;
	}

	public void setLockFlag(int lockFlag) {
		this.lockFlag = lockFlag;
	}

	public int getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(int lockStatus) {
		this.lockStatus = lockStatus;
	}

	public int getPowerFlag() {
		return powerFlag;
	}

	public void setPowerFlag(int powerFlag) {
		this.powerFlag = powerFlag;
	}

	public int getPowerStatus() {
		return powerStatus;
	}

	public void setPowerStatus(int powerStatus) {
		this.powerStatus = powerStatus;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(int alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public int getAlarmStaus() {
		return alarmStaus;
	}

	public void setAlarmStaus(int alarmStaus) {
		this.alarmStaus = alarmStaus;
	}
}
