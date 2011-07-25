package com.jwm.caronte.actions.connection;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

public class RemoveCurrentServer extends GenericAction {

	@Override
	public String execute() throws CommandException {
		String serverport = StringUtils.defaultIfEmpty(param("server"),"");
		String tokens[] = serverport.split(":");
		Map<String, String> current = getCurrServers();
		if(current.containsKey(tokens[0])){
			current.remove(tokens[0]);
		}
		return null;
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
