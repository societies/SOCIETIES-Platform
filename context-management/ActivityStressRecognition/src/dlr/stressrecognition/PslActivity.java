package dlr.stressrecognition;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.elicitation.PhysicalStress;
import dlr.stressrecognition.logger.Logger;
import dlr.stressrecognition.sensor.BioSensor;
import dlr.stressrecognition.sensor.MotionSensor;
import dlr.stressrecognition.utils.BluetoothConnection;
import dlr.stressrecognition.utils.PlaySound;
import dlr.stressrecognition.R;

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
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Implementation of a physical stress test. It displays the next activity to be conducted by the user.
 * 
 * @author Michael Gross
 *
 */
public class PslActivity extends StressElicitationActivity {
	private BluetoothConnection btConnection;
	    
    private MotionSensor sensor = null;
    private BioSensor bioSensor = null;
    
    private Logger eventLogger;
    private PhysicalStress exercise;
    
    private boolean stopped = true;
    private boolean btStarted = false;
    private boolean activityStarted = false;
	
	//GUI related attributes
	private TextView statusText;
	private ProgressDialog progressDialog;
    private Toast notification;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		//Create GUI and Layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_test);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.window_title);
    	statusText = (TextView) findViewById(R.id.textViewStatus);
    	
      	notification = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    	
        btConnection = new BluetoothConnection(getApplicationContext(), mHandler);
        btConnection.prepareBluetooth();
        
		// Attach Start Button Handler
    	findViewById(R.id.btnStart).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			String[] log =  new String[1];
    			log[0] = "Calibrating";
				eventLogger.write(log);
    			progressDialog = ProgressDialog.show(PslActivity.this, "", 
                        "Initializing ARS. Please wait...", true);
    	   		initSensorSystem();
    		}
    	});
    	   	
		// Attach Stop Button Handler
    	findViewById(R.id.btnStop).setOnClickListener(new Button.OnClickListener() {
    		public void onClick(View v) {
    			statusText.setText("Stopped");
    			stopSensorSystem();
    		}
    	});
	}
	
	/** Called when the activity is started. */	
	@Override
	public void onStart() {
		super.onStart();
		// Set Event Logger
		LOGFILE = "-" + System.currentTimeMillis();
		eventLogger = new Logger("Event");
		String[] events = {"Event"};
		eventLogger.writeHeader(events);
		
		// Create bio sensor object
        bioSensor = new BioSensor(mHandler, mHandler);
		// Create Physical Stress Exercise Object
		exercise = new PhysicalStress(this, mHandler);
	}
	
	/** Called when the activity is destroyed. */
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
	 * Stops the sensor system
	 */	
	private void stopSensorSystem() {
		String[] log = {"Stopped"};
		eventLogger.write(log);
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
	
	/**
	 * Handler for managing the communication with the different classes used for the combined stress test.
	 * E.g. MotionSensor, BioSensor ...
	 */
	public final Handler mHandler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
			String[] log =  new String[1];
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
	    			    	exercise.startExercise();
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
					statusText.setText(msg.getData().getString("Activity"));
            		break;
            	case EXERCISE_FINISHED:
            		log[0] = "Exercise changed->STOP";
					eventLogger.write(log);
					statusText.setText("STOP");
					stopSensorSystem();
					finish();
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