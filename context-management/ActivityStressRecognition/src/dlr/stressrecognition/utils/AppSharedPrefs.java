package dlr.stressrecognition.utils;

import dlr.stressrecognition.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * The AppSharedPrefs class handles the preference settings of the application.
 * 
 * @author Michael Gross
 *
 */
public class AppSharedPrefs {
	public final static String PREFS_NAME = "prefs";

	public static int getTaskTimer(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return Integer.parseInt(prefs.getString(
				context.getString(R.string.prefTimerKey), "5"));
	}

	public static void setTaskTimer(Context context, int newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefTimerKey), String.valueOf(newValue));
		prefsEditor.commit();
	}
	
	public static int getDifficulty(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return Integer.parseInt(prefs.getString(
				context.getString(R.string.prefDifficultyKey), "-1"));
	}

	public static void setDifficulty(Context context, int newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefDifficultyKey), String.valueOf(newValue));
		prefsEditor.commit();
	}
	
	public static String getName(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getString(context.getString(R.string.prefNameKey), "-1");
	}

	public static void setName(Context context, String newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefNameKey), newValue);
		prefsEditor.commit();
	}
	
	public static String getAge(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getString(context.getString(R.string.prefAgeKey), "-1");
	}

	public static void setAge(Context context, String newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefAgeKey), newValue);
		prefsEditor.commit();
	}
	
	public static int getGender(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return Integer.parseInt(prefs.getString(
				context.getString(R.string.prefGenderKey), "-1"));
	}

	public static void setGender(Context context, int newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefGenderKey), String.valueOf(newValue));
		prefsEditor.commit();
	}
	
	public static int getActivityTrace(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return Integer.parseInt(prefs.getString(
				context.getString(R.string.prefActivityTraceKey), "1"));
	}

	public static void setActivityTrace(Context context, int newValue) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		Editor prefsEditor = prefs.edit();
		prefsEditor.putString(
				context.getString(R.string.prefActivityTraceKey), String.valueOf(newValue));
		prefsEditor.commit();
	}
}
