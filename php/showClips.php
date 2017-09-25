<?php
require_once 'config.inc.php';
require_once 'token.inc.php';
require_once 'tokenUsed.php';

//$_POST['email'] = "david-heik@web.de";
//$_POST['password'] = "TestPW";
//$_POST['token'] = "66";
//$_POST['usetoken'] = TRUE;
//$_POST['offset'] = 0;
//$_POST['number'] = 10;
$iCountUser = FALSE;
$iCountToken = FALSE;
$iID = FALSE;
$bUseToken = FALSE;
$iOffset = 0;
$iNumberOfElements = 10;
if (isset($_POST['usetoken'])) {
    if ($_POST['usetoken'] == "1") {
        $bUseToken = TRUE;
    }
}
if (isset($_POST['offset'])) {
    $iOffset = $_POST['offset'];
}
if (isset($_POST['number'])) {
    $iNumberOfElements = $_POST['number'];
}
if (isset($_POST['email'])) {
    $email = $_POST['email'];
    if ($bUseToken) {
        // login with token
        if (isset($_POST['token'])) {
            $token = $_POST['token'];
            $iID = getUserIdWhenExitstAndActive($dbClipboarder, $email);
            if ($iID) {
                $sql = "SELECT * FROM `clipboarderlogin` WHERE `UserID` = '" . $iID . "' and  `Token` = '" . $token . "'";
                if ($result = $dbClipboarder->query($sql)) {
                    while ($row = $result->fetch_object()) {
                        if (!$iCountToken) {
                            $iCountToken = 1;
                            $sDbToken = ($row->Token);
                        } else {
                            createErrorJson("Token not set", "");
                            die("Error");
                        }
                    }
                }
                if ($sDbToken == $token) {
                    //  token correct
                    tokenUsed($dbClipboarder, $iID, $token);
                    //  load clips from User
                    loadClipsFromDatabase($dbClipboarder, $iID, $iOffset, $iNumberOfElements);
                } else {
                    createErrorJson("Token incorrect", "");
                }
            } else {
                createErrorJson("User does not exist or is not activated", "");
            }
        } else {
            createErrorJson("Token not set", "");
        }
    } else {
        //login with email and password
        if (isset($_POST['password'])) {
            $iID = getUserIdWhenExitstAndActive($dbClipboarder, $email);
            if ($iID) {
                $passwordFromDB = getUserPassword($dbClipboarder, $email);
                $password = $_POST['password'];
                if (password_verify($password, $passwordFromDB)) {
                    //  password correct
                    //  load clips from User
                    loadClipsFromDatabase($dbClipboarder, $iID, $iOffset, $iNumberOfElements);
                } else {
                    createErrorJson("Wrong password", "");
                }
            } else {
                createErrorJson("User does not exist or is not activated", "");
            }
        } else {
            createErrorJson("Missing parameter: password", "");
        }
    }
} else {
    createErrorJson("Missing parameter: email", "");
}

function getUserIdWhenExitstAndActive($dbClipboarder, $email)
{
    $iCountUser = FALSE;
    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '" . $email . "' AND `Activatedate` IS NOT NULL";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            if (!$iCountUser) {
                $iCountUser = 1;
                $iID = ($row->ID);
            } else {
                // found more than one record
                createErrorJson("Found more than one record", "");
            }
        }
    }
    if ($iID) {
        return $iID;
    } else {
        createErrorJson("User does not exist or is not activated", "");
    }
}

function getUserPassword($dbClipboarder, $email)
{
    $iCountUser = FALSE;
    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '" . $email . "' AND `Activatedate` IS NOT NULL";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            if (!$iCountUser) {
                $iCountUser = 1;
                $sPassword = ($row->Password);
            } else {
                // found more than one record
                createErrorJson("Found more than one record", "");
            }
        }
    }
    if ($sPassword) {
        return $sPassword;
    } else {
        createErrorJson("User does not exist or is not activated", "");
    }
}

function loadClipsFromDatabase($dbClipboarder, $iUserID, $iOffset, $iNumberOfElements)
{
    $sql = "SELECT * FROM `clipboarderclipboards` WHERE `UserID` = '" . $iUserID . "' ORDER BY `clipboarderclipboards`.`ID` DESC Limit " . $iOffset . "," . $iNumberOfElements;
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            $aDbRowData[] = [
                "ID" => $row->ID,
                "UserID" => $row->UserID,
                "Content" => ($row->Content),
                "CreateDate" => $row->CreateDate,
            ];
        }
        if (isset($aDbRowData)) {
            $aMessage = [
                "status" => "ok",
                "message" => "",
                "count" => countClipsFromDatabase($dbClipboarder, $iUserID),
                "data" => $aDbRowData
            ];
            echo json_encode($aMessage);
            die();
        } else {
            $aDbRowData[] = [
                "ID" => "0",
                "UserID" => $iUserID,
                "Content" => "Example clip",
                "CreateDate" => time(),
            ];
            $aMessage = [
                "status" => "ok",
                "message" => "No Clipboards found",
                "count" => "1",
                "data" => $aDbRowData
            ];
            echo json_encode($aMessage);
            die();
            // createErrorJson("No Clipboards found", "");
        }
    } else {
        createErrorJson("Unable to execute query", "");
    }
}

function countClipsFromDatabase($dbClipboarder, $iUserID)
{
    $iCount = 0;
    $sql = "SELECT * FROM `clipboarderclipboards` WHERE `UserID` = '" . $iUserID . "' ORDER BY `clipboarderclipboards`.`ID` DESC";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            $iCount++;
        }
    }
    return $iCount;
}

function createErrorJson($message, $data)
{
    $aMessage = [
        "status" => "error",
        "message" => $message,
        "data" => $data
    ];
    die(json_encode($aMessage));
}