package com.blg.rtu3.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器(注意要区分不同极光推动项目的接收类不同)
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyAiReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyAiReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyAiReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyAiReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	//processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyAiReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyAiReceiver] 接收到推送下来的通知的ID: " + notifactionId);
			processCustomMessage(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyAiReceiver] 用户点击打开了通知");
            
        	//打开自定义的Activity
        	/*Intent i = new Intent(context, JpushmessageListActivity.class);
//        	i.putExtras(bundle);
       	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
        	context.startActivity(i);*/
        	
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyAiReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyAiReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyAiReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next().toString();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to JpushmessageListActivity
	private void processCustomMessage(Context context, Bundle bundle) {
/*
			String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			Intent msgIntent = new Intent(JpushmessageListActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(JpushmessageListActivity.KEY_MESSAGE, message);
			if (!StringUtils.isEmpty(extras)) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					JpushMessage jpushmessage=new JpushMessage();
					JpushExtrasJson jpushJson=new Gson().fromJson(extras,JpushExtrasJson.class);
					//
					jpushmessage.setWellName(jpushJson.getWellName());
					jpushmessage.setWellNo(jpushJson.getWellNo());
					jpushmessage.setMsgId(jpushJson.getMsgId());
					jpushmessage.setMsgTime(jpushJson.getMsgTime());
					jpushmessage.setIsReaded("0");
					JpushMessageImp jpushMessageImp=new JpushMessageImp(context);

					try {
						deleteMsgOverMonth(jpushMessageImp);//删除超过一个月的数据
						//jpushMessageImp.delete(DBOpenHelper.TABLENAME,"isReaded","1");//将已读的删除
						//int deleteOld=	jpushMessageImp.delete(DBOpenHelper.TABLENAME,"wellNo",jpushJson.getWellNo());//覆盖原来的井的信息（直接删除）
						//LogUtils.e(TAG,"删除行数"+deleteOld);
					} catch (Exception e) {
						LogUtils.e(TAG,"删除旧数据异常"+e);
					}
					if (jpushJson.getType().get(0)!=null){
						LogUtils.e(TAG,"====="+jpushJson.getType().get(0));
						jpushmessage.setType(jpushJson.getType().get(0));
						insertMsgByType(jpushmessage,jpushMessageImp);
					}
					if (jpushJson.getType().get(1)!=null){
						jpushmessage.setType(jpushJson.getType().get(1));
						insertMsgByType(jpushmessage,jpushMessageImp);
					}
					if (jpushJson.getType().get(2)!=null){
						jpushmessage.setType(jpushJson.getType().get(2));
						insertMsgByType(jpushmessage,jpushMessageImp);
					}
					if (jpushJson.getType().get(3)!=null){
						jpushmessage.setType(jpushJson.getType().get(3));
						LogUtils.e(TAG,"====="+jpushJson.getType().get(3));
						insertMsgByType(jpushmessage,jpushMessageImp);
					}
					if (jpushJson.getType().get(4)!=null){
						jpushmessage.setType(jpushJson.getType().get(4));
						insertMsgByType(jpushmessage,jpushMessageImp);
					}

					if (extraJson.length() > 0) {
						msgIntent.putExtra(JpushmessageListActivity.KEY_EXTRAS, extras);
						msgIntent.putExtra("isHasNewMsg",true);
					}
				} catch (JSONException e) {
					LogUtils.e(TAG,"json解析异常"+e);
				}

			}*/
			//context.sendBroadcast(msgIntent);
		}
/*private void insertMsgByType(JpushMessage msg,JpushMessageImp jpushMessageImp){
	try {
		jpushMessageImp.insert(DBOpenHelper.TABLENAME,msg);
		LogUtils.e(TAG,"最大id====="+jpushMessageImp.findMaxId(DBOpenHelper.TABLENAME));
	} catch (Exception e) {
		e.printStackTrace();
		LogUtils.e(TAG,"数据插入数据库异常"+e);
	}
}*/

	/**
	 * 删除超过一个月的消息
	 */
	private static final int DAY30=259200000;
/*
private void deleteMsgOverMonth(JpushMessageImp jpushMessageImp) {
	List<JpushMessage> cacheList = new ArrayList<JpushMessage>();
	long maxId = jpushMessageImp.findMaxId(DBOpenHelper.TABLENAME);
	if (maxId <= 100) {
		cacheList = jpushMessageImp.queryPage(DBOpenHelper.TABLENAME, 1, 100);
		updateMsgFormList(jpushMessageImp,cacheList);
	} else {
long page=maxId/100+1;
		for(int i=1;i<=page;i++){
			cacheList=jpushMessageImp.queryPage(DBOpenHelper.TABLENAME,i*100+1,100);
updateMsgFormList(jpushMessageImp,cacheList);
		}
	}
	jpushMessageImp.delete(DBOpenHelper.TABLENAME, "msgTime", "overdue");//将过期删除删除
}
*/

	/**
	 * 更新 list内的过期消息,消息字段改为"overdue"
	 * @param jpushMessageImp
	 * @param cacheList
	 */
/*private void updateMsgFormList(JpushMessageImp jpushMessageImp,List<JpushMessage> cacheList){
	for (JpushMessage jpushmsg : cacheList) {
		String time = jpushmsg.getMsgTime();
		long timeL = DateFormatUtils.getTimeMillis(time);
		long timeNow = System.currentTimeMillis();
		if (timeL - timeNow >= DAY30) {
			jpushmsg.setMsgTime("overdue");
			jpushMessageImp.update(DBOpenHelper.TABLENAME,jpushmsg);//更新消息时间为overdue
		}
	}
}*/
}
