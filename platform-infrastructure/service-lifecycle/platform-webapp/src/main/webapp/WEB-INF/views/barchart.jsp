<%@page contentType="text/html" pageEncoding="UTF-8"%>

<html>

<%
  java.io.File f = new java.io.File(".");
%>

<p>Current directory is <%= f.getCanonicalPath() %>
<p>Listing:
<ul>
<% 
  String[] list = f.list();
  for (int i=0; i<list.length; i++) {
    out.print("<li>" + list[i]);
  }
 %>
</ul>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" >
        <title>Bar chart page</title>
    </head>
    
    <body>
    	Hey, it works:
        <img src="barchart.png" width="600" height="400" usemap="#map"/>
    </body>
</html>
