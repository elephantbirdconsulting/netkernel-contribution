/**
 *
 * De Bardijnen - Reuzen.
 *
 * @author Tom Geudens. 2014/05/11.
 *
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.representation.*
import org.netkernel.layer0.representation.impl.*;


/**
 * Processing Imports.
 */

/**
 *
 * Reuzen by id Accessor.
 *
 * @param  owner  owner
 * @param  id  id
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// owner (mandatory)
String aOwner = null;
if (aContext.getThisRequest().argumentExists("owner")) {
	try {
		aOwner = aContext.source("arg:owner", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - owner - argument");
	}
}
//

// id (mandatory)
String aID = null;
if (aContext.getThisRequest().argumentExists("id")) {
	try {
		aID = aContext.source("arg:id", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - id - argument");
	}
}
//

INKFRequest freemarkerrequest = aContext.createRequest("active:freemarker");
freemarkerrequest.addArgument("operator", "res:/resources/freemarker/construct.freemarker");
freemarkerrequest.addArgumentByValue("owner", aOwner);
freemarkerrequest.addArgumentByValue("id", aID);
freemarkerrequest.addArgument("baseurl", "reuzen:baseurl");
freemarkerrequest.setRepresentationClass(String.class);
String vQuery = (String)aContext.issueRequest(freemarkerrequest);
	
INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "reuzen:database");
sparqlrequest.addArgument("expiry", "reuzen:expiry");
sparqlrequest.addArgument("credentials", "reuzen:credentials");
sparqlrequest.addArgumentByValue("query", vQuery);
sparqlrequest.addArgumentByValue("accept", "application/rdf+xml");
Object vSparqlResult = aContext.issueRequest(sparqlrequest);

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom(vSparqlResult);
vResponse.setMimeType("application/rdf+xml");
String vCORSOrigin = null;
try {
	vCORSOrigin = aContext.source("httpRequest:/header/Origin", String.class);
}
catch (Exception e){
	//
}
if (vCORSOrigin != null) {
	// No CORS verification yet, I just allow the origin
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin",vCORSOrigin);
}
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//