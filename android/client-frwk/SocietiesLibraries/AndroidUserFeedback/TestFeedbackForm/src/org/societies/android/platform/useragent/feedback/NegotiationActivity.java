package org.societies.android.platform.useragent.feedback;

import java.util.List;

import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NegotiationActivity extends Activity implements OnItemSelectedListener {

	private static final String LOG_TAG = NegotiationActivity.class.getName();
	private static final String EXTRA_PRIVACY_POLICY = "org.societies.userfeedback.eventInfo";
	
	private TextView[] requestLabels;  
	private CheckBox[] requestCheckboxes; 
	private TableLayout[] tblConditions;
	private ScrollView svScroll;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_negotiation);
        
        //GET EVENT OBJECT
        Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		UserFeedbackPrivacyNegotiationEvent eventInfo = bundle.getParcelable(EXTRA_PRIVACY_POLICY);
		RequestorBean requestor = eventInfo.getNegotiationDetails().getRequestor();
		
		//SET HEADER INFO
		String sHeader = "";
		if (requestor instanceof RequestorCisBean) {
			Log.d(LOG_TAG, "RequestorCisBean");
			sHeader = "Community: " + ((RequestorCisBean)requestor).getCisRequestorId() +   
					  "\r\nAdmin: " + ((RequestorCisBean)requestor).getRequestorId();
		} 
		else if (requestor instanceof RequestorServiceBean) {
			Log.d(LOG_TAG, "RequestorServiceBean");
		} 
		TextView lblHeader = (TextView) findViewById(R.id.txtHeader);
		//lblHeader.setText(sHeader + "\r\n has requested access to the following data:");
		lblHeader.setText("The Community is requesting access to your personal info for the below uses. Please select what you would like to allow:");
		
		//GENERATE RESOURCE SPINNER
		List<ResponseItem> responses = eventInfo.getResponsePolicy().getResponseItems();
		String[] resourceItems = new String[responses.size()];
		for(int i=0; i<responses.size(); i++) {
			resourceItems[i] = responses.get(i).getRequestItem().getResource().getDataType();
		}
		Spinner spinResources  = (Spinner) findViewById(R.id.spinResource);
		ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, resourceItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinResources.setAdapter(adapter);
		spinResources.setOnItemSelectedListener(this);
		
		//PROCESS EACH RESPONSE
		tblConditions = new TableLayout[responses.size()];
		for(int a=0; a<responses.size(); a++) {
			ResponseItem response = responses.get(a);
			RequestItem request = response.getRequestItem();
			List<Condition> conditions = request.getConditions();
			
			TableLayout tblCondition = new TableLayout(this);
			tblCondition.setBackgroundResource(R.color.Grey);
			
			//Create Table row objects  
			requestLabels = new TextView[conditions.size()];  
			requestCheckboxes = new CheckBox[conditions.size()];
			//EACH CONDITION
			for (int i=0; i<conditions.size(); i++) {
				Condition condition = conditions.get(i);
				boolean optional = condition.isOptional();
				requestLabels[i] = new TextView(this);
				requestCheckboxes[i] = new CheckBox(this);
				//Set captions
				requestLabels[i].setText(condition.getConditionConstant().value());
				//requestCheckboxes[i].setChecked(Boolean.parseBoolean(condition.getValue()));
				requestCheckboxes[i].setChecked(true);
				requestCheckboxes[i].setEnabled(optional);
				
				TableRow row = new TableRow(this);
				row.setId(i);
				row.addView(requestLabels[i]);
				row.addView(requestCheckboxes[i]);
				
				tblCondition.addView(row);
			}
			tblConditions[a] = tblCondition;
		}
		svScroll = (ScrollView)findViewById(R.id.svConditions);
		svScroll.addView(tblConditions[0]);
		
		//ACCEPT BUTTON EVENT HANDLER
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		
        	}
        });
        //ACCEPT BUTTON EVENT HANDLER
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_negotiation, menu);
        return true;
    }

	/* @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long) */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		svScroll.removeAllViews();
		svScroll.addView(tblConditions[position]);
	}

	/* @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView) */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	
}
