--
-- Tabellenstruktur f�r Tabelle `clipboarderlogin`
--

CREATE TABLE IF NOT EXISTS `clipboarderlogin` (
`ID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Token` text NOT NULL,
  `CreateDate` int(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

--
-- Indizes f�r die Tabelle `clipboarderlogin`
--
ALTER TABLE `clipboarderlogin`
 ADD PRIMARY KEY (`ID`);
  
ALTER TABLE `clipboarderlogin`
 ADD FOREIGN KEY (`UserID`) REFERENCES `clipboarderuser`(`ID`);
--
-- AUTO_INCREMENT f�r Tabelle `clipboarderlogin`
--
ALTER TABLE `clipboarderlogin`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;