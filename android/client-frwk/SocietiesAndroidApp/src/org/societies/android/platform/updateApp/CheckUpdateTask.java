/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.updateApp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * Describe your class here...
 *
 * @author Ioannis Mimtsoudis
 *
 */
public class CheckUpdateTask extends AsyncTask<String, Void, Boolean> {
	private boolean showIsUpToDate = false;
	private Context context;
	
	int curVersion=0, newVersion=0;

	public CheckUpdateTask(Context context) {
		this.context = context;
	}

	public CheckUpdateTask(Context context, boolean showIsUpToDate) {
		this.context = context;
		this.showIsUpToDate = showIsUpToDate;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		
		try {
			Log.i("checkUpdate", "checkUpdate run");
			URL updateURL = new URL("http://societies.local2.macs.hw.ac.uk:9090/test/version/version.txt");                
			URLConnection conn = updateURL.openConnection(); 
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);

			int current = 0;
			while((current = bis.read()) != -1){
				baf.append((byte)current);
			}

			curVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
			Log.i("checkUpdate", "curVersion: "+curVersion);
			newVersion = Integer.valueOf(new String(baf.toByteArray()));
			Log.i("checkUpdate", "newVersion: "+newVersion);

		} catch (Exception e) {
			Log.e("checkUpdate", "error in check update");
			Log.e("checkUpdate", e.getMessage());
		}
		return (newVersion > curVersion);
	}

	@Override
	protected void onPostExecute(Boolean newVersion) {
		if (newVersion) {
			new AlertDialog.Builder(context)
			.setTitle("Update Available")
			.setMessage("An update for Societies is available! Do you want to download a new version?")
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					//Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://54.218.113.176/AF-S/apk/download/askfree.apk"));
					//context.startActivity(intent);
					new VersionCheckTaskDialog().execute();
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			})
			.show();
		}
		else {
			if (showIsUpToDate) {
				new AlertDialog.Builder(context)
				//.setIcon(R.drawable.icon)
				.setTitle("No Update Available")
				.setMessage("You have the latest version (" + curVersion + ") of Societies.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				})
				.show();
			}
		}
	}

	private class VersionCheckTaskDialog extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog = null;

		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setMessage("Updating Societies...");
			dialog.setIndeterminate(false);
			dialog.setCancelable(true);
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			updateApp("http://societies.local2.macs.hw.ac.uk:9090/test/download/SocietiesAndroidApp.apk");
			//updateApp("http://societies.local2.macs.hw.ac.uk:9090/test/SocietiesAndroidCommsApp.apk");
			return null;
		}

		@Override
		protected void onPostExecute(Void result){
			dialog.dismiss();
		}

	}

	public void updateApp(String apkurl){
		try {
			URL url = new URL(apkurl);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			String PATH = Environment.getExternalStorageDirectory() + "/download/";
			File file = new File(PATH);
			file.mkdirs();
			File outputFile = new File(file, "SocietiesAndroidApp.apk");
			FileOutputStream fos = new FileOutputStream(outputFile);

			InputStream is = c.getInputStream();

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1);
			}
			fos.close();
			is.close();

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "SocietiesAndroidApp.apk")), "application/vnd.android.package-archive");
			context.startActivity(intent); 

		} catch (IOException e) {
			Toast.makeText(context, "Update error!", Toast.LENGTH_LONG).show();
		}
	}  
}


