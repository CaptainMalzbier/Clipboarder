/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Philipp
 */
public class SceneModel {

	private Scene scene;

	public SceneModel(Configuration config) {
		scene = createScene(config);
		scene.getStylesheets().add(new File(config.get("stylePath")).toURI().toString());
	}

	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
	private List<CopyEntry> copyEntryList = new ArrayList<>();
	private TabPane tabPane;
	private int selectedEntry = 0;
	private VBox menu;

	private Scene createScene(Configuration config) {
		// Klasse zum Erzeugen der Szene
		layout.getChildren().clear();

		tabPane = new TabPane();

		BorderPane borderPane = new BorderPane();
		Tab tabSettings = new Tab();
		Tab tabHome = new Tab();
		VBox vBoxHome = setupHomeMenu();
		VBox vBoxSettings = setupSettingsMenu();
		tabHome.setText("Home");
		tabHome.setClosable(false);
		tabSettings.setText("Settings");
		tabSettings.setClosable(false);
		// vBoxHome.getChildren().add(new Label("Tab" + i));
		vBoxHome.setAlignment(Pos.BASELINE_RIGHT);
		vBoxSettings.getChildren().add(new Label("Settings"));
		vBoxSettings.setAlignment(Pos.CENTER);
		tabHome.setContent(vBoxHome);
		tabSettings.setContent(vBoxSettings);
		tabPane.getTabs().add(tabHome);
		tabPane.getTabs().add(tabSettings);

		borderPane.setCenter(tabPane);

		// layout.getChildren().add(vBox);
		layout.getChildren().add(borderPane);

		return new Scene(layout, config.getWidth(), config.getHeight());
	}

	private VBox setupHomeMenu() {
		menu = new VBox();
		// for (int index = 0; index < 9; index++) {
		// addCopyEntry("Test #" + (index + 1));
		// }
		return menu;
	}

	private Button createButton(CopyEntry copyEntry) {
		Button button = new Button(copyEntry.getContent());
		button.setMaxWidth(150);
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println(copyEntry.getContent());
				SceneModel.this.selectedEntry = copyEntry.getId();
			}
		});
		return button;
	}

	public int getSelectedEntryIndex() {
		return selectedEntry;
	}

	public CopyEntry getSelectedEntry() {
		return copyEntryList.get(selectedEntry);
	}

	private VBox setupSettingsMenu() {
		Button bExit = new Button("Exit");
		bExit.setOnAction(actionEvent -> System.exit(0));
		return new VBox(bExit);
	}

	public CopyEntry getCopyEntry(int index) {
		return copyEntryList.get(index);
	}

	public List<CopyEntry> getCopyEntryList() {
		return copyEntryList;
	}

	@Nullable
	public CopyEntry getLatestCopyEntry() {
		if (copyEntryList.isEmpty())
			return null;
		return copyEntryList.get(getCopyEntryList().size() - 1);
	}

	public void addCopyEntry(String content) {
		int index = copyEntryList.size();
		CopyEntry copyEntry = new CopyEntry(content);
		copyEntry.setId(index);
		copyEntryList.add(copyEntry);
		final Button button = createButton(copyEntry);
		menu.getChildren().add(button);
		copyEntry.addListener(() -> {
			button.setText(copyEntry.getContent());
			tabPane.requestLayout();
		});
	}

	public Scene getScene() {
		return scene;
	}
}
