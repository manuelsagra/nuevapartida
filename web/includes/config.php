<?php
// Database
$db_name = "nuevapartida";
$db_host = "localhost";
$db_user = "root";
$db_pass = "";
$db = NULL;
$db_queries = NULL;

// Installation
define("SERVER_URL", "http://localhost");
define("BASE_URL", "/nuevapartida/");

define("TYPE_GAME", 2);
define("TYPE_VERSION", 3);

define("TYPE_PLATFORM", 5);
define("TYPE_REGION", 9);
define("TYPE_GENRE", 10);

define("TYPE_COMPANY", 6);
define("TYPE_PUBLISHED", 14);
define("TYPE_DEVELOPED", 15);

define("TYPES_URL", serialize([23, 24, 25, 26]));

define("TYPE_CATALOG", 40);
define("TYPE_BARCODE", 41);
?>