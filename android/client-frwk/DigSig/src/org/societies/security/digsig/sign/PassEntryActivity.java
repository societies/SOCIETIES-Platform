package org.societies.security.digsig.sign;

import org.societies.security.digsig.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PassEntryActivity extends Activity {
	
	private EditText editText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pass_entry);
		
		editText = (EditText) findViewById(R.id.editText1);
		
		Button btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = getIntent();
				String pass = editText.getText().toString();
				i.putExtra("PASSWORD", pass);
				setResult(RESULT_OK, i);
				finish();
			}
		});		
		
		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);				
			}
		});
	}

}
