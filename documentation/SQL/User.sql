--
-- Tabellenstruktur für Tabelle `clipboarderuser`
--

CREATE TABLE IF NOT EXISTS `clipboarderuser` (
`ID` int(11) NOT NULL,
  `EMail` varchar(255) NOT NULL,
  `Username` text NOT NULL,
  `Password` text NOT NULL,
  `Registerdate` int(11) NOT NULL,
  `Activatedate` int(11),
  `Activatetoken` int(6) NOT NULL,
  `PasswordReset` int(6)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

--
-- Indizes für die Tabelle `clipboarderuser`
--
ALTER TABLE `clipboarderuser`
 ADD PRIMARY KEY (`ID`), ADD UNIQUE KEY `EMail` (`EMail`);

--
-- AUTO_INCREMENT für Tabelle `clipboarderuser`
--
ALTER TABLE `clipboarderuser`
MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=0;