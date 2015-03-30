<?php
require_once("includes/config.php");
require_once("includes/db.php");
require_once("includes/functions.php");
require_once("includes/strings.php");

$id = isset($_GET['id']) ? intval($_GET['id']) : 0;
$size = isset($_GET['size']) ? intval($_GET['size']) : 256;
$img = getItemImage($id, $size, true);

if ($img != '') {
	header('Location: ' . $img);
} else {
	header('Location: ' . BASE_URL . 'img/defaultitem.png');
}

header('Content-Type: image');
?>