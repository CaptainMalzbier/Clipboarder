/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clipboarder;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.awt.SystemTray;

/**
 *
 * @author Philipp
 */
public class Clipboarder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello World!");
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        
        String UserID =  ExecuteSQL.getUserID("david-heik@web.de", "testPW");
        System.out.println("Login: " + UserID);
        ExecuteSQL.executeQuerryAndPrint("SELECT ID, BenutzerName, EMail FROM Benutzer");
        ExecuteSQL.executeQuerry("UPDATE `Benutzer` SET `BenutzerName`= 'Arschi' WHERE `BenutzerName` like '%Arsch%'");
       // ExecuteSQL.executeQuerry("INSERT INTO `Benutzer`(`BenutzerName`, `EMail`, `Passwort`) VALUES ('Didi' , 'dd@web.de' , 'jay')");    
        
        
    }

}
