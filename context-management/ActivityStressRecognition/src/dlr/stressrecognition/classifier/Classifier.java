package dlr.stressrecognition.classifier;

import dlr.stressrecognition.logger.Logger;
import dlr.stressrecognition.system.StressConsumer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Implements the StressConsumer interface and receives the inferred stress.
 * The received values are given to the GUI or logged depending on the settting.
 * 
 * @author Michael Gross
 *
 */
public class Classifier implements StressConsumer {
	private Logger logger;
	private Handler mHandler;
	private boolean logging = true;
	
	public Classifier(Handler mHandler) {
		this.mHandler = mHandler;
		this.logger = new Logger("Stress");
		if(logging) {
			String[] header = {"Stress Level"};
			logger.writeHeader(header);
		}
	}
	
	@Override
	public void updateStress(String stress, String[] states, double[] prob) {
		if(logging) {
			logger.write(stress);
		} 
		Message msg = mHandler.obtainMessage(StressElicitationActivity.MESSAGE_STRESS);
		Bundle data = new Bundle();
		data.putString("stress", stress);
		data.putDoubleArray("prob", prob);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
}
