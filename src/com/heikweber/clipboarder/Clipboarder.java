/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.awt.AWTException;

import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author Philipp
 */
public class Clipboarder extends Application implements ActionListener, KeyListener {
    
    public static Scene scene;                                                  // in der Scene werden alle Elemente dargestellt
    public static Properties prop = new Properties();                           // Einstellungen / Pfade und Variablen 
    public static String configPath;
    public static Stage stage;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        configPath = args[0];                                                   // erhalte Pfad zur Konfigurationsdatei aus Startargumenten des Programms
        File propertiesFile = new File(configPath);
//        System.out.println(propertiesFile.getAbsolutePath());
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertiesFile))) {
            prop.load(bis);
        } catch (Exception ex) {
        }
        Application.launch(args);

        // TODO code application logic here
        System.out.println("Hello World!");
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        
        SysTray sysTray = new SysTray();
        try {
            sysTray.createTrayIcon();
        } catch (AWTException ex) {
            Logger.getLogger(Clipboarder.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String UserID = ExecuteSQL.getUserID("david-heik@web.de", "testPW");
        System.out.println("Login: " + UserID);
        ExecuteSQL.executeQuerryAndPrint("SELECT ID, BenutzerName, EMail FROM Benutzer");
        ExecuteSQL.executeQuerry("UPDATE `Benutzer` SET `BenutzerName`= 'Arschi' WHERE `BenutzerName` like '%Arsch%'");
        // ExecuteSQL.executeQuerry("INSERT INTO `Benutzer`(`BenutzerName`, `EMail`, `Passwort`) VALUES ('Didi' , 'dd@web.de' , 'jay')");    

    }
    
    @Override
    public void start(final Stage stage) {
        // stores a reference to the stage.
        this.stage = stage;
        // instructs the javafx system not to exit implicitly when the last application window is shut.
        Platform.setImplicitExit(false);
        // sets up the tray icon (using awt code run on the swing thread).
        javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

        // Funktion zum Starten des Fensters
        scene = new SceneGenerator().createScene(configPath);                   // erhalte die Szene aus createScene() aus der Klasse SceneGenerator
        // fuege Stylesheet zur Gestaltung per CSS zur Szene hinzu
        scene.getStylesheets().add(new File(prop.getProperty("stylePath")).toURI().toString());
        // setze eigenes Icon fuer das Fenster
        stage.getIcons().add(new Image(new File(prop.getProperty("icon")).toURI().toString()));
        stage.setFullScreen(false);
        stage.setScene(scene);                                                  // setze Szene in Fenster ein
        stage.setMaximized(false);                                              // minimiere Fenster
        stage.setMinWidth(Double.parseDouble(prop.getProperty("width")));       // setze Mindestbreite des Fensters bei Verkleinerung
        stage.setMinHeight(Double.parseDouble(prop.getProperty("height")));     // setze Mindesthoehe des Fensters bei Verkleinerung
//        stage.show();                                                           // zeige Fenster
    }
    
    private void addAppToTray() {
        try {
            // ensure awt toolkit is initialized.
            java.awt.Toolkit.getDefaultToolkit();

            // app requires system tray support, just exit if there is no support.
            if (!java.awt.SystemTray.isSupported()) {
                System.out.println("No system tray support, application exiting.");
                Platform.exit();
            }

            // set up a system tray icon.
            java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
            java.awt.Image image = Toolkit.getDefaultToolkit().getImage(
                    SysTray.class.getResource("assets/images/logo.gif"));
            java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

            // if the user double-clicks on the tray icon, show the main app stage.
            trayIcon.addActionListener(event -> Platform.runLater(this::showStage));
            
            
            // if the user selects the default menu item (which includes the app name), 
            // show the main app stage.
            java.awt.MenuItem openItem = new java.awt.MenuItem("hello, world");
            openItem.addActionListener(event -> Platform.runLater(this::showStage));

            // the convention for tray icons seems to be to set the default icon for opening
            // the application stage in a bold font.
            java.awt.Font defaultFont = java.awt.Font.decode(null);
            java.awt.Font boldFont = defaultFont.deriveFont(java.awt.Font.BOLD);
            openItem.setFont(boldFont);

            // to really exit the application, the user must go to the system tray icon
            // and select the exit option, this will shutdown JavaFX and remove the
            // tray icon (removing the tray icon will also shut down AWT).
            java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
            exitItem.addActionListener(event -> {
//                notificationTimer.cancel();
                Platform.exit();
                System.exit(0);
                tray.remove(trayIcon);
            });

            // setup the popup menu for the application.
            final java.awt.PopupMenu popup = new java.awt.PopupMenu();
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            // create a timer which periodically displays a notification message.
//            notificationTimer.schedule(
//                    new TimerTask() {
//                @Override
//                public void run() {
//                    javax.swing.SwingUtilities.invokeLater(()
//                            -> trayIcon.displayMessage(
//                                    "hello",
//                                    "The time is now " + timeFormat.format(new Date()),
//                                    java.awt.TrayIcon.MessageType.INFO
//                            )
//                    );
//                }
//            },
//                    5_000,
//                    60_000
//            );
            // add the application tray icon to the system tray.
            tray.add(trayIcon);
        } catch (java.awt.AWTException e) {
            System.out.println("Unable to init system tray");
            e.printStackTrace();
        }
    }
    
    public void showStage() {
        if (stage != null) {
            stage.show();
            stage.toFront();
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "trayIcon") {
            System.out.println(e.getID());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
        System.out.println(e.getID());
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

