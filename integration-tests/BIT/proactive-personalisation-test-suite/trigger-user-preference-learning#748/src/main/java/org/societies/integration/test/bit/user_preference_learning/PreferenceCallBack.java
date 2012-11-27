package org.societies.integration.test.bit.user_preference_learning;

import org.societies.api.identity.IIdentity;
//import org.societies.api.internal.personalisation.IPersonalisationCallback;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class PreferenceCallBack /*implements IPersonalisationCallback */{
	public int flag = 0;
	public Object lock = new Object();

	//@Override
	public void receiveIAction(IIdentity arg0, ServiceResourceIdentifier arg1,
			IAction arg2) {
		// TODO Auto-generated method stub
		synchronized (lock) {
			if (arg2 != null) {
				flag = 1;
			} else {
				flag = -1;
			}
			lock.notifyAll();
		}
	}

	public boolean get() {
		try {
			synchronized (lock) {
				while (flag == 0) {
					lock.wait();
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag == 1 ? true : false;
	}

}
