/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration2.ex.ConfigurationException;

import com.sun.istack.internal.Nullable;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
	Configuration config;
	public static StackPane layout = new StackPane(); // Layout-Pane auf dem alles dargestellt wird
	private List<CopyEntry> copyEntryList = new ArrayList<>();
	private int selectedEntry = 0;
	private int selectedTab = 0;
	private boolean loggedIn = false;
	private boolean userWantsToUploadClips = false;
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
	VBox layoutPane = new VBox(10);
	private VBox contentPane;
	private HBox navigationPane;

	public SceneModel(Stage stage, Configuration config) throws IllegalStateException, Exception {
		this.setStage(stage);
		this.config = config;
		scene = createScene(config);
		scene.getStylesheets().add(new File(config.get("stylePath")).toURI().toString());

		// set button actions (seen as tabs)
		int counter = 0;
		for (Button b : getTabs()) {
			b.setOnAction(new NavigationHandler(this, counter++));
			b.getStyleClass().add("nav");
		}

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

		System.out.println("SIND WIR EINGELOGGT? " + isLoggedIn());

		if (isLoggedIn()) {
			setNavigation(1);
			setSelectedTab(1);
			setContentPane(setupClipsMenu());
		} else {
			setNavigation(0);
			setSelectedTab(0);
			setContentPane(setupAccountMenu());
		}

		// init from config
		if (config.get("uploadclips").toString().equals("true")) {
			setUserWantsToUploadClips(true);
		} else {
			setUserWantsToUploadClips(false);
		}

		layout.getChildren().clear();

		layoutPane.getChildren().addAll(getNavigationPane(), getContentPane());
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

	VBox setupMessageDisplay(String diplayMassage, int confirmAction) {
		VBox messageContent = new VBox(10);
		Label messageStatus = new Label(diplayMassage);

		HBox messageConfirmButton = new HBox(5);
		Button messageConfirm = new Button("Okay");

		messageConfirm.setOnAction(new NavigationHandler(this, confirmAction));

		messageConfirmButton.getChildren().addAll(messageConfirm);

		messageContent.getChildren().addAll(messageStatus, messageConfirmButton);

		return messageContent;
	}

	VBox setupAccountMenu() {

		VBox accountContent = new VBox(10);

		Label accountStatus = new Label("Authentication");

		HBox mailBox = new HBox(5);
		HBox passwordBox = new HBox(5);
		Label lMail = new Label("E-Mail");
		lMail.getStyleClass().add("fill-in");
		Label lPassword = new Label("Password");
		lPassword.getStyleClass().add("fill-in");
		TextField mail = new TextField();
		TextField password = new TextField();

		mail.setText("david@heik.info");
		password.setText("TestPW");

		mailBox.getChildren().addAll(lMail, mail);
		passwordBox.getChildren().addAll(lPassword, password);

		HBox loginButton = new HBox(5);
		HBox accountButtons = new HBox(5);
		Button register = new Button("Register");
		Button login = new Button("Login");
		Button forgetPassword = new Button("Forgot Password");

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
		forgetPassword.setOnAction(new NavigationHandler(this, 7));

		loginButton.getChildren().addAll(login);

		accountButtons.getChildren().addAll(register, forgetPassword);

		accountContent.getChildren().addAll(accountStatus, mailBox, passwordBox, rememberMe, loginButton,
				accountButtons);

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
		TextField password = new TextField();

		nameBox.getChildren().addAll(lName, name);
		mailBox.getChildren().addAll(lMail, mail);
		passwordBox.getChildren().addAll(lPassword, password);

		HBox registerButton = new HBox(5);
		Button register = new Button("Register");

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

		registerButton.getChildren().addAll(register);

		registerContent.getChildren().addAll(registertStatus, nameBox, mailBox, passwordBox, registerButton);

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
		Button activation = new Button("Activat");

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

		HBox forgotPasswordButton = new HBox(5);
		Button forgotPassword = new Button("Confirm");

		mail.textProperty().addListener((observable, oldMail, newMail) -> {
			setMail(newMail);
		});

		forgotPassword.setOnAction(new NavigationHandler(this, 6));

		forgotPasswordButton.getChildren().addAll(forgotPassword);

		forgotPasswordContent.getChildren().addAll(forgotPasswordStatus, mailBox, forgotPasswordButton);

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
		TextField password = new TextField();

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

	VBox setupClipsMenu() throws IllegalStateException, Exception {

		int itemsPerPage = 10;

		refreshEntries(false);

		System.out.println("Größe der Liste: " + copyEntryList.size());

		int pageCount = 1;
		if (copyEntryList.size() >= 10) {
			pageCount = copyEntryList.size() / itemsPerPage;
		}

		pagination = new Pagination(pageCount, 0);
		pagination.setPageFactory(pageIndex -> createPage(pageIndex));
		pagination.setCurrentPageIndex(0);
		// AnchorPane anchorPane = new AnchorPane();
		// AnchorPane.setTopAnchor(pagination, 10.0);
		// AnchorPane.setRightAnchor(pagination, 5.0);
		// AnchorPane.setBottomAnchor(pagination, 2.0);
		// AnchorPane.setLeftAnchor(pagination, 5.0);
		// anchorPane.getChildren().addAll(pagination);

		return new VBox(pagination);
	}

	VBox setupSettingsMenu() {

		setSelectedTab(2);

		VBox settingContent = new VBox(10);
		Label settingStatus = new Label("Setting");

		CheckBox uploadClips = new CheckBox("Upload Clips");

		uploadClips.setSelected(getUserWantsToUploadClips());
		uploadClips.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!getUserWantsToUploadClips()) {
					setUserWantsToUploadClips(true);
					config.set("uploadclips", "true");
					try {
						config.saveConfig();
					} catch (IOException | ConfigurationException e) {
						e.printStackTrace();
					}
				} else {
					setUserWantsToUploadClips(false);
					config.set("uploadclips", "false");
					try {
						config.saveConfig();
					} catch (IOException | ConfigurationException e) {
						e.printStackTrace();
					}
				}
			}
		});

		HBox settingButton = new HBox(5);
		Button bExit = new Button("Exit");
		bExit.setOnAction(actionEvent -> System.exit(0));

		settingButton.getChildren().addAll(bExit);

		settingContent.getChildren().addAll(settingStatus, uploadClips, settingButton);

		if (isLoggedIn()) {
			HBox logoutButton = new HBox(5);
			Button bLogout = new Button("Logout");
			bLogout.setOnAction(new NavigationHandler(this, 12)); // Logout
			logoutButton.getChildren().addAll(bLogout);
			settingContent.getChildren().addAll(logoutButton);
		}

		return settingContent;

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
		if (copyEntryList.size() < number) {
			number = copyEntryList.size();
		}

		if (config.get("token").toString() != null && !config.get("token").toString().isEmpty()) {
			copyEntryList = HTTPRequestUtil.getClipsWithToken(config.get("mail"), config.get("token"),
					config.getInt("offset"), number);
		} else {
			copyEntryList = HTTPRequestUtil.getClipsWithPassword(getMail(), getPassword(), config.getInt("offset"),
					config.getInt("number"));
		}
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

	public boolean getUserWantsToUploadClips() {
		return userWantsToUploadClips;
	}

	public void setUserWantsToUploadClips(boolean userWantsToUploadClips) {
		this.userWantsToUploadClips = userWantsToUploadClips;
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
}
