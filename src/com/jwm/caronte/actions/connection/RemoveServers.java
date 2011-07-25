package com.jwm.caronte.actions.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

public class RemoveServers extends GenericAction {

	@Override
	public String execute() throws CommandException {
		for(String serverport: getServers()){
			String sp [] = serverport.split(":");
			if(getRegServers().containsKey(sp[0]) && getRegServers().get(sp[0]).equals(sp[1])){
				getRegServers().remove(sp[0]);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private List <String>
	getServers(){
		String[] servers = (servers = request().getParameterValues("server")) == null ? new String[0] : servers;
		return Arrays.asList(servers);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String>
	getRegServers(){
		if(application("servers") == null){
			application("servers", new TreeMap<String, String>());
		}
		return (Map<String, String>) application("servers");
	}
}
