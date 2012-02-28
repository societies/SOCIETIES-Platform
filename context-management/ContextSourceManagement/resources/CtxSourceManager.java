/**
 * Copyright 2009 PERSIST consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */
package org.personalsmartspace.cm.source.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferenceStrategy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.personalsmartspace.cm.api.pss3p.ContextDBException;
import org.personalsmartspace.cm.api.pss3p.ContextDataException;
import org.personalsmartspace.cm.api.pss3p.ContextException;
import org.personalsmartspace.cm.api.pss3p.ContextModelException;
import org.personalsmartspace.cm.db.api.platform.ICtxDBManager;
import org.personalsmartspace.cm.model.api.pss3p.CtxOriginType;
import org.personalsmartspace.cm.model.api.pss3p.ICtxAttribute;
import org.personalsmartspace.cm.model.api.pss3p.ICtxEntity;
import org.personalsmartspace.cm.model.api.pss3p.ICtxEntityIdentifier;
import org.personalsmartspace.cm.model.api.pss3p.ICtxIdentifier;
import org.personalsmartspace.cm.model.api.pss3p.ICtxQuality;
import org.personalsmartspace.cm.source.api.pss3p.ICtxSourceManager;
import org.personalsmartspace.cm.source.api.pss3p.callback.ICtxSource;
import org.personalsmartspace.log.impl.PSSLog;
import org.personalsmartspace.sre.api.pss3p.PssConstants;

/**
 * @author <a href="mailto:korbinianf@users.sourceforge.net">Korbinian
 *         Frank</a> (DLR)
 */
@Component(name = "cm.source", enabled = false, immediate = true)
@Service(ICtxSourceManager.class)
@Properties({
    @Property(name=PssConstants._FW_COMPONENT, value="true"),
    @Property(name=PssConstants._FW_ADVERTISE_SERVICE, value="true"),
    @Property(name=PssConstants._FW_ONTOLOGY_URI, value="urn:i:am:a:framework:service")
})
@References({
    @Reference(
        name = "dbMgr",
        referenceInterface = ICtxDBManager.class,
        cardinality = ReferenceCardinality.MANDATORY_UNARY,
        policy = ReferencePolicy.DYNAMIC,
        strategy = ReferenceStrategy.EVENT,
        bind="setDBMgr",
        unbind="unsetDBMgr")
})
public class CtxSourceManager implements ICtxSourceManager{

    /** Reference to the Context DB Mgmt service. */
    private ICtxDBManager dbMgr;
    
    private int counter = 0;
	
    private final PSSLog log = new PSSLog(this);
    private final String sensor = "CONTEXT_SOURCE";

    public CtxSourceManager() {
        if (this.log.isDebugEnabled())
            this.log.debug("Instantiating " + this);
    }

    /**
     * @see org.personalsmartspace.cm.source.api.pss3p.ICtxSourceManager
     */

//    public void register(String name, String contextType, ICtxSource caller) {
//
//    }
    public synchronized void register(String name, String contextType, ICtxSource source){
        if (dbMgr == null) {
            log.error("Could not register " + contextType + ": DB Manager canot be found");
            return;
        }

    	String id=name+counter++;
        ICtxEntity fooEnt;

        try {
	        List<ICtxEntityIdentifier> shadowEntities = dbMgr.lookupEntities(sensor, "CtxSourceId", id);
	    	if (shadowEntities.size()>0){
	    		log.error("Sensor-ID "+id+" is not unique. Sensor could not be registered");
	    		source.handleErrorMessage("Sensor-ID not unique. Registration failed!");
	    	}

            fooEnt = dbMgr.createEntity(sensor);
            ICtxAttribute nameAttr = dbMgr.createAttribute(
            		fooEnt.getCtxIdentifier(), "CtxSourceId", id);
            dbMgr.createAttribute(fooEnt.getCtxIdentifier(), "CtxType", contextType);
                        
            log.info("Created entity: " + fooEnt);
        } catch (ContextDBException e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        }
        source.handleCallbackString(id);
    }

    /**
     * @see org.personalsmartspace.cm.source.api.pss3p.ICtxSourceManager
     */
    public synchronized boolean unregister(String identifier){
        if (dbMgr == null) {
            log.error("Could not unregister " + identifier + ": DB Manager cannot be found");
            return false;
        }

    	List<ICtxEntityIdentifier> shadowEntities;
    	ICtxIdentifier shadowEntity = null;
		try {
			shadowEntities = dbMgr.lookupEntities(sensor, "CtxSourceId", identifier);
	    	if (shadowEntities.size()>1){
	    		log.debug("Sensor-ID "+identifier+" is not unique. Sensor could not be unregistered");
	    		//throw new Exception("Unregistering failure due to ambiguity.");
	    	}
	    	else if (shadowEntities.isEmpty()){
	    		log.debug("Sensor-ID "+identifier+" is not available. Sensor could not be unregistered");
	    		//throw new Exception("Unregistering failure due to missing Registration.");
	    	}
	    	else
	    		shadowEntity = shadowEntities.get(0);

	    	dbMgr.remove(shadowEntity);
		} catch (ContextDBException e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		} catch (Exception e) {
			//e.printStackTrace();
			log.error(e.getMessage());
		}
        return true;
    }

    /**
     * @see org.personalsmartspace.cm.source.api.pss3p.ICtxSourceManager
     */
    @Override
    public void sendUpdate (String identifier, Serializable data){
    	this.sendUpdate (identifier, data, null);
    }

    /**
     * @see org.personalsmartspace.cm.source.api.pss3p.ICtxSourceManager
     */
    @Override
    public void sendUpdate (String identifier, Serializable data, ICtxEntity owner){
        if (this.dbMgr == null) {
            this.log.error("Could not handle update from " + identifier + ": Context DB Manager is not available");
            return;
        }
        if (this.log.isDebugEnabled())
            this.log.debug("Sending update: id=" + identifier+ ", data=" + data + ", ownerEntity=" + owner);
    	List<ICtxEntityIdentifier> shadowEntities;
    	ICtxEntityIdentifier shadowEntityID = null;
    	Set<ICtxAttribute> attrs = null;
    	ICtxEntity shadowEntity =null;

        try {
            String type = "";
            ICtxAttribute dataAttr;
            ICtxQuality quality;

            shadowEntities = dbMgr.lookupEntities(sensor, "CtxSourceId", identifier);
            if (shadowEntities.size() > 1) {
                if (this.log.isDebugEnabled())
                    log.debug("Sensor-ID " + identifier + " is not unique. No information stored.");
                return;
                // throw new
                // Exception("Ambiguity: more than one context source with this identifier exists.");
            } else if (shadowEntities.isEmpty()) {
                if (this.log.isDebugEnabled())
                    log.debug("Sensor-ID " + identifier + " is not available. No information stored.");
                return;
                // throw new
                // Exception("Sending failure due to missing Registration.");
            } else {
                shadowEntityID = shadowEntities.get(0);
                shadowEntity = (ICtxEntity) dbMgr.retrieve(shadowEntityID);
            }

            attrs = shadowEntity.getAttributes("CtxType");
            if (attrs != null && attrs.size() > 0)
                type = attrs.iterator().next().getStringValue();
            else
                type = "data";

	        /* update Context Information at Context Source Shadow Entity 
            attrs = shadowEntity.getAttributes("data");
            if (attrs != null && attrs.size()>0)
            	dataAttr = attrs.iterator().next();
            else dataAttr = dbMgr.createAttribute(shadowEntityID, "data");

            if (data instanceof String) updateData((String)data,dataAttr);
            else dataAttr.setBlobValue(data);
            dataAttr.setSourceId(identifier);
            dataAttr.setHistoryRecorded(true);

            quality = dataAttr.getQuality();
	        quality.setOrigin(CtxOriginType.SENSED);

	        dbMgr.update(dataAttr);*/

	        /* update Context Information with Information Owner Entity */
            if (owner == null) {
                try {
                    owner = dbMgr.retrieveDevice();
                } catch (ContextDBException e) {
                    log.error("Could not handle update from " + identifier
                            + ": Could not retrieve device entity: "
                            + e.getLocalizedMessage(), e);
                    return;
                }
            }
	    attrs = owner.getAttributes(type, identifier);
            if (attrs.size()>0)
            	dataAttr = attrs.iterator().next();  //TODO get only those of the same kind
            else{
            	dataAttr = dbMgr.createAttribute(owner.getCtxIdentifier(), type);
            	dataAttr.setSourceId(identifier);
            }
            if (this.log.isDebugEnabled())
                this.log.debug("dataAttr=" + dataAttr);
            // Update QoC information.
            quality = dataAttr.getQuality();
            quality.setOrigin(CtxOriginType.SENSED);
            // Set history recorded flag.
            dataAttr.setHistoryRecorded(true);
            // Update attribute.
            updateData(data, dataAttr);
        } catch (ContextException e) {
            log.error("Could not handle update from " + identifier
                    + ": " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void sendUpdate(String identifier, Serializable data, ICtxEntity owner, 
            boolean inferred, double precision, double frequency) {
        if (this.dbMgr == null) {
            this.log.error("Could not handle update from " + identifier
                    + ": Context DB Manager is not available");
            return;
        }
        if (this.log.isDebugEnabled())
            this.log.debug("Sending update: id=" + identifier + ", data=" + data
                    + ", ownerEntity=" + owner + ", inferred=" + inferred
                    + ", precision=" + precision + ", frequency=" + frequency);
        List<ICtxEntityIdentifier> shadowEntities;
        ICtxEntityIdentifier shadowEntityID = null;
        Set<ICtxAttribute> attrs = null;
        ICtxEntity shadowEntity = null;

        try {
            shadowEntities = dbMgr.lookupEntities(sensor, "CtxSourceId",
                    identifier);
            if (shadowEntities.size() > 1) {
                if (this.log.isDebugEnabled())
                    this.log.debug("Sensor-ID " + identifier + " is not unique. No information stored.");
                return;
                // throw new
                // Exception("Unregistering failure due to ambiguity.");
            } else if (shadowEntities.isEmpty()) {
                if (this.log.isDebugEnabled())
                    this.log.debug("Sensor-ID " + identifier + " is not available. No information stored.");
                return;
                // throw new
                // Exception("Unregistering failure due to missing Registration.");
            } else {
                shadowEntityID = shadowEntities.get(0);
                shadowEntity = (ICtxEntity) dbMgr.retrieve(shadowEntityID);
            }

            String type = "";
            attrs = shadowEntity.getAttributes("CtxType");
            if (attrs != null && attrs.size() > 0)
                type = attrs.iterator().next().getStringValue();
            else
                type = "data";
            if (this.log.isDebugEnabled())
                this.log.debug("type is " + type);

            ICtxAttribute dataAttr;
            ICtxQuality quality;
            
	        /* update Context Information at Context Source Shadow Entity 
	    	attrs = shadowEntity.getAttributes("data");
            if (attrs != null && attrs.size()>0)
            	dataAttr = attrs.iterator().next();
            else dataAttr = dbMgr.createAttribute(shadowEntityID, "data");

            if (arg1 instanceof String) updateData((String)arg1,dataAttr);
            else dataAttr.setBlobValue(arg1);
            dataAttr.setSourceId(arg0);
            dataAttr.setHistoryRecorded(true);

            quality = dataAttr.getQuality();
            if (inferred) quality.setOrigin(CtxOriginType.INFERRED);
            else quality.setOrigin(CtxOriginType.SENSED);
            quality.setPrecision(precision);
            quality.setUpdateFrequency(frequency);

	        dbMgr.update(dataAttr);
	        */
	        
            /* update Context Information with Information Owner Entity */
            if (owner == null) {
                try {
                    owner = dbMgr.retrieveDevice();
                } catch (ContextDBException e) {
                    log.error("Could not handle update from " + identifier
                            + ": Could not retrieve device entity: "
                            + e.getLocalizedMessage(), e);
                    return;
                }
            }
            attrs = owner.getAttributes(type, identifier);
            if (attrs.size() > 0)
                dataAttr = attrs.iterator().next();
            else {
                dataAttr = dbMgr.createAttribute(owner.getCtxIdentifier(), type);
                dataAttr.setSourceId(identifier);
            }
            if (this.log.isDebugEnabled())
                this.log.debug("dataAttr=" + dataAttr);
            // Update QoC information.
            quality = dataAttr.getQuality();
            if (inferred)
                quality.setOrigin(CtxOriginType.INFERRED);
            else
                quality.setOrigin(CtxOriginType.SENSED);
            quality.setPrecision(precision);
            quality.setUpdateFrequency(frequency);
            // Set history recorded flag.
            dataAttr.setHistoryRecorded(true);
            // Update attribute.
            updateData(data, dataAttr);
        } catch (ContextException e) {
            log.error("Could not handle update from " + identifier
                    + ": " + e.getLocalizedMessage(), e);
        }
    }

    private void updateData(Serializable value, ICtxAttribute attr)
            throws ContextDBException, ContextModelException {
        if (value instanceof String)
            attr.setStringValue((String) value);
        else if (value instanceof Integer)
            attr.setIntegerValue((Integer) value);
        else if (value instanceof Double)
            attr.setDoubleValue((Double) value);
        else
            attr.setBlobValue(value);

        try {
            dbMgr.update(attr);
        } catch (ContextDataException cde) {
            // If the value is a String attempt to store it as a blob.
            if (value instanceof String) {
                if (this.log.isDebugEnabled())
                    this.log.debug("Attempting to store String value as a blob");
                attr.setBlobValue(value);
                dbMgr.update(attr);
            } else {
                throw cde;
            }
        }
    }
    
    protected synchronized void setDBMgr(ICtxDBManager dbMgr) {
        if (this.log.isDebugEnabled())
            this.log.debug("Binding " + dbMgr);
        this.dbMgr = dbMgr;
    }
    
    protected synchronized void unsetDBMgr(ICtxDBManager dbMgr) {
        if (this.log.isDebugEnabled())
            this.log.debug("Unbinding " + dbMgr);
        this.dbMgr = null;
    }
    
    protected synchronized void activate(ComponentContext compContext) throws Exception {
        this.log.info("Activating " + this);
    }
    
    protected synchronized void deactivate(ComponentContext compContext) throws Exception {
        this.log.info("Deactivating " + this);
    }
}