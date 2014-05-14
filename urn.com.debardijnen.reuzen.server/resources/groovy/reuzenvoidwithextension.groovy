/**
 *
 * De Bardijnen - Reuzen.
 *
 * @author Tom Geudens. 2014/05/12.
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
 * @param  extension  extension
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// extension (mandatory)
String aExtension = null;
if (aContext.getThisRequest().argumentExists("extension")) {
	try {
		aExtension = aContext.source("arg:extension", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - extension - argument");
	}
}
//

INKFRequest reuzenbyidrequest = aContext.createRequest("active:reuzenvoid");
Object vIDResult = aContext.issueRequest(reuzenbyidrequest);

// response
INKFResponse vResponse = null;
if (aExtension.equals("rdf")) {
	INKFRequest rdfxml2rdfxmlrequest = aContext.createRequest("active:rdfxml2rdfxml");
	rdfxml2rdfxmlrequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2rdfxmlrequest));
}
else if (aExtension.equals("ttl")) {
	INKFRequest rdfxml2turtlerequest = aContext.createRequest("active:rdfxml2turtle");
	rdfxml2turtlerequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2turtlerequest));
}
else if (aExtension.equals("nt")) {
	INKFRequest rdfxml2ntriplerequest = aContext.createRequest("active:rdfxml2ntriple");
	rdfxml2ntriplerequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2ntriplerequest));
}
else if (aExtension.equals("jsonld")) {
	INKFRequest rdfxml2jsonldrequest = aContext.createRequest("active:rdfxml2jsonld");
	rdfxml2jsonldrequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2jsonldrequest));
}
else if (aExtension.equals("html")) {
	INKFRequest rdfxml2htmlrequest = aContext.createRequest("active:rdfxml2html");
	rdfxml2htmlrequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2htmlrequest));
}
else {
	INKFRequest rdfxml2rdfxmlrequest = aContext.createRequest("active:rdfxml2rdfxml");
	rdfxml2rdfxmlrequest.addArgumentByValue("operand", vIDResult);
	vResponse = aContext.createResponseFrom(aContext.issueRequestForResponse(rdfxml2rdfxmlrequest));
}
//