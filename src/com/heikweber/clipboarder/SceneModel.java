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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Philipp
 */
public class SceneModel {

	private Stage stage;
	private Scene scene;
	private Configuration config;
	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
	private List<CopyEntry> copyEntryList = new ArrayList<>();
	private int selectedEntry = 0;
	private int selectedTab = 0;
	private boolean loggedIn = false;
	private boolean clipsLoaded = false;
	private Pagination pagination;
	private List<Button> tabs;
	private List<VBox> contents;
	private Button account;
	private Button clips;
	private Button settings;
	private Button hide;
	VBox layoutPane = new VBox(10);
	private VBox contentPane;
	private HBox navigationPane;

	public SceneModel(Stage stage, Configuration config) {
		this.setStage(stage);
		this.config = config;
		scene = createScene(config);
		scene.getStylesheets().add(new File(config.get("stylePath")).toURI().toString());

		// set button actions (seen as tabs)
		this.account.setOnAction(new NavigationHandler(this, 0));
		this.clips.setOnAction(new NavigationHandler(this, 1));
		this.settings.setOnAction(new NavigationHandler(this, 2));
		this.hide.setOnAction(new NavigationHandler(this, 3));

		this.account.setId("account");
		this.clips.setId("clips");
		this.settings.setId("settings");
		this.hide.setId("hide");

	}

	private Scene createScene(Configuration config) {
		// Klasse zum Erzeugen der Szene

		// tabPane = new TabPane();

		// BorderPane borderPane = new BorderPane();

		// VBox layoutPane = new VBox();

		// tabHome = new Tab();
		// tabSettings = new Tab();
		// tabHide = new Tab();

		setAccount(new Button("Account"));
		setClips(new Button("Clips"));
		setSettings(new Button("Settings"));
		setHide(new Button("X"));

		createNavigation();

		// TODO login
		setLoggedIn(true);

		if (isLoggedIn()) {
			setNavigation(1);
			setContentPane(setupClipsMenu());
		} else {
			setNavigation(0);
			setContentPane(setupAccountMenu());
		}

		layout.getChildren().clear();

		layoutPane.getChildren().addAll(getNavigationPane(), getContentPane());
		StackPane.setMargin(layoutPane, new Insets(5));
		layout.getChildren().add(layoutPane);

		// VBox vBoxSettings = setupSettingsMenu();
		// tabHome.setText("Home");
		// tabHome.setClosable(false);
		// tabSettings.setText("Settings");
		// tabSettings.setClosable(false);
		// tabHide.setText("X");
		// tabHide.getStyleClass().add("hide");
		// tabHide.setClosable(false);
		// tabHide.setOnSelectionChanged(new EventHandler<Event>() {
		// @Override
		// public void handle(Event event) {
		// stage.hide();
		// tabPane.getSelectionModel().select(0);
		// }
		// });

		// vBoxHome.getChildren().add(new Label("Tab" + i));
		// vBoxHome.setAlignment(Pos.BASELINE_RIGHT);
		// vBoxSettings.getChildren().add(new Label("Settings"));
		// vBoxSettings.setAlignment(Pos.CENTER);
		// tabHome.setContent(vBoxHome);
		// tabSettings.setContent(vBoxSettings);
		// tabPane.getTabs().add(tabHome);
		// tabPane.getTabs().add(tabSettings);
		// tabPane.getTabs().add(tabHide);
		// layoutPane.getChildren().add(tabPane);

		// borderPane.setRight(new Button("X"));

		// layout.getChildren().add(vBox);
		// layout.getChildren().add(layoutPane);

		return new Scene(layout, config.getWidth(), config.getHeight());
	}

	private void createNavigation() {

		List<Button> tabs = new ArrayList<Button>();

		tabs.add(getAccount());
		tabs.add(getClips());
		tabs.add(getSettings());
		tabs.add(getHide());

		setTabs(tabs);

	}

	VBox setupAccountMenu() {

		setSelectedTab(0);

		VBox accountContent = new VBox();
		Button register = new Button("Register");

		accountContent.getChildren().add(register);

		return accountContent;
	}

	void setNavigation(int tabOne) {
		if (tabOne < 2) {
			setSelectedTab(tabOne);
		}
		System.out.println(getSelectedTab());
		HBox navigationPane = new HBox(5);

		navigationPane.getChildren().addAll(getTabs().get(getSelectedTab()), getTabs().get(2), getTabs().get(3));
		// TODO automatically scale button width
		navigationPane.getChildren().get(2).setTranslateX(22);

		setNavigationPane(navigationPane);
	}

	VBox setupClipsMenu() {
		pagination = new Pagination(1, 0);
		pagination.setPageFactory(pageIndex -> createPage(pageIndex));
		// AnchorPane anchorPane = new AnchorPane();
		// AnchorPane.setTopAnchor(pagination, 10.0);
		// AnchorPane.setRightAnchor(pagination, 5.0);
		// AnchorPane.setBottomAnchor(pagination, 2.0);
		// AnchorPane.setLeftAnchor(pagination, 5.0);
		// anchorPane.getChildren().addAll(pagination);

		return new VBox(pagination);
	}

	protected Node createPage(Integer pageIndex) {
		System.out.println("creating page " + pageIndex);
		final VBox page = new VBox(5);

		if (pageIndex == null || copyEntryList.isEmpty())
			return page;

		int entryFrom = pageIndex * 10;
		int entryTo = entryFrom + 10;

		if (entryFrom >= copyEntryList.size())
			return page;
		if (entryTo >= copyEntryList.size())
			entryTo = copyEntryList.size();

		for (int i = entryFrom; i < entryTo; ++i) {
			// for (int i = entryFrom; i < entryTo; ++i) {
			final CopyEntry copyEntry = copyEntryList.get(i);
			final Button button = createButton(copyEntry);
			page.getChildren().add(button);
			copyEntry.addListener(() -> {
				button.setText(copyEntry.getContent());
				layoutPane.requestLayout();
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
		pagination.setPageFactory(idx -> createPage(idx));
	}

	public int getSelectedEntryIndex() {
		return selectedEntry;
	}

	public CopyEntry getSelectedEntry() {
		return copyEntryList.get(selectedEntry);
	}

	VBox setupSettingsMenu() {
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
		layoutPane.requestLayout();
		if (copyEntryList.size() < config.getInt("count")) {
			pagination.setPageCount(pagination.getPageCount() + 1);
		}
	}

	public Scene getScene() {
		return scene;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Button getAccount() {
		return account;
	}

	public void setAccount(Button account) {
		this.account = account;
	}

	public Button getClips() {
		return clips;
	}

	public void setClips(Button clips) {
		this.clips = clips;
	}

	public Button getSettings() {
		return settings;
	}

	public void setSettings(Button settings) {
		this.settings = settings;
	}

	public Button getHide() {
		return hide;
	}

	public void setHide(Button hide) {
		this.hide = hide;
	}

	public List<VBox> getContents() {
		return contents;
	}

	public void setContents(List<VBox> contents) {
		this.contents = contents;
	}

	public List<Button> getTabs() {
		return tabs;
	}

	public void setTabs(List<Button> tabs) {
		this.tabs = tabs;
	}

	public VBox getContentPane() {
		return contentPane;
	}

	public void setContentPane(VBox contentPane) {
		this.contentPane = contentPane;
	}

	public HBox getNavigationPane() {
		return navigationPane;
	}

	public void setNavigationPane(HBox navigationPane) {
		this.navigationPane = navigationPane;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean areClipsLoaded() {
		return clipsLoaded;
	}

	public void setClipsLoaded(boolean clipsLoaded) {
		this.clipsLoaded = clipsLoaded;
	}
}
