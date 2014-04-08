package com.elbeesee.triplestore.httpclient;

/**
 *
 * Elephant Bird Consulting - triplestore http client..
 * 
 * @author Tom Geudens. 2014/01/22.
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
 * Reconcile Accessor.
 *
 * @param  database  database name
 * @param  expiry  expiry 
 * @param  credentials  http credentials
 * 
 * @param  search  search term
 * @param  baseurl  base url
 * @param  limit  optional limit for the search
 * @param  type  optional type constraint for the search 
 * 
 */

public class ReconcileLARQAccessor extends StandardAccessorImpl {
	public ReconcileLARQAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(String.class);
		this.declareArgument(new SourcedArgumentMetaImpl("database",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("expiry",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("credentials",null,null,new Class[] {IHDSNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("search",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("baseurl",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("limit",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("type",null,null,new Class[] {String.class}));
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
		
		// baseurl
		String aBaseURL = null;
		if (aContext.getThisRequest().argumentExists("baseurl")) {
			try {
				aBaseURL = aContext.source("arg:baseurl", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - baseurl - argument");
			}
		} // no else needed, the grammar enforces the presence of a baseurl argument
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
		
		// type
		String aType = null;
		if (aContext.getThisRequest().argumentExists("type")) {
			try {
				aType = aContext.source("arg:type", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - type - argument");
			}
		} // no else needed, type is optional but we'll use the null to good effect
		//
		
		StringBuilder vResult = new StringBuilder("\"result\": [");
		
		String vTypeFilterClause = null;
		if (aType == null) {
			vTypeFilterClause = "";
		}
		else {
			INKFRequest typefilter = aContext.createRequest("active:freemarker");
			typefilter.addArgument("operator", "res:/resources/freemarker/typefilterclause.freemarker");
			typefilter.addArgumentByValue("type", aType);
		    typefilter.setRepresentationClass(String.class);
			vTypeFilterClause = (String)aContext.issueRequest(typefilter);			
		}
		
		String vQuery = null;
		INKFRequest reconcilequery = aContext.createRequest("active:freemarker");
		reconcilequery.addArgument("operator", "res:/resources/freemarker/reconcilelarq.freemarker");
		reconcilequery.addArgumentByValue("typefilterclause", vTypeFilterClause);
		reconcilequery.addArgumentByValue("search", aSearch);
		reconcilequery.addArgumentByValue("limit", aLimitString);
		reconcilequery.addArgumentByValue("baseurl", aBaseURL);
		reconcilequery.setRepresentationClass(String.class);
		vQuery = (String)aContext.issueRequest(reconcilequery);			

		INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
		sparqlrequest.addArgumentByValue("database", aDatabase);
		sparqlrequest.addArgumentByValue("query",vQuery);
		sparqlrequest.addArgumentByValue("accept", "application/sparql-results+xml");
		sparqlrequest.addArgumentByValue("expiry", aExpiry);
		sparqlrequest.addArgumentByValue("credentials", aCredentials);
		Object vSparqlResult = aContext.issueRequest(sparqlrequest);
		
		INKFRequest xsltcrequest = aContext.createRequest("active:xsltc");
		xsltcrequest.addArgumentByValue("operand", vSparqlResult);
		xsltcrequest.addArgument("operator", "res:/resources/xsl/sparqlresult_to_json.xsl");
		xsltcrequest.addArgumentByValue("search", aSearch);
		xsltcrequest.setRepresentationClass(String.class);
		String vJSONResult = (String)aContext.issueRequest(xsltcrequest);

		vResult.append(vJSONResult);
		vResult.append("]");

		// response
		INKFResponse vResponse = aContext.createResponseFrom(vResult.toString());
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
		//		
	}
}
