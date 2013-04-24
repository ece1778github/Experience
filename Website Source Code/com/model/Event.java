package com.model;

public class Event
{
	int idEvent;
	String eventName;
	int numOFQst;
	Project proj;
	
	public Project getProj() {
		return proj;
	}
	public void setProj(Project proj) {
		this.proj = proj;
	}
	public int getIdEvent() {
		return idEvent;
	}
	public void setIdEvent(int idEvent) {
		this.idEvent = idEvent;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public int getNumOFQst() {
		return numOFQst;
	}
	public void setNumOFQst(int numOFQst) {
		this.numOFQst = numOFQst;
	}
	
}
