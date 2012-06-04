package org.societies.da.openfire.plugin;

import gnu.inet.encoding.Stringprep;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.admin.AuthCheckFilter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.user.UserAlreadyExistsException;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.jivesoftware.util.Log;
import org.xmpp.packet.JID;

public class SocietiesServlet extends HttpServlet {

    private SocietiesPlugin plugin;


    @Override
	public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        plugin = (SocietiesPlugin) XMPPServer.getInstance().getPluginManager().getPlugin("societies");
 
        // Exclude this servlet from requiring the user to login
        AuthCheckFilter.addExclude("societies/societies");
        AuthCheckFilter.addExclude("societies/public/*");
    }

    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        // Printwriter for writing out responses to browser
        PrintWriter out = response.getWriter();

        if (!plugin.getAllowedIPs().isEmpty()) {
            // Get client's IP address
            String ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null) {
                ipAddress = request.getHeader("X_FORWARDED_FOR");
                if (ipAddress == null) {
                    ipAddress = request.getHeader("X-Forward-For");
                    if (ipAddress == null) {
                        ipAddress = request.getRemoteAddr();
                    }
                }
            }
            if (!plugin.getAllowedIPs().contains(ipAddress)) {
                Log.warn("User service rejected service to IP address: " + ipAddress);
                replyError("RequestNotAuthorised",response, out);
                return;
            }
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String type = request.getParameter("type");
        String secret = request.getParameter("secret");
        String groupNames = request.getParameter("groups");
        //No defaults, add, delete, update only
        //type = type == null ? "image" : type;
       
        // Check this request is authorized
        if (secret == null || !secret.equals(plugin.getSecret())){
            Log.warn("An unauthorised user service request was received: " + request.getQueryString());
            replyError("RequestNotAuthorised",response, out);
            return;
         }

        // Some checking is required on the username
        if (username == null){
            replyError("IllegalArgumentException",response, out);
            return;
        }


        // Check the request type and process accordingly
        try {
            username = username.trim().toLowerCase();
            username = JID.escapeNode(username);
            username = Stringprep.nodeprep(username);
            if ("add".equals(type)) {
                plugin.createUser(username, password, name, email, groupNames);
                replyMessage("ok",response, out);
                //imageProvider.sendInfo(request, response, presence);
            }
            else if ("delete".equals(type)) {
                plugin.deleteUser(username);
                replyMessage("ok",response,out);
                //xmlProvider.sendInfo(request, response, presence);
            }
            else if ("enable".equals(type)) {
                plugin.enableUser(username);
                replyMessage("ok",response,out);
            }
            else if ("disable".equals(type)) {
                plugin.disableUser(username);
                replyMessage("ok",response,out);
            }
            else if ("update".equals(type)) {
                plugin.updateUser(username, password,name,email, groupNames);
                replyMessage("ok",response,out);
                //xmlProvider.sendInfo(request, response, presence);
            }
            else {
                Log.warn("The societies servlet received an invalid request of type: " + type);
                // TODO Do something
            }
        }
        catch (UserAlreadyExistsException e) {
            replyError("UserAlreadyExistsException",response, out);
        }
        catch (UserNotFoundException e) {
            replyError("UserNotFoundException",response, out);
        }
        catch (IllegalArgumentException e) {
            
            replyError("IllegalArgumentException",response, out);
        }
        catch (Exception e) {
            replyError(e.toString(),response, out);
        }
    }

    private void replyMessage(String message,HttpServletResponse response, PrintWriter out) throws IOException{
        response.sendRedirect("public/signup-result.jsp?success=true");
    }

    private void replyError(String error,HttpServletResponse response, PrintWriter out) throws IOException{
    	response.sendRedirect("public/signup-result.jsp?error="+error);
    }
    
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
	public void destroy() {
        super.destroy();
        // Release the excluded URL
        AuthCheckFilter.removeExclude("societies/societies");
        AuthCheckFilter.removeExclude("societies/public/*");
    }
}
