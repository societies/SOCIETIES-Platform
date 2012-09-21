var type;

function lookup(model, type){
	 document.getElementById("method").value  = "lookup";
	 document.getElementById("lookupModel").value  = $('#ctx-type').val();
	 document.getElementById("lookupType").value  = type;
	 document.ctx.submit();
}




(function($) {
        $(document).ready(function() {
       
       
       $('#ctx-type').change(function() {
  				var model = $(this).val();
  				if (model == "entity"){
  					document.getElementById("entity-sel").style.visibility= "visible";
  					document.getElementById("attribute-sel").style.visibility= "hidden";
  					document.getElementById("association-sel").style.visibility= "hidden";
  					
  				}else if (model == "attribute"){
  					document.getElementById("entity-sel").style.visibility= "hidden";
  					document.getElementById("attribute-sel").style.visibility= "visible";
  					document.getElementById("association-sel").style.visibility= "hidden";
  				}else{
  					document.getElementById("entity-sel").style.visibility= "hidden";
  					document.getElementById("attribute-sel").style.visibility= "hidden";
  					document.getElementById("association-sel").style.visibility= "visible";
  				}
	    });
       
       $('#entity-sel').change(function() { type= $(this).val();});
       $('#attribute-sel').change(function() { type= $(this).val();});
       $('#association-sel').change(function() { type= $(this).val();});
       
       $('#lookup').click(function(){lookup();});
       
        $('input[name="lookup"]').search('#ctx-id tr', function(on) {
            on.all(function(results) {
              var size = results ? results.size() : 0
              $('#count').text(size + ' matches');
            });

            on.reset(function() {
              $('#none').hide();
              $('#ctx-id tr').show();
               $('#count').text("");
            });

            on.empty(function() {
              $('#none').show();
              $('#ctx-id tr').hide();
            });

            on.results(function(results) {
              $('#none').hide();
              $('#ctx-id tr').hide();
              results.show();
            });
          });
        });
      })(jQuery);