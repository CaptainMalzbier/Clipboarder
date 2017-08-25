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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author Philipp
 */
public class SceneModel {

	private Scene scene;
	private Configuration config;
	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
	private List<CopyEntry> copyEntryList = new ArrayList<>();
	private TabPane tabPane;
	private int selectedEntry = 0;
	private Pagination pagination;
	private Tab tabHome;

	public SceneModel(Configuration config) {
		this.config = config;
		scene = createScene(config);
		scene.getStylesheets().add(new File(config.get("stylePath")).toURI().toString());
	}

	private Scene createScene(Configuration config) {
		// Klasse zum Erzeugen der Szene
		layout.getChildren().clear();

		tabPane = new TabPane();

		BorderPane borderPane = new BorderPane();
		Tab tabSettings = new Tab();
		tabHome = new Tab();

		// TODO show after logged in
		// VBox vBoxStartScreen = startScreen();
		AnchorPane vBoxHome = setupHomeMenu();

		VBox vBoxSettings = setupSettingsMenu();
		tabHome.setText("Home");
		tabHome.setClosable(false);
		tabSettings.setText("Settings");
		tabSettings.setClosable(false);
		// vBoxHome.getChildren().add(new Label("Tab" + i));
		// vBoxHome.setAlignment(Pos.BASELINE_RIGHT);
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

	private VBox startScreen() {

		return new VBox();
	}

	private AnchorPane setupHomeMenu() {
		pagination = new Pagination(1, 0);
		pagination.setStyle("-fx-border-color:red;");
		pagination.setPageFactory(pageIndex -> createPage(pageIndex));
		AnchorPane anchorPane = new AnchorPane();
		AnchorPane.setTopAnchor(pagination, 10.0);
		AnchorPane.setRightAnchor(pagination, 10.0);
		AnchorPane.setBottomAnchor(pagination, 10.0);
		AnchorPane.setLeftAnchor(pagination, 10.0);
		anchorPane.getChildren().addAll(pagination);

		return anchorPane;
	}

	protected Node createPage(Integer pageIndex) {
		System.out.println("creating page " + pageIndex);
		final VBox page = new VBox();
		if (pageIndex == null || copyEntryList.isEmpty())
			return page;

		int entryFrom = pageIndex * 10;
		int entryTo = entryFrom + 10;

		if (entryFrom >= copyEntryList.size())
			return page;
		if (entryTo >= copyEntryList.size())
			entryTo = copyEntryList.size();

		// for (int i = copyEntryList.size(); i > copyEntryList.size() - pageIndex * 10;
		// i--) {
		// System.out.println(copyEntryList.get(i).getContent());
		// }

		for (int i = entryFrom; i < entryTo; ++i) {
			// for (int i = entryFrom; i < entryTo; ++i) {
			final CopyEntry copyEntry = copyEntryList.get(i);
			final Button button = createButton(copyEntry);
			page.getChildren().add(button);
			copyEntry.addListener(() -> {
				button.setText(copyEntry.getContent());
				tabPane.requestLayout();
			});
		}

		return page;
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

	public void refreshEntries() throws IllegalStateException, Exception {
		copyEntryList = HTTPRequestUtil.getClipsWithPassword(config.get("mail"), config.get("password"),
				config.getInt("offset"), config.getInt("number"));
		// System.out.println(response);
		// TODO do something with the answer
		//
		// for (CopyEntry copyEntry : copyEntryList) {
		// addCopyEntry(copyEntry.getContent());
		// }

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
		return copyEntryList.get(0);
	}

	public void addCopyEntry(String content) {
		System.out.println("adding copy entry");
		int index = copyEntryList.size();
		CopyEntry copyEntry = new CopyEntry(content);
		copyEntry.setId(index);
		copyEntryList.add(0, copyEntry);
		pagination.setPageFactory(idx -> createPage(idx));
		tabPane.requestLayout();
		if (copyEntryList.size() < config.getInt("count")) {
			pagination.setPageCount(pagination.getPageCount() + 1);
		}
	}

	public Scene getScene() {
		return scene;
	}
}
