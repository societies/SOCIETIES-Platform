package dlr.stressrecognition.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BTBroadcastReceiver extends BroadcastReceiver {
	private BluetoothAdapter mBluetoothAdapter = null;	
	
	public BTBroadcastReceiver(BluetoothAdapter btAdapter) {
		super();
		mBluetoothAdapter = btAdapter;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("BTIntent", intent.getAction());
		Bundle b = intent.getExtras();
		Log.d("BTIntent", b.get("android.bluetooth.device.extra.DEVICE")
				.toString());
		Log.d("BTIntent",
				b.get("android.bluetooth.device.extra.PAIRING_VARIANT")
						.toString());
		try {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(b.get(
					"android.bluetooth.device.extra.DEVICE").toString());
			Method m = BluetoothDevice.class.getMethod("convertPinToBytes",
					new Class[] { String.class });
			byte[] pin = (byte[]) m.invoke(device, "1234");
			m = device.getClass().getMethod("setPin",
					new Class[] { pin.getClass() });
			Object result = m.invoke(device, pin);
			Log.d("BTTest", result.toString());
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
