package dlr.stressrecognition.classifier;

import android.app.Activity;

/**
 * Parent class for all stress elicitation activities
 * 
 * @author Michael Gross
 *
 */
public class StressElicitationActivity extends Activity {
	// XSens IMU
	public static final int MESSAGE_TEST = 0x001;
	public static final int MESSAGE_CALIB = 0x002;
	public static final int MESSAGE_CALIB_FIN = 0x003;
	public static final int MESSAGE_UPDATE = 0x004;
	public static final int MESSAGE_STOP = 0x005;

	public static final int MESSAGE_STRESS = 0x006;
	
	// Zephyr Biosensor
	public static final int HEART_RATE = 0x100;
	public static final int RESPIRATION_RATE = 0x101;
	public static final int SKIN_TEMPERATURE = 0x102;
	public static final int POSTURE = 0x103;
	public static final int PEAK_ACCLERATION = 0x104;
	public static final int ECG = 0x105;
	public static final int RESPIRATION = 0x106;
	public static final int RR = 0x107;
	public static final int GSR = 0x108;
	public static final int BRAMPLITUDE = 0x109;
	public static final int ECGAMPLITUDE = 0x110;
	public static final int ECGNOISE = 0x111;
	
	// Bluetooth Handling
	public static final int BT_NOTAVAILABLE = 0x200;
	public static final int BT_CONNECTED = 0x201;
	public static final int BT_NOTCONNECTED = 0x202;
	
	// General exercise
	public static final int EXERCISE_STARTED = 0x300;
	public static final int EXERCISE_CHANGED = 0x301;
	public static final int EXERCISE_FINISHED = 0x302;
	
	// Stroop test specific
	public static final int EXERCISE_STROOP_ASK = 0x303;
	public static final int EXERCISE_STROOP_FIN = 0x304;
	public static final int EXERCISE_STROOP_NEXT_LVL = 0x305;
	public static final int EXERCISE_STROOP_CANCELED = 0x306;
	
	// Activity test specific
	
	// Logging
	public static long STARTUP = 0;
	public static String LOGFILE = "";
}
