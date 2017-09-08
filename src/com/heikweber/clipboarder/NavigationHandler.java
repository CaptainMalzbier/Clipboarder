package com.heikweber.clipboarder;

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
				if (!model.config.isEmpty("token")) {
					String response = HTTPRequestUtil.loginWithToken(model.config.get("mail"),
							model.config.get("token"));
					if (response.contains("true")) {
						login();
					} else {
						System.out.println(response);
					}
				} else {
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
			//
			model.setContentPane(model.setupSettingsMenu());
			break;
		case 3:
			// Render Register
			model.setContentPane(model.setupRegisterMenu());
			break;
		default:
			model.getStage().hide();
		}

		model.layoutPane.getChildren().clear();
		model.layoutPane.getChildren().addAll(model.getNavigationPane(), model.getContentPane());
		model.setSelectedTab(getSelectedTab());
		model.layoutPane.requestLayout();
	}

	private void login() {
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
