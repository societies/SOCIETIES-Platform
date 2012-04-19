package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

/**
  * This is the basic node class that all of the other type of
  * nodes will inherit from.&nbsp;It contains the values that are
  * applicate to each of the other classes.&nbsp;(e.g. the name of
  * the node, the type of node, it's location on the drawing
  * area, and any additional comments that can be included with
  * the node.
  *
  * @author Laura Kruse
  * @version v1.3
  */
public class Block extends Object {
	private String name;
	private String type;

	private double x;
	private double y;

	private String comments;

	/**
	  * Default constructor that is called when the class
	  * is being inherited from.
	  */
	public Block() {
	}

	/**
	  * Constructor that contains specific information about certian
	  * nodes.
	  *
	  * @param name the name of this node
	  * @param x the x coordinate of this node
	  * @param y the y coordinate of this node
	  * @param type the type of this node
	  */
	public Block(String name, double x, double y, String type) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.type = type;
	}

	/**
	  * Returns the name of the block
	  *
	  * @return name - returns the name associated with this node
	  */
	public String getBlockName() {
		return name;
	}

	/**
	  * Sets the block name to something specific
	  *
	  * @param name the new Block name
	  */
	public void setBlockName(String name) {
		this.name = name;
	}

	/**
	  * Sets the x,y coordinates of the block
	  */
	public void setCoordinates(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	  * Returns the value of the x coordinate
	  *
	  * @return x the x coordinate
	  */
	public double getx() {
		return x;
	}

	/**
	  * Returns the value of the y coordinate
	  *
	  * @return y the y coordinate
	  */
	public double gety() {
		return y;
	}

	/**
	  * Sets the type of this node.  A node can be a nature, decision, or
	  * utility node.
	  *
	  * @param type the type of the node
	  */
	public void setType(String type) {
		this.type = type;
	}

	/**
	  * Returns the type of the node.
	  *
	  * @return type - the type of the node
	  */
	public String getType() {
		return type;
	}
}
