package com.elbeesee.triplestore.httpclient;

/**
 *
 * Elephant Bird Consulting - triplestore http client..
 * 
 * @author Tom Geudens. 2014/05/10.
 * 
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

/**
 * Processing Imports.
 */
import org.netkernel.layer0.representation.IHDSNode;

/**
 * 
 * Keyword Search Accessor.
 *
 * @param  database  database name
 * @param  expiry  expiry 
 * @param  credentials  http credentials 
 * @param  accept  accept header
 * 
 * @param  search  search term
 * @param  limit  optional limit for the search
 * 
 */

public class KeywordSearchLARQAccessor extends StandardAccessorImpl {
	public KeywordSearchLARQAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("database",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("expiry",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("credentials",null,null,new Class[] {IHDSNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("accept",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("search",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("limit",null,null,new Class[] {Long.class}));
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		// database
		String aDatabase = null;
		if (aContext.getThisRequest().argumentExists("database")) {
			try {
				aDatabase = aContext.source("arg:database", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - database - argument");
			}
		} // no else needed, the grammar enforces the presence of a database argument
		//
		
		// expiry
		Long aExpiry = null;
		if (aContext.getThisRequest().argumentExists("expiry")) {
			try {
				aExpiry = aContext.source("arg:expiry", Long.class);
			}
			catch (Exception e){
				try {
					String aExpiryString = aContext.source("arg:expiry", String.class);
					aExpiry = Long.parseLong(aExpiryString);
				}
				catch (Exception e2) {
					throw new Exception("the request does not have a valid - expiry - argument");
				}
			}
		} // no else needed, the grammar enforces the presence of an expiry argument
		//

		// credentials
		IHDSNode aCredentials = null;
		if (aContext.getThisRequest().argumentExists("credentials")) {
			try {
				aCredentials = aContext.source("arg:credentials", IHDSNode.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - credentials - argument");
			}
		} // no else needed, the grammar enforces the presence of a credentials argument
		//
		
		// accept
		String aAccept = null;
		try {
			aAccept = aContext.source("arg:accept", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - accept - argument");
		}
		if (aAccept.equals("")) {
			throw new Exception("the request does not have a valid - accept - argument");
		}
		//
		
		// search
		String aSearch = null;
		if (aContext.getThisRequest().argumentExists("search")) {
			try {
				aSearch = aContext.source("arg:search", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - search - argument");
			}
		} // no else needed, the grammar enforces the presence of a search argument
		//
		
		// limit
		Long aLimit = null;
		String aLimitString = null;
		if (aContext.getThisRequest().argumentExists("limit")) {
			try {
				aLimit = aContext.source("arg:limit", Long.class);
				aLimitString = aLimit.toString();
			}
			catch (Exception e){
				try {
					aLimitString = aContext.source("arg:limit", String.class);
					aLimit = Long.parseLong(aLimitString);
				}
				catch (Exception e2) {
					aLimitString = "";
					aLimit = 0L;
				}
			}
		}
		else {
			aLimit = 0L;
			aLimitString = "";
		}
		//
		
		String vQuery = null;
		INKFRequest keywordsearchquery = aContext.createRequest("active:freemarker");
		keywordsearchquery.addArgument("operator", "res:/resources/freemarker/keywordsearchlarq.freemarker");
		keywordsearchquery.addArgumentByValue("search", aSearch);
		keywordsearchquery.addArgumentByValue("limit", aLimitString);
		keywordsearchquery.setRepresentationClass(String.class);
		vQuery = (String)aContext.issueRequest(keywordsearchquery);
		
		INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
		sparqlrequest.addArgumentByValue("database", aDatabase);
		sparqlrequest.addArgumentByValue("query", vQuery);
		sparqlrequest.addArgumentByValue("accept", aAccept);
		sparqlrequest.addArgumentByValue("expiry", aExpiry);
		sparqlrequest.addArgumentByValue("credentials", aCredentials);
		Object vSparqlResult = aContext.issueRequest(sparqlrequest);
		
		INKFResponse vResponse = aContext.createResponseFrom(vSparqlResult);
		vResponse.setMimeType(aAccept);
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
