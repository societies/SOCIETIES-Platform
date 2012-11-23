package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Long thisBundleId;
	private Map<Long, ClassLoader> classloaderMap;
	private Map<String, Long> classToBundle;
	
	public ClassLoaderManager(Object commMgrBean) {
		classloaderMap = new HashMap<Long, ClassLoader>();
		classToBundle = new HashMap<String, Long>();
		thisBundleId = getBundleId(commMgrBean);
	}

	public ClassLoader classLoaderMagic(Object targetBean) {
		LOG.info("getting classloader for object "+targetBean.toString());
		ClassLoader newClassloader = null;
	
		Long id = getBundleId(targetBean);
		newClassloader = classloaderMap.get(id);
		
		if (newClassloader!=null) {
			ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
			LOG.info("found a classloader for this context! oldClassloader="+oldClassloader.toString()+" newClassloader="+newClassloader);
			Thread.currentThread().setContextClassLoader(newClassloader);
			return oldClassloader;
		}
		else
			LOG.info("no classloader found!");
		
		return null;
	}

	// when this is called the Thread is from the target bundle... not always!
	public void classloaderRegistry(Object targetBean) {
		Long id = getBundleId(targetBean);
		
		LOG.info("saving class "+targetBean.getClass().getCanonicalName()+" for bundle "+id);
		classToBundle.put(targetBean.getClass().getCanonicalName(), id);
		if (!id.equals(thisBundleId)) {
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			LOG.info("saving classloader "+ccl.toString()+" for bundle "+id);
			classloaderMap.put(id, ccl);
		}
		else
			LOG.info("this is the local classloader: skipping...");
	}
	
	public boolean classloaderRegistryVerify(Object targetBean) {
		if (!classToBundle.containsKey(targetBean.getClass().getCanonicalName())){
			classloaderRegistry(targetBean);
			return false;
		}
		return true;
	}
	
	private void notFound(Class<?> cs, ClassLoader cl) {
		throw new RuntimeException("Unable to get bundle for class: "+cs.toString()+" with context classloader "+cl.toString());
	}
	
	private Long getBundleId(Object o) {
		Bundle b = null;
		ClassLoader hcl = null;
		
		// priority to registered references (the request is incoming, thread belongs to CommsFwrk)
		Long l = classToBundle.get(o.getClass().getCanonicalName());
		if (l!=null) {
			LOG.info("resolved "+o.toString()+" via class registry to bundle id '"+l+"'");
			return l;
		}
		
		// call from a different bundle: get bundleId from BundleDelegatingClassLoader if possible
		if (o instanceof Thread)
			hcl = ((Thread)o).getContextClassLoader();
		else
			hcl = Thread.currentThread().getContextClassLoader();
		
		if (hcl instanceof BundleDelegatingClassLoader)
			b = ((BundleDelegatingClassLoader)hcl).getBundle();
		
		// fallback to frameworkUtils resolution
		if (b==null) {
			b = FrameworkUtil.getBundle(o.getClass());
			if (b!=null) {
				LOG.info("resolved "+o.toString()+" to bundle '"+b.toString()+"' with id '"+b.getBundleId()+"' via FrameworkUtil");
			}
			else
				notFound(o.getClass(),Thread.currentThread().getContextClassLoader());
		}
		else
			LOG.info("resolved "+o.toString()+" to bundle '"+b.toString()+"' with id '"+b.getBundleId()+"' via context Classloader");
		
		return b.getBundleId();
	}
}
