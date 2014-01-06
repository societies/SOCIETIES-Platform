package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.springframework.stereotype.Controller;

@Controller
@ManagedBean(name = "cisPrefManager")
@ViewScoped
public class CisPreferenceManager {
	
	@ManagedProperty(value = "#{cisManager}")
	private ICisManager cisManager;
	
	@ManagedProperty(value = "#{communityPreferenceManager}")
	private ICommunityPreferenceManager cisPrefManager;
	
	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commManager;
	
	private List<String> cisIDList;
	
	private static Logger log = LoggerFactory.getLogger(CisPreferenceManager.class);
	
	public CisPreferenceManager() {
		this.cisIDList = new ArrayList<String>();
		
	}
	
	@PostConstruct
	public void initCisPrefMgr() {
		log.debug("Init()");
		//GET ALL CIS'S I AM A MEMBER OF
		
		//FIRST LETS GET OWNED CIS
		List<ICisOwned> ownedCIS = this.cisManager.getListOfOwnedCis();
		//GET REMOTE CIS
		List<ICis> remoteCIS = this.cisManager.getRemoteCis();
		//ADD THERE ID'S
		for(ICisOwned ownCIS : ownedCIS) {
			cisIDList.add(ownCIS.getCisId());
			log.debug("Adding (OWNED) ID " + ownCIS.getCisId() + " to the list");
		}
		for(ICis cis : remoteCIS) {
			cisIDList.add(cis.getCisId());
			log.debug("Adding (REMOTE) ID " + cis.getCisId() + " to the list");
		}
		
		
	}
	
	public List<String> getAllCISPref() {
		log.debug("getAllCISPref()");
		for(String cisID : this.cisIDList) {
			try {
				log.debug("Getting preferences for CIS : " + cisID);
				List<IPreferenceTreeModel> cisPrefList = this.cisPrefManager.getAllCommunityPreferences(this.commManager.getIdManager().fromJid(cisID));
				log.debug("Got : " + cisPrefList.size() + " preferences.");
			} catch (InvalidFormatException e) {
				log.debug("Theres an error!");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return new ArrayList<String>();
	}

	public ICisManager getCisManager() {
		return cisManager;
	}

	public void setCisManager(ICisManager cisManager) {
		this.cisManager = cisManager;
	}

	public ICommunityPreferenceManager getCisPrefManager() {
		return cisPrefManager;
	}

	public void setCisPrefManager(ICommunityPreferenceManager cisPrefManager) {
		this.cisPrefManager = cisPrefManager;
	}

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	

}
