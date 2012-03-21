package dlr.stressrecognition;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Activity to show the rules of the tests to the user.
 * 
 * @author Michael Gross
 *
 */
public class ShowRulesActivity extends Activity {
	public TextView rulesStroop;
	public TextView rulesActivity;
	public TextView rulesCombined;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rules);
		
		rulesStroop = (TextView) findViewById(R.id.rules_stroop);
		rulesActivity = (TextView) findViewById(R.id.rules_activity);
		rulesCombined = (TextView) findViewById(R.id.rules_combined);
				
		// Attach Start Button Handler
    	findViewById(R.id.btnContinue).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			finish();
    		}
    	});
		
	}
	
	
}
