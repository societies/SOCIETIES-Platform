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
<script type="text/javascript" src="context/context.js"></script>
<script src="js/context/jquery.livesearch.js" type="text/javascript" charset="utf-8"></script>



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
     <h1>My Context Data</h1>
    




	
	<h3>My Context   </h3>
		
		<fieldset>
			
			<label class="form-label-left" id="label_3" for="input_3"> Filter Context:  </label>
			<input type="text" size=50 name="lookup"/>
			<span id="count"></span>
			<br>
			
			<label class="form-label-left" id="label_3" for="input_3"> Lookup:  </label>
			<input type="text" size=50 name="type"/>
			<label class="form-label-left" id="label_3" for="input_3"> Type:  </label>
			         <select path="type" >
					   <option value="NONE">--- Select ---</option>
					   <option value="a">ALL</option>
					   <option value="a">ENTITY</option>
					   <option value="b">ATTRIBUTE</option>
					   <option value="c">RELATIONSHIP</option>
					</select>
					
					EntityTypes: <form:select path="entityTypes" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${entityTypes}" />
					</form:select>
				    <form:errors path="entityTypes" cssClass="error" />
				    <br>
				    Attribute Types:
				    <form:select path="attributeTypes" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${attributeTypes}" />
					</form:select>
					<form:errors path="attributeTypes" cssClass="error" />
				    
				    Association Types:
				    <form:select path="associationTypes" >
					   <form:option value="NONE" label="--- Select ---" />
					   <form:options items="${associationTypes}" />
					</form:select>
				    <form:errors path="associationTypes" cssClass="error" />
				    
			
			<br>
			<input type="button" value=" Create " id="add"/>
					<label class="form-label-left" id="label_3" for="input_3"> Type:  </label>
			         <select path="type" >
					   <option value="NONE">--- Select ---</option>
					   <option value="a">ALL</option>
					   <option value="a">ENTITY</option>
					   <option value="b">ATTRIBUTE</option>
					   <option value="c">RELATIONSHIP</option>
					</select>
		</fieldset>
		
		 
		<br>
		
		
			<table>
			<tr>
			   <th class="big"> Context Identifier </th>
			   <th> Type</th>
			   <th> Value   </th>
			   <th> Actions </th>
			</TR>
			</table>
			
			<table id="context">
			
			
			<tr class="entity">
			   <td class="big"> AAAAA   </td>
			   <td> BBBBBB  </td>
			   <td> CCCCCCC </td>
			   <td> <input type="button" value=" Create " id="add"/>
					<input type="button" value=" Delete " id="delete"/>

				</td>
			</TR>
			
			<tr class="entity">
			   <td class="big"> AAAAA   </td>
			   <td> BBBBBB  </td>
			   <td> CCCCCCC </td>
			   <td> <input type="button" value=" Edit " id="edit"/>
					<input type="button" value=" Delete " id="delete"/>

				</td>
			</TR>
			<tr class="relationship">
			   <td class="big"> AAAAA   </td>
			   <td>  </td>
			   <td> CCCCCCC </td>
			   <td> <input type="button" value=" Edit " id="edit"/>
					<input type="button" value=" Delete " id="delete"/>

				</td>
			</tr>
			
			<tr class="relationship">
			   <td class="big"><a href="context://myFooIIdentity@societies.local/ENTITY/person/0">context://myFooIIdentity@societies.local/ENTITY/person/0</a>   </td>
			   <td> Person  </td>
			   <td> ------ </td>
			   <td> <input type="button" value=" Edit " id="add"/>
					<input type="button" value=" Delete " id="delete"/>

				</td>
			</TR>
			
			<tr class="entity">
			   <td class="big"> AAAAA   </td>
			   <td> BBBBBB  </td>
			   <td> CCCCCCC </td>
			   <td> <input type="button" value=" Create " id="add"/>
					<input type="button" value=" Delete " id="delete"/>

				</td>
			</TR>
			
			
			
			
			</table>
					
		
    
    
   


</div>
     
     
	
	     
		
		
		 	
		 	
		 	

	

<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

