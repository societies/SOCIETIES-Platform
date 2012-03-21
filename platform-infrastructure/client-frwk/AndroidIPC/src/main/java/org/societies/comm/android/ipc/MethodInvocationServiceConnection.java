/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.comm.android.ipc;

import org.societies.comm.android.ipc.Stub;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

public class MethodInvocationServiceConnection<T> implements ServiceConnection {
	
	private Object rv;
	private Object mutex = new Object();
	private Intent intent;
	private Context context;
	private int flags;
	private Class<?> clazz;
	private T remote;
	private int binds = 0;
	
	public MethodInvocationServiceConnection(Intent intent, Context context, int flags, Class<?> clazz) {
		this.intent = intent;
		this.context = context;
		this.flags = flags;
		this.clazz = clazz;
	}
	
	public Object invoke(IMethodInvocation<T> methodInvocation) throws Throwable {
		bind();
		rv = methodInvocation.invoke(remote);
		unbind();
		return rv;
	}
	
	public Object invokeAndKeepBound(IMethodInvocation<T> methodInvocation) throws Throwable {		
		bind();
		return methodInvocation.invoke(remote);		
	}
	
	public void unbind() {
		if(binds > 0) {
			binds--;
			if(binds == 0 && remote != null) {
				context.unbindService(this);
				remote = null;
			}
		}
	}
	
	private void bind() throws InterruptedException {
		if(remote == null) {
			synchronized(mutex) {		
				context.bindService(intent, this, flags);				
				mutex.wait();
			}
		}
		
		binds++;
	}
	
	public void onServiceConnected(ComponentName cn, IBinder binder) {
		remote = (T)Stub.newInstance(new Class<?>[]{clazz}, new Messenger(binder));			
		synchronized(mutex) {
			mutex.notify();
		}
		
	}
	public void onServiceDisconnected(ComponentName cn) {
		remote = null;
	}	
}
