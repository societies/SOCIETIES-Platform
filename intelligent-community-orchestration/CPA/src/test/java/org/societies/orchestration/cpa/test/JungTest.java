/*
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp.,
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

package org.societies.orchestration.cpa.test;

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
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVStrategy;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeedCallback;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.schema.activityfeed.MarshaledActivityFeed;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.societies.orchestration.cpa.impl.SocialGraphEdge;
import org.societies.orchestration.cpa.impl.SocialGraphVertex;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.util.List;

public class JungTest extends JApplet 
{
	VisualizationViewer<SocialGraphVertex,SocialGraphEdge> vv;

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
        InputStream is = null;
        ArrayList<String[]> data = new ArrayList<String[]>();
        File directory = new File (".");
        try {
            System.out.println ("Current directory's canonical path: "
                    + directory.getCanonicalPath());
            System.out.println ("Current directory's absolute  path: "
                    + directory.getAbsolutePath());
        }catch(Exception e) {
            System.out.println("Exceptione is ="+e.getMessage());
        }

        try {
            CSVParser parser = new CSVParser(new FileReader("./src/test/resources/msn-data-xml.csv"), CSVStrategy.EXCEL_STRATEGY);
            String[] value =  parser.getLine();
            while(value!=null){
                data.add(value);
                value = parser.getLine();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        int maxMem = 20000;
        CISSimulator sim = new CISSimulator(10,10);
		final ArrayList<IActivity> actDiff = new ArrayList<IActivity>();
        ApplicationContextLoader loader = new ApplicationContextLoader();
        loader.load(sim, "SimTest-context.xml");
		sim.getActFeed().setSessionFactory(sim.getSessionFactory());
		cises = new ArrayList<ICisOwned>();
		sim.setMaxActs(70000);
		//cises.add(sim.simulate(1));
        cises.add(sim.simulate("./src/test/resources/msn-data-xml.csv"));
        final CPACreationPatterns cpa = new CPACreationPatterns();
        class GetActFeedCB implements IActivityFeedCallback{

            @Override
            public void receiveResult(MarshaledActivityFeed activityFeedObject) {
                System.out.println("in receiveresult: "+activityFeedObject.getGetActivitiesResponse().getMarshaledActivity().size());
                for(org.societies.api.schema.activity.MarshaledActivity act : activityFeedObject.getGetActivitiesResponse().getMarshaledActivity())  {
                    actDiff.add(new Activity(act));
                }
                cpa.init();
                cpa.analyze(actDiff);
            }
        }
        GetActFeedCB dummyFeedback = new GetActFeedCB();
        cises.get(0).getActivityFeed().getActivities("0 "+Long.toString(System.currentTimeMillis()+100000L),maxMem,dummyFeedback);

        System.out.println("cises.get(0).getActivityFeed(): "+cises.get(0).getActivityFeed());
        


		return cpa.getGraph().toJung();
	}
	List<ICisOwned> cises;
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
