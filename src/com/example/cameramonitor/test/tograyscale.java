package com.example.cameramonitor.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.example.cameramonitor.R;

/**
 * to grayscale
 * @author Administrator
 * http://www.cnblogs.com/error404/archive/2011/11/07/2239370.html
 */
public class tograyscale extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tograyscale);
		
		final Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		
		final ImageView imageview = (ImageView) findViewById(R.id.imageView1);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				imageview.setImageBitmap(toGrayScale(bmp));
				
			}
		});
		
		
		
		
	}
		
	public Bitmap toGrayScale(Bitmap bmp) {
		
		int width,height;
		height = bmp.getHeight();
		width = bmp.getWidth();
		
		Bitmap bmpGrayScale = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565);
		
		Canvas c = new Canvas(bmpGrayScale);
		
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpGrayScale, 0, 0, paint);
		
		return bmpGrayScale;
	}
}
