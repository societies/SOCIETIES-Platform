package org.societies.slm.servicemgmt.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceLocation;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.model.ServiceType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/bundle-context.xml");
		XMLServiceMetaDataToObjectConverter xmlConv = (XMLServiceMetaDataToObjectConverter) ctx.getBean("XMLtoServiceObjectConverter");
		//xmlConv.convertFromXMLToObject("serviceData.xml");
		/*System.out.println("Convert XML back to Object!");
		//from XML to object
		try {
			Service service = (Service)xmlConv.convertFromXMLToObject("Service123.xml");
			System.out.println("the strings are: "+service.getServiceDescription());
			System.out.println("the strings are: "+service.getServiceLocation());	
			System.out.println("fgfg"+service.getServiceIdentifier().getIdentifier());
		} catch (IOException e1) { 
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		//System.out.println(customer);
		//System.out.println("Done");
	
		Service ser=new Service();
		ser.setAuthorSignature("serviceid1111");
		ser.setCSSIDInstalled("cssinstalled");
		ser.setServiceDescription("service description");		
		ser.setServiceLocation(ServiceLocation.Local);
		ser.setServiceName("serviceName");
		ser.setServiceType(ServiceType.ThirdPartyService);
		ser.setVersion("1a.1a.111.1");
		try {
			ServiceResourceIdentifier sid = new ServiceResourceIdentifier(new URI("file:///foo/bar"));
			ser.setServiceIdentifier(sid);
			try {
				xmlConv.convertFromObjectToXML(ser, "Service123.xml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		
//			Customer customer = (Customer)xmlConv.convertFromXMLFileToObject(file);
//			System.out.println(customer);
		System.out.println("Done");
	}

}
