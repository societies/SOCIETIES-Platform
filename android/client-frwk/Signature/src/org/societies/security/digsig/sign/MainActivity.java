package org.societies.security.digsig.sign;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.societies.security.digsig.common.SigResult;
import org.societies.security.digsig.utility.StreamUtil;

import org.societies.security.digsig.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final static int SIGN = 1;
	private final static int VERIFY = 2;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
          
        Button installBtn = (Button) findViewById(R.id.button2);
        installBtn.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {								
				Intent i = new Intent("org.societies.security.digsig.action.InstallIdentity");
				startActivity(i);
			}
		});
        
        Button listBtn = (Button) findViewById(R.id.button3);
        listBtn.setOnClickListener(new View.OnClickListener() {		
			public void onClick(View v) {
				
				byte[] val = null;
				try {
					val = "<xml><miki Id='Miki1'>aadsads</miki></xml>".getBytes("UTF-8");
				} catch (Exception e) {}
								
				Intent i = new Intent("org.societies.security.digsig.action.Sign");
				i.putExtra("XML",val);
				
				ArrayList<String> idsToSign = new ArrayList<String>();
				idsToSign.add("Miki1");
				i.putStringArrayListExtra("IDS_TO_SIGN", idsToSign);
										
				startActivityForResult(i, SIGN);
			}
		});
        
        Button verifyBtn = (Button) findViewById(R.id.button1);
        verifyBtn.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent i = new Intent("org.societies.security.digsig.action.Verify");
				
				ByteArrayOutputStream os = new ByteArrayOutputStream();				
				InputStream is = getResources().openRawResource(R.raw.sample);
				
				StreamUtil.copyStream(is, os);
				
				i.putExtra("XML",os.toByteArray());
				
				startActivityForResult(i, VERIFY);
			}
		});
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == SIGN && resultCode==RESULT_OK) {
			
			try
			{
				byte[] signedXml = data.getByteArrayExtra("SIGNED_XML");
				FileOutputStream os = new FileOutputStream("/mnt/sdcard/signed2.xml");
				os.write(signedXml);
				os.close();
			
				Toast.makeText(this, "File signed sucessfully.\nOutput is in signed2.xml on SD CARD!", Toast.LENGTH_LONG).show();
			} catch(Exception e) {
				
			}					
		} if (requestCode == VERIFY) {
			if (resultCode == RESULT_OK) {
				// get data
				ArrayList<SigResult> sigResults = data.getParcelableArrayListExtra("RESULT");
				
				boolean allOk = true;
				
				for (int i=0;i<sigResults.size();i++) {
					SigResult result = sigResults.get(i);
					if (result.getSigStatus()==0) {
						Toast.makeText(this, String.format("Signature %d is invalid !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					} else if (result.getSigStatus()==-1) {
						Toast.makeText(this, String.format("Error while verifying signatrue number %d !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					}
					
					if (result.getTrustStatus()==0) {
						Toast.makeText(this, String.format("Trust status on signature number %d is invalid !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					} else if (result.getTrustStatus()==-1) {
						Toast.makeText(this, String.format("Error while verifying trust status on signatrue number %d !", i+1), Toast.LENGTH_LONG).show();
						allOk=false;
						break;
					}					
				}
				
				if (allOk) 
					Toast.makeText(this, String.format("Successfully verified %d signatures in the file.", sigResults.size()), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Error while verifying file. Is the XML valid ?", Toast.LENGTH_LONG).show();
			}
		}
	}
    		  
}