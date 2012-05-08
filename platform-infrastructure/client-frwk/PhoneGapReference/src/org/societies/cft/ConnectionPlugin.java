package org.societies.cft;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;


public class ConnectionPlugin extends Plugin {
	public static final String CREATE_CONNECTION_LISTENER = "createListener";
    private BroadcastReceiver receiver;
    private String callbackId;

    //Constructor
    public ConnectionPlugin() {
    	this.receiver = null;
    	this.callbackId = null;
    }

	@Override
	//If a connection receiver action is received, create the listener and
	//signal ok back to the Webview
	//The ConnectionReceiver class will handle the asynchronous updates
	public PluginResult execute(String action, JSONArray data, String callbackID) {
		PluginResult result = null;
		
		if (action.equals(CREATE_CONNECTION_LISTENER)) {
//			Log.i(this.getClass().getName(), "execute: " + action);
			
			if (this.callbackId != null) {
				result = new PluginResult(Status.ERROR, "Connection listener already created");
//				Log.i(this.getClass().getName(), "listener already created");
			} else {
				this.callbackId = callbackID;
				
	            IntentFilter intentFilter = new IntentFilter() ;
	            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
	            if (this.receiver == null) {
	                this.receiver = new BroadcastReceiver() {
	                    @Override
	                    public void onReceive(Context context, Intent intent) { 
	                    	updateConnectionInfo(intent);              
	                    }
	                };
	                this.ctx.getContext().registerReceiver(this.receiver, intentFilter);
	            }
	            // Don't return any result now, since status results will be sent when events come in from broadcast receiver 
	            result = new PluginResult(PluginResult.Status.NO_RESULT);
	            result.setKeepCallback(true);

			}
		} 
		return result;	
	}
	
	private void updateConnectionInfo(Intent intent) {
		Log.i(this.getClass().getName(), "action: "
                + intent.getAction());	
		if (this.callbackId != null) {
    		PluginResult result = new PluginResult(PluginResult.Status.OK, getConnectionInfo(intent));
    		result.setKeepCallback(true);
    		this.success(result, this.callbackId);
			
		}
		Log.i(this.getClass().getName(), "Plugin success method called, target: " + this.callbackId);

	}
	
    /**
     * Creates a JSONObject with the current connection information
     * 
     * @param intent of the connection
     * @return a JSONObject containing the connection status information
     */
    private JSONObject getConnectionInfo(Intent intent) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("action", intent.getAction());
            obj.put("actionMessage", "Check connection status");
        } catch (JSONException e) {
            Log.e(this.getClass().getName(), e.getMessage(), e);
        }
        return obj;
    }

	public BroadcastReceiver getReceiver() {
		return receiver;
	}

}
