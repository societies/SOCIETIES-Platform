package org.societies.css_serregistry.db.model;

import org.hsqldb.Server;

public class DBServerMgmt {
	private static Server hsqlServer;
	public void startServer() {
		hsqlServer = null;
        hsqlServer = new Server();

        // HSQLDB prints out a lot of informations when
        // starting and closing, which we don't need now.
        // Normally you should point the setLogWriter
        // to some Writer object that could store the logs.
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);

        // The actual database will be named 'xdb' and its
        // settings and data will be stored in files
        // testdb.properties and testdb.script
        hsqlServer.setDatabaseName(0, "xdb");
        hsqlServer.setDatabasePath(0, "file:testdb");

        // Start the database!
        hsqlServer.start();
	}
	
	public void stopServer(){
        // Closing the server
        if (hsqlServer != null) {
            hsqlServer.stop();
        }
	}
}
