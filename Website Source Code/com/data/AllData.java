package com.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;

import com.connection.DbConnection;
import com.model.Event;
import com.model.Experimenter;
import com.model.Project;
import com.model.Question;
import com.model.User;

public class AllData 
{
	//notes: /eXperienceV1/HomeServlet?action=modifyProj&value='${user.getProj().projName}'"
	DbConnection dbcon = new DbConnection();
	Connection con = null;
	Statement stmt = null;
	ResultSet rsExp = null;	
	ResultSet rsProj = null;
	ResultSet rsUser = null;
	Experimenter exp;
	User user;
	Project proj;
	ArrayList<Project> projects;
	ArrayList<User> users;
	
	public Experimenter getExpInfo(String un, String pw)
	{		
		try
		{
			//Create connection to Database to check if there is a acoount for this Experimenter
			con = dbcon.getConnection();
			stmt = con.createStatement();
			if(stmt != null)
			{
				String queryExp=  "SELECT * " +
	        			"FROM Experimenter Where email='" + un + "' AND passWord='" + pw + "'" ;				
				rsExp = stmt.executeQuery(queryExp);
				//get expInfo
				//get Exp's projects
				//get users who uses this specific projects.
				try{					
						while(rsExp.next())
						{
			        		 exp = new Experimenter();
			                 exp.setEmail(rsExp.getString("email"));
			                 exp.setpWrod(rsExp.getString("password"));
			                 exp.setIdExp(rsExp.getInt("idExp"));
			                 exp.setfName(rsExp.getString("fName"));
			                 exp.setlName(rsExp.getString("lName"));
			                 exp.setRole(rsExp.getString("role"));		 	             
		                 }
						rsExp = null;				
						}
							
				catch(NullPointerException e)
				{
					return null;
				}
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			
			 closeConnections();
		}
        return exp;
	}
	
	public ArrayList<User> getUsersProj(ArrayList<Project> projs, int idExp)
	{	
		try
		{
			con = dbcon.getConnection();
			stmt = con.createStatement();
			
			String queryUser = "select * from User where User.Project_Experimenter_idExp=" +
					idExp;			
			
			users = new ArrayList<User>();
			
			//get users registered on the same project 
			//then add the project to the experimenter
			
			rsUser = stmt.executeQuery(queryUser);
			while(rsUser.next())
			{
				user = new User();
				user.setfName(rsUser.getString("fName"));
				user.setlName(rsUser.getString("lName"));
				user.setEmail(rsUser.getString("email"));
				user.setpWrod(rsUser.getString("passWord"));
				user.setIdUser(rsUser.getInt("idUser"));
				user.setAddress(rsUser.getString("address"));
				for(int i = 0; i<projs.size(); i++)
				{
					//check if user.projid == exp.projid, 
					if(projs.get(i).getIdProject() == rsUser.getInt("Project_idProject"))
					{
						user.setProj(projs.get(i));
					}
				}
				users.add(user);
			}
		}			
		catch(NullPointerException | SQLException | ServletException e)
		{
			return null;
		}
	
		finally 
		{
			
			 closeConnections();
		}
		return users;
		
	}
	public ArrayList<Event>  getEventNamesAndInfo()
	{
		Event event;
		ArrayList<Event> events = new ArrayList<Event>();
		Statement stmt = null;
		ResultSet rsEvent = null;
		
		try
		{
			con = dbcon.getConnection();
			stmt = con.createStatement();
			
			String queryEvent = "SELECT * FROM event as e LEFT JOIN Project_has_Event pe ON e.idEvent = pe.event_idEvent "
					+ "LEFT JOIN project p ON pe.Project_idProject = p.idProject ORDER BY e.idevent";
			rsEvent = stmt.executeQuery(queryEvent);
			while(rsEvent.next())
			{
				event =new Event();
				event.setEventName(rsEvent.getString("eventName"));
				event.setNumOFQst(rsEvent.getInt("numOFQst"));
				event.setIdEvent(rsEvent.getInt("idEvent"));
				events.add(event);
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			 closeConnections();
		}
		return events;
	}
	public ArrayList<Question>  getQuestions()
	{
		Question qst;
		ArrayList<Question> qsts = new ArrayList<Question>();
		Statement stmt = null;
		ResultSet rsEvent = null;
		
		try
		{
			con = dbcon.getConnection();
			stmt = con.createStatement();
			
			String queryQst = "SELECT * FROM question";
			rsEvent = stmt.executeQuery(queryQst);
			while(rsEvent.next())
			{
				qst =new Question();
				qst.setQuestion(rsEvent.getString("text"));
				qst.setQuestionID(rsEvent.getInt("idQst"));
				qst.setNumOfOption(rsEvent.getInt("NumberOFOptions"));
				qsts.add(qst);
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			 closeConnections();
		}
		return qsts;
	}
	public ArrayList<Project> getProjs(Experimenter expe) throws SQLException
	{
		try {
				con = dbcon.getConnection();
				
				stmt = con.createStatement();
				
				String queryProj ="select * from Project where Project.Experimenter_idExp=" 
		        + expe.getIdExp() + " order by Project.projName";
				rsProj = stmt.executeQuery(queryProj);
				
				projects = new ArrayList<Project>();
				
				while(rsProj.next())
				{//if proj.idexp equals user.project_exp_idEexp,setProjtp exp
					proj = new Project();
					proj.setExp(expe);
					proj.setIdProject(rsProj.getInt("idProject"));
					proj.setProjName(rsProj.getString("projName"));
					proj.setType(rsProj.getString("Type"));
					projects.add(proj);
				}
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}
	public void AssignProjtoUser(Project proj, User user)
	{
		//Insert selected project'id to the user's table.
	}
	public void setProjectDetails()
	{
		
	}
	public void closeConnections()
	{
		try
		{
			if(rsProj != null) 
			{
				rsProj.close();
				rsProj = null;
			}
			if(rsExp != null) 
			{
				rsExp.close();
				rsExp = null;
			}
			if(stmt != null) {
				stmt.close();
				stmt = null;
			}
			if(con!= null)
			{
				con.close();
				con = null;
			}
		}
		catch(SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
}