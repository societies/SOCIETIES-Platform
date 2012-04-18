package org.personalsmartspace.cm.reasoning.bayesian;

import java.util.ArrayList;

import org.personalsmartspace.cm.reasoning.bayesian.solving.*;
import org.personalsmartspace.cm.reasoning.bayesian.structures.DAG;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Edge;
import org.personalsmartspace.cm.reasoning.bayesian.structures.Node;


/**
 * @author fran_ko
 *
 */
public class BayesMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Start new");

		DAG bNet = createActivityNetwork();//createExampleGraph();//
//		System.out.println(bNet);

		JunctionTree beispiel = new JunctionTree(bNet);

		beispiel.initialiseJTree();

		beispiel.propagate();
		/*change something*/
		
		
		
		/*
		 * HARD EVIDENCE: COMPLETE NETWORK
		 * Microphone Loud, show AS, Activity
		 * 
		 * later:
		 * 
		 * Microphone Loud, US WordProcessor, show AS, Activity

		beispiel.addEvidence(bNet.getNodes()[11], "Loud");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		beispiel.addEvidence(bNet.getNodes()[3], "WordProcessor");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");
		 */

		//beispiel.removeEvidence(bNet.getNodes()[11]);
		//beispiel.removeEvidence(bNet.getNodes()[3]);
		
		/*
		 * SOFT EVIDENCE: BAYESLETS
		 * AS soft, show Activity
		 * 
		 * later:
		 * 
		 * AS soft, US WordProcessor, show AS, Activity
		 */

		String[] values = bNet.getNodes()[8].getStates();//{"Noisy", "Conversation", "Quiet"};
		double[] probs = {0.6725,0.3275,0};

		beispiel.addEvidence(bNet.getNodes()[8], values, probs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		beispiel.addEvidence(bNet.getNodes()[3], "WordProcessor");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		System.out.println("\n\n\n\n\n"+ beispiel.getMarginalized("Activity", 1)+"\n\n\n\n\n");

		//Wrong Evidence!!!
		beispiel.addEvidence(bNet.getNodes()[3], "WordProssor");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		beispiel.addEvidence(bNet.getNodes()[8], values, probs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		System.out.println("\n\n\n\n\n"+ beispiel.getMarginalized("Activity", 1)+"\n\n\n\n\n");
		
//		beispiel.removeEvidence(bNet.getNodes()[3]);
	//	System.out.println("\n\n\n\n\n\n\n\n\nremoved evidence from UsedServices\n"+bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");
		
		
/*		String[] values = {"Home", "Office", "MeetingRoom", "Outdoor"};//{"off", "on"};
		double[] probs = {0,1,0,0};//{1,0};

		beispiel.addEvidence(bNet.getNodes()[4], "evening");
		beispiel.addEvidence(bNet.getNodes()[5], "yes");
		beispiel.addEvidence(bNet.getNodes()[2], "unknown");
		beispiel.addEvidence(bNet.getNodes()[3], "VoIP");
		beispiel.addEvidence(bNet.getNodes()[7], values, probs);
		

		System.out.println("\n\n\n\n\n"+bNet.getNodes()[0].printMarginalization());	
*/		
		System.out.println("fertig");
/*		String[] abc = {"on", "off"};
		Edge e = new Edge(new Node("a",abc),new Node("a",abc));
		UndirectedEdge u = (UndirectedEdge) e;
		
		System.out.println(u instanceof Edge);
*/		
	}
	
	private static DAG createExampleGraph(){
		
		String[] abc = {"on", "off"};
		
		Node a = new Node("a",abc);
		Node b = new Node("b",abc);
		Node c = new Node("c",abc);
		Node d = new Node("d",abc);
		Node e = new Node("e",abc);
		Node f = new Node("f",abc);//def);
		Node g = new Node("g",abc);
		Node h = new Node("h",abc);
		
		Edge ab = new Edge(a,b);
		Edge ac = new Edge(a,c);
		Edge bd = new Edge(b,d);
		Edge ce = new Edge(c,e);
		Edge df = new Edge(d,f);
		Edge ef = new Edge(e,f);
		Edge cg = new Edge(c,g);
		Edge eh = new Edge(e,h);
		Edge gh = new Edge(g,h);
		
		ArrayList nodes = new ArrayList();
		ArrayList edges = new ArrayList();

		nodes.add(a);
		nodes.add(b);
		nodes.add(c);
		/*
		ArrayList<String[]> states = new ArrayList<String[]>();
		String[] temp = new String[3];
		int position = 0;
		structures.ProbabilityDistribution.createStatePermutations(nodes, states, temp, position);
		String ergebnis = "";
		for (String[] s: states) { for(String str: s) ergebnis+=str+"\t"; ergebnis+="\n";}
		System.out.println("\nHier kommt das endergebnis, das ich jetzt nimmer versteh:\n"+ergebnis);
		*/
		nodes.add(d);
		nodes.add(e);
		nodes.add(f);
		nodes.add(g);
		nodes.add(h);

		edges.add(ab);
		edges.add(ac);
		edges.add(bd);
		edges.add(ce);
		edges.add(df);
		edges.add(ef);
		edges.add(cg);
		edges.add(eh);
		edges.add(gh);
		
		double[] tempa = {0.5, 0.5};
		a.setProbDistribution(tempa);
		double[] tempb = {0.5, 0.4,.5,.6};
		b.setProbDistribution(tempb);
		double[] tempc = {0.7, 0.2,.3,.8};
		c.setProbDistribution(tempc);
		double[] tempd = {0.9, 0.5,.1,.5};
		d.setProbDistribution(tempd);
		double[] tempe = {0.3, 0.6,.7,.4};
		e.setProbDistribution(tempe);
		double[] tempf = {0.01, 0.01,.01,.99,.99,.99,.99,.01};//,0,0,0,0,0,0,0,0};
		f.setProbDistribution(tempf);
		double[] tempg = {0.8, 0.1,.2,.9};
		g.setProbDistribution(tempg);
		double[] temph = {0.05, 0.95,.95,.95,.95,.05,.05,.05};
		h.setProbDistribution(temph);
		
		return new DAG(nodes, edges);
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
