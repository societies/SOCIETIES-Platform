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
	private ClassLoader thisBundlesClassLoader;
	
	public ClassLoaderManager() {
		thisBundle = FrameworkUtil.getBundle(this.getClass());
		//thisBundlesClassLoader = getBundleClassloader(thisBundle);
		thisBundlesClassLoader = this.getClass().getClassLoader();
		fsRegistry = new HashMap<IFeatureServer, ClassLoader>();
		callbackRegistry = new HashMap<ICommCallback, ClassLoader>();
		callbackTemporary = new Hashtable<String, ClassLoader>(); // Hashtable because it is synchronized
	}

	public ClassLoader classLoaderMagic(ICommCallback callback) {
		LOG.info("getting classloader for ICommCallback "+callback.toString());
		ClassLoader newClassloader = callbackRegistry.get(callback);
		ClassLoader oldClassloader = getOldClassloader();
		
		if (newClassloader!=null) {
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found! contextClassLoader="+oldClassloader.toString());
		
		return null;
	}

	public ClassLoader classLoaderMagic(IFeatureServer fs) {
		LOG.info("getting classloader for IFeatureServer "+fs.toString());
		ClassLoader newClassloader = fsRegistry.get(fs);
		ClassLoader oldClassloader = getOldClassloader();
		
		if (newClassloader!=null) {
			
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found! contextClassLoader="+oldClassloader.toString());
		
		return null;
	}
	
	private ClassLoader getOldClassloader() {
		 ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
		 
		 try {
			 ctcl.loadClass(this.getClass().getCanonicalName());
			 return ctcl;
		 } catch (ClassNotFoundException e) {
			 LOG.warn("Old ClassLoader is '"+ctcl.toString()+"' instead of '"+thisBundlesClassLoader.toString()+"'!!! Forcing...", e);
			 Thread.currentThread().setContextClassLoader(thisBundlesClassLoader);
			 return thisBundlesClassLoader;
		 }
		 
//		 if (recursiveEquals(ctcl,thisBundlesClassLoader))
//			 return ctcl;		 
		 
	}

	private boolean recursiveEquals(ClassLoader ctcl, ClassLoader rootCl) {
		LOG.info("comparing '"+ctcl+"' with '"+rootCl+"'");
		if (ctcl.equals(rootCl))
			return true;
		if (ctcl.getParent()==null)
			return false;
		return recursiveEquals(ctcl.getParent(), rootCl);
	}

	public ClassLoader classLoaderMagicTemp(String id) {
		LOG.info("getting classloader for request id "+id);
		ClassLoader newClassloader = callbackTemporary.get(id);
		ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader(); // not old classloader verification required here
		
		if (newClassloader!=null) {
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			callbackTemporary.remove(id);
			// TODO DISABLED FOR NOW!
			//Thread.currentThread().setContextClassLoader(newClassloader);
			//return oldClassloader;
			LOG.info("ClassLoaderManager IS DISABLED!!!");
		}
		else
			LOG.info("no classloader found! contextClassLoader="+oldClassloader.toString());
		
		return null;
	}

	public void addTempClassloader(String id) {
		// using context classloader that was used to send the IQ
		callbackTemporary.put(id, Thread.currentThread().getContextClassLoader());
	}

	public void classloaderRegistry(IFeatureServer fs) {
		Bundle b = FrameworkUtil.getBundle(fs.getClass());
		LOG.info("Class "+fs.getClass().toString()+" is associated with bundle "+b.toString());
		if (b.getBundleId()!=thisBundle.getBundleId())
			fsRegistry.put(fs, getBundleClassloader(b));
	}

	
	public void classloaderRegistry(ICommCallback messageCallback) {
		Bundle b = FrameworkUtil.getBundle(messageCallback.getClass());
		LOG.info("Class "+messageCallback.getClass().toString()+" is associated with bundle "+b.toString());
		if (b.getBundleId()!=thisBundle.getBundleId())
			callbackRegistry.put(messageCallback, getBundleClassloader(b));
	}
	
	private ClassLoader getBundleClassloader(Bundle b) {
		// http://tomsondev.bestsolution.at/2011/10/15/find-classloader-for-a-given-osgi-bundle/
		BundleWiring wiring = (BundleWiring)b.adapt(BundleWiring.class);
		return wiring.getClassLoader();
	}

}
