package com.blg.rtu3;

import android.app.Application;
import android.content.Context;

import org.xutils.x;

public class AppContext extends Application{

	private static AppContext mInstance = null;
	private static Context mAppContext;
	public AppContext() {
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mInstance = this;
		mAppContext = getApplicationContext();
		x.Ext.init(this);
		x.Ext.setDebug(org.xutils.BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

	}

	public static AppContext getInstance() {
		return mInstance;
	}

}
