package com.heikweber.clipboarder;

/**
 * Object class for entries in copy history
 *
 * @author Philipp, David
 */

class CopyEntry {

	private int id;
	private String content = "";
	private String shortContent = "";
	private boolean status;
	private Runnable listener = () -> {
	};

	CopyEntry(String content, String id) {
		setContent(content);
		setId(Integer.parseInt(id));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		listener.run();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content != null ? content : "";

		// shorten printed content of CopyEntry
		if (content.length() >= 12) {
			content = content.substring(0, 12) + "...";
		}

		setShortContent(content.trim());

		listener.run();
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
		listener.run();
	}

	public void addListener(Runnable listener) {
		this.listener = listener;
	}

	public String getShortContent() {
		return shortContent;
	}

	public void setShortContent(String shortContent) {
		this.shortContent = shortContent;
	}

}
