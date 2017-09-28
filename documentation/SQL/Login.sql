--
-- Tabellenstruktur für Tabelle `clipboarderlogin`
--

CREATE TABLE IF NOT EXISTS `clipboarderlogin` (
`ID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Token` text NOT NULL,
  `CreateDate` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

--
-- Indizes für die Tabelle `clipboarderlogin`
--
ALTER TABLE `clipboarderlogin`
 ADD PRIMARY KEY (`ID`);
  
ALTER TABLE `clipboarderlogin`
 ADD FOREIGN KEY (`UserID`) REFERENCES `clipboarderuser`(`ID`);
--
-- AUTO_INCREMENT für Tabelle `clipboarderlogin`
--
ALTER TABLE `clipboarderlogin`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;