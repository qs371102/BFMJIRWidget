package com.fishjord.irwidget.ir.codes;

public class LearnedButton extends CommandButton {

	public int id;
	private LearnedCommand  learnedCommand;
	private int robotId;
	
	public LearnedButton(String name, String display, String group,LearnedCommand learnedCommand,int robotId)
	{
		this.name=name;
		this.display=display;
		this.group=group;
		this.learnedCommand=learnedCommand;
		this.robotId=robotId;
	}
	
	
	public LearnedCommand getLearnCommand()
	{
		return learnedCommand;
	}

	public int getRobotId()
	{
		return this.robotId;
	}
	
}
