/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/03/24.
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
 * KBO data fragments Accessor.
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

String aExtension = null;
try {
	String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);

	if (vAcceptHeader.startsWith("text/plain")) {
		aExtension = "nt";
	}
	else if (vAcceptHeader.startsWith("text/turtle")) {
		aExtension = "ttl";
	}
	else if (vAcceptHeader.startsWith("application/x-turtle")) {
		aExtension = "ttl"
	}
	else if (vAcceptHeader.startsWith("text/html")) {
		aExtension = "html";
	}
	else if (vAcceptHeader.startsWith("application/ld-json")) {
		aExtension = "jsonld"
	}
	else {
		aExtension = "rdf";
	}
}
catch (Exception e) {
	aExtension = "rdf";
}

String vIdentity = "";
INKFRequest fragmentsrequest = aContext.createRequest("active:fragments");
fragmentsrequest.addArgument("database", "kbodata:database");
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
fragmentsrequest.addArgument("dataset", "kbodata:dataset");
fragmentsrequest.addArgument("expiry", "kbodata:expiry");
fragmentsrequest.addArgument("credentials", "kbodata:credentials");
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
	aContext.sink("pds:/fragments/" + vIdentityMD5, vFragmentsResult);
}

INKFRequest jenaparserequest = aContext.createRequest("active:jRDFParseXML");
jenaparserequest.addArgumentByValue("operand",vFragmentsResult);
Object vJenaParseResult = aContext.issueRequest(jenaparserequest);

INKFRequest modelemptyrequest = aContext.createRequest("active:jRDFModelIsEmpty");
modelemptyrequest.addArgumentByValue("operand", vJenaParseResult);
modelemptyrequest.setRepresentationClass(Boolean.class);
Boolean vIsModelEmpty = (Boolean)aContext.issueRequest(modelemptyrequest);

IReadableBinaryStreamRepresentation vRBS = null;
String vMimetype = null;
INKFRequest jenaserializerequest = null;
if (aExtension.equals("rdf")) {
	jenaserializerequest = aContext.createRequest("active:jRDFSerializeXML");
	vMimetype = "application/rdf+xml";
}
else if (aExtension.equals("ttl")) {
	jenaserializerequest = aContext.createRequest("active:jRDFSerializeTURTLE");
	vMimetype = "text/turtle";
}
else if (aExtension.equals("nt")) {
	jenaserializerequest = aContext.createRequest("active:jRDFSerializeN-TRIPLE");
	vMimetype = "text/plain";
}
else if (aExtension.equals("jsonld")) {
	jenaserializerequest = aContext.createRequest("active:jRDFSerializeJSONLD");
	vMimetype = "application/ld+json";
}
else {
	jenaserializerequest = aContext.createRequest("active:jRDFSerializeXML");
	vMimetype = "application/rdf+xml";
}

jenaserializerequest.addArgumentByValue("operand",vJenaParseResult);
jenaserializerequest.setRepresentationClass(IReadableBinaryStreamRepresentation);
vRBS = (IReadableBinaryStreamRepresentation)aContext.issueRequest(jenaserializerequest);

// response
INKFResponse vResponse = null;
if (aExtension.equals("html")) {
	vMimetype = "text/html";
	
	INKFRequest xsltrequest = aContext.createRequest("active:xslt2");
	xsltrequest.addArgumentByValue("operand", vRBS);
	xsltrequest.addArgumentByValue("url", vURL);
	xsltrequest.addArgument("dataset", "kbodata:dataset");
	xsltrequest.addArgument("operator", "res:/resources/xsl/kbofragments.xsl");
	Object vHTML = aContext.issueRequest(xsltrequest);
	
	vResponse = aContext.createResponseFrom(vHTML);
}
else {
	vResponse = aContext.createResponseFrom(vRBS);
}
vResponse.setMimeType(vMimetype);
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
if (vIsModelEmpty) {
	vResponse.setHeader("httpResponse:/code",404);
}
else {
	vResponse.setHeader("httpResponse:/header/Vary","Accept");
}
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//