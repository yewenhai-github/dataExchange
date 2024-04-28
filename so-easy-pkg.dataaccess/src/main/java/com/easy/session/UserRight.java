package com.easy.session;

/**
 * so-easy private
 * 
 * @author yewh 2015-10-20
 * 
 * @version 7.0.0
 * 
 */
public class UserRight {
	private String CommandName;
	private String IsEnabled;
	private String Disabled;
	
	public String getCommandName() {
		return CommandName;
	}
	public void setCommandName(String commandName) {
		CommandName = commandName;
	}
	public String getIsEnabled() {
		return IsEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		IsEnabled = isEnabled;
	}
	public String getDisabled() {
		return Disabled;
	}
	public void setDisabled(String disabled) {
		Disabled = disabled;
	}
}
