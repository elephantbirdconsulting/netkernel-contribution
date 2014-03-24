package com.elbeesee.triplestore.httpclient;

/**
 *
 * Elephant Bird Consulting - triplestore http client.
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
import org.netkernel.layer0.representation.impl.HDSBuilder;

/**
 * 
 * SPARQL Accessor.
 *
 * @param  database  database name
 * @param  query  sparql query
 * @param  accept  accept header
 * @param  expiry  expiry 
 * @param  credentials  http credentials 
 * 
 */

public class SPARQLAsyncAccessor extends StandardAccessorImpl {
	public SPARQLAsyncAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("database",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("query",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("accept",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("expiry",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("credentials",null,null,new Class[] {IHDSNode.class}));
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

		// query
		String aQuery = null;
		if (aContext.getThisRequest().argumentExists("query")) {
			try {
				aQuery = aContext.source("arg:query", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - query - argument");
			}
		} // no else needed, the grammar enforces the presence of a query argument
		//
		
		// accept
		String aAccept = null;
		if (aContext.getThisRequest().argumentExists("accept")) {
			try {
				aAccept = aContext.source("arg:accept", String.class);
			}
			catch (Exception e){
				throw new Exception("the request does not have a valid - accept - argument");
			}
		} // no else needed, the grammar enforces the presence of an accept argument
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
		
		// preparations
	    HDSBuilder vHeaders = new HDSBuilder();
        vHeaders.pushNode("Accept",aAccept);

	    HDSBuilder vBody = new HDSBuilder();
        vBody.pushNode("query", aQuery);
        
        String vHost = (String)aCredentials.getFirstValue("/httpCredentials/host");
        String vPort = (String)aCredentials.getFirstValue("/httpCredentials/port");
        //
        
        // triplestore request
		INKFRequest triplestorerequest = aContext.createRequest("active:post");
		triplestorerequest.addArgumentByValue("url", "http://" + vHost + ":" + vPort + "/" + aDatabase + "/query");
		triplestorerequest.addArgumentByValue("headers",vHeaders.getRoot());
		triplestorerequest.addArgumentByValue("nvp",vBody.getRoot());
		triplestorerequest.addArgumentByValue("credentials",aCredentials);
        triplestorerequest.setHeader("exclude-dependencies",true);
    	Object vTripleStoreResult = aContext.issueRequest(triplestorerequest);        
        //
        
		// response
		INKFResponse vResponse = aContext.createResponseFrom(vTripleStoreResult);
		vResponse.setExpiry(INKFResponse.EXPIRY_MIN_CONSTANT_DEPENDENT, System.currentTimeMillis() + aExpiry);
		vResponse.setMimeType(aAccept);
		//		
	}
}
