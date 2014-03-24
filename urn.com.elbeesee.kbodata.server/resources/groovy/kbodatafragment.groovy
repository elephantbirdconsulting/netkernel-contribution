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
 * KBO data fragment Accessor.
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

INKFRequest fragmentrequest = aContext.createRequest("active:fragment");
fragmentrequest.addArgument("database", "kbodata:database");
if (vSubject != null) {
	fragmentrequest.addArgumentByValue("subject", vSubject);
}
if (vPredicate != null) {
	fragmentrequest.addArgumentByValue("predicate", vPredicate);
}
if (vObject != null) {
	fragmentrequest.addArgumentByValue("object", vObject);
}
if (vLimit != null) {
	fragmentrequest.addArgumentByValue("limit", vLimit);
}
if (vOffset != null) {
	fragmentrequest.addArgumentByValue("offset", vOffset);
}
fragmentrequest.addArgument("expiry", "kbodata:expiry");
fragmentrequest.addArgument("credentials", "kbodata:credentials");
fragmentrequest.addArgumentByValue("accept", "application/rdf+xml");
Object vFragmentResult = aContext.issueRequest(fragmentrequest);
//

INKFRequest jenaparserequest = aContext.createRequest("active:jRDFParseXML");
jenaparserequest.addArgumentByValue("operand",vFragmentResult);
Object vJenaParseResult = aContext.issueRequest(jenaparserequest);

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
	xsltrequest.addArgument("operator", "res:/resources/xsl/kbofragment.xsl");
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
	// No CORS verification yet, I just allow the origin
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin",vCORSOrigin);
}
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//