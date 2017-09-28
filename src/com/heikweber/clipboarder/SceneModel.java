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
import javafx.scene.control.Pagination;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Philipp, David
 */

public class SceneModel {

	private Stage stage;
	private Scene scene;
	Configuration config;
	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
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

		// set button actions (seen as tabs)
		int counter = 0;
		for (Button b : getTabs()) {
			b.setOnAction(new NavigationHandler(this, counter++));
			b.getStyleClass().add("nav");
		}

		pagination = new Pagination(1, 0);

		this.account.setId("account"); // 0
		this.clips.setId("clips"); // 1
		this.settings.setId("settings"); // 2
		this.hide.setId("hide"); // 3
	}

	private Scene createScene(Configuration config) throws IllegalStateException, Exception {
		// Klasse zum Erzeugen der Szene
		setAccount(new Button("Account"));
		setClips(new Button("Clips"));
		setSettings(new Button("Settings"));
		setHide(new Button("–"));

		createNavigation();

		try {
			// check if set token and mail in config
			if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
				setToken(config.get("token").toString());
				if (config.get("mail").toString() != null && !config.get("mail").toString().isEmpty()) {
					setMail(config.get("mail").toString());

					// both is set -> so we can try to login
					// model.setSelectedTab(1); // Login Action
					// new NavigationHandler(model, 1);
					// System.out.println("Login with Token");
					String response = HTTPRequestUtil.loginWithToken(config.get("mail"), config.get("token"));
					System.out.println(response);
					if (response.contains("true")) {
						setLoggedIn(true);
					} else {
						System.out.println(response);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Could not automatically login");
			e.printStackTrace();
		}

		System.out.println("SIND WIR EINGELOGGT? " + isLoggedIn());

		if (isLoggedIn()) {
			setNavigation(1);
			setContentPane(setupClipsMenu(false));
		} else {
			setNavigation(0);
			setContentPane(setupAccountMenu());
		}

		// init from config
		if (config.get("uploadclips").toString().equals("true")) {
			setRecording(true);
		} else {
			setRecording(false);
		}

		layout.getChildren().clear();

		layoutPane.setTop(getNavigationPane());
		BorderPane.setMargin(getNavigationPane(), insets);
		layoutPane.setCenter(getContentPane());

		// layoutPane.getChildren().addAll(getNavigationPane(), getContentPane());

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

		// Navigationsleiste
		HBox navigationPane = new HBox(5);
		// Abstandshalter für die 3 Buttons in der Navigationsleiste
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

		login.setOnAction(new NavigationHandler(this, 1));
		register.setOnAction(new NavigationHandler(this, 5));
		forgotPassword.setOnAction(new NavigationHandler(this, 7));

		// HBox upperAccountButtons = new HBox(5);
		// HBox.setHgrow(register, Priority.ALWAYS);
		// HBox.setHgrow(login, Priority.ALWAYS);

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

		Label registertStatus = new Label("Registration");

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

		// HBox registerButton = new HBox(5);
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

		// registerButton.getChildren().addAll(register);

		registerContent.getChildren().addAll(registertStatus, nameBox, mailBox, passwordBox, register);

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

		HBox activationButton = new HBox(5);
		Button activation = new Button("Activate");

		token.textProperty().addListener((observable, oldToken, newToken) -> {
			setActivateToken(newToken);
		});

		activation.setOnAction(new NavigationHandler(this, 10)); // // Execute Activation
		activationButton.getChildren().addAll(activation);

		activationContent.getChildren().addAll(activationStatus, tokenBox, activationButton);

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

		HBox newPasswordButton = new HBox(5);
		Button newPasswordB = new Button("Confirm");

		token.textProperty().addListener((observable, oldToken, newToken) -> {
			setToken(newToken);
		});

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});

		password.textProperty().addListener((observable, oldPassword, newPassword) -> {
			setPassword(newPassword);
		});

		newPasswordB.setOnAction(new NavigationHandler(this, 8));

		newPasswordButton.getChildren().addAll(newPasswordB);

		newPasswordContent.getChildren().addAll(newPasswordStatus, mailBox, tokenBox, passwordBox, newPasswordButton);

		return newPasswordContent;
	}

	// AnchorPane setupClipsMenu() throws IllegalStateException, Exception {
	// return setupClipsMenu(false);
	// }

	AnchorPane setupClipsMenu(boolean createPage) throws IllegalStateException, Exception {

		int itemsPerPage = 10;

		refreshEntries(createPage);

		System.out.println("Größe der Liste: " + copyEntryList.size());

		int pageCount = 1;
		if (copyEntryList.size() >= 10) {
			pageCount = (int) Math.ceil(copyEntryList.size() / (float) itemsPerPage);
		}

		System.out.println("pageCount: " + pageCount);

		// pagination = new Pagination(pageCount, 0);
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

		VBox settingsContent = new VBox(10);
		Label settingsStatus = new Label("Settings");

		CheckBox uploadClips = new CheckBox("Enable Recording");

		String stylePath = config.get("stylePath");
		if (stylePath.isEmpty()) {
			stylePath = "c:/";
		}

		TextField stylePathField = new TextField(config.get("stylePath"));
		Button stylePathChooser = new Button("Choose");

		ComboBox<String> styleChooser = new ComboBox<>();

		collectStyles(styleChooser);

		uploadClips.setSelected(isRecording());
		uploadClips.setOnAction(new SettingsHandler(0, null, this, null, null));

		stylePathChooser
				.setOnAction(new SettingsHandler(1, stage, this, scene, new Node[] { stylePathField, styleChooser }));

		styleChooser.valueProperty().addListener((ObservableValue<? extends String> ov, String t, String t1) -> {
			System.out.println(ov + "___" +t + "___" +t1);
		});

		VBox settingsButtons = new VBox(20);
		Button bExit = new Button("Exit");
		settingsButtons.setMaxWidth(Double.MAX_VALUE);
		bExit.setMaxWidth(Double.MAX_VALUE);
		bExit.setOnAction(actionEvent -> System.exit(0));

		settingsButtons.getChildren().add(bExit);

		settingsContent.getChildren().addAll(settingsStatus, uploadClips, stylePathField, stylePathChooser,
				styleChooser, settingsButtons);

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			Button bLogout = new Button("Forget me");
			HBox.setHgrow(bLogout, Priority.ALWAYS);
			bLogout.setMaxWidth(Double.MAX_VALUE);
			bLogout.setOnAction(new NavigationHandler(this, 12)); // Forget me
			settingsButtons.getChildren().add(bLogout);
		}

		return settingsContent;

	}

	VBox setupMessageDisplay(String displayMessage, int confirmAction) {
		VBox messageContent = new VBox(10);

		// split message String into pieces of each 20 characters length for better
		// displaying
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
		System.out.println("creating page " + pageIndex);

		System.out.println(copyEntryList.size());

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
			final CopyEntry copyEntry = copyEntryList.get(i);
			final HBox entry = createEntry(copyEntry);
			page.getChildren().add(entry);
		}

		return page;
	}

	private HBox createEntry(CopyEntry copyEntry) {
		HBox entry = new HBox(5);
		Button entryContent = new Button(copyEntry.getShortContent());
		entryContent.setPrefWidth(155);
		entryContent.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println(copyEntry.getContent());
				SceneModel.this.selectedEntry = copyEntry.getId();
				final Clipboard clipboard = Clipboard.getSystemClipboard();
				final ClipboardContent content = new ClipboardContent();
				content.putString(copyEntry.getContent());
				clipboard.setContent(content);
			}
		});

		Button removeEntry = new Button("X");
		removeEntry.getStyleClass().add("removeEntry");
		removeEntry.setPrefWidth(20);
		removeEntry.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					HTTPRequestUtil.deleteClipWithPassword(getMail(), getPassword(), copyEntry.getId());
					refreshEntries(true);
					// setClipsLoaded(true);
					layoutPane.getChildren().clear();
					Node contentPane = setupClipsMenu(true);
					// clipboarder.getModel().layout.requestLayout();
					layoutPane.setTop(getNavigationPane());
					BorderPane.setMargin(getNavigationPane(), getInsets());
					layoutPane.setCenter(contentPane);
					// layoutPane.getChildren().addAll(getNavigationPane(), contentPane);
					// model.setSelectedTab(getSelectedTab());
					layoutPane.requestLayout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		// removeEntry.setOnAction(value);

		entry.getChildren().addAll(entryContent, removeEntry);
		return entry;
	}

	public void refreshEntries(boolean createNewPage) throws IllegalStateException, Exception {

		int number = config.getInt("number");
		System.out.println("number-2 " + number);
		System.out.println("refresh " + getNumberOfClips());
		// if (copyEntryList.size() < number) {
		// number = copyEntryList.size();
		// }

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			copyEntryList = HTTPRequestUtil.getClipsWithToken(config.get("mail"), config.get("token"),
					config.getInt("offset"), number, this);
		} else {
			copyEntryList = HTTPRequestUtil.getClipsWithPassword(getMail(), getPassword(), config.getInt("offset"),
					number, this);
		}
		System.out.println("refresh " + getNumberOfClips());
		if (createNewPage)
			pagination.setPageFactory(idx -> createPage(idx));
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
				comboBox.getItems().add(file.getName());
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

	public void setRecording(boolean userWantsToUploadClips) {
		this.isRecording = userWantsToUploadClips;
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
