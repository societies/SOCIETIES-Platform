package org.societies.cft;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutActivity extends Activity {
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        this.setTitle("About");
        TextView aboutText = (TextView) findViewById(R.id.editText1);
        aboutText.setText("   Version 0.1 \n\n" + 
    					  "   SOCIETIES 2011 \n\n" + 
    					  "   Legal Stuff \n\n" +
    					  "\n\n" + 
    					  "   SOCIETIES FP7 Project: http://www.ict-societies.eu/ \n" +
    					  "   Bug Reports: http://www.ict-societies.eu/issues/"
    			);
        Linkify.addLinks(aboutText, Linkify.WEB_URLS);
    }

}
