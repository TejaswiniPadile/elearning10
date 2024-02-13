package com.elearning.entity;

public class MaterialDetails {
	//private long id;
	private String detail;
	private String video;
	private String pdf;
	public MaterialDetails( String detail, String video, String pdf) {
		super();
		//this.id = id;
		this.detail = detail;
		this.video = video;
		this.pdf = pdf;
	}
//	public long getId() {
//		return id;
//	}
//	public void setId(long id) {
//		this.id = id;
//	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getVideo() {
		return video;
	}
	public void setVideo(String video) {
		this.video = video;
	}
	public String getPdf() {
		return pdf;
	}
	public void setPdf(String pdf) {
		this.pdf = pdf;
	}
	public MaterialDetails() {
		super();
	}
	
}
