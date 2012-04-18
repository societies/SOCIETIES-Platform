package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
  * The GraphicalPanel class is responsible for holding the GraphicalBlock
  * class.
  *
  * @author Laura Kruse
  * @version v1.6
  */
public class GraphicalPanel extends JPanel {
	private GraphicalBlock b;

	private final int boardwidth = 2000;
	private final int boardheight = 1000;

	/**
	  * Creates a new JPanel that contains the drawing area.
	  */
	public GraphicalPanel() {
		this.setLayout(new BorderLayout());
		b = new GraphicalBlock(null);
		this.add(b);

		this.setSize(new Dimension(boardwidth, boardheight));
		this.setVisible(true);
	}

	/**
	  * Creates a new Panel with a specific parent that contains
	  * the drawing area for the nodes
	  *
	  * @param parent the parent frame that this resides in
	  */
	public GraphicalPanel(XBN parent) {
		this.setLayout(new BorderLayout());
		b = new GraphicalBlock(parent);
		this.add(b);

		this.setSize(new Dimension(boardwidth, boardheight));
		this.setVisible(true);
	}

	/**
	  * Changes the drawing mode of the panel, it allows for inserting,
	  * deleting, and connecting of the nodes.
	  *
	  * @param type the new draw type
	  */
	public void changeDrawType(int type) {
		b.setMode(type);
	}

	/**
	  * Returns the Bayesian Network that is currently being used.
	  *
	  * @return b.getBayNet() - the Bayesian Network that is being used
	  */
	public LinkedList getBayNet() {
		return b.getBayNet();
	}

	/**
	  * Sets the current Bayesian Network to be this one.
	  *
	  * @param llist1 the new Bayesian Network
	  */
	public void setBayNet(LinkedList llist1) {
		b.setBayNet(llist1);
	}

	/**
	  * This function returns whether the current Bayesian Network has
	  * been saved or not.
	  *
	  * @return b.isSaved() - a boolean stating whether the Bayesian
	  *	Network has been saved or not
	  */
	public boolean isSaved() {
		return b.isSaved();
	}

	/**
	  * This function allows for the Bayesian Network to be set to a
	  * saved state or to an unsaved state.
	  *
	  * @param saved - a boolean stating whether the Bayesian Network
	  *	 has been saved or not
	  */
	public void setSaved(boolean saved) {
		b.setSaved(saved);
	}
}
