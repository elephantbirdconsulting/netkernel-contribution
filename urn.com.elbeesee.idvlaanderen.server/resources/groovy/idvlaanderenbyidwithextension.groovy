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
 * @param  extension  extension
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

// extension (mandatory)
String aExtension = null;
try {
	aExtension = aContext.source("arg:extension", String.class);
}
catch (Exception e) {
	throw new NKFException("request does not have a valid - extension - argument");
}
//

INKFRequest kbobyidrequest = aContext.createRequest("active:idvlaanderenbyid");
kbobyidrequest.addArgumentByValue("owner", aOwner);
kbobyidrequest.addArgumentByValue("id", aID);
kbobyidrequest.addArgumentByValue("template", aTemplate);
Object vIDResult = aContext.issueRequest(kbobyidrequest);

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