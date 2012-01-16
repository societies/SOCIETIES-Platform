package org.societies.comm.common;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CommonLibraryActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
	
		System.out.println("Starting common library exporter bundle");
			
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Stopping common library exporter bundle");
		
	}

}
