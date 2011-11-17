package org.societies.css.serregistry.api;

import java.util.List;

import org.societies.css_serregistry.db.model.Service;

public interface IServiceRegistry {
	
	public abstract void processServices(List<Service> service);
}
