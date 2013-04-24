package com.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;

public class DbConnection 
{
	public Connection getConnection() throws ServletException
	{
    	try
    	{
    		Class.forName("com.mysql.jdbc.Driver");
        	//return DriverManager.getConnection ("jdbc:mysql://142.1.17.47:3306/eXperience","root","123456");
        	return DriverManager.getConnection ("jdbc:mysql://192.168.0.175:3306/eXperience","root","123456");
        	//return DriverManager.getConnection ("jdbc:mysql://172.20.10.2:3306/eXperience?user=root&password=123456");
        	
    	}
    	catch (SQLException e) 
    	{
    		//flag=false;
    		//alert= "Error in SQL";
    		throw new ServletException("Servlet Could not display records.", e);
    	}
    	catch (ClassNotFoundException e)
    	{
    		//flag=false;
    		//alert="Error in ClassNotFound";
    		throw new ServletException("JDBC Driver not found.", e);
    	}        	
    	 	
	}
}
