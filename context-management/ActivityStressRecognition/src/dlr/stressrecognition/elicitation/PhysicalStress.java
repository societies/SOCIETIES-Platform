package dlr.stressrecognition.elicitation;

import java.util.ArrayList;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.utils.AppSharedPrefs;
import dlr.stressrecognition.utils.PlaySound;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class PhysicalStress {
	private Context mContext;
	private Handler mHandler;
	public enum activities {RUNNING, WALKING, STANDING, SITTING, LYING, JUMPING, FALLING};
	private ArrayList<String> activityTrace = new ArrayList<String>();
	private int countdown;
	private int trace;
	
	public PhysicalStress(Context context, Handler mHandler) {
		this.mContext = context;
		this.mHandler = mHandler;
		this.countdown = AppSharedPrefs.getTaskTimer(context) * 60000;
		this.trace =  AppSharedPrefs.getActivityTrace(context);
		
		switch(trace) {
		case 1:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Sitting");
			break;
		case 2:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Walking");
			break;
		case 3:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Running");
			break;
		case 4:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Running");
			activityTrace.add("Walking");
			activityTrace.add("Sitting");
			break;
		case 5:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Walking");
			activityTrace.add("Running");
			activityTrace.add("Walking");
			activityTrace.add("Sitting");
			activityTrace.add("Running");
			activityTrace.add("Walking");
			break;
		case 6:
			activityTrace = new ArrayList<String>();
			activityTrace.add("Running");
			activityTrace.add("Walking");
			activityTrace.add("Sitting");
			activityTrace.add("Walking");
			activityTrace.add("Sitting");
			activityTrace.add("Running");
			break;
		
		}
	}
	
	private void sendMsg(Bundle data) {
		Message msg = mHandler.obtainMessage(StressElicitationActivity.EXERCISE_CHANGED);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
	
	public void startExercise() {
   	 	if(!activityTrace.isEmpty()) {
   			Bundle data = new Bundle();
   			data.putString("Activity", activityTrace.get(0));
   			sendMsg(data);
   			activityTrace.remove(0);
   			
   			// Start task timer
   			GameTimer gameTimer = new GameTimer(countdown, 1000);
   			gameTimer.start();

   	 	} else {
   			mHandler.sendEmptyMessage(StressElicitationActivity.EXERCISE_FINISHED);
   	 	}
	}
	
	private class GameTimer extends CountDownTimer {

		public GameTimer(long millisInFuture, long countDownIntervall) {
			super(millisInFuture, countDownIntervall);
		}
		
		@Override
		public void onFinish() {
   	   		PlaySound.play(mContext);
   	   		startExercise();
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
	
	
}
