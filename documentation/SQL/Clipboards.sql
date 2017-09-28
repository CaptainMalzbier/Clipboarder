--
-- Tabellenstruktur für Tabelle `clipboarderclipboards`
--

CREATE TABLE IF NOT EXISTS `clipboarderclipboards` (
`ID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Content` text NOT NULL,
  `CreateDate` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

--
-- Indizes für die Tabelle `clipboarderclipboards`
--
ALTER TABLE `clipboarderclipboards`
 ADD PRIMARY KEY (`ID`);
 
 ALTER TABLE `clipboarderclipboards`
 ADD FOREIGN KEY (`UserID`) REFERENCES `clipboarderuser`(`ID`);

--
-- AUTO_INCREMENT für Tabelle `clipboarderclipboards`
--
ALTER TABLE `clipboarderclipboards`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;