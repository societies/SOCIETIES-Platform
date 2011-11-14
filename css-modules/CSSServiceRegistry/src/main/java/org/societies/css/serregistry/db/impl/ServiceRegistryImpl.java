package org.societies.css.serregistry.db.impl;

import java.util.List;

import org.societies.css.serregistry.api.IServiceRegistry;
import org.societies.css_serregistry.db.model.Service;

public class ServiceRegistryImpl implements IServiceRegistry {

	private IServiceRegistryDao cssRegistryDao; 
	
	
	public IServiceRegistryDao getCssRegistryDao() {
		return cssRegistryDao;
	}

	public void setCssRegistryDao(IServiceRegistryDao cssRegistryDao) {
		this.cssRegistryDao = cssRegistryDao;
	}

	public void processServices(List<Service> service) {
		cssRegistryDao.create(service);
		@SuppressWarnings("unchecked")
		List<Service> list =getCssRegistryDao().findAll();
		System.out.println("The saved courses are --> " + list);
	}

}
