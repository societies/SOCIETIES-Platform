package dlr.stressrecognition;

import java.util.Random;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.elicitation.MentalStress;
import dlr.stressrecognition.elicitation.PhysicalStress;
import dlr.stressrecognition.logger.Logger;
import dlr.stressrecognition.sensor.BioSensor;
import dlr.stressrecognition.sensor.MotionSensor;
import dlr.stressrecognition.utils.AppSharedPrefs;
import dlr.stressrecognition.utils.BluetoothConnection;
import dlr.stressrecognition.utils.DBAdapter;
import dlr.stressrecognition.utils.PlaySound;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of a combined stress test. It is a combination of the PslActivity and the MslActivity.
 * Whenever the user does an activity different from running, the Colour Word Test is played.
 * 
 * @author Michael Gross
 *
 */
public class CslActivity extends StressElicitationActivity {
	private Logger eventLogger;
	private BluetoothConnection btConnection;
	private BioSensor bioSensor;
	private MotionSensor sensor;
	private PhysicalStress pExercise;
	private MentalStress mExercise;
	private int difficulty = -1;
	private int score = 0;
	private CountDownTimer timer;
	private Toast notification;

	private boolean stopped = true;
	private boolean btStarted = false;
	private boolean activityStarted = false;

	private DBAdapter dbHelper;

	// GUI related attributes
	private TextView statusText;
	private TextView answerTypeText;
	private TextView timerText;
	private TextView question;
	private TextView answer1;
	private TextView answer2;
	private TextView answer3;
	private TextView answer4;
	private ProgressBar progressBar;
	private ProgressDialog progressDialog;



	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create GUI and Layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.combined_test);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		statusText = (TextView) findViewById(R.id.textViewStatus);
        question = (TextView) findViewById(R.id.question);
    	answer1 = (TextView) findViewById(R.id.answer1);
    	answer2 = (TextView) findViewById(R.id.answer2);
    	answer3 = (TextView) findViewById(R.id.answer3);
    	answer4 = (TextView) findViewById(R.id.answer4);
    	answerTypeText = (TextView) findViewById(R.id.AnswerType);
    	timerText = (TextView) findViewById(R.id.Timer);
    	progressBar=(ProgressBar)findViewById(R.id.progressBar);
    	
    	notification = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	
		// Set the difficulty according to preferences
        difficulty = AppSharedPrefs.getDifficulty(this);
        
        // Set DB connection to save highscore
        dbHelper = new DBAdapter(this);
		dbHelper.open();

		// Prepare the bluetooth connection
        btConnection = new BluetoothConnection(getApplicationContext(), mHandler);
        btConnection.prepareBluetooth();
        
        // Attach Start Button Handler
		findViewById(R.id.btnStart).setOnClickListener(
				new Button.OnClickListener() {
					public void onClick(View v) {
						String[] log = new String[1];
						log[0] = "Calibrating";
						eventLogger.write(log);
						progressDialog = ProgressDialog.show(CslActivity.this,
								"", "Initializing ARS. Please wait...", true);
						initSensorSystem();
					}
				});

		// Attach Stop Button Handler
		findViewById(R.id.btnStop).setOnClickListener(
				new Button.OnClickListener() {
					public void onClick(View v) {
						statusText.setText("Stopped");
						stopSensorSystem();
					}
				});
		
		// Attach a handler for the game answers
    	OnClickListener answerListener = new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView t = (TextView) v;
				String result;
				if(timer != null)
					timer.cancel();
				if(mExercise.checkAnswer(t.getText().toString())) {
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
				mExercise.ask();
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
		findViewById(R.id.stroop_test).setVisibility(View.INVISIBLE);
		// Set Event Logger
		LOGFILE = "-" + System.currentTimeMillis();
		eventLogger = new Logger("Event");
		String[] events = { "Event" };
		eventLogger.writeHeader(events);
		
		// Create bio sensor object
		bioSensor = new BioSensor(mHandler, mHandler);
		// Create Physical Stress Exercise Object
		pExercise = new PhysicalStress(this, mHandler);
	}
	
	/** Called when the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!stopped)
			stopSensorSystem();
	}
	
	/**
	 * Initializes the sensor system
	 */
	private void initSensorSystem() {
		// Start BT Sensor
		if (!btStarted) {
			btConnection.connectBTSensor(bioSensor);
		}
		// Start Activity Recognition
		if (!activityStarted)
			initActivityRecognition();
		// Sensor system is running
		stopped = false;
	}
	
	/**
	 * Initializes the activity recognition system
	 */
	private void initActivityRecognition() {
		ThreadGroup tg = new ThreadGroup("activity");
		tg.setMaxPriority(Thread.MAX_PRIORITY);
		Thread t = new Thread(tg, new Runnable() {
			@Override
			public void run() {
				Process.setThreadPriority(-10);
				sensor = new MotionSensor(mHandler);
			}
		}, "MotionSensor", 2000000) {
		};
		t.start();
		activityStarted = true;
	}
	
	
	/**
	 * Sets a random difficulty
	 */
	private void randomDifficulty() {
		Random generator = new Random();
		difficulty = generator.nextInt(5);	
		if(difficulty < 2) {
			progressBar.setVisibility(View.INVISIBLE);
			timerText.setVisibility(View.INVISIBLE);
		} else {
			progressBar.setVisibility(View.VISIBLE);
			timerText.setVisibility(View.VISIBLE);
		}
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
		                CslActivity.this.finish();
		        		Intent serverIntent = new Intent(getApplicationContext(), HighScoreActivity.class);
		        		startActivity(serverIntent);
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Stops the sensor system
	 */
	private void stopSensorSystem() {
		String[] log = { "Stopped" };
		eventLogger.write(log);
		// Close connections
		if (btStarted)
			btConnection.disconnectBTSensor();
		if (activityStarted)
			sensor.stop();
		// Reset variables
		stopped = true;
		activityStarted = false;
		btStarted = false;
	}

	/**
	 * Handler for managing the communication with the different classes used for the combined stress test.
	 * E.g. MotionSensor, BioSensor ...
	 */
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String[] log = new String[1];
			switch (msg.what) {
				case MESSAGE_CALIB:
	    			break;
	    	   	case MESSAGE_CALIB_FIN:
	    	   		log[0] = "Calibrating Finished";
					eventLogger.write(log);
					sensor.calibrate();
	    			new CountDownTimer(10000, 1000) {
	    			     public void onTick(long millisUntilFinished) {
	    			    	 statusText.setText("seconds remaining: " + millisUntilFinished / 1000);
	    			     }
	    			     public void onFinish() {
	    			    	statusText.setText("START!");
	    					PlaySound.play(getBaseContext());
	    			    	pExercise.startExercise();
		    				sensor.start();
	    			     }
	    			 }.start();
	        		statusText.setText("");
	        		progressDialog.dismiss();
	        		break;
	        	case MESSAGE_UPDATE:
	    	   		log[0] = "New Message";
					eventLogger.write(log);
					//statusText.setText(msg.getData().getString("sdBN"));
	        		break;
	        	case MESSAGE_STOP:
	    	   		log[0] = "Stop";
					eventLogger.write(log);
	        		break;
	        	case EXERCISE_CHANGED:
	        		log[0] = "Exercise changed->"+msg.getData().getString("Activity");
					eventLogger.write(log);
					if(!msg.getData().getString("Activity").equals("Running")) {
						findViewById(R.id.stroop_test).setVisibility(View.VISIBLE);
						if(mExercise != null) {
							mExercise.abort();
							mExercise = null;
							if(timer != null)
								timer.cancel();
						}
						// Create New Mental Stress Exercise Object
						randomDifficulty();
		        		mExercise = new MentalStress(CslActivity.this, mHandler, difficulty, false);
						mExercise.ask();
					} else {
						findViewById(R.id.stroop_test).setVisibility(View.INVISIBLE);
						if(mExercise != null) {
							mExercise.abort();
							mExercise = null;
							if(timer != null)
								timer.cancel();
						}
					}
					statusText.setText(msg.getData().getString("Activity"));
	        		break;
	        	case EXERCISE_FINISHED:
	        		log[0] = "Exercise changed->STOP";
					eventLogger.write(log);
					statusText.setText("STOP");
					dbHelper.createHighscore(MainActivity.NAME, score);
					stopSensorSystem();
					showHighScore();
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
	    				eventLogger.write("Exercise -> Question: Name the Word!");
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
			    					mExercise.ask();
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
	        	case EXERCISE_STARTED:
	        		//TODO: What happens after Stroop start;
	        		break;
	        	case EXERCISE_STROOP_NEXT_LVL:
	        		//TODO: What happens after Stroop level change;
	        		break;
	        	case EXERCISE_STROOP_CANCELED:
	        		//TODO: What happens after Stroop canceled;
	        		break;
	        	case EXERCISE_STROOP_FIN:
	        		//TODO: What happens after Stroop test finished;
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
}
