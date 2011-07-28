package com.jwm.caronte.fast.content;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fastsearch.esp.search.ConfigurationException;
import com.fastsearch.esp.search.ISearchFactory;
import com.fastsearch.esp.search.SearchEngineException;
import com.fastsearch.esp.search.SearchFactory;
import com.fastsearch.esp.search.query.BaseParameter;
import com.fastsearch.esp.search.query.IQuery;
import com.fastsearch.esp.search.query.Query;
import com.fastsearch.esp.search.result.IDocumentSummary;
import com.fastsearch.esp.search.result.IQueryResult;
import com.jwm.fast.content.IndexingHelper;
import com.jwm.fast.content.exception.IndexingException;

public class Eraser {

	private Map<String,IndexingHelper> helpers = null;
	private ISearchFactory factory = null;
	private IQuery query = null;
	private String qrservers = null;
	private String contentDist = null;
	private String searchview = null;
	
	private Eraser(String searchbox, String searchview, String qrservers,
			String contentDist) {
		this.query = buildQuery(searchbox);	
		this.searchview = searchview;
		this.qrservers = qrservers; 
		this.contentDist = contentDist;
	}

	public static void
	delete(String searchbox, String searchview, String qrservers, String contentDist){
		new Eraser(searchbox, searchview, qrservers, contentDist).delete();
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
	
	
	@SuppressWarnings("serial")
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
		
		String sq = StringUtils.defaultIfEmpty(searchQuery, "");
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
		query.setParameter(BaseParameter.HITS, 1000);
		return query;
	}
	
}
