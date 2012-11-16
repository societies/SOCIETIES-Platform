package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;

//import org.eclipse.gemini.web.tomcat.internal.loading.BundleWebappClassLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Long thisBundleId;
	private Map<Long, ClassLoader> classloaderMap;
	
	public ClassLoaderManager() {
		ClassLoader hcl = Thread.currentThread().getContextClassLoader();
		if (hcl instanceof BundleDelegatingClassLoader)
			thisBundleId = ((BundleDelegatingClassLoader)hcl).getBundle().getBundleId();
//		if (hcl instanceof BundleWebappClassLoader)
//			thisBundleId = ((BundleWebappClassLoader)hcl).getBundle().getBundleId();
		if (thisBundleId==null)
			notSupported(hcl);
		classloaderMap = new HashMap<Long, ClassLoader>();
	}
	
	public ClassLoader classLoaderMagic(Object o) {
		LOG.info("getting classloader for object "+o.toString());
		ClassLoader newClassloader = null;
		if (o instanceof Thread) {
			ClassLoader cl = ((Thread)o).getContextClassLoader();
			Long bid = null;
			if (cl instanceof BundleDelegatingClassLoader)
				bid = ((BundleDelegatingClassLoader) cl).getBundle().getBundleId();
//			if (cl instanceof BundleWebappClassLoader)
//				bid = ((BundleWebappClassLoader)cl).getBundle().getBundleId();
			
			LOG.info("resolved "+o.toString()+" to bundle "+bid);
			if (bid!=null)
				newClassloader = classloaderMap.get(bid);
			else
				notSupported(cl);
		}
		
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

	private void notSupported(ClassLoader cl) {
		throw new RuntimeException("Unsupported classloader detected: "+cl.getClass());
	}

	public void classloaderRegistry(ClassLoader contextClassLoader) {
		Long id = null;
		if (contextClassLoader instanceof BundleDelegatingClassLoader)
			id = ((BundleDelegatingClassLoader)contextClassLoader).getBundle().getBundleId();
//		if (contextClassLoader instanceof BundleWebappClassLoader)
//			id = ((BundleWebappClassLoader)contextClassLoader).getBundle().getBundleId();
		
		if (id==null)
			notSupported(contextClassLoader);
			
		if (!id.equals(thisBundleId)) {
			LOG.info("saving classloader "+contextClassLoader.toString()+" for bundle "+id);
			classloaderMap.put(id, contextClassLoader);
		}
		else
			LOG.info("this is the local classloader: skipping...");
	}
}
