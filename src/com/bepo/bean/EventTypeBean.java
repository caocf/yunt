package com.bepo.bean;

public class EventTypeBean {

	private String CODE;// ×ÓID
	private String NAME_C;// Ãû³Æ
	private String PARENT_CODE;// ¸¸ID

	public EventTypeBean() {
		super();
	}

	public EventTypeBean(String cODE, String nAME_C, String pARENT_CODE) {
		super();
		CODE = cODE;
		NAME_C = nAME_C;
		PARENT_CODE = pARENT_CODE;
	}

	@Override
	public String toString() {
		return "EventTypeBean [CODE=" + CODE + ", NAME_C=" + NAME_C + ", PARENT_CODE=" + PARENT_CODE + "]";
	}

	public void setCODE(String CODE) {
		this.CODE = CODE;
	}

	public String getCODE() {
		return this.CODE;
	}

	public void setNAME_C(String NAME_C) {
		this.NAME_C = NAME_C;
	}

	public String getNAME_C() {
		return this.NAME_C;
	}

	public void setPARENT_CODE(String PARENT_CODE) {
		this.PARENT_CODE = PARENT_CODE;
	}

	public String getPARENT_CODE() {
		return this.PARENT_CODE;
	}

}
