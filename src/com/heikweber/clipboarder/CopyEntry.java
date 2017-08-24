/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

/**
 * Object class for entries in copy history
 *
 * @author Philipp
 */
class CopyEntry {

	private int id;
	private String content = "";
	private boolean status;
	private Runnable listener = () -> {
	};

	CopyEntry(String content) {
		this.content = content;
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

}
