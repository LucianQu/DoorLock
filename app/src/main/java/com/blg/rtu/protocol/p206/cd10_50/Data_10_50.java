package com.blg.rtu.protocol.p206.cd10_50;


public class Data_10_50 {
	

	private int passWord ;

	public String toString(){
		String s = "\n" ;
		s += "password" + "=" + passWord ;
		return s ;
	}

	public int getPassWord() {
		return passWord;
	}

	public void setPassWord(int passWord) {
		this.passWord = passWord;
	}
}
