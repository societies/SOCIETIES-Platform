<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>My Context</title>


<link href="css/context/context.css" rel="stylesheet" type="text/css"
	media="screen" />
<link href="css/context/ctx-table-style.css" rel="stylesheet"
	type="text/css" media="screen" />

<script type="text/javascript" src="js/context/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="js/context/context.js"></script>


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

		<div class="query">
			<h1>My Context Data</h1>





			<label> Action: </label> <select id="method">
				<option value="NONE" label="--- Select Operation ---" />
				<option value="lookup" label="Lookup" />
				<option value="retrieve" label="Retrieve" />
				<!-- 
				<option value="create" label="Create" />
				 -->
			</select> 
			
			
			<select id="lookup" style="display: none">
				<option value="NONE" label="--- Select Model Type---" />

				<!-- 
				<option value="attribute">attribute</option>
				<option value="entity">entity</option>
				<option value="association">association</option>
 				-->

				<xc:forEach var="type" items="${models}">
					<option value="${type}" label="${type}" />
				</xc:forEach>
			</select> 
			
			
			<select id="create" style="display: none">
				<option value="NONE" label="--- Select Model Type---" />
				<options items="${models}" />
				<option value="attribute" label="Attribute" />
				<option value="entity" label="entity" />
				<option value="association" label="association" />
			</select> 
			
			
			<select context="type" id="${entity_label}" style="display: none">
				<option value="NONE" label="--- Select Entity Type---" />
				<xc:forEach var="type" items="${entityTypes}">
					<option value="${type}" label="${type}" />
				</xc:forEach>
			</select> 
			
			<select context="type" id="${attribute_label}"
				style="display: none">
				<option value="NONE" label="--- Select Attribute Type --- " />

				<xc:forEach var="type" items="${attributeTypes}">
					<option value="${type}">${type}</option>
				</xc:forEach>
			
			</select>
			
			
			 <select context="type" id="${association_label}"
				style="display: none">
				<option value="NONE" label="--- Select Association Type---" />
				<xc:forEach var="type" items="${associationTypes}">
					<option value="${type}" label="${type}" />
				</xc:forEach>
			</select> 
			
			
			<input style="display:none" size="50"	id="retrieve" />

				<select style="display:none" id="idList">
						<option value="NONE" label="--- Select context id ---" />
						<xc:forEach var="ctxId" items="${idList}">
							<option value="${ctxId}" label="${ctxId}" />
						</xc:forEach>
				</select>
				
				<input type="button" value=" Exectute " id="executeQuery" style="display: none" />
		</div>


		<div class="errorBlock">${error}</div>

		<div class="navigator">
		   <label> Context Path:</label>
	 	 	${parent} 
		</div>

		<!--  RESULT SECTION -->


		<!-- Table markup-->
		<table id="newspaper-a">

			<!-- Table header -->

			<thead>
				<tr>
				   
				    <th scope="col" id="ctx-model">Model</th>
				    <th scope="col" id="ctx-type">Type</th>
					<th scope="col" id="ctx-type">Source</th>
					<th scope="col" id="ctx-id">id</th>
					<th scope="col" id="ctx-value">Value</th>
					<th scope="col" id="ctx-quality">Quality</th>
					<th scope="col" id="ctx-action">Actions</th>

				</tr>
			</thead>

			<!-- Table footer -->
		
			<tfoot>
				
			</tfoot>

			<!-- Table body -->
			<tbody>
				<xc:forEach var="element" items="${results}">
					<tr id="${element.id}">
					    <td  name="model">${element.model}</td>
					    <td  name="type">${element.type}</td>
					    <td>${element.source}</td>
						<td><a href="#" onclick="retrieve('${element.id}');">${element.diplayId}</a></td>
						<td>${element.value}</td>
						<td>${element.quality}</td>
						<td>
						
						    <button onclick="retrieve('${element.id}')"> Retrieve </button>
							<!-- 
							<button onclick="edit('${element.id}')">Edit</button>
							<button onclick="del('${element.id}')">Delete</button> 
							-->
							
						</td>

					</tr>
				</xc:forEach>
			</tbody>

		</table>


		<form:form method="POST" action="context.html"  commandName="ctxForm" name="ctxForm">
				<form:input style="display:none"    name="method" path="method" value="" />
				<form:input style="display:none"    name="ctxID"  path="ctxID" value="" />
				<form:input style="display:none"    name="type"   path="type" value="" />
				<form:input style="display:none"    name="model"  path="model" value="" />
				<form:input style="display:none"    name="value"  path="value" value="" />
	     </form:form>




	</div>

	











	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

