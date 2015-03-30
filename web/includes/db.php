<?php
function db_connect() {
	global $db, $db_name, $db_host, $db_user, $db_pass;
	$db = mysqli_connect($db_host, $db_user, $db_pass, $db_name) or die('No se pudo conectar a la base de datos: ' . mysqli_error());
	$db->set_charset("utf8");
}
function db_disconnect() {
	global $db;
	if ($db) {
		mysqli_close($db);
	}
}
function db_query($query) {
	global $db, $db_queries;
	if (!$db) {
		db_connect();
	}
	$db_queries[] = $query;
	return $db->query($query);
}
class ItemDAO {
	private static function fillObject($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->type_id = $row["type_id"];
		$item->parent_id = $row["parent_id"];
		$item->name = $row["name"];
		$item->altname = $row["altname"];
		$item->shortname = $row["shortname"];
		$item->date = $row["date"];
		$item->modified = $row["modified"];
		$item->content = $row["content"];
		$item->excerpt = $row["excerpt"];
		$item->status = $row["status"];
		return $item;
	}

	private static function fillAttribute($row) {
		$item = ItemDAO::fillObject($row);
		$item->relationship = $row["relationship"];
		return $item;
	}

	private static function fillResult($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->name = $row["name"];
		$item->date = $row["date"];
		$item->platforms = $row["platforms"];
		$item->genres = $row["genres"];
		$item->regions = $row["regions"];
		return $item;
	}

	private static function fillVersion($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->type_id = $row["type_id"];
		$item->name = $row["name"];
		$item->shortname = $row["shortname"];
		return $item;
	}

	private static function fillEdition($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->type_id = $row["type_id"];
		$item->name = $row["name"];
		$item->shortname = $row["shortname"];
		$item->date = $row["date"];
		$item->regions = $row["regions"];
		return $item;
	}

	public static function getByShortname($shortname) {			
		$result = db_query("SELECT * FROM items WHERE shortname = '$shortname'");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = ItemDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getRandom($type_id) {
		$result = db_query("SELECT * FROM items WHERE type_id = $type_id AND status = 'publicado' ORDER BY RAND() LIMIT 0,1");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = ItemDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getAttributes($id) {
		$result = db_query("SELECT DISTINCT i.*, r.type_id as relationship FROM items i, relationships r WHERE r.parent_id = $id AND r.child_id = i.id ORDER BY i.name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillAttribute($row);
		}
		$result->free();
		return $items;
	}

	public static function getChildren($id) {
		$result = db_query("SELECT DISTINCT * FROM items WHERE parent_id = $id ORDER BY name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getPopularItemsByType($id) {
		$result = db_query("SELECT count(*), i.* FROM items i, logs l WHERE l.item_id = i.id AND i.type_id = $id GROUP BY l.item_id ORDER BY 1 DESC LIMIT 0, 10");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getItemById($id) {
		$result = db_query("SELECT * FROM items WHERE id = $id AND status = 'publicado'");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = ItemDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getGames($id) {
		$result = db_query("SELECT DISTINCT i.* FROM items i, relationships r WHERE ((r.child_id = $id AND r.parent_id = i.id) OR (r.parent_id = $id AND r.child_id = i.id)) AND i.type_id = " . TYPE_GAME . " ORDER BY i.type_id, i.name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getBytype($id) {	
		$result = db_query("SELECT * FROM items i WHERE type_id = $id AND status = 'publicado' ORDER BY name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getCombo($type, $onlyParents) {	
		$result = db_query("SELECT * FROM items i WHERE type_id = $type AND status = 'publicado'" . ($onlyParents ? " AND parent_id = 0" : "") . " ORDER BY name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getCompaniesCombo($type, $subtype) {	
		$result = db_query("SELECT * FROM items i WHERE type_id = $type AND status = 'publicado' AND id IN (SELECT DISTINCT child_id FROM relationships WHERE type_id = $subtype) ORDER BY name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}

	public static function getGameResults($criteria) {
		$query = "SELECT i.parent_id as id, i.name as name, min(i.date) as date";
		$query .= ",(select GROUP_CONCAT(ip.name SEPARATOR ',') from relationships rip, items ip where ip.id = rip.child_id and rip.parent_id = i.parent_id and rip.type_id = " . TYPE_PLATFORM . ") as platforms";
		$query .= ",(select GROUP_CONCAT(ig.name SEPARATOR ',') from relationships rig, items ig where ig.id = rig.child_id and rig.parent_id = i.parent_id and rig.type_id = " . TYPE_GENRE . ") as genres";
		$query .= ",(select GROUP_CONCAT(ir.name SEPARATOR ',') from relationships rir, items ir where ir.id = rir.child_id and rir.parent_id = i.parent_id and rir.type_id = " . TYPE_REGION . ") as regions";
		$query .= " FROM items i";
		if ($criteria->platform != '') {
			$query .= ", relationships rs";
		}
		if ($criteria->region != '') {
			$query .= ", relationships rr";
		}
		if ($criteria->genre != '') {
			$query .= ", relationships rg";
		}
		if ($criteria->developer != '') {
			$query .= ", relationships rd";
		}
		if ($criteria->publisher != '') {
			$query .= ", relationships rp";
		}
		if ($criteria->tag != '') {
			$query .= ", item_tags it";
		}
		$query .= " WHERE i.status = 'publicado' AND i.type_id = " . TYPE_VERSION . " AND year(i.date) >= $criteria->year_from AND year(i.date) <= $criteria->year_to ";
		if ($criteria->code != '') {
			$query .= "AND i.id IN (SELECT DISTINCT e.parent_id FROM items e, metadata m WHERE m.item_id = e.id AND upper(m.value) LIKE '%" . strtoupper($criteria->code) . "%') ";
		}
		if ($criteria->s != '') {
			$query .= "AND (upper(i.name) like '%" . strtoupper($criteria->s) . "%' OR upper(i.altname) like '%" . strtoupper($criteria->s) . "%') ";
		}
		if ($criteria->platform != '') {
			$query .= "AND rs.type_id = " . TYPE_PLATFORM . " AND rs.child_id = $criteria->platform AND rs.parent_id = i.id ";
		}
		if ($criteria->region != '') {
			$query .= "AND rr.type_id = " . TYPE_REGION . " AND (rr.child_id = $criteria->region OR rr.child_id IN (SELECT id FROM items WHERE type_id = " . TYPE_REGION . " AND parent_id = $criteria->region)) AND rr.parent_id = i.id ";
		}
		if ($criteria->genre != '') {
			$query .= "AND rg.type_id = " . TYPE_GENRE . " AND (rg.child_id = $criteria->genre OR rg.child_id IN (SELECT id FROM items WHERE type_id = " . TYPE_GENRE . " AND parent_id = $criteria->genre)) AND rg.parent_id = i.id ";
		}
		if ($criteria->developer != '') {
			$query .= "AND rd.type_id = " . TYPE_DEVELOPED . " AND rd.child_id = $criteria->developer AND rd.parent_id = i.id ";
		}
		if ($criteria->publisher != '') {
			$query .= "AND rp.type_id = " . TYPE_PUBLISHED . " AND rp.child_id = $criteria->publisher AND rp.parent_id = i.id ";
		}
		if ($criteria->tag != '') {
			$query .= "AND it.tag_id = $criteria->tag AND it.item_id = i.id ";
		}
		$query .= "GROUP BY 1,2 ORDER BY i.name";
		$result = db_query($query);
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillResult($row);
		}
		$result->free();
		return $items;
	}

	public static function getVersionsByType($id, $type_id) {	
		global $item;
		$query = "SELECT i.id as id, i.shortname AS shortname, i2.name AS name, i.type_id AS type_id FROM items i, items i2, relationships r WHERE i.parent_id = $id AND r.parent_id = i.id AND r.child_id = i2.id AND r.type_id = $type_id ORDER BY 2";
		$result = db_query($query);
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillVersion($row);
		}
		$result->free();
		return $items;
	}

	public static function getEditions($id) {	
		global $item;
		$query = "SELECT i.id AS id, i.shortname AS shortname, i.name AS name, i.date AS date, i.type_id AS type_id, (select GROUP_CONCAT(ip.name SEPARATOR ', ') from relationships rip, items ip where ip.id = rip.child_id and rip.parent_id = i.id and rip.type_id = " . TYPE_REGION . ") as regions FROM items i WHERE i.parent_id = $id AND i.status = 'publicado' ORDER BY i.date";
		$result = db_query($query);
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemDAO::fillEdition($row);
		}
		$result->free();
		return $items;
	}
}
class TypeDAO {
	private static function fillObject($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->parent_id = $row["parent_id"];
		$item->name = $row["name"];
		$item->shortname = $row["shortname"];
		$item->modified = $row["modified"];
		$item->content = $row["content"];
		$item->excerpt = $row["excerpt"];
		$item->status = $row["status"];
		$item->template = $row["template"];
		return $item;
	}

	public static function getById($id) {	
		$result = db_query("SELECT * FROM types WHERE id = $id");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = TypeDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getByShortname($shortname) {
		$result = db_query("SELECT * FROM types WHERE shortname = '$shortname'");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = TypeDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getRandom() {	
		$result = db_query("SELECT * FROM types ORDER BY RAND() LIMIT 0,1");
		$item = NULL;
		if ($result->num_rows > 0) {
			$item = TypeDAO::fillObject($result->fetch_array(MYSQLI_ASSOC));
		}
		$result->free();
		return $item;
	}

	public static function getTypes() {	
		$result = db_query("SELECT * FROM types WHERE status = 'publicado'");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[$row["id"]] = TypeDAO::fillObject($row);
			$items[$row["shortname"]] = TypeDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}
}
class ItemTagDAO {
	private static function fillObject($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->name = $row["name"];
		$item->shortname = $row["shortname"];
		return $item;
	}

	public static function getItemTags($id) {	
		$result = db_query("SELECT t.* FROM tags t, item_tags i WHERE i.item_id = $id AND i.tag_id = t.id");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = ItemTagDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}
}
class TagDAO {
	private static function fillObject($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->name = $row["name"];
		$item->shortname = $row["shortname"];
		$item->group = $row["grp"];
		return $item;
	}

	public static function getTags() {
		$result = db_query("SELECT tg.*, t.name AS grp FROM tags tg, types t WHERE tg.id IN (SELECT DISTINCT tag_id FROM item_tags) AND t.id = tg.type_id ORDER BY t.name, tg.name");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = TagDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}
}
class MetadataDAO {
	private static function fillObject($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->item_id = $row["item_id"];
		$item->type_id = $row["type_id"];
		$item->name = $row["name"];
		$item->value = $row["value"];
		return $item;
	}

	public static function getMetadata($item_id) {	
		$result = db_query("SELECT * FROM metadata WHERE item_id = $item_id");
		$items = NULL;
		while ($row = $result->fetch_array(MYSQLI_ASSOC)) {
			$items[] = MetadataDAO::fillObject($row);
		}
		$result->free();
		return $items;
	}
}
class LogDAO {
	private static function fillLog($row) {
		$item = new stdClass;
		$item->id = $row["id"];
		$item->item_id = $row["item_id"];
		$item->type_id = $row["type_id"];
		$item->uri = $row["uri"];
		$item->date = $row["date"];
		$item->ip = $row["ip"];
		$item->user_id = $row["user_id"];
		return $item;
	}

	public static function insertLog($log) {
		db_query("INSERT INTO logs (item_id, type_id, uri, ip, agent, user_id) VALUES ($log->item_id, $log->type_id, '$log->uri', '$log->ip', '$log->agent', $log->user_id)");
	}
}
?>