package com.jwm.caronte.actions.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

import com.fastsearch.esp.search.ConfigurationException;
import com.fastsearch.esp.search.ISearchFactory;
import com.fastsearch.esp.search.SearchEngineException;
import com.fastsearch.esp.search.SearchFactory;
import com.fastsearch.esp.search.query.BaseParameter;
import com.fastsearch.esp.search.query.IQuery;
import com.fastsearch.esp.search.query.Query;
import com.fastsearch.esp.search.result.IQueryResult;

public class Search extends GenericAction {

	@Override
	public String execute() throws CommandException {
		
		ISearchFactory factory = null;
		try {
			factory = SearchFactory.newInstance(
					new Properties(){{
						setProperty("com.fastsearch.esp.search.SearchFactory",
						"com.fastsearch.esp.search.http.HttpSearchFactory");
						setProperty("com.fastsearch.esp.search.http.qrservers",getQRServers());
					}});
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		IQuery query = new Query(StringUtils.defaultIfEmpty(param("searchbox"), ""));
		query.setParameter(BaseParameter.HITS, 10);
		IQueryResult result = null;
		try {
			result = factory.getSearchView(param("searchview")).search(query);
			request("result", result);			
		} catch (SearchEngineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
		System.out.println("Servers to connect>"+res);
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
