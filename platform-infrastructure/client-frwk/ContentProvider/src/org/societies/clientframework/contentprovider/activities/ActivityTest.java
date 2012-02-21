package org.societies.clientframework.contentprovider.activities;

import org.societies.api.android.internal.model.CredentialTable;
import org.societies.api.android.internal.model.ServiceTable;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ActivityTest extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Add credential
		ContentValues data = new ContentValues();
		data.put(CredentialTable.KEY_APPLICATION, "test");
		data.put(CredentialTable.KEY_COMPANY, "TI");
		data.put(CredentialTable.KEY_FIRSTNAME, "Luca");
		data.put(CredentialTable.KEY_LASTNAME, "Lamorte");
		data.put(CredentialTable.KEY_PASSWORD, "aaa");
		data.put(CredentialTable.KEY_PROXY_URL, "");
		data.put(CredentialTable.KEY_SERVICE, "cmmfrk");
		data.put(CredentialTable.KEY_TOKEN, "");
		data.put(CredentialTable.KEY_USEPROXY, false);
		data.put(CredentialTable.KEY_USERNAME, "lucasimone");
		
		Uri credentialUri = Uri.parse(CredentialTable.CREDENTIAL_DATA_URI);
		Uri result = getContentResolver().insert(credentialUri, data);
		Log.w("ContentProvider", "Uri is "+result.toString());
		
		
		
		// add Service
		data = new ContentValues();
		data.put(ServiceTable.KEY_NAME, "key1");
		data.put(ServiceTable.KEY_VALUE, "value1");
		data.put(ServiceTable.KEY_SERVICE, "service1");
		
		Uri serviceURI = Uri.parse(ServiceTable.SERVICE_DATA_URI);
		result = getContentResolver().insert(serviceURI, data);
		Log.w("ContentProvider", "Uri is "+result.toString());

		data.put(ServiceTable.KEY_NAME, "key2");
		data.put(ServiceTable.KEY_VALUE, "value2");
		data.put(ServiceTable.KEY_SERVICE, "service1");
		result = getContentResolver().insert(serviceURI, data);
		
		
		Log.w("ContentProvider", "Uri is "+result.toString());

		
		
		String[] projection = {CredentialTable.KEY_FIRSTNAME, CredentialTable.KEY_LASTNAME, CredentialTable.KEY_ID};
		String where = CredentialTable.KEY_APPLICATION +" = ?";
		String[] whereArgs= new String[]{"test"};
		String sortOrder= null;
		Cursor c = getContentResolver().query(credentialUri, projection, where, whereArgs, sortOrder);
		
		
		if (c!=null){
			Log.v("ContentProvider", "number of entries: "+c.getCount());
			c.moveToFirst();
			do{
			Log.v("ContentProvider", "ID :+ "+c.getString(c.getColumnIndexOrThrow(CredentialTable.KEY_ID)) + " Full Name: "+ c.getString(c.getColumnIndexOrThrow(CredentialTable.KEY_FIRSTNAME)) +  " " + c.getString(c.getColumnIndexOrThrow(CredentialTable.KEY_LASTNAME)));
			}while(c.moveToNext());
			
		}
		else
			Log.v("ContentProvider", "No data found" );
		
		
		
		finish();
	}
}
