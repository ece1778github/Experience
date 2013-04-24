package com.example.ece1778project;

public class DBConnection 
{
	String url = "jdbc:mysql://192.168.0.196:3306/experience?user=root&password=root";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
