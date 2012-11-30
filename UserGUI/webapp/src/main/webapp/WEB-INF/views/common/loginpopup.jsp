<div class="login-form">
<a href="#login-box" class="login-window">LOGIN / REGISTER</a>
</div>
<div id="login-box" class="login-popup">
<a href="#" class="close"><img src="images/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>
<form:form method="POST" class="signin" action="login.html" commandName="loginform">
<fieldset class="textbox">
<label class="username">
<span>Username</span>
</label>
<form:input id="username" path="username" value="tttt" type="text" autocomplete="on" placeholder="Username" width="100%"/>

<label class="password">
<span>Password</span>
</label>
<form:input id="password" path="password" value="tttt" type="password" placeholder="Password" width="100%">

<button class="submit button" type="button">Sign in</button>
<p>Don't have a login?
<a class="" href="new_account.html">Sign Up</a>
</p>        
</fieldset>
</form:form>
</div>
