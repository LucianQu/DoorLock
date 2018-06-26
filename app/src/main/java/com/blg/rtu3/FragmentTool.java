package com.blg.rtu3;

import android.app.FragmentManager;

import com.blg.rtu.frmChannel.ChFragment_01;
import com.blg.rtu.frmChannel.ChFragment_02;
import com.blg.rtu.frmChannel.ChFragment_03;
import com.blg.rtu.frmChannel.ChFragment_04;
import com.blg.rtu.frmChannel.ChFragment_05;
import com.blg.rtu.frmFunction.F_01_010;
import com.blg.rtu.frmFunction.F_1_0;
import com.blg.rtu.frmFunction.F_1_1;
import com.blg.rtu.frmFunction.F_1_2;
import com.blg.rtu.frmFunction.F_1_3;
import com.blg.rtu.frmFunction.FrmParent;
import com.blg.rtu.frmNoProtocol.NpFragment_01;

import java.util.ArrayList;
import java.util.List;

public class FragmentTool {

	private MainActivity mainAct ;
		
	public NpFragment_01 fragment_np01 ;

	
	public ChFragment_01 fragment_ch01 ;
	public ChFragment_02 fragment_ch02 ;
	public ChFragment_03 fragment_ch03 ;
	public ChFragment_04 fragment_ch04 ;
	public ChFragment_05 fragment_ch05 ;

	private static List<FrmParent> frms = new ArrayList<FrmParent>() ;;
	public F_1_0 f_1_0 ;
	public F_1_1 f_1_1 ;
	public F_1_2 f_1_2 ;
	public F_1_3 f_1_3 ;




	public F_01_010 f_01_010 ;


	
	public FragmentTool(MainActivity mainAct){
		this.mainAct = mainAct ;
		
        FragmentManager fm = this.mainAct.getFragmentManager();
		f_1_0 = (F_1_0) fm.findFragmentById(R.id.f_1_00) ;
		f_1_1 = (F_1_1) fm.findFragmentById(R.id.f_1_01) ;
		f_1_2 = (F_1_2) fm.findFragmentById(R.id.f_1_02) ;
		f_1_3 = (F_1_3) fm.findFragmentById(R.id.f_1_03) ;
		frms.add(f_1_0);
		frms.add(f_1_1);
		frms.add(f_1_2);
		frms.add(f_1_3);

        f_01_010 = (F_01_010)fm.findFragmentById(R.id.f_01_010) ;
        frms.add(f_01_010);
	}
	
	
	public void pageViewSelected(int pageIndex) {
		switch (pageIndex) {
		case 0://第0页
			//fragment_ch01.outThisPage() ;
			break;
		case 1://第1页
			//fragment_ch01.outThisPage() ;
			break;
		case 2://第2页
			//fragment_ch01.outThisPage() ;
			break;
		case 3://第3页
			//fragment_ch01.inThisPage() ;
			break;
		}
	}
	
	/**
	 * 打开所有功能项
	 */
	public void openAllFunctionFragment(){
		for(FrmParent frm : frms){
			frm.openFuncFrm() ;
		}
	}
	
	
	
	/**
	 * 关闭所有功能项
	 */
	public void closeAllFunctionFragment(){
		for(FrmParent frm : frms){
			frm.closeFuncFrm() ;
			
		}
	}
	
	/**
	 * 功能项右侧图标复原
	 */
	public void resetLabelImgFunctionFragment(){
		for(FrmParent frm : frms){
			frm.resetLabelImg() ;
		}
	}
	
}
