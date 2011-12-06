package org.societies.clientframework.contentprovider.services;


interface IConsumer {
	
    boolean store(String key, String value);
    
    String getValue(String key);
    
    boolean removeKey(String key);
    
    String[] getKeys();
    
    void resetDB();
}
