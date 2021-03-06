package com.heikweber.clipboarder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Nullable;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author Philipp, David
 */

public class SceneModel {

	private Stage stage;
	private Scene scene;
	Configuration config;
	public static StackPane layout = new StackPane();
	private List<CopyEntry> copyEntryList = new ArrayList<>();
	private int selectedEntry = 0;
	private int selectedTab = 0;
	private int numberOfClips = 1;
	private boolean loggedIn = false;
	private boolean isRecording = false;
	private boolean clipsLoaded = false;
	private String name;
	private String token;
	private String activateToken;
	private String mail;
	private String password;
	private boolean rememberMe;
	private Pagination pagination;
	private List<Button> tabs;
	private List<VBox> contents;
	private Button account;
	private Button clips;
	private Button settings;
	private Button hide;
	BorderPane layoutPane = new BorderPane();
	private Node contentPane;
	private HBox navigationPane;
	private Insets insets = new Insets(0, 0, 5, 0);

	public SceneModel(Stage stage, Configuration config) throws IllegalStateException, Exception {
		this.setStage(stage);
		this.config = config;
		scene = createScene(config);
		scene.getStylesheets()
				.add(new File(config.get("stylePath")).toURI().toString() + config.get("style").toString());

		/*
		 * set button actions (seen as tabs) account: 0 clips: 1 settings: 2 hide: 3
		 */
		int counter = 0;
		for (Button b : getTabs()) {
			b.setOnAction(new NavigationHandler(this, counter++));
			b.getStyleClass().add("nav");
		}

		pagination = new Pagination(1, 0);
	}

	private Scene createScene(Configuration config) throws IllegalStateException, Exception {
		setAccount(new Button("Account"));
		setClips(new Button("Clips"));
		setSettings(new Button("Settings"));
		setHide(new Button("�"));

		createNavigation();

		try {
			// check if set token and mail in config
			if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
				setToken(config.get("token").toString());
				if (config.get("mail").toString() != null && !config.get("mail").toString().isEmpty()) {
					setMail(config.get("mail").toString());
					String response = HTTPRequestUtil.loginWithToken(config.get("mail"), config.get("token"));
					if (response.contains("true")) {
						setLoggedIn(true);
					}
				}
			}
		} catch (Exception e) {
			// Could not automatically login
			e.printStackTrace();
		}

		if (isLoggedIn()) {
			setNavigation(1);
			setContentPane(setupClipsMenu(false)); // setup clips menu but don't create new page
		} else {
			setNavigation(0);
			setContentPane(setupAccountMenu());
		}

		// init from config
		if (config.get("recording").toString().equals("true")) {
			setRecording(true);
		} else {
			setRecording(false);
		}

		layout.getChildren().clear();

		layoutPane.setTop(getNavigationPane());
		BorderPane.setMargin(getNavigationPane(), insets);
		layoutPane.setCenter(getContentPane());
		StackPane.setMargin(layoutPane, new Insets(5));
		layout.getChildren().add(layoutPane);

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

	void setNavigation(int tabOne) {

		// leave it here for later usage when switching from account to clips
		if (tabOne < 2) {
			setSelectedTab(tabOne);
		}

		// Navigationbar
		HBox navigationPane = new HBox(5);
		// Spacer
		final Pane spacer = new Pane();
		spacer.setMinSize(5, 1);
		HBox.setHgrow(spacer, Priority.ALWAYS);

		navigationPane.getChildren().addAll(getTabs().get(getSelectedTab()), getTabs().get(2), spacer,
				getTabs().get(3));

		setNavigationPane(navigationPane);
	}

	VBox setupAccountMenu() {

		VBox accountContent = new VBox(10);

		Label accountStatus = new Label("Authentification");

		HBox mailBox = new HBox(5);
		HBox passwordBox = new HBox(5);
		Label lMail = new Label("E-Mail");
		lMail.getStyleClass().add("fill-in");
		Label lPassword = new Label("Password");
		lPassword.getStyleClass().add("fill-in");
		TextField mail = new TextField();
		PasswordField password = new PasswordField();

		mailBox.getChildren().addAll(lMail, mail);
		passwordBox.getChildren().addAll(lPassword, password);

		VBox accountButtons = new VBox(20);
		VBox secondAccountButtons = new VBox(5);
		Button register = new Button("Register");
		Button login = new Button("Login");
		Button forgotPassword = new Button("Forgot Password");

		CheckBox rememberMe = new CheckBox("Remember me");

		rememberMe.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!isRememberMe()) {
					setRememberMe(true);
				} else {
					setRememberMe(false);
				}
			}
		});

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});
		password.textProperty().addListener((observable, oldPassword, newPassword) -> {
			setPassword(newPassword);
		});

		// fire button action, login by pressing ENTER
		password.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER)
				login.fire();
		});

		login.setOnAction(new NavigationHandler(this, 1));
		register.setOnAction(new NavigationHandler(this, 5));
		forgotPassword.setOnAction(new NavigationHandler(this, 7));

		register.setMaxWidth(Double.MAX_VALUE);
		login.setMaxWidth(Double.MAX_VALUE);
		forgotPassword.setMaxWidth(Double.MAX_VALUE);

		secondAccountButtons.getChildren().addAll(register, forgotPassword);

		accountButtons.getChildren().addAll(login, secondAccountButtons);
		accountContent.getChildren().addAll(accountStatus, mailBox, passwordBox, rememberMe, accountButtons);

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			// In config exist a token -> render try Again button

			Label spaceStatus = new Label("");
			Label tokenStatus = new Label("Login with token");

			Button loginWithToken = new Button("Try again");
			loginWithToken.setOnAction(new NavigationHandler(this, 1));

			accountContent.getChildren().addAll(spaceStatus, tokenStatus, loginWithToken);
		}

		return accountContent;
	}

	VBox setupRegisterMenu() {

		VBox registerContent = new VBox(10);

		Label registerStatus = new Label("Registration");

		HBox nameBox = new HBox(5);
		HBox mailBox = new HBox(5);
		HBox passwordBox = new HBox(5);
		Label lName = new Label("Firstname");
		lName.getStyleClass().add("fill-in");
		Label lMail = new Label("E-Mail");
		lMail.getStyleClass().add("fill-in");
		Label lPassword = new Label("Password");
		lPassword.getStyleClass().add("fill-in");
		TextField name = new TextField();
		TextField mail = new TextField();
		PasswordField password = new PasswordField();

		nameBox.getChildren().addAll(lName, name);
		mailBox.getChildren().addAll(lMail, mail);
		passwordBox.getChildren().addAll(lPassword, password);

		Button register = new Button("Register");
		register.setMaxWidth(Double.MAX_VALUE);

		name.textProperty().addListener((observable, oldName, newName) -> {
			setName(newName);
		});

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});
		password.textProperty().addListener((observable, oldPassword, newPassword) -> {
			setPassword(newPassword);
		});

		register.setOnAction(new NavigationHandler(this, 4));

		registerContent.getChildren().addAll(registerStatus, nameBox, mailBox, passwordBox, register);

		return registerContent;
	}

	VBox setupActivationMenu() {

		VBox activationContent = new VBox(10);
		Label activationStatus = new Label("Activation");

		HBox tokenBox = new HBox(5);
		Label lToken = new Label("Token");
		lToken.getStyleClass().add("fill-in");
		TextField token = new TextField();

		tokenBox.getChildren().addAll(lToken, token);

		Button activate = new Button("Activate");

		token.textProperty().addListener((observable, oldToken, newToken) -> {
			setActivateToken(newToken);
		});

		activate.setOnAction(new NavigationHandler(this, 10)); // // Execute Activation

		activationContent.getChildren().addAll(activationStatus, tokenBox, activate);

		return activationContent;
	}

	VBox setupForgotPassword() {

		VBox forgotPasswordContent = new VBox(10);

		Label forgotPasswordStatus = new Label("Reset your password");

		HBox mailBox = new HBox(5);

		Label lMail = new Label("E-Mail");
		lMail.getStyleClass().add("fill-in");
		TextField mail = new TextField();

		mailBox.getChildren().addAll(lMail, mail);

		Button forgotPassword = new Button("Confirm");
		forgotPassword.setMaxWidth(Double.MAX_VALUE);

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});

		forgotPassword.setOnAction(new NavigationHandler(this, 6));

		forgotPasswordContent.getChildren().addAll(forgotPasswordStatus, mailBox, forgotPassword);

		return forgotPasswordContent;
	}

	VBox setupNewPassword() {

		VBox newPasswordContent = new VBox(10);

		Label newPasswordStatus = new Label("Setup new password");

		HBox mailBox = new HBox(5);
		HBox tokenBox = new HBox(5);
		HBox passwordBox = new HBox(5);
		Label lMail = new Label("E-Mail");
		lMail.getStyleClass().add("fill-in");
		Label lToken = new Label("Token");
		lToken.getStyleClass().add("fill-in");
		Label lPassword = new Label("New password");
		lPassword.getStyleClass().add("fill-in");
		TextField mail = new TextField();
		TextField token = new TextField();
		TextField password = new PasswordField();

		mailBox.getChildren().addAll(lMail, mail);
		tokenBox.getChildren().addAll(lToken, token);
		passwordBox.getChildren().addAll(lPassword, password);

		Button bNewPassword = new Button("Confirm");

		token.textProperty().addListener((observable, oldToken, newToken) -> {
			setToken(newToken);
		});

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});

		password.textProperty().addListener((observable, oldPassword, newPassword) -> {
			setPassword(newPassword);
		});

		bNewPassword.setOnAction(new NavigationHandler(this, 8));

		newPasswordContent.getChildren().addAll(newPasswordStatus, mailBox, tokenBox, passwordBox, bNewPassword);

		return newPasswordContent;
	}

	AnchorPane setupClipsMenu(boolean createPage) throws IllegalStateException, Exception {

		int itemsPerPage = (Integer.parseInt(config.get("height")) - (95)) / (33 + 5);
		config.set("count", Integer.toString(itemsPerPage));

		refreshEntries(createPage);

		int pageCount = 1;
		if (copyEntryList.size() >= itemsPerPage) {
			pageCount = (int) Math.ceil(copyEntryList.size() / (float) itemsPerPage);
		}

		Pagination pagination = new Pagination();
		pagination.setPageCount(pageCount);
		pagination.setCurrentPageIndex(0);
		pagination.setPageFactory(pageIndex -> createPage(pageIndex));
		AnchorPane anchorPane = new AnchorPane();
		AnchorPane.setTopAnchor(pagination, 0.0);
		AnchorPane.setRightAnchor(pagination, 0.0);
		AnchorPane.setBottomAnchor(pagination, 0.0);
		AnchorPane.setLeftAnchor(pagination, 0.0);
		anchorPane.getChildren().addAll(pagination);

		return anchorPane;
	}

	VBox setupSettingsMenu() {

		setSelectedTab(2);

		VBox settingsContent = new VBox(20);
		Label settingsStatus = new Label("Settings");

		CheckBox recording = new CheckBox("Enable Recording");

		String stylePath = config.get("stylePath");
		if (stylePath.isEmpty()) {
			stylePath = "c:/";
		}

		Label stylePathLabel = new Label("CSS Directory");
		stylePathLabel.getStyleClass().add("descriptionLabel");
		TextField stylePathField = new TextField(config.get("stylePath"));
		Button stylePathChooser = new Button("Choose");
		stylePathChooser.setMaxWidth(Double.MAX_VALUE);
		Label styleLabel = new Label("Theme");
		styleLabel.getStyleClass().add("descriptionLabel");

		ComboBox<String> styleChooser = new ComboBox<>();

		TextField widthChooser = new TextField(config.get("width"));
		TextField heightChooser = new TextField(config.get("height"));

		widthChooser.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(widthChooser, Priority.ALWAYS);
		heightChooser.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(heightChooser, Priority.ALWAYS);

		HBox widthFields = new HBox(5);
		HBox heightFields = new HBox(5);

		Label px1 = new Label("px");
		Label px2 = new Label("px");
		px1.getStyleClass().add("descriptionLabel");
		px2.getStyleClass().add("descriptionLabel");

		widthFields.getChildren().addAll(widthChooser, px1);
		heightFields.getChildren().addAll(heightChooser, px2);

		widthChooser.textProperty().addListener((observable, oldValue, newValue) -> {
			widthChooser.setText(widthChooser.getText().replaceAll("[^\\d]", ""));

			if (widthChooser.getText().length() > 9)
				widthChooser.setText(removeLastChar(widthChooser.getText()));
		});

		heightChooser.textProperty().addListener((observable, oldValue, newValue) -> {
			heightChooser.setText(heightChooser.getText().replaceAll("[^\\d]", ""));

			if (heightChooser.getText().length() > 9)
				heightChooser.setText(removeLastChar(heightChooser.getText()));
		});

		widthChooser.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == false) {
				resizeWindow(0, widthChooser, 200);
				widthChooser.selectPositionCaret(widthChooser.getText().length());
			}
		});
		heightChooser.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == false) {
				resizeWindow(1, heightChooser, 500);
				heightChooser.selectPositionCaret(heightChooser.getText().length());
			}
		});

		widthChooser.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				resizeWindow(0, widthChooser, 200);
				widthChooser.selectPositionCaret(widthChooser.getText().length());
			} else if (e.getCode() == KeyCode.DOWN) {
				resizeWindow(0, widthChooser, 200);
				heightChooser.requestFocus();
				heightChooser.selectPositionCaret(heightChooser.getText().length());
			}
		});

		heightChooser.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.ENTER) {
				resizeWindow(1, heightChooser, 500);
				heightChooser.selectPositionCaret(heightChooser.getText().length());
			} else if (e.getCode() == KeyCode.UP) {
				resizeWindow(1, heightChooser, 500);
				widthChooser.requestFocus();
				widthChooser.selectPositionCaret(widthChooser.getText().length());
			}
		});

		collectStyles(styleChooser);
		styleChooser.getSelectionModel().select(getDisplayStyleName(config.get("style")));
		styleChooser.setMaxWidth(Double.MAX_VALUE);

		recording.setSelected(isRecording());
		recording.setOnAction(new SettingsHandler(0, null, this, null, null));

		stylePathChooser
				.setOnAction(new SettingsHandler(1, stage, this, scene, new Node[] { stylePathField, styleChooser }));

		styleChooser.valueProperty()
				.addListener((ObservableValue<? extends String> ov, String oldValue, String newValue) -> {
					config.set("style", resetStyleName(newValue));
					scene.getStylesheets().clear();
					scene.getStylesheets()
							.add(new File(config.get("stylePath")).toURI().toString() + config.get("style").toString());
				});

		VBox styleElements = new VBox(10);
		VBox sizeElements = new VBox(10);
		VBox settingsButtons = new VBox(20);
		Button bExit = new Button("Exit");
		settingsButtons.setMaxWidth(Double.MAX_VALUE);
		bExit.setMaxWidth(Double.MAX_VALUE);
		bExit.setOnAction(actionEvent -> System.exit(0));

		styleElements.getChildren().addAll(stylePathLabel, stylePathField, stylePathChooser, styleLabel, styleChooser);
		sizeElements.getChildren().addAll(widthFields, heightFields);

		settingsButtons.getChildren().add(bExit);

		settingsContent.getChildren().addAll(settingsStatus, recording, styleElements, sizeElements, settingsButtons);

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			Button bLogout = new Button("Forget me");
			HBox.setHgrow(bLogout, Priority.ALWAYS);
			bLogout.setMaxWidth(Double.MAX_VALUE);
			bLogout.setOnAction(new NavigationHandler(this, 12)); // Forget me
			settingsButtons.getChildren().clear();
			settingsButtons.getChildren().addAll(bLogout, bExit);
		}

		return settingsContent;

	}

	VBox setupMessageDisplay(String displayMessage, int confirmAction) {
		VBox messageContent = new VBox(10);

		// split message String into pieces of each 20 characters for better displaying
		String[] message = displayMessage.split("(?<=\\G.{20})");

		displayMessage = "";

		// collect all messageParts and setup a new displayMessage String
		for (String messagePart : message) {
			displayMessage += messagePart + "\n";
		}

		Label messageStatus = new Label(displayMessage);

		Button messageConfirm = new Button("OK");
		messageConfirm.setMaxWidth(Double.MAX_VALUE);

		messageConfirm.setOnAction(new NavigationHandler(this, confirmAction));

		messageContent.getChildren().addAll(messageStatus, messageConfirm);

		return messageContent;
	}

	protected Node createPage(Integer pageIndex) {
		final VBox page = new VBox(5);

		if (pageIndex == null || copyEntryList.isEmpty())
			return page;

		int count = config.getInt("count");

		int entryFrom = pageIndex * count;
		int entryTo = entryFrom + count;

		config.set("count", Integer.toString(count));

		if (entryFrom >= copyEntryList.size())
			return page;
		if (entryTo >= copyEntryList.size())
			entryTo = copyEntryList.size();

		for (int i = entryFrom; i < entryTo; ++i) {
			final CopyEntry copyEntry = copyEntryList.get(i);
			final HBox entry = createEntry(copyEntry);
			page.getChildren().add(entry);
		}

		return page;
	}

	private HBox createEntry(CopyEntry copyEntry) {
		HBox entry = new HBox(5);
		Button entryContent = new Button(copyEntry.getSingleLineContent());
		entryContent.setTextOverrun(OverrunStyle.ELLIPSIS);
		entryContent.setMaxWidth(Double.MAX_VALUE);

		HBox.setHgrow(entryContent, Priority.ALWAYS);
		entryContent.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SceneModel.this.selectedEntry = copyEntry.getId();
				final Clipboard clipboard = Clipboard.getSystemClipboard();
				final ClipboardContent content = new ClipboardContent();
				content.putString(copyEntry.getContent());
				clipboard.setContent(content);
			}
		});

		Button removeEntry = new Button("X");
		removeEntry.getStyleClass().add("removeEntry");
		removeEntry.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(entryContent, Priority.ALWAYS);
		removeEntry.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					HTTPRequestUtil.deleteClipWithPassword(getMail(), getPassword(), copyEntry.getId());
					refreshEntries(true);
					layoutPane.getChildren().clear();
					Node contentPane = setupClipsMenu(true);
					layoutPane.setTop(getNavigationPane());
					BorderPane.setMargin(getNavigationPane(), getInsets());
					layoutPane.setCenter(contentPane);
					layoutPane.requestLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		entry.getChildren().addAll(entryContent, removeEntry);
		return entry;
	}

	public void refreshEntries(boolean createNewPage) throws IllegalStateException, Exception {

		int number = config.getInt("number");

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			copyEntryList = HTTPRequestUtil.getClipsWithToken(config.get("mail"), config.get("token"),
					config.getInt("offset"), number, this, config.get("cryptKey"));
		} else {
			copyEntryList = HTTPRequestUtil.getClipsWithPassword(getMail(), getPassword(), config.getInt("offset"),
					number, this, config.get("cryptKey"));
		}
		if (createNewPage)
			pagination.setPageFactory(idx -> createPage(idx));
	}

	private String removeLastChar(String s) {
		s = s.substring(0, s.length() - 1);
		return s;
	}

	private void resizeWindow(int id, TextField textField, int maxVal) {

		int newVal = Math.max(Integer.parseInt(textField.getText()), maxVal);

		switch (id) {
		case 0:
			newVal = (int) Math.min(newVal, Screen.getPrimary().getBounds().getMaxX() - config.getInt("offsetwidth"));
			config.set("width", Integer.toString(newVal));
			stage.setWidth(newVal);
			break;
		case 1:
			newVal = (int) Math.min(newVal, Screen.getPrimary().getBounds().getMaxY() - config.getInt("offsetheight"));
			config.set("height", Integer.toString(newVal));
			stage.setHeight(newVal);
			break;
		}
		textField.setText(Integer.toString(newVal));
	}

	private String resetStyleName(String style) {
		style = style + ".css";
		return style.toLowerCase();
	}

	private String getDisplayStyleName(String style) {
		style = style.replace(".css", "");
		return style.substring(0, 1).toUpperCase() + style.substring(1);
	}

	public int getSelectedEntryIndex() {
		return selectedEntry;
	}

	public CopyEntry getSelectedEntry() {
		return copyEntryList.get(selectedEntry);
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

	public Scene getScene() {
		return scene;
	}

	public int getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(int selectedTab) {
		this.selectedTab = selectedTab;

		// remove style class for non-active buttons
		for (Button b : getTabs()) {
			for (int i = 0; i < b.getStyleClass().size(); i++) {
				if (b.getStyleClass().get(i).toString().equals("active")) {
					b.getStyleClass().remove("active");
				}
			}
		}

		getTabs().get(selectedTab).getStyleClass().add("active");
	}

	void collectStyles(ComboBox<String> comboBox) {
		File[] files = new File(config.get("stylePath")).listFiles();
		// If this pathname does not denote a directory, then listFiles() returns null.

		comboBox.getItems().clear();

		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".css")) {
				comboBox.getItems().add(getDisplayStyleName(file.getName()));
			}
		}
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

	public Node getContentPane() {
		return contentPane;
	}

	public void setContentPane(Node contentPane) {
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

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean recording) {
		this.isRecording = recording;
	}

	public boolean areClipsLoaded() {
		return clipsLoaded;
	}

	public void setClipsLoaded(boolean clipsLoaded) {
		this.clipsLoaded = clipsLoaded;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getActivateToken() {
		return activateToken;
	}

	public void setActivateToken(String activateToken) {
		this.activateToken = activateToken;
	}

	public int getNumberOfClips() {
		return numberOfClips;
	}

	public void setNumberOfClips(int numberOfClips) {
		this.numberOfClips = numberOfClips;
	}

	public Insets getInsets() {
		return insets;
	}

	public void setInsets(Insets insets) {
		this.insets = insets;
	}

}
