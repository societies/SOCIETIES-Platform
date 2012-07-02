package org.societies.security.digsig.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {
	public static void copyStream(InputStream is,OutputStream os) {		
		try {
			byte buf[] = new byte[8192];
			int rd=-1;
			
			while ((rd = is.read(buf))!=-1) 
				os.write(buf,0,rd);			
		} catch (Exception e) {
			throw new RuntimeException("Stream copy failed",e);
		} finally {
			closeStream(os);
			closeStream(is);			
		}
	}
	
	public static void closeStream(InputStream is) {
		if (is==null) return;
		try { is.close(); } catch (IOException e) {} 
	}
	
	public static void closeStream(OutputStream os) { 
		if (os==null) return;
		try { os.close(); } catch (IOException e) {} 
	}
}
