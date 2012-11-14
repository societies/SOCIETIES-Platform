<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="xc" uri="http://java.sun.com/jsp/jstl/core"  %> 
<!DOCTYPE html>
<html lang="en">
<head>
<title>Societies</title>
<meta charset="utf-8">
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/dialog.css">
<script>!window.jQuery && document.write('<script src="js/jquery-v1.8.1.js"><\/script>')</script>
<!-- Button Script -->
	<script type="text/javascript">
		  function operationSelection(){
			  var areEqual = $('#operatorValue').val().toUpperCase() === "RANGE";
		     if( areEqual){
		    	 $('#criteriaValue2').show();	
		    } else {
		      $('#criteriaValue2').val("");
		      $('#criteriaValue2').hide();
		    }
		  }
	
	
	$(document).ready(function(){
		//startup functionality
	 $('#criteriaValue2').hide();

	 var i = 0;

	 
	 document.getElementById('createCISbutton').onclick = function() {
		 document.CreateCISForm.submit();
     }
		                 
	 

	 

	 
	 document.getElementById("addCriteria").onclick = function() {

		 // test log string



			// add the row
			var row = $('<tr/>', {class: "critRow", id:i}).appendTo("#existingCriteria");
			// column 
			var column = $('<td/>', {text: $('#attributeValue').val() + "    " + $('#operatorValue').val() + "    " + $('#criteriaValue').val() }).appendTo(row);
			if($('#criteriaValue2').is(":visible") )
				column.text(column.text() + "    " + $('#criteriaValue2').val()); 
			var inputAttribute = $('<input/>', {id:"ruleArray"+i+".attribute", value: $('#attributeValue').val(), name:"ruleArray["+i+"].attribute", type:"hidden"}).appendTo(row);
			var inputOperator = $('<input/>', {id:"ruleArray"+i+".operator", value: $('#operatorValue').val(), name:"ruleArray["+i+"].operator", type:"hidden"}).appendTo(row);
			var inputValue = $('<input/>',  {id:"ruleArray"+i+".value1", value: $('#criteriaValue').val(), name:"ruleArray["+i+"].value1", type:"hidden"}).appendTo(row);
			var inputValue2 = $('<input/>', {id:"ruleArray"+i+".value2", value: $('#criteriaValue2').val(), name:"ruleArray["+i+"].value2", type:"hidden"}).appendTo(row);
			var inputDeleted = $('<input/>', {id:"ruleArray"+i+".deleted", value: false, name:"ruleArray["+i+"].deleted", type:"hidden"}).appendTo(row);
			// button
			var f = function () {row.remove(); $('#ruleArray'+i+".deleted").val(true)};
			var b = $('<button/>',{		  text: "Delete", type: "button", 
				  click: f
			}).appendTo(column);

			i++;
	};// end of document.getElementById("addCriteria").onclick = function() {

	});// end of $(document).ready(function()
			
	</script>

<!--[if IE]>
<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script><![endif]--> 
<!--[if lt IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" />
<script src="http://ie7-js.googlecode.com/svn/version/2.1(beta4)/IE7.js"></script><![endif]-->
<!--[if IE 7]>
<link rel="stylesheet" type="text/css" media="all" href="css/ie7.css" /><![endif]-->
<!-- Menu -->
<script src="js/webmenu_nav.js"></script>
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
<!-- Left Column -->
<section id="left_col" class="grid_8">
<div class="breadcrumbs"><a href="">Home</a> / <a href="create_community.html">Create community</a></div>

<div id="msgid">
</div>

<!-- Form -->
<section id="form_style_main">
<form:form method="POST" action="create_community.html" commandName="createCISform" name="CreateCISForm">
		<form:errors path="*" cssClass="errorblock" element="div" />
	
<h4 class="form_title">Create a Community</h4>
<div class="hr dotted clearfix">&nbsp;</div>
<ul>						
<li class="clearfix">
<label for="">Community Name</label>
<form:input path="cisName" />
<div class="clear"></div>
<form:errors path="cisName" cssClass="error" /></li> 


<li class="clearfix">
<label for="">Community type</label>
<form:input path="cisType" />
<div class="clear"></div>
<form:errors path="cisType" cssClass="error" />
</li> 




<li class="clearfix"> 
<label for="">Description</label>
<form:textarea path="cisDescription" rows="30" cols="30"/>
<div class="clear"></div>
<form:errors path="cisDescription" cssClass="cisDescription" />
</li>

<li class="clearfix"> 
<label for="">Criteira</label>
<p><p>
<table id="existingCriteria"></table>
<p>
				<table id="critTable">
					<tbody>
					<tr id="critFistRow"><td>Attribute</td><td>Operator</td><td>Values</td></tr>

					<tr>
					<td><form:select id="attributeValue" path="dummyAtr" cssClass="select-wrapper"><form:options items="${listOfAttributes}"/></form:select></td>
					<td><form:select id="operatorValue" path="dummyOperation" cssClass="select-wrapper" onclick="operationSelection();" onkeypress="operationSelection();"><form:options items="${listOfOperators}"/></form:select></td>
					<td><form:input id="criteriaValue" path="dummyValue" /></td>
					<td><form:input id="criteriaValue2" path="dummyValue2" /></td>
					<td><button type="button" id="addCriteria">Add Criteria</button></td>
									

					</tr>


					</tbody>
				</table>



</li>
<!--
<li class="clearfix"> 
<label for="">Choose an option</label>
<div class="select-wrapper">
<select name="select" required>
<option value="1">Value 1</option>
<option value="2">Value 2</option>
<option value="3">Value 3</option>
<option value="4">Value 4</option>
</select>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 
<li class="clearfix"> 
<label for="">Choose opt</label>
<div class="option-group radio">
<input type="radio" name="" id="radio1" />
<label for="radio1">Option 1</label>
<input type="radio" name="" id="radio2" />
<label for="radio2">Option 2</label>			
<input type="radio" name="" id="radio3" />
<label for="radio3">Option 3</label>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 

<li class="clearfix"> 
<label for="option">Choose check </label>
<div class="option-group check">
<input type="checkbox" name="" id="check1" />
<label for="check1">Check 1</label>			
<input type="checkbox" name="" id="check2" />
<label for="check2">Check 2</label>
<input type="checkbox" name="" id="check3" />
<label for="check3">Check 3</label>
</div>
<div class="clear"></div>
<p id="option" class="error">Please, select an option</p>
</li> 
<li class="clearfix"> 
<p class="success">Thank you! Success Message here.</p>
<p class="error">Sorry, an error has occured. Please try again later.</p>	-->
<div id="button">
<input type="submit" id="createCISbutton" class="sendButton" value="Create Community" />
</div>				
</li> 
</ul> 
</form:form>


</section>
</section>
<!-- Right Column / Sidebar -->
<aside id="sidebar_right" class="grid_4">
<div class="sidebar_top_BG"></div>
<div class="hr dotted clearfix">&nbsp;</div>
<!-- <section>
<header>
<h3>Other Communities...</h3>
</header>
<ul class="sidebar">
<li><a href="">Community name</a></li>
<li><a href="">Community name</a></li>
<li><a href="">Community name</a></li>
<li><a href="">Community name</a></li>
<li><a href="">Community name</a></li>
</ul>
</section>
<section>
<header>
<h3>Other Activity</h3>
</header>
<div class="hr dotted clearfix">&nbsp;</div>
<ul class="contact_data">
<li><figure class="gravatar">
<img alt="" src="images/webcommunity_pic_sample1.jpg" height="48" width="48" />
</figure><strong>Community Name</strong> dolor sit amet, consectetur adipiscing elit. Ipsum dolor sit amet, elit.</li>
</ul>
<ul class="contact_data">	
<li>Other Link - <a href="#">Link 1</a></li>
<li>Other Link - <a href="#">Link 2</a></li>
<li>Other Link - <a href="#">Link 3</a></li>
</ul> 
</section>
-->
<div class="hr dotted clearfix">&nbsp;</div>
<div class="sidebar_bottom_BG"></div>
</aside>
<div class="hr grid_12 clearfix">&nbsp;</div>
</div>




<!-- #Main Content -->	
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