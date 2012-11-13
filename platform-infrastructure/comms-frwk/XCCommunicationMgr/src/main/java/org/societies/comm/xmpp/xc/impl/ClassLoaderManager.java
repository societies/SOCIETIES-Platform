package org.societies.comm.xmpp.xc.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.osgi.util.BundleDelegatingClassLoader;

public class ClassLoaderManager {
	
	private static Logger LOG = LoggerFactory
			.getLogger(ClassLoaderManager.class);
	
	private Long thisBundleId;
	private Map<Long, ClassLoader> classloaderMap;
	
	public ClassLoaderManager() {
		thisBundleId = ((BundleDelegatingClassLoader)Thread.currentThread().getContextClassLoader()).getBundle().getBundleId();
		classloaderMap = new HashMap<Long, ClassLoader>();
	}
	
	public ClassLoader classLoaderMagic(Object o) {
		LOG.info("getting classloader for object "+o.toString());
		ClassLoader newClassloader = null;
		if (o instanceof Thread) {
			ClassLoader cl = ((Thread)o).getContextClassLoader();
			if (cl instanceof BundleDelegatingClassLoader) {
				Long bid = ((BundleDelegatingClassLoader) cl).getBundle().getBundleId();
				LOG.info("resolved "+o.toString()+" to bundle "+bid);
				newClassloader = classloaderMap.get(bid);
			}
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

	public void classloaderRegistry(ClassLoader contextClassLoader) {
		if (contextClassLoader instanceof BundleDelegatingClassLoader) {
			BundleDelegatingClassLoader bdcl = (BundleDelegatingClassLoader) contextClassLoader;
			Long id = bdcl.getBundle().getBundleId();
			if (!id.equals(thisBundleId)) {
				LOG.info("saving classloader "+contextClassLoader.toString()+" for bundle "+id);
				classloaderMap.put(id, contextClassLoader);
			}
			else
				LOG.info("this is the local classloader");
		}
		else
			LOG.info("skipping classloader "+contextClassLoader.toString());
	}
}
