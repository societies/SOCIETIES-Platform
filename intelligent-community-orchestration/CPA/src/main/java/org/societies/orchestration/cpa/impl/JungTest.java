/*
* Copyright (c) 2003, SOCIETIES and the JUNG Project and the Regents of the University
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
* This file is based on the JUNG project's example
*/

package org.societies.orchestration.cpa.impl;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class JungTest extends JApplet 
{
	VisualizationViewer<SocialGraphVertex,SocialGraphEdge> vv;
	
//	Factory<Graph<Number,Number>> graphFactory;
	
	Map<SocialGraphVertex,Paint> vertexPaints = 
		LazyMap.<SocialGraphVertex,Paint>decorate(new HashMap<SocialGraphVertex,Paint>(),
				new ConstantTransformer(Color.white));
	Map<SocialGraphEdge,Paint> edgePaints =
	LazyMap.<SocialGraphEdge,Paint>decorate(new HashMap<SocialGraphEdge,Paint>(),
			new ConstantTransformer(Color.blue));
	public final Color[] similarColors =
	{
		new Color(216, 134, 134),
		new Color(135, 137, 211),
		new Color(134, 206, 189),
		new Color(206, 176, 134),
		new Color(194, 204, 134),
		new Color(145, 214, 134),
		new Color(133, 178, 209),
		new Color(103, 148, 255),
		new Color(60, 220, 220),
		new Color(30, 250, 100)
	};
	public static void main(String args[]){
		JungTest jt = new JungTest();
		jt.start();
		JFrame jf = new JFrame();
		jf.getContentPane().add(jt);
		
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.pack();
		jf.setVisible(true);
		
		
	}
	public void start(){
		try {
			setUpView(makeGraph());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public UndirectedSparseGraph makeGraph(){
		UndirectedSparseGraph<SocialGraphVertex,SocialGraphEdge> g = new UndirectedSparseGraph<SocialGraphVertex,SocialGraphEdge>();
		SocialGraphVertex v1 = new SocialGraphVertex("BM");
		SocialGraphVertex v2 = new SocialGraphVertex("Thomas");
		SocialGraphVertex v3 = new SocialGraphVertex("Babak");
		SocialGraphVertex v4 = new SocialGraphVertex("Kevin");
		SocialGraphVertex v5 = new SocialGraphVertex("Alec");
		SocialGraphVertex v6 = new SocialGraphVertex("Jaqueline");
		SocialGraph sg = new SocialGraph();
		sg.add(v1);sg.add(v2);sg.add(v3);sg.add(v4);sg.add(v5);sg.add(v6);
		g.addVertex(v1);
		g.addVertex(v2);
		g.addVertex(v3);
		g.addVertex(v4);
		g.addVertex(v5);
		g.addVertex(v6);
		
		g.addEdge(new SocialGraphEdge(v1,v2,1.0),v1,v2);
		g.addEdge(new SocialGraphEdge(v1,v3,1.0),v1,v3);
		//g.addEdge(new SocialGraphEdge(v1,v4,0.1),v1,v4);
		g.addEdge(new SocialGraphEdge(v1,v5,0.6),v1,v5);
		g.addEdge(new SocialGraphEdge(v1,v5,0.6),v1,v6);
		
		g.addEdge(new SocialGraphEdge(v2,v3,1.0),v2,v3);
		g.addEdge(new SocialGraphEdge(v2,v4,0.1),v2,v4);
		g.addEdge(new SocialGraphEdge(v2,v5,0.8),v2,v5);
		g.addEdge(new SocialGraphEdge(v2,v5,0.7),v2,v6);
		
		g.addEdge(new SocialGraphEdge(v3,v4,0.1),v3,v4);
		g.addEdge(new SocialGraphEdge(v3,v5,0.6),v3,v5);
		g.addEdge(new SocialGraphEdge(v3,v5,0.5),v3,v6);
		
		g.addEdge(new SocialGraphEdge(v4,v5,0.7),v4,v5);
		g.addEdge(new SocialGraphEdge(v4,v6,0.3),v4,v6);
		
		g.addEdge(new SocialGraphEdge(v5,v6,0.2),v5,v6);
		
		System.out.println(""+g.toString());
		return g;
	}
	private void setUpView(UndirectedSparseGraph graph) throws IOException {
		
    	Factory<SocialGraphVertex> vertexFactory = new Factory<SocialGraphVertex>() {
            int n = 0;
            public SocialGraphVertex create() { return new SocialGraphVertex(Integer.toString(n++)); }
        };
        Factory<Number> edgeFactory = new Factory<Number>()  {
            int n = 0;
            public Number create() { return n++; }
        };
//
//        PajekNetReader<Graph<Number, Number>, Number,Number> pnr = 
//            new PajekNetReader<Graph<Number, Number>, Number,Number>(vertexFactory, edgeFactory);
//        
//        final Graph<Number,Number> graph = new SparseMultigraph<Number, Number>();
        
        //pnr.load(br, graph);

		//Create a simple layout frame
        //specify the Fruchterman-Rheingold layout algorithm
        final AggregateLayout<SocialGraphVertex,SocialGraphEdge> layout = 
        	new AggregateLayout<SocialGraphVertex,SocialGraphEdge>(new FRLayout<SocialGraphVertex,SocialGraphEdge>(graph));

		vv = new VisualizationViewer<SocialGraphVertex,SocialGraphEdge>(layout);
		vv.setBackground( Color.white );
		//Tell the renderer to use our own customized color rendering
		vv.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<SocialGraphVertex,Paint>getInstance(vertexPaints));
		vv.getRenderContext().setVertexDrawPaintTransformer(new Transformer<SocialGraphVertex,Paint>() {
			public Paint transform(SocialGraphVertex v) {
				if(vv.getPickedVertexState().isPicked(v)) {
					return Color.cyan;
				} else {
					return Color.BLACK;
				}
			}
		});
	
		vv.getRenderContext().setEdgeDrawPaintTransformer(MapTransformer.<SocialGraphEdge,Paint>getInstance(edgePaints));

		vv.getRenderContext().setEdgeStrokeTransformer(new Transformer<SocialGraphEdge,Stroke>() {
                protected final Stroke THIN = new BasicStroke(1);
                protected final Stroke THICK= new BasicStroke(2);
                public Stroke transform(SocialGraphEdge e)
                {
                    Paint c = edgePaints.get(e);
                    if (c == Color.LIGHT_GRAY)
                        return THIN;
                    else 
                        return THICK;
                }
            });
		vv.getRenderContext().setVertexIconTransformer(new Transformer<SocialGraphVertex,Icon>() {

        	/*
        	 * Implements the Icon interface to draw an Icon with background color and
        	 * a text label
        	 */
			public Icon transform(final SocialGraphVertex v) {
				return new Icon() {

					public int getIconHeight() {
						return 20;
					}

					public int getIconWidth() {
						return 20;
					}

					public void paintIcon(Component c, Graphics g,
							int x, int y) {
						if(vv.getPickedVertexState().isPicked(v)) {
							g.setColor(Color.yellow);
						} else {
							g.setColor(Color.red);
						}
						g.fillOval(x, y, 20, 20);
						if(vv.getPickedVertexState().isPicked(v)) {
							g.setColor(Color.black);
						} else {
							g.setColor(Color.white);
						}
						g.setFont(new Font("Monospaced",Font.PLAIN,10));
						g.drawString(""+v, x+6, y+15);
						
					}};
			}});
		//add restart button
		JButton scramble = new JButton("Restart");
		scramble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Layout layout = vv.getGraphLayout();
				layout.initialize();
				Relaxer relaxer = vv.getModel().getRelaxer();
				if(relaxer != null) {
					relaxer.stop();
					relaxer.prerelax();
					relaxer.relax();
				}
			}

		});
		
		DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
		vv.setGraphMouse(gm);
		
		final JToggleButton groupVertices = new JToggleButton("Group Clusters");

		//Create slider to adjust the number of edges to remove when clustering
		final JSlider edgeBetweennessSlider = new JSlider(JSlider.HORIZONTAL);
        edgeBetweennessSlider.setBackground(Color.WHITE);
		edgeBetweennessSlider.setPreferredSize(new Dimension(210, 50));
		edgeBetweennessSlider.setPaintTicks(true);
		edgeBetweennessSlider.setMaximum(graph.getEdgeCount());
		edgeBetweennessSlider.setMinimum(0);
		edgeBetweennessSlider.setValue(0);
		edgeBetweennessSlider.setMajorTickSpacing(10);
		edgeBetweennessSlider.setPaintLabels(true);
		edgeBetweennessSlider.setPaintTicks(true);

//		edgeBetweennessSlider.setBorder(BorderFactory.createLineBorder(Color.black));
		//TO DO: edgeBetweennessSlider.add(new JLabel("Node Size (PageRank With Priors):"));
		//I also want the slider value to appear
		final JPanel eastControls = new JPanel();
		eastControls.setOpaque(true);
		eastControls.setLayout(new BoxLayout(eastControls, BoxLayout.Y_AXIS));
		eastControls.add(Box.createVerticalGlue());
		eastControls.add(edgeBetweennessSlider);

		final String COMMANDSTRING = "Edges removed for clusters: ";
		final String eastSize = COMMANDSTRING + edgeBetweennessSlider.getValue();
		
		final TitledBorder sliderBorder = BorderFactory.createTitledBorder(eastSize);
		eastControls.setBorder(sliderBorder);
		//eastControls.add(eastSize);
		eastControls.add(Box.createVerticalGlue());
		
		groupVertices.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
					clusterAndRecolor(layout, edgeBetweennessSlider.getValue(), 
							similarColors, e.getStateChange() == ItemEvent.SELECTED);
					vv.repaint();
			}});


		clusterAndRecolor(layout, 0, similarColors, groupVertices.isSelected());

		edgeBetweennessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				if (!source.getValueIsAdjusting()) {
					int numEdgesToRemove = source.getValue();
					clusterAndRecolor(layout, numEdgesToRemove, similarColors,
							groupVertices.isSelected());
					sliderBorder.setTitle(
						COMMANDSTRING + edgeBetweennessSlider.getValue());
					eastControls.repaint();
					vv.validate();
					vv.repaint();
				}
			}
		});

		Container content = getContentPane();
		content.add(new GraphZoomScrollPane(vv));
		JPanel south = new JPanel();
		JPanel grid = new JPanel(new GridLayout(2,1));
		grid.add(scramble);
		grid.add(groupVertices);
		south.add(grid);
		south.add(eastControls);
		JPanel p = new JPanel();
		p.setBorder(BorderFactory.createTitledBorder("Mouse Mode"));
		p.add(gm.getModeComboBox());
		south.add(p);
		content.add(south, BorderLayout.SOUTH);
	}

	public void clusterAndRecolor(AggregateLayout<SocialGraphVertex,SocialGraphEdge> layout,
		int numEdgesToRemove,
		Color[] colors, boolean groupClusters) {
		//Now cluster the vertices by removing the top 50 edges with highest betweenness
		//		if (numEdgesToRemove == 0) {
		//			colorCluster( g.getVertices(), colors[0] );
		//		} else {
		
		Graph<SocialGraphVertex,SocialGraphEdge> g = layout.getGraph();
        layout.removeAll();

		EdgeBetweennessClusterer<SocialGraphVertex,SocialGraphEdge> clusterer =
			new EdgeBetweennessClusterer<SocialGraphVertex,SocialGraphEdge>(numEdgesToRemove);
		Set<Set<SocialGraphVertex>> clusterSet = clusterer.transform(g);
		List<SocialGraphEdge> edges = clusterer.getEdgesRemoved();

		int i = 0;
		//Set the colors of each node so that each cluster's vertices have the same color
		for (Iterator<Set<SocialGraphVertex>> cIt = clusterSet.iterator(); cIt.hasNext();) {

			Set<SocialGraphVertex> vertices = cIt.next();
			Color c = colors[i % colors.length];

			colorCluster(vertices, c);
			if(groupClusters == true) {
				groupCluster(layout, vertices);
			}
			i++;
		}
		for (SocialGraphEdge e : g.getEdges()) {

			if (edges.contains(e)) {
				edgePaints.put(e, Color.lightGray);
			} else {
				edgePaints.put(e, Color.black);
			}
		}

	}

	private void colorCluster(Set<SocialGraphVertex> vertices, Color c) {
		for (SocialGraphVertex v : vertices) {
			vertexPaints.put(v, c);
		}
	}
	
	private void groupCluster(AggregateLayout<SocialGraphVertex,SocialGraphEdge> layout, Set<SocialGraphVertex> vertices) {
		if(vertices.size() < layout.getGraph().getVertexCount()) {
			Point2D center = layout.transform(vertices.iterator().next());
			Graph<SocialGraphVertex,SocialGraphEdge> subGraph = SparseMultigraph.<SocialGraphVertex,SocialGraphEdge>getFactory().create();
			for(SocialGraphVertex v : vertices) {
				subGraph.addVertex(v);
			}
			Layout<SocialGraphVertex,SocialGraphEdge> subLayout = 
				new CircleLayout<SocialGraphVertex,SocialGraphEdge>(subGraph);
			subLayout.setInitializer(vv.getGraphLayout());
			subLayout.setSize(new Dimension(40,40));

			layout.put(subLayout,center);
			vv.repaint();
		}
	}
}
