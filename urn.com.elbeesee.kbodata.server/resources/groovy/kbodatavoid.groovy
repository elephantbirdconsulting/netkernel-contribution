/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/01/08.
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
 * KBO data void Accessor.
 *
 * @param  extension  extension
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
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
}
//
Object vSparqlResult = null;

INKFRequest incacherequest = aContext.createRequest("pds:/dataset_kbo");
incacherequest.setVerb(INKFRequestReadOnly.VERB_EXISTS);
incacherequest.setRepresentationClass(Boolean.class);
Boolean vInCache = (Boolean)aContext.issueRequest(incacherequest);

if (vInCache) {
	vSparqlResult = aContext.source("pds:/dataset_kbo");
}
else {
	INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
	sparqlrequest.addArgument("database", "kbodata:database");
	sparqlrequest.addArgument("expiry", "kbodata:expiry");
	sparqlrequest.addArgument("credentials", "kbodata:credentials");
	sparqlrequest.addArgument("query", "res:/resources/sparql/void.sparql");
	sparqlrequest.addArgumentByValue("accept", "application/rdf+xml");
	vSparqlResult = aContext.issueRequest(sparqlrequest);
	
	aContext.sink("pds:/dataset_kbo", vSparqlResult);
}

INKFRequest jenaparserequest = aContext.createRequest("active:jRDFParseXML");
jenaparserequest.addArgumentByValue("operand",vSparqlResult);
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
	xsltrequest.addArgument("operator", "res:/resources/xsl/kbometa.xsl");
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