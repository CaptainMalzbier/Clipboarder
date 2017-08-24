package com.heikweber.clipboarder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	private Properties props;
	private double width;
	private double height;

	public Configuration(String path) throws FileNotFoundException, IOException, IllegalStateException {
		props = new Properties();
		File propertiesFile = new File(path);
		try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(propertiesFile))) {
			props.load(bis);
		}
		load();
	}

	private void load() throws IllegalStateException {
		width = getDouble("width");
		height = getDouble("height");
		assertKey("trayicon");
		assertKey("stylePath");
		assertKey("icon");
		assertKey("alwaysontop");
	}

	private void assertKey(String key) {
		if (!props.containsKey(key))
			throw new IllegalStateException("Key does not exist in configuration: " + key);
	}

	public String get(String key) {
		return props.getProperty(key);
	}

	public int getInt(String key) throws IllegalStateException {
		String value = get(key);
		try {
			return Integer.parseInt(value, 10);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public double getDouble(String key) throws IllegalStateException {
		String value = get(key);
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}
}
