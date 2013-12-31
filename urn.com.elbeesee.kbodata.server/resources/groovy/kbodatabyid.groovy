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
 *
 * KBO data by id Accessor.
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
		else {
			aExtension = "rdf";
		}
	}
	catch (Exception e) {
		aExtension = "rdf";
	}
}
//

INKFRequest freemarkerrequest = aContext.createRequest("active:freemarker");
freemarkerrequest.addArgument("operator", "res:/resources/freemarker/construct.freemarker");
freemarkerrequest.addArgumentByValue("owner", aOwner);
freemarkerrequest.addArgumentByValue("id", aID);
freemarkerrequest.addArgumentByValue("extension", aExtension);
freemarkerrequest.setRepresentationClass(String.class);
String vQuery = (String)aContext.issueRequest(freemarkerrequest);

INKFRequest stardogrequest = aContext.createRequest("active:kbodatastardogsparql");
stardogrequest.addArgumentByValue("query", vQuery);
stardogrequest.addArgumentByValue("accept", "application/rdf+xml");
Object vSparqlResult = aContext.issueRequest(stardogrequest);

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

	INKFRequest xsltrequest = aContext.createRequest("active:xslt");
	xsltrequest.addArgumentByValue("operand", vRBS);
	xsltrequest.addArgument("operator", "res:/resources/xsl/html.xsl");
	xsltrequest.addArgumentByValue("owner", aOwner);
	xsltrequest.addArgumentByValue("id", aID);
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