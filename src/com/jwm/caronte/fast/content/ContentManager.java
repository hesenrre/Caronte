package com.jwm.caronte.fast.content;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jwm.fast.content.IndexingHelper;
import com.jwm.fast.content.IndexingHelperFactory;
import com.jwm.fast.content.exception.IndexingException;

public class ContentManager {
		
	public static IndexingHelper
	getHelperInstance(String collection, String distributors){
		try {
			IndexingHelper indexer =
				IndexingHelperFactory.newInstance(distributors, collection, false);
			indexer.configureFeeder(new HashMap<String,String>(){
				private static final long serialVersionUID = 8169093772959603462L;
				{
					put(IndexingHelper.WAIT_FOR_DOCS_TO_BE_SEARCHABLE, "true");
					put(IndexingHelper.DOCUMENT_TIMEOUT_MIN, "1");
					put(IndexingHelper.MAX_DOCS_IN_BATCH, "1");
				}});
			return indexer;
		} catch (IndexingException e) {			
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("serial")
	public static Map<String, IndexingHelper>
	getHelperInstance(final List<String> collections, final String distributors){
		return new HashMap<String, IndexingHelper>(){{
			for(String coll : collections){
				put(coll, getHelperInstance(coll, distributors));
			}
		}};
	}
	
	public static void
	close(Collection<IndexingHelper> helpers){
		for(IndexingHelper helper: helpers){
			close(helper);
		}
	}
	
	public static void
	close(IndexingHelper helper){
		try {
			helper.waitForCompletition();
		} catch (IndexingException e) {
			e.printStackTrace();
		}
		helper.close();
		helper.shutdown();
	}
}
