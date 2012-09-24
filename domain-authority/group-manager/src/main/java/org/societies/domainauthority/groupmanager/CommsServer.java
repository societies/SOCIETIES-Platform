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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.domainauthority.groupmanager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.css.directory.CssDirectoryBean;
import org.societies.api.schema.css.directory.CssDirectoryBeanResult;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.cis.directory.CisDirectoryBean;
import org.societies.api.schema.cis.directory.CisDirectoryBeanResult;




public class CommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/css/directory",
					  "http://societies.org/api/schema/cis/directory"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.css.directory",
					  "org.societies.api.schema.cis.directory"));
	
	
	//PRIVATE VARIABLES
	private ICommManager commManager;
	private ICssDirectory cssDirectory;
	private ICisDirectory cisDirectory;
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	public ICssDirectory getCssDirectory() {
		return cssDirectory;
	}

	public void setCssDirectory(ICssDirectory cssDirectory) {
		this.cssDirectory = cssDirectory;
	}
	
	public ICisDirectory getCisDirectory() {
		return cisDirectory;
	}

	public void setCisDirectory(ICisDirectory cisDirectory) {
		this.cisDirectory = cisDirectory;
	}
	

	
	
	//METHODS
	public CommsServer() {
	}
	
	public void InitService() {
		//Registry Css Directory with the Comms Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		if(LOG.isDebugEnabled()) LOG.debug("getQuery: Received a message!");
		
		if (payload.getClass().equals(CssDirectoryBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Css Directory");
			
			CssDirectoryBean messageBean = (CssDirectoryBean) payload;
		
			try
			{
				switch (messageBean.getMethod()) {
					case ADD_CSS_ADVERTISEMENT_RECORD :
						this.getCssDirectory().addCssAdvertisementRecord(messageBean.getCssA());
						break;
					case DELETE_CSS_ADVERTISEMENT_RECORD :
						this.getCssDirectory().deleteCssAdvertisementRecord(messageBean.getCssA());
						break;
					case UPDATE_CSS_ADVERTISEMENT_RECORD :
						this.getCssDirectory().updateCssAdvertisementRecord(messageBean.getCssA(), messageBean.getCssB());
						break;
				};
			} catch (Exception e) {
					e.printStackTrace();
			};
		}
			if (payload.getClass().equals(CisDirectoryBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Css Directory");
			
			CisDirectoryBean messageBean = (CisDirectoryBean) payload;
		
			try
			{
				switch (messageBean.getMethod()) {
					case ADD_CIS_ADVERTISEMENT_RECORD :
						this.getCisDirectory().addCisAdvertisementRecord(messageBean.getCisA());
						break;
					case DELETE_CIS_ADVERTISEMENT_RECORD :
						this.getCisDirectory().deleteCisAdvertisementRecord(messageBean.getCisA());
						break;
					case UPDATE_CIS_ADVERTISEMENT_RECORD :
						this.getCisDirectory().updateCisAdvertisementRecord(messageBean.getCisA(), messageBean.getCisB());
						break;
				};
			} catch (Exception e) {
					e.printStackTrace();
			};
		}
			
	}

	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		if(LOG.isDebugEnabled()) LOG.debug("getQuery: Received a message!");
		
		if (payload.getClass().equals(CssDirectoryBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Discovery");
			
			CssDirectoryBean messageBean = (CssDirectoryBean) payload;
			CssDirectoryBeanResult resultBean = new CssDirectoryBeanResult(); 
			
			Future<List<CssAdvertisementRecord>> returnList = null;
			List<CssAdvertisementRecord> resultBeanList = resultBean.getResultCss();
			List<CssAdvertisementRecord> resultList = null;
			
			try
			{
				switch (messageBean.getMethod()) {
					case FIND_ALL_CSS_ADVERTISEMENT_RECORDS :
					{
						returnList = this.getCssDirectory().findAllCssAdvertisementRecords();
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
					}
						break;
					case FIND_FOR_ALL_CSS :
					{
						returnList = this.getCssDirectory().findForAllCss(messageBean.getCssA());
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						
						break;
					} 
					case SEARCH_BY_ID :
					{
						resultList = null;
						returnList = null;
						
						returnList = this.getCssDirectory().searchByID(messageBean.getCssIdList());
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						
						break;
					} 
				}
			} catch (Exception e) {
					e.printStackTrace();
			};
				
		
			return resultBean;
			
		}
		
		if (payload.getClass().equals(CisDirectoryBean.class)) {
			
			if(LOG.isDebugEnabled()) LOG.debug("Remote call to Service Discovery");
			
			CisDirectoryBean messageBean = (CisDirectoryBean) payload;
			CisDirectoryBeanResult resultBean = new CisDirectoryBeanResult(); 
			
			Future<List<CisAdvertisementRecord>> returnList = null;
			List<CisAdvertisementRecord> resultBeanList = resultBean.getResultCis();
			List<CisAdvertisementRecord> resultList = null;
			
			try
			{
				switch (messageBean.getMethod()) {
					case FIND_ALL_CIS_ADVERTISEMENT_RECORDS :
					{
						returnList = this.getCisDirectory().findAllCisAdvertisementRecords();
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
					}
						break;
					case FIND_FOR_ALL_CIS :
					{
						returnList = this.getCisDirectory().findForAllCis(messageBean.getCisA(), null);
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						
						break;
					}   
					case SEARCH_BY_ID :
					{
						returnList = this.getCisDirectory().searchByID(messageBean.getFilter());
						resultList =  returnList.get();
						
						if (resultList != null)
						{
							for (int i = 0; i < resultList.size(); i++)
							{
								resultBeanList.add(resultList.get(i));
							}
						}
						break;
					}
				}
			} catch (Exception e) {
					e.printStackTrace();
			};
				
		
			return resultBean;
			
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
