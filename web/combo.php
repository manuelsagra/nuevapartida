<?php
header('Content-Type: application/json');

require_once("includes/config.php");
require_once("includes/db.php");
require_once("includes/functions.php");
require_once("includes/strings.php");

$c = isset($_GET['c']) ? trim($_GET['c']) : '';

$results = [];
$items = [];

session_start();

if (!isset($_SESSION[$c])) {
	if($c == 'region') {
		$items = getFullCombo(TYPE_REGION);
	} else if($c == 'genre') {
		$items = getFullCombo(TYPE_GENRE);
	} else if($c == 'developer') {
		$items = ItemDAO::getCompaniesCombo(TYPE_COMPANY, TYPE_DEVELOPED);
	} else if($c == 'publisher') {
		$items = ItemDAO::getCompaniesCombo(TYPE_COMPANY, TYPE_PUBLISHED);
	} else if($c == 'platform') {
		$items = ItemDAO::getCombo(TYPE_PLATFORM, true);
	} else if($c == 'tag') {
		$items = TagDAO::getTags();
	}
	$_SESSION[$c] = $items;
} else {
	$items = $_SESSION[$c];
}


if ($items != null && count($items) > 0) {
	foreach($items as $item) {
		$r = new stdClass;
		$r->i = intVal($item->id);
		$r->n = $item->name;
		if ($c == 'tag') {
			$r->g = $item->group;
		}
		$results[] = $r;
	}
}

echo json_encode($results);
?>