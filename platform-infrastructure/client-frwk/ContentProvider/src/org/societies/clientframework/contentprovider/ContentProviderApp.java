package org.societies.clientframework.contentprovider;

import android.app.Application;
import android.content.SharedPreferences;

public class ContentProviderApp extends Application {
	
	SharedPreferences preferences;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
