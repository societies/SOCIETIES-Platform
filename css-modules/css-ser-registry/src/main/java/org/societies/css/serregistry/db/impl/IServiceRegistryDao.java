package org.societies.css.serregistry.db.impl;

import java.util.List;

import org.societies.css_serregistry.db.model.Service;

public interface IServiceRegistryDao {
	
	public abstract void create(List<Service> listServices);
	@SuppressWarnings("rawtypes")
	public abstract List findAll( );
}
