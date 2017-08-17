<?php

function getUserExists($dbClipboarder, $email)
{
    $iCountUser = False;
    $sql = "SELECT * FROM clipboarderuser WHERE  email = '" . $email . "'";
    if ($result = $dbClipboarder->query($sql)) {
        while ($row = $result->fetch_object()) {
            if (!$iCountUser) {
                $iCountUser = 1;
            } else {
                $iCountUser = $iCountUser + 1;
            }
        }
    }
    return $iCountUser;
}