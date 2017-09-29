package com.heikweber.clipboarder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Configuration {
	PropertiesConfiguration config;
	PropertiesConfigurationLayout layout;
	private String path;
	private double width;
	private double height;

	public Configuration(String path)
			throws FileNotFoundException, IOException, IllegalStateException, ConfigurationException {
		// props = new Properties();
		this.path = path;
		// File propertiesFile = new File(path);
		// try (BufferedInputStream bis = new BufferedInputStream(new
		// FileInputStream(propertiesFile))) {
		// props.load(bis);
		// }
		// load();

		File file = new File(path);

		config = new PropertiesConfiguration();
		layout = new PropertiesConfigurationLayout();
		layout.load(config, new InputStreamReader(new FileInputStream(file)));

		config.getString("token");
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
		if (!config.containsKey(key))
			throw new IllegalStateException("Key does not exist in configuration: " + key);
	}

	public void set(String key, String value) {
		config.setProperty(key, value);
		try {
			saveConfig();
		} catch (IOException | ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		return (String) config.getProperty(key);
	}

	public Boolean isEmpty(String key) {
		if (config.containsKey(key)) {
			return true;
		} else {
			return false;
		}
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

	public String getPath() {
		return path;
	}

	public void setWidth(double width) {
		this.width = width;
		set("width", Double.toString(width));
	}

	public void setHeight(double height) {
		this.height = height;
		set("height", Double.toString(height));
	}

	public void saveConfig() throws IOException, ConfigurationException {
		// FileOutputStream fr = new FileOutputStream(new File(this.getPath()));
		layout.save(config, new FileWriter(path, false));
	}
}
