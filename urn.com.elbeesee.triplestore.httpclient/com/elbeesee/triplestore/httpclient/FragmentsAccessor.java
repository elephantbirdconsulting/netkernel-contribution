package com.elbeesee.triplestore.httpclient;

/**
 * 
 * Elephant Bird Consulting - triplestore http client.
 * @author Tom Geudens. 2014/05/18.
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
 * Fragments Accessor.
 *
 * @param  database  database name
 * @param  expiry  expiry 
 * @param  credentials  http credentials 
 * @param  accept  accept header
 * @param  url  fragment url
 * @param  subject  fragment subject
 * @param  predicate  fragment predicate
 * @param  object  fragment object
 * @param  offset  fragment offset
 * @param  limit  fragment limit
 */

public class FragmentsAccessor extends StandardAccessorImpl {
	public FragmentsAccessor() {
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

		// url
		String aURL = null;
		if (aContext.getThisRequest().argumentExists("url")) {
			try {
				aURL = aContext.source("arg:url", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - url - argument");				
			}
		}
		else {
			aURL = "http://localhost/fragments";
		}
		if (aURL.equals("")) {
			aURL = "http://localhost/fragments";
		}
		//
		
		// query
		String aQuery = null;
		if (aContext.getThisRequest().argumentExists("query")) {
			try {
				aQuery = aContext.source("arg:query", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - query - argument");				
			}
		}
		else {
			aQuery = "";
		}
		//
		
		// dataset
		String aDataset = null;
		if (aContext.getThisRequest().argumentExists("dataset")) {
			try {
				aDataset = aContext.source("arg:dataset", String.class);
			}
			catch (Exception e) {
				throw new Exception("the request does not have a valid - dataset - argument");				
			}
		}
		else {
			aDataset = "http://localhost";
		}
		if (aDataset.equals("")) {
			aDataset = "http://localhost";
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
		if (!aSubject.equals("?s")) {
			if (! aSubject.startsWith("<")) {
				aSubject = "<" + aSubject + ">";
			}
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
		if (!aPredicate.equals("?p")) {
			if (! aPredicate.startsWith("<")) {
				aPredicate = "<" + aPredicate + ">";
			}
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
		if (!aObject.equals("?o")) {
			if ( (! aObject.startsWith("<")) && (! aObject.startsWith("'")) && (! aObject.startsWith("\""))) {
				aObject = "<" + aObject + ">";
			}
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
		INKFRequest buildfragmentscount = aContext.createRequest("active:freemarker");
		buildfragmentscount.addArgument("operator", "res:/resources/freemarker/fragmentscount.freemarker");
		buildfragmentscount.addArgumentByValue("subject", aSubject);
		buildfragmentscount.addArgumentByValue("predicate", aPredicate);
		buildfragmentscount.addArgumentByValue("object", aObject);
		String vFragmentsCount = (String)aContext.issueRequest(buildfragmentscount);

		INKFRequest sparqlcountrequest = aContext.createRequest("active:sparql");
		sparqlcountrequest.addArgumentByValue("database", aDatabase);
		sparqlcountrequest.addArgumentByValue("query", vFragmentsCount);
		sparqlcountrequest.addArgumentByValue("accept", "application/sparql-results+xml");
		sparqlcountrequest.addArgumentByValue("expiry", aExpiry);
		sparqlcountrequest.addArgumentByValue("credentials", aCredentials);
		Object vSparqlCountResult = aContext.issueRequest(sparqlcountrequest);
		
		INKFRequest xsltcrequest = aContext.createRequest("active:xsltc");
		xsltcrequest.addArgumentByValue("operand", vSparqlCountResult);
		xsltcrequest.addArgument("operator", "res:/resources/xsl/sparqlresult_to_count.xsl");
		xsltcrequest.setRepresentationClass(String.class);
		String vCount = (String)aContext.issueRequest(xsltcrequest);
		
		Long vCountLong = Long.parseLong(vCount);
		
		INKFRequest buildfragments = aContext.createRequest("active:freemarker");
		buildfragments.addArgument("operator", "res:/resources/freemarker/fragments.freemarker");
		buildfragments.addArgumentByValue("dataset", aDataset);
		buildfragments.addArgumentByValue("query", (aQuery.equals("")) ? "" : "?" + aQuery);
		buildfragments.addArgumentByValue("url", aURL);
		buildfragments.addArgumentByValue("subject", aSubject);
		buildfragments.addArgumentByValue("predicate", aPredicate);
		buildfragments.addArgumentByValue("object", aObject);
		buildfragments.addArgumentByValue("offset", aOffset.toString());
		buildfragments.addArgumentByValue("limit", aLimit.toString());
		buildfragments.addArgumentByValue("count", vCount);
		Long vPrevious = aOffset - aLimit;
		Long vNext = aOffset + aLimit;
		String vQueryWithoutPosition = ("?" + aQuery).replaceAll("(?<=[?&;])offset=.*?($|[&;])", "").replaceAll("(?<=[?&;])limit=.*?($|[&;])", "").replaceAll("&$","");
		if (vPrevious >= 0L) {
			buildfragments.addArgumentByValue("previous", aURL + vQueryWithoutPosition + (aQuery.equals("") ? "" : "&") + "offset=" + vPrevious.toString() + "&limit=" + aLimit.toString());
		}
		if (vNext <= vCountLong) {
			buildfragments.addArgumentByValue("next", aURL + vQueryWithoutPosition + (aQuery.equals("") ? "" : "&") + "offset=" + vNext.toString() + "&limit=" + aLimit.toString());
		}
		buildfragments.setRepresentationClass(String.class);
		String vFragments = (String)aContext.issueRequest(buildfragments);
		
		INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
		sparqlrequest.addArgumentByValue("database", aDatabase);
		sparqlrequest.addArgumentByValue("query", vFragments);
		sparqlrequest.addArgumentByValue("accept", aAccept);
		sparqlrequest.addArgumentByValue("expiry", aExpiry);
		sparqlrequest.addArgumentByValue("credentials", aCredentials);
		Object vSparqlResult = aContext.issueRequest(sparqlrequest);
		
		INKFResponse vResponse = aContext.createResponseFrom(vSparqlResult);
		vResponse.setMimeType(aAccept);
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
