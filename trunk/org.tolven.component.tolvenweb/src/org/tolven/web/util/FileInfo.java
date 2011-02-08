package org.tolven.web.util;

public class FileInfo {

	private long timestamp;
	private String script;
	
	public FileInfo(String script, long timestamp) {
		super();
		this.script = script;
		this.timestamp = timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public String getScript() {
		return script;
	}
}
