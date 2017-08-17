<?php
require_once 'config.inc.php';
require_once 'token.inc.php';

$_POST['password'] = "TestPW";
$_POST['email'] = "david-heik@web.de";
$username = "";
$iCountUser = False;
$iID = FALSE;


if (isset($_POST['password']) && isset($_POST['email'])) {
    $email = $_POST['email'];
    $sql = "SELECT * FROM `clipboarderuser` WHERE `EMail` = '" . $email . "' AND `Activatedate` IS NOT NULL";
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
        // User exists, now we can change the password.
        $aPasswordOptions = [
            'cost' => 12,
        ];
        $password = password_hash($_POST['password'], PASSWORD_BCRYPT, $aPasswordOptions);
        $token = getToken(6);

        $sql = "UPDATE `clipboarderuser` SET `Password` = '" . $password . "' WHERE `ID` = '" . $iID . "'";

        if ($dbClipboarder->query($sql)) {
            echo "Password Changed";
            sendMail($email, $username);

        } else {
            echo "Error while changing password";
        }
    } else {
        echo "User does not exitis";
    }
}
function sendMail($email, $username)
{
    $sInhalt = "";
    $sInhalt = $sInhalt . "Hallo " . $username . "," . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Passwort wurde erfolgreich zurueckgesetzt." . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Clipboarder Team" . "\n";

    // $empfaenger = $email;
    $empfaenger = "david-heik@web.de";  // TODO: Bevor entfernt wird, black list einrichten und unsubsribe
    $betreff = "Clipboarder Passwort geaendert";
    $from = "From: Clipboarder <clipboarder@notizbuch.online>";

    mail($empfaenger, $betreff, $sInhalt, $from);
}