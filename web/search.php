<?php
header('Content-Type: application/json');

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

$type_id = isset($_GET['type']) ? intval($_GET['type']) : 2;

$criteria = new stdClass;
$criteria->s = isset($_GET['s']) ? mysql_real_escape_string(trim($_GET['s'])) : '';
$criteria->code = isset($_GET['code']) ? mysql_real_escape_string(trim($_GET['code'])) : '';
$criteria->year_from = isset($_GET['from']) ? mysql_real_escape_string(intval($_GET['from'])) : 1994;
$criteria->year_to = isset($_GET['to']) ? mysql_real_escape_string(intval($_GET['to'])) : intval(date("Y"));
$criteria->platform = isset($_GET['platform']) ? mysql_real_escape_string($_GET['platform']) : '';
$criteria->region = isset($_GET['region']) ? mysql_real_escape_string($_GET['region']) : '';
$criteria->genre = isset($_GET['genre']) ? mysql_real_escape_string($_GET['genre']) : '';
$criteria->developer = isset($_GET['developer']) ? mysql_real_escape_string($_GET['developer']) : '';
$criteria->publisher = isset($_GET['publisher']) ? mysql_real_escape_string($_GET['publisher']) : '';
$criteria->tag = isset($_GET['tag']) ? mysql_real_escape_string($_GET['tag']) : '';

$items = [];
switch($type_id) {
	case TYPE_GAME:
		$items = ItemDAO::getGameResults($criteria);
		break;
}

$results =[];
$years = [];
$platforms = [];
$regions = [];
$genres = [];
for ($i = $criteria->year_from; $i <= $criteria->year_to; $i++) {
	$years[$i] = 0; 
}

switch($type_id) {
	case TYPE_GAME:
		if ($items != null && count($items) > 0) {
			foreach ($items as $item) {
				$r = new stdClass;
				$r->i = intVal($item->id);
				$r->n = $item->name;
				$results[] = $r;

				if ($criteria->platform == '') {
					foreach(explode(",", $item->platforms) as $platform) {
						if (array_key_exists($platform, $platforms)) {
							$platforms[$platform]++;
						} else {
							$platforms[$platform] = 1;
						}
					}
				}

				if ($criteria->genre == '') {
					foreach(explode(",", $item->genres) as $genre) {
						if (array_key_exists($genre, $genres)) {
							$genres[$genre]++;
						} else {
							$genres[$genre] = 1;
						}
					}
				}

				if ($criteria->region == '') {
					foreach(explode(",", $item->regions) as $region) {
						if (array_key_exists($region, $regions)) {
							$regions[$region]++;
						} else {
							$regions[$region] = 1;
						}
					}
				}

				$years[getYear($item->date)]++;
			}
		}
		break;
}

$result = new stdClass;
$result->type = $types[$type_id]->shortname;
$result->years = $years;
if ($type_id == TYPE_GAME && $criteria->platform == '') {
	$result->platforms = $platforms;
}
if ($type_id == TYPE_GAME && $criteria->region == '') {
	$result->regions = $regions;
}
if ($type_id == TYPE_GAME && $criteria->genre == '') {
	$result->genres = $genres;
}
$result->results = $results;

echo json_encode($result);
?>