<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

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
                $sql = "SELECT * FROM `clipboarderlogin` WHERE `UserID` = '" . $iID . "'";
                if ($result = $dbClipboarder->query($sql)) {
                    while ($row = $result->fetch_object()) {
                        if (!$iCountToken) {
                            $iCountToken = 1;
                            $sDbToken = ($row->Token);
                        } else {
                            die("Error");
                        }
                    }
                }
                if ($sDbToken == $token) {
                    //  token correct
                    //  load clips from User
                    loadClipsFromDatabase($dbClipboarder, $iID, $iOffset, $iNumberOfElements);

                } else {
                    die("token incorrect");
                }
            } else {
                die("User does not exists or is not activated");
            }
        } else {
            die('Token not set');
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
                    die('Worng password.');
                }
            } else {
                die("User does not exists or is not activated");
            }
        } else {
            die("Missing parameter password");
        }
    }
} else {
    die("Missing parameter email");
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
                die("Error");
            }
        }
    }
    if ($iID) {
        return $iID;
    } else {
        die("User does not exists or is not activated");
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
                die("Error");
            }
        }
    }
    if ($sPassword) {
        return $sPassword;
    } else {
        die("User does not exists or is not activated");
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
                "Content" =>  ($row->Content),
                "CreateDate" => $row->CreateDate,
            ];
        }
    }
    echo json_encode($aDbRowData);
}