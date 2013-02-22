package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Map<IFeatureServer,ClassLoader> fsRegistry;
	private Map<ICommCallback,ClassLoader> callbackRegistry;
	private Map<String,ClassLoader> callbackTemporary;
	private Bundle thisBundle;
	
	public ClassLoaderManager() {
		thisBundle = FrameworkUtil.getBundle(this.getClass());
		fsRegistry = new HashMap<IFeatureServer, ClassLoader>();
		callbackRegistry = new HashMap<ICommCallback, ClassLoader>();
		callbackTemporary = new Hashtable<String, ClassLoader>(); // Hashtable because it is synchronized
	}

	public ClassLoader classLoaderMagic(ICommCallback callback) {
		LOG.info("getting classloader for ICommCallback "+callback.toString());
		ClassLoader newClassloader = null;
	
		newClassloader = callbackRegistry.get(callback);
		
		if (newClassloader!=null) {
			ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found!");
		
		return null;
	}

	public ClassLoader classLoaderMagic(IFeatureServer fs) {
		LOG.info("getting classloader for IFeatureServer "+fs.toString());
		ClassLoader newClassloader = null;
	
		newClassloader = fsRegistry.get(fs);
		
		if (newClassloader!=null) {
			ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found!");
		
		return null;
	}

	public ClassLoader classLoaderMagicTemp(String id) {
		LOG.info("getting classloader for request id "+id);
		ClassLoader newClassloader = null;
	
		newClassloader = callbackTemporary.get(id);
		
		if (newClassloader!=null) {
			ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			callbackTemporary.remove(id);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found!");
		
		return null;
	}

	public void addTempClassloader(String id) {
		// using context classloader that was used to send the IQ
		callbackTemporary.put(id, Thread.currentThread().getContextClassLoader());
	}

	public void classloaderRegistry(IFeatureServer fs) {
		Bundle b = FrameworkUtil.getBundle(fs.getClass());
		if (b.getBundleId()!=thisBundle.getBundleId())
			fsRegistry.put(fs, getBundleClassloader(b));
	}

	
	public void classloaderRegistry(ICommCallback messageCallback) {
		Bundle b = FrameworkUtil.getBundle(messageCallback.getClass());
		if (b.getBundleId()!=thisBundle.getBundleId())
			callbackRegistry.put(messageCallback, getBundleClassloader(b));
	}
	
	private ClassLoader getBundleClassloader(Bundle b) {
		// http://tomsondev.bestsolution.at/2011/10/15/find-classloader-for-a-given-osgi-bundle/
		BundleWiring wiring = (BundleWiring)b.adapt(BundleWiring.class);
		return wiring.getClassLoader();
	}

}
