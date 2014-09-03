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
 * ID Vlaanderen by id Accessor.
 *
 * @param  owner  owner
 * @param  id  id
 * @param  template  template
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// owner (mandatory)
String aOwner = null;
try {
	aOwner = aContext.source("arg:owner", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - owner - argument");
}
//

// id (mandatory)
String aID = null;
try {
	aID = aContext.source("arg:id", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - id - argument");
}
//

// template (mandatory)
String aTemplate = null;
try {
	aTemplate = aContext.source("arg:template", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - template - argument");
}
//

INKFRequest freemarkerrequest = aContext.createRequest("active:freemarker");
freemarkerrequest.addArgument("operator", "res:/resources/freemarker/" + aTemplate + ".freemarker");
freemarkerrequest.addArgumentByValue("owner", aOwner);
freemarkerrequest.addArgumentByValue("id", aID);
freemarkerrequest.addArgument("baseurl", "idvlaanderen:baseurl");
freemarkerrequest.setRepresentationClass(String.class);
String vQuery = (String)aContext.issueRequest(freemarkerrequest);
	
INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "idvlaanderen:database");
sparqlrequest.addArgument("expiry", "idvlaanderen:expiry");
sparqlrequest.addArgument("credentials", "idvlaanderen:credentials");
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
	// No CORS verification yet, I just allow everything
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin","*");
}
vResponse.setHeader("httpResponse:/header/Vary","Accept");
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//