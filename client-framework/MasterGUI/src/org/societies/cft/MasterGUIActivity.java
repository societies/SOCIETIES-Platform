package org.societies.cft;

import com.phonegap.DroidGap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;

public class MasterGUIActivity extends DroidGap {
/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//          setContentView(R.layout.main);
      super.loadUrl("file:///android_asset/www/index.html");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override 
    /**
     * Workaround required since PhoneGap 1.0.0
     */
    public boolean onKeyDown(int keyCode,KeyEvent event){ 
            if (keyCode == KeyEvent.KEYCODE_MENU) { 
                 return false; 
           }else{ 
               return super.onKeyDown(keyCode, event); 
           } 
    }

}
