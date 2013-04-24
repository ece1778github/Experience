package com.model;

import java.sql.Date;

public class User 
{
	int idUser;
	String lName;
	String fName;
	Date dob;
	String address;
	String email;
	String pWrod;
	Project Proj;

	public Project getProj() {
		return Proj;
	}
	public void setProj(Project proj) {
		Proj = proj;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	public String getlName() {
		return lName;
	}
	public void setlName(String lName) {
		this.lName = lName;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	/*public Date getDob() {
		return dob;
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}*/
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getpWrod() {
		return pWrod;
	}
	public void setpWrod(String pWrod) {
		this.pWrod = pWrod;
	}
	
	
	

}
