package org.societies.context.user.refinement.test;
import java.io.File;
import java.util.ArrayList;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.JunctionTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Clique;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;


/**
 * @author fran_ko
 *
 */
public class MainTestInference {

	private static int repetitions = 1;
	private static int repeatPropagation = 0;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i=0;i<repetitions;i++){
			//activityNetwork();

			String path = "resources";
			
			String fileName = "superHuge.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);
			
			fileName = "superHuge_leftHalf.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);

			fileName = "superHuge_rightHalf.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);
			
			fileName = "superHuge_leftTop.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);

			
			fileName = "superHuge_leftBottom.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);

			fileName = "superHuge_rightTop.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);

			fileName = "superHuge_rightBottom.xdsl";
			System.out.println("\n\n\n"+fileName+"\n");
			propagateNetworkFromFile(path,fileName);

			
			//testMutualInformation();
		}
	}
	
	private static void testMutualInformation() {
		DAG bNet = NetworkConverter.genieToJava("resources"+File.separator+"simple.xdsl");//createActivityNetwork();//createExampleGraph();//
//		System.out.println(bNet);

		JunctionTree beispiel = new JunctionTree(bNet);
		beispiel.initialiseJTree();

		beispiel.propagate();
		
		Node first = bNet.getNodes()[1];
		Node second = bNet.getNodes()[2];
		
		beispiel.addEvidence(bNet.getNodes()[2], "n31");
		
		double i_XY = mutualInformation(first,second, beispiel);
		System.out.println("Mutual Information between "+first.getName()+" and "+second.getName()+
				": I("+first.getName()+":"+second.getName()+ ")="+i_XY);
		//beispiel.propagate();
		//System.out.println(entropy(first));

	}

	private static double mutualInformation(Node x, Node y, JunctionTree jTree) {
		return entropy(x) - conditionalEntropy(x,y,jTree);
	}

	private static double conditionalEntropy(Node x, Node y, JunctionTree jTree) {
		double result = 0.0;
		ProbabilityDistribution pdX;
		ProbabilityDistribution pdY = y.getMarginalization();
		String[] statesY = y.getStates();
		if (statesY.length!=pdY.getProbabilities().length)
			System.err.println("Passt NICHT!");
		int stateCounter = 0;
		
		double prob_y;
		double conditionalEntropyXy;

		if (!y.hasHardEvidence()) return entropy(x);
		
		for (Probability cptYCase : pdY.getProbabilities()) {
			prob_y = cptYCase.getProbability();
			
			//jTree.removeEvidence(y);
			jTree.addEvidence(y, statesY[stateCounter++]);  // propagates after addition
			
			conditionalEntropyXy=entropy(x); // gets newest marginalization
			//System.out.println(prob_y *conditionalEntropyXy);
			
			result -= prob_y * conditionalEntropyXy;
			jTree.removeEvidence(y);
		}
		
		return result;
	}

	private static double entropy(Node x) {
		double result = 0.0;
		ProbabilityDistribution pd = x.getMarginalization();
		double frequency; 
			
		for (Probability cptCase : pd.getProbabilities()) {
			frequency = cptCase.getProbability();
			if (frequency == 0) continue; // 0 * log(0) := 0
			result -= frequency * (Math.log(frequency) / Math.log(2));
		}
		return result;
	}

	private static void propagateNetworkFromFile(String path, String fileName) {

		long startTime;
		long duration;
		
		String construction = "BN construction took (ns):\t";
		String jTree = "\tJ-Tree construction took (ns):\t";
		String initialisation = "\tInitialisation took (ns):\t";
		String propagation = "\tPropagation took (ns):\t";
		
		startTime = System.nanoTime();
		DAG bNet = NetworkConverter.genieToJava(path+File.separator+fileName);
		duration = System.nanoTime()-startTime;
		System.out.print(construction+(duration));

		startTime = System.nanoTime();
		JunctionTree beispiel = new JunctionTree(bNet);
		duration = System.nanoTime()-startTime;
		System.out.print(jTree +duration);
		Clique[] cliques = beispiel.getCliques();
		int maxWeight = 0;
		int overallCount = 0;
		int size = 0;
		for (Clique c: cliques){
			size = c.countStates();
			if (size > maxWeight)	maxWeight = size;
			overallCount += size;
		}
		System.out.print("\tNumber of Cliques:\t"+cliques.length+"\tMaximum clique size:\t"+maxWeight+"\tSum of clique sizes:\t"+overallCount);

		startTime = System.nanoTime();
		beispiel.initialiseJTree();
		duration = System.nanoTime()-startTime;
		System.out.print(initialisation+duration);

		for (int j=0; j<repeatPropagation ;j++){
			startTime = System.nanoTime();
			beispiel.propagate();
			duration = System.nanoTime()-startTime;
			
			System.out.println(((j>0)?"\t\t\t\t\t":"") + propagation + duration);
		}
		
	}

	private static void activityNetwork(){
		
		System.out.println("Start new");

		DAG bNet = createActivityNetwork();//createExampleGraph();//
//		System.out.println(bNet);

		JunctionTree beispiel = new JunctionTree(bNet);

		beispiel.initialiseJTree();

		beispiel.propagate();
		

		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");
		/*change something*/
		
		

		System.out.println("EVIDENCE ADDITION FROM HERE ON:\n\n\n\n\n\n\n\n\n\n");
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

		System.out.println("Test hard and soft evidence, with double propagation:");
		beispiel.addEvidence(bNet.getNodes()[8], values, probs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		beispiel.addEvidence(bNet.getNodes()[3], "WordProcessor");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[3].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		System.out.println("\n\n\n\n\n>>>>>>2 evidences\n\t\targmax Activity = "+ beispiel.getMarginalized("Activity", 1)+"\n\n\n\n\n");

		//Wrong Evidence!!!
		System.out.println("Test wrong evidence, without clearing before:");
		beispiel.addEvidence(bNet.getNodes()[3], "WordProssor");
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[3].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		beispiel.addEvidence(bNet.getNodes()[8], values, probs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());
		System.out.println(bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");

		System.out.println("\n\n\n\n\n>>>>>>1 wrong, 1 correct evidence\n\t\targmax Activity = "+ beispiel.getMarginalized("Activity", 1)+"\n\n\n\n\n");

		
		
		
		//REMOVING ALL EVIDENCE
		beispiel.removeEvidence(bNet.getNodes()[8]);
		beispiel.removeEvidence(bNet.getNodes()[3]);
		System.out.println("\n\n\n\n\n\n\n\n\nremoved evidence from UsedServices. Prior of Activity as follows:\n"+bNet.getNodes()[1].printMarginalization()+"\n\n\n\n\n\n\n\n\n\n");
		
		
/*		String[] values = {"Home", "Office", "MeetingRoom", "Outdoor"};//{"off", "on"};
		double[] probs = {0,1,0,0};//{1,0};

		beispiel.addEvidence(bNet.getNodes()[4], "evening");
		beispiel.addEvidence(bNet.getNodes()[5], "yes");
		beispiel.addEvidence(bNet.getNodes()[2], "unknown");
		beispiel.addEvidence(bNet.getNodes()[3], "VoIP");
		beispiel.addEvidence(bNet.getNodes()[7], values, probs);
		

		System.out.println("\n\n\n\n\n"+bNet.getNodes()[0].printMarginalization());	
*/
		
		
		
		System.out.println("Two evidences, hard and soft, without automatic intermediate, but final manual propagation:");
		double[] usedServiceProbs = {1,0,0,0};
		
		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[8], values, probs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[8].printMarginalization());

		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[3], bNet.getNodes()[3].getStates(), usedServiceProbs);
		System.out.println("\n\n\n\n\n\n\n\n\n\n"+bNet.getNodes()[3].printMarginalization());

		beispiel.propagate();
		System.out.println("\n\n\n\n\n>>>>>>2 evidences without intermediate propagation\n\t\targmax Activity = "+ beispiel.getMarginalized("Activity", 1)+"\n\n\n\n\n");
		
		
		
		
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
		
		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();

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
		
		ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
		ArrayList<ConnectingNodes> edges = new ArrayList<ConnectingNodes>();

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
