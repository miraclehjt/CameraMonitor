package com.example.cameramonitor.utils;

import com.example.cameramonitor.utils.Utils.writeLog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class Monitor_API {
	Context mContext;
	private writeLog mLogWriter;
	
	public Monitor_API(Context context){
		super();
		this.mContext = context;
	}
	
	public static Monitor_API getInstance(Context context){
		return new Monitor_API(context);
	}
	

	public void InvadeAlarm(Context context) {
		SharedPreferences sp = context.getSharedPreferences(ServerConfig.ANDYSHAREPREFERENCES, Context.MODE_PRIVATE);
		
		//发送邮件
		boolean emailused = sp.getBoolean(ServerConfig.EMAILUSED_FLAG, false);
		if(emailused){
			String emailaccount = sp.getString(ServerConfig.EMAILACCOUNT, null);
			if(emailaccount != null){
				Log.i("Monitor_API", "InvadeAlarm->sendemail:"+emailaccount);
				this.SendEmail(context, emailaccount, "入侵", ServerConfig.INVADE_ALARM);
				
				mLogWriter = new writeLog(context);
				mLogWriter.printLn("send email->"+emailaccount);
				mLogWriter.close();
				mLogWriter = null;
			}
		}
		
		//发送短信
		boolean phoneused = sp.getBoolean(ServerConfig.PHONEUSED_FLAG, false);
		if(phoneused){
			String phonenum = sp.getString(ServerConfig.PHONE_NUM, null);
			if(phonenum != null){
				Log.i("Monitor_API", "InvadeAlarm->SendMsg:"+phonenum);
				this.SendMsg(context, phonenum, ServerConfig.INVADE_ALARM);
				
				mLogWriter = new writeLog(context);
				mLogWriter.printLn("send email->"+phonenum);
				mLogWriter.close();
				mLogWriter = null;
			}
		}
		
	}
	
	//send alarm email
	public void SendEmail(Context context,String towho, String title,String body){
		
		Intent mintent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_TO, towho);
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_TITLE, "");
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_BODY, body);
		mintent.putExtra(ServerConfig.EXTRA_BUNDLE, bundle);
		mintent.setClass(context, SendEmailService.class);
		context.startService(mintent);
	} 
	
	public void SendMsg(Context context,String to,String body){
		Utils.sendMess(to, body);
	}
	
	
	


}
