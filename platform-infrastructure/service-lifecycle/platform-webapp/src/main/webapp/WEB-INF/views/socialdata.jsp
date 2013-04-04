<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies services - Social Data</title>
<script type="text/javascript" src="js/socialdata.js"></script>
<script src="http://code.jquery.com/jquery-1.9.1.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.1.1.min.js"></script>
<link href="css/socialdata.css" rel="stylesheet" type="text/css"  media="screen" />
<!-- 

<style>

input.icon{
	width:45px;
}

div.legend{
	border: solid 2px;
	background-color: grey;
	color:white;
	width:20%;
	
}

.error {
	color: #ff0000;
}
 
 .connectors{
 	border: solid black;
	text-align: center;
	
 }
.errorblock {
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding: 8px;
	margin: 16px;
}

div#container {
	width: 90%;
	border: 1px solid #000;
	text-align: center;
}
div.split2 div {
	float: left;
	width: 50%;
}

div.split2 ul { list-style-type: none; }

div.wide {
	clear: left;
}

div#connectors ul { list-style-type: none; text-align:left; margin-left: 20%; }
div#connectors img{width:20px;}

div#addConnector{
	visibility: hidden;
}




</style>
-->

</head>




<body>




	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
    <!-- .................PLACE YOUR CONTENT HERE ................ -->

     
	
	
     <div id="container">
     <h1>Social Network Management</h1>
    <p> 
         <strong>SocialData</strong> links your Social Networks with <strong>SOCIETIES</strong>. 
         <br>Your social friends, profile preferences, groups and activities 
         <br>will be then part of your network. Link them and Enjoy!
    
    </p>  
    
    
	      
<div class="split2">
   <div id="connectors">
      <h2>Connected Networks</h2>
      <p>Below the list of connectors active</p>
       <ul id="listConn">
		     ${connectors}
		</ul>
   </div>
   <div>
      <h2>Links</h2>
      <p>With those commands it is possible to fetch<br>social data from all the connectors</p>
      <ul>
         <li><a href="#" onclick="exe('friends');">Show Social <strong>Friends </strong></a></li>
         <li><a href="#" onclick="exe('profiles');">Show connected <Strong>Profiles</Strong></a></li>
         <li><a href="#" onclick="exe('activities');">Show <Strong>Activities</strong> feed</a></li>
         <li><a href="#" onclick="exe('groups');">Show subscribed <strong>Groups</a></strong></li>
         <li><a href="#" onclick="exe('update');"> <strong>Update</strong></a></li>
         <li>Last update: ${lastupdate}</strong></li>
         <jsp:include page="socialdatastatus.jsp" />
      </ul>
   </div>
</div>


<div class="wide">

 

 
   <div id="popup" style="visibility:hidden" ><img src="images/loading.gif"> Loading connector data. Please wait... </div><br>
   <p> Connect your social network with Societies <br/>
      <!-- 
     <input class="icon" onclick="getToken('http://157.159.160.188:8080/examples/servlets/servlet/TwitterLoginServlet','twitter');" type="image" src="images/Twitter.jpg">
	  
     <input class="icon" onclick="getToken('http://dev.lucasimone.eu/fb.php', 'facebook');" type="image" src="images/facebook.png">
     <input class="icon" onclick="getToken('http://157.159.160.188:8080/examples/servlets/servlet/FoursquareLoginServlet', 'foursquare');" type="image" src="images/Foursquare.png">
	 <input class="icon" onclick="getToken('http://dev.lucasimone.eu/auth.php', 'Linkedin')" type="image" src="images/Linkedin.png">
	   -->
     
	 <input class="icon" onclick="connectSN('connect_tw')" type="image" src="images/Twitter.jpg">
	 <input class="icon" onclick="connectSN('connect_fq')" type="image" src="images/Foursquare.png">
	 <input class="icon" onclick="connectSN('connect_lk')" type="image" src="images/Linkedin.png">
	 <input class="icon" onclick="connectSN('connect_fb')" type="image" src="images/Facebook.png">
	 
	 
   </p>
   <div id="addConnector">
  <form:form method="POST" action="socialdata.html" commandName="sdForm" name="sd">
		     
	        <p> 
	             When the popup has generated a json that contains access_token param,<br>
		         please copy it to the Token field and click on add Connector
		     </p> 
   	     
		     Method: <form:input id="method"  path="method" value="" />
			 SN:     <form:input id="snName"     path="snName" value="" />
			 Parameters<form:input id="params"  path="params" value=""/><br>
			 Token:<form:input id="token"   path="token"   size="100" value=""/><br>
			 <form:input id="id"  style="visibility:hidden" path="id" value=""/>
		     <a href="javascript: submitform()">Add connector</a>  --- 
		     <a href="javascript: removeform()">Cancel </a>
    </form:form>
    </div>
  
</div>

</div>
     
     
	
	     
		
		
		 	
		 	
		 	

	

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

