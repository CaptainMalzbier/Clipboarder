<?php

function tokenUsed($dbClipboarder, $iID, $token)
{
    $Zeitpunkt = time() - (60*60*24*3);
    $sql = "DELETE FROM `clipboarderlogin` WHERE `CreateDate` < $Zeitpunkt";
    if ($dbClipboarder->query($sql)) {
        // ("Successfully deleted not used token");
    } else {
        die("Error while deleting old tokens");
    }
    $sql = "UPDATE `clipboarderlogin` SET `CreateDate`= '".time()."' WHERE `UserID` = '$iID' AND `Token` = '$token'";
    if ($dbClipboarder->query($sql)) {
        // ("Successfully updated");
    } else {
        die("Error while updating");
    }
}