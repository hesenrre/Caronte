package com.jwm.caronte.actions.search;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		
		String sq = StringUtils.defaultIfEmpty(param("searchbox"), "");
		Pattern pattern = Pattern.compile(".*\\$today([ ]*([-+]?)[ ]*([0-9]*)).*");
		Matcher matcher = pattern.matcher(sq);
		if(matcher.matches()){				
				if(matcher.group(1).trim().equals("")){
					sq = sq.replaceAll("\\$today", "range(min, "+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+")");
				}else{
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, (matcher.group(2).equals("-") ? -1 : 1) * Integer.valueOf(matcher.group(3)));
					sq = sq.replaceAll("\\$today([ ]*([-+]?)[ ]*([0-9]*))","range(min, "+new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime())+")");
				}
		}
		
		IQuery query = new Query(sq);
		System.out.println(query);
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
