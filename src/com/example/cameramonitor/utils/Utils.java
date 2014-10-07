package com.example.cameramonitor.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;


public class Utils {
	/**
	 * the function to return the current tiem
	 * 
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	public static final String GetCurrentTime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		return sdf.format(new Date(System.currentTimeMillis()));

	}

	/**
	 * the function to send the message
	 * 
	 * @param to
	 * @param title
	 * @param body
	 */
	public static final void sendMess(String to, String title, String body) {

	}

	/**
	 * send email method,be sure your host server must open "smpt" service
	 * 
	 * @param from
	 * @param hostname
	 * @param ps
	 * @param to
	 * @param title
	 * @param body
	 */
	public static final void sendEmail(final String from,
			final String hostname, final String ps, final String to,
			final String title, final String body) {

		class SendEmailTask extends AsyncTask<Void, Void, Void> {

			@Override
			protected Void doInBackground(Void... params) {
				//
				// 邮件系统会自动判别你发送邮件的频率 --！，太快就会出错
				HtmlEmail email = new HtmlEmail();
				email.setHostName(hostname);
				email.setSSLOnConnect(true);
				email.setCharset("utf-8");

				try {
					email.addTo(to);
					email.setFrom(from);
					email.setAuthentication(from, ps);
					email.setSubject(title);
					email.setMsg(body);
					email.send();

				} catch (EmailException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		new SendEmailTask().execute();
	}

	
	/**
	 * witeLog class is used to write the mess to log file
	 * 
	 * after called,must use writeLog.close().
	 * @author andy
	 *
	 */
	public static class writeLog {
		private String path;
		private Writer writer;

		@SuppressLint("SimpleDateFormat")
		private static final SimpleDateFormat fmt = new SimpleDateFormat(
				ServerConfig.SIMPLEDATEFORMAT_PATTERN);

		public writeLog(Context context) {
			File filepath = Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED) ? Environment
					.getExternalStorageDirectory() : context
					.getFilesDir();

			File logfile = new File(filepath, "/GProject");
			path = logfile.getAbsolutePath() + "/log.txt";
			
			if (!logfile.exists()) {
				logfile.mkdirs();
			}
			
			File logtxt = new File(path);
			if (!logtxt.exists()) {
				try {
					logtxt.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			open();
		}

		// open the writer, if it not closed ,close it
		protected void open() {
			try {
				if (writer != null) {
					writer.close();
					writer = null;
				}
				writer = new BufferedWriter(new FileWriter(this.path, true), 2048);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// print the mess to file
		public void printLn(String mess) {
			try {
				writer.write(getToday());
				writer.write("  " + mess);
				writer.write("\n");
				writer.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// after use ,must close the writer
		public void close() {
			try {
				if(writer!=null){
					writer.close();
					writer = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@SuppressWarnings("static-access")
		private String getToday() {
			return this.fmt.format(new Date());
		}

	}

	/**
	 * detect if the device's platform version is Gingerbread or later
	 */
	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	/**
	 * detect if the device's platform version is honeycomb or later
	 */
	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	/**
	 * detect if the device's platform version is honeycomb MR1 or later
	 */
	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	/**
	 * detect if the device's platform version is ICS or later
	 */
	public static boolean hasICS() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}

}
