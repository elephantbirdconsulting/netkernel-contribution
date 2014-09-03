/**
 *
 * Elephant Bird Consulting - ID Vlaanderen.
 *
 * @author Tom Geudens. 2014/09/02.
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
 * ID Vlaanderen Meta Accessor.
 * 
 * @param  meta  meta
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// meta (mandatory)
String aMeta = null;
try {
	aMeta = aContext.source("arg:meta", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - meta - argument");
}
//
	
INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "idvlaanderen:database");
sparqlrequest.addArgument("expiry", "idvlaanderen:expiry");
sparqlrequest.addArgument("credentials", "idvlaanderen:credentials");
sparqlrequest.addArgument("query", "res:/resources/sparql/" + aMeta + ".sparql");
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
	// No CORS verification yet, I just allow everything
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin","*");
}
vResponse.setHeader("httpResponse:/header/Vary","Accept");
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//