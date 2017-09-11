package com.heikweber.clipboarder;

import java.awt.HeadlessException;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.jnativehook.NativeInputEvent;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;

import javafx.application.Platform;
import javafx.scene.input.Clipboard;

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
				System.out.println(model.getUserWantsToUploadClips());
				System.out.println(model.isLoggedIn());
				if (model.getUserWantsToUploadClips()) {
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
				response = HTTPRequestUtil.addClipWithPassword("david@heik.info", "TestPW", clipboardText);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Strato: " + response);
			try {
				clipboarder.getModel().refreshEntries(true);
			} catch (Exception e) {
				System.out.println("Could not refresh.");
				e.printStackTrace();
			}
		});
	}

}
