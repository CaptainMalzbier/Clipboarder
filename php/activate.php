<?php
require_once 'config.inc.php';
if (isset($_GET['email']) && isset($_GET['token'])){
    $email = $_GET['email'];
    $token = $_GET['token'];
    $username = "";
    $iCountUser = False;
    $iID = FALSE;

    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '". $email . "' AND `Activatetoken` = '". $token . "' AND `Activatedate` IS NULL";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            if (!$iCountUser) {
                $iCountUser = 1;
                $iID = ($row->ID);
                $username = ($row->Username);
            } else {
                die("Error");
            }
        }
    }
    if($iID){
        $sql = "UPDATE `clipboarderuser` SET `Activatedate`= ". time() . " WHERE `ID` = '". $iID . "'";
        if ($dbClipboarder->query($sql)) {
            echo "Successfully activated";
            sendMail($email, $username);
        } else {
            echo "Error while activating";
        }
    }else{
        $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '". $email . "' AND `Activatetoken` = '". $token . "'";
        if ($result = $dbClipboarder->query($sql)) {
            while ($row = $result->fetch_object()) {
                if (!$iCountUser) {
                    $iCountUser = 1;
                    $iID = ($row->ID);
                    $username = ($row->Username);
                } else {
                    die("Error");
                }
            }
        }
        if($iID) {
            die("Your account is already activated");
        }
        $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '". $email . "'";
        if ($result = $dbClipboarder->query($sql)) {
            while ($row = $result->fetch_object()) {
                if (!$iCountUser) {
                    $iCountUser = 1;
                    $iID = ($row->ID);
                    $username = ($row->Username);
                } else {
                    die("Error");
                }
            }
        }
        if($iID) {
            die("Wrong token");
        }else{
            die("EMail not found");
        }
    }
}
else{
    echo "Missing parameters";
}

function sendMail($email, $username)
{
    $sInhalt = "";
    $sInhalt = $sInhalt . "Hallo " . $username . "," . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "deine Registierung ist Abgeschlossen." . "\n";
    $sInhalt = $sInhalt . "Du kannst jetzt kostenfrei Clipboarder nutzen, viel Freude dabei!" . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Clipboarder Team" . "\n";

    // $empfaenger = $email;
    $empfaenger = $email;
    $betreff = "Ihre Clipboarder Benutzerkonto wurde freigeschaltet";
    $from = "From: Clipboarder <clipboarder@notizbuch.online>";

    mail($empfaenger, $betreff, $sInhalt, $from);
}