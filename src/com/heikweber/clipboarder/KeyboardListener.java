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

	// Inject dependency clipboarder
	public KeyboardListener(Clipboarder clipboarder, SceneModel model) {
		this.clipboarder = clipboarder;
		this.model = model;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_C) {
			if ((e.getModifiers() & NativeInputEvent.CTRL_L_MASK) != 0) {
				System.out.println(model.isRecording());
				System.out.println(model.isLoggedIn());
				if (model.isRecording()) {
					if (model.isLoggedIn()) {
						try {
							copyToClipboarder();
						} catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}

			}
		}
	}

	public void copyToClipboarder() throws HeadlessException, UnsupportedFlavorException, IOException {
		Platform.runLater(() -> {
			final String clipboardText;
			Clipboard clipboard = Clipboard.getSystemClipboard();
			String string = clipboard.getString();
			clipboardText = string;
			System.out.println(clipboardText);

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
						System.out.println("Add Clip with Token");
						// both is set -> so we can try to addClipWithtoken
						response = HTTPRequestUtil.addClipWithToken(model.config.get("mail").toString(),
								model.config.get("token").toString(), clipboardText);
					}
				} else {
					System.out.println("Add Clip with Password");
					response = HTTPRequestUtil.addClipWithPassword(model.getMail(), model.getPassword(), clipboardText);
				}
			} catch (Exception e) {
				System.out.println("Could not add Clip");
				e.printStackTrace();
			}
			System.out.println("Strato: " + response);
			try {
				// clipboarder.getModel().refreshEntries(true);
				model.layoutPane.getChildren().clear();
				Node contentPane = clipboarder.getModel().setupClipsMenu(true);

				// clipboarder.getModel().layout.requestLayout();
				model.layoutPane.setTop(model.getNavigationPane());
				BorderPane.setMargin(model.getNavigationPane(), model.getInsets());
				model.layoutPane.setCenter(contentPane);
				// model.layoutPane.getChildren().addAll(model.getNavigationPane(),
				// contentPane);
				// model.setSelectedTab(getSelectedTab());
				model.layoutPane.requestLayout();
			} catch (Exception e) {
				System.out.println("Could not refresh.");
				e.printStackTrace();
			}
		});
	}

}
