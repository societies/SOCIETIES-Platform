<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="xc" uri="http://java.sun.com/jsp/jstl/core"  %> 
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>


	<script type="text/javascript" src="js/jquery-v1.8.1.js">
 

var nextMembers;
var previoustMembers;




$(document).ready(function(){
	//startup functionality
	
	
	//managing member list display
	// initial setup
	/*var member_pagination = 5;
	
	var memberListFirstIndex = 0;
	var memberSet = $(".member_item_class");
	var memberSetSize = memberSet.length;
	var lastPaginationSize = memberSetSize/member_pagination; 
	

	// initial layout
	memberSet.slice(0,memberSetSize).hide();
	if(memberSetSize <= member_pagination){
		$("#linkBrowseNExtMembers").hide();
		memberSet.slice(0,memberSetSize).show();
	}
	else{
		memberSet.slice(0,member_pagination).show();
	}
	$("#linkBrowsePreviousMembers").hide();
	
	// functions
	// Next function
	nextMembers = function (){
		memberSet.slice(memberListFirstIndex,member_pagination +1).hide();
		$("#linkBrowsePreviousMembers").show();
		memberListFirstIndex = memberListFirstIndex + member_pagination;
		if( (memberListFirstIndex + member_pagination) < lastPaginationSize ){
			memberSet.slice(memberListFirstIndex,member_pagination +1).show();
			$("#linkBrowseNExtMembers").show();
		}else{
			memberSet.slice(memberListFirstIndex,member_pagination +1).show();
			$("#linkBrowseNExtMembers").hide();			
		}
	};
	// Prev function
	previoustMembers = function (){
		memberSet.slice(memberListFirstIndex,member_pagination +1).hide();
		$("#linkBrowseNExtMembers").show();
		memberListFirstIndex = memberListFirstIndex - member_pagination;
		memberSet.slice(memberListFirstIndex,member_pagination +1).show();
		if( (memberListFirstIndex - member_pagination) == 0){
			$("#linkBrowsePreviousMembers").hide();
		}
	};
	
	$("#linkBrowseNExtMembers").onclick = nextMembers;
	$("#linkBrowsePreviousMembers").onclick = previoustMembers;
	
	// managing activity list display
	*/
	
	
 document.getElementById('postActButton').onclick = function() {
	 document.AddActivityForm.submit();
	 };
	 
	 document.getElementById('addMemberButton').onclick = function() {
		 document.AddMemberForm.submit();
		 };

	 
	 //	 document.getElementById('getPolicyButton').onclick = function createPolicyWindow () { 
	//		var htmlText = ${priacyPolicyString};
	//		window.open("data:text/xml;charset=utf-8,<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + htmlText);
	//	};

});// end of $(document).ready(function()

		

		 
</script>


</head>
<body>
<div id="wrapper" class="clearfix">
<div id="container" class="container_12 clearfix">
<!-- Header -->
<header id="header" class="grid_12">
<div class="login-form">   
</div>
<!-- Logo -->
<h1 id="logo" class="logo-pos">
<a href="index.html"><img src="images/societies_logo.jpg" alt="Logo" /></a>
</h1>
<!--WebMenu -->
<nav id="webmenu_nav">
<ul id="navigation" class="grid_8">
<li><a href="myProfile.html"><br />My Account</a>
<ul class="sub-menu"> 
<li><a href="myProfile.html">My Profile</a></li>
<li><a href="profilesettings.html">Profile Settings</a></li>
<li><a href="settings.html">Security Settings</a></li>
<li><a href="privacysettings.html">Privacy Settings</a></li>
</ul>
</li>
<li><a href="your_installed_apps.html"><br />Apps</a>
</li>
<li><a href="your_societies_friends_list.html"><br />Friends</a>
<ul class="sub-menu"> 
<li><a href="your_societies_friends_list.html">Your Friends</a></li>
<li><a href="suggested_societies_friends_list.html">Suggested Friends</a></li>
</ul>
</li>
<li><a href="your_communities_list.html"><br />Communities</a>
<ul class="sub-menu"> 
<li><a href="your_communities_list.html">Your Communities</a></li>
<li><a href="manage_communities.html">Manage your Communities</a></li>
<li><a href="create_community.html">Create a Community</a></li>
<li><a href="your_suggested_communities_list.html">Suggested Communities</a></li>
</ul>
</li>
<li><a href="index.html"><br />Home</a></li>
</ul>
</nav><!-- #webmenu -->
<div class="clear"></div>
</header><!-- #header -->
<div class="hr grid_12 clearfix">&nbsp;</div>
<section  class="grid_12">
<section>
<div class="breadcrumbs"><a href="">Home</a> / 
<xc:if test="${cisInfo != null}">
	<a href="community_profile.html?cisId=${cisInfo.getCommunityJid()}">${cisInfo.getCommunityName()}</a>
</xc:if>

<br>
<xc:if test="${not empty response}">
	<div >${response}</div>
</xc:if>

</div>

</section>
<div class="websearchbar">



<div class="websearchtitle">
<xc:if test="${cisInfo != null}">
	<h4 class="profile_title">${cisInfo.getCommunityName()} Profile</h4>
</xc:if>
</div>
<div class="groupsearch">
<form action="" class="websearch-form frame nobtn rsmall">
<input type="text" name="search" class="websearch-input" placeholder="Search for Friends..." />
</form>
</div>
</div>
</section>
<!-- Left Column -->
<section id="left_col" class="grid_8">
<xc:if test="${not empty response}">
	<div >${response}</div>
</xc:if>

<xc:if test="${cisInfo != null}">
	<section>
	<figure class="gravatar">
	<img alt="" src="images/webcommunity_pic_sample1.jpg" height="48" width="48" />
	
		<xc:if test="${isOwner == true}">
			<a class="furtherinfo-link" href="delete_community.html?cisId=${cisInfo.getCommunityJid()}" onclick="return confirm('Are you sure you want to delete the CIS?')">REMOVE</a>
		</xc:if>
		<xc:if test="${isOwner == false}">
			<a class="furtherinfo-link" href="leave_community.html?cisId=${cisInfo.getCommunityJid()}" onclick="return confirm('Are you sure you want to leave the CIS?')">LEAVE</a>
		</xc:if>
	
	</figure>
	<div class="keyinfo_content">
	<div class="clearfix">
	<cite class="author_name"><a href="friend_profile.html?cssId=${cisInfo.getOwnerJid()}">Owner</a>
	    <!--<input type="submit" id="getPolicyButton"   value="getPolicy"  >-->
	    </cite>
	</div>
	<div class="keyinfo_text">
	<p>${cisInfo.getDescription()}</p>
	</div>
	<p><strong>Activity Feed:</strong></p>
	<!-- Unordered -->
	
	<xc:if test="${not empty acitivityAddError}">
		<div class="error">${acitivityAddError}</div>
	</xc:if>
	
	
	<xc:if test="${activities != null}"><!--list activities just if it is an owned or joined CIS-->
		
		<form:form method="POST" action="add_activity_cis_profile_page.html" commandName="activityForm" name="AddActivityForm">
				<form:errors path="*" cssClass="errorblock" element="div" />
				
				<table id="addActivityFormInputs">
				<tr>
				<td><form:input path="object" defaultValue="write your activity here"/></td>
				<td><form:errors path="object" cssClass="error" /></td>
				</tr>
				
				<tr>
					<td><form:input path="cisId" style="display:none;" value="${cisInfo.getCommunityJid()}"/></td>
					<td><form:errors path="cisId" cssClass="error" /></td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><form:input path="verb" style="display:none;" value="posted"/></td>
					<td><form:errors path="verb" cssClass="error" /></td>
					<td>&nbsp;</td>
				</tr>
					<tr>
						<td colspan="3"><input id="postActButton" type="button" value="PostActivity"/></td>
					</tr>
				</table>
				
		</form:form>
		
		<ul>
		<xc:forEach var="activity" items="${activities}">
				<li class="activity_item_class">
		${activity.getActor()} <font color="red"> ${activity.getVerb()} </font> ${activity.getObject()}  
		
		<xc:if test="${activity.getTarget() != null}">
		at 	<font color="red">${activity.getTarget()}</font>
		</xc:if>  
				</li>
		</xc:forEach>
		</ul>
	</xc:if>
	
	<ul>
	<li>List item example</li>
	<li>List item example
	</li>
	<li><a class="tooltip" href="#" title="">List item example</a></li>
	</ul>
	<article class="post">
	&nbsp;
	</article>
	<p><strong>Additional Info:</strong></p>
	<!-- Unordered -->
	<ul>
	<li>List item example</li>
	<li>List item example
	</li>
	<li><a class="tooltip" href="#" title="">List item example</a></li>
	</ul>
	<article class="post">
	&nbsp;
	</article>
	
	
	<p><em>Membership Criteria:</em></p> 
	
	<table id="membership Criteria" class="table">
	
		<xc:if test="${cisInfo.getMembershipCrit().getCriteria().size() > 0}">
			<tr id="critFistRow"><td>Attribute</td><td>Operator</td><td>Values</td></tr>
			<xc:forEach var="criteria" items="${cisInfo.getMembershipCrit().getCriteria()}">
				<tr>
				<td>${criteria.getAttrib()}</td>
				<td>${criteria.getOperator()}</td>
				<td>${criteria.getValue1()}</td>
				<xc:if test="${criteria.getValue2() != null}">
					<td>${criteria.getValue2()}</td>
				</xc:if>  
				</tr>
			</xc:forEach>
		</xc:if>
	</table>
	
	<xc:if test="${cisInfo.getMembershipCrit().getCriteria().size() == 0}">
	<p>No membership criteria for this community</p> 
	</xc:if>
	
	</div>
	<div class="hr dotted clearfix">&nbsp;</div>
	</section>
</xc:if>
</section>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>		
<div class="hr dotted clearfix">&nbsp;</div>	
<xc:if test="${cisInfo != null}">

	<section>
	<header>
	<h3>Members</h3>
	</header>
	<ul class="sidebar">
	<xc:forEach var="participant" items="${cisInfo.getParticipant()}">
			<li class="member_item_class">
	<a href="friend_profile.html?cssId=${participant.getJid()}">${participant.getJid()}</a>
	<xc:if test="${isOwner == true && cisInfo.getOwnerJid() != participant.getJid()}">
			<a class="furtherinfo-link" href="delete_member.html?cisId=${cisInfo.getCommunityJid()}&cssId=${participant.getJid()}" onclick="return confirm('Are you sure you want to delete this member?')">Delete Member</a>
	</xc:if>
	 
			</li>
			
	</xc:forEach>
	<li><button id="linkBrowseNExtMembers" hidden="false">Next</button> <button id="linkBrowsePreviousMembers" class="furtherinfo-link" hidden="false">Previous</button></li>
	
	
		<xc:if test="${isOwner == true}">
			<form:form method="POST" action="add_member_cis_profile_page.html" commandName="memberForm" name="AddMemberForm">
					<form:errors path="*" cssClass="errorblock" element="div" />
					
					<table id="addMemberFormInputs">
					<tr>
					<td><form:input path="cssJid" defaultValue="jid of member to be added"/></td>
					<td><form:errors path="cssJid" cssClass="error" /></td>
					</tr>
					
					<tr>
						<td><form:input path="cisJid" style="display:none;" value="${cisInfo.getCommunityJid()}"/></td>
						<td><form:errors path="cisJid" cssClass="error" /></td>
					</tr>
						<tr>
							<td colspan="2"><input id="addMemberButton" type="button" value="AddMember"/></td>
						</tr>
					</table>
					
			</form:form>		
		</xc:if>
	
	
	</ul>
	</section>
</xc:if>
<section>
<header>
<h3>App Activity</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</li>
</ul>
<ul class="contact_data">	
<li>Other Link - <a href="#">Link 1</a></li>
<li>Other Link - <a href="#">Link 2</a></li>
</ul> 
</section>
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>


<!-- Footer -->
<footer class="container_12 clearfix">
<section class="footer">
<p class="footer-links">
<span><a href="termsofuse.html">Terms of Use</a> | <a href="disclaimer.html">Disclaimer</a> | <a href="privacy.html">Privacy</a> | <a href="help.html">Help</a> | <a href="about.html">About</a></span>
<a class="float right toplink" href="#">top</a>
</p>
</section>
</footer>
</div> 	
</body>
</html>