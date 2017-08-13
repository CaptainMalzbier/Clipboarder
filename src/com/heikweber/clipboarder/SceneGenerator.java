/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Philipp
 */
public class SceneGenerator {

    public Properties prop = new Properties();                                  // Eigenschaften der Konfigurationsdatei
    public static StackPane layout = new StackPane();                           // Layout-Pane auf dem alles dargestellt wird
    public File propertiesFile;							// Datei mit den Einstellungen 
    public List<CopyEntry> copyEntryList = new ArrayList<CopyEntry>();

    public Scene createScene(String configPath) {
        //Klasse zum Erzeugen der Szene
        propertiesFile = new File(configPath);                                  // lade Konfigurationsdatei
        // lese Konfigurationsdatei aus und speichere in Eigenschaften-Objekt
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertiesFile))) {
            prop.load(bis);
        } catch (Exception ex) {
        }

//        System.out.println(configPath);
        layout.getChildren().clear();

        VBox vBox = showMenu();

        layout.getChildren().add(vBox);
        
        System.out.println(propertiesFile.getAbsolutePath());

        return new Scene(layout, Double.parseDouble(prop.getProperty("width")), Double.parseDouble(prop.getProperty("height")));
    }

    public VBox showMenu() {

        VBox menu = new VBox();

        for (int i = 0; i < 10; i++) {
            copyEntryList.add(new CopyEntry("Test #" + i));
        }

        for (CopyEntry ce : copyEntryList) {
            Button b = new Button(ce.getName());
            menu.getChildren().add(b);
        }

        return menu;
    }
}
