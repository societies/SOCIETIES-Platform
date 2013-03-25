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
import org.societies.activity.ActivityFeed;
import org.societies.activity.model.Activity;
import org.societies.api.activity.IActivity;
import org.societies.api.cis.management.ICisOwned;
import org.societies.orchestration.cpa.impl.CPACreationPatterns;
import org.societies.orchestration.cpa.impl.SocialGraph;
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
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 18.10.12
 * Time: 01:26
 */
public class TrendTest  extends JApplet {
    public static final String TREND_1 = "trend1";
    public static final String NON_TREND = "non_trend";
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
    public static JFrame jf = null;
    public static void main(String[] args){

        TrendTest jt = new TrendTest();
        jt.start();
        jf = new JFrame();
        jf.getContentPane().add(jt);

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.pack();
        jf.setVisible(true);
        jt.update();

/*        sim.simulate(1);
        sim.setMaxActs(2000);*/







    }
    public void update(){
        //System.out.println("sim.getActFeed().getActivities((t+1)+\" \"+Long.toString(System.currentTimeMillis()+100000000L)).size(): "+sim.getActFeed().getActivities(lastAct.getPublished()+" "+Long.toString(System.currentTimeMillis()+100000000L)).size());

        System.out.println("after setupview");
        List<IActivity> actList = sim.getActFeed().getActivitiesFromDB(Long.toString(t + 3) + " " + Long.toString(System.currentTimeMillis() + 100000000L));
        IActivity lastAct = actList.get(actList.size()-1);
        this.sleep();

        sim.getActFeed().addActivityToDB(a2);
        cpa.analyze(sim.getActFeed().getActivitiesFromDB(lastAct.getPublished()+" "+Long.toString(System.currentTimeMillis()+100000000L)));
        actList = sim.getActFeed().getActivitiesFromDB(Long.toString(0) + " " + Long.toString(System.currentTimeMillis() + 100000000L));
        lastAct = actList.get(actList.size()-1);
        jf.setVisible(false);jf.setVisible(true);
        vv.revalidate();
        this.sleep();

        //cpa.analyze()
        sim.getActFeed().addActivityToDB(a3);
        cpa.analyze(sim.getActFeed().getActivitiesFromDB(lastAct.getPublished()+" "+Long.toString(System.currentTimeMillis()+100000000L)));
        actList = sim.getActFeed().getActivitiesFromDB(Long.toString(0) + " " + Long.toString(System.currentTimeMillis() + 100000000L));
        lastAct = actList.get(actList.size()-1);
        jf.setVisible(false);jf.setVisible(true);
        vv.revalidate();
        this.sleep();

        sim.getActFeed().addActivityToDB(a4);
        cpa.analyze(sim.getActFeed().getActivitiesFromDB(lastAct.getPublished()+" "+Long.toString(System.currentTimeMillis()+100000000L)));
        jf.setVisible(false);jf.setVisible(true);
        vv.revalidate();
        List<String> topTrends = cpa.getGraph().topTrends(4);
        System.out.println("topTrends size: "+topTrends.size());
        if(topTrends.size()>0) {
            System.out.println("top trend: "+ topTrends.get(0));
            System.out.println("bottom trend: "+ topTrends.get(topTrends.size()-1));
        }
    }
    Activity a1 = new Activity();
    Activity a2 = new Activity();
    Activity a3 = new Activity();
    Activity a4 = new Activity();
    Activity pa1 = new Activity();
    Activity pa2 = new Activity();
    Activity pa3 = new Activity();
    Activity pa4 = new Activity();
    long t = 0;
    public void makeActs(){
        t = System.currentTimeMillis();
        a1.setTime(t+1);
        System.out.println("adding a1 with t: "+a1.getTime());
        a1.setPublished(Long.toString(t+1));
        a1.setActor("user1");
        a1.setTarget("user2");
        a1.setObject(TREND_1);


        a2.setTime(t+2);
        System.out.println("adding a2 with t: "+a2.getTime());
        a2.setPublished(Long.toString(t+2));
        a2.setActor("user5");
        a2.setTarget("user3");
        a2.setObject(TREND_1);


        a3.setTime(t+3);
        System.out.println("adding a3 with t: "+a3.getTime());
        a3.setPublished(Long.toString(t+3));
        a3.setActor("user5");
        a3.setTarget("user17");
        a3.setObject(TREND_1);


        a4.setTime(t+4);
        System.out.println("adding a4 with t: "+a4.getTime());
        a4.setPublished(Long.toString(t+4));
        a4.setActor("user17");
        a4.setTarget("user9");
        a4.setObject(TREND_1);

        pa1.setTime(t-1);
        pa1.setPublished(Long.toString(t-1));
        pa1.setActor("user1");
        pa1.setTarget("user2");
        pa1.setObject(NON_TREND+"1");


        pa2.setTime(t-2);
        pa2.setPublished(Long.toString(t-2));
        pa2.setActor("user5");
        pa2.setTarget("user3");
        pa2.setObject(NON_TREND+"2");


        pa3.setTime(t-3);
        pa3.setPublished(Long.toString(t-3));
        pa3.setActor("user5");
        pa3.setTarget("user17");
        pa3.setObject(NON_TREND+"3");


        pa4.setTime(t-4);
        pa4.setPublished(Long.toString(t-4));
        pa4.setActor("user17");
        pa4.setTarget("user9");
        pa4.setObject(NON_TREND+"3");

    }
    CISSimulator sim = new CISSimulator(20,0);
    public void setup(){
        ApplicationContextLoader loader = new ApplicationContextLoader();
        loader.load(sim, "SimTest-context.xml");
        sim.getActFeed().setSessionFactory(sim.getSessionFactory());
        sim.getActFeed().setId("1");
    }
    public void sleep(){
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void start(){
        makeActs();
        setup();
        aFeed = sim.getActFeed();
        sim.getActFeed().addActivityToDB(pa1);sim.getActFeed().addActivityToDB(pa2);sim.getActFeed().addActivityToDB(pa3);sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user11");pa4.setObject("jej");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user12");pa4.setObject("hej");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user13");pa4.setObject("jeh");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user14");pa4.setObject("jeg");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user15");pa4.setObject("oej");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user16");pa4.setObject("iej");sim.getActFeed().addActivityToDB(pa4);
        pa4.setActor("user17");pa4.setObject("aej");sim.getActFeed().addActivityToDB(pa4);
        sim.getActFeed().addActivityToDB(a1);
        makeGraph();
        try {
            System.out.println("before setupview");
            setUpView(graph.toJung());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    ActivityFeed aFeed = null;
    SocialGraph graph = null;
    CPACreationPatterns cpa = null;
    public UndirectedSparseGraph makeGraph(){








        cpa = new CPACreationPatterns();
        cpa.init();
        cpa.analyze(sim.getActFeed().getActivitiesFromDB("0 "+Long.toString(System.currentTimeMillis()+100000000L)));
        graph = cpa.getGraph();

        ((SocialGraphVertex)cpa.getGraph().getVertices().get(0)).setTrend(true);
        return cpa.getGraph().toJung();
    }
    java.util.List<ICisOwned> cises;
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
                        if(vv.getPickedVertexState().isPicked(v) || v.hasTrend(TREND_1)) {
                            g.setColor(Color.yellow);               //her setter vi trend !?
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
        java.util.List<SocialGraphEdge> edges = clusterer.getEdgesRemoved();

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
