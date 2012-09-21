<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>My Context</title>

    
<link href="css/context.css" rel="stylesheet" type="text/css" media="screen" />

<script type="text/javascript" src="js/context/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="js/context/jquery.slidertron-1.0.js"></script>
<script type="text/javascript" src="js/context/jquery.js"></script>
<script type="text/javascript" src="js/context/jquery.cookie.js"></script>
<script type="text/javascript" src="js/context/jquery.hotkeys.js"></script>
<script type="text/javascript" src="js/context/jquery.jstree.js"></script>
<script type="text/javascript" src="js/context/context.js"></script>
<script type="text/javascript" src="js/context/jquery.livesearch.js"></script>



</head>

<script>
	var ctxAttributeType = ${entityTypes};
	
	
</script>



<body>




	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
    <!-- .................PLACE YOUR CONTENT HERE ................ -->

     
	
	
     <div id="container">
     <h1>My Context Data</h1>
    


	<fieldset>
			
			<label> Lookup:</label>
			
			        <form:select path="model" id="model">
					   <form:option value="NONE" label="--- Select Model Type---" />
					   <form:options items="${model}" />
					</form:select>
				    <form:errors path="ctx-type" cssClass="error" />
					
		
			        <form:select path="entityTypes" id="entity-sel">
					   <form:option value="NONE" label="--- Select Entity Type---" />
					   <form:options items="${entityTypes}" />
					</form:select>
				    <form:errors path="entityTypes" cssClass="error" />
				    
				    <form:select path="attributeTypes" id="attribute-sel">
					   <form:option value="NONE" label="--- Select Attribute Type ---" />
					   <form:options items="${attributeTypes}" />
					</form:select>
					<form:errors path="attributeTypes" cssClass="error" />
					
					  
				    <form:select path="associationTypes" id="association-sel" >
					   <form:option value="NONE" label="--- Select Association Type---" />
					   <form:options items="${associationTypes}" />
					</form:select>
				    <form:errors path="associationTypes" cssClass="error" />
			
			<input type="button" value=" Send Lookup" id="lookup"/>
		   </fieldset>
		
		 
		<br>
		
	
		
			<table id="context-title">
			<tr>
			   <th class="big"> Context Identifier </th>
			   <th> Type</th>
			   <th> Value   </th>
			   <th> Actions </th>
			</tr>
			<tr>
				<td><input type="text" size=50 name="lookup"/></td>
				<td>
					<input type="text" size=50 name="lookup"/>
				<td>
					<input type="text" size=50 name="lookup"/>
				
				</td>
				<td>
					<input type="text" size=50 name="lookup"/>
				</td>
			</tr>
			
			<tr>
				<td><span id="count"></span></td>
 				<td>   </td>
 				<td>   </td>
 				<td>   </td>
			<tr>
			</table>
			
			<br>
			
			<table id="context">
			
			<xc:forEach var="elm" items="${results}">
        	<tr class="${elm.model}">
        	<td id="ctx-id"><a href="lookup('${elm.id}','{elm.type}');">{elm.id}</a></td>
         	<td>${elm.type}</td>
            <td>${service.value}</td>
            <td>
            	<input type="button" value=" Create " id="add"/>
				<input type="button" value=" Delete " id="delete"/>
			</td>
        	</tr>
   		 	</xc:forEach>
			
			
			
			
			</table>
					
		
    <form:form method="POST" action="context.html" commandName="ctxForm" name="ctx">
    	<form:input id="lookupModel"  style="visibility:hidden" path="lookupModel" value=""/>
    	<form:input id="lookupType"   style="visibility:hidden" path="lookupType" value=""/>
    	<form:input id="method"  style="visibility:hidden" path="method" value=""/>
    </form:form>


</div>
     
     
	
	     
		
		
		 	
		 	
		 	

	

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

