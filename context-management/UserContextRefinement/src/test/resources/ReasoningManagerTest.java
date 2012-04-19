/*
 * Copyright (C) 2009-2010 PERSIST consortium
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * Component Maintainer: gspadotto@users.sourceforge.net
 */
package org.personalsmartspace.cm.reasoning.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.osgi.framework.BundleContext;
import org.personalsmartspace.cm.api.pss3p.ContextException;
import org.personalsmartspace.cm.api.pss3p.ContextModelException;
import org.personalsmartspace.cm.broker.api.platform.ICtxBroker;
import org.personalsmartspace.cm.broker.impl.PlatformCtxBrokerImpl;
import org.personalsmartspace.cm.broker.test.CtxBrokerTestUtils;
import org.personalsmartspace.cm.db.api.platform.ICtxDBManager;
import org.personalsmartspace.cm.db.impl.util.HibernateUtil;
import org.personalsmartspace.cm.model.api.pss3p.CtxAttributeTypes;
import org.personalsmartspace.cm.model.api.pss3p.CtxModelType;
import org.personalsmartspace.cm.model.api.pss3p.CtxOriginType;
import org.personalsmartspace.cm.model.api.pss3p.ICtxAttribute;
import org.personalsmartspace.cm.model.api.pss3p.ICtxAttributeIdentifier;
import org.personalsmartspace.cm.model.api.pss3p.ICtxEntity;
import org.personalsmartspace.cm.model.api.pss3p.ICtxEntityIdentifier;
import org.personalsmartspace.cm.model.api.pss3p.ICtxQuality;
import org.personalsmartspace.cm.reasoning.api.platform.ICtxRefiner;
import org.personalsmartspace.cm.reasoning.api.platform.IReasoningManager;
import org.personalsmartspace.cm.reasoning.bayesian.impl.BayesianInference;
import org.personalsmartspace.lm.api.IRule;
import org.personalsmartspace.lm.bayesian.impl.BayesianLearning;
import org.personalsmartspace.lm.bayesian.rule.BayesianRule;
import org.personalsmartspace.spm.identity.api.platform.IIdentityManagement;
import org.personalsmartspace.spm.identity.impl.StubIdentityManagement;



public class ReasoningManagerTest {
    
    private static final String _CTX_DEFAULT_ATTR_TYPE = CtxAttributeTypes.LOCATION+":10m";

    private ICtxRefiner<Rule4Test> ref_01 = new DummyInferrer();
    private ICtxRefiner<Rule4Test> ref_02 = new DummyInferrer();
    private ICtxRefiner<Rule4Test> ref_03 = new DummyInferrer();
    private ICtxRefiner<Rule4Test> ref_04 = new DummyInferrer();
    private ICtxRefiner<BayesianRule> bayesRefiner = new BayesianInference();
    @Mock
    private ICtxAttribute dummyAttribute = null;
    @Mock
    private BundleContext dummyBundleContext = null;
    @Mock 
    private ICtxEntityIdentifier dummyCtxId = null;
    @Mock
    private BayesianRule br = null;
    
    private static ICtxBroker ctxBroker = null;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        if (ReasoningManagerTest.ctxBroker == null)
            ReasoningManagerTest.ctxBroker = createCtxBroker();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        HibernateUtil.shutdown();
    }
    
    class DummyContextAttribute implements ICtxAttribute {
        
        class DummyContextQuality implements ICtxQuality{
            ICtxAttribute containingAttr = null;
            CtxOriginType origin = CtxOriginType.MANUALLY_SET;
            
            public DummyContextQuality(ICtxAttribute containingAttr) {
                super();
                this.containingAttr = containingAttr;
            }

            @Override
            public ICtxAttribute getAttribute() {
                return this.containingAttr;
            }

            @Override
            public long getFreshness() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Date getLastUpdate() {
                // TODO Auto-generated method stub
                return new Date();
            }

            @Override
            public CtxOriginType getOrigin() {
                return this.origin;
            }

            @Override
            public Double getPrecision() {
                // TODO Auto-generated method stub
                return new Double(0);
            }

            @Override
            public Double getUpdateFrequency() {
                // TODO Auto-generated method stub
                return new Double(0);
            }

            @Override
            public Double getSensitivity() {
                // TODO Auto-generated method stub
                return new Double(0);
            }

            @Override
            public void setOrigin(CtxOriginType origin) {
                this.origin = origin;
            }

            @Override
            public void setPrecision(Double precision) {
                // TODO Auto-generated method stub                
            }

            @Override
            public void setUpdateFrequency(Double updateFrequency) {
                // TODO Auto-generated method stub                
            }            

            @Override
            public void setSensitivity(Double sensitivity) {
                // TODO Auto-generated method stub                
            }            
        }
        
        ICtxQuality q = new DummyContextQuality(this);
        ICtxAttributeIdentifier attrId = this.getCtxIdentifier();
        
        @Override
        public Serializable getBlobValue(ClassLoader classLoader)
                throws ContextModelException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public byte[] getBlobValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ICtxAttributeIdentifier getCtxIdentifier() {
            return null;
        }

        @Override
        public Double getDoubleValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Integer getIntegerValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ICtxQuality getQuality() {
            return q;
        }

        @Override
        public ICtxEntityIdentifier getScope() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getSourceId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getStringValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Serializable getValue() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isHistoryRecorded() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setBlobValue(Serializable value)
                throws ContextModelException {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setDoubleValue(Double value) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setHistoryRecorded(boolean historyRecorded) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setIntegerValue(Integer value) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setSourceId(String sourceId) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setStringValue(String value) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void setValue(Serializable value) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getLocalServiceId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public CtxModelType getModelType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Long getObjectNumber() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getOperatorId() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Date getTimestamp() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public String getType() {            
            return ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE;
        }

        @Override
        public boolean isDynamic() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setDynamic(boolean dynamic) {
            // TODO Auto-generated method stub
            
        }
        
    }
    
    class DummyInferrer implements ICtxRefiner<Rule4Test> {

        @Override
        public Collection<ICtxAttribute> eval(ICtxAttribute attr, Rule4Test rule, IReasoningManager callback) {   
            Collection<ICtxAttribute> result = new LinkedList<ICtxAttribute>();
            result.add(dummyAttribute);
            return result;
        }

        @Override
        public Class<Rule4Test> getRuleType() {            
            return Rule4Test.class;
        }        
    }
    
    private ICtxRefiner dummyInferrer = new DummyInferrer();
    
    private ICtxAttribute dummyCtxAttribute = new DummyContextAttribute();
    
    public class Rule4Test implements IRule{
        String[] inferredAttrs = null;
        
        public Rule4Test(String[] inferredAttrs) {
            super();
            this.inferredAttrs = inferredAttrs;
        }
        
        @Override
        public Collection<String> getOutputTypes() {            
            return Arrays.asList(this.inferredAttrs);
        }

        @Override
        public Collection<String> getInputTypes() {
            return new HashSet<String>();
        }          
    }
        
    class ReasoningManager4Test extends ReasoningManager{
        public void setCtxBroker(ICtxBroker broker){
            this.ctxBroker = broker;
        }
        
        public void unsetCtxBroker(ICtxBroker broker){
            this.ctxBroker = null;
        }
        
        public void setCache(IRmAttributeCache instance){
            //this.cache = instance;
        }
    }
    
    class Cache4Test extends  RmAttributeCache{
        public void setCtxBroker(ICtxBroker brokerInstance){
            this.ctxBroker = brokerInstance;
        }        
    }
    
    ReasoningManager4Test rm = new ReasoningManager4Test();
    String[] inferredAttrs1 = {ReasoningManager.getFilteredCtxType(ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE),"BB1","CC1","DD1"},
             inferredAttrs2 = {"AA2","BB2","CC2","DD2"},
             inferredAttrs3 = {"AA3","BB3","CC3","DD3"},
             inferredAttrs4 = {"AA4","BB4","CC4","DD4"};
    Rule4Test   r1 = new Rule4Test(inferredAttrs2),
                r2 = new Rule4Test(inferredAttrs2),
                r3 = new Rule4Test(inferredAttrs3),
                r4 = new Rule4Test(inferredAttrs4);
    
    ICtxEntity myTestEntity = null;
    ICtxAttribute myTestAttribute = null;
    
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.rm.setCtxBroker(ReasoningManagerTest.ctxBroker);
        this.rm.bayesianLearning = new BayesianLearning();
        when(this.br.getOutputTypes()).thenReturn(Arrays.asList(inferredAttrs1));
        //br.getRule should return some sort of XML document.
        when(this.br.getRule()).thenReturn("<hello></hello>");
        this.rm.addRule(br);
        this.rm.addRule(r1);
        this.rm.addRule(r2);
        this.rm.addRule(r3);
        this.rm.addRule(r4);      
        this.rm.setAvailableRefiner(bayesRefiner);
        this.rm.setAvailableRefiner(dummyInferrer);
        this.rm.setAvailableRefiner(ref_01);
        this.rm.setAvailableRefiner(ref_02);
        this.rm.setAvailableRefiner(ref_03);
        Cache4Test cache = new Cache4Test();
        cache.setCtxBroker(ReasoningManagerTest.ctxBroker);
        this.rm.setCache(cache);
        myTestEntity = this.rm.ctxBroker.createEntity("myTestEntity");
        myTestAttribute = this.rm.ctxBroker.createAttribute(myTestEntity.getCtxIdentifier(), _CTX_DEFAULT_ATTR_TYPE);        
    }

    @After
    public void tearDown() throws Exception {
        this.rm.availableLocalRefiners.clear();
        this.rm.availableRules.clear();
        this.rm.ruleType2ICtxRefiner.clear();
        if (myTestEntity!=null && this.rm.ctxBroker!=null)
            this.rm.ctxBroker.remove(myTestEntity.getCtxIdentifier());
    }  
    
    @Test 
    public final void testSetAvailableInferrer(){
       assertFalse(this.rm.availableLocalRefiners.contains(ref_04));
       this.rm.setAvailableRefiner(ref_04);
       assertTrue(this.rm.availableLocalRefiners.contains(ref_04));       
    }
    
    @Test 
    public final void testUnsetAvailableInferrer(){
       assertTrue(this.rm.availableLocalRefiners.contains(ref_01));
       this.rm.unsetAvailableRefiner(ref_01);
       assertFalse(this.rm.availableLocalRefiners.contains(ref_01));
    }
    
    @Test
    public final void testAddRule() {
        Set<IRule> listOfRules1 = this.rm.mapper.outputTypes2Rules.get(ReasoningManager.getFilteredCtxType(ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE));
        assertNotNull(listOfRules1);
        assertTrue(listOfRules1.size() == 1);
        String[] inferredAttrs1b = {ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE, "BBX", "CCX", "DDX" };
        Rule4Test r1bis = new Rule4Test(inferredAttrs1b);
        this.rm.addRule(r1bis);
        listOfRules1 = this.rm.mapper.outputTypes2Rules.get(ReasoningManager.getFilteredCtxType(ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE));
        assertNotNull(listOfRules1);
        assertTrue(listOfRules1.size() == 2);
    }
    
    @Test
    public final void testRemoveRule() {
        this.rm.removeRule(r1);
        Set<IRule> relevantFoundRules = this.rm.mapper.outputTypes2Rules.get(ReasoningManager.getFilteredCtxType(ReasoningManagerTest._CTX_DEFAULT_ATTR_TYPE));
        assertNotNull(relevantFoundRules);
        assertTrue(relevantFoundRules.size()==1);        
        assertFalse(this.rm.availableRules.contains(r1));
    }
    
    @Test
    public final void testSetUnsetCtxBroker() {        
        this.rm.setCtxBroker(ReasoningManagerTest.ctxBroker);
        assertNotNull(this.rm.ctxBroker);
        this.rm.unsetCtxBroker(ReasoningManagerTest.ctxBroker);
        assertNull(this.rm.ctxBroker);
    }
   
    @Test
    public final void testGetEvaluationInputs() {        
        try {
            ICtxEntity myEnt = ctxBroker.createEntity("CtxReasoningTest");
            ICtxEntityIdentifier myEntId = myEnt.getCtxIdentifier();
            ICtxAttribute myAttr = ctxBroker.createAttribute(myEntId, _CTX_DEFAULT_ATTR_TYPE, "MyValue");
            ICtxAttribute retrievedMyAttr = (ICtxAttribute) this.rm.ctxBroker.retrieve(myAttr.getCtxIdentifier());
            assertTrue(myAttr.equals(retrievedMyAttr));            
            Collection<ICtxAttribute> foundAttributes = this.rm.getEvaluationInputs(myEnt.getCtxIdentifier(), _CTX_DEFAULT_ATTR_TYPE);
            assertFalse(foundAttributes.isEmpty());            
            assertTrue(foundAttributes.contains(myAttr));
        } catch (ContextException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a platform context broker instance.
     * 
     * Only call once per test suite (i.e. in setupBeforeClass() because it
     * creates a new context db manager each time.
     * 
     * @return An instance of {@link ICtxBroker}
     * @throws Exception if an error occurs
     */
    private static final ICtxBroker createCtxBroker() throws Exception {
        final ICtxDBManager dbManager = CtxBrokerTestUtils.createCtxDbManager();
        final IIdentityManagement identityManagement = new StubIdentityManagement();
        return new PlatformCtxBrokerImpl(dbManager, identityManagement,
                CtxBrokerTestUtils.createServiceDiscoveryMock(),
                CtxBrokerTestUtils.createReasoningManagerMock(false));
    }
}
