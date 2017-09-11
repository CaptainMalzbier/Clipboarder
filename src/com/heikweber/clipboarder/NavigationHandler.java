package com.heikweber.clipboarder;

import java.io.IOException;

import org.apache.commons.configuration2.ex.ConfigurationException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

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
			model.setContentPane(model.setupAccountMenu());
			break;
		case 1:
			// Login Action
			try {
				if (model.config.get("token").toString() != null && !model.config.get("token").toString().isEmpty()) {
					System.out.println("Login with Token");
					String response = HTTPRequestUtil.loginWithToken(model.config.get("mail"),
							model.config.get("token"));
					if (response.contains("true")) {
						login();
					} else {
						System.out.println(response);
					}
				} else {
					System.out.println("Login with Password");
					String response = HTTPRequestUtil.loginWithPassword(model.getMail(), model.getPassword(),
							model.isRememberMe());

					if (response.contains("true")) {
						if (model.isRememberMe()) {
							System.out.println(response);
							response = response.split(",")[1];
							model.config.set("mail", model.getMail());
							model.config.set("token", response);
							model.config.saveConfig();
						}
						login();
					} else {
						System.out.println(response);
					}
				}
			} catch (Exception e) {
				System.out.println("Could not log in");
				e.printStackTrace();
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
			System.out.println("Execute registration");
			try {
				String response = HTTPRequestUtil.register(model.getName(), model.getMail(), model.getPassword());
				System.out.println(response);
				if (response.contains("User created")) {
					model.setContentPane(model.setupMessageDisplay("Check your mails", 11)); // Render Activation Pane
					System.out.println(response);
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 5)); // Try Again: Render Register Pane
					System.out.println(response);
				}
			} catch (Exception e) {
				System.out.println("Could not registed");
				e.printStackTrace();
			}
			break;
		case 5:
			System.out.println("Render Register Pane");
			// Render Register Pane
			model.setContentPane(model.setupRegisterMenu());
			break;
		case 6:
			System.out.println("Execute Forgot Password");
			// Execute Forgot Password
			try {
				String response = HTTPRequestUtil.forgotPassword(model.getMail());
				System.out.println(response);
				if (response.contains("Successfully")) {
					model.setContentPane(model.setupMessageDisplay(response, 9)); // Render Set new Password Pane
					System.out.println(response);
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 7)); // Repeat: Render Forgot Password Pane
					System.out.println(response);
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				e.printStackTrace();
			}
			break;
		case 7:
			System.out.println("Render Forgot Password Pane");
			// Render Forgot Password Pane
			model.setContentPane(model.setupForgotPassword());
			break;
		case 8:
			System.out.println("Execute Set new Password");
			// Execute Set new Password
			try {
				String response = HTTPRequestUtil.resetPassword(model.getMail(), model.getToken(), model.getPassword());
				System.out.println(response);
				if (response.contains("Password changed")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Login
					System.out.println(response);
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 9)); // Tray again, Render Set new Password
																					// Pane
					System.out.println(response);
				}
			} catch (Exception e) {
				System.out.println("Could not setup a new password");
				e.printStackTrace();
			}
			break;
		case 9:
			System.out.println("Render Set new Password Pane");
			// Render Set new Password Pane
			model.setContentPane(model.setupNewPassword());
			break;
		case 10:
			System.out.println("Execute Activation");
			// Execute Activation
			try {
				String response = HTTPRequestUtil.activate(model.getMail(), model.getActivateToken());
				System.out.println(response);
				if (response.contains("Successfully activated")) {
					model.setContentPane(model.setupMessageDisplay(response, 0)); // Render Login Pane
					System.out.println(response);
				} else {
					model.setContentPane(model.setupMessageDisplay(response, 11)); // Repeat: Render Forgot Password
																					// Pane
					System.out.println(response);
				}
			} catch (Exception e) {
				System.out.println("Could not request a new password");
				e.printStackTrace();
			}
			break;
		case 11:
			System.out.println("Render Activation Pane");
			// Render Activation Pane
			model.setContentPane(model.setupActivationMenu());
			break;
		case 12:
			// Render Logout Pane
			System.out.println("Render Forgot me Pane");
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

			model.setContentPane(model.setupAccountMenu());
			break;
		default:
			model.getStage().hide();
		}

		model.layoutPane.getChildren().clear();
		model.layoutPane.getChildren().addAll(model.getNavigationPane(), model.getContentPane());
		// model.setSelectedTab(getSelectedTab());
		model.layoutPane.requestLayout();
	}

	private void login() {
		model.setLoggedIn(true);
		model.setNavigation(1);
		try {
			model.setContentPane(model.setupClipsMenu());
		} catch (Exception e1) {
			System.out.println("Could not load Clips");
			e1.printStackTrace();
		}
		if (!model.areClipsLoaded()) {
			try {
				model.refreshEntries(true);
				model.setClipsLoaded(true);
			} catch (Exception e) {
				System.out.println("Unable to refresh Clips");
				e.printStackTrace();
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
