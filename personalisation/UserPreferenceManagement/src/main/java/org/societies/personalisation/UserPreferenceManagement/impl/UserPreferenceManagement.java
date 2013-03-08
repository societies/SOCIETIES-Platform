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

package org.societies.personalisation.UserPreferenceManagement.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceManagement.impl.evaluation.PreferenceConditionExtractor;
import org.societies.personalisation.UserPreferenceManagement.impl.evaluation.PreferenceEvaluator;
import org.societies.personalisation.UserPreferenceManagement.impl.evaluation.PrivateContextCache;
import org.societies.personalisation.UserPreferenceManagement.impl.management.PrivatePreferenceCache;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.model.*;
import org.springframework.scheduling.annotation.AsyncResult;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.Future;


public class UserPreferenceManagement implements IUserPreferenceManagement {
    private Logger logging = LoggerFactory.getLogger(this.getClass());
    private PrivateContextCache contextCache;
    private PrivatePreferenceCache preferenceCache;
    private Hashtable<IPreferenceOutcome, List<CtxIdentifier>> outcomeConditionListTable;
    private ICtxBroker ctxBroker;
    //private IIdentity userId;

    // Personalisable preferences from registered services
    private final Map<IActionConsumer, Set<PersonalisablePreferenceIdentifier>> actionConsumerPreferenceMap = new HashMap<IActionConsumer, Set<PersonalisablePreferenceIdentifier>>();
    private final Set<PersonalisablePreferenceIdentifier> aggregateActionConsumerPreferences = new HashSet<PersonalisablePreferenceIdentifier>();

    public UserPreferenceManagement(ICtxBroker broker) {

        this.ctxBroker = broker;
        this.contextCache = new PrivateContextCache(this.ctxBroker);
        this.preferenceCache = new PrivatePreferenceCache(this.ctxBroker);
        outcomeConditionListTable = new Hashtable<IPreferenceOutcome, List<CtxIdentifier>>();

    }

    /*
     *  Get the instance of the context cache held under the preference manager
     *
     */
    public PrivateContextCache getPrivateContextCache() {
        return this.contextCache;
    }

    /*
     * get the instance of the preference cache held under the preference manager
     *
     */
    public PrivatePreferenceCache getPrivatePreferenceCache() {
        return this.preferenceCache;
    }

    public void removePreference(IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        this.preferenceCache.deletePreference(ownerID, serviceType, serviceID, preferenceName);
    }

    public IPreferenceOutcome getPreference(IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        if (serviceID == null) {
            logging.debug("Request for preference with null serviceID, returning empty Action");
            return null;
        }
        if (preferenceName == null) {
            logging.debug("Request for preference with null preferenceName, returning empty Action");
            return null;
        }
        IPreferenceTreeModel model = this.preferenceCache.getPreference(serviceType, serviceID, preferenceName);
        if (null != model) {
            IPreference p = model.getRootPreference();
            PreferenceEvaluator pEvaluator = new PreferenceEvaluator(this.contextCache);

            Hashtable<IPreferenceOutcome, List<CtxIdentifier>> evaluationResult = pEvaluator.evaluatePreference(p);
            Enumeration<IPreferenceOutcome> e = evaluationResult.keys();
            IPreferenceOutcome action = null;
            if (e.hasMoreElements()) {
                action = e.nextElement();
                action.setServiceID(serviceID);
                action.setServiceType(serviceType);
                logging.debug("evaluated preference " + preferenceName + " of " + serviceType + ":" + ServiceModelUtils.serviceResourceIdentifierToString(serviceID) + "\nand returning value: " + action.getvalue());
                return action;
            } else {
                logging.debug("evaluated preference " + preferenceName + " of " + serviceType + ":" + ServiceModelUtils.serviceResourceIdentifierToString(serviceID) + "\n did not yield any actions, returning empty action");
            }
        }
        logging.debug("No preference available for: " + preferenceName + " of " + serviceType + ":" + ServiceModelUtils.serviceResourceIdentifierToString(serviceID));
        return null;
    }

    public List<IPreferenceConditionIOutcomeName> getPreferenceConditions(IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID) {
        logging.debug("extracting conditions for all preferences of : " + serviceType + ":" + ServiceModelUtils.serviceResourceIdentifierToString(serviceID));
        List<IPreferenceConditionIOutcomeName> list = new ArrayList<IPreferenceConditionIOutcomeName>();
        List<String> prefnames = this.preferenceCache.getPreferenceNamesofService(serviceType, serviceID);
        PreferenceConditionExtractor pce = new PreferenceConditionExtractor();
        for (int i = 0; i < prefnames.size(); i++) {
            logging.debug("extracting conditions for: " + prefnames.get(i));
            IPreferenceTreeModel model = this.preferenceCache.getPreference(serviceType, serviceID, prefnames.get(i));
            if (null != model) {
                logging.debug("got preference " + prefnames.get(i) + " from cache");
                List<IPreferenceConditionIOutcomeName> tempList = pce.extractConditions(model);
                if (null != tempList) {
                    logging.debug("found conditions: ");
                    for (int k = 0; k < tempList.size(); k++) {
                        logging.debug("condition: " + tempList.get(k).getICtxIdentifier().getType());
                    }
                    list.addAll(tempList);
                } else {
                    logging.debug("not found any conditions, preference must be context-independent");
                }
            } else {
                logging.debug("not found any preference " + prefnames.get(i));
            }
        }
        logging.debug("found " + list.size() + " entries");
        return list;
    }


    public IPreferenceOutcome reEvaluatePreferences(IIdentity ownerID, CtxAttribute attribute, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        logging.debug("New context event received, requested re-evaluation of preference: ");
        logging.debug(preferenceName + "" + serviceType + ":" + ServiceModelUtils.serviceResourceIdentifierToString(serviceID));
        this.contextCache.updateCache(attribute);
        IPreferenceTreeModel model = this.preferenceCache.getPreference(serviceType, serviceID, preferenceName);
        if (model != null) {
            PreferenceEvaluator pEvaluator = new PreferenceEvaluator(this.contextCache);
            Hashtable<IPreferenceOutcome, List<CtxIdentifier>> evaluationResult = pEvaluator.evaluatePreference(model.getRootPreference());
            Enumeration<IPreferenceOutcome> e = evaluationResult.keys();
            IPreferenceOutcome o = null;
            if (e.hasMoreElements()) {
                o = e.nextElement();
                o.setServiceID(serviceID);
                o.setServiceType(serviceType);

                this.outcomeConditionListTable.put(o, evaluationResult.get(o));
                logging.debug("returning new Outcome to PCM: " + o.getparameterName() + " -> " + o.getvalue());
            } else {
                logging.debug("no  new outcome for PCM, returning empty Action");
            }
            return o;
        } else {
            logging.debug("Preference not found in cache");
        }
        return null;
    }

	/*
    public void updatePreference(IIdentity ownerID, IPreference preference, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
		log("Request to update preference: "+preferenceName+" of "+serviceType+":"+serviceID.toString());
		PreferenceRetriever retr = new PreferenceRetriever(this.myContext); 
		IPreferenceTreeModel iptm = retr.retrievePreference(serviceID, serviceType, preferenceName);
		if (null==iptm){
			log("No existing preference, try to store it directly to context mgmt");
			this.storePreference(ownerID, preference, serviceType, serviceID, preferenceName);
		}else{
			log("existing preference exists, merging will start now");
			PreferenceMerger merger = new PreferenceMerger();
			IPreference mergedPreference = merger.mergeTrees(iptm.getRootPreference(), preference, "");
			if (null!=mergedPreference){
				PreferenceStorer storer = new PreferenceStorer(this.myContext, this.registryManager);
				IPreferenceTreeModel newModel = new PreferenceTreeModel(mergedPreference);
				newModel.setPreferenceName(preferenceName);
				newModel.setServiceID(serviceID);
				newModel.setServiceType(serviceType);
				log("storing merged preference in context");
				storer.storeExisting(newModel);
			}
		}

	}*/

    private void calculateSizeOfObject(IPreference p) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(p);
            oos.flush();
            oos.close();
            bos.close();
            this.logging.debug("Trying to store preference of size: " + bos.size());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private byte[] getBytes(Object obj) throws java.io.IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(obj);
        oos.flush();
        oos.close();
        bos.close();
        byte[] data = bos.toByteArray();


        return data;
    }

    public boolean storePreference(IIdentity ownerID, PreferenceDetails details, IPreference preference) {

        logging.debug("request to store preference: for " + details.toString() + "\nPreference:\n" + preference.toTreeString());

        IPreferenceTreeModel model = new PreferenceTreeModel(preference);
        model.setPreferenceName(details.getPreferenceName());
        if (details.getServiceID() != null) {
            model.setServiceID(details.getServiceID());
        }
        model.setServiceType(details.getServiceType());
        return this.preferenceCache.storePreference(ownerID, details, model);
        //this.calculateSizeOfObject(preference);


    }

    public IPreferenceOutcome getPreference(IIdentity requestor, IIdentity owner, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        // TODO: Need to use the AccessControl to request permission to personalise service
        return this.getPreference(requestor, serviceType, serviceID, preferenceName);

		/*IPreferenceTreeModel model = this.preferenceCache.getPreference(serviceType, serviceID, preferenceName);
        PrefEvaluator pEvaluator = new PrefEvaluator(this.contextCache);
		if (model!=null){
			Hashtable<IPreferenceOutcome, List<ICtxIdentifier>> evaluationResult = pEvaluator.evaluatePreference(model.getRootPreference());
			Enumeration<IPreferenceOutcome> e = evaluationResult.keys();
			IPreferenceOutcome o = null;
			while (e.hasMoreElements()){
				o = (IPreferenceOutcome) e.nextElement();
				o.setServiceID(serviceID);
				o.setServiceType(serviceType);
			}
			return  o;
		}else{
			logging.debug("Preference "+preferenceName+" not found in cache");
			return null;
		}*/
    }


    public List<IPreferenceOutcome> reEvaluatePreferences(IIdentity dpi, CtxAttribute attr, List<PreferenceDetails> preferenceIdentifiers) {
        logging.debug("New context event received, requested re-evaluation of preference ");
        List<IPreferenceOutcome> list = new ArrayList<IPreferenceOutcome>();
        this.contextCache.updateCache(attr);
        logging.debug("updated my context cache");
        for (int i = 0; i < preferenceIdentifiers.size(); i++) {
            PreferenceDetails details = preferenceIdentifiers.get(i);
            logging.debug("getting preference: " + details.getServiceType() + ":" + details.getServiceID().toString() + ":" + details.getPreferenceName() + " from my preference cache");
            IPreferenceTreeModel model = this.preferenceCache.getPreference(details);
            if (model != null) {
                logging.debug("got valid preference from my preference cache. attempting to evaluate it");
            }
            IPreference preference = model.getRootPreference();
            if (null == preference) {
                logging.debug("Preference object inside PreferenceTreeModel is null");
            } else {
                PreferenceEvaluator pEvaluator = new PreferenceEvaluator(this.contextCache);
                Hashtable<IPreferenceOutcome, List<CtxIdentifier>> evaluationResult = pEvaluator.evaluatePreference(preference);
                Enumeration<IPreferenceOutcome> e = evaluationResult.keys();
                IPreferenceOutcome o = null;
                while (e.hasMoreElements()) {
                    o = e.nextElement();
                    o.setServiceID(model.getServiceID());
                    o.setServiceType(model.getServiceType());
                    this.outcomeConditionListTable.put(o, evaluationResult.get(o));
                }

                if (null != o) {
                    logging.debug("Evaluation result: " + o.getparameterName() + " -> " + o.getvalue());
                    list.add(o);
                } else {
                    logging.debug("Evaluation result for " + details.getServiceType() + ":" + details.getServiceID().toString() + ":" + details.getPreferenceName() + " is NULL");
                }
            }
        }
        return list;
    }

	/* (non-Javadoc)
     * @see org.personalsmartspace.pm.prefmgr.api.platform.IPreferenceHandler#getConditions(org.personalsmartspace.sre.api.pss3p.IIdentity, org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceOutcome)
	 */

    public List<CtxIdentifier> getConditions(IIdentity dpi,
                                             IPreferenceOutcome outcome) {
        return this.outcomeConditionListTable.get(outcome);
    }


    //TODO: use PrivatePreferenceCache. not PreferenceRetriever
    public IPreferenceTreeModel getModel(IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        return this.preferenceCache.getPreference(serviceType, serviceID, preferenceName);
    }


    public List<CtxIdentifier> getPreferenceConditions(IIdentity ownerID, String serviceType, ServiceResourceIdentifier serviceID, String preferenceName) {
        PreferenceConditionExtractor pce = new PreferenceConditionExtractor();
        IPreferenceTreeModel model = this.preferenceCache.getPreference(serviceType, serviceID, preferenceName);
        if (model == null) {
            this.logging.debug("Preference for " + new Tools(this.ctxBroker).convertToKey(serviceType, ServiceModelUtils.serviceResourceIdentifierToString(serviceID), preferenceName) + " doesn't exist");
            return new ArrayList<CtxIdentifier>();
        }
        List<IPreferenceConditionIOutcomeName> list = pce.extractConditions(model);
        List<CtxIdentifier> ctxIDs = new ArrayList<CtxIdentifier>();

        for (IPreferenceConditionIOutcomeName obj : list) {
            ctxIDs.add(obj.getICtxIdentifier());
        }

        return ctxIDs;

    }


    @Override
    public boolean deletePreference(IIdentity ownerID, PreferenceDetails details) {
        return this.preferenceCache.deletePreference(ownerID, details);
    }

    /*
     * (non-Javadoc)
     * @see org.societies.personalisation.preference.api.IUserPreferenceManagement#getPreferenceDetailsForAllPreferences()
     */
    @Override
    public List<PreferenceDetails> getPreferenceDetailsForAllPreferences() {
        return this.preferenceCache.getPreferenceDetailsForAllPreferences();
    }

    /*
     * (non-Javadoc)
     * @see org.societies.personalisation.preference.api.IUserPreferenceManagement#getModel(org.societies.api.identity.IIdentity, org.societies.api.internal.personalisation.model.PreferenceDetails)
     */
    @Override
    public IPreferenceTreeModel getModel(IIdentity ownerDPI,
                                         PreferenceDetails details) {
        return this.preferenceCache.getPreference(details);
    }


    public void emptyContextCache() {
        this.contextCache = new PrivateContextCache(ctxBroker);
    }

    public void updateContext(CtxAttribute attribute) {
        this.contextCache.updateCache(attribute);

    }

    /*
     * (non-Javadoc)
     * @see org.societies.api.internal.personalisation.preference.IUserPreferenceManagement#getOutcome(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
     */
    @Override
    public Future<IOutcome> getOutcome(IIdentity ownerId,
                                       ServiceResourceIdentifier serviceId, String preferenceName) {
        return new AsyncResult<IOutcome>(this.getPreference(ownerId, "", serviceId, preferenceName));
    }

    @Override
    public void registerPersonalisableService(IActionConsumer actionConsumer, PersonalisablePreferenceIdentifier preference) {
        if (actionConsumer == null)
            throw new IllegalArgumentException("actionConsumer cannot be null");
        if (preference == null)
            throw new IllegalArgumentException("preference cannot be null");

        // add to actionConsumerPreferenceMap and aggregateActionConsumerPreferences

        Set<PersonalisablePreferenceIdentifier> consumerSet;
        if (actionConsumerPreferenceMap.containsKey(actionConsumer))
            consumerSet = actionConsumerPreferenceMap.get(actionConsumer);
        else {
            consumerSet = new HashSet<PersonalisablePreferenceIdentifier>();
            actionConsumerPreferenceMap.put(actionConsumer, consumerSet);
        }

        aggregateActionConsumerPreferences.add(preference);
        consumerSet.add(preference);
    }


    @Override
    public List<PersonalisablePreferenceIdentifier> getKnownPersonalisablePreferences() {
        // clone the list so it can't be messed with
        return new ArrayList<PersonalisablePreferenceIdentifier>(aggregateActionConsumerPreferences);
    }
}
