<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

$username = "";
$iCountUser = False;
$iID = FALSE;
// $_POST['email'] = "david-heik@web.de";
$token = getToken(6);

if (isset($_POST['email'])) {
    $email = $_POST['email'];
    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '" . $email . "'";
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
    if ($iID) {
        $sql = "UPDATE `clipboarderuser` SET `PasswordReset`= '" . $token . "' WHERE `ID` = '" . $iID . "'";
        if ($dbClipboarder->query($sql)) {
            echo "Successfully request, check your mails to change ";
            sendMail($email, $username, $token);
        } else {
            echo "Error while password ";
        }
    } else {
        die("EMail not found");
    }
} else {
    die("User not found");
}

function sendMail($email, $username, $token)
{
    $sInhalt = "";
    $sInhalt = $sInhalt . "Hallo " . $username . "," . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "um Ihr Passwort zurueckzusetzten geben Sie den Token: " . $token . " in die Desktopanwendung ein und vergeben Sie ein neues sicheres Passwort." . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Clipboarder Team" . "\n";

    // $empfaenger = $email;
    $empfaenger = $email;
    $betreff = "Clipboarder Passwort vergessen";
    $from = "From: Clipboarder <clipboarder@notizbuch.online>";

    mail($empfaenger, $betreff, $sInhalt, $from);
}