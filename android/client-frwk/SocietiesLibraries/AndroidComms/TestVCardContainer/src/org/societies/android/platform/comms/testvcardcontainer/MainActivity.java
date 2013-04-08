package org.societies.android.platform.comms.testvcardcontainer;

import java.util.List;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.VCardParcel;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private ClientCommunicationMgr ccm;
	EditText logText = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        logText = (EditText)findViewById(R.id.editText1);
        this.ccm = new ClientCommunicationMgr(this, true);
        this.ccm.bindCommsService(new IMethodCallback() {
			@Override
			public void returnException(String result) { 
				logText.setText("Exception binding to service: " + result);
				Log.d(MainActivity.class.getName(), "Exception binding to service: " + result);
			}
			@Override
			public void returnAction(String result) { 
				logText.setText("return Action.flag: " + result);
				Log.d(MainActivity.class.getName(), "return Action.flag: " + result);
			}
			@Override
			public void returnAction(boolean resultFlag) {
				logText.setText("return Action.flag: " + resultFlag);
				Log.d(MainActivity.class.getName(), "return Action.flag: " + resultFlag);
			}
		});        
        //LOAD ME BUTTON EVENT HANDLER
        Button btnMe = (Button) findViewById(R.id.button1);
        btnMe.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		loadMe();
        	}
        });
        //LOAD USER BUTTON EVENT HANDLER
        Button btnUser = (Button) findViewById(R.id.button2);
        btnUser.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		loadUser("jane@societies.local");
        	}
        });
        //LOAD USER BUTTON EVENT HANDLER
        Button btnNull = (Button) findViewById(R.id.button3);
        btnNull.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		loadUser("bob@societies.local");
        	}
        });    
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void loadMe() {
    	ICommCallback callback = new VCardCallback();
    	ccm.getVCard(callback);
    }
    
    private void loadUser(String userId) {
    	ICommCallback callback = new VCardCallback();
    	ccm.getVCard(userId, callback);
    }
    
    /**
	 * Callback used with Android Comms for CSSDirectory
	 *
	 */
	private class VCardCallback implements ICommCallback {

		public List<String> getXMLNamespaces() { return null;}
		public List<String> getJavaPackages() {  return null;}
		public void receiveError(Stanza arg0, XMPPError arg1) { }
		public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }
		public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {	}
		public void receiveMessage(Stanza arg0, Object arg1) { }

		public void receiveResult(Stanza arg0, Object retValue) {
			Log.d(VCardCallback.class.getName(), "VCardCallback Callback receiveResult");
			
			VCardParcel vCard = (VCardParcel)retValue;
		    byte[] avatarBytes = vCard.getAvatar();
		    if (avatarBytes != null) {
		    	Bitmap bMap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);
		    
		    	ImageView image = (ImageView) findViewById(R.id.imageView1);
		    	image.setImageBitmap(bMap);
		    	logText.setText(vCard.getTo());
		    }
		    else
		    	logText.setText(vCard.getTo() + ": avatarbytes null");
		}
	}
}
