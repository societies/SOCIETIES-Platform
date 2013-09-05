<?php
function parseInt($int) {
	return intval(trim($int));
}
function parseFloat($decimal) {
	return floatval(trim($decimal));
}
function parseHexa($hexa) {
	$hexa = trim($hexa);
	if (!('0' == $hexa[0] && 'x' == $hexa[1])) {
		return '0x'.$hexa;
	}
	return $hexa;
}
function parseString($str) {
	return trim($str);
}
function parseConstant($str) {
	$str = trim($str);
	$str = str_replace(array('-', ' '), array('_', '_'), $str);
	$str = preg_replace('!([a-z])([A-Z])!', '$1_$2', $str);
	$str = preg_replace('!(IHM|CAN)([A-Z])!', '$1_$2', $str);
	$str = preg_replace('!([A-Z])(Req|Res)!i', '$1_$2', $str);
	$str = strtoupper($str);
	return $str;
}

function startsWith($hay, $needle) {
	return substr($hay, 0, strlen($needle)) === $needle;
}

function endsWith($hay, $needle) {
	return substr($hay, -strlen($needle)) === $needle;
}

function removeWhiteSpaces($str) {
	$str = preg_replace('!\s!', '', $str);
	return $str;
}

function space2Tab($str) {
	return str_replace('    ', '\t', $str);
}

function echa($array) {
	echo '<pre>'; var_dump($array); echo '</pre><br />';
}

function prepareJSON($input) {
	//This will convert ASCII/ISO-8859-1 to UTF-8.
	//Be careful with the third parameter (encoding detect list), because
	//if set wrong, some input encodings will get garbled (including UTF-8!)
	$imput = mb_convert_encoding($input, 'UTF-8', 'ASCII,UTF-8,ISO-8859-1');

	//Remove UTF-8 BOM if present, json_decode() does not like it.
	if(substr($input, 0, 3) == pack("CCC", 0xEF, 0xBB, 0xBF)) $input = substr($input, 3);

	return $input;
}

function getJSONError() {
	$constants = get_defined_constants(true);
	$json_errors = array();
	foreach ($constants["json"] as $name => $value) {
		if (!strncmp($name, "JSON_ERROR_", 11)) {
			$json_errors[$value] = $name;
		}
	}
	return 'Dernière erreur JSON: '. $json_errors[json_last_error()].'<br />';
}

?>