package org.societies.datasource;


import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOCIETIESDataSource extends BasicDataSource {
	private static Logger log = LoggerFactory.getLogger(SOCIETIESDataSource.class);
	public SOCIETIESDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public synchronized void close() throws SQLException {
		log.info("in close");
		DriverManager.getConnection("jdbc:derby:;shutdown=true");
		dataSource.getConnection().close();
		
		super.close();
	}
}
