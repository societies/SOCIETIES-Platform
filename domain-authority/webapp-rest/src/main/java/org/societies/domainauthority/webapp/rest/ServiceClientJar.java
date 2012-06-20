package org.societies.domainauthority.webapp.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for hosting jar files for clients of 3rd party services.
 */
@Path("/serviceclient")
public class ServiceClientJar {
    
	private static Logger LOG = LoggerFactory.getLogger(ServiceClientJar.class);

	/**
	 * URL parameter
	 */
	public static final String KEY = "key";
	
	/**
     * Method processing HTTP GET requests, producing "application/java-archive" MIME media type.
     * 
     * @return Service client in form of jar file
     */
	@Path("{name}.jar")
    @GET 
    @Produces("application/java-archive")
    public byte[] getIt(@PathParam("name") String name, @QueryParam(KEY) String key) {

		LOG.debug("HTTP GET: name = {}, key = {}", name, key);
		
		byte[] result = new byte[] {'a', 'h', 'o', 'j'};

		return result;
    }
}
