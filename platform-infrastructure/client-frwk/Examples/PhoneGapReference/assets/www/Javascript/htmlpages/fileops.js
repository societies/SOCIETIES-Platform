var targetFileName = "SocietiesPhoneGapTest.txt";

var translateFileErrors = function(fileError) {
	var message = "Error";

	switch (fileError.code) {
	case FileError.NOT_FOUND_ERR:
		message = "File not found";
		break;
	case FileError.SECURITY_ERR:
		message = "Security breach";
		break;
	case FileError.ABORT_ERR:
		message = "Abort";
		break;
	case FileError.NOT_READABLE_ERR:
		message = "Not readable";
		break;
	case FileError.ENCODING_ERR:
		message = "Malformed URL";
		break;
	case FileError.NO_MODIFICATION_ALLOWED_ERR:
		message = "No modification allowed";
		break;
	case FileError.INVALID_STATE_ERR:
		message = "Invalid state";
		break;
	case FileError.SYNTAX_ERR:
		message = "Syntax problem";
		break;
	case FileError.INVALID_MODIFICATION_ERR:
		message = "Invalid modification";
		break;
	case FileError.QUOTA_EXCEEDED_ERR:
		message = "Quota exceeded";
		break;
	case FileError.TYPE_MISMATCH_ERR:
		message = "File/Directory mismatch";
		break;
	case FileError.PATH_EXISTS_ERR:
		message = "File/Directory already exists";
		break;
	default:
		message = "Unexpected Error";
		break;
	}
	return message;
};

var clearFileTags = function() {
	jQuery("#fileErrors").text("");
	jQuery("#fileStatus").text("");
};

var fileExists = function() {
	console.log("Does file exist?");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileStatus").text("False");
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function success() {
		jQuery("#fileStatus").text("True");
	}

	function onFileSystemSuccess(fileSystem) {
		jQuery("#fileSystemData").text(
				fileSystem.name + " " + fileSystem.root.fullPath);

		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var deleteFile = function() {
	console.log("Delete file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileStatus").text("False");
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function onRemoveSuccess() {
		jQuery("#fileStatus").text("Deleted");

	}

	function success(fileEntry) {
		fileEntry.remove(onRemoveSuccess, fail);
	}

	function onFileSystemSuccess(fileSystem) {

		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);
};

var createFile = function() {
	console.log("Create file");

	clearFileTags();

	function success(fileEntry) {
		alert(fileEntry.name + " created");
	}

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : true,
			exclusive : true
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var writeToFile = function() {
	console.log("Write to file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	/* Append text*/
	function writeFile(writer) {
		writer.seek(writer.length);
		writer
				.write("Some day Societies will work\nbut it may take a long time\n");
	}

	function success(fileEntry) {
		fileEntry.createWriter(writeFile, fail);
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var displayFile = function() {
	console.log("Display file");

	clearFileTags();

	function fail(fileError) {
		jQuery("#fileErrors").text(translateFileErrors(fileError));
	}

	function gotFile(file) {
		var reader = new FileReader();

		reader.onloadend = function(evt) {
			jQuery("#fileContents").text(evt.target.result);
		};

		reader.readAsText(file);
	}

	function success(fileEntry) {
		fileEntry.file(gotFile, fail);
	}

	function onFileSystemSuccess(fileSystem) {
		fileSystem.root.getFile(targetFileName, {
			create : false
		}, success, fail);
	}

	window.requestFileSystem(LocalFileSystem.TEMPORARY, 0, onFileSystemSuccess,
			fail);

};

var listFiles = function(root) {
	console.log("List files for root: " + root);

	var directoryReader = root.createReader();

	function fail(fileError) {
		alert(translateFileErrors(fileError));
	}

	function onReadSuccess(files) {
		var i;
		for (i = 0; i < files.length; i++) {
			if (files[i].isFile) {
				jQuery("#fileStatus").text(files[i].fullPath);
			}
		}
	}

	directoryReader.readEntries(onReadSuccess, fail);
};



jQuery(function() {
	console.log("file operations jQuery calls");


	$('#createFile').click(function() {
		createFile();
	});
	$('#fileExists').click(function() {
		fileExists();
	});
	$('#displayFile').click(function() {
		displayFile();
	});
	$('#writeToFile').click(function() {
		writeToFile();
	});
	$('#deleteFile').click(function() {
		deleteFile();
	});

});
