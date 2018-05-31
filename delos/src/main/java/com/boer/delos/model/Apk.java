package com.boer.delos.model;

import java.util.LinkedList;

public class Apk {
	
	private String type;
	private String version;
	private String versionName;

	private String name;
	private String description;
	private String file_url;
	private String new_function;
	private LinkedList<String> fileUrlList = new LinkedList<String>();
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version.replace("\"", "");
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName.replace("\"", "");
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description.replace("\"", "");
	}
	public String getFile_url() {
		return file_url;
	}
	public void setFile_url(String file_url) {
		this.file_url = file_url.replace("\"", "");
	}
	public String getNew_function() {
		return new_function;
	}
	public void setNew_function(String new_function) {
		this.new_function = new_function.replace("\"", "");
		this.new_function = this.new_function.replace("|", "<br>");
	}
	public LinkedList<String> getFileUrlList() {
		return fileUrlList;
	}
	public void setFileUrlList(LinkedList<String> fileUrlList) {
		this.fileUrlList = fileUrlList;
	}
	
	

}
