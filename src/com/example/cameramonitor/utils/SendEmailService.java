package com.example.cameramonitor.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;


/**
 * 发送邮件，发送后自我销毁
 * @author andy
 *
 */
public class SendEmailService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Bundle bundle = intent.getBundleExtra(ServerConfig.EXTRA_BUNDLE);

		String emailsendto = bundle
				.getString(ServerConfig.BKEY_SENDEMAIL_TO);
		String emailbody = bundle
				.getString(ServerConfig.BKEY_SENDEMAIL_BODY);
		String emailtitile = bundle
				.getString(ServerConfig.BKEY_SENDEMAIL_TITLE);

		Utils.sendEmail(ServerConfig.SEND_EMAIL_FROM,
				ServerConfig.SEND_EMAIL_HOSTNAME, ServerConfig.SEND_EMAIL_AUTH,
				emailsendto, emailtitile, emailbody);
		
		stopSelf();

	}

}
