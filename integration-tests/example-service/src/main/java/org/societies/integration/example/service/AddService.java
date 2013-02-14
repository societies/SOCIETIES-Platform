package org.societies.integration.example.service;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.integration.example.service.api.IAddService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * 
 * @author pkuppuud
 * 
 */
public class AddService implements IAddService {

	private static Logger LOG = LoggerFactory.getLogger(AddService.class);
	
	public AddService() {
		LOG.info("Example Service Constructor!");
	}
	
	@Override
	@Async
	public Future<Integer> addNumbers(int a, int b) {		
		LOG.info("a = " + a + "; b = " +b);
		Integer retVal = a + b;
		return new AsyncResult<Integer>(retVal);
	}

}
