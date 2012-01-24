/*
 * ExampleClientApp.java
 */

package org.societies.comm.examples.clientgui;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class ExampleClientApp extends SingleFrameApplication {

	private ExampleClientView exampleClientView;
	
    public ExampleClientView getExampleClientView() {
		return exampleClientView;
	}

	public void setExampleClientView(ExampleClientView exampleClientView) {
		this.exampleClientView = exampleClientView;
	}

	/**
     * At startup create and show the main frame of the application.
     */
//    @Override protected void startup() {
//        show(new ExampleClientView(this));
//    }

	 @Override protected void startup() {
	        show(this.getExampleClientView());
	    }
	
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of ExampleClientApp
     */
    public static ExampleClientApp getApplication() {
        return Application.getInstance(ExampleClientApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(ExampleClientApp.class, args);
    }
}
