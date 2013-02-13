//http://cubiccow.blogspot.co.uk/2012/06/spring-in-osgi-environments.html
package org.societies.osgi;

import org.osgi.framework.BundleContext;
import org.springframework.context.ApplicationContext;
import org.springframework.osgi.context.ConfigurableOsgiBundleApplicationContext;
import org.springframework.osgi.web.context.support.OsgiBundleXmlWebApplicationContext;

import javax.servlet.ServletContext;

public class OsgiWebBundleContext extends OsgiBundleXmlWebApplicationContext {

    @Override
    public void setServletContext(ServletContext servletContext) {

        if (getBundleContext() == null) {
            if (servletContext != null) {
                Object context = servletContext
                        .getAttribute("osgi-bundlecontext");//<--- only change

                if (context != null) {
                    this.logger.debug("Using the bundle context "
                            + "located in the servlet context at osgi-bundlecontext");

                    setBundleContext((BundleContext) context);
                }

            }

            ApplicationContext parent = getParent();

            if ((parent instanceof ConfigurableOsgiBundleApplicationContext)) {
                this.logger.debug("Using the application context parent's bundle context");
                setBundleContext(((ConfigurableOsgiBundleApplicationContext) parent)
                        .getBundleContext());
            }
        }

        //to call "this.servletContext = servletContext;" in super
        super.setServletContext(servletContext);
    }

}
