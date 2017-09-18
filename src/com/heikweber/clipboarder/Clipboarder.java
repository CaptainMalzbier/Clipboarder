package com.heikweber.clipboarder;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import com.sun.javafx.application.PlatformImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Philipp, David
 */

public class Clipboarder extends Application {

	private SceneModel model;
	private Stage stage;
	private Configuration config;

	/**
	 * @param args
	 *            the command line arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		PlatformImpl.setTaskbarApplication(false);
		// Clear previous logging configurations.
		LogManager.getLogManager().reset();
		// Get the logger for "org.jnativehook" and set the level to off.
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			System.exit(1);
		}

		// global keyboard listener
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		if (args.length < 1) {
			System.out.println("Usage: java Clipboarder <path-to-config-file>");
			System.exit(1);
		}

		Application.launch(args);

	}

	@Override
	public void start(final Stage stage) throws IllegalStateException, Exception {
		String configPath = getParameters().getRaw().get(0); // erhalte Pfad zur Konfigurationsdatei aus Startargumenten
																// des Programms
		try {
			config = new Configuration(configPath);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Failed to read configuration file: " + configPath);
			System.exit(1);
		}

		this.stage = stage;
		// instructs the javafx system not to exit implicitly when the last application
		// window is shut.
		Platform.setImplicitExit(false);
		// Funktion zum Starten des Fensters
		model = new SceneModel(stage, config);
		// setze eigenes Icon fuer das Fenster
		stage.getIcons().add(new Image(new File(config.get("icon")).toURI().toString()));

		System.out.println(new File(config.get("icon")).toURI().toString());
		System.out.println(new File(config.get("stylePath")).toURI().toString());

		stage.setFullScreen(false);
		stage.setScene(model.getScene()); // setze Szene in Fenster ein
		stage.setMaximized(false); // minimiere Fenster
		stage.setResizable(false);
		if (Boolean.parseBoolean(config.get("alwaysontop"))) {
			stage.setAlwaysOnTop(true);
		}

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
		stage.setX(primaryScreenBounds.getMinX() + primaryScreenBounds.getWidth() - config.getWidth() - 20);
		stage.setY(primaryScreenBounds.getMinY() + primaryScreenBounds.getHeight() - config.getHeight() - 80);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setMinWidth(config.getWidth()); // setze Mindestbreite des Fensters bei
		stage.setMinHeight(config.getHeight()); // setze Mindesthoehe des Fensters bei

		// add key listener
		GlobalScreen.addNativeKeyListener(new KeyboardListener(this, model));

		// sets up the tray icon (using awt code run on the swing thread).
		SwingUtilities.invokeLater(this::addAppToTray);

		// set global URL for HTTPRequestUtil to Website where PHP scripts are executed.
		HTTPRequestUtil.setGlobalURL(config.get("globalurl"));

		// Verkleinerung
		// stage.show(); // zeige Fenster
		showStage();
	}

	private void addAppToTray() {
		try {
			// ensure awt toolkit is initialized.
			Toolkit.getDefaultToolkit();

			// set up a system tray icon.
			SystemTray tray = SystemTray.getSystemTray();
			java.awt.Image image = Toolkit.getDefaultToolkit().getImage(config.get("trayicon"));
			TrayIcon trayIcon = new SysTray(this, image);

			// if the user double-clicks on the tray icon, show the main app stage.
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						if (!stage.isShowing()) {
							Platform.runLater(() -> Clipboarder.this.showStage());
						} else {
							Platform.runLater(() -> Clipboarder.this.hideStage());
						}
					}
				}
			});

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
			MenuItem exitItem = new MenuItem("Exit");
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
		} catch (

		AWTException e) {
			System.out.println("Unable to init system tray");
			e.printStackTrace();
		}
	}

	public void showStage() {
		if (stage != null) {
			if (model.getSelectedTab() == 1) {
				try {
					model.refreshEntries(true);
					model.setClipsLoaded(true);
					model.setupClipsMenu(true);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			stage.show();
			stage.toFront();
		}
	}

	public void hideStage() {
		if (stage != null) {
			stage.hide();
		}
	}

	public SceneModel getModel() {
		return model;
	}

	public Stage getStage() {
		return stage;
	}
}
