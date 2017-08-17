<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

$_POST['password'] = "TestPW";
$_POST['email'] = "david-heik@web.de";
$_POST['remindme'] = 1;
$_POST['token'] = "66";
$_POST['usetoken'] = TRUE;
$iCountUser = FALSE;
$iCountToken = FALSE;
$iID = FALSE;
$bRemindMe = FALSE;
$bUseToken = FALSE;
if (isset($_POST['remindme'])) {
    if ($_POST['remindme'] == "1") {
        $bRemindMe = TRUE;
    }
}
if (isset($_POST['usetoken'])) {
    if ($_POST['usetoken'] == "1") {
        $bUseToken = TRUE;
    }
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
                    die("token correct");
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
                    if ($bRemindMe) {
                        // create new token and print out
                        $token = getTokenWithLetters(16);
                        $sql = "INSERT INTO `clipboarderlogin`(`UserID`, `Token`, `CreateDate`) VALUES ('" . $iID . "', '" . $token . "', '" . time() . "')";
                        if ($dbClipboarder->query($sql)) {
                            die($token);
                        } else {
                            die("Error while creating token");
                        }
                    } else {
                        die('Correct password');
                    }
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

