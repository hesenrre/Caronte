package com.jwm.caronte.actions;

import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

public class TestAction extends GenericAction {

	@Override
	public String execute() throws CommandException {
		// TODO Auto-generated method stub
		System.out.println(param("test"));
		return null;
	}

}
