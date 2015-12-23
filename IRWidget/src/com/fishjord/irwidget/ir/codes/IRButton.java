package com.fishjord.irwidget.ir.codes;

public class IRButton extends CommandButton{
	
	private final IRCommand command;

	
	public IRButton(String name, String display, String group, IRCommand command) {
		this.name = name;
		this.display = display;
		this.command = command;
		this.group = group;
	}

	public IRCommand getCommand() {
		return command;
	}
	
	
}
