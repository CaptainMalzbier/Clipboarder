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
			model.setContentPane(model.setupAccountMenu());
			break;
		case 1:
			model.setNavigation(1);
			model.setContentPane(model.setupClipsMenu());
			if (!model.areClipsLoaded()) {
				try {
					model.refreshEntries();
					model.setClipsLoaded(true);
				} catch (Exception e) {
					System.out.println("Unable to refresh Clips");
					e.printStackTrace();
				}
			}
			break;
		case 2:
			model.setContentPane(model.setupSettingsMenu());
			break;
		default:
			model.getStage().hide();
		}

		model.layoutPane.getChildren().clear();
		model.layoutPane.getChildren().addAll(model.getNavigationPane(), model.getContentPane());
		model.setSelectedTab(getSelectedTab());
		model.layoutPane.requestLayout();
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
