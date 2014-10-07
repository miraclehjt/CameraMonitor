package com.example.cameramonitor.utils;

import com.example.cameramonitor.MainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Monitor_API {
	Context mContext;
	
	public Monitor_API(Context context){
		super();
		this.mContext = context;
	}
	
	public static Monitor_API getInstance(Context context){
		return new Monitor_API(context);
	}
	

	//send the email
	public static void SendEmail(Context context){
		
		Intent mintent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_TO, "");
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_TITLE, "");
		bundle.putString(ServerConfig.BKEY_SENDEMAIL_BODY, "");
		mintent.putExtra(ServerConfig.EXTRA_BUNDLE, bundle);
		mintent.setClass(context, SendEmailService.class);
		context.startService(mintent);
	} 
	

}
