/**
 *
 * Elephant Bird Consulting - KBO data.
 * 
 * @author Tom Geudens. 2013/12/23.
 * 
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;

/**
 *
 * KBO data SPARQL Accessor.
 *
 * @param  database  database name
 * @param  query  sparql query
 * @param  accept  accept header
 * @param  expiry  expiry
 * @param  credentials  http credentials
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// database (optional)
String aDatabase = null;
if (aContext.getThisRequest().argumentExists("database")) {
	try {
		aDatabase = aContext.source("arg:database", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - database - argument");
	}
}
else {
	try {
		aDatabase = aContext.source("kbodata:database", String.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:database - does not exist");
	}
}
//

// query (optional)
String aQuery = null;
if (aContext.getThisRequest().argumentExists("query")) {
	try {
		aQuery = aContext.source("arg:query", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - query - argument");
	}
	if ((aQuery == null) || (aQuery.equals(""))) {
		try {
			aQuery = aContext.source("kbodata:query", String.class);
		}
		catch (Exception e) {
			throw new NKFException("resource - kbodata:query - does not exist");
		}
	}
}
else {
	try {
		aQuery = aContext.source("kbodata:query", String.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:query - does not exist");
	}
}
//

// accept (optional)
String aAccept = null;
if (aContext.getThisRequest().argumentExists("accept")) {
	try {
		aAccept = aContext.source("arg:accept", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - accept - argument");
	}
}
else {
	try {
		aAccept = aContext.source("kbodata:accept", String.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:accept - does not exist");
	}
}
//

// expiry (optional)
Long aExpiry = null;
if (aContext.getThisRequest().argumentExists("expiry")) {
	try {
		aExpiry = aContext.source("arg:expiry", Long.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - expiry - argument");
	}
}
else {
	try {
		aExpiry = aContext.source("kbodata:expiry", Long.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:expiry - does not exist");
	}
}
//

// credentials (optional)
IHDSNode aCredentials = null;
if (aContext.getThisRequest().argumentExists("credentials")) {
	try {
		aCredentials = aContext.source("arg:credentials", IHDSNode.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - credentials - argument");
	}
}
else {
	try {
		aCredentials = aContext.source("kbodata:credentials", IHDSNode.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:credentials - does not exist");
	}
}

INKFRequest stardogsparqlrequest = aContext.createRequest("active:stardogsparql");
stardogsparqlrequest.addArgumentByValue("database", aDatabase);
stardogsparqlrequest.addArgumentByValue("query",aQuery);
stardogsparqlrequest.addArgumentByValue("accept", aAccept);
stardogsparqlrequest.addArgumentByValue("expiry", aExpiry);
stardogsparqlrequest.addArgumentByValue("credentials", aCredentials);
Object vResult = aContext.issueRequest(stardogsparqlrequest);

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom(vResult);
vResponse.setMimeType(aAccept);
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//
