<%@page import="com.google.inject.spi.Element"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

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
		<div class="userBar">
			<table width="100%">
			    <tr>
			        <td align="left">Context data</td>
			        <td align="right" style='font-size: 0.5em'>source: ${model.source}</td>
			    </tr>
			</table>
		</div>
		
		<div class="switchLayout">
			<xc:choose>
				<xc:when test="${model.viewType == 'ALL_ENTITIES' }">
					<span class="switch" style="background-color:#6699CC;" >All entities</span>
					<span class="switch" onclick="changeView('LINKED_ENTITIES');">Linked entities</span>
				</xc:when>
				<xc:otherwise>
					<span class="switch" onclick="changeView('ALL_ENTITIES');">All entities</span>
					<span class="switch" style="background-color:#6699CC;">Linked entities</span>				
				</xc:otherwise>
			</xc:choose>
		</div>
		
		<div class="pathBar"> 
			<span id="fixpath">Context path: <span class="button" onclick="changeView('${model.viewType}');">ROOT</span>/</span>
			<span id="variablePath">
			<xc:forEach var="path" items="${model.path_parent}" varStatus="count">
				<span class="button" onclick="retrieveWithCut('${path.link}','${count.count}')">${path.name}</span> /
			</xc:forEach>
			</span>
		</div>
		
		<table id='action_table' class="newspaper-a">
			<tr>
				<th style='width:50px'>Actions:</th>
				<th align='left'>
					<select id="method" onchange="showAction();">
						<option value="NONE" label="--- Select Operation ---">--- Select Operation ---</option>
						<option value="lookup" label="Lookup">Lookup</option>
						<option value="retrieve" label="Retrieve">Retrieve</option>
						<option value="newModel" label="Create">New model</option>
						<xc:if test="${model.is_association}">
							<option value="linkEntity" label="Create">Link to model</option>
						</xc:if>
					</select> 
				</th>
			</tr>
			
			<!----------------------------------------------- LOOKUP TABLE ---------------------------------------------------->
			
			<tr class='action' id='lookup' style='display:none'>
				<td colspan='2'>
					<select id="lookup_model">
						<option value="NONE" label="--- Select Model Type---">--- Select Model Type---</option>
						<xc:forEach var="type" items="${model.models}">
							<option value="${type}" label="${type}">${type}</option>
						</xc:forEach>
					</select> 
				
					
					<select id="create" style="display: none">
						<option value="NONE" label="--- Select Model Type---" />
						<options items="${model.models}" />
						<option value="attribute" label="Attribute" />
						<option value="entity" label="entity" />
						<option value="association" label="association" />
					</select> 
					
					
					<select context="type" id="${model.entity_label}" style="display: none">
						<option value="NONE" label="--- Select Entity Type---" />
						<xc:forEach var="type" items="${model.entityTypes}">
							<option value="${type}" label="${type}" >${type}</option>
						</xc:forEach>
					</select> 
					
					<select context="type" id="${model.attribute_label}" style="display: none">
						<option value="NONE" label="--- Select Attribute Type --- " />
		
						<xc:forEach var="type" items="${model.attributeTypes}">
							<option value="${type}">${type}</option>
						</xc:forEach>
					
					</select>
					
					
					 <select context="type" id="${model.association_label}" style="display: none">
						<option value="NONE" label="--- Select Association Type---" />
						<xc:forEach var="type" items="${model.associationTypes}">
							<option value="${type}" label="${type}" >${type}</option>
						</xc:forEach>
					</select> 
					
					<input type="button" value=" Exectute " id="executeLookup"/>
				<td>
			</tr>
			
			<!----------------------------------------------- RETRIEVE TABLE ---------------------------------------------------->
			
			<tr class='action'  id='retrieve' style='display:none'>
				<td colspan='2' >					
					<input style="display:none" size="50" id="retrieve" />
		
						<select id="idList">
								<option value="NONE" label="--- Select context id ---" />
								<xc:forEach var="ctxId" items="${model.idList}">
									<option value="${ctxId}" label="${ctxId}" >${ctxId}</option>
								</xc:forEach>
						</select>
						
						<input type='text' size="50" id="retrieve_value" />
						
						<input type="button" value=" Exectute " id="executeRetrieve"/>
						
				</td>
			</tr>
			
			<!----------------------------------------------- NEW OBJECT ---------------------------------------------------->
			
			<tr class='action'  id='newModel' style='display:none'>
				<td colspan='2'>
					<select name="model_newValue" id="model_newValue" onchange="showSelect(this)">
						<option value="" label="" selected="selected"> --- Select Model Type ---</option>
						<xc:choose>
							<xc:when test="${model.is_entity}">
								<option value="attribute" label="attribute">ATTRIBUTE</option>
								<option value="association" label="association">ASSOCIATION</option>
							</xc:when>
							<xc:otherwise>
								<option value="entity" label="entity">ENTITY</option>
							</xc:otherwise>
						</xc:choose>
					</select>
					
					<select class="type_value" name="type_newValue" id="type_newValue_">
						<option value="" label="" ></option>
					</select>
					<select class="type_value" name="type_newValue" id="type_newValue_association" style="display: none">
						<option value="" label="" > --- Select Association Type ---</option>
						<xc:forEach var="type" items="${model.associationTypes}">
							<option value="${type}">${type}</option>
						</xc:forEach>
					</select>
					<select class="type_value" name="type_newValue" id="type_newValue_attribute" style="display: none">
						<option value="" label="" >  --- Select Attribute Type --- </option>
						<xc:forEach var="type" items="${model.attributeTypes}">
							<option value="${type}">${type}</option>
						</xc:forEach>
					</select>
					<select class="type_value" name="type_newValue" id="type_newValue_entity" style="display: none">
						<option value="" label=""> --- Select Entity Type ---</option>
						<xc:forEach var="type" items="${model.entityTypes}">
							<option value="${type}">${type}</option>
						</xc:forEach>
					</select>
					
					<input type="text" name="value_newValue" id="value_newValue" style="display:none;" placeholder="Value" />
					
					<input type="button" name="Save" value="Save" onclick="save('${model.parent_id}')"/>
				</td>
			</tr>
			
			<!----------------------------------------------- LINK ENTITY ---------------------------------------------------->
			
			<tr class='action'  id='linkEntity' style='display:none'>
				<td colspan='2'>
					<select name="model_linkEntity" id="model_linkEntity">
						<option value="" label="" selected="selected"> --- Select Entity to Link ---</option>
						<xc:forEach var="entity" items="${model.entity_link}">
							<option value='${entity.id}'>${entity.type}</option>
						</xc:forEach>
					</select>
					
					<input type="button" name="Link" value="Link" onclick="link('${model.parent_id}')"/>
				</td>
			</tr>
		</table>

		<div class="errorBlock">${model.error}</div>		


		<!------------------------------------------ ENTITY TABLE SECTION ----------------------------------------------->

		<xc:if test="${not model.is_entity}">
			<table class="newspaper-a" id="entity_table">
				<thead class="button" onclick="showHide('entity_table')">
					<tr>
						<th id="entity_table_arrow" width="20"><img src="images/arrow_right.png"/></th>
						<th  scope="col" colspan='9'>
							Enities (
							<span id="entity_table_counter">${fn:length(model.entity_results)}</span>
							 elements)
						</th>
					</tr>
				</thead>	
				<tbody id="entity_table_body" style="display:none">
					<tr>
					   	<th scope="col" id="ctx-model" colspan="2"></th>
					    <th scope="col" id="ctx-id">id</th>
					    <th scope="col" id="ctx-model">Model</th>
					    <th scope="col" id="ctx-type">Type</th>
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>					
					<!-- search -->
					<tr id='lastHeadRow'>
					   	<th scope="col" id="ctx-model" colspan="2"><img src="images/search.png" class="icon"/></th>
					    <th scope="col" id="ctx-id">
							<input type="text" name="value-srchId" value="" onkeyup="setFilter('entity_table', 'ctx-id',this);" size="5"/>
						</th>
					    <th scope="col" id="ctx-model">
							<!-- <select name="model-srchValue" onchange="setFilter('entity_table', 'ctx-model',this);">
								<option value="" label=""></option>
								<option value="ATTRIBUTE" label="Attribute">ATTRIBUTE</option>
								<option value="ENTITY" label="entity">ENTITY</option>
								<option value="ASSOCIATION" label="association">ASSOCIATION</option>
							</select>
							 -->
						</th>
					    <th scope="col" id="ctx-type">
							<select name="type-srchValue" onchange="setFilter('entity_table', 'ctx-type',this);">
								<option value="" label="" ></option>
								<xc:forEach var="type" items="${model.entityTypes}">
									<option value="${type}">${type}</option>
								</xc:forEach>
							</select>
						</th>				
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>
					<xc:forEach var="element" items="${model.entity_results}">
						<tr class="ctx-row" id="${element.idNoSpecChar}">
							<td> <div class='button' onclick="deleteRow('${element.id}','entity_table')"> <img  class='icon' src="images/delete.png"> </div></td>
							<td> <div class='button' onclick="retrieve('${element.id}')"> <img class='icon' src="images/ahead.png"> </div></td>
						    <td class="ctx-id-val">${element.diplayId}</td>
						    <td class="ctx-model-val" name="model">${element.model}</td>
						    <td class="ctx-type-val" name="type">${element.type}</td>
							<td>${element.quality}</td>
						</tr>
					</xc:forEach>
				</tbody>
	
			</table>
		</xc:if>
		
		<!------------------------------------------ ATTRIBUTE TABLE SECTION ----------------------------------------------->


		<xc:if test="${model.is_entity}">
			<!-- Table markup-->
			<table class="newspaper-a" id="attribute_table">
				<thead class="button" onclick="showHide('attribute_table')" >
					<tr>
						<th id="attribute_table_arrow" width="20"><img src="images/arrow_right.png"/></th>
						<th  scope="col" colspan='9'>
							Attributes (
							<span id="attribute_table_counter">${fn:length(model.attr_results)}</span>
							 elements)
						</th>
					</tr>
				</thead>
				<tbody id="attribute_table_body" style="display:none">
					<tr>
					   	<th scope="col" id="ctx-model" colspan="2"></th>
					    <th scope="col" id="ctx-id">id</th>
					    <th scope="col" id="ctx-model">Model</th>
					    <th scope="col" id="ctx-type">Type</th>
						<th scope="col" id="ctx-value">Value</th>
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>					
					<tr id='lastHeadRow'>
					   	<th scope="col" id="ctx-model" colspan="2"><img src="images/search.png" class="icon"/></th>
					    <th scope="col" id="ctx-id">
							<input type="text" name="value-srchId" value="" onkeyup="setFilter('attribute_table', 'ctx-id',this);" size="5"/>
						</th>
					    <th scope="col" id="ctx-model">
							<select name="model-srchValue" onchange="setFilter('ctx-model',this);">
								<option value="" label=""></option>
								<option value="ATTRIBUTE" label="Attribute">ATTRIBUTE</option>
								<option value="ENTITY" label="entity">ENTITY</option>
								<option value="ASSOCIATION" label="association">ASSOCIATION</option>
							</select>
						</th>
					    <th scope="col" id="ctx-type">
							<!-- -->
							<select name="type-srchValue" onchange="setFilter('attribute_table', 'ctx-type',this);">
								<option value="" label="" ></option>
								<xc:forEach var="type" items="${model.attributeTypes}">
									<option value="${type}">${type}</option>
								</xc:forEach>
							</select> 
						</th>				
						<th scope="col" id="ctx-value">
							<input type="text" name="value-srchValue" value="" onkeyup="setFilter('attribute_table', 'ctx-value',this);"/>
						</th>
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>
					<xc:forEach var="element" items="${model.attr_results}">
						<tr id="${element.idNoSpecChar}">
							<td class="ctx-mod-btn"> <div class='button' onclick="setRowModificable('${element.id}')"> <img class='icon' src="images/modify.png"> </div></td>
							<td> <div class='button' onclick="deleteRow('${element.id}','attribute_table')"> <img  class='icon' src="images/delete.png"> </div></td>
						    <td class="ctx-id-val">${element.diplayId}</td>
						    <td class="ctx-model-val" name="model">${element.model}</td>
						    <td class="ctx-type-val" name="type">${element.type}</td>
							<td class="ctx-value-val">${element.value}</td>
							<td>${element.quality}</td>
						</tr>
					</xc:forEach>
				</tbody>
	
			</table>
		</xc:if>
		
		
		<!------------------------------------------ ASSOCIATION TABLE SECTION ----------------------------------------------->


		<xc:if test="${model.is_entity}">
			<!-- Table markup-->
			<table class="newspaper-a" id="association_table">
				<thead  class="button" onclick="showHide('association_table')" >
					<tr>
						<th id="association_table_arrow" width="20"><img src="images/arrow_right.png"/></th>
						<th  scope="col" colspan='9'>
							Associations (
							<span id="association_table_counter">${fn:length(model.asso_results)}</span>
							elements)
						</th>
					</tr>
				</thead>
				<tbody  id="association_table_body" style="display:none">
					<tr>
					   	<th scope="col" id="ctx-model" colspan="2"></th>
					    <th scope="col" id="ctx-id">id</th>
					    <th scope="col" id="ctx-model">Model</th>
					    <th scope="col" id="ctx-type">Type</th>
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>
					
					<!-- search -->
					<tr id='lastHeadRow'>
					   	<th scope="col" id="ctx-model" colspan="2"><img src="images/search.png" class="icon"/></th>
					    <th scope="col" id="ctx-id">
							<input type="text" name="value-srchId" value="" onkeyup="setFilter('association_table', 'ctx-id',this);" size="5"/>
						</th>
					    <th scope="col" id="ctx-model">
							<!-- <select name="model-srchValue" onchange="setFilter('ctx-model',this);">
								<option value="" label=""></option>
								<option value="ATTRIBUTE" label="Attribute">ATTRIBUTE</option>
								<option value="ENTITY" label="entity">ENTITY</option>
								<option value="ASSOCIATION" label="association">ASSOCIATION</option>
							</select> -->
						</th>
					    <th scope="col" id="ctx-type">
							<select name="type-srchValue" onchange="setFilter('association_table', 'ctx-type',this);">
								<option value="" label="" ></option>
								<xc:forEach var="type" items="${model.associationTypes}">
									<option value="${type}">${type}</option>
								</xc:forEach>
							</select>
						</th>				
						<th scope="col" id="ctx-quality">Quality</th>
					</tr>
					<xc:forEach var="element" items="${model.asso_results}">
						<tr id="${element.idNoSpecChar}">
							<td> <div class='button' onclick="deleteRow('${element.id}','association_table')"> <img  class='icon' src="images/delete.png"> </div></td>
							<td> <div class='button' onclick="retrieve('${element.id}')"> <img class='icon' src="images/ahead.png"> </div></td>
						    <td class="ctx-id-val">${element.diplayId}</td>
						    <td class="ctx-model-val" name="model">${element.model}</td>
						    <td class="ctx-type-val" name="type">${element.type}</td>
							<td>${element.quality}</td>
						</tr>
					</xc:forEach>
				</tbody>
	
			</table>
		</xc:if>
		

		<form method="POST" action="context.html"	name="ctxForm">
				<input type="hidden"	name="method"/>
				<input type="hidden"	name="ctxID"/>
				<input type="hidden"	name="type"/>
				<input type="hidden"	name="model"/>
				<input type="hidden"	name="value"/>
				<input type="hidden"	name="viewType"	value="${model.viewType}" />
				<input type="hidden"	name="pathIndex" value="-1"/>
	     </form>

	</div>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>
</html>

