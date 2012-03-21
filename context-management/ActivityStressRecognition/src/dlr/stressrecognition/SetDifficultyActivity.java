package dlr.stressrecognition;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

/**
 * Activity to set the difficulty of the CWT.
 * 
 * @author Michael Gross
 *
 */
public class SetDifficultyActivity extends Activity {
	
	private RadioButton veryEasy;
	private RadioButton easy;
	private RadioButton medium;
	private RadioButton hard;
	private RadioButton veryHard;
	
	private int level;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_difficulty);
		
        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);
		
		// Attach Start Button Handler
    	findViewById(R.id.btnContinue).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			Intent resultIntent = new Intent();
    			
    			// Set result and finish this Activity
    			veryEasy = (RadioButton) findViewById(R.id.radio0);
    	    	easy = (RadioButton) findViewById(R.id.radio1);
    	    	medium = (RadioButton) findViewById(R.id.radio2);
    	    	hard = (RadioButton) findViewById(R.id.radio3);
    	    	veryHard = (RadioButton) findViewById(R.id.radio4);
    	    	
                if(veryEasy.isChecked()) {
                	level = 1;
                } else if(easy.isChecked()) {
                	level = 2;
                } else if(medium.isChecked()) {
                	level = 3;
                } else if(hard.isChecked()) {
                	level = 4;
                } else if(veryHard.isChecked()) {
                	level = 5;
                } else {
                	level = -1;
                }
                resultIntent.putExtra("dlr.stressrecognition.Level", level);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
    		}
    	});
    	   	
	}
}
