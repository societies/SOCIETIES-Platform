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
package org.societies.personalisation.management.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.*;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IFeedbackEvent;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.useragent.decisionmaking.IDecisionMaker;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.*;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.personalisation.model.IActionConsumer;
import org.societies.api.personalisation.model.PersonalisablePreferenceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.CAUI.api.CAUIPrediction.ICAUIPrediction;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CRIST.api.CRISTUserIntentPrediction.ICRISTUserIntentPrediction;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;
import org.societies.personalisation.DIANNE.api.DianneNetwork.IDIANNE;
import org.societies.personalisation.DIANNE.api.model.IDIANNEOutcome;
import org.societies.personalisation.common.api.management.IInternalPersonalisationManager;
import org.societies.personalisation.common.api.model.PersonalisationTypes;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PersonalisationManager extends EventListener implements IPersonalisationManager, IInternalPersonalisationManager, CtxChangeEventListener {

    // services
    private ICtxBroker ctxBroker;
    private Logger logging = LoggerFactory.getLogger(this.getClass());
    private IUserPreferenceConditionMonitor pcm;
    private IDIANNE dianne;
    private ICAUIPrediction cauiPrediction;
    private ICRISTUserIntentPrediction cristPrediction;
    private IIdentityManager idm;
    private IDecisionMaker decisionMaker;
    private ICommManager commsMgr;
    private IEventMgr eventMgr;

    // data structures
    ArrayList<CtxAttributeIdentifier> dianneList;
    ArrayList<CtxAttributeIdentifier> prefMgrList;
    ArrayList<CtxAttributeIdentifier> cauiList;
    ArrayList<CtxAttributeIdentifier> cristList;

    //internal confidence levels for each personalisation source
    private int dianneConfidenceLevel;
    private int prefMgrConfidenceLevel;
    private int cauiConfidenceLevel;
    private int cristConfidenceLevel;

    public PersonalisationManager() {
        this.dianneList = new ArrayList<CtxAttributeIdentifier>();
        this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
        this.cauiList = new ArrayList<CtxAttributeIdentifier>();
        this.cristList = new ArrayList<CtxAttributeIdentifier>();
        this.logging.debug("executed constructor");
    }

    public void initialisePersonalisationManager(ICtxBroker broker, IUserPreferenceConditionMonitor pcm, IDIANNE dianne, ICAUIPrediction cauiPrediction, ICRISTUserIntentPrediction cristPrediction, ICommManager commsMgr, IDecisionMaker decisionMaker) {
        this.ctxBroker = broker;
        this.pcm = pcm;
        this.dianne = dianne;
        this.cauiPrediction = cauiPrediction;
        this.cristPrediction = cristPrediction;
        this.setCommsMgr(commsMgr);
        this.decisionMaker = decisionMaker;
        this.idm = this.getCommsMgr().getIdManager();
        retrieveConfidenceLevels();
        this.registerForUIMEvents();
        this.dianne.registerContext();
        this.logging.debug("initialisePersonalisationManager(ICtxBroker broker, IUserPreferenceConditionMonitor pcm, IDIANNE dianne, ICAUIPrediction cauiPrediction, ICRISTUserIntentPrediction cristPrediction, ICommManager commsMgr, IDecisionMaker decisionMaker)");

    }

    public void initialisePersonalisationManager() {

        this.dianneList = new ArrayList<CtxAttributeIdentifier>();
        this.prefMgrList = new ArrayList<CtxAttributeIdentifier>();
        this.cauiList = new ArrayList<CtxAttributeIdentifier>();
        this.cristList = new ArrayList<CtxAttributeIdentifier>();
        this.registerForUIMEvents();
        retrieveConfidenceLevels();
        this.dianne.registerContext();
        this.logging.debug("initialisePersonalisationManager()");

    }


    private void registerForUIMEvents() {
        String eventFilter = "(&" +
                "(" + CSSEventConstants.EVENT_NAME + "=newaction)" +
                "(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/monitoring)" +
                ")";
        this.getEventMgr().subscribeInternalEvent(this, new String[]{EventTypes.UIM_EVENT}, eventFilter);
        this.logging.debug("Subscribed to " + EventTypes.UIM_EVENT + " events");

    }

    private void retrieveConfidenceLevels() {
        try {
            Future<List<CtxIdentifier>> futuredianneConf = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, "dianneConfidenceLevel");
            List<CtxIdentifier> dianneConfs = futuredianneConf.get();
            if (dianneConfs.isEmpty()) {
                this.dianneConfidenceLevel = 50;
            } else {
                CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(dianneConfs.get(0)).get();
                this.dianneConfidenceLevel = tempAttr.getIntegerValue();
            }

            Future<List<CtxIdentifier>> futureprefMgrConf = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, "prefMgrConfidenceLevel");
            List<CtxIdentifier> prefMgrConf = futureprefMgrConf.get();
            if (prefMgrConf.isEmpty()) {
                this.prefMgrConfidenceLevel = 50;
            } else {
                CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(prefMgrConf.get(0)).get();
                this.prefMgrConfidenceLevel = tempAttr.getIntegerValue();
            }

            Future<List<CtxIdentifier>> futurecauiConf = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, "cauiConfidenceLevel");
            List<CtxIdentifier> cauiConf = futurecauiConf.get();
            if (cauiConf.isEmpty()) {
                this.cauiConfidenceLevel = 50;
            } else {
                CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(cauiConf.get(0)).get();
                this.cauiConfidenceLevel = tempAttr.getIntegerValue();
            }


            Future<List<CtxIdentifier>> futurecristConf = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, "cristConfidenceLevel");
            List<CtxIdentifier> cristConf = futurecauiConf.get();
            if (cristConf.isEmpty()) {
                this.cristConfidenceLevel = 50;
            } else {
                CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(cristConf.get(0)).get();
                this.cristConfidenceLevel = tempAttr.getIntegerValue();
            }

            logging.debug("retrieved confidence levels");
        } catch (CtxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    public ICAUIPrediction getCauiPrediction() {
        return cauiPrediction;
    }

    public void setCauiPrediction(ICAUIPrediction cauiPrediction) {
        this.cauiPrediction = cauiPrediction;
    }

    public ICRISTUserIntentPrediction getCristPrediction() {
        return cristPrediction;
    }

    public void setCristPrediction(ICRISTUserIntentPrediction cristPrediction) {
        this.cristPrediction = cristPrediction;
    }

    public IUserPreferenceConditionMonitor getPcm() {
        System.out.println(this.getClass().getName() + "Return PCM");
        return pcm;
    }

    public void setPcm(IUserPreferenceConditionMonitor pcm) {
        System.out.println(this.getClass().getName() + "GOT PCM");
        this.pcm = pcm;
    }

    public IDIANNE getDianne() {
        System.out.println(this.getClass().getName() + "Return DIANNE");
        return dianne;
    }

    public void setDianne(IDIANNE dianne) {
        System.out.println(this.getClass().getName() + "GOT DIANNE");
        this.dianne = dianne;
    }

    public ICtxBroker getCtxBroker() {
        System.out.println(this.getClass().getName() + "Return CtxBroker");
        return this.ctxBroker;
    }

    public void setCtxBroker(ICtxBroker broker) {
        System.out.println(this.getClass().getName() + "GOT CtxBroker");
        this.ctxBroker = broker;
    }

    public IDecisionMaker getDecisionMaker() {
        return decisionMaker;
    }

    public void setDecisionMaker(IDecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    /**
     * @return the commsMgr
     */
    public ICommManager getCommsMgr() {
        return commsMgr;
    }

    /**
     * @param commsMgr the commsMgr to set
     */
    public void setCommsMgr(ICommManager commsMgr) {
        this.commsMgr = commsMgr;
        this.idm = commsMgr.getIdManager();
    }

    /**
     * @return the eventMgr
     */
    public IEventMgr getEventMgr() {
        return eventMgr;
    }

    /**
     * @param eventMgr the eventMgr to set
     */
    public void setEventMgr(IEventMgr eventMgr) {
        this.eventMgr = eventMgr;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.societies.personalisation.common.api.management.
     * IInternalPersonalisationManager
     * #returnFeedback(org.societies.api.internal.
     * personalisation.model.IFeedbackEvent)
     */
    @Override
    public void returnFeedback(IFeedbackEvent arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see org.societies.personalisation.common.api.management.
     * IInternalPersonalisationManager
     * #registerForContextUpdate(org.societies.api
     * .comm.xmpp.datatypes.IIdentity,
     * org.societies.personalisation.common.api.model.PersonalisationTypes,
     * org.societies.api.context.model.CtxAttributeIdentifier)
     */
    @Override
    public void registerForContextUpdate(IIdentity id,
                                         PersonalisationTypes type, CtxAttributeIdentifier ctxAttributeId) {
        try {
            if (isAlreadyRegistered(ctxAttributeId)) {
                this.logging.debug(type.toString() + " requested event registration for: " + ctxAttributeId.getType() + " but I'm already registered for it.");
            } else {
                this.ctxBroker.registerForChanges(this, ctxAttributeId);
                this.logging.debug(type.toString() + " requested event registration for: " + ctxAttributeId.getType());
            }

        } catch (CtxException e) {
            logging.error(e.getMessage());
            e.printStackTrace();
        }

        switch (type) {
            case UserPreference:
                this.addtoList(ctxAttributeId, prefMgrList);
            case DIANNE:
                this.addtoList(ctxAttributeId, dianneList);
                break;
            case CAUIIntent:
                this.addtoList(ctxAttributeId, cauiList);
                break;
            case CRISTIntent:
                this.addtoList(ctxAttributeId, cristList);
                break;
            default:
                return;
        }

    }

    private boolean isAlreadyRegistered(CtxAttributeIdentifier id) {
        for (CtxAttributeIdentifier ctxAttrId : this.prefMgrList) {
            if (ctxAttrId.equals(id)) {
                return true;
            }
        }

        for (CtxAttributeIdentifier ctxAttrId : this.dianneList) {
            if (ctxAttrId.equals(id)) {
                return true;
            }
        }

        for (CtxAttributeIdentifier ctxAttrId : this.cauiList) {
            if (ctxAttrId.equals(id)) {
                return true;
            }
        }

        for (CtxAttributeIdentifier ctxAttrId : this.cristList) {
            if (ctxAttrId.equals(id)) {
                return true;
            }
        }
        return false;

    }

    private void addtoList(CtxAttributeIdentifier ctxId,
                           List<CtxAttributeIdentifier> list) {
        for (CtxAttributeIdentifier Id : list) {
            if (ctxId.equals(Id)) {
                return;
            }
        }

        list.add(ctxId);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.societies.personalisation.common.api.management.
     * IInternalPersonalisationManager
     * #getPreference(org.societies.api.comm.xmpp.datatypes.IIdentity,
     * java.lang.String,
     * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
     * java.lang.String,
     * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
     */
    @Override
    public Future<IAction> getPreference(IIdentity ownerID, String serviceType,
                                         ServiceResourceIdentifier serviceID, String preferenceName) {
        this.logging.debug("Processing request for preference: " + preferenceName + " for serviceType: " + serviceType + " and serviceID " + serviceID.getServiceInstanceIdentifier());

        Future<List<IDIANNEOutcome>> futureDianneOuts;

        futureDianneOuts = this.dianne.getOutcome(ownerID, serviceID, preferenceName);

        if (futureDianneOuts == null) {
            futureDianneOuts = new AsyncResult<List<IDIANNEOutcome>>(new ArrayList<IDIANNEOutcome>());
            this.logging.debug(".getPreference(...): DIANNE returned null list");
        }
        Future<IOutcome> futurePrefOuts;

        futurePrefOuts = this.pcm.getOutcome(ownerID, serviceID, preferenceName);


        if (futurePrefOuts == null) {
            futurePrefOuts = new AsyncResult<IOutcome>(null);
            this.logging.debug(".getPreference(...): PCM returned null list");
        }
        IAction action;
        try {
            List<IDIANNEOutcome> dianneOutList = futureDianneOuts.get();
            if (dianneOutList.size() > 0) {

                IDIANNEOutcome dianneOut = dianneOutList.get(0);
                this.logging.debug(".getPreference(...): DIANNE returned an outcome: " + dianneOut.toString());
                IPreferenceOutcome prefOut = (IPreferenceOutcome) futurePrefOuts.get();

                if (null == prefOut) {
                    this.logging.debug(".getPreference(...): PCM didn't return an outcome. Returning DIANNE's outcome: " + dianneOut.toString());
                    return new AsyncResult<IAction>(dianneOut);
                }

                this.logging.debug(".getPreference(...): PCM returned an outcome " + prefOut.toString());
                if (dianneOut.getvalue().equalsIgnoreCase(prefOut.getvalue())) {
                    action = new Action(serviceID, serviceType, preferenceName, prefOut.getvalue());
                    action.setServiceID(serviceID);
                    action.setServiceType(serviceType);
                    this.logging.debug(".getPreference(...): returning action: " + action.toString());
                    return new AsyncResult<IAction>(action);
                } else {
                    this.logging.debug(".getPreference(...): conflict between pcm and dianne.");
                    return new AsyncResult<IAction>(this.resolvePreferenceConflicts(dianneOut, prefOut));
                }

            } else {

                IPreferenceOutcome prefOut = (IPreferenceOutcome) futurePrefOuts.get();
                if (prefOut != null) {
                    this.logging.debug(".getPreference(...): DIANNE didn't return an outcome. Returning PCM's outcome: " + prefOut.toString());
                    return new AsyncResult<IAction>(prefOut);
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.logging.debug(".getPreference(...): Returning null action");
        return new AsyncResult<IAction>(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#
     * getIntentAction(org.societies.api.comm.xmpp.datatypes.IIdentity,
     * org.societies.api.comm.xmpp.datatypes.IIdentity,
     * org.societies.api.servicelifecycle.model.ServiceResourceIdentifier,
     * java.lang.String,
     * org.societies.api.personalisation.mgmt.IPersonalisationCallback)
     */
    @Override
    public Future<IAction> getIntentAction(Requestor requestor, IIdentity ownerID,
                                           ServiceResourceIdentifier serviceID, String preferenceName) {
        return this.getIntentAction(ownerID, serviceID, preferenceName);
    }


    /*
     * (non-Javadoc)
     * @see org.societies.api.personalisation.mgmt.IPersonalisationManager#getPreference(org.societies.api.identity.Requestor, org.societies.api.identity.IIdentity, java.lang.String, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
     */
    @Override
    public Future<IAction> getPreference(Requestor requestor, IIdentity ownerID,
                                         String serviceType, ServiceResourceIdentifier serviceID,
                                         String preferenceName) {
        //check with access control
        return this.getPreference(ownerID, serviceType, serviceID, preferenceName);

    }

    @Override
    public void registerPersonalisableService(IActionConsumer actionConsumer) {
        if (actionConsumer == null)
            throw new IllegalArgumentException("actionConsumer cannot be null");

        List<PersonalisablePreferenceIdentifier> preferenceIdentifiers = actionConsumer.getPersonalisablePreferences();

        if (preferenceIdentifiers == null) {
            throw new IllegalArgumentException("actionConsumer.getPersonalisablePreferences() must return a list of personalisable preferences");
        }

        for (PersonalisablePreferenceIdentifier pref : preferenceIdentifiers) {
            this.pcm.getPreferenceManager().registerPersonalisableService(actionConsumer, pref);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.societies.personalisation.common.api.management.IInternalPersonalisationManager#getIntentAction(org.societies.api.identity.IIdentity, org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, java.lang.String)
     */
    @Override
    public Future<IAction> getIntentAction(IIdentity ownerID,
                                           ServiceResourceIdentifier serviceID, String preferenceName) {

        Future<IUserIntentAction> futureCAUIOuts;
        try {
            futureCAUIOuts = this.cauiPrediction.getCurrentIntentAction(ownerID, serviceID, preferenceName);
        } catch (Exception e) {
            e.printStackTrace();
            futureCAUIOuts = new AsyncResult<IUserIntentAction>(null);
        }
        Future<CRISTUserAction> futureCRISTOuts;
        try {
            futureCRISTOuts = this.cristPrediction.getCurrentUserIntentAction(ownerID, serviceID, preferenceName);
        } catch (Exception e) {
            e.printStackTrace();
            futureCRISTOuts = new AsyncResult<CRISTUserAction>(null);
        }
        IAction action;

        try {
            IUserIntentAction cauiOut = futureCAUIOuts.get();
            CRISTUserAction cristOut = futureCRISTOuts.get();

            if (cauiOut == null) {
                if (cristOut == null) {
                    return new AsyncResult<IAction>(null);
                } else {
                    return new AsyncResult<IAction>(cristOut);
                }
            } else {
                if (cristOut == null) {
                    return new AsyncResult<IAction>(cauiOut);
                }
            }


            if (cauiOut.getvalue().equalsIgnoreCase(cristOut.getvalue())) {
                action = new Action(serviceID, "", preferenceName, cauiOut.getvalue());
                return new AsyncResult<IAction>(action);
            } else {
                return new AsyncResult<IAction>(this.resolveIntentConflicts(cristOut, cauiOut));
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new AsyncResult<IAction>(null);
    }


    private boolean containsCtxId(CtxAttributeIdentifier ctxId,
                                  List<CtxAttributeIdentifier> list) {
        for (CtxAttributeIdentifier id : list) {
            if (ctxId.equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreation(CtxChangeEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onModification(final CtxChangeEvent event) {
        this.logging.debug("Received context event: " + event.getId().getType());

        new Thread() {

            public void run() {
                CtxIdentifier ctxIdentifier = event.getId();

                try {
                    Thread.sleep(10);
                    Future<CtxModelObject> futureAttribute = ctxBroker.retrieve(ctxIdentifier);

                    CtxAttribute ctxAttribute = (CtxAttribute) futureAttribute.get();

                    if (null != ctxAttribute) {

                        if (ctxAttribute instanceof CtxAttribute) {
                            logging.debug("Received event and retrieved value " + ctxAttribute.getStringValue() + " for context attribute: " + ctxAttribute.getType());
                            CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();
                            IIdentity userId = idm.fromJid(ctxId.getOperatorId());
                            List<IOutcome> preferenceOutcomes = processPreferences(userId, ctxAttribute);
                            List<IOutcome> intentOutcomes = processIntent(userId, ctxAttribute);

                            if (preferenceOutcomes.size() == 0 && intentOutcomes.size() == 0) {
                                logging.debug("Nothing to send to decisionMaker");
                                return;
                            } else {
                                for (int i = 0; i < preferenceOutcomes.size(); i++) {
                                    logging.debug("Pref Outcome " + i + " :" + preferenceOutcomes.get(i));
                                }
                                for (int i = 0; i < intentOutcomes.size(); i++) {
                                    logging.debug("Intent Outcome " + i + " :" + intentOutcomes.get(i));
                                }
                            }
                            logging.debug("Sending " + preferenceOutcomes.size() + " preference outcomes and " + intentOutcomes.size() + " intent outcomes to decisionMaker");
                            decisionMaker.makeDecision(intentOutcomes, preferenceOutcomes);
                        } else {
                            logging.debug("retrieved attribute but was not instanceof CtxAttribute");
                        }
                    } else {
                        logging.debug("Tried to retrieve ctxAttribute from ctxDB onModification() but the attribute is null");
                    }
                } catch (CtxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InvalidFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private List<IOutcome> processIntent(IIdentity userId, CtxAttribute ctxAttribute) {
        this.logging.debug("Processing intent");
        List<IOutcome> results = new ArrayList<IOutcome>();
        CtxAttributeIdentifier ctxId = ctxAttribute.getId();


        try {
            if (this.containsCtxId(ctxId, cauiList)) {
                this.logging.debug("caui is registered for events of: " + ctxId.toUriString());
                Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
                if (this.containsCtxId(ctxId, cristList)) {
                    this.logging.debug("crist is registered for events of: " + ctxId.toUriString());
                    Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
                    return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get());
                } else {
                    this.logging.debug("crist is NOT registered for events of: " + ctxId.toUriString());
                    List<IUserIntentAction> cauiActions = futureCauiActions.get();

                    for (IUserIntentAction cauiAction : cauiActions) {
                        CRISTUserAction cristAction = this.cristPrediction.getCurrentUserIntentAction(userId, cauiAction.getServiceID(), cauiAction.getparameterName()).get();
                        if (null == cristAction) {
                            results.add(cauiAction);
                        } else {
                            if (cristAction.getvalue().equalsIgnoreCase(cauiAction.getvalue())) {
                                results.add(cauiAction);
                            } else {
                                results.add(this.resolveIntentConflicts(cristAction, cauiAction));
                            }
                        }
                    }
                }
            } else if (this.containsCtxId(ctxId, cristList)) {
                this.logging.debug("crist is registered for events of: " + ctxId.toUriString());
                Future<List<CRISTUserAction>> futureCristActions = this.cristPrediction.getCRISTPrediction(userId, ctxAttribute);
                if (this.containsCtxId(ctxId, cauiList)) {
                    this.logging.debug("caui is registered for events of: " + ctxId.toUriString());
                    Future<List<IUserIntentAction>> futureCauiActions = this.cauiPrediction.getPrediction(userId, ctxAttribute);
                    return this.compareIntentConflicts(futureCauiActions.get(), futureCristActions.get());
                } else {
                    this.logging.debug("caui is NOT registered for events of: " + ctxId.toUriString());
                    List<CRISTUserAction> cristActions = futureCristActions.get();

                    for (CRISTUserAction cristAction : cristActions) {
                        IUserIntentAction cauiAction = this.cauiPrediction.getCurrentIntentAction(userId, cristAction.getServiceID(), cristAction.getparameterName()).get();
                        if (null == cauiAction) {
                            results.add(cristAction);
                        } else {
                            if (cauiAction.getvalue().equalsIgnoreCase(cristAction.getvalue())) {
                                results.add(cristAction);
                            } else {
                                results.add(this.resolveIntentConflicts(cristAction, cauiAction));
                            }
                        }
                    }
                }
            } else {
                this.logging.debug("Context attribute: " + ctxAttribute.getType() + " not affecting intent models");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return results;
    }


    private List<IOutcome> processPreferences(IIdentity userId, CtxAttribute ctxAttribute) {
        this.logging.debug("Processing preferences after receiving context event");
        List<IOutcome> results = new ArrayList<IOutcome>();
        /*
         * List<IPreferenceOutcome> pcmResults = new
		 * ArrayList<IPreferenceOutcome>(); List<IDIANNEOutcome> dianneResults =
		 * new ArrayList<IDIANNEOutcome>();
		 */

        try {
            CtxAttributeIdentifier ctxId = (CtxAttributeIdentifier) ctxAttribute.getId();

            if (this.containsCtxId(ctxId, dianneList)) {
                this.logging.debug("dianne is registered for events of: " + ctxId.toUriString());
                Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
                if (this.containsCtxId(ctxId, prefMgrList)) {
                    this.logging.debug("pcm is registered for events of: " + ctxId.toUriString());
                    Future<List<IPreferenceOutcome>> futurePrefOutcomes = this.pcm.getOutcome(userId, ctxAttribute);
                    return this.comparePreferenceConflicts(futureDianneOutcomes.get(), futurePrefOutcomes.get());
                } else {
                    this.logging.debug("pcm is NOT registered for events of: " + ctxId.toUriString());
                    List<IDIANNEOutcome> dianneOutcomes;

                    dianneOutcomes = futureDianneOutcomes.get();
                    this.logging.debug("Received " + dianneOutcomes.size() + " outcomes from dianne after receiving context event: " + ctxAttribute.getType());
                    for (IDIANNEOutcome dOut : dianneOutcomes) {
                        IPreferenceOutcome pOut = (IPreferenceOutcome) this.pcm.getOutcome(userId, dOut.getServiceID(), dOut.getparameterName()).get();
                        if (null == pOut) {
                            results.add(dOut);
                        } else {
                            if (pOut.getvalue().equalsIgnoreCase(dOut.getvalue())) {
                                results.add(dOut);
                            } else {
                                results.add(this.resolvePreferenceConflicts(dOut, pOut));
                            }
                        }
                    }
                }
            } else if (this.containsCtxId(ctxId, prefMgrList)) {
                this.logging.debug("pcm is registered for events of: " + ctxId.toUriString());
                Future<List<IPreferenceOutcome>> futurePcmOutcomes = this.pcm.getOutcome(userId, ctxAttribute);
                if (this.containsCtxId(ctxId, dianneList)) {
                    this.logging.debug("dianne is registered for events of: " + ctxId.toUriString());
                    Future<List<IDIANNEOutcome>> futureDianneOutcomes = this.dianne.getOutcome(userId, (CtxAttribute) ctxAttribute);
                    return this.comparePreferenceConflicts(futureDianneOutcomes.get(), futurePcmOutcomes.get());
                } else {
                    this.logging.debug("dianne is NOT registered for events of: " + ctxId.toUriString());
                    List<IPreferenceOutcome> pcmOutcomes = futurePcmOutcomes.get();
                    this.logging.debug("Received " + pcmOutcomes.size() + " outcomes from pcm after receiving context event: " + ctxAttribute.getType());
                    for (IPreferenceOutcome pOut : pcmOutcomes) {
                        IDIANNEOutcome dOut = this.dianne.getOutcome(userId, pOut.getServiceID(), pOut.getparameterName()).get().get(0);
                        if (null == dOut) {
                            results.add(pOut);
                        } else {
                            if (dOut.getvalue().equalsIgnoreCase(pOut.getvalue())) {
                                results.add(pOut);
                            } else {
                                results.add(this.resolvePreferenceConflicts(dOut, pOut));
                            }
                        }
                    }
                }
            } else {
                this.logging.debug("Context attribute: " + ctxAttribute.getType() + " not affecting any preferences");
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return results;
    }


    private List<IOutcome> comparePreferenceConflicts(List<IDIANNEOutcome> dOuts, List<IPreferenceOutcome> pOuts) {
        this.logging.debug("Finding conflicts between dianne and pcm");

        List<IOutcome> result = new ArrayList<IOutcome>();
        for (IDIANNEOutcome dOut : dOuts) {
            boolean matches = false;
            IPreferenceOutcome matchedOutcome = null;
            for (IPreferenceOutcome pOut : pOuts) {
                if (dOut.getparameterName().equalsIgnoreCase(pOut.getparameterName())) {
                    matches = true;
                    matchedOutcome = pOut;
                } else {
                    matches = false;
                }
            }
            if (!matches) {
                result.add(dOut);
            } else {
                result.add(this.resolvePreferenceConflicts(dOut, matchedOutcome));
            }
        }


        for (IPreferenceOutcome pOut : pOuts) {
            IOutcome out = matches(pOut, result);


            if (out == null) {
                result.add(pOut);
            }
        }

        return result;
    }


    private IOutcome matches(IOutcome outcome, List<IOutcome> outcomes) {
        for (IOutcome out : outcomes) {
            if (outcome.getServiceID().equals(out.getServiceID())) {
                if (outcome.getparameterName().equalsIgnoreCase(out.getparameterName())) {
                    return out;
                }
            }
        }

        return null;
    }

    private List<IOutcome> compareIntentConflicts(List<IUserIntentAction> cauiOuts, List<CRISTUserAction> cristOuts) {
        this.logging.debug("Finding conflicts between caui and crist");
        List<IOutcome> result = new ArrayList<IOutcome>();
        for (IUserIntentAction cauiOut : cauiOuts) {
            boolean matches = false;
            CRISTUserAction matchedOutcome = null;
            for (CRISTUserAction cristOut : cristOuts) {
                if (cauiOut.getparameterName().equalsIgnoreCase(cristOut.getparameterName())) {
                    matches = true;
                    matchedOutcome = cristOut;
                } else {
                    matches = false;
                }
            }
            if (!matches) {
                result.add(cauiOut);
            } else {
                result.add(this.resolveIntentConflicts(matchedOutcome, cauiOut));
            }
        }


        for (CRISTUserAction cristOut : cristOuts) {
            IOutcome out = matches(cristOut, result);


            if (out == null) {
                result.add(cristOut);
            }
        }

        return result;
    }

    private IOutcome resolvePreferenceConflicts(IDIANNEOutcome dOut, IPreferenceOutcome pOut) {
        this.logging.debug("Resolving preference conflicts between dianne and pcm");
        int dConf = this.dianneConfidenceLevel * dOut.getConfidenceLevel();

        int pConf = this.prefMgrConfidenceLevel * pOut.getConfidenceLevel();

        if (dConf > pConf) {
            this.logging.debug("Conflict Resolved. Returning dianne's outcome: " + dOut.toString());
            return dOut;
        } else {
            this.logging.debug("Conflict Resolved. Returning pcm's outcome:" + pOut.toString());
            return pOut;
        }


    }

    private IOutcome resolveIntentConflicts(CRISTUserAction cristAction, IUserIntentAction cauiAction) {
        this.logging.debug("Resolving intent conflicts between crist and caui");
        int cauiConf = this.cauiConfidenceLevel * cauiAction.getConfidenceLevel();

        int cristConf = this.cristConfidenceLevel * cristAction.getConfidenceLevel();

        if (cauiConf > cristConf) {
            this.logging.debug("Conflict Resolved. Returning caui's outcome: " + cauiAction.toString());

            return cauiAction;
        } else {
            this.logging.debug("Conflict Resolved. Returning crist's outcome: " + cristAction.toString());

            return cristAction;
        }

    }

    @Override
    public void onRemoval(CtxChangeEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdate(CtxChangeEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleInternalEvent(final InternalEvent event) {
        this.logging.debug("Received UIM event:");
        this.logging.debug("Event name " + event.geteventName() +
                "Event info: " + event.geteventInfo().toString() +
                "Event source: " + event.geteventSource() +
                "Event type: " + event.geteventType());
        new Thread() {

            public void run() {
                try {

                    if (event.geteventType().equals(EventTypes.UIM_EVENT)) {
                        UIMEvent uimEvent = (UIMEvent) event.geteventInfo();
                        Future<List<IUserIntentAction>> futureCauiActions = cauiPrediction.getPrediction(uimEvent.getUserId(), uimEvent.getAction());
                        logging.debug("Requested caui prediction");
                        List<IUserIntentAction> cauiActions = futureCauiActions.get();
                        logging.debug("cauiPrediction returned: " + cauiActions.size() + " outcomes");

                        Future<List<CRISTUserAction>> futureCristActions = cristPrediction.getCRISTPrediction(uimEvent.getUserId(), uimEvent.getAction());
                        logging.debug("Requested crist prediction");
                        List<CRISTUserAction> cristActions = futureCristActions.get();
                        logging.debug("cristPrediction returned: " + cristActions.size() + " outcomes");
                        for (CRISTUserAction action : cristActions) {
                            logging.debug("Crist outcome - parameter: " + action.getparameterName() + " - value: " + action.getvalue());
                        }


                        /**
                         * get intent outcomes
                         */


                        Hashtable<IUserIntentAction, CRISTUserAction> overlapping = new Hashtable<IUserIntentAction, CRISTUserAction>();
                        List<IOutcome> intentNonOverlapping = new ArrayList<IOutcome>();


                        for (IUserIntentAction caui : cauiActions) {
                            CRISTUserAction crist = exists(cristActions, caui);
                            if (null == crist) {
                                intentNonOverlapping.add(caui);
                            } else {
                                overlapping.put(caui, crist);
                            }
                        }

                        for (CRISTUserAction crist : cristActions) {
                            IUserIntentAction caui = exists(cauiActions, crist);
                            if (null == caui) {
                                intentNonOverlapping.add(crist);
                            }
                        }

                        Enumeration<IUserIntentAction> cauiEnum = overlapping.keys();

                        while (cauiEnum.hasMoreElements()) {
                            IUserIntentAction caui = cauiEnum.nextElement();
                            CRISTUserAction crist = overlapping.get(caui);
                            intentNonOverlapping.add(resolveIntentConflicts(crist, caui));
                        }


                        /**
                         * get preference outcomes
                         */

                        Future<List<IDIANNEOutcome>> futureDianneActions = dianne.getOutcome(uimEvent.getUserId(), uimEvent.getAction());
                        logging.debug("Requested outcome from dianne");
                        List<IDIANNEOutcome> dianneActions = futureDianneActions.get();
                        logging.debug("DIANNE returned: " + dianneActions.size() + " outcomes");


                        Future<List<IPreferenceOutcome>> futurePreferenceActions = pcm.getOutcome(uimEvent.getUserId(), uimEvent.getAction());
                        logging.debug("Requested preference outcome");
                        List<IPreferenceOutcome> prefActions = futurePreferenceActions.get();
                        logging.debug("PCM returned: " + prefActions.size() + " outcomes");


                        Hashtable<IPreferenceOutcome, IDIANNEOutcome> prefOverlapping = new Hashtable<IPreferenceOutcome, IDIANNEOutcome>();
                        List<IOutcome> prefNonOverlapping = new ArrayList<IOutcome>();

                        for (IDIANNEOutcome d : dianneActions) {
                            IPreferenceOutcome p = exists(prefActions, d);
                            if (null == p) {
                                prefNonOverlapping.add(d);
                            } else {
                                prefOverlapping.put(p, d);
                            }
                        }

                        for (IPreferenceOutcome p : prefActions) {
                            IDIANNEOutcome d = exists(dianneActions, p);
                            if (null == d) {
                                prefNonOverlapping.add(p);
                            }
                        }

                        Enumeration<IPreferenceOutcome> prefEnum = prefOverlapping.keys();

                        while (prefEnum.hasMoreElements()) {
                            IPreferenceOutcome pref = prefEnum.nextElement();
                            IDIANNEOutcome dianne = prefOverlapping.get(pref);
                            prefNonOverlapping.add(resolvePreferenceConflicts(dianne, pref));
                        }

                        if (intentNonOverlapping.size() == 0 & prefNonOverlapping.size() == 0) {
                            logging.debug("Action Event-> Nothing to send to decisionMaker");
                            return;
                        } else {
                            for (int i = 0; i < prefNonOverlapping.size(); i++) {
                                logging.debug("Preference Outcome " + i + " :" + prefNonOverlapping.get(i));
                            }
                            for (int i = 0; i < intentNonOverlapping.size(); i++) {
                                logging.debug("Intent Outcome " + i + " :" + intentNonOverlapping.get(i));
                            }
                        }
                        decisionMaker.makeDecision(intentNonOverlapping, prefNonOverlapping);
                    } else {
                        logging.debug("event: " + event.geteventType() + " not a " + EventTypes.UIM_EVENT);
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                logging.debug("Thread of handleInternalEvent finished executing");
            }
        }.start();

    }

    private IUserIntentAction exists(List<IUserIntentAction> cauiActions, CRISTUserAction crist) {
        for (IUserIntentAction o : cauiActions) {
            if (this.outcomesMatch(o, crist)) {
                return o;
            }
        }
        return null;
    }

    private CRISTUserAction exists(List<CRISTUserAction> cristActions, IUserIntentAction caui) {

        for (CRISTUserAction o : cristActions) {
            if (this.outcomesMatch(o, caui)) {
                return o;
            }
        }

        return null;
    }

    private IDIANNEOutcome exists(List<IDIANNEOutcome> dianneActions, IPreferenceOutcome prefOutcome) {
        for (IDIANNEOutcome d : dianneActions) {
            if (this.outcomesMatch(d, prefOutcome)) {
                return d;
            }
        }
        return null;
    }


    private IPreferenceOutcome exists(List<IPreferenceOutcome> prefActions, IDIANNEOutcome dianneOutcome) {
        for (IPreferenceOutcome p : prefActions) {
            if (this.outcomesMatch(p, dianneOutcome)) {
                return p;
            }
        }
        return null;
    }

    private boolean outcomesMatch(IOutcome outcome1, IOutcome outcome2) {
        if (outcome1.getServiceID().getServiceInstanceIdentifier().equalsIgnoreCase(outcome2.getServiceID().getServiceInstanceIdentifier())) {
            if (outcome1.getparameterName().equalsIgnoreCase(outcome2.getparameterName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void handleExternalEvent(CSSEvent arg0) {
        // TODO Auto-generated method stub

    }

}
