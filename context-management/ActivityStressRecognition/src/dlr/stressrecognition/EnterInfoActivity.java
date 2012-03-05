package dlr.stressrecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

/**
 * Formular to enter the personal information of the test person.
 * 
 * @author Michael Gross
 *
 */
public class EnterInfoActivity extends Activity {
    private EditText nameText;
	private EditText ageText;
	private RadioButton radioMale;
	private RadioButton radioFemale;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.personal_info);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
		
    	nameText = (EditText) findViewById(R.id.textName);
    	ageText = (EditText) findViewById(R.id.textAge);
    	radioMale = (RadioButton) findViewById(R.id.radioMale);
    	radioFemale = (RadioButton) findViewById(R.id.radioFemale);
		// Attach Start Button Handler
    	findViewById(R.id.btnContinue).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			Intent resultIntent = new Intent();
    			resultIntent.putExtra("dlr.stressrecognition.Name", nameText.getText().toString());
    			resultIntent.putExtra("dlr.stressrecognition.Age", ageText.getText().toString());
                if(radioMale.isChecked()) {
                	resultIntent.putExtra("dlr.stressrecognition.Gender", radioMale.getText());
                } else {
                	resultIntent.putExtra("dlr.stressrecognition.Gender", radioFemale.getText());
                }
                // Set result and finish this Activity
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
    		}
    	});
    	   	
	}
}
