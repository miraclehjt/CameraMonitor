package com.example.cameramonitor.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataReceiver extends BroadcastReceiver{
	
	
	private boolean flag = false;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		Log.i("broadcast", "i get it");
		Log.i("message",">>"+arg1.getStringExtra("andy"));
	}

}
