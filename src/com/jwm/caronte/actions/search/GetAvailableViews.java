package com.jwm.caronte.actions.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.lainsoft.forge.flow.nav.CommandException;
import org.lainsoft.forge.flow.nav.GenericAction;

import com.fastsearch.esp.search.ConfigurationException;
import com.fastsearch.esp.search.ISearchFactory;
import com.fastsearch.esp.search.SearchEngineException;
import com.fastsearch.esp.search.SearchFactory;

public class GetAvailableViews extends GenericAction {

	@Override
	public String execute() throws CommandException {
		String viewList[];
		try {
			final ISearchFactory factory = SearchFactory.newInstance(
					new Properties(){{
						setProperty("com.fastsearch.esp.search.SearchFactory",
						"com.fastsearch.esp.search.http.HttpSearchFactory");
						setProperty("com.fastsearch.esp.search.http.qrservers",getQRServers());
					}});

			viewList = null;

			return new JSONArray(
					new TreeSet<String>(){{
						for(String view : factory.getSearchViewList()){							
							add(view);
						}
					}}).toString();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (SearchEngineException e) {
			e.printStackTrace();
		}

		return null;
		//return new JSONArray(new TreeSet<String>(Arrays.asList(viewList))).toString();
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
