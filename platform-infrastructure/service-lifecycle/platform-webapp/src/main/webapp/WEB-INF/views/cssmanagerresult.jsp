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
	width: 90px;
	text-align: center;
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
	<h4>${res}</h4>
	<br />
	<h4>${error}</h4>
	<br />

					<form:form method="POST" action="cssmanager.html"
						commandName="cmLoginForm">
						<form:errors path="*" cssClass="errorblock" element="div" />
						

	<div class="TabView" id="TabView">

		<!-- *** Tabs ************************************************************** -->

		<div class="Tabs" style="width: 500px;">
			<a>Css Admin </a> <a>Yellow Pages</a> <a>Friends' Services</a> <a>Css 
				Requests</a>
		</div>

		<!-- *** Pages ************************************************************* -->

		<div class="Pages"
			style="width: 800px; height: 300px; text-align: left;">

			<div class="Page">
				<div class="Pad">

					<!-- *** Css Admin Start *** -->
					Here goes the admin stuff


						<form:errors path="*" cssClass="errorblock" element="div" />
						<table>
							<tr>
								<td>Css Identity :</td>
								<td><form:input path="cssIdentity"
										value="${cmLoginForm.cssIdentity}" readonly="true" size="50" />
								</td>
							</tr>


							
						</table>
				


					<!-- *** Css Admin End ***** -->

				</div>
			</div>

			<!-- *** Page2 Start *** -->

			<div class="Page">
				<div class="Pad">
					<br /> Here is the listing from css directory



						<Table>


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
											value="1"  /> Join<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests1.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests1.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests1.value"
											value="2"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests1.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests1.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests1.value"
											value="3"  /> Leave<br />
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
											value="1"  /> Join<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests2.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests2.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests2.value"
											value="2"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests2.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests2.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests2.value"
											value="3"  /> Leave<br />
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
											value="1"  /> Join<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
										
										
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests3.adRecObj.status == 'PENDING'}">

									<td>
										<form:radiobutton 
										path="cssAdRequests3.value"
											value="2"  /> Cancel<br />
										</td>
										<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
									
									</xc:if>
									<xc:if test="${cmLoginForm.cssAdRequests3.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton 
											path="cssAdRequests3.value"
											value="3"  /> Leave<br />
										</td>
									<td><form:errors path="${cmLoginForm.cssAdRequests3.value}" cssClass="error" /></td>
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

					Here is control of friend css's


					<Table>
<!--  

						<xc:forEach var="friendCssDetails" items="${friendCssDetails}">
							<tr>
								<td>${friendCssDetails.cssIdentity}</td>
								<td>${friendCssDetails.requestStatus}</td>

								
								
									<xc:if test="${friendCssDetails.cssRequestObj.status == 'PENDING'}">
									<td>
										<form:radiobutton type="radio"
											path="${friendCssDetails.value}"
											value="2"  /> Cancel<br />
										</td>
										<td><form:errors path="${friendCssDetails.value}" cssClass="error" /></td>
									</xc:if>
									<xc:if test="${friendCssDetails.cssRequestObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton type="radio"
											path="${friendCssDetails.value}"
											value="3"  /> Leave<br />
										</td>
										<td><form:errors path="${friendCssDetails.value}" cssClass="error" /></td>
									</xc:if>


								</tr>
							</xc:forEach>
-->

					</table>

					<!-- *** Page3 End ***** -->

				

				</div>
			</div>

			<div class="Page">
				<div class="Pad">

					<!-- *** Page4 Start *** -->

					Here is css requests to you

					
				
						
						<Table>


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
						
						
<!--  
<xc:forEach var="cssPendingRequests" items="${cssPendingRequests}">
							<tr>
								<td>${cssPendingRequests.cssIdentity}</td>
								<td>${cssPendingRequests.requestStatus}</td>

								
								
								
							


							<xc:if test="${cssPendingRequests.cssRequestObj.status == 'PENDING'}">
									
									<td>
										<form:radiobutton type="radio"
											path="${cssPendingRequests.value}"
											value="1"  /> Accept<br />
										</td>
										<form:radiobutton type="radio"
											path="${cssPendingRequests.value}"
											value="2"  /> Reject<br />
										</td>
										<td><form:errors path="${cssPendingRequests.value}" cssClass="error" /></td>
									</xc:if>
									<xc:if test="${cssPendingRequests.adRecObj.status == 'ACCEPTED'}">
									<td>
										<form:radiobutton type="radio"
											path="${cssPendingRequests.value}"
											value="3"  /> Cancel<br />
										</td>
										<td><form:errors path="${cssPendingRequests.value}" cssClass="error" /></td>
									</xc:if>
									
	





								</tr>
							</xc:forEach>
-->
						</table>
						

					

					<!-- *** Page3 End ***** -->

				</div>
			</div>


		</div>
	</div>

<input type="submit" value="Save" />
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


