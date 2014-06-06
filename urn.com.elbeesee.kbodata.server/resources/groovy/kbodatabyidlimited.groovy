/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2013/12/24.
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
 * KBO data by id Accessor.
 *
 * @param  owner  owner
 * @param  id  id
 * @param  extension  extension
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

// extension (optional)
String aExtension = null;
if (aContext.getThisRequest().argumentExists("extension")) {
	try {
		aExtension = aContext.source("arg:extension", String.class);
	}
	catch (Exception e) {
		aExtension = "rdf";
	}
}
else {
	// default is rdf, but accept header may have something else to say
	// determining all this "hardcoded" is not quite the best way, but sufficient for now
	try {
		String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);

		if (vAcceptHeader.startsWith("text/plain")) {
			aExtension = "nt";
		}
		else if (vAcceptHeader.startsWith("text/turtle")) {
			aExtension = "ttl";
		}
		else if (vAcceptHeader.startsWith("application/x-turtle")) {
			aExtension = "ttl";
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
}
//

Object vSparqlResult = null;

INKFRequest incacherequest = aContext.createRequest("pds:/" +aOwner + "_" + aID);
incacherequest.setVerb(INKFRequestReadOnly.VERB_EXISTS);
incacherequest.setRepresentationClass(Boolean.class);
Boolean vInCache = (Boolean)aContext.issueRequest(incacherequest);

if (vInCache) {
	vSparqlResult = aContext.source("pds:/" +aOwner + "_" + aID);
}
else {
	INKFRequest freemarkerrequest = aContext.createRequest("active:freemarker");
	freemarkerrequest.addArgument("operator", "res:/resources/freemarker/constructlimited.freemarker");
	freemarkerrequest.addArgumentByValue("owner", aOwner);
	freemarkerrequest.addArgumentByValue("id", aID);
	freemarkerrequest.addArgumentByValue("extension", aExtension);
	freemarkerrequest.setRepresentationClass(String.class);
	String vQuery = (String)aContext.issueRequest(freemarkerrequest);
		
	INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
	sparqlrequest.addArgument("database", "kbodata:database");
	sparqlrequest.addArgument("expiry", "kbodata:expiry");
	sparqlrequest.addArgument("credentials", "kbodata:credentials");
	sparqlrequest.addArgumentByValue("query", vQuery);
	sparqlrequest.addArgumentByValue("accept", "application/rdf+xml");
	vSparqlResult = aContext.issueRequest(sparqlrequest);
	
	aContext.sink("pds:/" +aOwner + "_" + aID, vSparqlResult);
}

INKFRequest jenaparserequest = aContext.createRequest("active:jRDFParseXML");
jenaparserequest.addArgumentByValue("operand",vSparqlResult);
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
	xsltrequest.addArgument("operator", "res:/resources/xsl/kbo.xsl");
	Object vHTML = aContext.issueRequest(xsltrequest);
	
	INKFRequest serializerequest = aContext.createRequest("active:saxonSerialize");
	serializerequest.addArgumentByValue("operand", vHTML);
	serializerequest.addArgumentByValue("operator", "<serialize><indent>yes</indent><omit-declaration>yes</omit-declaration><encoding>UTF-8</encoding><method>xhtml</method><mimeType>text/html</mimeType></serialize>");
	IReadableBinaryStreamRepresentation vRBSHTML = (IReadableBinaryStreamRepresentation)aContext.issueRequest(serializerequest);
	
	vResponse = aContext.createResponseFrom(vRBSHTML);
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