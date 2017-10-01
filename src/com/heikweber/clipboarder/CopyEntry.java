package com.heikweber.clipboarder;

/**
 * Object class for entries in copy history
 *
 * @author Philipp, David
 */

class CopyEntry {

	private int id;
	private String content = "";
	private String singleLineContent = "";
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

		content = content.replaceAll("[\\r\\n\\t]", "");

		setSingleLineContent(content.trim());

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

	public String getSingleLineContent() {
		return singleLineContent;
	}

	private void setSingleLineContent(String singleLineContent) {
		this.singleLineContent = singleLineContent;
	}

	@Override
	public String toString() {
		return String.format("CopyEntry(id=%s,short=%s,content=%s)", this.id, this.singleLineContent, this.content);
	}

}
