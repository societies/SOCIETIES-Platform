package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
  * This is a standalone class that will allow the user the ability to call
  * a dialog that will ask for the name of a bayesian network, and the gold
  * standard graph, so that they can be compared for the calculation of
  * graph errors.
  *
  * @author Laura Kruse
  * @version v1.0
  */
public class GraphErrorInput extends JFrame {
	//private JFrame frame;
	private JDialog frame;

	private JPanel one;
	private JPanel two;
	private JPanel three;

	private JFileChooser fchoose;

	private JLabel fncurrent;
	private JLabel fngold;

	private JButton current;
	private JButton goldstandard;
	private JButton done;

	/**
	  * Creates a new dialog and prompts the user to input two particular
	  * filenames.
	  */
	public GraphErrorInput() {
		//frame = new JFrame("Graph Errors");
		frame = new JDialog(this, "Graph Errors", true);

		one = new JPanel();
		two = new JPanel();
		three = new JPanel();

		fchoose = new JFileChooser();

		fncurrent = new JLabel("Select Your Created Network");
		fngold = new JLabel("Select the Gold Standard Network");

		current = new JButton("Select");
		goldstandard = new JButton("Select");
		done = new JButton("Continue");

		frame.getContentPane().setLayout(new GridLayout(3, 1));

		one.setLayout(new GridLayout(1, 2));
		one.add(fncurrent);
		one.add(current);
		current.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadCurrent();
			}
		});

		two.setLayout(new GridLayout(1, 2));
		two.add(fngold);
		two.add(goldstandard);
		goldstandard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadGoldStandard();
			}
		});

		three.setLayout(new FlowLayout());
		three.add(done);
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});

		frame.getContentPane().add(one);
		frame.getContentPane().add(two);
		frame.getContentPane().add(three);

		frame.pack();
	}

	/**
	  * Shows the dialog.
	  */
	public void show() {
		frame.show();
	}

	/**
	  * Gets the path name of the current network.
	  *
	  * @return the path to the file that stores the current network
	  */
	public String getCurrentNetwork() {
		return fncurrent.getText();
	}

	/**
	  * Gets the path name of the bayesian network
	  *
	  * @return the path to the file that stores the gold standard network
	  */
	public String getGoldStandard() {
		return fngold.getText();
	}

	/**
	  * Loads a dialog that will prompt the user to input the current
	  * network.
	  */
	private void loadCurrent() {
		File file;

		fchoose.setDialogTitle("Load K2 Network");
		fchoose.showOpenDialog(frame);

		file = fchoose.getSelectedFile();
		fncurrent.setText(file.getAbsolutePath());
	}

	/**
	  * Loads a dialog that will prompt the user to input the gold
	  * standard network.
	  */
	private void loadGoldStandard() {
		File file;

		fchoose.setDialogTitle("Load Gold Standard Network");
		fchoose.showOpenDialog(frame);

		file = fchoose.getSelectedFile();
		fngold.setText(file.getAbsolutePath());
	}

	/**
	  * Closes the dialog.
	  */
	private void close() {
		frame.dispose();
	}
}
