package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;

//import ncsa.d2k.modules.ls.*;

/**
  * The main GUI, this class contains all of the GUI features.  It
  * calls all of the functions that will end up manipulation the
  * nodes and their relations to each other.  The entire GUI is
  * divided into three sections:  the toolbars, the editarea, and
  * the graphical area.
  *
  * @author Laura Kruse
  * @version v1.5
  */
public class XBN {
	private JFrame frame = new JFrame("XBN Editor");
	private JPanel panel = new JPanel();
	private JPanel paneltop = new JPanel();

	private JMenuBar menubar = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenu tools = new JMenu("Tools");

	private JMenuItem nnew = new JMenuItem("New");
	private JMenuItem open = new JMenuItem("Open");
	private JMenuItem save = new JMenuItem("Save");
	private JMenuItem saveas = new JMenuItem("Save As");
	private JMenuItem close = new JMenuItem("Exit");
	private JMenuItem exit = new JMenuItem("Exit All");

	private JMenu edit = new JMenu("Edit Node");
	private JMenuItem addstate = new JMenuItem("Insert State");
	private JMenuItem deletestate = new JMenuItem("Delete State");

	private JMenuItem undo = new JMenuItem("Undo");
	private JMenuItem redo = new JMenuItem("Redo");

	private JMenu filetype = new JMenu("File Type");
	// These are public for Haipengs code apparently.
	public JCheckBoxMenuItem xml = new JCheckBoxMenuItem(".xml");
	public JCheckBoxMenuItem bif = new JCheckBoxMenuItem(".bif");
        public JCheckBoxMenuItem dsl = new JCheckBoxMenuItem(".dsl");

	private JMenu loss = new JMenu("Measure Loss");
	private JMenuItem grapherrors = new JMenuItem("Graph Errors");
	private JMenuItem inference = new JMenuItem("Inference");

	private JFileChooser fchoose = new JFileChooser();

	private JToolBar toolbar = new JToolBar();
	public static EditNode editnode;
	public GraphicalPanel draw;

	private int startx;
	private int starty;

	private GridBagLayout gridbag;
	private GridBagConstraints gbc;

	private Item cb = null;

	private JDialog dialog;
	private String state;

	private Vector vundo;
	private Vector vredo;

	private boolean standalone;

	public FileIO fileio = new FileIO();
	public String filename = null;
	public String format = "xml";

	private int[][] goldstandard;
	private int[][] bnstandard;

	private final int width = 80;
	private final int height = 50;

	private final int boardwidth = 2000;
	private final int boardheight = 1000;

	/**
	  * Creates a new instance of the GUI and shows it to the
	  * user.&nbsp;This is also responsible for placing all of
 	  * the widgets into the starting layout.
	  */
	public XBN() {
		gridbag = new GridBagLayout();
		gbc = new GridBagConstraints();
		frame.setContentPane(new JScrollPane(panel));
		panel.setLayout(gridbag);

		// setting up the menu bar
		frame.setJMenuBar(menubar);

		menubar.add(file);
		file.setMnemonic('F');

		file.add(nnew);
		nnew.setMnemonic('N');
		nnew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,Event.CTRL_MASK));
		nnew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nnew();
			}
		});

		file.add(open);
		open.setMnemonic('O');
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,Event.CTRL_MASK));
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});

		file.addSeparator();

		file.add(save);
		save.setMnemonic('S');
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,Event.CTRL_MASK));
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});

		file.add(saveas);
		saveas.setMnemonic('A');
		saveas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,Event.CTRL_MASK));
		saveas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveas();
			}
		});

		file.addSeparator();

		file.add(close);
		close.setMnemonic('X');
		close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		file.add(exit);
		exit.setMnemonic('X');
		exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,Event.ALT_MASK));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		menubar.add(tools);
		tools.setMnemonic('T');

		tools.add(edit);
		edit.setMnemonic('E');

		edit.add(addstate);
		addstate.setMnemonic('I');
		addstate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,Event.CTRL_MASK));
		addstate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cb!=null) redrawEditNodeArea(cb,1,false);
			}
		});

		edit.add(deletestate);
		deletestate.setMnemonic('D');
		deletestate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,Event.CTRL_MASK));
		deletestate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cb!=null) deleteDialog((ChanceBlock) cb.getItem());
			}

		});

		tools.addSeparator();

		tools.add(undo);
		undo.setMnemonic('U');
		undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Event.CTRL_MASK));
		undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		undo.setEnabled(false);

		vundo = new Vector();

		tools.add(redo);
		redo.setMnemonic('R');
		redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
		redo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redo();
			}
		});
		redo.setEnabled(false);

		vredo = new Vector();

		tools.addSeparator();

		tools.add(filetype);
		filetype.setMnemonic('Y');

		filetype.add(xml);
		xml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				format = "xml";
				xml.setSelected(true);
				bif.setSelected(false);
                                dsl.setSelected(false);
			}
		});
		xml.setSelected(true);

		filetype.add(bif);
		bif.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				format = "bif";
				xml.setSelected(false);
				bif.setSelected(true);
                                dsl.setSelected(false);
			}
		});

                filetype.add(dsl);
		dsl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				format = "dsl";
				xml.setSelected(false);
				bif.setSelected(false);
                                dsl.setSelected(true);
			}
		});

		menubar.add(loss);
		loss.setMnemonic('L');

		loss.add(grapherrors);
		grapherrors.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getStandards();
			}
		});

		loss.add(inference);
		inference.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				inference();
			}
		});

		// setting up the toolbar, this will eveuntally contain
		// only picutre icons representing what to do
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(toolbar,gbc);
		panel.add(toolbar);
		toolbar.setFloatable(false);

		Action pointer = new AbstractAction(null,new ImageIcon("modules/ncsa/d2k/modules/k2/editor/images/node.jpg")) {
			public void actionPerformed(ActionEvent e) {
				draw.changeDrawType(0);
			}
		};
		toolbar.add(pointer);
		toolbar.addSeparator();

		Action dline = new AbstractAction(null,new ImageIcon("modules/ncsa/d2k/modules/k2/editor/images/arrow1.jpg")) {
			public void actionPerformed(ActionEvent e) {
				draw.changeDrawType(1);
			}
		};
		toolbar.add(dline);
		toolbar.addSeparator();

		Action del = new AbstractAction("X",null) {
			public void actionPerformed(ActionEvent e) {
				// delete the highlited node
				draw.changeDrawType(2);
			}
		};
		toolbar.add(del);
		toolbar.addSeparator();

		// setting stuff for the info area
		editnode = new EditNode(new Item(), -1);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(editnode,gbc);

		// This is just a label that doesn't really need to be here
		//label = new JLabel("Drag the Objects around within the area");
		//gbc.gridwidth = GridBagConstraints.REMAINDER;
		//gridbag.setConstraints(label,gbc);

		// adding the drawing area for the BN
		draw = new GraphicalPanel(this);
		gbc.ipady = boardheight;
		gbc.ipadx = boardwidth;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(draw,gbc);
		panel.add(draw);

		//panel.add(label);

		// makes the little X in the corner work
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});


		frame.pack();
		frame.setSize(600, 400);
		frame.show();
	}

	/**
	  * This method allows for a new Bayesian Network to be created
	  * over the existing Bayesian Network.  If the previous network
	  * has not been saved the user will be asked if they want to save
	  * the network or not.  Then the drawing area will be cleared
	  * and a new network can be created.
	  */
	private void nnew() {
		if(draw.isSaved()) {
			draw.setBayNet(new LinkedList());
		} else {
			save();
			// possible add something here in case the user
			// decides that they really don't want to make a
			// new file
			draw.setBayNet(new LinkedList());
		}
	}

	/**
	  * This is a file menu option and will allow the user to
	  * load a previously generate BN file.
	  */
	private void open() {
		File file;

		fchoose.setDialogTitle("Load File");
		fchoose.showOpenDialog(frame);

		file = fchoose.getSelectedFile();

		if(file != null) {
			//System.out.println(file.getAbsolutePath());
			filename = file.getAbsolutePath();

			if(file.getName()!=null) {
				LinkedList llist = fileio.load(filename);
				format = fileio.getFileType();
				if(format=="xml") {
					xml.setSelected(true);
					bif.setSelected(false);
                                        dsl.setSelected(false);
				} else if(format=="bif") {
                                        xml.setSelected(true);
					bif.setSelected(false);
                                        dsl.setSelected(false);
                                 } else {
					xml.setSelected(false);
					bif.setSelected(false);
                                        dsl.setSelected(true);
				}
				draw.setBayNet(llist);
			}
		}
	}

	/**
	  * Allows the user to save the current BN they are working with.
	  */
	private synchronized void save() {
		if(filename==null) {
			saveas();
		} else {
			fileio.setFileType(format);
			fileio.save(filename,draw.getBayNet());
			draw.setSaved(true);
		}
	}

	/**
	  * Allows the user to save the current BN with a specific name.
	  */
	private synchronized void saveas() {
		File file;

		fchoose.setDialogTitle("Save File As");

		if(fchoose.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			try {
				file = fchoose.getSelectedFile();
				filename = file.getAbsolutePath();

				fileio.setFileType(format);
				fileio.save(filename,draw.getBayNet());
				draw.setSaved(true);
			} catch(NullPointerException npe) {
				// the user didn't specify a file, tell them
			}
		}
	}

	/**
	  * Puts a copy of the Network onto the undo Vector so that it can
	  * be undone if the user so chooses.
	  */
	public void addundo() {
		vundo.add(copy());

		undo.setEnabled(true);
	}

	/**
	  * Puts a copy of the Network onto the redo Vector so that it can
	  * be redone if the user so chooses.
	  */
	private void addredo() {
		vredo.add(copy());

		redo.setEnabled(true);
	}

	/**
	  * This internally makes a copy of the entire Network.  This is a
	  * very slow process.  The Java version of clone can not be done in
	  * this case because it only does a high level copy of the structure
	  * which results in each node being put on the new list as a refrenec
	  * to the old copy.  This is not the behavior that we want.  Instead
	  * this traverses the entire structure and creates a new copy down
	  * to the last detail.  The larger the networks become the more time
	  * this will take.
	  */
	private LinkedList copy() {
		LinkedList old;
		LinkedList copy;
		Item current;

		old = draw.getBayNet();
		copy = new LinkedList();

		for(int i=0;i<old.size();i++) {
			current = (Item) old.get(i);

			LinkedList states;
			String blockName;

			int xcoord;
			int ycoord;


			ChanceBlock cb;
			Ellipse2D.Double e2d;
			Item item;

			states = ((ChanceBlock) current.getItem()).getAttributeNames();
			blockName = current.getItem().getBlockName();
			xcoord = (new Double(current.getItem().getx())).intValue();
			ycoord = (new Double(current.getItem().gety())).intValue();

			cb = new ChanceBlock(blockName, xcoord, ycoord, false);

			for(int s=0;s<states.size();s++) {
				cb.add((String) states.get(s));
			}

			e2d = new Ellipse2D.Double(xcoord - width / 2, ycoord - height / 2, width, height);
			item = new Item(cb, e2d);

			copy.add(item);
		}

		// Looking at the old list and are going to set the
		// parents and children accordingly.
		for(int i=0;i<old.size();i++) {
			current = (Item) old.get(i);

			Item child;

			String parentName;
			String childName;

			int rows;
			int columns;
			int loc;
			int size;

			loc = 0;
			size = copy.size();
			childName = current.getItem().getBlockName();

			child = (Item) copy.getFirst();
			while(!child.getItem().getBlockName().equals(childName) && loc<size) {
				child = (Item) copy.get(loc);
				loc++;
			}

			rows = ((ChanceBlock) current.getItem()).getRows();
			columns = ((ChanceBlock) current.getItem()).getColumns();

			// Need to find the Node in the new list to make
			// the changes too.
			for(int p=0;p<current.numParents();p++) {
				Item parent;

				parentName = current.getParent(p).getItem().getBlockName();

				loc = 0;
				size = copy.size();
				parent = (Item) copy.getFirst();

				while(!parent.getItem().getBlockName().equals(parentName) && loc<size) {
					parent = (Item) copy.get(loc);
					loc++;
				}

				parent.setChild(child);
				child.setParent(parent);
			}

			// Copy the probabilities over
			for(int j=0;j<columns;j++) {
				for(int k=0;k<rows;k++) {
					((ChanceBlock) child.getItem()).setValue(((ChanceBlock) current.getItem()).getValue(k, j), k, j);
				}
			}

		}

		return copy;
	}

	/**
	  * This takes the most recent copy of the Network on the undo Vector
	  * and sets it as the current Network.
	  */
	private void undo() {
		LinkedList object;
		int size;

		size = vundo.size();
		object = (LinkedList) vundo.remove(size - 1);

		addredo();

		draw.setBayNet(object);

		if(size == 1) {
			// this means we undid the last item
			undo.setEnabled(false);
		}
	}

	/**
	  * This takes the newest copy of the Network off the redo Vector
	  * and sets it to be the current Network.
	  */
	private void redo() {
		LinkedList object;
		int size;

		size = vredo.size();
		object = (LinkedList) vredo.remove(size - 1);

		addundo();

		draw.setBayNet(object);

		if(size==1) {
			// there are no more item left in the vector
			redo.setEnabled(false);
		}
	}

	/**
	  * This closes the viewer.  If the current network has not been
	  * saved then it will prompt the user.
	  */
	private synchronized void close() {
		SaveDialog sdialog;
		int ret;

		if(draw.isSaved()) {
			frame.dispose();
		} else {
			sdialog = new SaveDialog(frame, filename, true);
			sdialog.show();

			ret = sdialog.value();
			switch(ret) {
				case(1): {
					save();
					frame.dispose();
					break;
				}
				case(2): {
					frame.dispose();
					break;
				}
				default: {
					// do nothing
				}
			}
		}
                System.exit(0);
	}

	/**
	  * Calculates and redraws the treetable.
	  */
	private void redrawTreeTable() {
		editnode.check();
		redraw();
	}

	/**
	  * Creates and draws the edit area corresponding to a particular
	  * node.
	  *
	  * @param choice 0 = delete state 1 = add state
	  */
	public void redrawEditNodeArea(Item tmp, int choice, boolean check) {
		// need to add methods for different things
		cb = tmp;
		if(check) editnode.check();
		editnode = new EditNode(tmp, choice);
		redraw();
	}

	/**
	  * Redraws the entire window and updates the components
	  * accordingly.
	  */
	private void redraw() {
		panel.removeAll();

		gridbag = new GridBagLayout();
		gbc = new GridBagConstraints();
		panel.setLayout(gridbag);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gridbag.setConstraints(toolbar,gbc);
		panel.add(toolbar);

		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(editnode,gbc);
		panel.add(editnode);

		//gbc.gridwidth = GridBagConstraints.REMAINDER;
		//gridbag.setConstraints(label,gbc);

		gbc.ipady = boardheight;
		gbc.ipadx = boardwidth;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(draw,gbc);
		panel.add(draw);

		//panel.add(label);

		frame.validate();
	}

	/**
	  * Make a dialog appear for this particular node asking which
	  * state needs to be deleted.
	  *
	  * @param block - the nature node that is having a state removed
	  */
	public void deleteDialog(ChanceBlock block) {
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		final LinkedList states = block.getAttributeNames();

		dialog = new JDialog(frame,"Delete States",true);
		dialog.getContentPane().setLayout(layout);

		JLabel label = new JLabel("Choose a state to delete.");

		final JComboBox combobox = new JComboBox();
		combobox.addItem("<none>");
		for(int i=0;i<block.numAttributes();i++) {
			combobox.addItem(states.get(i));
		}
		combobox.setEnabled(true);

		JButton done = new JButton("Done");
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state=combobox.getSelectedItem().toString();
				dialog.dispose();
				if(!state.equals("<done>")) {
					//System.out.println(states.indexOf(state));
					((ChanceBlock) cb.getItem()).delete(((ChanceBlock) cb.getItem()).getAttributeNames().indexOf(state));
					redrawEditNodeArea(cb,0,false);
				}
			}
		});

		constraints.gridwidth = GridBagConstraints.RELATIVE;
		constraints.anchor = GridBagConstraints.NORTH;
		layout.setConstraints(label,constraints);

		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(done,constraints);

		constraints.insets=new Insets(0,50,30,0);
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		layout.setConstraints(combobox,constraints);

		dialog.getContentPane().add(label);
		dialog.getContentPane().add(combobox);
		dialog.getContentPane().add(done);

		dialog.pack();
		dialog.show();
	}

	/**
	  * This function should actually be pushing the data somewhere in
	  * d2k, but I don't know how to do it based on a user click.
	  */
	private void getStandards() {
		GraphErrorDialog ged;
		CountGraphErrors cge;
		File file;
		String name;

		ged = new GraphErrorDialog(frame, true);

		fchoose.setDialogTitle("Compare Gold Standard");
		fchoose.showOpenDialog(frame);

		file = fchoose.getSelectedFile();
		name = file.getAbsolutePath();

		if(file.getName() != null) {
			cge = new CountGraphErrors(draw.getBayNet(), name);

			ged.setAddition(cge.countErrorAddition());
			ged.setDeletion(cge.countErrorDeletion());
			ged.setReversal(cge.countErrorReversal());
			ged.show();
		}
	}

	/**
	  * This function should actually be pushing the data somewhere in
	  * d2k, but I don't know how to do it based on a user click.
	  */
	private void inference() {
/*
                FileIO file;
               LinkedList bn;
		//CreateCliqueTree graph;
                 CreateCliqueTreeParallel graph;
		CliqueProbabilityDriver cpd;

                file = new FileIO();
                bn = file.load(filename);
                // print the starting time
        Calendar cal1 = Calendar.getInstance();
        Date startTime1 = cal1.getTime();
        System.out.println("Start time of the first graph transform step is:"+ startTime1);

		//graph = new CreateCliqueTree(bn);
                 graph = new CreateCliqueTreeParallel(bn);
		cpd = new CliqueProbabilityDriver();

                //Phase 1 : create the permanent clique tree from the input BN
                //graph tranform
		graph.moralize();
		graph.triangulate();
		graph.build();

Calendar cal2 = Calendar.getInstance();
        Date endTime1 = cal2.getTime();
        System.out.println("End time of the first graph transform step is:"+ endTime1);
                //Phase 2 : compute the prob of cliques and query varialbes
                //          from the input evidnce file and the permanent clique
                //          tree from phase 1
                // message passing

		cpd.setRoot((Clique) graph.getCliqueTree());
		cpd.doIt();
		System.out.println(cpd.report());

        Calendar cal3 = Calendar.getInstance();
        Date endTime2 = cal3.getTime();
        System.out.println("Start time of the first graph transform step is:"+ startTime1);
       System.out.println("End time of the first graph transform step is:"+ endTime1);
        System.out.println("End time of the second probs computation is:"+endTime2);

		// do something to send this to Ben's stuff here
                */
	}

}
