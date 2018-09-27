package com.blg.rtu.help;

import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import com.blg.rtu3.MainActivity;
import com.blg.rtu3.R;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class HelpSaveProtocolDataToFile {
	
	private static String tag = HelpSaveProtocolDataToFile.class.getName() ;
	
	// 准备文件夹
	public static File getFile(MainActivity act, String yyyy_mm_dd){
		Resources rs = act.getResources() ;
		String filePath = Environment.getExternalStorageDirectory() 
				+  rs.getString(R.string.fileDir) 
				+  yyyy_mm_dd + "_命令结果"
				+  '.' 
				+ rs.getString(R.string.fileExtendsName) ;
		File file = new File(filePath);
		File dir = file.getParentFile() ;
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return file ;
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	public static void deleteFile(File file){
		try{
			if(file != null && file.exists()){
				file.delete() ;
			}
		}catch(Exception e){} 
	}

}
