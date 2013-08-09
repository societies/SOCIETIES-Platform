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
	
	private Map<Class<?>,ClassLoader> fsRegistry;
	private Map<Class<?>,ClassLoader> callbackRegistry;
	private Map<String,ClassLoader> callbackTemporary;
	private Bundle thisBundle;
	private ClassLoader thisBundlesClassLoader;
	
	public ClassLoaderManager() {
		thisBundle = FrameworkUtil.getBundle(this.getClass());
		//thisBundlesClassLoader = getBundleClassloader(thisBundle);
		thisBundlesClassLoader = this.getClass().getClassLoader();
		fsRegistry = new HashMap<Class<?>, ClassLoader>();
		callbackRegistry = new HashMap<Class<?>, ClassLoader>();
		callbackTemporary = new Hashtable<String, ClassLoader>(); // Hashtable because it is synchronized
	}

	public ClassLoader classLoaderMagic(ICommCallback callback) {
		LOG.debug("getting classloader for ICommCallback {}",callback.toString());
		ClassLoader newClassloader = callbackRegistry.get(callback.getClass());
		ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
		
		if (newClassloader!=null) {
			LOG.debug("found a classloader for this context! oldClassloader={} newClassloader={}",oldClassloader.toString(),newClassloader);
			Thread.currentThread().setContextClassLoader(newClassloader);
			return oldClassloader;
		}
		else {
			LOG.debug("no classloader found! contextClassLoader=",oldClassloader.toString());
		}
		
		return null;
	}

	public ClassLoader classLoaderMagic(IFeatureServer fs) {
		LOG.debug("getting classloader for IFeatureServer {}",fs.toString());
		ClassLoader newClassloader = fsRegistry.get(fs.getClass());
		ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
		
		if (newClassloader!=null) {
			LOG.debug("found a classloader for this context! oldClassloader={} newClassloader={}",oldClassloader.toString(),newClassloader);
			Thread.currentThread().setContextClassLoader(newClassloader);
			return oldClassloader;
		}
		else {
			LOG.debug("no classloader found! contextClassLoader={}",oldClassloader.toString());
		}
		
		return null;
	}
	
	 // CISCommFactory generated comms are fixed
//	private ClassLoader getOldClassloader() {
//		 ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
//		 
//		
//		 try {
//			 ctcl.loadClass(this.getClass().getCanonicalName());
//			 return ctcl;
//		 } catch (ClassNotFoundException e) {
//			 LOG.warn("Old ClassLoader is '"+ctcl.toString()+"' instead of '"+thisBundlesClassLoader.toString()+"'!!! Forcing...", e);
//			 Thread.currentThread().setContextClassLoader(thisBundlesClassLoader);
//			 return thisBundlesClassLoader;
//		 }
//	}

	public ClassLoader classLoaderMagicTemp(String id) {
		LOG.debug("getting classloader for request id {}",id);
		ClassLoader newClassloader = callbackTemporary.get(id);
		ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader(); // not old classloader verification required here
		
		if (newClassloader!=null) {
			LOG.debug("found a classloader for this context! oldClassloader={} newClassloader={}",oldClassloader.toString(),newClassloader);
			callbackTemporary.remove(id);
			Thread.currentThread().setContextClassLoader(newClassloader);
			return oldClassloader;
		}
		else {
			LOG.debug("no classloader found! contextClassLoader={}",oldClassloader.toString());
		}
		
		return null;
	}

	public void addTempClassloader(String id, Object payload) {
		// using context classloader that was used to send the IQ
		//callbackTemporary.put(id, Thread.currentThread().getContextClassLoader());
		// using the payload classloader that was used to send the IQ
		callbackTemporary.put(id, payload.getClass().getClassLoader());
	}

	public void classloaderRegistry(IFeatureServer fs) {
		Bundle b = FrameworkUtil.getBundle(fs.getClass());
		LOG.info("Class "+fs.getClass().toString()+" is associated with bundle "+b.toString());
		if (b.getBundleId()!=thisBundle.getBundleId())
			fsRegistry.put(fs.getClass(), getBundleClassloader(b));
	}

	
	public void classloaderRegistry(ICommCallback messageCallback) {
		Bundle b = FrameworkUtil.getBundle(messageCallback.getClass());
		LOG.info("Class "+messageCallback.getClass().toString()+" is associated with bundle "+b.toString());
		if (b.getBundleId()!=thisBundle.getBundleId())
			callbackRegistry.put(messageCallback.getClass(), getBundleClassloader(b));
	}
	
	private ClassLoader getBundleClassloader(Bundle b) {
		// http://tomsondev.bestsolution.at/2011/10/15/find-classloader-for-a-given-osgi-bundle/
		BundleWiring wiring = (BundleWiring)b.adapt(BundleWiring.class);
		return wiring.getClassLoader();
	}

}
