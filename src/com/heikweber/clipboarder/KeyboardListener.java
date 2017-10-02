package com.heikweber.clipboarder;

import java.awt.HeadlessException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;

/**
 * Object class for entries in copy history
 *
 * @author Philipp, David
 */

public class KeyboardListener extends NativeKeyAdapter {

	private Clipboarder clipboarder;
	private SceneModel model;
	private Configuration config;

	// Inject dependency clipboarder
	public KeyboardListener(Clipboarder clipboarder, SceneModel model, Configuration config) {
		this.clipboarder = clipboarder;
		this.model = model;
		this.config = config;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_C) {
			if ((e.getModifiers() & NativeInputEvent.CTRL_L_MASK) != 0) {
				if (model.isRecording()) {
					if (model.isLoggedIn()) {
						try {
							copyToClipboarder();
						} catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		}
	}

	public void copyToClipboarder() throws HeadlessException, UnsupportedFlavorException, IOException {
		Platform.runLater(() -> {
			String clipboardText;
			Clipboard clipboard = Clipboard.getSystemClipboard();
			clipboardText = clipboard.getString();

			if (clipboardText == null)
				return;

			CopyEntry latestEntry = clipboarder.getModel().getLatestCopyEntry();
			if (latestEntry != null && latestEntry.getContent().equals(clipboardText))
				return;

			String response = null;

			try {
				// check if set token and mail in config
				if (model.config.get("token").toString() != null && !model.config.get("token").toString().isEmpty()) {
					if (model.config.get("mail").toString() != null && !model.config.get("mail").toString().isEmpty()) {
						// both is set -> so we can try to addClipWithtoken
						response = HTTPRequestUtil.addClipWithToken(model.config.get("mail").toString(),
								model.config.get("token").toString(), clipboardText, config.get("cryptKey"));
					}
				} else {
					response = HTTPRequestUtil.addClipWithPassword(model.getMail(), model.getPassword(), clipboardText,
							config.get("cryptKey"));
				}
			} catch (Exception e) {
				System.out.println("Could not add Clip" + response);
			}
			try {
				model.layoutPane.getChildren().clear();
				Node contentPane = clipboarder.getModel().setupClipsMenu(true);
				// setting active tab to clips
				model.setNavigation(1);
				model.layoutPane.setTop(model.getNavigationPane());
				BorderPane.setMargin(model.getNavigationPane(), model.getInsets());
				model.layoutPane.setCenter(contentPane);
				model.layoutPane.requestLayout();
			} catch (Exception e) {
				System.out.println("Could not refresh.");
			}
		});
	}

}
