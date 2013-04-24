package com.model;


public class Project 
{
	int idProject;
	String projName;
	String Type;
	Experimenter Exp;
	
	public String getType() {
		return Type;
	}
	public void setType(String type) {
		Type = type;
	}	
	public Experimenter getExp() {
		return Exp;
	}
	public void setExp(Experimenter exp) {
		Exp = exp;
	}
	public int getIdProject() {
		return idProject;
	}
	public void setIdProject(int idProject) {
		this.idProject = idProject;
	}
	public String getProjName() {
		return projName;
	}
	public void setProjName(String projName) {
		this.projName = projName;
	}
}
