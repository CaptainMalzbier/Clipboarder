<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

//$_POST['email'] = "david-heik@web.de";
//$_POST['password'] = "TestPW";
//$_POST['token'] = "66";
//$_POST['usetoken'] = TRUE;
//$_POST['clipboard'] = "TestEintrag";
$iCountUser = FALSE;
$iCountToken = FALSE;
$iID = FALSE;
$bUseToken = FALSE;
if (isset($_POST['usetoken'])) {
    if ($_POST['usetoken'] == "1") {
        $bUseToken = TRUE;
    }
}

if (isset($_POST['email']) && isset($_POST['clipboard'])) {
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
                    //  insert clip
                    insertClipIntoDatabase($dbClipboarder, $iID, $_POST['clipboard']);
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
                    //  insert clip
                    insertClipIntoDatabase($dbClipboarder, $iID, $_POST['clipboard']);
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

function insertClipIntoDatabase($dbClipboarder, $iUserID, $sClipContent)
{
    $sql = "INSERT INTO `clipboarderclipboards`(`UserID`, `Content`, `CreateDate`) VALUES 
          (
          '" . $iUserID . "', 
          '" . $sClipContent . "', 
          '" . time() . "',
          )";
    if ($dbClipboarder->query($sql)) {
        die ("Successfully activated");
    } else {
        die("Error while creating");
    }
}