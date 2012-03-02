package dlr.stressrecognition.elicitation;

import java.util.ArrayList;
import java.util.Random;

import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.utils.AppSharedPrefs;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

public class MentalStress {
	private Context mContext;
	private Handler mHandler;
		
	private int difficulty;
	private int taskTimer;
	boolean answerType = true;
	
	private ArrayList<String> colors = new ArrayList<String>();
	private String[] answers = new String[4];
	private String stroopColorWord;
	private int stroopColorFont;
	private boolean timeUp = false;
	private boolean autoInc = false;
	private GameTimer gameTimer;
	
	public MentalStress(Context context, Handler mHandler, int level, boolean increase) {
		this.mContext = context;
		this.mHandler = mHandler;
        this.difficulty = level;
        this.taskTimer = AppSharedPrefs.getTaskTimer(mContext)*60000;
        this.autoInc = increase;
		
		colors.add("red");
		colors.add("blue");
		colors.add("yellow");
		colors.add("green");
		colors.add("black");
		colors.add("white");
		colors.add("magenta");
		
		// Start task timer
		gameTimer = new GameTimer(taskTimer, 1000);
		gameTimer.start();
		
		mHandler.sendEmptyMessage(StressElicitationActivity.EXERCISE_STARTED);
	}
	
	private String[] getAnswers(int correct) {
		
		ArrayList<String> tempColors = new ArrayList<String>();
		tempColors.addAll(colors);
		tempColors.remove(correct);
		
		Random generator = new Random();	
		int position = generator.nextInt(answers.length);
		String[] temp = new String[4];
		temp[position] = colors.get(correct);
		for(int i = 0; i < answers.length; i++) {
			if(i == position)
				continue;
			int wrong = generator.nextInt(tempColors.size());
			temp[i] = tempColors.get(wrong);
			tempColors.remove(wrong);
		}
		
		return temp;
	}
	
	private int createIncongruentColor(int color) {
		Random generator = new Random();
		int incongruentColor = generator.nextInt(colors.size());
		while(color == incongruentColor)
			incongruentColor = generator.nextInt(colors.size());
		
		return incongruentColor;
	}
	
	public void ask() {
		Random generator = new Random();
		int colorWord = generator.nextInt(colors.size());
		// Create only incongruent combinations
		int fontColor = createIncongruentColor(colorWord);
		
		ArrayList<String> tempColors = new ArrayList<String>();
		tempColors.addAll(colors);
		String[] answers = new String[4];
		int[] answerColors = {Color.parseColor("black"), Color.parseColor("black"), Color.parseColor("black"), Color.parseColor("black")};
		int timer = 0;
		
		if(difficulty > 3)
			answerType = generator.nextBoolean();
		// From difficulty > 3 on, the right answer is the word or the color
		// depending on the question
		if(answerType) {
			answers = getAnswers(fontColor);
		} else {
			answers = getAnswers(colorWord);
		}
		
		stroopColorWord = colors.get(colorWord);
		stroopColorFont = Color.parseColor(colors.get(fontColor));
		
		if(difficulty == 5) {
			timer = 3;
		} else if(difficulty > 1) {
			timer = 5;
		}
		
		if(difficulty > 2) {
			for(int i = 0; i < answers.length; i++) {
				int color = generator.nextInt(tempColors.size());
				answerColors[i] = Color.parseColor(tempColors.get(color));
				tempColors.remove(color);
			}
		}
				
		Message msg = mHandler.obtainMessage(StressElicitationActivity.EXERCISE_STROOP_ASK);
		Bundle data = new Bundle();
		data.putString("Question", stroopColorWord);
		data.putInt("QuestionColor", stroopColorFont);
		data.putStringArray("Answers", answers);
		data.putIntArray("AnswersColors", answerColors);
		data.putInt("Timer", timer);
		data.putBoolean("AnswerType", answerType);
		msg.setData(data);
		mHandler.sendMessage(msg);
	}
	
	public boolean checkAnswer(String answer) {
		if(timeUp) {
			difficulty++;
			gameTimer.start();
			mHandler.sendEmptyMessage(StressElicitationActivity.EXERCISE_STROOP_NEXT_LVL);
			timeUp = false;
		}
		if(answerType) {
			return (stroopColorFont == Color.parseColor(answer));
		} else {
			return (stroopColorWord.equals(answer));
		}
	}
	
	public void abort() {
		gameTimer.cancel();
		mHandler.sendEmptyMessage(StressElicitationActivity.EXERCISE_STROOP_CANCELED);
	}
	
	private void stop() {
		mHandler.sendEmptyMessage(StressElicitationActivity.EXERCISE_STROOP_FIN);
	}
	
	private class GameTimer extends CountDownTimer {

		public GameTimer(long millisInFuture, long countDownIntervall) {
			super(millisInFuture, countDownIntervall);
		}
		
		@Override
		public void onFinish() {
			if(difficulty < 5 && autoInc ) {
				timeUp = true;
			} else {
				stop();
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
}