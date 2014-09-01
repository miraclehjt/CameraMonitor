package com.example.cameramonitor;


import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class CameraPreview extends SurfaceView implements
	SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;


	public CameraPreview(Context context, Camera camera) {

		super(context);
		mCamera = camera;

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		try {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
		int height) {
		// If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
		if(mHolder.getSurface() == null) {
			// preview surface does not exist
			return;
		}
		
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		}catch(Exception e){
			
		}
		 // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
		
		try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){

        }
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		// TODO Auto-generated method stub

	}

}
