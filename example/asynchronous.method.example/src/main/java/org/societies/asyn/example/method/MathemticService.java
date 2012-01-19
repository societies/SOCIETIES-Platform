package org.societies.asyn.example.method;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class MathemticService {
	
		
	@Async
	public Future<String> getEchoStringAsync(final String someString) {
	   
		String retString ="returning your string in asynchronous way : "+ someString;
		try {
			Thread.sleep(5000); // simulate delay
		} catch (InterruptedException e) { 
			e.printStackTrace();
		}
		System.out.println("Returning asynch result from MathematicService");
		return new AsyncResult<String>(retString);
	}
}
