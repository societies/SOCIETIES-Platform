package org.personalsmartspace.cm.reasoning.bayesian;


import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.personalsmartspace.cm.model.api.platform.CtxAttributeTypes;
import org.personalsmartspace.cm.reasoning.bayesian.solving.JunctionTree;
import org.personalsmartspace.cm.reasoning.bayesian.structures.DAG;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Edge;
import org.personalsmartspace.cm.reasoning.bayesian.structures.HasProbabilityTable;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Node;
import org.personalsmartspace.cm.reasoning.bayesian.structures.conversion.GenieJava;
import org.personalsmartspace.lm.bayesian.rule.BayesianRule;
import org.personalsmartspace.log.impl.PSSLog;



public class InferenceTest {
	private DAG graph;
	private BayesianRule status;
	
	private PSSLog log = new PSSLog(this);
	
	private String eclipsePath = "src/main/resources/";

	@Before
	public void setUp() {
	}

	@Test
    public void testDAGtoBall() {
		
		String ruleString = "";
		try {
			ruleString = readFileAsString(eclipsePath+"AvailabilityRule.xdsl");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        status = new BayesianRule();
        status.setRule(ruleString);
	    ArrayList<String> outputs = new ArrayList<String>();
	    outputs.add(CtxAttributeTypes.STATUS);
	    ArrayList<String> inputs = new ArrayList<String>();
	    inputs.add("ActivityType");
	    inputs.add("Caller");
	    inputs.add("Used_Services");
	    inputs.add("Time");
	    inputs.add("Working_Day");
	    inputs.add(CtxAttributeTypes.ACTIVITY);
	    inputs.add("Noise_Level");
	    inputs.add(CtxAttributeTypes.SYMBOLIC_LOCATION);
	    inputs.add("Microphone");

        status.setInputs(inputs);
        status.setOutputs(outputs);
        System.out.println("Status Rule "+status+" correctly produced.");
        assert(status!=null);
	}
	
	@Test
    public void testBalLtoDAG() {

		try{
			setGraph();
		}
		catch (Exception e){
			e.printStackTrace();
			fail();
		}

        System.out.println("Status BN ((BN==null)=="+(graph==null)+") correctly produced.");
		assert(graph!=null);
    
	
	}

	private void setGraph() {
		String ruleString = "";
		try {
			ruleString = readFileAsString(eclipsePath+"AvailabilityRule.xdsl");
		} catch (IOException e) {
			e.printStackTrace();
		}
	    status = new BayesianRule();
	    status.setRule(ruleString);
	    ArrayList<String> outputs = new ArrayList<String>();
	    outputs.add(CtxAttributeTypes.STATUS);
	    ArrayList<String> inputs = new ArrayList<String>();
	    inputs.add("ActivityType");
	    inputs.add("Caller");
	    inputs.add("Used_Services");
	    inputs.add("Time");
	    inputs.add("Working_Day");
	    inputs.add(CtxAttributeTypes.ACTIVITY);
	    inputs.add("Noise_Level");
	    inputs.add(CtxAttributeTypes.SYMBOLIC_LOCATION);
	    inputs.add("Microphone");
	
	    status.setInputs(inputs);
	    status.setInputs(outputs);
		graph = GenieJava.genieToJava(eclipsePath+"AvailabilityRule.xdsl");
	}

	@Test
    public void testAvailabilityInference() {
		setGraph();

        JunctionTree jtree = new JunctionTree(graph);
        jtree.initialiseJTree();

        boolean evidenceAvailable = false;
        if (!evidenceAvailable) jtree.propagate();
        
        System.out.println("Value of Availability = "+jtree.getMarginalized(CtxAttributeTypes.STATUS, 1));
		assert(jtree.getMarginalized(CtxAttributeTypes.STATUS, 1)!=null);
	
	}
	
	@Test
    public void testTaskInference() {
		graph = GenieJava.genieToJava(eclipsePath+"TaskRule.xdsl");
		if(log.isDebugEnabled())
			for (Node h : graph.getNodes())
				log.debug(h.getName());
		log.info("Rule created");
		
        JunctionTree jtree = new JunctionTree(graph);
        jtree.initialiseJTree();
		log.info("JTree initialised");
		
		log.info("Evidence node found: "+jtree.getNode(CtxAttributeTypes.ACTIVITY));
		if (jtree.getNode(CtxAttributeTypes.ACTIVITY)==null) fail();
		
		jtree.addEvidence((Node)jtree.getNode(CtxAttributeTypes.ACTIVITY), "Standing");
		jtree.addEvidence((Node)jtree.getNode(CtxAttributeTypes.AGENDA_SLOT), new String[] {"YourSlot","NotYours"},new double[]{0.8,0.2});
		log.info("Evidence added");
        jtree.propagate();
		log.info("... and propagated");
        
        System.out.println("Value of Task = "+jtree.getMarginalized(CtxAttributeTypes.TASK, 1));
		assert(jtree.getMarginalized(CtxAttributeTypes.TASK, 1)!=null);
	
	}	
	
	@Test
	public void testGenieToJava(){
		DAG g = getStatusRule();
		GenieJava.javaToGenie(eclipsePath+"AvailabilityRule.xdsl", g);
		assert(true);
	}
	
	
	@After
	public void tearDown() throws Exception {
	}
	
	private DAG getStatusRule() {		
		String[] as = {"busy", "free"};
		String[] bs = {"DangerousActivity", "IntellectualActivity", "CommunicationActivity", "IdleActivity", "PassiveActivity"};
		String[] cs = {"none", "Family", "Boss", "unknown"};
		String[] ds = {"WordProcessor", "VoIP", "Newscast", "NoActivity"};
		String[] es = {"morning", "afternoon", "evening", "night"};
		String[] fs = {"yes", "no"};
		String[] gs = {"Walking", "Standing", "Sitting"};
		String[] hs = {"Home", "Office", "MeetingRoom", "Outdoor"};
		String[] is = {"Noisy", "Conversation", "Quiet"};
		String[] js = {"high", "medium", "low"};
		String[] ks = {"Home", "Office", "MeetingRoom", "Outdoor"};
		String[] ls = {"Loud", "Medium", "Silent"};
		
		Node a = new Node(CtxAttributeTypes.STATUS,as);
		Node b = new Node("ActivityType",bs);
		Node c = new Node("Caller",cs);
		Node d = new Node("Used_Services",ds);
		Node e = new Node("Time",es);
		Node f = new Node("Working_Day",fs);//def);
		Node g = new Node(CtxAttributeTypes.ACTIVITY,gs);
		Node h = new Node("Location",hs);
		Node i = new Node("Noise_Level",is);
		Node j = new Node("MovementSensor",js);
		Node k = new Node(CtxAttributeTypes.SYMBOLIC_LOCATION,ks);
		Node l = new Node("Microphone",ls);


		/*
		 * ONLY the order of constructing edges determines the order of parent-configurations in probability tables!!!
		 */
		Edge ca = new Edge(c,a);
		Edge ba = new Edge(b,a);
		Edge bd = new Edge(b,d);
		Edge fb = new Edge(f,b);
		Edge eb = new Edge(e,b);
		Edge bg = new Edge(b,g);
		Edge bh = new Edge(b,h);
		Edge bi = new Edge(b,i);
		Edge hj = new Edge(h,j);
		Edge gj = new Edge(g,j);
		Edge hk = new Edge(h,k);
		Edge il = new Edge(i,l);
		
		ArrayList nodes = new ArrayList();
		ArrayList edges = new ArrayList();

		nodes.add(a);
		nodes.add(b);
		nodes.add(c);
		nodes.add(d);
		nodes.add(e);
		nodes.add(f);
		nodes.add(g);
		nodes.add(h);
		nodes.add(i);
		nodes.add(j);
		nodes.add(k);
		nodes.add(l);

		edges.add(ca);
		edges.add(ba);
		edges.add(bd);
		edges.add(fb);
		edges.add(eb);
		edges.add(bg);
		edges.add(bh);
		edges.add(bi);
		edges.add(hj);
		edges.add(gj);
		edges.add(hk);
		edges.add(il);
		
		
		double[] tempa = {1, 0.7, 0.8, 0, 0.4, 1, 0.3, 0.5, 0, 0.4, 1, 0, 0.1, 0, 0.2, 1, 0.7, 0.8, 0, 0.4, 0, 0.3, 0.2, 1, 0.6, 0, 0.7, 0.5, 1, 0.6, 0, 1, 0.9, 1, 0.8, 0, 0.3, 0.2, 1, 0.6};
		a.setProbDistribution(tempa);
		double[] tempb = {0, 0, 0.2, 0.1, 0.1, 0.3, 0.2, 0.2,        0.5, 0.5, 0.2, 0, 0, 0.1, 0.1, 0,         0.3, 0.3, 0.1, 0.1, 0.1, 0.3, 0.2, 0.2,         0, 0, 0.2, 0.7, 0.5, 0.1, 0.2, 0.5,         0.2, 0.2, 0.3, 0.1, 0.3, 0.2, 0.3, 0.1};
		b.setProbDistribution(tempb);
		double[] tempc = {0.98, 0.01,.005,.005};
		c.setProbDistribution(tempc);
		double[] tempd = {0, 0.6, 0.1, 0, 0, 0.2, 0.3, 0.5, 0, 0, 0, 0, 0.2, 0, 0.8, 0.8, 0.1, 0.2, 1, 0.2};
		d.setProbDistribution(tempd);
		double[] tempe = {0.25, 0.25,.25,.25};
		e.setProbDistribution(tempe);
		double[] tempf = {0.6, 0.4};  /* 220/365 = 0.6027; 145/365 = 0.3972...*/
		f.setProbDistribution(tempf);
		double[] tempg = {0.8, 0.15, 0.2, 0, 0.2, 0.2, 0.15, 0.2, 0.1, 0.1, 0, 0.7, 0.6, 0.9, 0.7};
		g.setProbDistribution(tempg);
		double[] temph = {0.1, 0.2, 0.25, 0.6, 0.5, 0.1, 0.5, 0.1, 0.1, 0.1, 0.1, 0.2, 0.4, 0.1, 0.2, 0.7, 0.1, 0.25, 0.2, 0.2};
		h.setProbDistribution(temph);
		double[] tempi = {0.5, 0, 0.1, 0.1, 0.3, 0, 0.3, 0.8, 0.1, 0.6, 0.5, 0.7, 0.1, 0.8, 0.1};
		i.setProbDistribution(tempi);
		double[] tempj = {0.4, 0, 0, 0.4, 0, 0, 0.4, 0, 0, 0.4, 0, 0, 0.5, 0.3, 0.1, 0.5, 0.3, 0.1, 0.5, 0.3, 0.1, 0.5, 0.3, 0.1, 0.1, 0.7, 0.9, 0.1, 0.7, 0.9, 0.1, 0.7, 0.9, 0.1, 0.7, 0.9};
		j.setProbDistribution(tempj);
		double[] tempk = {1,0,0,0,   0,1,0,0,     0,0,1,0,    0,0,0,1};
		k.setProbDistribution(tempk);
		double[] templ = {0.9, 0.2, 0,    0.1, 0.7, 0.1,    0, 0.1, 0.9};
		l.setProbDistribution(templ);
		     
		return new DAG(nodes,edges);
	}
	

	private String readFileAsString(String filePath) throws java.io.IOException{
	    byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream f = null;
	    try {
	        f = new BufferedInputStream(new FileInputStream(filePath));
	        f.read(buffer);
	    } finally {
	        if (f != null) try { f.close(); } catch (IOException ignored) { }
	    }
	    return new String(buffer);
	}

}
