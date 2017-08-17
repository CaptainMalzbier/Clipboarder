<?php
require_once 'config.inc.php';
require_once 'checkUserExists.php';
require_once 'token.inc.php';

$_POST['password'] = "TestPW";
$_POST['username'] = "David";
$_POST['email'] = "david-heik@web.de";

if (isset($_POST['username']) && isset($_POST['password']) && isset($_POST['email'])) {
// check
    $iCountUser = getUserExists($dbClipboarder, $_POST['email']);
    if ($iCountUser == FALSE) {
        // User does not exists, now we can create the User.
        $aPasswordOptions = [
            'cost' => 12,
        ];
        $password = password_hash($_POST['password'], PASSWORD_BCRYPT, $aPasswordOptions);
        $username = $_POST['username'];
        $email = $_POST['email'];
        $token = getToken(6);

        $sql = "INSERT INTO `clipboarderuser`(`EMail`, `Username`, `Password`, `Registerdate`, `Activatedate`, `Activatetoken`) VALUES 
          (
          '" . $email . "', 
          '" . $username . "', 
          '" . $password . "', 
          '" . time() . "',
          NULL,
          '" . $token . "'
          )";

        if ($dbClipboarder->query($sql)) {
            echo "User Create";
            sendMail($email, $username, $token);

        } else {
            echo "Error while creating";
        }

    } else {
        echo "User exitis";
    }

}

function sendMail($email, $username, $token)
{
    $sInhalt = "";
    $sInhalt = $sInhalt . "Hallo " . $username . "," . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "um Deine Registierung abzuschließen aktivier bitte dein Benutzerkonto." . "\n";
    $sInhalt = $sInhalt . "Klicke auf auf den link: https://notizbuch.online/Clipboarder/activate.php?email=" . $email . "&token=" . $token . " oder gib deinen sechsstelligen Aktivierungsschluessel: " . $token . " in der Desktopanwendung ein". "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "\n";
    $sInhalt = $sInhalt . "Dein Clipboarder Team" . "\n";

    // $empfaenger = $email;
    $empfaenger = "david-heik@web.de";  // TODO: Bevor entfernt wird, black list einrichten und unsubsribe
    $betreff = "Ihre Clipboarder Aktivierung";
    $from = "From: Clipboarder <clipboarder@notizbuch.online>";

    mail($empfaenger, $betreff, $sInhalt, $from);
}