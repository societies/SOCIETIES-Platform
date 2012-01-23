package org.societies.comm.examples.clientgui;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/bundle-context.xml");
		ExampleClientApp clApp =(ExampleClientApp) ctx.getBean("guiApp");		
	}

}
