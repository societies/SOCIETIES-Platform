package org.societies.api.internal.servicelifecycle.serviceMgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.societies.api.internal.servicelifecycle.serviceMgmt.IServiceManagement;
import org.societies.api.internal.servicelifecycle.serviceMgmt.ServiceMgmtException;

public class ServiceMgmtMetadataBroker {

	private List<String> serviceMetafileLocation;
	private IServiceManagement serviceMgmt;

	public ServiceMgmtMetadataBroker() {

	}

	public List<String> getServiceMetafileLocation() {
		return serviceMetafileLocation;
	}

	public void setServiceMetafileLocation(List<String> serviceMetafileLocation) {
		this.serviceMetafileLocation = serviceMetafileLocation;
	}

	public IServiceManagement getServiceMgmt() {
		return serviceMgmt;
	}

	public void setServiceMgmt(IServiceManagement serviceMgmt) {
		this.serviceMgmt = serviceMgmt;
	}

	public void registerData() {
		List<File> fileList = new ArrayList<File>();
		System.out.println("Reading service meta data files...");
		System.out.println("Quantity of meta file urls : "+ getServiceMetafileLocation().size());
		try {
			Iterator<String> iterator = getServiceMetafileLocation().iterator();
			while (iterator.hasNext()) {
				fileList.add(new File(iterator.next()));
			}
			
			System.out.println("Quantity of meta file to be processed : "+ getServiceMetafileLocation().size());
			if (fileList.size() > 0 ) {
				// TODO: Make this call Asychronous
				getServiceMgmt().processServiceMetaData(fileList);				
			}
		} catch (ServiceMgmtException e) {
			System.out.println("error from registerData...");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
