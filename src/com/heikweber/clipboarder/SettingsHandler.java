package com.heikweber.clipboarder;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class SettingsHandler implements EventHandler<ActionEvent> {

	private int id;
	private Stage stage;
	private SceneModel model;
	private Node[] node;

	public SettingsHandler(int id, Stage stage, SceneModel model, Scene scene, Node[] node) {
		this.id = id;
		this.stage = stage;
		this.model = model;
		this.node = node;
	}

	@Override
	public void handle(ActionEvent event) {

		switch (id) {
		case 0:
			if (!model.isRecording()) {
				model.setRecording(true);
				model.config.set("uploadclips", "true");

			} else {
				model.setRecording(false);
				model.config.set("uploadclips", "false");
			}
			break;
		case 1:

			String defaultDir = model.config.get("stylePath");
			if (defaultDir.isEmpty()) {
				defaultDir = "c:/";
			}

			DirectoryChooser chooser = new DirectoryChooser();
			chooser.setTitle("JavaFX Projects");

			File defaultDirectory = new File(defaultDir);
			chooser.setInitialDirectory(defaultDirectory);
			File selectedDirectory = chooser.showDialog(stage);

			TextField dir = (TextField) node[0];
			dir.setText(selectedDirectory.getPath());
			node[0] = dir;

			model.config.set("stylePath", selectedDirectory.getPath());

			model.collectStyles((ComboBox<String>) node[1]);

			break;
		}

	}

}
