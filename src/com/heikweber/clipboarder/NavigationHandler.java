package com.heikweber.clipboarder;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Object class for entries in copy history
 *
 * @author Philipp, David
 */

public class NavigationHandler implements EventHandler<ActionEvent> {

	private SceneModel model;
	private HBox navigation;
	private int selectedTab;
	private VBox contentPane;

	public NavigationHandler(SceneModel model, int selectedTab) {
		this.setModel(model);
		this.setSelectedTab(selectedTab);
	}

	@Override
	public void handle(ActionEvent event) {

		SceneModel model = getModel();
		switch (getSelectedTab()) {
		case 0:
			// Authentication Pane
			model.setNavigation(0);
			model.setContentPane(model.setupAccountMenu());
			break;
		case 1:
			// Login Action
			if (model.isLoggedIn()) {
				login();
			} else {
				try {
					if (model.config.get("token").toString() != null
							&& !model.config.get("token").toString().isEmpty()) {
						String response = HTTPRequestUtil.loginWithToken(model.config.get("mail"),
								model.config.get("token"));
						if (response.contains("true")) {
							login();
						} else {
							// Try again: render login pane
							model.setContentPane(model.setupMessageDisplay(response, 0));
						}
					} else {
						String response = HTTPRequestUtil.loginWithPassword(model.getMail(), model.getPassword(),
								model.isRememberMe());

						if (response.contains("true")) {
							if (model.isRememberMe()) {
								response = response.split(",")[1];
								model.config.set("mail", model.getMail());
								model.config.set("token", response);
								model.config.saveConfig();
							}
							login();
						} else {
							// Try again: render login pane
							model.setContentPane(model.setupMessageDisplay(response, 0));
						}
					}
				} catch (Exception e) {
					// Try again: render login pane
					model.setContentPane(model.setupMessageDisplay("Could not log in", 0));
					System.out.println("Could not log in");
				}

			}
			break;
		case 2:
			// Setting Pane
			model.setContentPane(model.setupSettingsMenu());
			break;
		case 3:
			// Hide Stage
			model.getStage().hide();
			break;
		case 4:
			// Execute registration
			try {
				String response = HTTPRequestUtil.register(model.getName(), model.getMail(), model.getPassword());
				if (response.contains("User created")) {
					model.setContentPane(model.setupMessageDisplay("Check your mails", 11)); // render activation pane
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 5)); // Try again: render register pane

				}
			} catch (Exception e) {
				// Try again: render register pane
				model.setContentPane(model.setupMessageDisplay("Could not register", 5));
				System.out.println("Could not register");
			}
			break;
		case 5:
			// render register pane
			model.setContentPane(model.setupRegisterMenu());
			break;
		case 6:
			// execute forgot password
			try {
				String response = HTTPRequestUtil.forgotPassword(model.getMail());
				if (response.contains("Successfully")) {
					model.setContentPane(model.setupMessageDisplay(response, 9)); // render set new password pane
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 7)); // Repeat: render forgot password pane
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				// Repeat: render forgot password pane
				model.setContentPane(model.setupMessageDisplay("Could not request a new password", 7));
			}
			break;
		case 7:
			// render forgot password pane
			model.setContentPane(model.setupForgotPassword());
			break;
		case 8:
			// Execute Set new Password
			try {
				String response = HTTPRequestUtil.resetPassword(model.getMail(), model.getToken(), model.getPassword());
				if (response.contains("Password changed")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Login
				} else {
					// Tray again: render set new password pane
					model.setContentPane(model.setupMessageDisplay(response, 9));
				}
			} catch (Exception e) {
				// Tray again: render set new password pane
				model.setContentPane(model.setupMessageDisplay("Could not setup a new password", 9));
				System.out.println("Could not setup a new password");
			}
			break;
		case 9:
			// render set new password pane
			model.setContentPane(model.setupNewPassword());
			break;
		case 10:
			// Execute activation
			try {
				String response = HTTPRequestUtil.activate(model.getMail(), model.getActivateToken());
				if (response.contains("Successfully activated")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Render login pane
				} else {
					// Repeat: render forgot password pane
					model.setContentPane(model.setupMessageDisplay(response, 11));
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				// Repeat: render, forgot password
				model.setContentPane(model.setupMessageDisplay("Could not request a new password", 11));
			}
			break;
		case 11:
			// Render activation pane
			model.setContentPane(model.setupActivationMenu());
			break;
		case 12:
			// Render logout pane
			model.setLoggedIn(false);
			model.setMail("");
			model.setActivateToken("");
			model.setPassword("");
			model.setToken("");
			model.config.set("mail", "");
			model.config.set("token", "");
			model.config.set("recording", "true");
			try {
				model.config.saveConfig();
			} catch (IOException | ConfigurationException e) {
				e.printStackTrace();
			}
			// Try again: render login pane
			model.setContentPane(model.setupMessageDisplay("Your login data has been removed", 0));
			break;
		default:
			model.getStage().hide();
		}

		model.layoutPane.getChildren().clear();
		model.layoutPane.setTop(model.getNavigationPane());
		BorderPane.setMargin(model.getNavigationPane(), model.getInsets());
		model.layoutPane.setCenter(model.getContentPane());
		model.layoutPane.requestLayout();

	}

	private void login() {
		model.setLoggedIn(true);
		model.setNavigation(1);
		try {
			model.setContentPane(model.setupClipsMenu(true));
		} catch (Exception e1) {
			System.out.println("Could not load Clips");
		}
		if (!model.areClipsLoaded()) {
			try {
				model.refreshEntries(true);
				model.setClipsLoaded(true);
			} catch (Exception e) {
				System.out.println("Unable to refresh Clips");
			}
		}
	}

	public SceneModel getModel() {
		return model;
	}

	public void setModel(SceneModel model) {
		this.model = model;
	}

	public VBox getContentPane() {
		return contentPane;
	}

	public void setContentPane(VBox contentPane) {
		this.contentPane = contentPane;
	}

	public HBox getNavigation() {
		return navigation;
	}

	public void setNavigation(HBox navigation) {
		this.navigation = navigation;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

}
