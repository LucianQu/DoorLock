package com.blg.rtu.protocol.p206;

import com.blg.rtu.protocol.RtuCommand;
import com.blg.rtu.protocol.p206.F1.Param_F1;
import com.blg.rtu.protocol.p206.cd02.Param_02;
import com.blg.rtu.protocol.p206.cd10_50.Param_10;
import com.blg.rtu.util.StringValueForServer;

public class CommandCreator {


	public RtuCommand cd_02(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId) ;
		com.setCommandCode(Code206.cd_02) ;
		Param_02 p = new Param_02() ;
		p.setType(Param_02.type_F2) ;
		com.getParams().put(Param_02.KEY, p) ;
		return com ;
	}

	public RtuCommand cd_10(String regionNum, String clientId, String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setCommandCode(Code206.cd_10) ;
		com.setRtuId(rtuId);
		Param_10 p = new Param_10() ;
		p.setNewId(regionNum + clientId) ;
		com.getParams().put(Param_10.KEY, p) ;
		return com ;
	}

	public RtuCommand cd_F_1(int command, String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setCommandCode(Code206.cd_F1) ;
		com.setRtuId(rtuId);
		Param_F1 p = new Param_F1() ;
		p.setDoorContral(command);
		com.getParams().put(Param_F1.KEY, p) ;
		return com ;
	}
	public RtuCommand cd_F_2(int command, String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setCommandCode(Code206.cd_F2) ;
		com.setRtuId(rtuId);
		Param_F1 p = new Param_F1() ;
		p.setDoorContral(command);
		com.getParams().put(Param_F1.KEY, p) ;
		return com ;
	}

	public RtuCommand cd_F_3(int command, String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setCommandCode(Code206.cd_F3) ;
		com.setRtuId(rtuId);
		Param_F1 p = new Param_F1() ;
		p.setDoorContral(command);
		com.getParams().put(Param_F1.KEY, p) ;
		return com ;
	}

	public RtuCommand cd_F_4(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId);
		com.setCommandCode(Code206.cd_F4) ;
		return com ;
	}
	

	public RtuCommand cd_10_(String hexId, String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setCommandCode(Code206.cd_10) ;
		com.setRtuId(rtuId);
		Param_10 p = new Param_10() ;
		p.setHexNewId(hexId) ;
		com.getParams().put(Param_10.KEY, p) ;
		return com ;
	}
	
	public RtuCommand cd_50(){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(StringValueForServer.protocolBroastCommandRtuId) ;//用广播命令的RTU ID
		com.setCommandCode(Code206.cd_50) ;
		return com ;
	}
	
	public RtuCommand cd_74(){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(StringValueForServer.protocolBroastCommandRtuId) ;//用广播命令的RTU ID
		com.setCommandCode(Code206.cd_74) ;
		return com ;
	}
	

	
	public RtuCommand cd_51(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId);
		com.setCommandCode(Code206.cd_51) ;
		return com ;
	}
	
	

	
	public RtuCommand cd_52(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId);
		com.setCommandCode(Code206.cd_52) ;
		return com ;
	}
	

	

	
	public RtuCommand cd_55(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId);
		com.setCommandCode(Code206.cd_55) ;
		return com ;
	}
	
	public RtuCommand cd_56(String rtuId){
		RtuCommand com = new RtuCommand() ;
		com.setRtuId(rtuId);
		com.setCommandCode(Code206.cd_56) ;
		return com ;
	}

}
