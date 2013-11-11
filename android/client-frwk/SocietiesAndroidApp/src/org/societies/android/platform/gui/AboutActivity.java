/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.gui;



import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	int curVersion;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        this.setTitle("About");
        TextView aboutText = (TextView) findViewById(R.id.editText1);
        aboutText.setText("   Version 0.4.1 \n\n" + 
    					  "   SOCIETIES 2012 \n\n" + 
    					  "   Legal Stuff \n\n" +
    					  "   Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG),\n" + 
    					  "   HERIOT-WATT UNIVERSITY (HWU),\n" + 
    					  "	  SOLUTA.NET (SN),\n" + 
    					  "   GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR),\n" +
    					  "   Zavod za varnostne tehnologije informacijske družbe in elektronsko poslovanje (SETCCE),\n" + 
    					  "   INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS),\n" +
    					  "   LAKE COMMUNICATIONS (LAKE),\n" + 
    					  "   INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL),\n" + 
    					  "   PORTUGAL TELECOM INOVAÇÃO, SA (PTIN),\n" + 
    					  "   IBM Corp (IBM),\n" + 
    					  "   INSTITUT TELECOM (ITSUD),\n" + 
    					  "   AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC),\n" + 
    					  "   TELECOM ITALIA S.p.a.(TI),\n" + 
    					  "   TRIALOG (TRIALOG),\n" +
    					  "   Stiftelsen SINTEF (SINTEF),\n" + 
    					  "   NEC EUROPE LTD (NEC)) \n" + 
					  	  "   All rights reserved. \n\n" + 
    					  "   SOCIETIES FP7 Project: http://www.ict-societies.eu/ \n\n" +
    					  "   Bug Reports: https://redmine.ict-societies.eu/"
    			);
        Linkify.addLinks(aboutText, Linkify.WEB_URLS);
        
        TextView version = (TextView)findViewById(R.id.textView1);
        
        try {
			curVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
			version.setText("Application Version: " + curVersion);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

}
