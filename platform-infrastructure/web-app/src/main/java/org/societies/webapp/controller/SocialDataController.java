package org.societies.webapp.controller;

/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.sns.Message;
import org.societies.webapp.models.ErrorModel;
import org.societies.webapp.models.SocialDataModel;
import org.springframework.stereotype.Controller;

@Controller
@ManagedBean(name="socialDataController")
@RequestScoped
public class SocialDataController extends BasePageController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value = "#{socialData}")
	private ISocialData socialData;
	
	private static final Logger logger = LoggerFactory.getLogger(SocialDataController.class);
	
	private SocialDataModel socialDataModel;
	
	private String messagePost;
	private List<String> selectedSocialToPost;
	private SelectItem [] selectSocialItems;
	

	public ISocialData getSocialData() {
		return this.socialData;
	}
	
	public void setSocialData(ISocialData socialData)
	{
		this.socialData = socialData;
	}
	
	/**
	 * @return the socialDataModel
	 */
	public SocialDataModel getSocialDataModel() {
		return socialDataModel;
	}

	/**
	 * @param socialDataModel the socialDataModel to set
	 */
	public void setSocialDataModel(SocialDataModel socialDataModel) {
		this.socialDataModel = socialDataModel;
	}
	
	/**
	 * @return the messagePost
	 */
	public String getMessagePost() {
		return messagePost;
	}

	/**
	 * @param messagePost the messagePost to set
	 */
	public void setMessagePost(String messagePost) {
		this.messagePost = messagePost;
	}

	/**
	 * @return the selectedSocialToPost
	 */
	public List<String> getSelectedSocialToPost() {
		return selectedSocialToPost;
	}

	/**
	 * @param selectedSocialToPost the selectedSocialToPost to set
	 */
	public void setSelectedSocialToPost(List<String> selectedSocialToPost) {
		this.selectedSocialToPost = selectedSocialToPost;
	}

	@PostConstruct
	public void service()
	{
		socialDataModel = new SocialDataModel(socialData);
		//is from social connect?
		handleSocialConnectReturn();
	}

	private SocialNetwork getSocialNetworkName(String name) {

		if ("facebook".equalsIgnoreCase(name))
			return SocialNetwork.FACEBOOK;
		if ("FB".equalsIgnoreCase(name))
			return SocialNetwork.FACEBOOK;
		if ("twitter".equalsIgnoreCase(name))
			return SocialNetwork.TWITTER;
		if ("TW".equalsIgnoreCase(name))
			return SocialNetwork.TWITTER;
		if ("foursquare".equalsIgnoreCase(name))
			return SocialNetwork.FOURSQUARE;
		if ("FQ".equalsIgnoreCase(name))
			return SocialNetwork.FOURSQUARE;
		if ("linkedin".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("LK".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("LN".equalsIgnoreCase(name))
			return SocialNetwork.LINKEDIN;
		if ("googleplus".equalsIgnoreCase(name))
			return SocialNetwork.GOOGLEPLUS;
		if ("G+".equalsIgnoreCase(name))
			return SocialNetwork.GOOGLEPLUS;

		return null;
	}
	
	public void handleSocialConnectReturn()
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String socialNetworkType 	=request.getParameter("type");
		String token 				=request.getParameter("token");
		
		logger.debug("type: "+socialNetworkType);
		logger.debug("token: "+token);
		
		if(socialNetworkType != null && token != null)
		{
			try {
	
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(ISocialConnector.AUTH_TOKEN, token);
	
				//ISocialConnector con = socialdata.createConnector(getSocialNetowkName(sdForm.getSnName()), params);
				ISocialConnector con = socialData.createConnector(getSocialNetworkName(socialNetworkType), params);
				//error = "We are not able to create " + con.getConnectorName() + " connector!";
	
				socialData.addSocialConnector(con);
				socialData.updateSocialData();
				//				content = "<b>Connector</b> ID:" + sdForm.getId() + " for "
				// 						+ sdForm.getSnName() + " with token: "
				//						+ sdForm.getToken() + "<br>";
				//				model.put("sdForm", sdForm);
			}
			catch (Exception ex) {
				logger.error(" Errore creazione token :", ex);
				ErrorModel error = new ErrorModel();
				error.setErrorMessage(ex.getMessage());
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));
			}
	
			//		socialdata.updateSocialData(); // this is required to read all the SN
			//		model.put("connectors", getConnectorsHTML());
			//		model.put("result_title", res);
			
			socialDataModel = new SocialDataModel(socialData);
		}

	}
	
	public void connectSN(String p)
	{
		String social = socialDataModel.getSocialNameShortFromIcon(p);
		ExternalContext ext = FacesContext.getCurrentInstance().getExternalContext();
		URI uri;
		try {
			uri = new URI(ext.getRequestScheme(),
			          null, ext.getRequestServerName(), ext.getRequestServerPort(),
			          ext.getRequestContextPath(), null, null);
		String path = uri.toASCIIString();
		logger.debug("PATH: "+path);
		String redirect = "http://societies.lucasimone.eu/connect.php?sn="+social+"&from="+ path + "/socialnetwork.xhtml";
		FacesContext.getCurrentInstance().getExternalContext().redirect(redirect);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			ErrorModel error = new ErrorModel();
			error.setErrorMessage(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));
		} 
	}
	
	public void disconnectSN(String connection_id)
	{
		try{
			logger.debug("removing social network: "+connection_id);
			socialData.removeSocialConnector(connection_id);
		} catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		service();
	}
	
	public void postMessage()
	{
		if(getSelectedSocialToPost().size() > 0 && messagePost != null)
		{
			for(String s : getSelectedSocialToPost())
			{
				SocialNetwork socialName = getSocialNetworkName(s);
				logger.debug("SocialNetworkName found: "+socialName);
				if(socialName != null)
				{
					Message message = new Message();
					message.setData(messagePost);
					logger.debug("Social Network: "+socialName.name()+" trying to post:\""+message.getData()+"\"");
					try
					{
						socialData.postMessage(socialName, message);
					}
					catch(Exception e)
					{
						logger.error(e.getMessage(),e);
						ErrorModel error = new ErrorModel();
						error.setErrorMessage(e.getMessage());
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error", error.getErrorMessage()));
					}
				}
			}
			messagePost = null;
			service();
		}
	}
		
	public SelectItem [] getSelectSocialItems()
	{
		if(selectSocialItems == null)
		{
			List<String> connectedSocial = socialDataModel.getConnectedSocial();
			selectSocialItems = new SelectItem [connectedSocial.size()];
			for(int i = 0; i < connectedSocial.size(); i++)
			{
				String social = connectedSocial.get(i);
				selectSocialItems[i] = new SelectItem(socialDataModel.getSocialNameShortFromLong(social), 
						socialDataModel.getSocialNameShortFromLong(social).toUpperCase());
				
			}
		}
		return selectSocialItems;
	}
}
