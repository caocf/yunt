package com.bepo.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventDetailBean {
	HashMap<String, Object> metaAttributes = new HashMap<String, Object>();
	HashMap<String, Object> eevent = new HashMap<String, Object>();
	String userCode;
	String wfid;
	String duration_history;
	String duration;
	ArrayList<Map<String, Object>> opinions = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> eeventImg = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();

	public String getDuration_history() {
		return duration_history;
	}

	public void setDuration_history(String duration_history) {
		this.duration_history = duration_history;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public HashMap<String, Object> getMetaAttributes() {
		return metaAttributes;
	}

	public void setMetaAttributes(HashMap<String, Object> metaAttributes) {
		this.metaAttributes = metaAttributes;
	}

	public HashMap<String, Object> getEevent() {
		return eevent;
	}

	public void setEevent(HashMap<String, Object> eevent) {
		this.eevent = eevent;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
	}

	public ArrayList<Map<String, Object>> getOpinions() {
		return opinions;
	}

	public void setOpinions(ArrayList<Map<String, Object>> opinions) {
		this.opinions = opinions;
	}

	public ArrayList<Map<String, Object>> getEeventImg() {
		return eeventImg;
	}

	public void setEeventImg(ArrayList<Map<String, Object>> eeventImg) {
		this.eeventImg = eeventImg;
	}

	public ArrayList<Map<String, Object>> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Map<String, Object>> actions) {
		this.actions = actions;
	}

}
