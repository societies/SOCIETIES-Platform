package org.societies.android.platform.events.notifications;

import org.societies.android.api.internal.cssmanager.IAndroidCSSManager;
import org.societies.android.platform.cssmanager.ServiceCSSManagerLocal;
import org.societies.android.platform.cssmanager.ServiceCSSManagerLocal.LocalCSSManagerBinder;
import org.societies.android.platform.events.notifications.R;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FriendsActivity extends Activity {

	private static final String EXTRA_CSS_ADVERT = "org.societies.api.schema.css.directory.CssAdvertisementRecord";
	private static final String LOG_TAG = FriendsActivity.class.getName();
	private static final String CLIENT_NAME      = "org.societies.android.platform.events.notifications.FriendsActivity";
	private IAndroidCSSManager localCSSManager;
    private String targetCssID = "";
 
    /** CSSManager service connection */
    private ServiceConnection cssManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from LocalCSSManager service");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to LocalCSSManager service");
        	//get a local binder and service API
        	LocalCSSManagerBinder binder = (LocalCSSManagerBinder) service;
            localCSSManager = (IAndroidCSSManager) binder.getService();
            
            localCSSManager.acceptFriendRequest(CLIENT_NAME, targetCssID);
            FriendsActivity.this.getApplicationContext().unbindService(cssManagerConnection);
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //SET THE TEXT LABELS FOR THIS FRIEND REQUEST
        CssAdvertisementRecord advert = (CssAdvertisementRecord) getIntent().getParcelableExtra(EXTRA_CSS_ADVERT);
        targetCssID = advert.getId();
        
        TextView lblName = (TextView) findViewById(R.id.txtName);
        TextView lblJid = (TextView) findViewById(R.id.txtJid);
        CharSequence charName = advert.getName();
        CharSequence charJid = targetCssID;
        lblName.setText(charName);
        lblJid.setText(charJid);
        
        //ACCEPT BUTTON EVENT HANDLER
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		acceptFriendRequest(targetCssID);
        	}
        });
        
      	//LATER BUTTON EVENT HANDLER
        Button btnLater = (Button) findViewById(R.id.btnLater);
        btnLater.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		finish(); //BASICALLY, IGNORE REQUEST
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_friends, menu);
        return true;
    }
    
    private void acceptFriendRequest(String requestId) {
    	//BIND TO CSS MANAGER AND SEND ACCEPT
    	Intent cssManagerintent = new Intent(getApplicationContext(), ServiceCSSManagerLocal.class);
    	this.getApplicationContext().bindService(cssManagerintent, cssManagerConnection, Context.BIND_AUTO_CREATE);
    	
    	finish();
    }
}
