package com.sk.model;

public class Route {
	
	private String name;
	private String medium;
	private String action;
	private String source_dir;
	private String scan_ext;
	private String scan_ext_associated_file;
	private String target_dir;
	private String date_path;
	private String target_folder;
	private boolean active;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getSource_dir() {
		return source_dir;
	}
	public void setSource_dir(String source_dir) {
		this.source_dir = source_dir;
	}
	public String getScan_ext() {
		return scan_ext;
	}
	public void setScan_ext(String scan_ext) {
		this.scan_ext = scan_ext;
	}
	public String getScan_ext_associated_file() {
		return scan_ext_associated_file;
	}
	public void setScan_ext_associated_file(String scan_ext_associated_file) {
		this.scan_ext_associated_file = scan_ext_associated_file;
	}
	public String getTarget_dir() {
		return target_dir;
	}
	public void setTarget_dir(String target_dir) {
		this.target_dir = target_dir;
	}
	
	public String getDate_path() {
		return date_path;
	}
	public void setDate_path(String date_path) {
		this.date_path = date_path;
	}
	public String getTarget_folder() {
		return target_folder;
	}
	public void setTarget_folder(String target_folder) {
		this.target_folder = target_folder;
	}
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getMedium() {
		return medium;
	}
	public void setMedium(String medium) {
		this.medium = medium;
	}
	@Override
	public String toString() {
		return "Route [name=" + name + ", action=" + action + ", source_dir=" + source_dir + ", scan_ext=" + scan_ext
				+ ", scan_ext_associated_file=" + scan_ext_associated_file + ", target_dir=" + target_dir
				+ ", date_path=" + date_path + ", target_folder=" + target_folder + ", active=" + active + "]";
	}
	
}
