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
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NegotiationActivity extends Activity {

	private static final String LOG_TAG = NegotiationActivity.class.getName();
	private static final String EXTRA_PRIVACY_POLICY = "org.societies.userfeedback.eventInfo";
	
	private TextView[] requestLabels;  
	private CheckBox[] requestCheckboxes; 
	
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
		lblHeader.setText(sHeader + "\r\n has requested access to the following data:");
		
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
		
		//PROCESS EACH CONDITION
		for(ResponseItem response: responses) {			
			RequestItem request = response.getRequestItem();
			List<Condition> conditions = request.getConditions();
			
			TableLayout tblConditions = (TableLayout) findViewById(R.id.tblConditions);
			
			//Create all of the objects  
			requestLabels = new TextView[conditions.size()];  
			requestCheckboxes = new CheckBox[conditions.size()];
			//ScrollView svScroll = new ScrollView(this);  
			//LinearLayout llList = new LinearLayout(this);  
			
			for (int i=0; i<conditions.size(); i++) {
				Condition condition = conditions.get(i);
				boolean optional = condition.isOptional();
				requestLabels[i] = new TextView(this);  
				requestCheckboxes[i] = new CheckBox(this);  
				//Set captions  
				requestLabels[i].setText(condition.getConditionConstant().value());  
				requestCheckboxes[i].setChecked(true);  
				
				TableRow row = new TableRow(this);
				row.setId(i);
				row.addView(requestLabels[i]);
				row.addView(requestCheckboxes[i]);
				
				tblConditions.addView(row);
			}
			//Put the linear list in the ScrollView  
			//svScroll.addView(tblConditions);   
			//svScroll.addView(llList);
			//Make the LinearList arrange the controls vertically  
			//llList.setOrientation(LinearLayout.VERTICAL); 
			//Instead of an XML layout, the ScrollView will be our content  
			//setContentView(svScroll); 
			break;
		}
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_negotiation, menu);
        return true;
    }
}