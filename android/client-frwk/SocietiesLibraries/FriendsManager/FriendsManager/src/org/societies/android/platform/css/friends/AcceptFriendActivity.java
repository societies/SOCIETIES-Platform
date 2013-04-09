package org.societies.android.platform.css.friends;

import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.internal.cssmanager.IFriendsManager;
import org.societies.android.platform.css.friends.FriendsManagerLocal.LocalFriendsManagerBinder;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AcceptFriendActivity extends Activity {
	private static final String EXTRA_CSS_VCARD  = "org.societies.android.api.comms.xmpp.VCardParcel";
	private static final String EXTRA_CSS_ADVERT = "org.societies.api.schema.css.directory.CssAdvertisementRecord";
	private static final String LOG_TAG = AcceptFriendActivity.class.getName();
	private static final String CLIENT_NAME      = "org.societies.android.platform.events.notifications.FriendsActivity";
	private IFriendsManager localFriendManager;
    private String targetCssID = "";
 
    /** CSSManager service connection */
    private ServiceConnection friendManagerConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
        	Log.d(LOG_TAG, "Disconnecting from LocalCSSManager service");
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
        	Log.d(LOG_TAG, "Connecting to LocalCSSManager service");
        	//get a local binder and service API
        	LocalFriendsManagerBinder binder = (LocalFriendsManagerBinder) service;
            localFriendManager = (IFriendsManager) binder.getService();
            
            localFriendManager.acceptFriendRequest(CLIENT_NAME, targetCssID);
            finish();
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_friend_activity);

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
        
        //ADD IMAGE - IF AVAILABLE
        VCardParcel vCard = (VCardParcel) getIntent().getParcelableExtra(EXTRA_CSS_VCARD);;
	    byte[] avatarBytes = vCard.getAvatar();
	    if (avatarBytes != null) {
	    	Bitmap bMap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
	    
	    	ImageView image = (ImageView) findViewById(R.id.imgProfilePic);
	    	image.setImageBitmap(bMap);
	    }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.accept_friend_activity, menu);
        return true;
    }
    
    @Override
	public void onDestroy() {
		Log.d(LOG_TAG, "FriendsActivity service terminating");
		try {
			if (localFriendManager !=null ) unbindService(friendManagerConnection);
		} catch (Exception ex) {}
		super.onDestroy();
	}
    
    private void acceptFriendRequest(String requestId) {
    	//BIND TO CSS MANAGER AND SEND ACCEPT
    	Intent cssManagerintent = new Intent(getApplicationContext(), FriendsManagerLocal.class);
    	this.getApplicationContext().bindService(cssManagerintent, friendManagerConnection, Context.BIND_AUTO_CREATE);
    }
}
