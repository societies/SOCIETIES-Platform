<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="java.util.*"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Societies Css Manager Result</title>

<script type="text/javascript" src="js/tabView.js"></script>

<style type="text/css">
Tabview code from www.javascriptsource.com css tab-view.html -->div.TabView div.Tabs
	{
	height: 24px;
	overflow: hidden;
}

div.TabView div.Tabs a {
	float: left;
	display: block;
	width: 150px;
	text-align: left;
	height: 24px;
	line-height: 28px;
	vertical-align: middle;
	background: url("images/tabs.png") no-repeat -2px -1px;
	text-decoration: none;
	font-family: "Times New Roman", Serif;
	font-weight: 900;
	font-size: 13px;
	color: #000080;
}

div.TabView div.Tabs a:hover,div.TabView div.Tabs a.Active {
	background: url("images/tabs.png") no-repeat -2px -31px;
}

div.TabView div.Pages {
	clear: both;
	border: 1px solid #404040;
	overflow: hidden;
}

div.TabView div.Pages div.Page {
	height: 100%;
	padding: 0px;
	overflow: hidden;
}

div.TabView div.Pages div.Page div.Pad {
	padding: 3px 18px;
}
</style>




</head>
<body>





	<!-- HEADER -->
	<jsp:include page="header.jsp" />
	<!-- END HEADER -->

	<!-- LEFTBAR -->
	<jsp:include page="leftbar.jsp" />
	<!-- END LEFTBAR -->
	<!-- .................PLACE YOUR CONTENT HERE ................ -->
	<h4>${message}</h4>
	<br />
	<h4>${error}</h4>
	<br />
				<form:form method="POST" action="cssmanager.html"
						commandName="cmLoginForm">
						<form:errors path="*" cssClass="errorblock" element="div" />
						

	<div class="TabView" id="TabView">

		<!-- *** Tabs ************************************************************** -->

		<div class="Tabs" style="width: 800px;">
			<a>&nbsp; &nbsp; Css Admin </a> <a>&nbsp; &nbsp; Yellow Pages</a> <a>&nbsp; &nbsp; Services</a> <a>&nbsp; &nbsp; Css 
				Requests</a> <a>&nbsp; &nbsp; Css Friends </a>
		</div>

		<!-- *** Pages ************************************************************* -->

		<div class="Pages"
			style="width: 1000px; height: 300px; text-align: left;">

			<div class="Page">
				<div class="Pad">

					<!-- *** Css Admin Start *** -->
					


						<form:errors path="*" cssClass="errorblock" element="div" />
						
						<b>CSS Details</b>
						
						<table>
							<tr>
								<td>Css Identity :</td>
								<td><form:input path="cssIdentity"
										value="${cmLoginForm.cssIdentity}" readonly="true" size="50" />
								</td>
							</tr>
							<tr>		
								<td>Domain Server</td>
								<td><form:input path="domainServer"
										value="${cmLoginForm.domainServer}"  size="50" />
								</td>
							</tr>
							<tr>
								<td>Hosting Location</td>
								<td><form:input path="cssHostingLocation"
										value="${cmLoginForm.cssHostingLocation}"  size="50" />
								</td>
															</tr>
							<tr>
								<td>Email</td>
								<td><form:input path="emailID"
										value="${cmLoginForm.emailID}"  size="50" />
								</td>
															</tr>
							<tr>
							<td>Name</td>
								<td><form:input path="name"
										value="${cmLoginForm.name}"  size="50" />
								</td>
														</tr>
							<tr>
								<td>Home Location</td>
								<td><form:input path="homeLocation"
										value="${cmLoginForm.homeLocation}"  size="50" />
								</td>
														</tr>
							<tr>
								<td>Identity Name</td>
								<td><form:input path="identityName"
										value="${cmLoginForm.identityName}"  size="50" />
								</td>
														</tr>
							<tr>
								<td>IM ID</td>
								<td><form:input path="imID"
										value="${cmLoginForm.imID}"  size="50" />
								</td>
														</tr>
							<tr>
								<td>Sex</td>
								<td><form:input path="sex"
										value="${cmLoginForm.sex}" size="50" />
								</td>
							</tr>
						</table>
						<br/>
						
						<b>My CSS Nodes</b>
						<table>
						<tr><td><b>ID</b></td><td><b>Type</b></td></tr>
						<xc:forEach var="aNode" items="${allNodes}">
					        <tr>
					        	<td>${aNode.getJid()}</td>
					         	<td>${aNode.getType()}</td>
					        </tr>
					    </xc:forEach>
						</table>
						
						<br/>
						<b>CSS Advertisement Details</b>
				
		<table >
			<!-- name="CssAdvertisementRecordDetails" -->
			<tr>
				<td>Id</td>
				<td><form:input path="cssAdId"  size="50" readonly="true" /></td>
				<td><form:errors path="cssAdId" cssClass="error" /></td>
				
			</tr>
			<tr>
				<td>Name</td>
				<td><form:input path="cssAdName"  size="100" /></td>
				<td><form:errors path="cssAdName" cssClass="error" /></td>
				
			</tr>
			<tr>
				<td>Uri</td>
				<td><form:input path="cssAdUri"  size="100" /></td>
				<td><form:errors path="cssAdUri" cssClass="error" /></td>
				
			</tr>
		</table>

					<!-- *** Css Admin End ***** -->

				</div>
			</div>

			<!-- *** Page2 Start *** -->

			<div class="Page">
				<div class="Pad">
					<br />


						<table>
						


						<xc:if test="${cmLoginForm.cssAdRequests1.active == true}"> 
						<tr>
						<td>${cmLoginForm.cssAdRequests1.adRecObj.resultCssAdvertisementRecord.id}</td>
									
									<td>${cmLoginForm.cssAdRequests1.adRecObj.resultCssAdvertisementRecord.name}</td>

									<td>${cmLoginForm.cssAdRequests1.adRecObj.resultCssAdvertisementRecord.uri}</td>
									<td>${cmLoginForm.cssAdRequests1.adRecObj.status}</td>
									
									
									
									<xc:if test="${cmLoginForm.cssAdRequests1.adRecObj.status == 'NOTREQUESTED'}">
			
										<td>
										<form:radiobutton 
											path="cssAdRequests1.value"
											value="1"  /> Send Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests1.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests1.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests1.value"
											value="2"  /> Cancel Pending Request <br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests1.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests1.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests1.value"
											value="3"  /> Cancel Request <br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests1.value}" cssClass="error" /></td>
										</xc:if>
									
									
						</tr>
						</xc:if>
					<xc:if test="${cmLoginForm.cssAdRequests2.active == true}"> 
					<tr>
						<td>${cmLoginForm.cssAdRequests2.adRecObj.resultCssAdvertisementRecord.id}</td>
									
									<td>${cmLoginForm.cssAdRequests2.adRecObj.resultCssAdvertisementRecord.name}</td>

									<td>${cmLoginForm.cssAdRequests2.adRecObj.resultCssAdvertisementRecord.uri}</td>
									<td>${cmLoginForm.cssAdRequests2.adRecObj.status}</td>
									
									
									

										
									<xc:if test="${cmLoginForm.cssAdRequests2.adRecObj.status == 'NOTREQUESTED'}">
			
										<td>
										<form:radiobutton 
											path="cssAdRequests2.value"
											value="1"  /> Send Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests2.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests2.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests2.value"
											value="2"  /> Cancel Pending Request <br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests2.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests2.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests2.value"
											value="3"  /> Cancel Request<br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests2.value}" cssClass="error" /></td>
										</xc:if>
									
						</tr>
						</xc:if>
						<xc:if test="${cmLoginForm.cssAdRequests3.active == true}"> 
					<tr>
						<td>${cmLoginForm.cssAdRequests3.adRecObj.resultCssAdvertisementRecord.id}</td>
									
									<td>${cmLoginForm.cssAdRequests3.adRecObj.resultCssAdvertisementRecord.name}</td>

									<td>${cmLoginForm.cssAdRequests3.adRecObj.resultCssAdvertisementRecord.uri}</td>
									<td>${cmLoginForm.cssAdRequests3.adRecObj.status}</td>
									
								
									
									<xc:if test="${cmLoginForm.cssAdRequests3.adRecObj.status == 'NOTREQUESTED'}">
			
										<td>
										<form:radiobutton 
											path="cssAdRequests3.value"
											value="1"  /> Send Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests3.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests3.value"
											value="2"  /> Cancel Pending Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests3.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests3.value"
											value="3"  /> Cancel Request<br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
										</xc:if>
									
						</tr>
						</xc:if> 
						<xc:if test="${cmLoginForm.cssAdRequests4.active == true}"> 
					<tr>
						<td>${cmLoginForm.cssAdRequests4.adRecObj.resultCssAdvertisementRecord.id}</td>
									
									<td>${cmLoginForm.cssAdRequests4.adRecObj.resultCssAdvertisementRecord.name}</td>

									<td>${cmLoginForm.cssAdRequests4.adRecObj.resultCssAdvertisementRecord.uri}</td>
									<td>${cmLoginForm.cssAdRequests4.adRecObj.status}</td>
									
								
									
									<xc:if test="${cmLoginForm.cssAdRequests4.adRecObj.status == 'NOTREQUESTED'}">
			
										<td>
										<form:radiobutton 
											path="cssAdRequests4.value"
											value="1"  /> Send Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests4.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests4.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests4.value"
											value="2"  /> Cancel Pending Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests4.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests4.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests4.value"
											value="3"  /> Cancel Request<br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests4.value}" cssClass="error" /></td>
										</xc:if>
									
						</tr>
						</xc:if> 

								<xc:if test="${cmLoginForm.cssAdRequests5.active == true}"> 
					<tr>
						<td>${cmLoginForm.cssAdRequests5.adRecObj.resultCssAdvertisementRecord.id}</td>
									
									<td>${cmLoginForm.cssAdRequests5.adRecObj.resultCssAdvertisementRecord.name}</td>

									<td>${cmLoginForm.cssAdRequests5.adRecObj.resultCssAdvertisementRecord.uri}</td>
									<td>${cmLoginForm.cssAdRequests5.adRecObj.status}</td>
									
								
									
									<xc:if test="${cmLoginForm.cssAdRequests5.adRecObj.status == 'NOTREQUESTED'}">
			
										<td>
										<form:radiobutton 
											path="cssAdRequests5.value"
											value="1"  /> Send Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests5.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests5.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests5.value"
											value="2"  /> Cancel Pending Request<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests5.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests5.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests5.value"
											value="3"  /> Cancel Request<br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests5.value}" cssClass="error" /></td>
										</xc:if>
									
						</tr>
						</xc:if> 
						

						</table>

		
					<!-- *** Page2 End ***** -->

				</div>
			</div>

			<div class="Page">
				<div class="Pad">

					<!-- *** Page3 Start *** -->



					<table>
					
					
					<tr>
					<td><b>My Services</b></td>
					</tr>
					<xc:if test="${cmLoginForm.cssService1.active == true}">
					
					 <tr>

                        <td>${cmLoginForm.cssService1.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssService1.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssService1.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssService1.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssService1.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService1.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssService1.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssService1.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService1.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
            		</xc:if>
            		<xc:if test="${cmLoginForm.cssService2.active == true}">
					
					 <tr>

                        <td>${cmLoginForm.cssService2.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssService2.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssService2.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssService2.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssService2.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService2.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssService2.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssService2.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService2.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
            		</xc:if>
            		<xc:if test="${cmLoginForm.cssService3.active == true}">
					
					 <tr>

                        <td>${cmLoginForm.cssService3.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssService3.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssService3.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssService3.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssService3.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService3.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssService3.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssService3.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssService3.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
            		</xc:if>
            		
            		<tr></tr>
            		<tr></tr>
            		<tr></tr>
            		

					<xc:if test="${cmLoginForm.cssFriendService11.active == true}">
					<tr>
					<td><b>${cmLoginForm.cssAdRequests1.adRecObj.resultCssAdvertisementRecord.id}</b></td>
					</tr>
					 <tr>

                        <td>${cmLoginForm.cssFriendService11.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssFriendService11.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssFriendService11.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssFriendService11.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService11.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService11.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssFriendService11.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService11.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService11.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
					</xc:if>
					<xc:if test="${cmLoginForm.cssFriendService12.active == true}">
					
					 <tr>
                        <td>${cmLoginForm.cssFriendService12.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssFriendService12.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssFriendService12.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssFriendService12.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService12.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService12.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssFriendService12.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService12.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService12.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
					</xc:if>
					<xc:if test="${cmLoginForm.cssFriendService13.active == true}">
					
					 <tr>
                        <td>${cmLoginForm.cssFriendService13.serviceDetails.serviceName}</td>
         				<td>${cmLoginForm.cssFriendService13.serviceDetails.serviceDescription}</td>
            			<td>${cmLoginForm.cssFriendService13.serviceDetails.serviceStatus}</td>
            			
            			 <xc:if test="${cmLoginForm.cssFriendService13.serviceDetails.serviceStatus == 'STARTED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService13.value"
											value="1"  /> Stop<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService13.value}" cssClass="error" /></td>
										
										
							</xc:if>
						 <xc:if test="${cmLoginForm.cssFriendService13.serviceDetails.serviceStatus == 'STOPPED'}">
			
										<td>
										<form:radiobutton 
											path="cssFriendService13.value"
											value="2"  /> Start<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssFriendService13.value}" cssClass="error" /></td>
										
										
							</xc:if>
										
            		</tr>
					</xc:if>
	

					</table>

					<!-- *** Page3 End ***** -->

				

				</div>
			</div>

			<div class="Page">
				<div class="Pad">

					<!-- *** Page4 Start *** -->

					

					
				
						
						<table>


						<xc:if test="${cmLoginForm.cssRequests1.active == true}"> 
						<tr>
						<td>${cmLoginForm.cssRequests1.cssRequestObj.cssIdentity}</td>
									<td>${cmLoginForm.cssRequests1.cssRequestObj.requestStatus}</td>
									
									<td>
									
										
									
									<xc:if test="${cmLoginForm.cssRequests1.cssRequestObj.requestStatus == 'PENDING'}">
			
										<td>
										<form:radiobutton 
											path="cssRequests1.value"
											value="1"  /> Accept<br />
										</td>
										<td>
										<form:radiobutton 
											path="cssRequests1.value"
											value="2"  /> Reject<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests1.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssRequests1.cssRequestObj.requestStatus == 'ACCEPTED'}">

									<td>
										<form:radiobutton 
											path="cssRequests1.value"
											value="3"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests1.value}" cssClass="error" /></td>
									
									</xc:if>
									
						</tr>
						</xc:if>
				<xc:if test="${cmLoginForm.cssRequests2.active == true}"> 
						<tr>
						<td>${cmLoginForm.cssRequests2.cssRequestObj.cssIdentity}</td>
									<td>${cmLoginForm.cssRequests2.cssRequestObj.requestStatus}</td>
									
									<td>
									
										
									
									<xc:if test="${cmLoginForm.cssRequests2.cssRequestObj.requestStatus == 'PENDING'}">
			
										<td>
										<form:radiobutton 
											path="cssRequests2.value"
											value="1"  /> Accept<br />
										</td>
										<td>
										<form:radiobutton 
											path="cssRequests2.value"
											value="2"  /> Reject<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests2.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssRequests2.cssRequestObj.requestStatus == 'ACCEPTED'}">

									<td>
										<form:radiobutton 
											path="cssRequests2.value"
											value="3"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests2.value}" cssClass="error" /></td>
									
									</xc:if>
									
						</tr>
						</xc:if>
						<xc:if test="${cmLoginForm.cssRequests3.active == true}"> 
						<tr>
						<td>${cmLoginForm.cssRequests3.cssRequestObj.cssIdentity}</td>
									<td>${cmLoginForm.cssRequests3.cssRequestObj.requestStatus}</td>
									
									<td>
									
										
									
									<xc:if test="${cmLoginForm.cssRequests3.cssRequestObj.requestStatus == 'PENDING'}">
			
										<td>
										<form:radiobutton 
											path="cssRequests3.value"
											value="1"  /> Accept<br />
										</td>
										<td>
										<form:radiobutton 
											path="cssRequests3.value"
											value="2"  /> Reject<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests3.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssRequests1.cssRequestObj.requestStatus == 'ACCEPTED'}">

									<td>
										<form:radiobutton 
											path="cssRequests3.value"
											value="3"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssRequests3.value}" cssClass="error" /></td>
									
									</xc:if>
									
						</tr>
						</xc:if>
						

						
						
						</table>
						

					<!-- *** Page3 End ***** -->

				</div>
			</div>
<div class="Page">
				<div class="Pad">

					<!-- *** Page5 Start *** -->
						<table>
						<tr> 
							<td> 
								Just Temporary screen to show that new functionality working 
							</td>
						</tr>
						<xc:forEach var="cssFriend" items="${cssFriends}">
        				<tr>
        					<td>${cssFriend.id}</td>
        					<td>${cssFriend.name}</td>
        					<td>${cssFriend.uri}</td>
        				</tr>	
        				</xc:forEach>
						</table>
						
					<!-- *** Page5 End ***** -->

					</table>
						
				</div>
			</div>

					

		</div>
	</div>

<form:input type="submit" path="buttonLabel" value="Save" />
<form:input type="submit" path="buttonLabel" value="Refresh" />
</form:form>


	<script type="text/javascript">
		tabview_initialize('TabView');
	</script>

	<!-- .................END PLACE YOUR CONTENT HERE ................ -->
	<!-- FOOTER -->
	<jsp:include page="footer.jsp" />
	<!-- END FOOTER -->
</body>


</html>


