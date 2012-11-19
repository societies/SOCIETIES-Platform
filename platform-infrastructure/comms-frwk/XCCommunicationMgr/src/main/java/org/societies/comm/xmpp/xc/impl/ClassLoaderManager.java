package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
//import org.eclipse.gemini.web.tomcat.internal.loading.BundleWebappClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Long thisBundleId;
	private Map<Long, ClassLoader> classloaderMap;
	
	public ClassLoaderManager(Object commMgrBean) {
		Bundle b = FrameworkUtil.getBundle(commMgrBean.getClass());
		
		if (b!=null) {
			thisBundleId = b.getBundleId();
		}
		else
			notFound(commMgrBean.getClass());
		
		classloaderMap = new HashMap<Long, ClassLoader>();
	}
	
	public ClassLoader classLoaderMagic(Object targetBean) {
		LOG.info("getting classloader for object "+targetBean.toString());
		ClassLoader newClassloader = null;
	
		Bundle b = FrameworkUtil.getBundle(targetBean.getClass());
		
		if (b!=null) {
			LOG.info("resolved "+targetBean.toString()+" to bundle "+b.getBundleId());
			newClassloader = classloaderMap.get(b.getBundleId());
		}
		else
			notFound(targetBean.getClass());
		
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

	public void classloaderRegistry(Object targetBean) {
		Bundle b = FrameworkUtil.getBundle(targetBean.getClass());
		
		if (b==null)
			notFound(targetBean.getClass());
		
		Long id = b.getBundleId();
			
		if (!id.equals(thisBundleId)) {
			LOG.info("saving classloader "+Thread.currentThread().getContextClassLoader().toString()+" for bundle "+id);
			classloaderMap.put(id, Thread.currentThread().getContextClassLoader());
		}
		else
			LOG.info("this is the local classloader: skipping...");
	}
	
	private void notFound(Class<?> cl) {
		throw new RuntimeException("Unable to get bundle for class: "+cl.toString());
	}
}
