package com.example.cameramonitor.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

/**
 * ProcessService.service
 * 
 * get the bitmaps and judge there if need to alarm user
 * 
 * @author andy
 *
 */
public class ProcessService extends Service {
	private Servicehandler mServiceHandler;
	private Looper mHandlerLooper;
	private ImagePHash mImagePHash = null;
	private Bitmap b1 = null;
	boolean flag = false;

	private final class Servicehandler extends Handler {
		public Servicehandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();

			Log.i("processService->handleMessage", "in");

			mImagePHash = new ImagePHash();
			byte[] A_Bytes = bundle.getByteArray(ServerConfig.EXTRA_BYTEARRAYA);
			byte[] B_Bytes = bundle.getByteArray(ServerConfig.EXTRA_BYTEARRAYB);

			// 获取两张bitmap
			Bitmap b1 = BitmapFactory.decodeByteArray(A_Bytes, 0,
					A_Bytes.length);
			Bitmap b2 = BitmapFactory.decodeByteArray(B_Bytes, 0,
					B_Bytes.length);

			int distance = mImagePHash.getImagePHash(b1, b2);
			if (distance > 10) {
				// 图像phash>5,图片就应该不是同一张图
				// alarm user
				Log.i("processService", "not the same pic,dis "+ distance);

			} else {
				Log.i("processService", "the same pics,dis "+ distance);
			}
			mImagePHash = null;
			stopSelf(msg.arg1);

		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.i("processService ", "start");
		
		super.onStart(intent, startId);
	}

	@Override
	public void onCreate() {
		HandlerThread thread = new HandlerThread("ads",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();
		mHandlerLooper = thread.getLooper();
		mServiceHandler = new Servicehandler(mHandlerLooper);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub

		Message msg = mServiceHandler.obtainMessage();
		Bundle bundle = intent.getBundleExtra(ServerConfig.EXTRA_BUNDLE);
		msg.setData(bundle);
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		return START_STICKY;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
