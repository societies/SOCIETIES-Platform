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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class NegotiationActivity extends Activity implements OnItemSelectedListener {

	private static final String LOG_TAG = NegotiationActivity.class.getName();
	private static final String EXTRA_PRIVACY_POLICY = "org.societies.userfeedback.eventInfo";

	private UserFeedbackPrivacyNegotiationEvent eventInfo = null;
	private View[] requestControls;
	private View[][] allResponses;
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
		eventInfo = bundle.getParcelable(EXTRA_PRIVACY_POLICY);
		
		//SET HEADER INFO
		RequestorBean requestor = eventInfo.getNegotiationDetails().getRequestor();
		String sRequestorType = "community";
		if (requestor instanceof RequestorServiceBean) {
			sRequestorType = "installed service";
		} 
		TextView lblHeader = (TextView) findViewById(R.id.txtHeader);
		//lblHeader.setText(sHeader + "\r\n has requested access to the following data:");
		lblHeader.setText("The " + sRequestorType + " is requesting access to your personal info for the following uses. Please select what you would like to allow:");
		
		//GENERATE RESOURCE SPINNER
		final List<ResponseItem> responses = eventInfo.getResponsePolicy().getResponseItems();
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
		allResponses = new View[responses.size()][]; 
		for(int a=0; a<responses.size(); a++) {
			ResponseItem response = responses.get(a);
			RequestItem request = response.getRequestItem();
			List<Condition> conditions = request.getConditions();
			
			TableLayout tblCondition = new TableLayout(this);
			tblCondition.setBackgroundResource(R.color.Grey);
			requestControls = new View[conditions.size()];
			
			//EACH CONDITION
			for (int i=0; i<conditions.size(); i++) {
				Condition condition = conditions.get(i);
				boolean optional = condition.isOptional();
				TextView label = new TextView(this);
				label.setText(condition.getConditionConstant().value());
				//DATA TYPE - CHECKBOX/TEXTBOX
				if (condition.getConditionConstant().value().startsWith("data")) {
					final EditText textbox = new EditText(this);
					textbox.setText(condition.getValue());
					textbox.setContentDescription(a + "_" + i);
					textbox.addTextChangedListener(new TextWatcher() {
						EditText thisTextBox = textbox;
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) { }
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
						@Override
						public void afterTextChanged(Editable s) {
							String textValue = thisTextBox.getText().toString();
							String posValues = (String)thisTextBox.getContentDescription();
							String[] positions = posValues.split("_");
							int responsePos = Integer.parseInt(positions[0]);
							int conditionPos = Integer.parseInt(positions[1]);
							responses.get(responsePos).getRequestItem().getConditions().get(conditionPos).setValue(textValue);
						}
					});
					requestControls[i] = textbox; 
				}
				else {
					CheckBox checkbox = new CheckBox(this);
					checkbox.setChecked(true);
					checkbox.setEnabled(optional);
					checkbox.setContentDescription(a + "_" + i);
					checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							String conditionValue = (isChecked ? "YES" : "NO");
							String posValues = (String)buttonView.getContentDescription();
							String[] positions = posValues.split("_");
							int responsePos = Integer.parseInt(positions[0]);
							int conditionPos = Integer.parseInt(positions[1]);
							responses.get(responsePos).getRequestItem().getConditions().get(conditionPos).setValue(conditionValue);
						}
					});
					requestControls[i] = checkbox;
				}				
				TableRow row = new TableRow(this);
				row.setId(i);
				//row.addView(requestLabels[i]);
				row.addView(label);
				row.addView(requestControls[i]);
				
				tblCondition.addView(row);
			}
			tblConditions[a] = tblCondition;
			allResponses[a] = requestControls;
		}
		//ADD FIRST TABLE OF CONDITIONS TO SCROLL VIEW - OTHERS ADDED ON CHANGE EVENT
		svScroll = (ScrollView)findViewById(R.id.svConditions);
		svScroll.addView(tblConditions[0]);
		
		//ACCEPT BUTTON EVENT HANDLER
        Button btnAccept = (Button) findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		publishEvent();
        		finish();
        	}
        });
        //CANCEL BUTTON EVENT HANDLER
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {            
        	public void onClick(View v) {
        		finish(); //BASICALLY, IGNORE REQUEST
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
	
	private void publishEvent() {
		Log.d(LOG_TAG, "publishing eventinfo");
	}
	
}
