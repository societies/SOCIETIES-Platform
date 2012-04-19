package xbneditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.lang.Math;
import java.util.*;
import javax.swing.*;

/**
  * This class contains all of the information for drawing the
  * corresponding blocks on to the panel and allows the user to
  * manipulate them.
  *
  * @author Laura Kruse
  * @version v1.7
  */
public class GraphicalBlock extends JPanel {
	private Graphics2D g2;

	private LinkedList img;

	private Rectangle area;

	private boolean pressOut = false;
	private boolean saved = true;

	private Item active;
	private Ellipse2D.Double current;
	private int mode;	// 0 - add, 1 - connect, 2 - delete
	private boolean newnode;

	final XBN parent;
	private Item first;
	private Item focus;
	private String firstname;

	private PopupMenu popup = new PopupMenu();
	private MenuItem insert = new MenuItem ("Insert State");
	private MenuItem delete = new MenuItem ("Delete State");
	private MenuItem remove	= new MenuItem ("Delete Node");

	private final int width = 80;
	private final int height = 50;

	private final int boardwidth = 2000;
	private final int boardheight = 1000;

	/**
	  * Constructs a new instance of the drawing area with a
	  * default background and starts out with no nodes that
	  *
	  * @param parent the parent frame, this is needed so that
	  *	static updates can be done relative to the location of
	  *	the node that is currently being moved
	  */
	public GraphicalBlock(XBN frame) {
		parent = frame;
		mode = 0;
		img = new LinkedList();

		area = new Rectangle(boardwidth, boardheight);
		this.setSize(boardwidth, boardheight);

		this.add(popup);
		popup.add(insert);
		insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.redrawEditNodeArea(active,1,false);
			}
		});

		popup.add(delete);
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.deleteDialog((ChanceBlock) active.getItem());
			}
		});

		popup.addSeparator();

		popup.add(remove);
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remove(active);
				repaint();
			}
		});

		// probally add the popup menu to the objects that need it

		this.setBackground(Color.white);
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				if(!pressOut && mode!=1) {
					updateLocation(e);
				}
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				parent.addundo();

				if((e.getModifiers()==InputEvent.BUTTON3_MASK) && (active=getItem(e))!=null) {
					popup.show(e.getComponent(),e.getX(),e.getY());
				} else {
					press(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				release(e);
			}

			public void mouseClicked(MouseEvent e) {
				click(e);
				saved = false;
			}
		});

		this.setVisible(true);
	}

	/**
	  * Updates the current location of the node that is being
	  * moved around by the user.
	  *
	  * @param e contains the location of the objects current position
	  */
	public void updateLocation(MouseEvent e) {
		if(first!=null) {
			first.getItem().setCoordinates(e.getX(),e.getY());
		}
		current.setFrame(e.getX() - width / 2, e.getY() - height / 2, current.getWidth(), current.getHeight());

		checkImage();

		//if(checkImage()) {
		//	XBN.label.setText("Image located at " + current.getX() + ", " + current.getY());
		//}

		this.repaint();
	}

	/**
	  * Repaints the graphical manipulation area
	  *
	  * @param g the Graphics that needs to be repainted
	  */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D tmp = (Graphics2D) g;
		tmp.setStroke(new BasicStroke());

		tmp.setPaint(Color.white);
		tmp.fillRect(0, 0, boardwidth, boardheight);
		tmp.setPaint(Color.black);

		// Draw the lines connecting the nodes first.
		Item line;

		for(int loc=0;loc<img.size();loc++) {
			line = (Item) img.get(loc);

			int num;

			if((num=line.numChild())!=0) {
				for(int i=0;i<num;i++) {
					double x1 = line.getItem().getx();
					double y1 = line.getItem().gety();
					double x2 = line.getChild(i).getItem().getx();
					double y2 = line.getChild(i).getItem().gety();

					int startx = (int) x1;
					int starty = (int) y1;
					int endx = (int) x2;
					int endy = (int) y2;
					drawArc(x1,x2,y1,y2,tmp);
				}
			}
		}

		// Now draw the nodes.
		Item tmp1;

		for(int loc=0;loc<img.size();loc++) {
			tmp1 = (Item) img.get(loc);

			Ellipse2D.Double tmp2 = tmp1.getEllipse();
			String name = tmp1.getItem().getBlockName();

			long len = name.length();

			tmp.setPaint(new Color(255, 255, 180));
			tmp.fill(tmp2);
			tmp.setColor(Color.black);

			if(len > 10) {
				name = name.substring(0, 10);
				name = name.concat("...");
				len = 10;
			}

			tmp.setStroke(new BasicStroke(2.0f));
			tmp.drawString(name, new Double(tmp2.getX() + 4 * (10 -len)).intValue(), new Double(tmp2.getY() + 30).intValue());

			if(tmp2 != current) {
				tmp.setStroke(new BasicStroke());
			}

			tmp.draw(tmp2);
		}

		/*
		Item line;

		for(int loc=0;loc<img.size();loc++) {
			line = (Item) img.get(loc);

			int num;

			if((num=line.numChild())!=0) {
				for(int i=0;i<num;i++) {
					double x1 = line.getItem().getx();
					double y1 = line.getItem().gety();
					double x2 = line.getChild(i).getItem().getx();
					double y2 = line.getChild(i).getItem().gety();

					int startx = (int) x1;
					int starty = (int) y1;
					int endx = (int) x2;
					int endy = (int) y2;
					drawArc(x1,x2,y1,y2,tmp);
				}
			}
		}
		*/
	}

	private boolean cyclic(String name, Item child) {
		if(name.equals(child.getItem().getBlockName())) {
			return true;
		}

		for(int i=0;i<child.numChild();i++) {
			if(cyclic(name, child.getChild(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	  * Checks to make sure that the image is located within the
	  * boundry of the drawing area.&nbsp;If the user trys to move it
	  * outside the area then it just redraws it at the nearest edge.
	  */
	private boolean checkImage() {
		// need to do some changing to make sure that the arrows
		// don't draw off the screen either
		if(area==null) {
			return false;
		}
		if(area.contains(current.x, current.y, width, height)) {
			return true;
		}

		double newx = current.x;
		double newy = current.y;

		if(current.x + width > area.getWidth()) {
			newx = (int) area.getWidth() - width - 1;
		}
		if(current.x < 0) {
			newx = 0;
		}
		if(current.y + height > area.getHeight()) {
			newy = (int) area.getHeight() - height - 1;
		}
		if(current.y < 0) {
			newy = 0;
		}

		current.setFrame(newx, newy, current.getWidth(), current.getHeight());
		focus.getItem().setCoordinates(newx + width / 2, newy + height / 2);
		return false;
	}

	/**
	  * Sets the drawing type, this allows for deletions and insersions
	  *
	  * @param mode the current draw type
	  */
	public void setMode(int mode) {
		this.mode = mode;
	}

	/**
	  * Returns the item that was being affected by the most recent
	  * mouse event.
	  *
	  * @param e the Mouse Event that took place
	  * @return Item - the item that was affected by it
	  */
	private Item getItem(MouseEvent e) {
		Item tmp;

		for(int loc=0;loc<img.size();loc++) {
			tmp = (Item) img.get(loc);

			if(tmp.getEllipse().contains(e.getX(),e.getY())) {
				current = tmp.getEllipse();
				return tmp;
			}
		}
		return null;
	}

	/**
	  * Registers that the mouse has been pressed and
	  * then runs through the list of images to find out
	  * which particular node caused the event.
	  *
	  * @param e the MouseEvent that took place
	  */
	private void press(MouseEvent e) {
		// check through the list of images
		Item tmp;

		if((tmp=getItem(e))!=null) {
			first = tmp;
			firstname = tmp.getItem().getBlockName();
			newnode = false;
			focus=tmp;
		} else if(mode==0) {
			ChanceBlock cb;
			Ellipse2D.Double ed;

			int x;
			int y;

			x = new Double(e.getX()).intValue();
			y = new Double(e.getY()).intValue();

			cb = new ChanceBlock("C" + img.size(), x, y, true);
			ed = new Ellipse2D.Double(x, y, width, height);
			tmp = new Item(cb, ed);

			img.add(tmp);
			((Item) img.getLast()).getItem().setCoordinates(x, y);

			focus = (Item) img.getLast();
			current = focus.getEllipse();

			first = null;
			newnode = true;
		}
		if(mode!=1) updateLocation(e);
	}

	/**
	  * Registeres that the mouse has been released and
	  * then runs through the list of images to find out
	  * which particular node(s) caused the event.  If the
	  * mode is set to 1 that means two nodes are being
	  * connected togeather, and a line needs to be drawn
	  * to connect them.  In addition the parents need to
	  * be set for the corresponding nodes.
	  *
	  * @param e the MouseEvent that took place
	  */
	private void release(MouseEvent e) {
		if(mode==1) {
			Item tmp;
			if((tmp=getItem(e))!=null) {
				// update parent child list
				if(firstname!=tmp.getItem().getBlockName() && first!=null) {
					if(!cyclic(first.getItem().getBlockName(), tmp)) {
						tmp.setParent(first);
						first.setChild(tmp);
					} else {
						JOptionPane.showMessageDialog(null, "Bayesian Networks are not allowed to be cyclic.", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
				first=null;
				this.repaint();
			}
		} else {
			if(current.contains(e.getX(),e.getY())) {
				updateLocation(e);
			} else {
				pressOut = false;
			}
		}
	}

	/**
	  * Registers that the mouse has been clicked.
	  * This is always preformed after press and
	  * release are called.
	  *
	  * @param e the MouseEvent that took place
	  */
	private void click(MouseEvent e) {
		// find the image and update the edit panel
		Item tmp = getItem(e);
		if(mode!=2 && tmp!=null) {
			tmp.getItem().setCoordinates(e.getX(),e.getY());
			current = tmp.getEllipse();
			if(newnode) {
				parent.redrawEditNodeArea(tmp,0,false);
			} else {
				parent.redrawEditNodeArea(tmp,0,true);
			}
		} else {
			remove(tmp);
		}
	}

	/**
	  * This function removes the current item from the Network.  The
	  * node will then no longer exist in the list.
	  *
	  * @param tmp the node to be deleted
	  */
	private void remove(Item tmp) {

		// Remove the pointers to the children.
		for(int i=0;i<tmp.numParents();i++) {
			int loc;

			// Check to make sure this item is in the list
			if((loc=img.indexOf(tmp.getParent(i))) != -1) {
				((Item) img.get(loc)).deleteChild(tmp);
			}
		}

		for(int i=0;i<tmp.numChild();i++) {
			int loc;

			// Check to make sure this item is in the list
			if((loc=img.indexOf(tmp.getChild(i))) != -1) {
				((Item) img.get(loc)).deleteParent(tmp);
			}
		}

		// Now that all of the parents and children have been
		// taken care of, we can delete the node.
		img.remove(tmp);
	}

	/**
	  * This function returns a the current Bayesian Network that needs
	  * to be saved to the disk.
	  *
	  * @return img - the current Bayesian Network that is acting as the
	  *	active network
	  */
	public LinkedList getBayNet() {
		return img;
	}

	/**
	  * This function takes a network from off the disk and makes it to be
	  * the active network that the user can modify and change as they
	  * see fit.
	  *
	  * @param llist - a LinkedList representing the new network
	  */
	public void setBayNet(LinkedList llist) {
		img = llist;
		if(img.size() > 0) {
			active = (Item) img.getFirst();
			current = active.getEllipse();

			parent.redrawEditNodeArea(active, 0, false);
		}
		repaint();
	}

	/**
	  * This function is responsible for drawing the arcs on the Bayesian
	  * Network graph.  It does all of the calculations that are necessary
	  * to preform this function.
	  *
	  * @param x1 the parents x coordinate
	  * @param x2 the childs x coordinate
	  * @param y1 the parents y coordinate
	  * @param y2 the childs y coordinate
	  * @param g the Graphics2D that is responsible for drawing the arcs
	  *	onto the palette.
	  */
	private void drawArc(double x1, double x2, double y1, double y2, Graphics2D g) {
		double x;
		double y;

		double a;
		double b;
		double d;

		double alpha;

		double r;
		double m;
		double n;

		double s;
		double t;

		double e;
		double f;

		x = x2 - x1;
		y = y2 - y1;

		a = width / 2;
		b = height / 2;
		d = 10.0;

		alpha = Math.atan(y/x);

		r = Math.sqrt((Math.pow(b, 2) * Math.pow(a, 2)) / ((Math.pow(b, 2) * Math.pow(Math.cos(alpha), 2)) + (Math.pow(a, 2) * Math.pow(Math.sin(alpha), 2))));

		if(x >= 0) {
			m = r * Math.cos(alpha);
			n = r * Math.sin(alpha);

			s = d * Math.cos(alpha);
			t = d * Math.sin(alpha);

			e = d / 2.0 * Math.sin(Math.PI - alpha);
			f = d / 2.0 * Math.cos(Math.PI - alpha);
		} else {
			m = -r * Math.cos(alpha);
			n = -r * Math.sin(alpha);

			s = -d * Math.cos(alpha);
			t = -d * Math.sin(alpha);

			e = -d / 2.0 * Math.sin(Math.PI - alpha);
			f = -d / 2.0 * Math.cos(Math.PI - alpha);
		}

		g.setStroke(new BasicStroke(2.0f));
		g.drawLine((int) (x1 + m), (int) (y1 + n), (int) (x2 - m), (int) (y2 - n));

		//drawArrow((int) (x2 - m - s - e), (int) (y2 - n - t - f), (int) (x2 - m - s), (int) (y2 - n - t), (int) (x2 - m), (int) (y2 - n), g);
		drawArrow((int) (x2 - m - s - e), (int) (y2 - n - t - f), (int) (x2 - m - s + e), (int) (y2 - n - t + f), (int) (x2 - m), (int) (y2 - n), g);
	}

	/**
	  * This method in is charge of the tip of the arrow for the
	  * nodes in the program.  It takes in three coordinate pairs
	  * and will draw the triangle that is represented by these verticies.
	  *
	  * @param x1 the x coordinate of the 1st coordinate pair
	  * @param x2 the y coordinate of the 1st coordinate pair
	  * @param y1 the x coordinate of the 2nd coordinate pair
	  * @param y2 the y coordinate of the 2nd coordinate pair
	  * @param z1 the x coordinate of the 3rd coordinate pair
	  * @param z2 the y coordinate of the 3rd coorditate pair
	  * @param g the graphics that will draw the triangle on the panel
	  */
	private void drawArrow(int x1, int x2, int y1, int y2, int z1, int z2, Graphics2D g) {
		int a1[] = { x1, y1, z1, x1 };
		int b1[] = { x2, y2, z2, x2 };

		g.fillPolygon(a1, b1, 4);
	}

	/**
	  * This function returns whether the file has been modifed since
	  * it was last saved.  If it is still in it's saved state the
	  * function will return true, otherwise the function will return
	  * false.
	  *
	  * @return saved - a boolean stating whether the current Bayesian
	  *	Network is saved or not
	  */
	public boolean isSaved() {
		return saved;
	}

	/**
	  * Sets the Bayesian network to a saved state or not.
	  *
	  * @param saved a boolean stating whether the network is saved or not
	  */
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
}
