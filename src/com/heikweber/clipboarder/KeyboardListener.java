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

	// Inject dependency clipboarder
	public KeyboardListener(Clipboarder clipboarder) {
		this.clipboarder = clipboarder;
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_C) {
			if ((e.getModifiers() & NativeInputEvent.CTRL_L_MASK) != 0) {
				try {
					copyToClipboarder();
				} catch (HeadlessException | UnsupportedFlavorException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void copyToClipboarder() throws HeadlessException, UnsupportedFlavorException, IOException {
		Platform.runLater(() -> {
			final String clipboardText;
			Clipboard clipboard = Clipboard.getSystemClipboard();
			clipboardText = clipboard.getString();
			System.out.println(clipboardText);

			if (clipboardText == null)
				return;

			// TODO counter hochzählen bis 10
			// In SceneModel -> getSelectedButton(), setSelectedButton(int), dann bei
			// Knopfdruck selektion enstprechend setzen
			// Neue Methoden im SceneModel anlegen für das Hinzufügen eines Button
			// mit Counter?

			CopyEntry latestEntry = clipboarder.getModel().getLatestCopyEntry();
			if (latestEntry != null && latestEntry.getContent().equals(clipboardText))
				return;

			clipboarder.getModel().addCopyEntry(clipboardText);
		});
	}

}
