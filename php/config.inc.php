<?php

require_once('getClipboarderConstant.inc.php');

// Create connection
$dbClipboarder = new mysqli($sMySQLserver, $sMySQlBenutzer, $sMySQLPasswort, $sMySQLDBName);
// Check connection
if ($dbClipboarder->connect_error) {
    die("Connection failed");
}

