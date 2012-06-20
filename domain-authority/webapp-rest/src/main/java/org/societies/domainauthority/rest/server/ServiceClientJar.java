package org.societies.domainauthority.rest.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.domainauthority.rest.control.ServiceClientJarAccess;

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
	
	public ServiceClientJar() {
		LOG.info("Constructor");
		new ServiceClientJarAccess();  // TODO: remove
	}
	
	/**
     * Method processing HTTP GET requests, producing "application/java-archive" MIME media type.
     * 
     * @return Service client in form of jar file.
     * Error 401 if file or key not valid.
     * Error 500 on server error.
     */
	@Path("{name}.jar")
    @GET 
    @Produces("application/java-archive")
    public byte[] getIt(@PathParam("name") String name, @QueryParam(KEY) String key) {

		LOG.debug("HTTP GET: name = {}, key = {}", name, key);
		
		String path = name + ".jar";
		byte[] file;
		
		if (!ServiceClientJarAccess.isKeyValid(path, key)) {
			LOG.warn("Invalid filename or key");
			// Return HTTP code 401 - Unauthorized
			throw new WebApplicationException(401);
		}
		
		try {
			file = getBytesFromFile(path);
		} catch (IOException e) {
			LOG.warn("Could not open file {}", path, e);
			// Return HTTP code 500 - Internal Server Error
			throw new WebApplicationException(500);
		}
		
		LOG.info("Serving {}", path);
		return file;
    }
	
	private byte[] getBytesFromFile(String path) throws IOException {
		
		File file = new File(path);
		InputStream is = new FileInputStream(file);
		
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
        	throw new IOException("File is too large " + file.getName());
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead = is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
	}
}
