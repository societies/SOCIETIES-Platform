package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.personalisation.preference.api.CommunityPreferenceManagement.ICommunityPreferenceManager;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.webapp.controller.privacy.prefs.ModelTranslator;
import org.springframework.stereotype.Controller;

@Controller
@ManagedBean(name = "cisPrefManager")
@RequestScoped
public class CisPreferenceManager {

	@ManagedProperty(value = "#{cisManager}")
	private ICisManager cisManager;

	@ManagedProperty(value = "#{communityPreferenceManager}")
	private ICommunityPreferenceManager cisPrefManager;

	@ManagedProperty(value = "#{commMngrRef}")
	private ICommManager commManager;

	//private List<CisPreference> preferences;

	private HashMap<ICis, List<IPreferenceTreeModel>> preferences;
	private HashMap<TreeNode,PreferenceDetails> preferenceDetails;

	private static Logger log = LoggerFactory.getLogger(CisPreferenceManager.class);

	public CisPreferenceManager() {
		this.preferences = new HashMap<ICis, List<IPreferenceTreeModel>>();
	}

	@PostConstruct
	public void initCisPrefMgr() {
		log.debug("Init()");
		preferenceDetails = new HashMap<TreeNode, PreferenceDetails>();
		//GET ALL CIS'S I AM A MEMBER OF

		//GET LIST OF CIS'S
		List<ICis> cisList = cisManager.getCisList();
		List<TreeNode> nodes;
		List<IPreferenceTreeModel> model;
		for(ICis cis : cisList) {
			try {
				model = this.cisPrefManager.getAllCommunityPreferences(this.commManager.getIdManager().fromJid(cis.getCisId()));
				nodes = new ArrayList<TreeNode>();
				if(null!=model && !model.isEmpty()) {
					preferences.put(cis, model);
				}
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

	public TreeNode getTreeNode(IPreferenceTreeModel model) {
		TreeNode node = new DefaultTreeNode("Root", null); 
		return ModelTranslator.getPreference(model.getRootPreference(), node);

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

	public List<ICis> getAllCIS() {
		return new ArrayList<ICis>() {{ addAll(preferences.keySet()); }};
	}
	
	public List<IPreferenceTreeModel> getPreferenceTrees(ICis cis) {
		return preferences.get(cis);
	}




}
