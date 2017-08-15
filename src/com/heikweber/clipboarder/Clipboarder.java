/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Philipp
 */
public class Clipboarder extends Application {

	public static Scene scene; // in der Scene werden alle Elemente dargestellt
	public static Properties prop = new Properties(); // Einstellungen / Pfade und Variablen
	public static String configPath;
	public static Stage stage;

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		// Clear previous logging configurations.
		LogManager.getLogManager().reset();

		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		// global keyboard listener
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			// System.err.println("There was a problem registering the native hook.");
			// System.err.println(ex.getMessage());
			// System.exit(1);
		}

		configPath = args[0]; // erhalte Pfad zur Konfigurationsdatei aus Startargumenten des Programms
		File propertiesFile = new File(configPath);
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertiesFile))) {
			prop.load(bis);
		} catch (Exception ex) {
		}
		Application.launch(args);

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
	}

	@Override
	public void start(final Stage stage) {

		double width = Double.parseDouble(prop.getProperty("width"));
		double height = Double.parseDouble(prop.getProperty("height"));

		// stores a reference to the stage.
		Clipboarder.stage = stage;
		// instructs the javafx system not to exit implicitly when the last application
		// window is shut.
		Platform.setImplicitExit(false);
		// sets up the tray icon (using awt code run on the swing thread).
		javax.swing.SwingUtilities.invokeLater(this::addAppToTray);

		// Funktion zum Starten des Fensters
		scene = new SceneGenerator().createScene(configPath); // erhalte die Szene aus createScene() aus der Klasse
																// SceneGenerator
		// fuege Stylesheet zur Gestaltung per CSS zur Szene hinzu
		scene.getStylesheets().add(new File(prop.getProperty("stylePath")).toURI().toString());
		// setze eigenes Icon fuer das Fenster
		stage.getIcons().add(new Image(new File(prop.getProperty("icon")).toURI().toString()));
		stage.setFullScreen(false);
		stage.setScene(scene); // setze Szene in Fenster ein
		stage.setMaximized(false); // minimiere Fenster
		stage.setResizable(false);
		if (Boolean.parseBoolean(prop.getProperty("alwaysontop"))) {
			System.out.println("test");
			stage.setAlwaysOnTop(true);
		}
		javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
		stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - width - 20);
		stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - height - 80);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setMinWidth(width); // setze Mindestbreite des Fensters bei
		stage.setMinHeight(height); // setze Mindesthoehe des Fensters bei

		// add key listener
		GlobalScreen.addNativeKeyListener(new KeyboardListener());

		// Verkleinerung
		// stage.show(); // zeige Fenster
	}

	private void addAppToTray() {
		try {
			// ensure awt toolkit is initialized.
			Toolkit.getDefaultToolkit();

			// app requires system tray support, just exit if there is no support.
			if (!java.awt.SystemTray.isSupported()) {
				System.out.println("No system tray support, application exiting.");
				Platform.exit();
			}

			// set up a system tray icon.
			SystemTray tray = SystemTray.getSystemTray();
			java.awt.Image image = Toolkit.getDefaultToolkit().getImage(prop.getProperty("trayicon"));
			TrayIcon trayIcon = new SysTray(image);

			// if the user double-clicks on the tray icon, show the main app stage.
			 trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			// if the user selects the default menu item (which includes the app name),
			// show the main app stage.
			MenuItem openItem = new MenuItem("Clipboarder");
			// openItem.addActionListener(event -> Platform.runLater(this::showStage));

			// the convention for tray icons seems to be to set the default icon for opening
			// the application stage in a bold font.
			Font defaultFont = Font.decode(null);
			Font boldFont = defaultFont.deriveFont(Font.BOLD);
			openItem.setFont(boldFont);

			// to really exit the application, the user must go to the system tray icon
			// and select the exit option, this will shutdown JavaFX and remove the
			// tray icon (removing the tray icon will also shut down AWT).
			MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				// notificationTimer.cancel();
				Platform.exit();
				System.exit(0);
				tray.remove(trayIcon);
			});

			// setup the popup menu for the application.
			final PopupMenu popup = new PopupMenu();
			popup.add(openItem);
			popup.addSeparator();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);
			tray.add(trayIcon);
		} catch (AWTException e) {
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
}
