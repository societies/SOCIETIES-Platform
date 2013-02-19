function contacts_success(contacts) {
	alert(contacts.length +
			" contacts returned." + 
			(contacts[2] && contacts[2].name ? (" Third contact is " + contacts[2].name.formatted)
					: ""));
}

function failNoContacts() {
	alert("No Contacts");
}

var getContacts = function() {
	console.log("Get phone contacts");

	var obj = new ContactFindOptions();
	obj.filter = "";
	obj.multiple = true;
	obj.limit = 5;
	navigator.contacts.find([ "displayName", "name" ], contacts_success,
			failNoContacts, obj);
};


jQuery(function() {
	console.log("contacts jQuery calls");


	$('#userContacts').click(function() {
		getContacts();
	});
});