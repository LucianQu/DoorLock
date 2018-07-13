package com.blg.rtu.protocol.p206.cd10_50;


public class Data_10_50 {
	

	private String passWord ;

	public String toString(){
		String s = "\n" ;
		s += "password" + "=" + passWord ;
		return s ;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
}
