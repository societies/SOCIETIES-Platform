package dlr.stressrecognition.classifier;

import dlr.stressrecognition.sensor.BioSensor;
import dlr.stressrecognition.sensor.MotionSensor;
import dlr.stressrecognition.utils.BluetoothConnection;
import dlr.stressrecognition.utils.TextProgressBar;
import dlr.stressrecognition.R;
import dlr.stressrecognition.system.*;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demonstrator of the stress inference.
 * 
 * @author Michael Gross
 *
 */
public class SRSActivity extends StressElicitationActivity {
	private BluetoothConnection btConnection;
	private BioSensor bioSensor;
	private MotionSensor sensor;
	private Classifier classifier = null;
	private TextView statusText;
	private StressClassifier srs;
	private DataReader dr;
	
	private TextProgressBar restingBar;
	private TextProgressBar stressoneBar;
	private TextProgressBar stresstwoBar;
	private TextProgressBar stressthreeBar;
	private TextProgressBar stressfourBar;
	private TextProgressBar stressfiveBar;
	
	private ProgressDialog progressDialog;
	
	private boolean stopped = true;
	private boolean btStarted = false;
	private boolean activityStarted = false;

	// GUI related attributes
	private Toast notification;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notification = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        btConnection = new BluetoothConnection(getApplicationContext(), mHandler);
        btConnection.prepareBluetooth();
		
		// Create GUI and Layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.srs);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);
		
		statusText = (TextView) findViewById(R.id.stress);
		
    	restingBar = (TextProgressBar) findViewById(R.id.pbResting);
    	stressoneBar = (TextProgressBar) findViewById(R.id.pbStressOne);
    	stressoneBar.setProgressDrawable(getResources().getDrawable(R.drawable.azure_progress));
    	stresstwoBar = (TextProgressBar) findViewById(R.id.pbStressTwo);
    	stresstwoBar.setProgressDrawable(getResources().getDrawable(R.drawable.blue_progress));
    	stressthreeBar = (TextProgressBar) findViewById(R.id.pbStressThree);
    	stressthreeBar.setProgressDrawable(getResources().getDrawable(R.drawable.yellow_progress));
    	stressfourBar = (TextProgressBar) findViewById(R.id.pbStressFour);
    	stressfourBar.setProgressDrawable(getResources().getDrawable(R.drawable.red_progress));
    	stressfiveBar = (TextProgressBar) findViewById(R.id.pbStressFive);
    	stressfiveBar.setProgressDrawable(getResources().getDrawable(R.drawable.violet_progress));
	}
	
	/** Called when the activity is started. */
	@Override
	public void onStart() {
		super.onStart();
		STARTUP = System.nanoTime();

		// Create bio sensor object
		bioSensor = new BioSensor(mHandler, mHandler);
		progressDialog = ProgressDialog.show(SRSActivity.this, "", 
                "Initializing ARS. Please wait...", true);
   		initSensorSystem();
		
		classifier = new Classifier(mHandler);
		double[] baseline = {68.058212058212060, 13.110602910602905, 33.903742203742300, 36.445979165902780};
		srs = new SRS_DLR(classifier, baseline);
		dr = new DataReader(srs.getSignals(), srs);
	}

	/** Called when the activity is destroyed. */
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!stopped) {
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
	 * Stops the sensor system
	 */	
	private void stopSensorSystem() {
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
			Bundle b;
			switch (msg.what) {
			case MESSAGE_CALIB:
				break;
			case MESSAGE_CALIB_FIN:
				sensor.calibrate();
        		statusText.setText("");
        		progressDialog.dismiss();
				break;
			case MESSAGE_STRESS:
				Bundle b2 = msg.getData();
				String stress = b2.getString("stress");
				double[] prob = b2.getDoubleArray("prob");
				System.out.println(prob[0]);
				statusText.setText(stress);
		        restingBar.setProgress((int)(prob[0]*100));
		        restingBar.setText(String.valueOf((int)(prob[0]*100))+" %");  
		        stressoneBar.setProgress((int)(prob[1]*100));
		        stressoneBar.setText(String.valueOf((int)(prob[1]*100))+" %");  
		        stresstwoBar.setProgress((int)(prob[2]*100));
		        stresstwoBar.setText(String.valueOf((int)(prob[2]*100))+" %");  
		        stressthreeBar.setProgress((int)(prob[3]*100));
		        stressthreeBar.setText(String.valueOf((int)(prob[3]*100))+" %");  
		        stressfourBar.setProgress((int)(prob[4]*100));
		        stressfourBar.setText(String.valueOf((int)(prob[4]*100))+" %");  
		        stressfiveBar.setProgress((int)(prob[5]*100));
		        stressfiveBar.setText(String.valueOf((int)(prob[5]*100))+" %");
			case MESSAGE_UPDATE:
				Bundle activityMsg = msg.getData();
				String activity = activityMsg.getString("activity");
				double[] probabilities = activityMsg.getDoubleArray("dBN");
				if(activity != null) {
					dr.updateActivity(activity, probabilities);
				}
				break;
			case MESSAGE_STOP:
				break;
			case HEART_RATE:
				b = msg.getData();
				double hr = b.getDouble("HeartRate");
				double br = b.getDouble("RespirationRate");
				double temp = b.getDouble("SkinTemperature");
				if(dr != null) {
					dr.updateGP(hr, br, temp);				
				}
				break;
			case RESPIRATION_RATE:
				break;
			case SKIN_TEMPERATURE:
				break;
			case ECG:
				break;
			case RESPIRATION:
				break;
			case RR:
				Bundle bRR = msg.getData();
				int rr = bRR.getInt("RtoR");
				if(dr != null) {
					dr.updateRR(rr);
				}
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
				notification.setText(msg.getData().getString("device")
						+ "connected");
				notification.show();
				break;
			case BT_NOTCONNECTED:
				notification.setText("Unable to connect");
				notification.show();
				break;
			case BT_NOTAVAILABLE:
				Toast.makeText(getApplicationContext(),
						"Bluetooth is not available", Toast.LENGTH_LONG).show();
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