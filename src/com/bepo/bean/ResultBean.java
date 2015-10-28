package com.bepo.bean;

public class ResultBean {

	private String status;// ×´Ì¬
	private String info;// ·µ»ØÐÅÏ¢

	public ResultBean() {
		super();
	}

	@Override
	public String toString() {
		return "ResultBean [status=" + status + ", info=" + info + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
