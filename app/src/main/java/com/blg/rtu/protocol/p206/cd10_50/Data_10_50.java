package com.blg.rtu.protocol.p206.cd10_50;


public class Data_10_50 {
	

	private String passWord ;
	private boolean isDoor = true;
	public String toString(){
		String s = "\n" ;
		s += "password" + "=" + passWord +"\n 设备类型为门="+isDoor;
		return s ;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public boolean isDoor() {
		return isDoor;
	}

	public void setDoor(boolean door) {
		isDoor = door;
	}
}
