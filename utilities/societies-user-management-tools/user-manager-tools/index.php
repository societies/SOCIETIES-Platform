<!DOCTYPE html>
<html lang="fr">
<head>
	<meta charset="utf-8">
	<title>SOCIETIES - User Management Tools</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<!--[if lt IE 9]>
	  <script src="assets/js/html5.js"></script>
	<![endif]-->
	<link href="assets/css/bootstrap.min.css" rel="stylesheet">
	<link href="assets/css/bootstrap-responsive.min.css" rel="stylesheet">
	<link rel="stylesheet" href="assets/css/custom-style.css" type="text/css" />
</head>
<body>

<div class="navbar navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<a class="brand" href="">SOCIETIES - User Management Tools</a>
		</div>
	</div>
</div>
    
<div class="container">

<?php
include_once('src/tools.php');
$generators = array(
	'ContainerGenerator' => 'generator/virgo/VirgoGenerator',
	);
if (isset($_POST['patron']) && NULL != $_POST['patron']) {
	$patron = $_POST['patron'];
	include_once('src/'.$generators[$_POST['generator']].'.class.php');
	$generatorClass = explode('/', $generators[$_POST['generator']]);
	$generatorClass = $generatorClass[2];
	$generator = new $generatorClass(@$_POST['prefixe'], @$_POST['generatedPath'], @$_POST['templatePath']);
	// Generate content
	$content = $generator->generate($generator->parse($patron));
}
else {
	$patron = file_get_contents('src/data/VirgoGeneratorExemple.json');
}
?>
<form action="index.php" method="post">
	<input type="submit" name="generate" class="generate btn btn-primary btn-large" value="Generate" tabindex="10" />
	<div class="row show-grid">
	<div class="span4">
	<label for="generator">Generator*</label>
		<select name="generator" id="generator" tabindex="20" >
			<?php
			foreach($generators AS $generator => $path) {
				echo '<option value="'.$generator.'"'.($generator == @$_POST['generator'] ? ' selected="selected"' : '').'>'.$generator.'</option>';
			} 
			?>
		</select>
	
	<label for="patron">Parameters*</label>
		<textarea name="patron" id="patron" rows="15" tabindex="50" placeholder="JSON formatted parameters"><?php echo @$patron; ?></textarea>
	</div>
	
	<?php
	echo 'Go to "gen/" folder. It contains generated files (as described below).';
	if (isset($content) && NULL != $content && '' != $content) {
		if (is_array($content)) {
			foreach($content AS $str) {
				echo $str;
			}
		}
		else {
			echo $content;
		}
	}
	?>
	</div>
	<input type="submit" name="generate" class="generate generateBottom btn btn-primary btn-large" value="Generate" />
</form>


	<footer>&copy; Trialog</footer>
</div>

<script src="style/js/jquery.min.js"></script>
<script src="style/js/bootstrap.min.js"></script>

</body>
</html>
