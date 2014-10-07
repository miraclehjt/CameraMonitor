package com.example.cameramonitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.cameramonitor.utils.Monitor_API;
import com.example.cameramonitor.utils.ProcessService;
import com.example.cameramonitor.utils.ServerConfig;

public class MainActivity extends Activity {

	private Camera mCamera;
	private CameraPreview mPreview;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	private boolean mStartMonitorFlag = false;
	private static final String TAG = "cameramonitor";

	private boolean mPicTokenFlag = false;
	private byte[] mPrePicByte = null;

	private Monitor_API mAPI = Monitor_API.getInstance(MainActivity.this);

	private Handler mHandler = new Handler();

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Config.RGB_565;
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,
					opts);

			Matrix matrixs = new Matrix();
			if (orientations > 325 || orientations <= 45) {
				matrixs.setRotate(90);
			} else if (orientations > 45 && orientations <= 135) {
				matrixs.setRotate(180);
			} else if (orientations > 135 && orientations < 225) {
				matrixs.setRotate(270);
			} else {
				matrixs.setRotate(0);
			}

			bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrixs, true);

			//
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				bmp.compress(Bitmap.CompressFormat.PNG, 85, fos);
				fos.close();

			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			// start service

			if (mPicTokenFlag) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putByteArray(ServerConfig.EXTRA_BYTEARRAYA, mPrePicByte);
				bundle.putByteArray(ServerConfig.EXTRA_BYTEARRAYB, data);
				intent.putExtra(ServerConfig.EXTRA_BUNDLE, bundle);
				intent.setClass(MainActivity.this, ProcessService.class);
				startService(intent);
			} else {	
				mPicTokenFlag = true;
			}
			mPrePicByte = data;
			
			mPreview.reStartView();

		}
	};
	private OrientationEventListener myOrientationEventListener = null;
	private int orientations;

	private Camera.AutoFocusCallback mAutoFocusCallback;
	private ToggleButton mTgButtom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_main);

		mTgButtom = (ToggleButton) findViewById(R.id.toggleButton1);
		mTgButtom.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// 开始监视
					Log.i("toogle buttom ", "checked");
					mStartMonitorFlag = true;
					StartMonitor();
				} else {
					// 停止监视
					mStartMonitorFlag = false;
					Log.i("toogle buttom ", "not checked");
					StopMonitor();

				}

			}
		});

		// 方向事件监听器
		// http://blog.csdn.net/xiaona1047985204/article/details/14162115
		myOrientationEventListener = new OrientationEventListener(this) {

			@Override
			public void onOrientationChanged(int orientation) {

				orientations = orientation;
			}
		};

		if (checkCaeramHardware(this)) {
			mCamera = getCameraInstance();
		} else {
			return;
		}

		Camera.Parameters params = mCamera.getParameters();
		List<String> focusModes = params.getSupportedFocusModes();
		if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
			params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(params);
		}

		mAutoFocusCallback = new Camera.AutoFocusCallback() {

			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				// TODO Auto-generated method stub
				if (success) {
					mCamera.setOneShotPreviewCallback(null);
					Toast.makeText(MainActivity.this, "自动聚焦成功",
							Toast.LENGTH_SHORT).show();
				}

			}
		};

		// 摄像头展示旋转对应的角度
		setCameraDisplayOrientation(MainActivity.this, 0, mCamera);

		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		// preview.setVisibility(View.INVISIBLE);

		// Add a listener to the Capture button
		Button captureButton = (Button) findViewById(R.id.button1);
		captureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// get an image from the camera
				mCamera.takePicture(null, null, mPicture);
			}
		});
	}

	/**
	 * 设置摄像头显示
	 * 
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	@SuppressLint("NewApi")
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {

		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	@Override
	protected void onResume() {

		super.onResume();
		if (myOrientationEventListener != null) {
			myOrientationEventListener.enable();
		}
	}

	@Override
	protected void onPause() {

		super.onPause();
		// releaseCamera();
	}

	public static Camera getCameraInstance() {

		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c;
	}

	private boolean checkCaeramHardware(Context context) {

		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	/** Create a File for saving an image or video */
	@SuppressLint("SimpleDateFormat")
	private static File getOutputMediaFile(int type) {

		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	// 供api调用
	public void StartMonitor() {

		Runnable task = new Runnable() {

			@Override
			public void run() {
				if (mStartMonitorFlag) {
					mHandler.postDelayed(this, 10000);

					mCamera.takePicture(null, null, mPicture);
					//

				}

			}
		};
		mHandler.postDelayed(task, 1000);

	}

	// 供api调用
	public void StopMonitor() {
		mStartMonitorFlag = false;
		mPicTokenFlag = false;
		mPrePicByte = null;

	}
}
