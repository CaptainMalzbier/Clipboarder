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
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

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
public class Clipboarder extends Application implements NativeKeyListener {

	public static Scene scene; // in der Scene werden alle Elemente dargestellt
	public static Properties prop = new Properties(); // Einstellungen / Pfade und Variablen
	public static String configPath;
	public static Stage stage;
	private short hotKeyFlag = 0x00;
	public static boolean MASK_CTRL = false;
	public static boolean MASK_C = false;

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

		// TODO code application logic here
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

	}

	@Override
	public void start(final Stage stage) {
		// stores a reference to the stage.
		this.stage = stage;
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
		stage.setMinWidth(Double.parseDouble(prop.getProperty("width"))); // setze Mindestbreite des Fensters bei
																			// Verkleinerung
		stage.setMinHeight(Double.parseDouble(prop.getProperty("height"))); // setze Mindesthoehe des Fensters bei

		// add key listener
		GlobalScreen.addNativeKeyListener(new Clipboarder());

		// Verkleinerung
		// stage.show(); // zeige Fenster
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
			java.awt.Image image = Toolkit.getDefaultToolkit()
					.getImage(SysTray.class.getResource("assets/images/logo.gif"));
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
				// notificationTimer.cancel();
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
			// notificationTimer.schedule(
			// new TimerTask() {
			// @Override
			// public void run() {
			// javax.swing.SwingUtilities.invokeLater(()
			// -> trayIcon.displayMessage(
			// "hello",
			// "The time is now " + timeFormat.format(new Date()),
			// java.awt.TrayIcon.MessageType.INFO
			// )
			// );
			// }
			// },
			// 5_000,
			// 60_000
			// );
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

	public void nativeKeyPressed(NativeKeyEvent e) {
		System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
			MASK_CTRL = true;
			// Check the mask and do work.
			if (MASK_CTRL && MASK_C) {
				System.out.println("BEIDES");
			}
		} else if (e.getKeyCode() == NativeKeyEvent.VC_C) {
			MASK_C = true;
			if (MASK_CTRL && MASK_C) {
				System.out.println("BEIDES");
			}

		}

		// if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
		// try {
		// GlobalScreen.unregisterNativeHook();
		// } catch (NativeHookException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// }
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
			MASK_CTRL = false;
		} else if (e.getKeyCode() == NativeKeyEvent.VC_C) {
			MASK_C = false;
		}

	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	}

}
