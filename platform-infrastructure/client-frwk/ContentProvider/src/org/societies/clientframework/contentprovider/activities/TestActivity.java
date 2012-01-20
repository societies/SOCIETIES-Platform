package org.societies.clientframework.contentprovider.activities;

import org.societies.clientframework.contentprovider.R;
import org.societies.clientframework.contentprovider.services.IConsumer;
import org.societies.clientframework.contentprovider.services.RemoteService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestActivity extends Activity{
	
	Button  bind, ubind, getKeys, save, resetDB, search;
	TextView status, values, keys;
	EditText key,value;
	ServiceConnection mServiceConnection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		
		
		bind     = (Button) findViewById(R.id.bindService);
		ubind    = (Button) findViewById(R.id.unbindService);
		getKeys  = (Button) findViewById(R.id.getKeys);
		save  	 = (Button) findViewById(R.id.saveDB);
		search   = (Button) findViewById(R.id.searchKey);
		resetDB  = (Button) findViewById(R.id.resetDB);
		
		status = (TextView) findViewById(R.id.cpStatus);
		keys   = (TextView) findViewById(R.id.keys);
		
		key    = (EditText) findViewById(R.id.editKey);
		value  = (EditText) findViewById(R.id.editValue);
		 
		
		bind.setOnClickListener(listener);	
		ubind.setOnClickListener(listener);	
		search.setOnClickListener(listener);
		save.setOnClickListener(listener);
		getKeys.setOnClickListener(listener);	
		resetDB.setOnClickListener(listener);	
		
		
	}

	OnClickListener listener = new OnClickListener() {
		
		public void onClick(View v) {
			switch(v.getId()){
				
				case R.id.bindService: 	doBindService(); break;	
				case R.id.unbindService:doUnbindService();break;
				case R.id.resetDB: 		resetDB();	break;
				case R.id.getKeys: 		getKeys(); 	break;
				case R.id.searchKey:  	search();  	break;
				case R.id.saveDB: 		save(); 	break;
				
			}
			
		}
	};
	
	private void save(){
		if (mBoundService==null) {
			changeStatus("Bind service FIRST!");
			return;
		}
		
		if (key.getText().length()==0) {
			changeStatus("Please set a valid KEY");
			return;
		}
		
		if (value.getText().length()==0){
			changeStatus("Please set a valid VALUE");
			return;
		}
			
		try {
			
			if (mBoundService.store(key.getText().toString(), value.getText().toString())){
				
				changeStatus(key.getText() +"/"+value.getText()+ "stored in DB");
				key.setText("");
				value.setText("");
				
			}
			else changeStatus("Error while saving :( ");
		}
		catch (RemoteException e) {
			e.printStackTrace();
			changeStatus("Error"+e);
		}
		
		
	}
	
	
	private void search(){
		if (mBoundService==null) {
			changeStatus("Bind service FIRST!");
			return;
		}
		try {
			value.setText("");
			if (key.getText().length()>0)
				value.setText(mBoundService.getValue(key.getText().toString()));
			else changeStatus("Key not present");
		} catch (RemoteException e) {
			e.printStackTrace();
			changeStatus("Error"+e);
		}
		
		
	}
	
	
	private void resetDB(){
		 
		if (mBoundService==null) {
			changeStatus("Bind service FIRST!");
			return;
		}
		try {
				mBoundService.resetDB();
				changeStatus("DB initialized");
			} catch (RemoteException e) {
				e.printStackTrace();
				changeStatus("Error"+e);
			}
			
	}
	
	private void getKeys(){
		if (mBoundService==null) {
			keys.setText("Service is disconnected");
			return;
		}
		
		String keyList="KeyList: ";
		try {
			for (String s : mBoundService.getKeys()) keyList +="\n"+s;
			keys.setText(keyList);
		}
		catch (RemoteException e) {keys.setText("Service Exception");}
	
	}
	
	private void changeStatus(final String value){
		runOnUiThread(new Runnable() {
			
			public void run() {
				status.setText(value);
			}
		});
	}
	
	
	private IConsumer mBoundService=null;

	private ServiceConnection mConnection = new ServiceConnection() {
	   
		
		public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
			mBoundService = IConsumer.Stub.asInterface(service);
			changeStatus("Content Provider Connected");
	        // Tell the user about this for our demo.
	        //Toast.makeText(TestActivity.this, "Connected", Toast.LENGTH_SHORT).show();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	        changeStatus("Content Provider Disconnected");
	    }
	};

	private boolean mIsBound;

	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because we want a specific service implementation that
	    // we know will be running in our own process (and thus won't be
	    // supporting component replacement by other applications).
		Intent consumer = new Intent(this, RemoteService.class); 
		consumer.setAction(IConsumer.class.getName());
		bindService(consumer, mConnection, Context.BIND_AUTO_CREATE);
	    mIsBound = true;
	}

	void doUnbindService() {
	    if (mIsBound) {
	        // Detach our existing connection.
	        unbindService(mConnection);
	        mIsBound = false;
	    }
	}

	
	
}
