package org.personalsmartspace.cm.reasoning.bayesian.impl;

import org.personalsmartspace.cm.broker.api.platform.ICtxBroker;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.personalsmartspace.cm.api.pss3p.ContextException;
import org.personalsmartspace.cm.api.pss3p.ContextModelException;
import org.personalsmartspace.cm.model.api.platform.CtxAttributeTypes;
import org.personalsmartspace.cm.model.api.pss3p.CtxOriginType;
import org.personalsmartspace.cm.model.api.pss3p.ICtxAttribute;
import org.personalsmartspace.cm.model.api.pss3p.ICtxEntityIdentifier;
import org.personalsmartspace.cm.model.api.pss3p.ICtxQuality;
import org.personalsmartspace.cm.reasoning.api.platform.ICtxRefiner;
import org.personalsmartspace.cm.reasoning.api.platform.IReasoningManager;
import org.personalsmartspace.cm.reasoning.bayesian.solving.JunctionTree;
import org.personalsmartspace.cm.reasoning.bayesian.structures.DAG;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Edge;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Node;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Probability;
import org.personalsmartspace.cm.reasoning.bayesian.structures.conversion.BalLInputInterface;
import org.personalsmartspace.cm.reasoning.bayesian.structures.conversion.GenieJava;
import org.personalsmartspace.lm.api.IRule;
import org.personalsmartspace.lm.bayesian.fakeRule.DAGtoBall;
import org.personalsmartspace.lm.bayesian.rule.BayesianRule;
import org.personalsmartspace.log.impl.PSSLog;



@Component(name="Bayesian Inference", immediate=true)
@Service(value=ICtxRefiner.class)
public class BayesianInference implements ICtxRefiner<BayesianRule> {

	@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC)
	protected ICtxBroker broker;
	private BundleContext bundleContext;
	private PSSLog logger = new PSSLog(this);

	protected void activate(ComponentContext context) {
		this.bundleContext = context.getBundleContext();
	//	test();
		
		BayesianRule statusRule = getStatusRule();
		BayesianRule taskRule = getTaskRule();
/*		ArrayList<String> ctP = new ArrayList<String>();
		ctP.add("closeToPSS");
		ProximityRule closeToPSS = new ProximityRule(ctP);
		ArrayList<String> csttP = new ArrayList<String>();
		csttP.add("closestToPSS");
		ProximityRule closestToPSS = new ProximityRule(csttP);

		ArrayList<String> ctS = new ArrayList<String>();
		ctS.add("closeToService");
		ProximityRule closeToService = new ProximityRule(ctS);
		ArrayList<String> csttS = new ArrayList<String>();
		csttS.add("closestToService");
		ProximityRule closestToService = new ProximityRule(csttS);

        this.bundleContext.registerService(IRule.class.getName(), closeToPSS, null);
        this.bundleContext.registerService(IRule.class.getName(), closestToPSS, null);
        this.bundleContext.registerService(IRule.class.getName(), closeToService, null);
*/
		this.bundleContext.registerService(new String[]{IRule.class.getName(), BayesianRule.class.getName()}, statusRule, null);
		logger .debug("Input="+statusRule.getInputTypes());
		logger.debug("Output="+statusRule.getOutputTypes());

		this.bundleContext.registerService(new String[]{IRule.class.getName(), BayesianRule.class.getName()}, taskRule, null);
	}
	
	private String readFileAsString(String filePath) throws java.io.IOException{
		InputStream stream = null;
	    BufferedInputStream f = null;
	    StringBuffer file = new StringBuffer();
		try {
			// logger
			// .debug("org/personalsmartspaces/cm/reasoning/proximity/impl/"+relativePathOSGI+"diffusionConfiguration.properties");
			stream = this.getClass().getResourceAsStream(filePath);
			logger.debug("loaded file = " + stream); 
		    byte[] buffer = new byte[(int) new File(filePath).length()];

	        f = new BufferedInputStream(stream);
	        
	        byte[] contents = new byte[1024];
	        int bytesRead=0;
	        while( (bytesRead = f.read(contents)) != -1)
	        	file.append(new String(contents, 0, bytesRead));
	    } catch (IOException e){
	    	logger.error("Error while reading File\n"+e.getLocalizedMessage());
	    }
	        finally {
	        if (f != null) try { f.close(); stream.close();} catch (IOException ignored) { }
	    }
	    return new String(file);
	}

	private BayesianRule getTaskRule() {
//		DAG taskRule = GenieJava.genieToJava("TaskRule.xdsl");
		
		String ruleString = "";
		try {
			ruleString = readFileAsString("/TaskRule.xdsl");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    BayesianRule result = new BayesianRule();
	    result.setRule(ruleString);
	    ArrayList<String> outputs = new ArrayList<String>();
	    outputs.add(CtxAttributeTypes.TASK);
	    ArrayList<String> inputs = new ArrayList<String>();
	    inputs.add(CtxAttributeTypes.ACTIVITY);
	    inputs.add(CtxAttributeTypes.AGENDA_SLOT);
	    inputs.add("Microphone");
//	    inputs.add(CtxAttributeTypes.SYMBOLIC_LOCATION);
	
	    result.setInputs(inputs);
	    result.setOutputs(outputs);
		return result;
	}

	private BayesianRule getStatusRule() {
		
		String ruleString = "";
		try {
			ruleString = readFileAsString("/AvailabilityRule.xdsl");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
        BayesianRule result = new BayesianRule();
        result.setRule(ruleString);
        ArrayList<String> outputs = new ArrayList<String>();
        outputs.add(CtxAttributeTypes.STATUS);
        ArrayList<String> inputs = new ArrayList<String>();
        inputs.add(CtxAttributeTypes.ACTIVITY);
        inputs.add("Caller");
        inputs.add("Used_Services");
        inputs.add("Time");
        inputs.add("Working_Day");
        inputs.add("Movement");
        inputs.add(CtxAttributeTypes.SYMBOLIC_LOCATION);
        inputs.add("Microphone");

        result.setInputs(inputs);
        result.setOutputs(outputs);
		return result;
	}

	@Override
	public Collection<ICtxAttribute> eval(ICtxAttribute inferredAttribute, BayesianRule arg0, IReasoningManager caller) {
		DAG graph = GenieJava.xDSLstringToJava(arg0.getRule());
        JunctionTree jtree = new JunctionTree(graph);
        jtree.initialiseJTree();

        HashSet<String> inputs = new HashSet<String>(arg0.getInputTypes());
        boolean evidenceAvailable = false;
        
        HashMap<String,Node> nodeMap = new HashMap<String,Node>();
        Node[] nodes = graph.getNodes();
        for (int i=0;i<nodes.length;i++) {
        	nodeMap.put(nodes[i].getName().toLowerCase(), nodes[i]);
        }
        
        /* add Evidence */     
        ICtxEntityIdentifier target = inferredAttribute.getScope();
        Collection<ICtxAttribute> temp = null;
        ICtxAttribute ctxAttribute = null;
        ICtxQuality iqual = null;

        if (inputs!=null && inputs.size()!=0)
	        for (String ctxType : inputs) {
				temp = caller.getEvaluationInputs(target,ctxType);
	        	if (temp == null || temp.size()==0){
	        		try {
						temp = caller.getEvaluationInputs(broker.retrieveDevice().getCtxIdentifier(),ctxType);
						logger.debug("From device entity: "+temp);
					} catch (ContextException e) {
						e.printStackTrace();
					}
	        	}
	        	if (temp != null && temp.size()!=0){
	        		ctxAttribute = temp.iterator().next();  //we assume there is only one. if there are several, the first one will be used
	        		Serializable value = null;
		        	if (ctxAttribute != null){
		        		value = ctxAttribute.getStringValue();
			        	if (value==null) 
			        		try{
			        			value = (String)ctxAttribute.getBlobValue(this.getClass().getClassLoader());
			        		}
			        		catch (ContextModelException e)
			        		{
			        			logger.debug("Error retrieving Blob value for attribute "+ctxType);
			        		}
			            if (value== null) continue;
		
			            iqual = ctxAttribute.getQuality();
			            boolean found = false;
		            	if (iqual!=null && iqual.getPrecision() != null && !(iqual.getPrecision()>1)){
		            		found = jtree.addEvidence(nodeMap.get(ctxAttribute.getType().toLowerCase()), value.toString(),iqual.getPrecision());
		            		if (found) logger.debug("Adding soft Evidence for "+ctxAttribute.getType().toLowerCase()+": "+value.toString()+", precision="+iqual.getPrecision());
		            	}
		            	else{
		            		found = jtree.addEvidence(nodeMap.get(ctxAttribute.getType().toLowerCase()), value.toString());
		            		if (found) logger.debug("Adding hard Evidence for "+ctxAttribute.getType().toLowerCase()+": "+value.toString());
		            	}
		
		            	if (found) evidenceAvailable = true;
		        	}
	        	}
	        }
       /* evidence added */
        
        if (!evidenceAvailable) jtree.propagate();

        HashSet<ICtxAttribute> result = new HashSet<ICtxAttribute>();
        
        Probability marginalisation = jtree.getMarginalized(inferredAttribute.getType(), 1);
        String resultValue = marginalisation.getStates()[0];
       	inferredAttribute.setStringValue(resultValue);
       	inferredAttribute.setSourceId("BayesianInference");
       	iqual = inferredAttribute.getQuality();
       	iqual.setOrigin(CtxOriginType.INFERRED);
       	iqual.setPrecision(marginalisation.getProbability());
       	
       	logger.debug("inferred Attribute value = "+inferredAttribute.getStringValue());
       	
       	result.add(inferredAttribute);
        return result;        
	}

	@Override
	public Class<BayesianRule> getRuleType() {
		return BayesianRule.class;
	}

}
