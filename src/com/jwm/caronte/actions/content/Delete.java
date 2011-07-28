package com.jwm.caronte.actions.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

import com.fastsearch.esp.search.ISearchFactory;
import com.fastsearch.esp.search.query.IQuery;
import com.jwm.caronte.fast.content.Eraser;

public class Delete extends GenericAction {
	
	@Override
	public String execute() throws CommandException {
		param("searchview");
		Eraser.delete(param("searchbox"), param("searchview"), getQRServers(), getDistributors());
		return null;
	}
	
	private String
	getDistributors(){
		String[] servers = (servers = request().getParameterValues("contentDist")) == null ? new String[0] : servers;
		return StringUtils.join(Arrays.asList(servers).iterator(), ",");
	}
	
	private String
	getQRServers(){
		@SuppressWarnings("serial")
		String res = 
		StringUtils.join(new ArrayList<String>(){{
			for(Entry<String,String> e : getCurrServers().entrySet()){
				add(new StringBuffer(e.getKey()).append(":").append(e.getValue()).toString());
			}
		}}.iterator(), ",");
		
		return res;
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
