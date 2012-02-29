package dlr.stressrecognition.sensor;

import org.personalsmartspace.cm.reasoning.activity.impl.ARS_Administrator;
import org.personalsmartspace.cm.reasoning.activity.impl.DataReader;
import org.personalsmartspace.cm.reasoning.activity.impl.IMUBelt_ARS;
import org.personalsmartspace.cm.reasoning.activity.interfaces.ActivityConsumer;
import org.personalsmartspace.cm.reasoning.activity.sensors.SensorIMU_Belt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.logger.Logger;

/**
 * The MotionSensor class handles the connection with the ARS.
 * 
 * @author Michael Gross
 *
 */
public class MotionSensor implements ActivityConsumer {
	private Handler mHandler;
	private Logger logger;
	private DataReader d;
	private IMUBelt_ARS beltARSIMU;
	private String classifier = "Not set";
	final String serialPort = "/dev/ttyUSB0";

	public MotionSensor(Handler mHandler) {
		this.mHandler = mHandler;
		this.logger = new Logger("Activity");
		SensorIMU_Belt iMU = new SensorIMU_Belt();
		beltARSIMU = new IMUBelt_ARS(iMU);
		beltARSIMU.setConsumer(this);
				
		ARS_Administrator adminARS = new ARS_Administrator(this);
		adminARS.registerIARSSensorLocation(beltARSIMU);
		
		d = new DataReader(adminARS, serialPort);
		d.registerISensor(iMU);
		
		String[] activities = { "Sitting", "Standing", "Walking", "Running",
				"Jumping", "Falling", "Lying", "UpDown", "Time" };
		logger.writeHeader(activities);
		Message msg = mHandler.obtainMessage(StressElicitationActivity.MESSAGE_CALIB_FIN);
		mHandler.sendMessage(msg);
	}

	public void start() {
		beltARSIMU.setComputeInitialConditions(true);
	}

	public void stop() {
		beltARSIMU.stopSystem();
		d.stopXSens();
		Message msg = mHandler
				.obtainMessage(StressElicitationActivity.MESSAGE_STOP);
		mHandler.sendMessage(msg);
	}

	public void restart() {
		beltARSIMU.restartSystem();
	}

	public void pause() {
		beltARSIMU.stopSystem();
	}

	public void calibrate() {
		beltARSIMU.timeToGetRotationMatrixFromSensorToBody();
	}

	@Override
	public void update(long time, String classifier, double[] nB, double[] dNB, double[] bN,
			double[] dBN, String snB, String sdNB, String sbN, String sdBN) {
		String activity = "";
		System.out.println(classifier);
		double[] output = null;
		if(classifier.equals("sNB")) { 
			String[] data = new String[nB.length + 1];
			for(int i=0; i < nB.length; i++) {
				data[i] = String.valueOf(nB[i]);
			}
			data[nB.length] = String.valueOf(time);
			logger.write(data);
			activity = snB;
			output = nB;
		} else if(classifier.equals("dNB")) {
			String[] data = new String[dNB.length + 1];
			for(int i=0; i < dNB.length; i++) {
				data[i] = String.valueOf(dNB[i]);
			}
			data[dNB.length] = String.valueOf(time);
			logger.write(data);
			activity = sdNB;
			output = dNB;
		} else if(classifier.equals("sBN")) {
			String[] data = new String[bN.length + 1];
			for(int i=0; i < bN.length; i++) {
				data[i] = String.valueOf(bN[i]);
			}
			data[bN.length] = String.valueOf(time);
			logger.write(data);
			activity = sbN;
			output = bN;
		} else if(classifier.equals("dBN")) {
			String[] data = new String[dBN.length + 1];
			for(int i=0; i < dBN.length; i++) {
				data[i] = String.valueOf(dBN[i]);
			}
			data[dBN.length] = String.valueOf(time);
			logger.write(data);
			activity = sdBN;
			output = dBN;
		}
		setClassifier(classifier);	
		Message msg = mHandler.obtainMessage(StressElicitationActivity.MESSAGE_UPDATE);
		Bundle data = new Bundle();
		data.putString("activity", activity);
		data.putString("sNB", snB);
		data.putString("sdNB", sdNB);
		data.putString("sbN", sbN);
		data.putString("sdBN", sdBN);
		/*
		 * dBN 0: "Sitting" 1: "Standing" 2: "Walking" 3: "Running" 4: "Jumping"
		 * 5: "Falling" 6: "Lying" 7: "UpDown"
		 */
		data.putDoubleArray("dBN", output);

		msg.setData(data);
		mHandler.sendMessage(msg);
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getClassifier() {
		return classifier;
	}
}
