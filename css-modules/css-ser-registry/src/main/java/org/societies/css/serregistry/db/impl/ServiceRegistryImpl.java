package org.societies.css.serregistry.db.impl;

import java.util.List;
import javax.annotation.PostConstruct;

import org.societies.css.serregistry.api.IServiceRegistry;
import org.societies.css_serregistry.db.model.DBServerMgmt;
import org.societies.css_serregistry.db.model.Service;

public class ServiceRegistryImpl implements IServiceRegistry {

	private ServiceRegistryDaoImpl cssRegistryDao;
	private DBServerMgmt dbServiceMgmt;
	
	public ServiceRegistryImpl(){
		System.out.println("Service Registry bean Created");
//		dbServiceMgmt=new DBServerMgmt();
//		cssRegistryDao=new ServiceRegistryDaoImpl(); 
	}
	
	

	public void processServices(List<Service> service) {
		cssRegistryDao.create(service);
		List<Service> list =cssRegistryDao.findAll();
		System.out.println("The saved courses are --> " + list);
	}

	public DBServerMgmt getDbServiceMgmt() {
		return dbServiceMgmt;
	}	
	
	@PostConstruct
	public void init(){
		this.getDbServiceMgmt().startServer();
		System.out.println("Starting Server..");
	}	

}
