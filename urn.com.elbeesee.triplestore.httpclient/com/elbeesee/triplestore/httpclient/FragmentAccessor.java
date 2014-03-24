package com.elbeesee.triplestore.httpclient;

/**
 * 
 * Elephant Bird Consulting - triplestore http client.
 * @author Tom Geudens. 2014/03/17.
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
 * Fragment Accessor.
 *
 * @param  database  database name
 * @param  expiry  expiry 
 * @param  credentials  http credentials 
 * @param  accept  accept header
 * @param  subject  fragment subject
 * @param  predicate  fragment predicate
 * @param  object  fragment object
 * @param  offset  fragment offset
 * @param  limit  fragment limit
 */

public class FragmentAccessor extends StandardAccessorImpl {
	public FragmentAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("database",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("expiry",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("credentials",null,null,new Class[] {IHDSNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("accept",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("subject",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("predicate",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("object",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("limit",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("offset",null,null,new Class[] {Long.class}));
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		// database
		String aDatabase = null;
		try {
			aDatabase = aContext.source("arg:database", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - database - argument");
		}
		if (aDatabase.equals("")) {
			throw new Exception("the request does not have a valid - database - argument");
		}
		//
		
		// expiry
		Long aExpiry = null;
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
		//

		// credentials
		IHDSNode aCredentials = null;
		try {
			aCredentials = aContext.source("arg:credentials", IHDSNode.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - credentials - argument");
		}
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
		
		// subject
		String aSubject = null;
		if (aContext.getThisRequest().argumentExists("subject")) {
			try {
				aSubject = aContext.source("arg:subject", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - subject - argument");				
			}
		}
		else {
			aSubject = "?s";
		}
		if (aSubject.equals("")) {
			aSubject = "?s";
		}
		//
		
		// predicate
		String aPredicate = null;
		if (aContext.getThisRequest().argumentExists("predicate")) {
			try {
				aPredicate = aContext.source("arg:predicate", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - predicate - argument");				
			}
		}
		else {
			aPredicate = "?p";
		}
		if (aPredicate.equals("")) {
			aPredicate = "?p";
		}		
		//

		// object
		String aObject = null;
		if (aContext.getThisRequest().argumentExists("object")) {
			try {
				aObject = aContext.source("arg:object", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - object - argument");
			}
		}
		else {
			aObject = "?o";
		}
		if (aObject.equals("")) {
			aObject = "?o";
		}		
		//
		
		// offset
		Long aOffset = null;
		if (aContext.getThisRequest().argumentExists("offset")) {
			try {
				aOffset = aContext.source("arg:offset", Long.class);
			}
			catch (Exception e) {
				try {
					String aOffsetString = aContext.source("arg:offset", String.class);
					aOffset = Long.parseLong(aOffsetString);
				}
				catch (Exception e2) {
					throw new Exception("the request does not have a valid - offset - argument");
				}
			}
		}
		else {
			aOffset = 0L;
		}
		//
		
		// limit
		Long aLimit = null;
		if (aContext.getThisRequest().argumentExists("limit")) {
			try {
				aLimit = aContext.source("arg:limit", Long.class);
			}
			catch (Exception e) {
				try {
					String aLimitString = aContext.source("arg:limit", String.class);
					aLimit = Long.parseLong(aLimitString);
				}
				catch (Exception e2) {
					throw new Exception("the request does not have a valid - limit - argument");
				}
			}
		}
		else {
			aLimit = 100L;
		}
		//
		
		INKFRequest buildfragment = aContext.createRequest("active:freemarker");
		buildfragment.addArgument("operator", "res:/resources/freemarker/fragment.freemarker");
		buildfragment.addArgumentByValue("subject", aSubject);
		buildfragment.addArgumentByValue("predicate", aPredicate);
		buildfragment.addArgumentByValue("object", aObject);
		buildfragment.addArgumentByValue("offset", aOffset);
		buildfragment.addArgumentByValue("limit", aLimit);
		buildfragment.setRepresentationClass(String.class);
		String vFragment = (String)aContext.issueRequest(buildfragment);
		
		INKFRequest sparqlrequest = aContext.createRequest("active:sparqlasync");
		sparqlrequest.addArgumentByValue("database", aDatabase);
		sparqlrequest.addArgumentByValue("query", vFragment);
		sparqlrequest.addArgumentByValue("accept", aAccept);
		sparqlrequest.addArgumentByValue("expiry", aExpiry);
		sparqlrequest.addArgumentByValue("credentials", aCredentials);
		Object vSparqlResult = aContext.issueRequest(sparqlrequest);
		
		INKFResponse vResponse = aContext.createResponseFrom(vSparqlResult);
		vResponse.setMimeType(aAccept);
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
