<?php
require_once("includes/config.php");
require_once("includes/db.php");
require_once("includes/functions.php");
require_once("includes/strings.php");

session_start();
if (!isset($_SESSION["types"])) {
	$types = TypeDAO::getTypes();
	$_SESSION["types"] = $types;
} else {
	$types = $_SESSION["types"];
}

// Check canonical URL and get permalink
checkUri();
$path = getPath();

// Initialize global variables
$item = NULL;
$type = NULL;

// Get template
if (isHome()) {
	include("template/home.php");
} else if (count($path) <= 2) {
	if ($path[0] == "aviso-legal") {
		include("template/legal.php");
	} else {
		$type = $types[$path[0]];
		if (!$type || !$type->template || $type->template == "") {
			include("template/404.php");
		} else {
			if (file_exists("template/type-$type->template.php")) {
				include("template/type-$type->template.php");
			} else {
				include("template/type.php");
			}
		}
	}
} else if (count($path) == 3) {
	$exp = explode("_", $path[1]);
	$item = ItemDAO::getItemById($exp[0]);
	if (!$item || $item->status != 'publicado') {
		include("template/404.php");
	} else {
		$type = $types[$item->type_id];
		if ($type->shortname == $path[0] && count($exp) == 2 && $item->shortname == $exp[1]) {
			if (file_exists("template/item-$type->shortname.php")) {
				include("template/item-$type->shortname.php");
			} else {
				include("template/item.php");
			}
		} else {
			header('Location: ' . getItemLink($type, $item));
		}
	}
} else {
	include("template/404.php");
}
//saveLog();

// Show queries
/*
echo '<pre>';
print_r($db_queries);
echo '</pre>';
*/

// Disconnect from database
db_disconnect();
?>