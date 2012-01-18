package org.societies.asyn.example.method;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
/**
 * This class need to implement asynch methods to handle your asynchronous call results
 * 
 * @author pkuppuud
 *
 */
public class MyAsynReturnHandler {

	/**
	 * this method should be void and asynchronous 
	 * @param retObj Future object passed for processing when arrived. 
	 */
	@Async
	public void processMyReturnValue(Future<String> retObj){
		try {
			System.out.println("Handler received Message :" + retObj.get());
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
}
