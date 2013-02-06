package org.societies.android.platform.phongegap;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.util.Log;


public class PluginFeedback extends Plugin {

	public static final String BEEP_FEEDBACK = "beepFeedback";
	public static final String VIBRATE_FEEDBACK = "vibrateFeedback";
	
	@Override
	public PluginResult execute(String action, JSONArray args, String callbackContext) {
		// TODO Auto-generated method stub
		PluginResult result = null;
		
		if (action.equals(BEEP_FEEDBACK)){
			try { 
				this.beep(args.getInt(0));
				result = new PluginResult(Status.OK);
			}
			catch (JSONException e) {
				e.printStackTrace();
			}	
		}
		else if (action.equals(VIBRATE_FEEDBACK)) {
			try { 
				this.vibrate(args.getInt(0));
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
			result = new PluginResult(Status.OK);
		}
		else {
			result = new PluginResult(Status.INVALID_ACTION);
		}			
		return result;
	}
	
	/**
	 * Plays the default ringtone
	 * @param count Number of times to play notification
	 */
	public void beep (int count) {
		Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone notification = RingtoneManager.getRingtone(this.ctx.getContext(), ringtone);
		
		// If phone is not set to silent mode 
		if (notification != null){
			for (int i = 0; i < count; ++i)
			{
				notification.play();
				int timeout = 500;
				while (notification.isPlaying() && (timeout > 0)) {
					timeout = timeout - 100;
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException e) {
						
					}
				}
			}
		}
	}
	
	/**
	 * Vibrates the device for the specified amount of time
	 * @param time Time to vibrate in ms
	 */
	public void vibrate (int time) {
		// Start the vibration, defaults to half a second
		if (time ==0){
			time = 500;
		}
		
		Vibrator vibrator = (Vibrator) this.ctx.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(time);
	}
	
	

}
