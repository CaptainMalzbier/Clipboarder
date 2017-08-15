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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Philipp
 */
public class SceneGenerator {

	public Properties prop = new Properties(); // Eigenschaften der Konfigurationsdatei
	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
	public File propertiesFile; // Datei mit den Einstellungen
	public List<CopyEntry> copyEntryList = new ArrayList<CopyEntry>();

	public Scene createScene(String configPath) {
		// Klasse zum Erzeugen der Szene
		propertiesFile = new File(configPath); // lade Konfigurationsdatei
		// lese Konfigurationsdatei aus und speichere in Eigenschaften-Objekt
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertiesFile))) {
			prop.load(bis);
		} catch (Exception ex) {
		}

		// System.out.println(configPath);
		layout.getChildren().clear();


		TabPane tabPane = new TabPane();

		BorderPane borderPane = new BorderPane();

		Tab tabSettings = new Tab();
		Tab tabHome = new Tab();
		VBox vBoxHome = showHomeMenu();
		VBox vBoxSettings = showSettingsMenu();
		tabHome.setText("Home");
		tabHome.setClosable(false);
		tabSettings.setText("Settings");
		tabSettings.setClosable(false);
//		vBoxHome.getChildren().add(new Label("Tab" + i));
		vBoxHome.setAlignment(Pos.BASELINE_RIGHT);
		vBoxSettings.getChildren().add(new Label("Settings"));
		vBoxSettings.setAlignment(Pos.CENTER);
		tabHome.setContent(vBoxHome);
		tabSettings.setContent(vBoxSettings);
		tabPane.getTabs().add(tabHome);
		tabPane.getTabs().add(tabSettings);
		
		// // bind to take available space
		// borderPane.prefHeightProperty().bind(scene.heightProperty());
		// borderPane.prefWidthProperty().bind(scene.widthProperty());

		borderPane.setCenter(tabPane);

//		layout.getChildren().add(vBox);
		layout.getChildren().add(borderPane);

		System.out.println(propertiesFile.getAbsolutePath());

		return new Scene(layout, Double.parseDouble(prop.getProperty("width")),
				Double.parseDouble(prop.getProperty("height")));
	}

	public VBox showHomeMenu() {

		VBox menu = new VBox();

		for (int i = 0; i < 9; i++) {
			CopyEntry ce = new CopyEntry("Test #" + (i+1));
			ce.setId(i);
			ce.setContent("Test - " + (i+1));
			copyEntryList.add(ce);
		}

		for (CopyEntry ce : copyEntryList) {

			Button b = new Button(ce.getName());

			b.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
						System.out.println(ce.getContent());
				}
			});

			menu.getChildren().add(b);
		}

		return menu;
	}

	public VBox showSettingsMenu() {
		Button bExit = new Button("Exit");
		bExit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});
		return new VBox(bExit);
	}
}
