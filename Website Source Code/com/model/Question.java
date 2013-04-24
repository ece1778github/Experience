package com.model;

public class Question 
{	
	int questionID;
	String question;
	int scoreValue;
	int numOfOption;
	
	public int getNumOfOption() {
		return numOfOption;
	}
	public void setNumOfOption(int numOfOption) {
		this.numOfOption = numOfOption;
	}
	public int getScoreValue() {
		return scoreValue;
	}
	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}	
	
	public int getQuestionID() 
	{
		return questionID;
	}
	public void setQuestionID(int questionID) 
	{
		this.questionID = questionID;
	}
	public String getQuestion() 
	{
		return question;
	}
	public void setQuestion(String question) 
	{
		this.question = question;
	}
	
}
