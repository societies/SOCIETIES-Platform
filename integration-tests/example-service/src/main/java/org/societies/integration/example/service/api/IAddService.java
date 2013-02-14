package org.societies.integration.example.service.api;

import java.util.concurrent.Future;


/**
 * 
 * @author pkuppuud
 * 
 */
public interface IAddService {
	
	public Future<Integer> addNumbers(int a, int b);


}
