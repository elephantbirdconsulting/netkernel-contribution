package com.elbeesee.stardog;

/**
 *
 * Elephant Bird Consulting - Stardog.
 * 
 * @author Tom Geudens. 2013/11/29.
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

public class SPARQLAccessor extends StandardAccessorImpl {
	public SPARQLAccessor() {
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
        
        // setting up the session
        INKFRequest staterequest = aContext.createRequest("active:httpState");
        staterequest.addArgumentByValue("credentials", aCredentials);
        staterequest.setVerb(INKFRequestReadOnly.VERB_NEW);
        staterequest.setHeader("exclude-dependencies",true);
        String vId = (String)aContext.issueRequest(staterequest);
        //
        
        // stardog request
		INKFRequest stardogrequest = aContext.createRequest("active:httpPost");
		stardogrequest.addArgument("url", "http://" + vHost + ":" + vPort + "/" + aDatabase + "/query");
		stardogrequest.addArgumentByValue("headers",vHeaders.getRoot());
		stardogrequest.addArgument("state", vId);
		stardogrequest.addArgumentByValue("nvp",vBody.getRoot());
        stardogrequest.setHeader("exclude-dependencies",true);
    	Object vStardogResult = aContext.issueRequest(stardogrequest);        
        //
        
        // tearing down the session
		INKFRequest removestaterequest = aContext.createRequest(vId);
		removestaterequest.setVerb(INKFRequestReadOnly.VERB_DELETE);
		removestaterequest.setHeader("exclude-dependencies",true);
		aContext.issueRequest(removestaterequest);
        //
        
		// response
		INKFResponse vResponse = aContext.createResponseFrom(vStardogResult);
		vResponse.setExpiry(INKFResponse.EXPIRY_MIN_CONSTANT_DEPENDENT, System.currentTimeMillis() + aExpiry);
		vResponse.setMimeType(aAccept);
		//		
	}
}
