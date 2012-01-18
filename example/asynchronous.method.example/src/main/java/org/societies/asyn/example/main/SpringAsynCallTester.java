package org.societies.asyn.example.main;

import java.util.concurrent.Future;

import org.societies.asyn.example.method.MathemticService;
import org.societies.asyn.example.method.MyAsynReturnHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAsynCallTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/bundle-context.xml");
		
		MathemticService mathService=(MathemticService) ctx.getBean("mathService");
		
		MyAsynReturnHandler myHandler=(MyAsynReturnHandler) ctx.getBean("myHandler");
		
		// calling a asynchronous method which return some value
		Future<String> retObj = mathService.getEchoStringAsync("Hello Asyn Method");
		
		System.out.println("I am doing other stuffs while waiting for asynch reply");
		
		//calling asynchronous message handler and passing future object for processing
		myHandler.processMyReturnValue(retObj);
		
		//this method is completed now
		System.out.println("I am doing final clean");
	}
	
	
}
