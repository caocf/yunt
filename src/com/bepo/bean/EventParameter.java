package com.bepo.bean;

import java.util.HashMap;
import java.util.Map;

public class EventParameter {

	public String eventTitle;
	public String eventSuqiuren;
	public Map<String, String> eventFromMap = new HashMap<String, String>();
	public EventTypeBean eventType = new EventTypeBean();
	public String eventStatus;
	public Map<String, String> eventYesMap = new HashMap<String, String>();

	public String eventStartTime0;
	public String eventStartTime1;
	public String eventEndTime0;
	public String eventEndTime1;

	public String eventSearchUrl;

	public String getEventSearchUrl() {
		return eventSearchUrl;
	}

	public void setEventSearchUrl(String eventSearchUrl) {
		this.eventSearchUrl = eventSearchUrl;
	}

	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	public String getEventSuqiuren() {
		return eventSuqiuren;
	}

	public void setEventSuqiuren(String eventSuqiuren) {
		this.eventSuqiuren = eventSuqiuren;
	}

	public Map<String, String> getEventFromMap() {
		return eventFromMap;
	}

	public void setEventFromMap(Map<String, String> eventFromMap) {
		this.eventFromMap = eventFromMap;
	}

	public Map<String, String> getEventYesMap() {
		return eventYesMap;
	}

	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	public EventTypeBean getEventType() {
		return eventType;
	}

	public void setEventType(EventTypeBean eventType) {
		this.eventType = eventType;
	}

	public void setEventYesMap(Map<String, String> eventYesMap) {
		this.eventYesMap = eventYesMap;
	}

	public String getEventStartTime0() {
		return eventStartTime0;
	}

	public void setEventStartTime0(String eventStartTime0) {
		this.eventStartTime0 = eventStartTime0;
	}

	public String getEventStartTime1() {
		return eventStartTime1;
	}

	public void setEventStartTime1(String eventStartTime1) {
		this.eventStartTime1 = eventStartTime1;
	}

	public String getEventEndTime0() {
		return eventEndTime0;
	}

	public void setEventEndTime0(String eventEndTime0) {
		this.eventEndTime0 = eventEndTime0;
	}

	public String getEventEndTime1() {
		return eventEndTime1;
	}

	public void setEventEndTime1(String eventEndTime1) {
		this.eventEndTime1 = eventEndTime1;
	}

}
