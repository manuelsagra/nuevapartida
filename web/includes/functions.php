<?php
// URL functions
function checkUri() {
	if (substr($_SERVER["REQUEST_URI"], -1) != '/' && count($_GET) == 0) {
		header('Location: ' . $_SERVER["REQUEST_URI"] . '/');
	}
}
function getPath() {
	$uri = substr($_SERVER["REQUEST_URI"], strlen(BASE_URL));
	return explode('/', $uri);
}
function getItemLink($type, $item) {
	return BASE_URL . "$type->shortname/$item->id" . "_$item->shortname/";
}
// Type of page
function isHome() {
	global $path;
	return $path[0] == "";
}
function isType($name) {
	global $type;
	if (!$type || strrpos($type->shortname, $name) === false) {
		return false;
	} else {
		return true;
	}
}
// Combos
function getOptionYears() {
	$opt = '';
	for ($i = 2014; $i > 1950; $i--) {
		$opt .= '<option value="' . $i . '">' . $i . '</option>';
	}
	return $opt;
}
// Images
function getItemDir($id) {
	$id .= '';
	$r = 'items/';
	while (strlen($id) > 3) {
		$p[] = substr($id, -3);
		$id = substr($id, 0, strlen($id) - 3);
	}
	$p[] = $id;
	for ($i = count($p) - 1; $i >= 0; $i--) {
		$r .= $p[$i] . '/';
	}
	return $r;
}
// Items
function getPlatformClass() {
	global $attributes;
	if(is_array($attributes)) {
		foreach($attributes as $attr) {
			if ($attr->type_id == TYPE_PLATFORM) {
				return $attr->shortname;
			}
		}
	}
}
function getPlatformLink() {
	global $attributes;
	if(is_array($attributes)) {
		foreach($attributes as $attr) {
			if ($attr->type_id == TYPE_PLATFORM) {
				return "<a href=\"" . BASE_URL . "plataformas/" . $attr->id . "_" . $attr->shortname . "/\" class=\"format\" title=\"" . $attr->name . "\"></a>";
			}
		}
	}
}
function getItemImage($id, $size = 256, $square = false) {
	$p = getItemDir($id) . 'item' . $size . ($square ? 's' : '') . '.jpg';
	if (file_exists(getcwd() . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, $p))) {
		return BASE_URL . $p;
	} else {
		return '';
	}
}
function showTags() {
	global $item;
	$tags = ItemTagDAO::getItemTags($item->id);
	$val = '';
	if(is_array($tags)) {
		foreach($tags as $tag) {
			$val .= "<li><a href=\"" . BASE_URL . "etiqueta/$tag->shortname\">$tag->name</a></li>";	
		}
		if ($val != '') {
			echo "<ul class=\"tags clearfix\">$val</ul>";
		}
	}
}
function showValue($m) {
	return $m->value;
}
function showLink($m) {
	return "<a href=\"$m->value\">$m->name</a>";
}
function showItemLink($m) {
	global $types;
	return "<a href=\"" . getItemLink($types[$m->type_id], $m) . "\">$m->name</a>";
}
function showRegion($m) {
	global $types;
	return "<a href=\"" . getItemLink($types[$m->type_id], $m) . "\" class=\"region $m->shortname\">$m->name</a>";
}
function showGenre($m) {
	global $types;
	return "<a href=\"" . getItemLink($types[$m->type_id], $m) . "\">" . ($m->parent_id != 0 ? getGenre($m->parent_id)->name . ' &raquo; ' : '') . "$m->name</a>";
}
function getGenre($id) {
	global $genres;
	foreach ($genres as $genre) {
		if ($genre->id == $id) {
			return $genre;
		}
	}
}
function showBarcode($m) {
	$barcode = getcwd() . DIRECTORY_SEPARATOR . str_replace('/', DIRECTORY_SEPARATOR, "barcode/cache/$m->value.png");
	if (!file_exists($barcode)) {
		$ch = curl_init(SERVER_URL . BASE_URL . "barcode/barcode.php?code=$m->value" . (strlen($m->value) == 12 ? "&encoding=UPC" : ""));
		$fp = fopen($barcode, "w");
		curl_setopt($ch, CURLOPT_FILE, $fp);
		curl_setopt($ch, CURLOPT_HEADER, 0);
		curl_exec($ch);
		curl_close($ch);
		fclose($fp);
	}
	return "<a href=\"http://www.google.es/search?q=$m->value\"><img src=\"" . BASE_URL ."barcode/cache/$m->value.png\" alt=\"$m->value\" title=\"$m->value\"></a>";
}
function showAttribute($title, $type_id, $function = 'showItemLink') {
	global $item, $attributes;
	$val = '';
	if(is_array($attributes)) {
		foreach($attributes as $attr) {
			if ($attr->relationship == $type_id) {
				$val .= "<li>" . $function($attr) . "</li>";
			}				
		}
		if ($val != '') {
			echo "<dt>$title</dt><dd><ul>$val</ul></dd>";
		}
	}
}
function showMetadata($title, $type, $function = "showValue") {
	global $item, $metadata;
	$val = '';
	$types = @unserialize($type);
	if(is_array($metadata)) {
		foreach($metadata as $m) {
			if ($types !== false) {
				foreach($types as $t) {
					if ($t == $m->type_id) {
						$val .= "<div>" . $function($m) . "</div>";
					}
				}
			} else if ($type == $m->type_id) {
						$val .= "<div>" . $function($m) . "</div>";
			}
		}
		if ($val != '') {
			echo "<dt>$title</dt><dd>$val</dd>";
		}
	}
}
function showVersions($title, $type_id) {
	global $item, $types;
	$val = '';
	$children = ItemDAO::getVersionsByType($item->id, $type_id);
	if(is_array($children)) {
		foreach($children as $child) {
			$val .= "<li><a href=\"" . getItemLink($types[$child->type_id], $child) . "\">$child->name</a></li>";	
		}
		if ($val != '') {
			echo "<dt>$title</dt><dd><ul>$val</ul></dd>";
		}
	}
}
function showEditions() {
	global $item, $types;
	$editions = ItemDAO::getEditions($item->id);
	if (is_array($editions)) {
		echo '<ul class="references">';
		foreach($editions as $e) {
			$bg = getItemImage($e->id, 64, true);
			$bg = ($bg != '') ? $bg : BASE_URL . 'img/defaultitem.png';
			echo "<li class=\"clearfix\" title=\"$e->name\">";
			echo "<a href=\"" . getItemLink($types[$e->type_id], $e) . "\" style=\"background-image:url($bg)\">";
			echo "<span class=\"name\">$e->name</span>";
			$year = substr($e->date, 0, 4);
			$year = ($year != "0000") ? " &middot; $year" : "";
			echo "<span class=\"data\">$e->regions $year</span>";
			echo "</a></li>";
		}
		echo '<ul>';
	} else {
		echo '<div class="message">No editado</div>';
	}
}
function showItems($items) {
	global $types;
	if ($items && count($items) > 0) {
		echo '<ul class="items clearfix">';
		foreach ($items as $i) {
			echo "<li id=\"item$i->id\"><a href=\"" . getItemLink($types[$i->type_id], $i) . "\" style=\"background-image:url(" . BASE_URL . "getItemImg.php?id=$i->id)\"><span>$i->name</span></a></li>";
		}
		echo '</ul>';
	}
}
function showScore() {
	
}
function getFullCombo($type) {
	$items = ItemDAO::getCombo($type, false);
	return getItemsFullCombo($items, 0);
}
function getItemsFullCombo($items, $parent) {
	$res = [];
	foreach($items as $item) {
		if (intval($item->parent_id) === $parent) {
			if ($parent != 0) {
				$item->name = " ∟ " . $item->name;
			}
			$res[] = $item;
			$res = array_merge($res, getItemsFullCombo($items, intval($item->id)));
		}
	}
	return $res;
}
// Logging system
function saveLog() {
	global $item, $type;
	$log = new stdClass;
	$log->item_id = 0;
	$log->type_id = 0;
	if ($item != NULL) {		
		$log->item_id = $item->id;
	}
	if ($type != NULL) {		
		$log->type_id = $type->id;
	}
	$log->ip = $_SERVER['REMOTE_ADDR'];
	$log->uri = $_SERVER["REQUEST_URI"];
	$log->agent = $_SERVER["HTTP_USER_AGENT"];
	$log->user_id = 0;		
	LogDAO::insertLog($log);
}
?>