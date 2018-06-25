package com.blg.rtu.protocol.p206.F1;

import java.io.Serializable;


public class Param_F implements Serializable{
	
	private static final long serialVersionUID = 20180625005700123L;

	public static final String KEY = Param_F.class.getName() ;
	private int doorContral ;
	public String toString(){
		String s = "\n" ;
		s += "智能门控制：" + "\n" +
		" 控制命令：" + doorContral + "\n" ;
		return s ;
	}

	public Param_F() {
	}

	public int getDoorContral() {
		return doorContral;
	}

	public void setDoorContral(int doorContral) {
		this.doorContral = doorContral;
	}
}
