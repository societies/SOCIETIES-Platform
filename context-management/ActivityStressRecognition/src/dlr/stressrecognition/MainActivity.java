package dlr.stressrecognition;

import java.io.DataOutputStream;
import java.io.IOException;

import dlr.stressrecognition.classifier.SRSActivity;
import dlr.stressrecognition.classifier.SRSEvaluationActivity;
import dlr.stressrecognition.classifier.SRSMslActivity;
import dlr.stressrecognition.utils.AppSharedPrefs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// Intent request codes
	private static final int REQUEST_PERSONAL_INFO = 1;
	private static final int REQUEST_DIFFICULTY = 2;

	// Test subject related
	public static String NAME;
	private String age;
	private String gender;

	public static final long STARTUP = System.nanoTime();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Give read / write / exec access to /dev/
		execCommand("busybox chmod 777 /dev/");
		execCommand("busybox chmod 777 /data/local/tmp/");

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_menu);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.window_title);

		GridView grid = (GridView) findViewById(R.id.grid);
		grid.setAdapter(new HomeScreenShortcutAdapter(this));
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				Intent serverIntent = null;
				switch (position) {
				case 0:
					serverIntent = new Intent(getApplicationContext(),
							PslActivity.class);
					startActivity(serverIntent);
					break;
				case 1:
					serverIntent = new Intent(getApplicationContext(),
							MslActivity.class);
					startActivity(serverIntent);
					break;
				case 2:
					serverIntent = new Intent(getApplicationContext(),
							CslActivity.class);
					startActivityForResult(serverIntent, REQUEST_PERSONAL_INFO);
					break;
				case 3:
					serverIntent = new Intent(getApplicationContext(),
							ShowRulesActivity.class);
					startActivity(serverIntent);
					break;
				case 4:
					serverIntent = new Intent(getApplicationContext(),
							DemonstratorActivity.class);
					startActivity(serverIntent);
					break;
				case 5:
					serverIntent = new Intent(getApplicationContext(),
							SRSActivity.class);
					startActivity(serverIntent);
					break;
				case 6:
					serverIntent = new Intent(getApplicationContext(),
							SRSEvaluationActivity.class);
					startActivity(serverIntent);
					break;
				case 7:
					serverIntent = new Intent(getApplicationContext(),
							SRSMslActivity.class);
					startActivity(serverIntent);
					break;
				}
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
		NAME = AppSharedPrefs.getName(getApplicationContext());
		age = AppSharedPrefs.getAge(getApplicationContext());
		int gender_temp = AppSharedPrefs.getGender(getApplicationContext());
		if (NAME == "" || age == "" || gender_temp == -1) {
			Intent serverIntent = new Intent(getApplicationContext(),
					EnterInfoActivity.class);
			startActivityForResult(serverIntent, REQUEST_PERSONAL_INFO);
			
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_PERSONAL_INFO:
			if (resultCode == Activity.RESULT_OK) {
				setPersonalInfo(data);
				Toast.makeText(this, R.string.pi_set, Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this, R.string.pi_not_set, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
	}

	private void setPersonalInfo(Intent data) {
		Bundle extras = data.getExtras();
		// Save Name, Age, Gender of Test subject
		NAME = extras.getString("dlr.stressrecognition.Name");
		age = extras.getString("dlr.stressrecognition.Age");
		gender = extras.getString("dlr.stressrecognition.Gender");
		AppSharedPrefs.setName(getApplicationContext(), NAME);
		AppSharedPrefs.setAge(getApplicationContext(), age);
		if(gender.equals("Female")) {
			AppSharedPrefs.setGender(getApplicationContext(), 1);
		} else if(gender.equals("Male")) {
			AppSharedPrefs.setGender(getApplicationContext(), 2);
		}
	}

	private class HomeScreenShortcutAdapter extends BaseAdapter {
		Context mContext;

		HomeScreenShortcutAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			return 8;
		}

		@Override
		public Object getItem(int position) {
			switch (position) {
			case 0:
				return "Physical Stress Logging";
			case 1:
				return "Mental Stress Logging";
			case 2:
				return "Combined Stress Logging";
			case 3:
				return "Show rules";
			case 4:
				return "Demonstrator";
			case 5:
				return "SRS Showcase";
			case 6:
				return "SRS Evaluation";
			case 7:
				return "SRS Evaluation Mental";
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv;
			if (convertView == null) {
				tv = new TextView(mContext);
				tv.setGravity(Gravity.CENTER);
			} else {
				tv = (TextView) convertView;
			}

			tv.setText((CharSequence) getItem(position));
			return tv;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent serverIntent;
		switch (item.getItemId()) {
		/*case R.id.enter_personal_info:
			serverIntent = new Intent(getApplicationContext(),
					EnterInfoActivity.class);
			startActivityForResult(serverIntent, REQUEST_PERSONAL_INFO);
			return true;
		*/
		case R.id.set_preferences:
			serverIntent = new Intent(getApplicationContext(),
					SetPreferencesActivity.class);
			startActivityForResult(serverIntent, REQUEST_DIFFICULTY);
			return true;
		}
		return false;
	}
	
	public Boolean execCommand(String command) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process process = rt.exec("su");
			DataOutputStream os = new DataOutputStream(
					process.getOutputStream());
			os.writeBytes(command + "\n");
			os.flush();
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (IOException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}
}