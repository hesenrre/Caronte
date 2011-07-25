package com.jwm.caronte.actions.connection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

public class UpdateCurrentServers extends GenericAction {

	@Override
	public String execute() throws CommandException {
		System.out.println("UpdateCurrentServers method called");
		for(String serverport: getServers()){
			System.out.println("processing server:"+serverport);
			String sp [] = serverport.split(":");
			getCurrServers().put(sp[0], sp[1]);
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
	getCurrServers(){
		if(application("currentServers") == null){
			application("currentServers", new TreeMap<String, String>());
		}
		return (Map<String, String>) application("currentServers");
	}

}
