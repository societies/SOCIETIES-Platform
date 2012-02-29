package dlr.stressrecognition.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BTBondReceiver extends BroadcastReceiver {

	private BluetoothAdapter mBluetoothAdapter = null;

	public BTBondReceiver(BluetoothAdapter btAdapter) {
		super();
		mBluetoothAdapter = btAdapter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(b.get(
				"android.bluetooth.device.extra.DEVICE").toString());
		Log.d("Bond state", "BOND_STATED = " + device.getBondState());
	}
}
