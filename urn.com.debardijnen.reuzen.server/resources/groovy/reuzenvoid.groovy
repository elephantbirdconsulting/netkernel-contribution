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
 * Reuzen Void Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//
	
INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "reuzen:database");
sparqlrequest.addArgument("expiry", "reuzen:expiry");
sparqlrequest.addArgument("credentials", "reuzen:credentials");
sparqlrequest.addArgument("query", "res:/resources/sparql/void.sparql");
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