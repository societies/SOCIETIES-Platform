package dlr.stressrecognition;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.sensor.BioSensor;
import dlr.stressrecognition.sensor.MotionSensor;
import dlr.stressrecognition.utils.BluetoothConnection;
import dlr.stressrecognition.utils.TextProgressBar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of a demonstrator for the activity recognition.
 * 
 * @author Michael Gross
 *
 */
public class DemonstratorActivity extends StressElicitationActivity {
	private BluetoothConnection btConnection;
	private BioSensor bioSensor;
	private MotionSensor sensor;
		
	private TextProgressBar lyingBar;
	private TextProgressBar sittingBar;
	private TextProgressBar standingBar;
	private TextProgressBar walkingBar;
	private TextProgressBar runningBar;
	private TextProgressBar jumpingBar;
	private TextProgressBar fallingBar;
	private TextProgressBar updownBar;
	private ProgressDialog progressDialog;
	private LinearLayout btnLayout;
	private LinearLayout barLayout;
	private TextView heartRateValue;
	private TextView breathingRateValue;
	
	private Toast notification;
	
	private boolean stopped = true;
	private boolean btStarted = false;
	private boolean activityStarted = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create GUI and Layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		setContentView(R.layout.demonstrator);
		heartRateValue = (TextView) findViewById(R.id.hrValue);
		breathingRateValue = (TextView) findViewById(R.id.brValue);
		
    	notification = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        btConnection = new BluetoothConnection(getApplicationContext(), mHandler);
        btConnection.prepareBluetooth();
		
		barLayout = (LinearLayout) findViewById(R.id.barLayout);
		
		btnLayout = new LinearLayout(this);
		btnLayout.setOrientation(LinearLayout.HORIZONTAL);
		btnLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		    	      	
    	lyingBar = (TextProgressBar) findViewById(R.id.pbLying);
    	lyingBar.setProgressDrawable(getResources().getDrawable(R.drawable.red_progress));
    	sittingBar = (TextProgressBar) findViewById(R.id.pbSitting);
    	standingBar = (TextProgressBar) findViewById(R.id.pbStanding);
    	standingBar.setProgressDrawable(getResources().getDrawable(R.drawable.azure_progress));
    	walkingBar = (TextProgressBar) findViewById(R.id.pbWalking);
    	walkingBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progress));
    	runningBar = (TextProgressBar) findViewById(R.id.pbRunning);
    	runningBar.setProgressDrawable(getResources().getDrawable(R.drawable.violet_progress));
    	jumpingBar = (TextProgressBar) findViewById(R.id.pbJumping);
    	jumpingBar.setProgressDrawable(getResources().getDrawable(R.drawable.brown_progress));
    	fallingBar = (TextProgressBar) findViewById(R.id.pbFalling);
    	fallingBar.setProgressDrawable(getResources().getDrawable(R.drawable.rose_progress));
    	updownBar = (TextProgressBar) findViewById(R.id.pbUpdown);
    	updownBar.setProgressDrawable(getResources().getDrawable(R.drawable.yellow_progress));
    	
    	Button startBtn = new Button(this);
    	Button pauseBtn = new Button(this);
    	Button restartBtn = new Button(this);
    	Button mountedBtn = new Button(this);
    	
    	startBtn.setText("Start");
    	startBtn.setLayoutParams(new LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	pauseBtn.setText("Pause");
    	pauseBtn.setLayoutParams(new LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	restartBtn.setText("Restart");
    	restartBtn.setLayoutParams(new LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	mountedBtn.setText("Calibrate");
    	mountedBtn.setLayoutParams(new LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
    			LinearLayout.LayoutParams.WRAP_CONTENT));
    	
    	btnLayout.addView(startBtn);
    	btnLayout.addView(pauseBtn);
    	btnLayout.addView(restartBtn);
    	btnLayout.addView(mountedBtn);
    	
    	barLayout.addView(btnLayout);

    	// Attach Start Button Handler
    	startBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			sensor.start();
    		}
    	});
    	   	
		// Attach Stop Button Handler
    	pauseBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			sensor.pause();
    		}
    	});
    	
		// Attach Stop Button Handler
    	restartBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			sensor.restart();
    		}
    	});
    	
		// Attach Stop Button Handler
    	mountedBtn.setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			sensor.calibrate();
    		}
    	});
	}
	
	/** Called when the activity is started. */
	@Override
	public void onStart() {
		super.onStart();
		// Create bio sensor object
		bioSensor = new BioSensor(mHandler, mHandler);
		bioSensor.setLogging(false);
		if(stopped) {
			progressDialog = ProgressDialog.show(DemonstratorActivity.this, "", 
                "Initializing ARS. Please wait...", true);
   			initSensorSystem();
		}
	}
	
	/** Called when the activity is first destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(!stopped)
			stopSensorSystem();
	}
	
	/**
	 * Initializes the sensor system
	 */
	private void initSensorSystem() {
		// Start BT Sensor
		if(!btStarted) {
			btConnection.connectBTSensor(bioSensor);
		}
		// Start Activity Recognition
		if(!activityStarted)
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
		Thread t = new Thread(tg, 
				new Runnable() {
					@Override
						public void run() {
							Process.setThreadPriority(-10);
							sensor = new MotionSensor(mHandler);
						}
				}
				, "MotionSensor", 2000000) {
		};
		t.start();
		activityStarted = true;
	}
	
	/**
	 * Stops the sensor system
	 */
	private void stopSensorSystem() {
		//Close connections
		if(btStarted)
			btConnection.disconnectBTSensor();
		if(activityStarted)
			sensor.stop();
		//Reset variables
		stopped = true;
		activityStarted = false;
		btStarted = false;		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.demonstrator_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.get_classifier:
			// Show close dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("The currently used classifier is:\n" +
					sensor.getClassifier());
			builder.setCancelable(true);
			AlertDialog alert = builder.create();
			alert.show();
			return true;
		}
		return false;
	}
	
	/**
	 * Handler for managing the communication with the different classes used for the combined stress test.
	 * E.g. MotionSensor, BioSensor ...
	 */
	public final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE_CALIB:
					break;
	    	   	case MESSAGE_CALIB_FIN:
	    	   		new CountDownTimer(10000, 1000) {
	    			     public void onTick(long millisUntilFinished) {
	    			     }
	    			     public void onFinish() {
	    		    	   	progressDialog.dismiss();
	    			    	sensor.start();
	    			     }
	    			 }.start();
	    	   		break;
	        	case MESSAGE_UPDATE:
					double[] prob = msg.getData().getDoubleArray("dBN");
			        sittingBar.setProgress((int)(prob[0]*100));
			        sittingBar.setText(String.valueOf((int)(prob[0]*100))+" %");  
			        standingBar.setProgress((int)(prob[1]*100));
			        standingBar.setText(String.valueOf((int)(prob[1]*100))+" %");  
			        walkingBar.setProgress((int)(prob[2]*100));
			        walkingBar.setText(String.valueOf((int)(prob[2]*100))+" %");  
			        runningBar.setProgress((int)(prob[3]*100));
			        runningBar.setText(String.valueOf((int)(prob[3]*100))+" %");  
			        jumpingBar.setProgress((int)(prob[4]*100));
			        jumpingBar.setText(String.valueOf((int)(prob[4]*100))+" %");  
			        fallingBar.setProgress((int)(prob[5]*100));
			        fallingBar.setText(String.valueOf((int)(prob[5]*100))+" %");  
			        lyingBar.setProgress((int)(prob[6]*100));
			        lyingBar.setText(String.valueOf((int)(prob[6]*100))+" %");  
			        updownBar.setProgress((int)(prob[7]*100));
			        updownBar.setText(String.valueOf((int)(prob[7]*100))+" %");  
			        break;
	        	case MESSAGE_STOP:
	        		break;
	        	case HEART_RATE:
	        		if(activityStarted)
	        			heartRateValue.setText(msg.getData().getString("HeartRate"));
	        		break;
	        	case RESPIRATION_RATE:
	        		if(activityStarted)
	        			breathingRateValue.setText(msg.getData().getString("RespirationRate"));
	        		break;
	        	case SKIN_TEMPERATURE:
	        		break;
	        	case ECG:
	        		break;
	        	case RESPIRATION:
	        		break;
	        	case RR:
	        		break;
	        	case GSR:
	        		break;
	        	case BRAMPLITUDE:
	        		break;
	        	case ECGAMPLITUDE:
	        		break;
	        	case ECGNOISE:
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
