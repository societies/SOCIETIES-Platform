/**
 * Copyright (c) 2011, SOCIETIES Consortium
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
package org.societies.personalisation.CAUITaskManager.test;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.societies.personalisation.CAUI.api.CAUITaskManager.ICAUITaskManager;
import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.CAUI.api.model.IUserIntentTask;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUITaskManager.impl.CAUITaskManager;

/**
 * CAUITaskManagerTest  tests CAUITaskManager methods.
 * @author nikosk
 * @created 12-Jan-2012 7:15:15 PM
 */
public class CAUITaskManagerTest extends JPanel implements TreeSelectionListener{

	private static final long serialVersionUID = 1L;
	private static boolean useSystemLookAndFeel = false;

	
	private JTree tree;
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";
	private JEditorPane htmlPane;
	private String helpURL;
	private static boolean DEBUG = false;
	//Optionally set the look and feel.
	
	ICAUITaskManager modelManager;
	
	
	CAUITaskManagerTest(){
		super(new GridLayout(1,0));
		
		System.out.println("CAUITaskManagerTest start");
		modelManager = new CAUITaskManager();
		
		createModel();
	
		//visualise model
		this.tree = modelManager.getModelTree();
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		//Listen for when the selection changes.
		this.tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			System.out.println("line style = " + lineStyle);
			this.tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		//Create the scroll pane and add the tree to it. 
		JScrollPane treeView = new JScrollPane(this.tree);

		//Create the HTML viewing pane.
		htmlPane = new JEditorPane();
		htmlPane.setEditable(false);
		//	initHelp();
		JScrollPane htmlView = new JScrollPane(htmlPane);

		//Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(100); 
		splitPane.setPreferredSize(new Dimension(500, 300));

		//Add the split pane to this panel.
		add(splitPane);
		//visualise model end
	
		//start testing
		retrieveTests();
		System.out.println("CAUITaskManagerTest end");
	}

	
	
	private void  retrieveTests(){
		
		//type based retrieval
		
		/*
		DefaultMutableTreeNode root = modelManager.getRoot();
		System.out.println("The root has " + root.getChildCount() + " children");
	    System.out.println("The tree's depth is " + root.getDepth());
	    System.out.println("The tree has " + root.getLeafCount() + " leaves");
	    System.out.println("'Root' is really a root? " + root.isRoot());
	    System.out.println("Root's userObject: " + root.toString());
	    */
	
		UserIntentAction retrievedAction = modelManager.retrieveAction("Actions=start/0");
		System.out.println("retrievedAction "+ retrievedAction.getparameterName()+" "+retrievedAction.getvalue());
		
		List<UserIntentAction> resultsType = modelManager.retrieveActionsByType("A-homePc");
		System.out.println("getActionsByType(homePC) " + resultsType);

		List<UserIntentAction> resultsTypeValue = modelManager.retrieveActionsByTypeValue("A-homePc","off");
		System.out.println("getActionsByType(homePC,off) " + resultsTypeValue);
	
		IUserIntentAction actionsResult = resultsTypeValue.get(0);
		System.out.println ("action:"+actionsResult.toString()+" "+this.modelManager.actionBelongsToModel(actionsResult));
		
	}
	

	private void createModel(){
		
		DefaultMutableTreeNode model = modelManager.retrieveModel();
		
		//each node belongs to one task only
		//start Action
		DefaultMutableTreeNode task1ActionStart = new DefaultMutableTreeNode(modelManager.createAction("Actions","start",100));
		
		IUserIntentAction actionA1 =modelManager.createAction("A-homePc","off",50);
		HashMap<String, Serializable> actCtxA1 = new HashMap<String,Serializable>();
		actCtxA1.put("ToD", "morning");
		actionA1.setActionContext(actCtxA1);
		DefaultMutableTreeNode task1ActionA1 = new DefaultMutableTreeNode(actionA1);
		
		IUserIntentAction actionA2 =modelManager.createAction("A-homePc","off",100);
		HashMap<String, Serializable> actCtxA2 = new HashMap<String,Serializable>();
		actCtxA2.put("ToD", "night");
		actionA2.setActionContext(actCtxA2);
		DefaultMutableTreeNode task1ActionA2 = new DefaultMutableTreeNode(actionA2);
			
		IUserIntentAction actionB1 = modelManager.createAction("B-tv","off", 100);
		HashMap<String, Serializable> actCtxB1 = new HashMap<String,Serializable>();
		actCtxB1.put("ToD", "morning");
		actionB1.setActionContext(actCtxB1);
		DefaultMutableTreeNode task1ActionB1 = new DefaultMutableTreeNode(actionB1);
		
		IUserIntentAction actionB2 = modelManager.createAction("B-tv","off", 50);
		HashMap<String, Serializable> actCtxB2 = new HashMap<String,Serializable>();
		actCtxB2.put("ToD", "night");
		actionB2.setActionContext(actCtxB2);
		DefaultMutableTreeNode task1ActionB2 = new DefaultMutableTreeNode(actionB2);
			
		IUserIntentAction actionC1 = modelManager.createAction("C-roomLights","off",100);
		HashMap<String, Serializable> actCtxC1 = new HashMap<String,Serializable>();
		actCtxC1.put("ToD", "morning");
		actionC1.setActionContext(actCtxC1);
		DefaultMutableTreeNode task1ActionC1 = new DefaultMutableTreeNode(actionC1);		
		
		IUserIntentAction actionC2 = modelManager.createAction("C-roomLights","off",100);
		HashMap<String, Serializable> actCtxC2 = new HashMap<String,Serializable>();
		actCtxC2.put("ToD", "night");
		actionC2.setActionContext(actCtxC2);
		DefaultMutableTreeNode task1ActionC2 = new DefaultMutableTreeNode(actionC2);
		
		
		IUserIntentTask uiTask1 = modelManager.createTask("task1", 100);
		Map<String, Serializable> task1Context = new HashMap<String, Serializable>();
		task1Context.put("temperature", 12);
		uiTask1.setTaskContext(task1Context);
		DefaultMutableTreeNode task1node= new DefaultMutableTreeNode(uiTask1);
		
		modelManager.setNextTaskLink(null, uiTask1, new Double(100.0));
		model.add(task1node);
		
		task1node.add(task1ActionStart);
		
		// taskA branch1
		task1ActionStart.add(task1ActionA1);
		task1ActionA1.add(task1ActionB1);
		task1ActionB1.add(task1ActionC1);

		// taskA branch2
		task1ActionStart.add(task1ActionB2);
		task1ActionB2.add(task1ActionA2);
		task1ActionA2.add(task1ActionC2);

		// task B
		IUserIntentTask uiTask2 = modelManager.createTask("task2", 100);
		Map<String, Serializable> task2Context = new HashMap<String, Serializable>();
		task1Context.put("timeOfDay", "morning");
		uiTask2.setTaskContext(task2Context);
		DefaultMutableTreeNode task2node= new DefaultMutableTreeNode(uiTask2);
		
		DefaultMutableTreeNode task2ActionStart = new DefaultMutableTreeNode(modelManager.createAction("Actions","start",100));
		
		
		IUserIntentAction actionD1 = modelManager.createAction("D-setVolume","low",100);
		HashMap<String, Serializable> actCtxD1 = new HashMap<String,Serializable>();
		actCtxD1.put("timeOfDay", "morning");
		actionD1.setActionContext(actCtxD1);
		DefaultMutableTreeNode task1ActionD1 = new DefaultMutableTreeNode(actionD1);		
		
		IUserIntentAction actionD2 = modelManager.createAction("D-setRadio","on",100);
		HashMap<String, Serializable> actCtxD2 = new HashMap<String,Serializable>();
		actCtxD2.put("ToD", "night");
		actionD2.setActionContext(actCtxD2);
		DefaultMutableTreeNode task1ActionD2 = new DefaultMutableTreeNode(actionD2);
		
		task1node.add(task2node);
		task2node.add(task2ActionStart);
		task2ActionStart.add(task1ActionD1);
		task1ActionD1.add(task1ActionD2);

		//model created
		modelManager.updateModel(model);
	}
	
	


	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event dispatch thread.
	 */
	private static void createAndShowGUI() {
		if (useSystemLookAndFeel) {
			try {
				UIManager.setLookAndFeel(
						UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				System.err.println("Couldn't use system look and feel.");
			}
		}

		//Create and set up the window.
		JFrame frame = new JFrame("TreeDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add content to the window.
		frame.add(new CAUITaskManagerTest());

		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}



	public static void main(String[] args) {

		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}


	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
	}

}
