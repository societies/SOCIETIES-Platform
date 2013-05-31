package org.societies.security.policynegotiator.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.security.digsig.DigsigException;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class DOMHelper {
	
	private static Logger LOG = LoggerFactory.getLogger(DOMHelper.class);
	
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder docBuilder;
		
	private static DOMImplementationRegistry domRegistry;
    private static DOMImplementationLS domImpl;

    private static LSSerializer serializer;
    private static DOMConfiguration domConfig;

     	
	static {		
    	dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		try {
			docBuilder = dbf.newDocumentBuilder();
			
			domRegistry = DOMImplementationRegistry.newInstance();
			domImpl = (DOMImplementationLS) domRegistry.getDOMImplementation("LS");
			serializer = domImpl.createLSSerializer();
			domConfig = serializer.getDomConfig();
			
			domConfig.setParameter("comments", new Boolean(true));			
		} catch (Exception e) {			
			LOG.error("Initialization failed", e);
		}
	}
	
	public static Document parseDocument(InputStream is) throws DigsigException {
		
		Document doc = null;
		
		try {
			docBuilder = dbf.newDocumentBuilder();
			doc = docBuilder.parse(is);
		} catch (Exception e) {			
			throw new DigsigException(e);
		}

		return doc;
	}
	
	public static void outputDocument(Document doc, OutputStream os) {
		 LSOutput domOutput = domImpl.createLSOutput();
         domOutput.setByteStream(os);
         domOutput.setEncoding("UTF-8");
         
         serializer.write(doc, domOutput); 
	}
	

	/**
	 * Transform XML from byte[] to {@link Document}
	 * 
	 * @param xml
	 *            The XML in form of byte array
	 * @return XML {@link Document} or null on error
	 */
	public static Document byteArray2doc(byte[] xml) {

		Document doc = null;

		try {
			doc = docBuilder.parse(new ByteArrayInputStream(xml));
		} catch (SAXException e) {
			LOG.warn("byteArray2doc(" + xml + ")", e);
		} catch (IOException e) {
			LOG.warn("byteArray2doc(" + xml + ")", e);
		}

		return doc;
	}

	/**
	 * Transform XML from {@link Document} to byte[]
	 * 
	 * @param doc The XML in form of {@link Document}
	 * @return XML byte array or null on error
	 */
	public static byte[] doc2byteArray(Document doc) {

		LSOutput domOutput = domImpl.createLSOutput();
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		domOutput.setByteStream(output);
		domOutput.setEncoding("UTF-8");
		serializer.write(doc, domOutput);

		return output.toByteArray();
	}

}
