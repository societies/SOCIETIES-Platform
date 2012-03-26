package org.societies.context.user.refinement.test;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;

public class TestGenieJavaTransformation {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void testDAGtoGenie() {
		/* Test 1 */
		NetworkConverter.javaToGenie(System.getProperty("user.home")+"/Desktop/test_canBeDeleted.xdsl", createActivityNetwork());

		/* Test 2 
		GenieJava.javaToGenie("H:\\pepe.xdsl", GenieJava
				.genieToJava("H:\\Network1.xdsl"));
		 */
	}

	@Test
	public void testGenieToDAG() {
		String filenameNetworkSBN = "resources/MainTestLearning.ser";
		DAG dag = NetworkConverter.recoverNetwork(filenameNetworkSBN);
		logger.debug(dag.toString());
		assert(dag!=null);
		logger.info("Completed successfully");
	}
	
	private static DAG createActivityNetwork(){
		
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
		
		Node a = new Node("Busy_Status",as);
		Node b = new Node("Activity",bs);
		Node c = new Node("Caller",cs);
		Node d = new Node("Used_Services",ds);
		Node e = new Node("Time",es);
		Node f = new Node("Working_Day",fs);//def);
		Node g = new Node("Movement",gs);
		Node h = new Node("Location",hs);
		Node i = new Node("Noise_Level",is);
		Node j = new Node("MovementSensor",js);
		Node k = new Node("LocationSensor",ks);
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
		double[] tempc = {0.98, 0.1,.05,.05};
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
		
		
		return new DAG(nodes, edges);
	}
}
