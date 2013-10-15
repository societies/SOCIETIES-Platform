<!--
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="xc" %>
-->

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Societies services - Social Data</title>
			
		<!-- 
		<link rel="stylesheet" href="css/jquery-ui.css" />
		<script src="js/jquery.js"></script>
		<script src="js/jquery-ui.js"></script>
		 -->
		
		<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.1/themes/base/jquery-ui.css" />
		<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
		<script src="http://code.jquery.com/ui/1.10.2/jquery-ui.js"></script>
		
		<link rel="stylesheet" href="css/socialStyle.css" />
		<script type="text/javascript" src="js/socialdata.js"></script>
	</head>
	<body>
		<!-- HEADER -->
		<jsp:include page="header.jsp" />
		<!-- END HEADER -->
		
		<!-- LEFTBAR -->
		<jsp:include page="leftbar.jsp" />
		<!-- END LEFTBAR -->
		<!-- .................PLACE YOUR CONTENT HERE ................ -->

		 <div id="connection_container" class="connection_container">
			<div id="button_container" class="button_container">
				<div id="fb_button" class="button_connector"><img class="icon" src="images/facebook.png"></div>
				<div id="tw_button" class="button_connector"><img class="icon" src="images/Twitter.png"></div>
				<div id="fq_button" class="button_connector"><img class="icon" src="images/Foursquare.png"></div>
				<div id="ln_button" class="button_connector"><img class="icon" src="images/Linkedin.png"></div>
			</div>
			<div id="connection_area" class="connection_area">
				<div id="connection_area_title" class="title">
					Drag and Drop a Social Network icon in this area to connect!
				</div>
				<div id="connection_area_content" class="connection_area_content">
					<xc:forEach var="profile" items="${model.profileList}" varStatus="count">
						<div class="user_connected" id='${profile.id}'>
							<div class="social_image_container">
								<img id="${profile.connection_id}" class="circle icon hover_img" src="${profile.icon}"/>
							</div>
							<div class="user_image_container circle">
								<img src="${profile.thumbnail}" class="user_image"/>
							</div>
						</div>
					</xc:forEach>
				</div>
			</div>
		</div>
		
		<div class="separator"></div>
		
		<div class="messenger_container">
			<div class="title">
				Write a post here:<br/>
			</div>
			<textarea id='messageToPost' name="messenger_value" placeholder="Write here"></textArea>
			<div class="messenger_buttons_container">
				<input type="checkbox" class="messenger_ck" id="ck_all" name="ck_all" value="ck_all" onclick="manageSelection(this)"/> All
				<input type="checkbox" class="messenger_ck" id="ck_fb" name="ck_fb" value="ck_fb" onclick="manageSelection(this)"/> <img class="icon" src="images/facebook.png"/>
				<input type="checkbox" class="messenger_ck" id="ck_tw" name="ck_tw" value="ck_tw" onclick="manageSelection(this)"/> <img class="icon" src="images/Twitter.png"/>
				<input type="checkbox" class="messenger_ck" id="ck_fq" name="ck_fq" value="ck_fq" onclick="manageSelection(this)"/> <img class="icon" src="images/Foursquare.png"/>
				<input type="checkbox" class="messenger_ck" id="ck_ln" name="ck_ln" value="ck_ln" onclick="manageSelection(this)"/> <img class="icon" src="images/Linkedin.png"/> 
				<span class="button" onclick="sendPost()"> Send </span>
			</div>
		</div>
		
		<div class="separator"></div>
		
		<div class="toolbox_container">
			<div class="title_container circle title" onclick="goToSocial('friends')">
				Friends (total ${model.totFriends})
				<div class="arrow"><img src="images/arrow_right.png"/></div>
			</div>
			<div class="toolbox_content">
				<div class="toolbox_item ${model.connection.facebook}">
					Facebook (${model.counters.friend_facebook})
				</div>
				<div class="toolbox_item ${model.connection.foursquare}">
					Foursquare (${model.counters.friend_foursquare})
				</div>
				<div class="toolbox_item ${model.connection.linkedin}">
					Linkedin (${model.counters.friend_linkedin})
				</div>
				<div class="toolbox_item ${model.connection.twitter}">
					Twitter (${model.counters.friend_twitter})
				</div>
			</div>
		</div>
		
		<div class="toolbox_container">
			<div class="title_container circle title"  onclick="goToSocial('activities')">
				Activities (total ${model.totActivities})
				<div class="arrow"><img src="images/arrow_right.png"/></div>
			</div>
			<div class="toolbox_content">
				<div class="toolbox_item ${model.connection.facebook}">
					Facebook (${model.counters.activity_facebook})
				</div>
				<div class="toolbox_item ${model.connection.foursquare}">
					Foursquare (${model.counters.activity_foursquare})
				</div>
				<div class="toolbox_item ${model.connection.linkedin}">
					Linkedin (${model.counters.activity_linkedin})
				</div>
				<div class="toolbox_item ${model.connection.twitter}">
					Twitter (${model.counters.activity_twitter})
				</div>
			</div>
		</div>
		
		<div class="toolbox_container">
			<div class="title_container circle title"  onclick="goToSocial('groups')">
				Groups (total ${model.totGroups})
				<div class="arrow"><img src="images/arrow_right.png"/></div>
			</div>
			<div class="toolbox_content">
				<div class="toolbox_item ${model.connection.facebook}">
					Facebook (${model.counters.group_facebook})
				</div>
				<div class="toolbox_item ${model.connection.foursquare}">
					Foursquare (${model.counters.group_foursquare})
				</div>
				<div class="toolbox_item ${model.connection.linkedin}">
					Linkedin (${model.counters.group_linkedin})
				</div>
				<div class="toolbox_item ${model.connection.twitter}">
					Twitter (${model.counters.group_twitter})
				</div>
			</div>
		</div>
		
		 <form method="POST" action="socialdata.html" commandName="sdForm" name="sd">
			     <input type="hidden" id="method" name="method"  path="method" value="" />
				 <input type="hidden" id="snName" name="snName"    path="snName" value="" />
				 <input type="hidden" id="params" name="params" path="params" value=""/><br>
				 <input type="hidden" id="token"  name="token" path="token"   size="100" value=""/>
				 <input type="hidden" id="id"  name="id" path="idSN" value=""/>
	    </form>
		

		<!-- .................END PLACE YOUR CONTENT HERE ................ -->
		<!-- FOOTER -->
		<!--  jsp:include page="footer.jsp" /-->
		<!-- END FOOTER -->
</body>
</html>

