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
package org.societies.display.server.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class ScreenConfiguration {

    private static Logger logging = LoggerFactory.getLogger(ScreenConfiguration.class);

    private List<Screen> screens ;

	public ScreenConfiguration(){
		screens = new ArrayList<Screen>();
        //READ SCREESN FROM FILE
       // getScreensFromFile();//GET SCREENS FROM FILE
	}

    /**
     * Method to get screens from file and add them to the list of screens
     */
    public void getScreensFromFile() {

        //URL of file
        URL fileURL = ScreenConfiguration.class.getResource("/config/screenConfig.txt");

        BufferedReader br = null;
        String[] screenData;
        Screen newScreen;

        String configLine = null;
        try {
               br = new BufferedReader( new FileReader(fileURL.getFile()));
            while((configLine = br.readLine())!= null)
            {
                screenData = configLine.split(",");
                newScreen= new Screen(screenData[0],screenData[1],screenData[2]);
                addScreen(newScreen);
              //  this.logging.debug("New Screen Added: "+ screenData[0]); CURRENTLY LOGS @ SCREEN CONFIG
            }

        }catch(Exception e)
        {
            if(logging.isDebugEnabled()) logging.debug("ScreenConfiguration Error - Unable to read file");
            if(logging.isDebugEnabled()) logging.debug(e.toString());
        }

    }

    public List<Screen> getAllScreens(){
        return this.screens;
    }

    public void removeAllScreens()
    {
        this.screens.clear();
    }
	
	public void addScreen(Screen screen){
        if(logging.isDebugEnabled()) logging.debug("Screen added: " + screen.getScreenId());
		this.screens.add(screen);
	}
	public Screen getScreenBasedOnLocation(String location){
		for (Screen screen: screens){
			if (screen.getLocation().equalsIgnoreCase(location.trim())){
				return screen;
			}
		}
		
		return null;
	}
	
	public Screen getScreenBasedOnScreenId(String screenId){
		for (Screen screen: screens){
			if (screen.getScreenId().equalsIgnoreCase(screenId)){
				return screen;
			}
		}
		
		return null;	
	}
	
	public Screen getScreenBasedOnIPAddress(String ipAddress){
		for (Screen screen: screens){
			if (screen.getIpAddress().equalsIgnoreCase(ipAddress)){
				return screen;
			}
		}
		
		return null;			
	}


	
	public String[] getLocations(){
		String[] locations = new String[screens.size()];
		
		for (int i=0; i<screens.size(); i++){
			locations[i] = screens.get(i).getLocation();
		}
		
		return locations;
	}
	
	public boolean isEmpty(){
		return this.screens.size()==0;
	}
	
	public void clearScreens(){
		this.screens = new ArrayList<Screen>();
	}
	
	@Override
	public String toString(){
		String str = "Screen Configuration\n";
		
		int i=1;
		for (Screen screen : screens){
			str += "Screen "+i+": "+screen.toString()+"\n";
			i++;
		}
		
		return str;
	}

}
