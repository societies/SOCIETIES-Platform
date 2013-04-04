/**  
   Socialdata JS 
	private static final String ADD				= "add";
	private static final String REMOVE			= "remove";
	private static final String FRIENDS			= "friends";
	private static final String PROFILES		= "profiles";
	private static final String GROUPS			= "groups";
	private static final String ACTIVITIES		= "activities";
	private static final String LIST			= "list"


*/
  


function submitform(){
    token= document.getElementById("token");
    method= document.getElementById("method");
    
    if (method.value==""){
    	alert("Please add a valid method)");
    	return;
    }
    
    if (token.value==""){
    	alert("Please add a valid token (copy from popup)");
    	w.focus(); 
    	return;
    }
    else {
    	document.getElementById("addConnector").style.visibility= "hidden";
    	document.getElementById("popup").style.visibility="visible";
    	document.sd.submit();
    }
}

function countConnectors(){
   var ul = document.getElementById('listConn');
   var liNodes = [];
 
   for (var i = 0; i < ul.childNodes.length; i++) {
	if (ul.childNodes[i].nodeName == "LI") {
		liNodes.push(ul.childNodes[i]);
	}
   }
   
   return liNodes.length;
}

  
  function disconnect(id){
    document.getElementById("method").value  = "remove";
    document.getElementById("id").value  = id;
    if (id==""){
      alert("Connector id:"+id+ "not Valid!");
    }
    else  document.sd.submit();
  } 


  function exe(method){
  
     if(countConnectors()>0){
  	    document.getElementById("method").value  = method;
  	    document.sd.submit();
  	 }
  	 else{
  	 	alert("Add a connector first!");
  	 }
  }
  
  function connectSN(sn){
	   document.getElementById("method").value  = sn;
	   document.sd.submit();
//	    w = window.open("http://societies.lucasimone.eu/connect.php?sn=" + sn +"&from=http://societies.lucasimone.eu/print.php", "TEST1", "width=400,height=400,resizeable,scrollbars");
  }
  
  

  function getToken(url, title){
     w = window.open(url, title, "width=600,height=400,resizeable,scrollbars");
     document.getElementById("addConnector").style.visibility= "visible"; 
    // setTimeout(CheckLoginStatus(), 3000);
    document.getElementById("method").value  = "add";
    document.getElementById("snName").value  = title;
    document.getElementById("token").value  = "";
 
  }
  
  
  function removeform(){
    document.getElementById("addConnector").style.visibility= "hidden";
    }

 function CheckLoginStatus() {
      
      
     
      try {
      
      
      if (document.readyState === "complete"){
      	alert("You need to copy these params in the form!");
      }
      /*
      var json = JSON.parse(w.document.body.innerHTML);
      
      if(json.connector!=""){
         
    	 document.getElementById("token").value   = json.connector.access_token;
         document.getElementById("expires").value = json.connector.expires;
         document.getElementById("snname").value  = json.connector.from;
         document.getElementById("method").value  = "add";
         //document.sdForm.submit();
         w.close();
      }
      */
      else setTimeout(CheckLoginStatus, 1000);
   } 
   catch(e)
   {
       //alert(e + " - " + body);
       setTimeout(CheckLoginStatus, 1000);
   }
  	   
 }
 
 function queryBundleStatus() {
	  $.ajax({
	    url: 'status',
	    success: function(data) {
	    	$('#status').setVal(data); 
	    }
	  });
	  setTimeout(queryBundleStatus, 5000); // you could choose not to continue on failure...
	}

	
 
 $(document).ready(function(){ 
	 
	  $('#status').val("Reading status..."); 
	  setTimeout(queryBundleStatus, 5000);
});

 	
 
