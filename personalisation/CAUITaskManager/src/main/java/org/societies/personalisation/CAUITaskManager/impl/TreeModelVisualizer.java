package org.societies.personalisation.CAUITaskManager.impl;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;


public class TreeModelVisualizer extends JPanel implements TreeSelectionListener {

	private static final long serialVersionUID = 1L;
	private JTree tree = null;
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";
	private JEditorPane htmlPane;
//	private String helpURL;
//	private static boolean DEBUG = false;
//	private static boolean useSystemLookAndFeel = false;

	TreeModelVisualizer(JTree tree){
		super(new GridLayout(1,0));
		this.tree = tree;
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	
	}

	TreeModelVisualizer(JTree tree, int i){
		super(new GridLayout(1,0));
		this.tree = tree;
		modelVisualizer();
	}

	private void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("User Intent Tree");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new TreeModelVisualizer(this.tree,  1));
				
		//Display the window.
		frame.pack();
		frame.setVisible(true);
	}


	protected void modelVisualizer(){
		
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
	}

	public JTree getTree() {
		return tree;
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		// TODO Auto-generated method stub
	}
}
