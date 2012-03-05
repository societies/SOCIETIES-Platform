package com.disaster.dcc;

import android.app.Activity;
import android.os.Bundle;
import org.societies.api.internal.*;

public class DccApp extends Activity {

    /**
     * Called when the activity is first created.
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
     */
    String societiesServer = "server.societies-eu";
    String username = "Babak";
    String password = "SocietieS";
    SocietiesApp dccSoc;
    CssRecord cssRecord;
    CssId cssId;  //TODO: Find out which class CssId is.
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initializes the logging
        // Log.init();

        // Log a message (only on dev platform)
        // Log.i(this, "onCreate");

        setContentView(R.layout.main);
        // create a new SocietiesApp that will provide access to the platform
        dccSoc = new SocietiesApp("DCC");
        // populate a CssRecord to create it later:
        cssRecord = new CssRecord(societiesServer, username, password);
        // contact CssManager to create the new CSS:
        cssId = createCss(cssRecord);
    }
    
    CssId createCss(CssRecord _record){
	CssId cssId;
	try{
	    cssId = dccSoc.getCssManager().createCss(_record);
	    return cssId;
	} catch (CssException e) {
	    return null;
	}
    }
    
    CisId createCis(CisRecord _record){
	CisId cisId;
	try{
	    cisId = dccSoc.getCisManager().createCis(_record);
	    return cisId;
	} catch (CisException e){
	return null;
	}
    }
}

