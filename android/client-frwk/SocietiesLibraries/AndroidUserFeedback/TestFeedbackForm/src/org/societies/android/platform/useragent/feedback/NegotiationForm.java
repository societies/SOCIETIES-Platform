package org.societies.android.platform.useragent.feedback;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class NegotiationForm extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_negotiation_form);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.negotiation_form, menu);
		return true;
	}

}
