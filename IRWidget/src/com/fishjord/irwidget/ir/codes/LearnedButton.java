package com.fishjord.irwidget.ir.codes;

public class LearnedButton extends CommandButton {

	public int id;
	private LearnedCommand  learnedCommand;
	
	public LearnedButton(String name, String display, String group,LearnedCommand learnedCommand)
	{
		this.name=name;
		this.display=display;
		this.group=group;
		this.learnedCommand=learnedCommand;
	}
	
	
	public LearnedCommand getLearnCommand()
	{
		return learnedCommand;
	}

}
