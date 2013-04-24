package com.coreServlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.data.AllData;
import com.model.Event;
import com.model.Experimenter;
import com.model.Project;
import com.model.Question;
import com.model.User;

@WebServlet("/HomeServlet")
public class HomeServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	String loc;
	AllData allData;
	Experimenter exp = new Experimenter();
	ArrayList<User> users;
	ArrayList<Project> projects;
	ArrayList<Event> events;
	ArrayList<Question> qsts;
	String alert;
	boolean flag;
    public HomeServlet() {
        super();
    }

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		try
		{
			//based on the user's action. we request the jsp page.		
			String action = request.getParameter("action");
			HttpSession session = request.getSession();
			
			allData = new AllData();
			if(action.equals("signIn"))
			{
				String un = request.getParameter("userName");
				String pw = request.getParameter("passWord");
				
				checkLoginValue(un, pw);
				
				if(flag)
				{
					//get the experimenter info and his projects and users, and then 
					//check the Project.project_id w user.project_id to attach it to the user registered under his name.
					projects = new ArrayList<Project>();
					
					
					exp = allData.getExpInfo(un, pw);
					//show all the projects and the users.
					if(exp != null)
					{
						 projects = allData.getProjs(exp);
						
						 users = new ArrayList<User>();
						 users = allData.getUsersProj(projects, exp.getIdExp());
						 alert="Welcome " + exp.getfName() + " " + exp.getlName();
						 
						 session.setAttribute("exp", exp);
						 session.setAttribute("users", users);
						 
						 request.setAttribute("users", users);						 
						 request.setAttribute("alert", alert);
						 
						 RequestDispatcher rd = request.getRequestDispatcher("home.jsp");
						 rd.forward(request, response);	
					}
					else
					{
						alert = "Wrong entry, please try again!";
						request.setAttribute("alert", alert);
						
						RequestDispatcher rd = request.getRequestDispatcher("signIn.jsp");
						rd.forward(request, response);
					}
				}
				else
				{
					alert = "Empty Field, Please Enter you UserName and Passwrod!";
					request.setAttribute("alert", alert);
					
					RequestDispatcher rd = request.getRequestDispatcher("signIn.jsp");
					rd.forward(request, response);
				}
			
			}
			else if(action.equals("projBuilder"))
			{
				exp = (Experimenter) session.getAttribute("exp");
				users = (ArrayList<User>) session.getAttribute("users");
				events = (ArrayList<Event>) session.getAttribute("events");
				
				request.setAttribute("events", events);
				request.setAttribute("questions", qsts);
				request.setAttribute("exp", exp);
				request.setAttribute("users", users);
				RequestDispatcher rd = request.getRequestDispatcher("projBuilder.jsp");
				rd.forward(request, response);
			}
			else if(action.equals("curProj"))
			{
				exp = (Experimenter) session.getAttribute("exp");
				users = (ArrayList<User>) session.getAttribute("users");
				
				events = new ArrayList<Event>();
				events = allData.getEventNamesAndInfo();
				
				
				qsts = new ArrayList<Question>();
				qsts = allData.getQuestions();
				
				session.setAttribute("events", events);
				session.setAttribute("qsts", qsts);
				
				request.setAttribute("events", events);
				request.setAttribute("questions", qsts);
				request.setAttribute("exp", exp);
				request.setAttribute("users", users);
				RequestDispatcher rd = request.getRequestDispatcher("curProj.jsp");
				rd.forward(request, response);
			}
			else if(action.equals("modifyProj"))
			{

				exp = (Experimenter) session.getAttribute("exp");
				users = (ArrayList<User>) session.getAttribute("users");
				events = (ArrayList<Event>) session.getAttribute("events");
				//getting the selected project name to get all the events and
				//other info of it and pass it to curProj page to modify.
				//String selectedProj = request.getParameter("selectedProj");
				//String projN = request.getParameter("value").toString();
				
				ArrayList<Event> eventNames = new ArrayList<Event>();
				eventNames = allData.getEventNamesAndInfo();
				
				flag = true;
				
				request.setAttribute("flag", flag);
				request.setAttribute("eventsNames", eventNames);
				request.setAttribute("modifyProj", "modifyProj");
				session.setAttribute("eventNames", eventNames);
				RequestDispatcher rd = request.getRequestDispatcher("curProj.jsp");
				rd.forward(request, response);
			}
			else if(action.equals("userData"))
			{

				exp = (Experimenter) session.getAttribute("exp");
				users = (ArrayList<User>) session.getAttribute("users");
				events = (ArrayList<Event>) session.getAttribute("events");
				
				request.setAttribute("events", events);
				
				RequestDispatcher rd = request.getRequestDispatcher("userData.jsp");
				rd.forward(request, response);
			}
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException 
	{
		doGet(request, response);
	}
	
	public void checkLoginValue(String username, String password)
	{
		int ulength;
		int plength;
		ulength = username.length();
		plength = password.length();
		
		if(ulength > 0 && plength > 0)
		{
			flag = true;
		}
		else if(ulength == 0 && plength > 0)
	    {
	       alert = "Please Enter the UserID";
	    }
	    else if(plength == 0 && ulength > 0)
	    {
	        alert = "Please Enter the Password";
	    }
	    else if(ulength == 0 && plength == 0)
	    {
	        alert="Please Enter the UserID and Password";
	    }
				
	}

}
