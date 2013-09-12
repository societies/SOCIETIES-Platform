/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.rfid.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eliza
 *
 */
public class RFIDConfig {
	private Hashtable<String, String> wUnitToSymloc = new Hashtable<String, String>();
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	private int readers;
	public RFIDConfig(){
		
	}
	
	private Properties getRFIDConfiguration(){
		
		try {
			Properties props = this.getDefaultProps();
			if (props!=null){
				try{
				readers = Integer.parseInt(props.getProperty("org.societies.css.devicemgmt.rfiddriver.readers").trim());
				}catch(NumberFormatException e){
					readers = 0;
				}
				
				for (int i = 0; i< readers; i++){
					String prop = "org.societies.css.devicemgmt.rfiddriver.reader."+i+".units";
					System.out.println("Processing reader: "+prop+"="+props.getProperty(prop));
					
					String[] units = props.getProperty(prop).split(",");
					for (int n=0; n<units.length; n++){
						String unitID = "org.societies.css.devicemgmt.rfiddriver.reader."+i+".unit."+units[n]+".symloc";
						System.out.println("Adding : "+unitID);
						this.wUnitToSymloc.put(units[n], props.getProperty(unitID));
					}
				}
			}
			return props;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Properties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Properties();
		}
		
	}
	
	
	/**
     * Returns the default properties from rfid.conf file
     * 
     * @throws IOException
     *             if the  properties file cannot be read.
     */
    private Properties getDefaultProps() throws IOException {
        if (this.logging.isDebugEnabled())
            this.logging.debug("Reading configuration from rfid.conf");
        final Properties defaultProps = new Properties();
        final InputStream is = this.getClass().getResourceAsStream("/rfid.conf");
        if (is != null) {
            defaultProps.load(is);
            is.close();
            return defaultProps;
        } else {
            throw new FileNotFoundException("rfid.conf: No such file in resources)");
        }
    }
	public static void main(String[] args) throws IOException{
		RFIDConfig config = new RFIDConfig();
		
		Properties props = config.getRFIDConfiguration();
		
		Enumeration<Object> keys = props.keys();
		System.out.println(props.size());
		while (keys.hasMoreElements()){
			String key = (String) keys.nextElement();
			System.out.println(key+" : "+props.getProperty(key));
		}
		
		Hashtable<String, String> table = config.getUnitToSymloc();
		
		Enumeration<String> units = table.keys();
		
		while (units.hasMoreElements()){
			String unit = units.nextElement();
			System.out.println(unit+" : "+table.get(unit));
		}
	}
	
	
	public Hashtable<String, String> getUnitToSymloc(){
		this.getRFIDConfiguration();
		return  this.wUnitToSymloc;
	}
	
}
