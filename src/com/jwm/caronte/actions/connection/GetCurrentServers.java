package com.jwm.caronte.actions.connection;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

public class GetCurrentServers extends GenericAction {

	@Override
	public String execute() throws CommandException {	
		return new JSONArray(){{
			Map<String, String> servers = (Map<String, String>)application("currentServers");
			if(servers != null){
				try{
					for(final Entry<String, String> e : servers.entrySet()){
						put(new JSONObject(){{
							put("server", e.getKey());
							put("port", e.getValue());
						}});
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}}.toString();		
	}

}
