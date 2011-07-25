package com.jwm.caronte.actions.content;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

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
import com.fastsearch.esp.search.result.IDocumentSummary;
import com.fastsearch.esp.search.result.IQueryResult;
import com.jwm.caronte.fast.content.ContentManager;
import com.jwm.fast.content.IndexingHelper;
import com.jwm.fast.content.IndexingHelperFactory;
import com.jwm.fast.content.exception.IndexingException;

public class Delete extends GenericAction {

	private Map<String,IndexingHelper> helpers = null;
	private ISearchFactory factory = null;
	private IQuery query = null;
	private String qrservers = null;
	private String contentDist = null;
	String searchview = null;
	
	@Override
	public String execute() throws CommandException {
		param("searchview");
		// 200.66.111.113
		// title:string("toolkit")
		query = buildQuery(param("searchbox"));	
		searchview = param("searchview");
		qrservers = getQRServers();
		contentDist = getDistributors();
		delete();
		return null;
	}

	private void
	delete(){
		IQueryResult result = search();
		int total = result.getDocCount();
		int counter = 0;
		
		for(int i=1; result.getDocCount() > 0 && i <= result.getDocCount() ;i++){
			counter ++;
			loading(counter, total - counter, total);
			IDocumentSummary doc = result.getDocument(i);
			try {				
				getHelpers().get(
						doc.getSummaryField("collection").getStringValue())
						.removeDocument(
								doc.getSummaryField("contentid")
										.getStringValue());
			} catch (IndexingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if(i == 1000){
				ContentManager.close(getHelpers().values());
				result = search();				
				helpers = null;
				getHelpers();
				i=0;
			}
		}		
		ContentManager.close(getHelpers().values());
		
	}
	
	private void loading(int i, int remaining, long total){
		System.out.print("\rDeleting [");
		if((i % 4) == 0){
			System.out.print("\\");
		}else if((i % 4) == 1){
			System.out.print("|");
		}else if((i % 4) == 2){
			System.out.print("/");
		}else if((i % 4) == 3){
			System.out.print("-");
		}

		System.out.print("]("+(((total - remaining) * 100)/total)+"%)");
	}
		
	@SuppressWarnings("unchecked")
	private Map<String, IndexingHelper> 
	getHelpers(){		
		if(helpers == null || helpers.isEmpty()){
			try {
				helpers = ContentManager.getHelperInstance(factory
						.getSearchView(searchview).getContentSpecification()
						.getCollections(), contentDist);
				return helpers;
			} catch (SearchEngineException e) {

				e.printStackTrace();
				
			}
			helpers = new HashMap<String, IndexingHelper>();
		}
		return helpers;
	}
	
	
	private IQueryResult
	search(){		
		try {
			if(factory == null){
				factory = SearchFactory.newInstance(
						new Properties(){{
							setProperty("com.fastsearch.esp.search.SearchFactory",
							"com.fastsearch.esp.search.http.HttpSearchFactory");
							setProperty("com.fastsearch.esp.search.http.qrservers",qrservers);
						}});
			}
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IQueryResult result = null;
		try {
			result = factory.getSearchView(searchview).search(query);
		} catch (SearchEngineException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	private IQuery
	buildQuery(String searchQuery){
		IQuery query = new Query(StringUtils.defaultIfEmpty(searchQuery, ""));
		query.setParameter(BaseParameter.HITS, 1000);
		return query;
	}
	
	
	@SuppressWarnings("unused")
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
