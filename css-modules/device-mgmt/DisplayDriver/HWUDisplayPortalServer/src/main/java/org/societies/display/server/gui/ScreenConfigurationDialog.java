package org.societies.display.server.gui;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;


import org.societies.display.server.model.Screen;
import org.societies.display.server.model.ScreenConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author  Administrator
 * @created July 13, 2012
 */
public class ScreenConfigurationDialog extends JDialog
{
    private static Logger logging = LoggerFactory.getLogger(ScreenConfigurationDialog.class);

    static ScreenConfigurationDialog theScreenConfigurationDialog;

	JPanel pnPanel0;

	JPanel pnMainPanel;
	JTable configJTable;

	JPanel pnButtonsPanel;
	JButton btnSave;

	private ConfigurationTableModel configModel;

	private JPopupMenu mainPopupMenu;

	private ScreenConfiguration screenConfig;

	private Pattern pattern;
	private Matcher matcher;

	private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";



	/**
	 */
	public static void main( String args[] ) 
	{
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch ( ClassNotFoundException e ) 
		{
		}
		catch ( InstantiationException e ) 
		{
		}
		catch ( IllegalAccessException e ) 
		{
		}
		catch ( UnsupportedLookAndFeelException e ) 
		{
		}
		theScreenConfigurationDialog = new ScreenConfigurationDialog();
	}


	public ScreenConfiguration getScreens(){

		return this.screenConfig;
	}

    public void readConfigFile()
    {


    }

	/**
	 */
	public ScreenConfigurationDialog() 
	{

		super(new JFrame(), "Screen Configuration", true);
        this.logging.debug("ScreenConfigurationDialogue Started : ");


        this.screenConfig = new ScreenConfiguration();
		pattern = Pattern.compile(IPADDRESS_PATTERN);
		this.createPopupMenu();
		this.setTitle("Screen Configuration");
		//super( OWNER, "TITLE", MODAL );

		pnPanel0 = new JPanel();
		GridBagLayout gbPanel0 = new GridBagLayout();
		GridBagConstraints gbcPanel0 = new GridBagConstraints();
		pnPanel0.setLayout( gbPanel0 );

		pnMainPanel = new JPanel();
		pnMainPanel.setBorder( BorderFactory.createTitledBorder( "Screens Configuration Details" ) );
		GridBagLayout gbMainPanel = new GridBagLayout();
		GridBagConstraints gbcMainPanel = new GridBagConstraints();
		pnMainPanel.setLayout( gbMainPanel );


		configModel = new ConfigurationTableModel();
		configJTable = new JTable( configModel);
		/**
		 * setup table mouse adapter
		 */

		configJTable.addMouseListener(new MousePopupListener());

		JScrollPane scpTable0 = new JScrollPane( configJTable );
		scpTable0.setToolTipText("Right click to show menu");
		scpTable0.addMouseListener(new MousePopupListener());
		gbcMainPanel.gridx = 0;
		gbcMainPanel.gridy = 5;
		gbcMainPanel.gridwidth = 20;
		gbcMainPanel.gridheight = 5;
		gbcMainPanel.fill = GridBagConstraints.BOTH;
		gbcMainPanel.weightx = 1;
		gbcMainPanel.weighty = 1;
		gbcMainPanel.anchor = GridBagConstraints.NORTH;
		gbcMainPanel.insets = new Insets( 10,10,10,10 );
		gbMainPanel.setConstraints( scpTable0, gbcMainPanel );
		pnMainPanel.add( scpTable0 );

		pnButtonsPanel = new JPanel();
		GridBagLayout gbButtonsPanel = new GridBagLayout();
		GridBagConstraints gbcButtonsPanel = new GridBagConstraints();
		pnButtonsPanel.setLayout( gbButtonsPanel );

		btnSave = new JButton( "Save Configuration"  );
		btnSave.addActionListener(new ButtonListener());
		gbcButtonsPanel.gridx = 15;
		gbcButtonsPanel.gridy = 2;
		gbcButtonsPanel.gridwidth = 1;
		gbcButtonsPanel.gridheight = 1;
		gbcButtonsPanel.fill = GridBagConstraints.VERTICAL;
		gbcButtonsPanel.weightx = 1;
		gbcButtonsPanel.weighty = 0;
		gbcButtonsPanel.anchor = GridBagConstraints.EAST;
		gbcButtonsPanel.insets = new Insets( 25,0,25,25 );
		gbButtonsPanel.setConstraints( btnSave, gbcButtonsPanel );
		pnButtonsPanel.add( btnSave );
		gbcMainPanel.gridx = 0;
		gbcMainPanel.gridy = 10;
		gbcMainPanel.gridwidth = 20;
		gbcMainPanel.gridheight = 6;
		gbcMainPanel.fill = GridBagConstraints.BOTH;
		gbcMainPanel.weightx = 1;
		gbcMainPanel.weighty = 0;
		gbcMainPanel.anchor = GridBagConstraints.NORTH;
		gbMainPanel.setConstraints( pnButtonsPanel, gbcMainPanel );
		pnMainPanel.add( pnButtonsPanel );
		JScrollPane scpMainPanel = new JScrollPane( pnMainPanel );
		gbcPanel0.gridx = 0;
		gbcPanel0.gridy = 0;
		gbcPanel0.gridwidth = 20;
		gbcPanel0.gridheight = 20;
		gbcPanel0.fill = GridBagConstraints.BOTH;
		gbcPanel0.weightx = 1;
		gbcPanel0.weighty = 0;
		gbcPanel0.anchor = GridBagConstraints.NORTH;
		gbPanel0.setConstraints( scpMainPanel, gbcPanel0 );
		pnPanel0.add( scpMainPanel );

        //LOAD SCREENS FROM FILE
        loadFileScreens();

		setDefaultCloseOperation( DISPOSE_ON_CLOSE );

		setContentPane( pnPanel0 );
		pack();
		setVisible( true );

	}

    /**
     * Method to load screen configurations from file onto the GUI
     */
    public void loadFileScreens() {
       List<Screen> fileScreens = screenConfig.getAllScreens();
        if(fileScreens.isEmpty())
        {
            configModel.addEmptyRow();
        }
        else
        {

            Vector row; //= new Vector();
            for(Screen newScreen : fileScreens)
            {
                row = new Vector();
                row.add(0, newScreen.getScreenId());
                row.add(1, newScreen.getLocation());
                row.add(2, newScreen.getIpAddress());
                configModel.addRow(row);
            }
        }

    }

	public boolean checkIp(String sip) {
		matcher = pattern.matcher(sip);
		return matcher.matches();
	}
	private void createPopupMenu() {
		mainPopupMenu = new JPopupMenu();
		MenuActionListener menuListener = new MenuActionListener();
		JMenuItem addLinesItem = new JMenuItem("Add more screens");
		addLinesItem.setActionCommand("add");
		addLinesItem.setHorizontalTextPosition(JMenuItem.CENTER);
		addLinesItem.addActionListener(menuListener);
		JMenuItem removeLineItem = new JMenuItem("Remove screen");
		removeLineItem.setActionCommand("remove");
		removeLineItem.setHorizontalTextPosition(JMenuItem.CENTER);
		removeLineItem.addActionListener(menuListener);

		mainPopupMenu.add(addLinesItem);
		mainPopupMenu.add(removeLineItem);


	}


    // An inner class to check whether mouse events are the popup trigger
	class MousePopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		private void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				mainPopupMenu.show(ScreenConfigurationDialog.this, e.getX(), e.getY()+20);
			}
		}
	}

	class MenuActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("add")){
				configModel.addEmptyRow();
			}else if (e.getActionCommand().equals("remove")){
				if (configJTable.getSelectedRow()>=0){
					configModel.removeRow(configJTable.getSelectedRow());
				}else{
					JOptionPane.showMessageDialog(ScreenConfigurationDialog.this, "Please select a row to delete.");
				}
			}

		}


	}

	class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {

			if (configModel.getRowCount()<=0){
				JOptionPane.showMessageDialog(ScreenConfigurationDialog.this, "Please enter at least one screen configuration", "Error", JOptionPane.ERROR_MESSAGE, null);
				return;
			}

            //****CLEAR SCREEN ARRAY SO THERE ARE NO DUPLICATES****//
            screenConfig.clearScreens();

			for (int i = 0; i < configModel.getRowCount(); i++){
				String ipAddress = configModel.getIPAddress(i);
				if (checkIp(ipAddress)){
					Screen screen = new Screen(configModel.getScreenId(i), configModel.getLocation(i), configModel.getIPAddress(i));
					screenConfig.addScreen(screen);
                    logging.debug("Screen added: " + screen.getScreenId());
                }else{
					JOptionPane.showMessageDialog(ScreenConfigurationDialog.this, "IP Address: "+ipAddress+" is not valid. Please correct and try again.", "Invalid IP Address", JOptionPane.ERROR_MESSAGE, null);
					screenConfig.clearScreens();
					return;
				}

			}
			ScreenConfigurationDialog.this.dispose();
		}


	}
} 
