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
 * ID Vlaanderen Fragments Server Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

String vSubject = aContext.source("httpRequest:/param/subject", String.class);
String vPredicate = aContext.source("httpRequest:/param/predicate", String.class);
String vObject = aContext.source("httpRequest:/param/object", String.class);
String vLimit = aContext.source("httpRequest:/param/limit", String.class);
String vOffset = aContext.source("httpRequest:/param/offset", String.class);
String vURL = aContext.source("httpRequest:/url", String.class);
String vQuery = aContext.source("httpRequest:/query", String.class);

String vIdentity = "";
INKFRequest fragmentsrequest = aContext.createRequest("active:fragments");
fragmentsrequest.addArgument("database", "idvlaanderen:database");
if (vSubject != null) {
	fragmentsrequest.addArgumentByValue("subject", vSubject);
	vIdentity = vIdentity + "s=" + vSubject + "-";
}
if (vPredicate != null) {
	fragmentsrequest.addArgumentByValue("predicate", vPredicate);
	vIdentity = vIdentity + "p=" + vPredicate + "-";
}
if (vObject != null) {
	fragmentsrequest.addArgumentByValue("object", vObject);
	vIdentity = vIdentity + "o=" + vObject + "-";
}
if (vLimit != null) {
	fragmentsrequest.addArgumentByValue("limit", vLimit);
	vIdentity = vIdentity + "l=" + vLimit + "-";
}
if (vOffset != null) {
	fragmentsrequest.addArgumentByValue("offset", vOffset);
	vIdentity = vIdentity + "f=" + vOffset + "-";
}
if (vURL != null) {
	fragmentsrequest.addArgumentByValue("url", vURL);
}
if (vQuery != null) {
	fragmentsrequest.addArgumentByValue("query", vQuery);
}
fragmentsrequest.addArgument("dataset", "idvlaanderen:dataset");
fragmentsrequest.addArgument("expiry", "idvlaanderen:expiry");
fragmentsrequest.addArgument("credentials", "idvlaanderen:credentials");
fragmentsrequest.addArgumentByValue("accept", "application/rdf+xml");

INKFRequest md5request = aContext.createRequest("active:md5");
md5request.addArgumentByValue("operand", vIdentity);
md5request.setRepresentationClass(String.class);
String vIdentityMD5 = aContext.issueRequest(md5request);

Object vFragmentsResult = null;

INKFRequest incacherequest = aContext.createRequest("pds:/fragments/" + vIdentityMD5);
incacherequest.setVerb(INKFRequestReadOnly.VERB_EXISTS);
incacherequest.setRepresentationClass(Boolean.class);
Boolean vInCache = (Boolean)aContext.issueRequest(incacherequest);

if (vInCache) {
	vFragmentsResult = aContext.source("pds:/fragments/" + vIdentityMD5);
}
else {
	vFragmentsResult = aContext.issueRequest(fragmentsrequest);
	// uncomment for 2nd level caching of fragments
	// aContext.sink("pds:/fragments/" + vIdentityMD5, vFragmentsResult);
}

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom(vFragmentsResult);
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