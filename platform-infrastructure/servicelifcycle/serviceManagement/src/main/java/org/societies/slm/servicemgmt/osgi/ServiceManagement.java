package org.societies.slm.servicemgmt.osgi;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
//import org.springframework.osgi.service.importer.OsgiServiceLifecycleListener;
import org.springframework.osgi.bundle.BundleFactoryBean;
import org.springframework.osgi.bundle.BundleAction;
import org.springframework.osgi.service.importer.support.OsgiServiceCollectionProxyFactoryBean;
import org.springframework.osgi.context.*;


public class ServiceManagement implements BundleContextAware  {

	private BundleContext bundleContext;
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceManagement.class);
	
	public ServiceManagement() {
		
		logger.info("starting ServiceManagement bean");
		System.out.println("starting ServiceManagement bean ****");
		
	}
	
	public void bind(Object arg0, @SuppressWarnings("rawtypes") Map arg1) throws Exception {
		// TODO Auto-generated method stub		
		BundleFactoryBean bndlfb;
	//	bndlfb.getBundle();
		BundleAction bndlac;
	//	bndlac.START
		OsgiServiceCollectionProxyFactoryBean bb;
		//bb.
		BundleContext bc;
	//	bc.get
		logger.info("bind method is called");
	}

	public void unbind(Object arg0, @SuppressWarnings("rawtypes") Map arg1) throws Exception {
		// TODO Auto-generated method stub	
		logger.info("unbind method is called");
	}

	public void setBundleContext(BundleContext bundleContext) {
		logger.info("Bundle Context is injected to ServiceManagement bean");
		System.out.println("Bundle Context is injected to ServiceManagement bean ****");
		this.bundleContext=bundleContext;		
	}

	public void afterPropertiesSet() throws Exception {
		logger.info("after properties are vset");
		logger.info("bundles to string"+ bundleContext.getBundles().toString());		
	}

	public BundleContext getBundleContext() {
		return bundleContext;
	}
}
