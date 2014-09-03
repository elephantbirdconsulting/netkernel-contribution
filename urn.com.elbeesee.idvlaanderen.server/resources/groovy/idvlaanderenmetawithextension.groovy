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
 * @param  extension  extension
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// extension (mandatory)
String aExtension = null;
try {
	aExtension = aContext.source("arg:extension", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - extension - argument");
}
//

INKFRequest kbometarequest = aContext.createRequest("active:idvlaanderenmeta");
kbometarequest.addArgument("meta", "arg:meta");
Object vMetaResult = aContext.issueRequest(kbometarequest);

// response
INKFResponse vResponse = null;
if (aExtension.equals("rdf")) {
	INKFRequest rdfxml2rdfxmlrequest = aContext.createRequest("active:rdfxml2rdfxml");
	rdfxml2rdfxmlrequest.addArgumentByValue("operand", vMetaResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2rdfxmlrequest));
}
else if (aExtension.equals("ttl")) {
	INKFRequest rdfxml2turtlerequest = aContext.createRequest("active:rdfxml2turtle");
	rdfxml2turtlerequest.addArgumentByValue("operand", vMetaResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2turtlerequest));
}
else if (aExtension.equals("nt")) {
	INKFRequest rdfxml2ntriplerequest = aContext.createRequest("active:rdfxml2ntriple");
	rdfxml2ntriplerequest.addArgumentByValue("operand", vMetaResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2ntriplerequest));
}
else if (aExtension.equals("jsonld")) {
	INKFRequest rdfxml2jsonldrequest = aContext.createRequest("active:rdfxml2jsonld");
	rdfxml2jsonldrequest.addArgumentByValue("operand", vMetaResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2jsonldrequest));
}
else if (aExtension.equals("html")) {
	INKFRequest rdfxml2htmlrequest = aContext.createRequest("active:rdfxml2html");
	rdfxml2htmlrequest.addArgumentByValue("operand", vMetaResult);
	rdfxml2htmlrequest.addArgumentByValue("sheet", "meta");
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2htmlrequest));
}
else {
	INKFRequest rdfxml2rdfxmlrequest = aContext.createRequest("active:rdfxml2rdfxml");
	rdfxml2rdfxmlrequest.addArgumentByValue("operand", vMetaResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2rdfxmlrequest));
}
//