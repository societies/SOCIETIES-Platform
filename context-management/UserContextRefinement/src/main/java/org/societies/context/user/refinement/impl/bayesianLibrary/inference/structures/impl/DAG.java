package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;

/**
 * @author fran_ko
 *
 */
public class DAG implements Serializable{
	
	/**
	 * default ID for serialization
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(DAG.class);
	
	private ArrayList<HasProbabilityTable> nodesSet;
	private ArrayList<ConnectingNodes> edgesSet;
	private String name;
	private boolean debug=false;
	
	
	public DAG(ArrayList<HasProbabilityTable> nodes, ArrayList<ConnectingNodes> edges){
		nodesSet = nodes;
		edgesSet = edges;
		
		name = getNodes().toString();
	}

	/**
	 * Constructor for Bayeslets
	 * 
	 */
	public DAG() {
		nodesSet = new ArrayList<HasProbabilityTable>();
		edgesSet = new ArrayList<ConnectingNodes>();
	}

	public void addNode(Node n)
	{
		nodesSet.add(n);
	}
	
	public void addEdge(UndirectedEdge e){
		edgesSet.add(e);
		e.getBorder2().updateProbTable(this);
	}
	
	public void removeEdge(Edge e){
		edgesSet.remove(e);
		e.getTarget().updateProbTable(this);
		
		e.getTarget().removeIncoming(e);
		e.getSource().removeOutgoing(e);
	}
	
	public void removeNode(Node n)
	{
		nodesSet.remove(n);
		for(int i=0; i<edgesSet.size();i++){
			Edge e = (Edge)edgesSet.get(i);
			if (e.getSource() == n || e.getTarget() == n) removeEdge(e);
		}
	}
	
	public Node[] getNodes(){
		return nodesSet.toArray(new Node[0]);
	}
	
	public Edge[] getEdges(){
		return edgesSet.toArray(new Edge[0]);
	}
	
	public String toString(){
		String part1 = "";
		if(logger.isDebugEnabled()) logger.debug("DAG:toString - Entered");
		if(logger.isDebugEnabled()) logger.debug("DAG:toString - nodeSetsize="+nodesSet.size());		
		for(int i=0; i<nodesSet.size();i++){
			Node n = (Node)nodesSet.get(i);
			part1+=n.getName()+"\t";
			
			//DEBUG 
			if(logger.isDebugEnabled()) logger.debug("Prob-TableLength of Node "+n+"="+n.getProbTable().getProbabilities().length);
			//if(n.getName().equals("f1")) privatString(n);
		}
		part1+="\n\n";
		String part2="";
		if(logger.isDebugEnabled()) logger.debug("DAG:toString - edgesSetsize="+edgesSet.size());
		for(int i=0; i<edgesSet.size();i++){
			Edge e = (Edge)edgesSet.get(i);
			part2+=e+"\n";
		}
		part2+="\n\n";
		String part3="";
		if(logger.isDebugEnabled()) logger.debug("DAG:toString - nodeSetsize="+nodesSet.size());		
		for(int i=0; i<nodesSet.size();i++){
			if(logger.isDebugEnabled()) logger.debug("DAG:toString - inProbTablePrinting Runthrough="+i+" Node="+nodesSet.get(i));
			Node n = (Node)nodesSet.get(i);
			part3+=n.getProbTable()+"\n";

			if(logger.isDebugEnabled()) logger.debug("DAG:toString - inProbTablePrinting End of Runthrough="+i);
		}

		if(debug) System.out.println("DAG:toString - Left");
		return part1+part2+part3;
	}

	public void printToFile(Writer fw) throws IOException{
		String part1 = "";	
		for(int i=0; i<nodesSet.size();i++){
			Node n = (Node)nodesSet.get(i);
			part1+=n.getName()+"\t Number of States: "+n.countStates()+"\t Length of Probability Table: "+n.getProbTable().getProbabilities().length+"\n";
			
			//DEBUG 
			if(logger.isDebugEnabled()) logger.debug("Prob-TableLength of Node "+n+"="+n.getProbTable().getProbabilities().length);
		}
		part1+="\n\n";
		
		String part2="";
		for(int i=0; i<edgesSet.size();i++){
			Edge e = (Edge)edgesSet.get(i);
			part2+=e+"\n";
		}
		
		fw.write(part1+part2);
		fw.flush();
	}

	public String getName() {
		return name;
	}
	public ArrayList<HasProbabilityTable> getNodesArrayList() {
		return nodesSet;
	}

	public ArrayList<ConnectingNodes> getEdgesArrayList() {
		return edgesSet;
	}
	
}
