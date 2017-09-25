<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

$username = "";
$sDbToken = "";
$iCountUser = False;
$iID = FALSE;


if (isset($_POST['password']) && isset($_POST['email']) && isset($_POST['token'])) {
    $token = $_POST['token'];
    $email = $_POST['email'];
    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '" . $email . "' AND `Activatedate` IS NOT NULL";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            if (!$iCountUser) {
                $iCountUser = 1;
                $iID = ($row->ID);
                $username = ($row->Username);
                $sDbToken = ($row->PasswordReset);
            } else {
                die("Error");
            }
        }
    }
    if ($iID) {
        // User exists, now we can change the password.
        $aPasswordOptions = [
            'cost' => 12,
        ];
        $password = password_hash($_POST['password'], PASSWORD_BCRYPT, $aPasswordOptions);
        if ($token == $sDbToken) {
            $sql = "UPDATE `clipboarderuser` SET `Password` = '" . $password . "' ,`PasswordReset`= '' WHERE `ID` = '" . $iID . "'";

            if ($dbClipboarder->query($sql)) {
                echo "Password changed";
                sendMail($email, $username);

            } else {
                echo "Error while changing password";
            }
            // todo ausgabe
        } else {
            echo "User does not exists";
        }
    }
}else{
    echo "Missing parameters";
}

function sendMail($email, $username)
{
    $sInhalt = "";
    $sInhalt = $sInhalt . "Hallo " . $username . "," . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Passwort wurde erfolgreich zurueckgesetzt." . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Clipboarder-Team" . "\n";

    // $empfaenger = $email;
    $empfaenger = $email;
    $betreff = "Clipboarder-Passwort geaendert";
    $from = "From: Clipboarder <clipboarder@notizbuch.online>";

    mail($empfaenger, $betreff, $sInhalt, $from);
}