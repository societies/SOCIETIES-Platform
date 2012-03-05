package dlr.stressrecognition;

import java.util.Random;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.elicitation.MentalStress;
import dlr.stressrecognition.logger.Logger;
import dlr.stressrecognition.sensor.BioSensor;
import dlr.stressrecognition.utils.AppSharedPrefs;
import dlr.stressrecognition.utils.BluetoothConnection;
import dlr.stressrecognition.utils.DBAdapter;
import dlr.stressrecognition.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of a mental stress test. The Colour Word Test is played.
 * 
 * @author Michael Gross
 *
 */
public class MslActivity extends StressElicitationActivity {
	private BluetoothConnection btConnection;
    
	private BioSensor bioSensor = null;
	
    private Logger eventLogger;
    private MentalStress exercise;
    private int difficulty = -1;
    private int score = 0;
    private CountDownTimer timer;
    
    private boolean stopped = true;
    private boolean btStarted = false;
	
    private DBAdapter dbHelper;
    
    //GUI related attributes
	private TextView answerTypeText;
	private TextView timerText;
	private TextView question;
	private TextView answer1;
	private TextView answer2;
	private TextView answer3;
	private TextView answer4;
	private ProgressBar progressBar;
    private Toast notification;
		
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Create GUI and Layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.stroop_test);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
        
        question = (TextView) findViewById(R.id.question);
    	answer1 = (TextView) findViewById(R.id.answer1);
    	answer2 = (TextView) findViewById(R.id.answer2);
    	answer3 = (TextView) findViewById(R.id.answer3);
    	answer4 = (TextView) findViewById(R.id.answer4);
    	answerTypeText = (TextView) findViewById(R.id.AnswerType);
    	timerText = (TextView) findViewById(R.id.Timer);
    	progressBar=(ProgressBar)findViewById(R.id.progressBar);
    	
    	notification = Toast.makeText(this, "", Toast.LENGTH_SHORT);
       
    	difficulty = AppSharedPrefs.getDifficulty(this);
        
        dbHelper = new DBAdapter(this);
		dbHelper.open();
    	
        btConnection = new BluetoothConnection(getApplicationContext(), mHandler);
        btConnection.prepareBluetooth();
    	
    	OnClickListener answerListener = new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView t = (TextView) v;
				String result;
				if(timer != null)
					timer.cancel();
				if(exercise.checkAnswer(t.getText().toString())) {
					eventLogger.write("Exercise -> Answer: right");
					result = "Right!";
				    score++;
				} else {
					eventLogger.write("Exercise -> Answer: wrong");
					result = "Wrong!";
					score--;
				}
		    	notification.setText(result);
		    	notification.show();
				exercise.ask();
			}
    	};
		
    	findViewById(R.id.answer1).setOnClickListener(answerListener);
    	findViewById(R.id.answer2).setOnClickListener(answerListener);
    	findViewById(R.id.answer3).setOnClickListener(answerListener);
    	findViewById(R.id.answer4).setOnClickListener(answerListener);
	}
	
	/** Called when the activity is started. */
	@Override
	public void onStart() {
		super.onStart();
		STARTUP = System.nanoTime();
		
		// Setup Logging
		LOGFILE = "-" + System.currentTimeMillis();
		eventLogger = new Logger("Event");
		String[] events = {"Event"};
		eventLogger.writeHeader(events);
		
		// Create bio sensor object
		bioSensor = new BioSensor(mHandler, mHandler);
		// Initialize sensor system
		initSensorSystem();
	
		if(difficulty == -1) {
			Random generator = new Random();
			difficulty = generator.nextInt(5);	
		}
		if(difficulty < 2) {
			progressBar.setVisibility(View.INVISIBLE);
			timerText.setVisibility(View.INVISIBLE);
		}
		
		exercise = new MentalStress(this, mHandler, difficulty, true);
		eventLogger.write("Exercise -> Started with difficulty: " + difficulty);
		
		// Ask question
		exercise.ask();
	}
	
	/** Called when the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(!stopped) {
			exercise.abort();
			stopSensorSystem();
		}
	}
	
	/**
	 * Initializes the sensor system
	 */
	private void initSensorSystem() {
		// Start BT Sensor
		if (!btStarted) {
			btConnection.connectBTSensor(bioSensor);
		}
		// Sensor system is running
		stopped = false;
	}
	
	/**
	 * Stops the sensor system
	 */
	private void stopSensorSystem() {
		eventLogger.write("Stopped");
		//Close connections
		if(btStarted)
			btConnection.disconnectBTSensor();
		
		//Reset variables
		stopped = true;
		btStarted = false;
	}
	
	/**
	 * Shows the highscore to the user and finishes the CslActivity
	 */
	private void showHighScore() {
		// Show close dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Gameover!\n" +
				"Congratulations! You made "+score+" points.");
		builder.setCancelable(false);
		builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                MslActivity.this.finish();
		        		Intent serverIntent = new Intent(getApplicationContext(), HighScoreActivity.class);
		        		startActivity(serverIntent);
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Handler for managing the communication with the different classes used for the combined stress test.
	 * E.g. MotionSensor, BioSensor ...
	 */	
	public final Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	switch (msg.what) {
	    		case EXERCISE_STARTED:
	    			//TODO: What happens when Stroop Test setup is finished
	    	    	break;
	    		case EXERCISE_STROOP_ASK:
	    			Bundle data = msg.getData();
	    			String questionText = data.getString("Question");
	    			int questionColor = data.getInt("QuestionColor");
	    			String[] answers = data.getStringArray("Answers");
	    			int[] answerColors = data.getIntArray("AnswersColors");
	    			int time = data.getInt("Timer");
	    			boolean answerType = data.getBoolean("AnswerType");
	    			if(answerType) {
	    				answerTypeText.setText("Name the Color!");
		    			eventLogger.write("Exercise -> Question: Name the Color");
	    			} else {
	    				answerTypeText.setText("Name the Word!");
	    				eventLogger.write("Exercise -> Question: Name the Word");
	    			}

	    			
	    			if(time > 0) {
	    		    	progressBar.setMax(time);
	    		    	timer = new CountDownTimer(time*1000, 1000) {
	    					int tick = 0;
	    					public void onTick(long millisUntilFinished) {
	    						tick++;
	    						progressBar.setProgress(tick);
	    						timerText.setText("seconds remaining: " + millisUntilFinished / 1000);
	    					}
		    			    public void onFinish() {
		    			    	if(!stopped) {
			    			    	eventLogger.write("Exercise -> Question: Time out");
			    			    	notification.setText("Time out");
			    			    	notification.show();
			    					exercise.ask();
		    			    	}
		    			    }
		    			 };
		    			 timer.start();
	    			}
	    			question.setText(questionText);
	    	        question.setTextColor(questionColor);
	    	    	answer1.setText(answers[0]);
	    	    	answer1.setTextColor(answerColors[0]);
	    	    	answer2.setText(answers[1]);
	    	    	answer2.setTextColor(answerColors[1]);
	    	    	answer3.setText(answers[2]);
	    	    	answer3.setTextColor(answerColors[2]);
	    	    	answer4.setText(answers[3]);
	    	    	answer4.setTextColor(answerColors[3]);
            		break;
	    		case EXERCISE_STROOP_NEXT_LVL:
	    			difficulty++;
	    			if(difficulty > 1) {
	    				progressBar.setVisibility(View.VISIBLE);
	    				timerText.setVisibility(View.VISIBLE);
	    			}
    				eventLogger.write("Exercise -> Level changed to: Level " + difficulty);
    				notification.setText("Next level reached");
	    			break;
	    		case EXERCISE_STROOP_CANCELED:
					//TODO: Anything to handle?
	    			break;
            	case EXERCISE_STROOP_FIN:
					eventLogger.write("Exercise finished -> STOP");
					dbHelper.createHighscore(MainActivity.NAME, score);
					showHighScore();
					stopSensorSystem();
            		break;
    			case BT_CONNECTED:
    				btStarted = true;
    				notification.setText(msg.getData().getString("device") + "connected");
    				notification.show();
    				break;
    			case BT_NOTCONNECTED:
    				notification.setText("Unable to connect");
    				notification.show();
    				break;
    			case BT_NOTAVAILABLE:
    				Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
    				finish();
    				return;
	    	}	
	    }
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.connect_bt_sensor:
			btConnection.connectBTSensor(bioSensor);
			return true;
		case R.id.disconnect_bt_sensor:
			btConnection.disconnectBTSensor();
			return true;
		}
		return false;
	}	
}