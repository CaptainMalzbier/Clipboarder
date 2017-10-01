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
							model.setContentPane(model.setupMessageDisplay(response, 0)); // Try Again: Render Login
																							// Pane
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
							model.setContentPane(model.setupMessageDisplay(response, 0)); // Try Again: Render Login
																							// Pane
						}
					}
				} catch (Exception e) {
					model.setContentPane(model.setupMessageDisplay("Could not log in", 0)); // Try Again: Render Login
																							// Pane
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
					model.setContentPane(model.setupMessageDisplay("Check your mails", 11)); // Render Activation Pane
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 5)); // Try Again: Render Register Pane

				}
			} catch (Exception e) {
				model.setContentPane(model.setupMessageDisplay("Could not registed", 5)); // Try Again: Render Register
																							// Pane
				System.out.println("Could not registed");
			}
			break;
		case 5:
			// Render Register Pane
			model.setContentPane(model.setupRegisterMenu());
			break;
		case 6:
			// Execute Forgot Password
			try {
				String response = HTTPRequestUtil.forgotPassword(model.getMail());
				if (response.contains("Successfully")) {
					model.setContentPane(model.setupMessageDisplay(response, 9)); // Render Set new Password Pane
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 7)); // Repeat: Render Forgot Password Pane
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				model.setContentPane(model.setupMessageDisplay("Could not request a new password", 7)); // Repeat:
																										// Render Forgot
																										// Password Pane
			}
			break;
		case 7:
			// Render Forgot Password Pane
			model.setContentPane(model.setupForgotPassword());
			break;
		case 8:
			// Execute Set new Password
			try {
				String response = HTTPRequestUtil.resetPassword(model.getMail(), model.getToken(), model.getPassword());
				if (response.contains("Password changed")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Login
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 9)); // Tray again, Render Set new Password
																					// Pane
				}
			} catch (Exception e) {
				model.setContentPane(model.setupMessageDisplay("Could not setup a new password", 9)); // Tray again,
																										// Render Set
																										// new Password
																										// Pane
				System.out.println("Could not setup a new password");
			}
			break;
		case 9:
			// Render Set new Password Pane
			model.setContentPane(model.setupNewPassword());
			break;
		case 10:
			// Execute Activation
			try {
				String response = HTTPRequestUtil.activate(model.getMail(), model.getActivateToken());
				if (response.contains("Successfully activated")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Render Login Pane
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 11)); // Repeat: Render Forgot Password
																					// Pane
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				model.setContentPane(model.setupMessageDisplay("Could not request a new password", 11)); // Repeat:
																											// Render
																											// Forgot
																											// Password
			}
			break;
		case 11:
			// Render Activation Pane
			model.setContentPane(model.setupActivationMenu());
			break;
		case 12:
			// Render Logout Pane
			model.setLoggedIn(false);
			model.setMail("");
			model.setActivateToken("");
			model.setPassword("");
			model.setToken("");
			model.config.set("mail", "");
			model.config.set("token", "");
			model.config.set("uploadclips", "true");
			try {
				model.config.saveConfig();
			} catch (IOException | ConfigurationException e) {
				e.printStackTrace();
			}

			model.setContentPane(model.setupMessageDisplay("Your login data has been removed", 0)); // Try Again: Render
																									// Login Pane
			break;
		default:
			model.getStage().hide();
		}

		model.layoutPane.getChildren().clear();
		model.layoutPane.setTop(model.getNavigationPane());
		BorderPane.setMargin(model.getNavigationPane(), model.getInsets());
		model.layoutPane.setCenter(model.getContentPane());
		// model.layoutPane.getChildren().addAll(model.getNavigationPane(),
		// model.getContentPane());
		// model.setSelectedTab(getSelectedTab());
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
