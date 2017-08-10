-- phpMyAdmin SQL Dump
-- version 4.6.5.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 10. Aug 2017 um 11:56
-- Server-Version: 10.1.21-MariaDB
-- PHP-Version: 7.0.15

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `test`
--
CREATE DATABASE IF NOT EXISTS `test` DEFAULT CHARACTER SET latin1 COLLATE latin1_swedish_ci;
USE `test`;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `benutzer`
--

CREATE TABLE `benutzer` (
  `ID` int(11) NOT NULL,
  `BenutzerName` varchar(255) NOT NULL,
  `EMail` varchar(255) NOT NULL,
  `Passwort` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `benutzer`
--

INSERT INTO `benutzer` (`ID`, `BenutzerName`, `EMail`, `Passwort`) VALUES
(1, 'Arschi', 'david-heik@web.de', 'testPW'),
(2, 'Arschi', 'david-heik2@web.de', 'testPW'),
(3, 'Arschi', 'david-hei3k@web.de', 'testPW'),
(4, 'Didi', 'dd@web.de', 'jay'),
(5, 'Didi', 'dd@web.de', 'jay'),
(6, 'Didi', 'dd@web.de', 'jay'),
(7, 'Didi', 'dd@web.de', 'jay'),
(8, 'Didi', 'dd@web.de', 'jay'),
(9, 'Didi', 'dd@web.de', 'jay'),
(10, 'Didi', 'dd@web.de', 'jay'),
(11, 'Didi', 'dd@web.de', 'jay'),
(12, 'Didi', 'dd@web.de', 'jay'),
(13, 'Didi', 'dd@web.de', 'jay'),
(14, 'Didi', 'dd@web.de', 'jay'),
(15, 'Didi', 'dd@web.de', 'jay');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `benutzer`
--
ALTER TABLE `benutzer`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `benutzer`
--
ALTER TABLE `benutzer`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
