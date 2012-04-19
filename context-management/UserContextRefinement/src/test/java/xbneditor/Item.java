package xbneditor;

import java.awt.geom.*;
import java.util.*;

/**
 * This class is responsible for keeping track of the node and the corresponding
 * graphical representation of the node.
 * 
 * @author Laura Kruse
 * @version v1.3
 */
public class Item extends Object {
	private Block b;
	private Ellipse2D.Double e;

	private Vector parent;
	private Vector child;

	/**
	 * Creates a new item with no associated Block or Ellipse2D.Double
	 */
	public Item() {
		b = new ChanceBlock("", 0, 0, true);
		e = null;
		parent = new Vector();
		child = new Vector();
	}

	/**
	 * Creates a new item with a specific Block and Ellipse2D.Double
	 * 
	 * @param b
	 *            the Block that is being added to the editor
	 * @param e
	 *            the Ellipse2D.Double corresponding to that Block
	 */
	public Item(Block b, Ellipse2D.Double e) {
		this.b = b;
		this.e = e;

		parent = new Vector();
		child = new Vector();
	}

	/**
	 * Gets the node corresponding to this item
	 * 
	 * @return Block - b
	 */
	public Block getItem() {
		return b;
	}

	/**
	 * Returns the Ellipse2D.Double associated with this item
	 * 
	 * @return Ellipse2D.Double - e
	 */
	public Ellipse2D.Double getEllipse() {
		return e;
	}

	/**
	 * Gets a specific parent associated with this Item
	 * 
	 * @param i
	 *            the location of the parent
	 * @return Item
	 */
	public Item getParent(int i) {
		return (Item) parent.elementAt(i);
	}

	public Vector getParents() {
		return parent;
	}

	/**
	 * Associates the Item with a new parent.
	 * 
	 * @param i
	 *            the Item to add onto the parent list
	 */
	public void setParent(Item i) {
		parent.add(i);
	}

	/**
	 * Returns the number of parents this particular Item has.
	 * 
	 * @return int
	 */
	public int numParents() {
		return parent.size();
	}

	/**
	 * Deletes a specific Item from the parent list.
	 * 
	 * @Item i the Item to be deleted
	 */
	public void deleteParent(Item item) {
		parent.remove(item);
	}

	/**
	 * Gets a specific child associated with this Item
	 * 
	 * @param i
	 *            the location of the child
	 * @return Item
	 */
	public Item getChild(int i) {
		return (Item) child.elementAt(i);
	}

	public Vector getChildren() {
		return child;
	}

	/**
	 * Associates the Item with a new child.
	 * 
	 * @param i
	 *            the Item to add onto the child list
	 */
	public void setChild(Item i) {
		child.add(i);
	}

	/**
	 * Returns the number of children this particular Item has.
	 * 
	 * @return int
	 */
	public int numChild() {
		return child.size();
	}

	/**
	 * Deletes a specific Item from the child list.
	 * 
	 * @Item i the Item to be deleted
	 */
	public void deleteChild(Item item) {
		child.remove(item);
	}

	public void print() {
		System.out.println("---nodename : " + b.getBlockName());
		System.out.println("---node position : " + Double.toString(e.getX())
				+ " ," + Double.toString(e.getY()));
	}
}
