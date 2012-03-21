package dlr.stressrecognition.utils;

import java.util.Set;

import zephyr.android.BioHarnessBT.BTClient;
import zephyr.android.BioHarnessBT.ZephyrProtocol;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import dlr.stressrecognition.classifier.StressElicitationActivity;
import dlr.stressrecognition.sensor.BioSensor;

/**
 * Handles the connection with the Zephyr BT client.
 * 
 * @author Michael Gross
 *
 */
public class BluetoothConnection {
	// Zephyr BT Client and communication protocol
	BTClient _bt;
	ZephyrProtocol _protocol;
	
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Name of the connected device
	private String mConnectedDeviceName = null;
	
	// Context of the Activity and notification handler
	private Context mContext;
	private Handler mHandler = null;
	
	public BluetoothConnection(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
	}
	
	public void prepareBluetooth() {
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Message msg = mHandler.obtainMessage(StressElicitationActivity.BT_NOTAVAILABLE);
			mHandler.sendMessage(msg);
		}
		
		/*
		 * Sending a message to android that we are going to initiate a pairing
		 * request
		 */
		IntentFilter filter = new IntentFilter(
				"android.bluetooth.device.action.PAIRING_REQUEST");

		/*
		 * Registering a new BTBroadcast receiver from the Main Activity context
		 * with pairing request event
		 */
		mContext.registerReceiver(
				new BTBroadcastReceiver(mBluetoothAdapter), filter);

		// Registering the BTBondReceiver in the application that the status of
		// the receiver has changed to Paired
		IntentFilter filter2 = new IntentFilter(
				"android.bluetooth.device.action.BOND_STATE_CHANGED");
		mContext.registerReceiver(
				new BTBondReceiver(mBluetoothAdapter), filter2);
	}
	
	public void connectBTSensor(BioSensor bioSensor) {
		String BhMacID = "00:07:80:9D:8A:E8";
		// BhMacID = btDevice.getAddress();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();

		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().startsWith("BH")) {
					BluetoothDevice btDevice = device;
					BhMacID = btDevice.getAddress();
					System.out.println(BhMacID);
					break;
				}
			}
		}

		BluetoothDevice Device = mBluetoothAdapter
				.getRemoteDevice(BhMacID);
		mConnectedDeviceName = Device.getName();
		_bt = new BTClient(mBluetoothAdapter, BhMacID);
		_bt.addConnectedEventListener(bioSensor);

		if (_bt.IsConnected()) {
			_bt.start();
			Message msg = mHandler.obtainMessage(StressElicitationActivity.BT_CONNECTED);
			Bundle data = new Bundle();
			data.putString("device", mConnectedDeviceName);
			msg.setData(data);
			mHandler.sendMessage(msg);
		} else {
			Message msg = mHandler.obtainMessage(StressElicitationActivity.BT_NOTCONNECTED);
			mHandler.sendMessage(msg);
		}
	}
	
	public void disconnectBTSensor() {
		_bt.Close();
	}
}