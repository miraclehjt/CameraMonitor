package com.example.cameramonitor.utils;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;


public class takePics extends Service{

	
	PictureCallback rawCallback = new PictureCallback() {
	    public void onPictureTaken(byte[] data, Camera camera) {
	        Log.d("CAMERA", "onPictureTaken - raw");
	    }
	};

	ShutterCallback shutterCallback = new ShutterCallback() {
	    public void onShutter() {
	        Log.i("CAMERA", "onShutter'd");
	    }
	};
	
	@Override
	public IBinder onBind(Intent intent) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		Camera a = Camera.open();
		
		SurfaceView view = new SurfaceView(this);
		try {
			a.setPreviewDisplay(view.getHolder());
			a.startPreview();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
	        a.takePicture(shutterCallback, rawCallback, null);
	        Log.i("CAMERA", "Success");
	    } catch (Exception e) {
	        Log.e("CAMERA", "Click Failure");
	        e.printStackTrace();
	    }
	    a.release();
	}
	

}
