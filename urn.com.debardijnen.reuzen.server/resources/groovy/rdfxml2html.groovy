/**
 *
 * De Bardijnen - Reuzen.
 *
 * @author Tom Geudens. 2014/05/11.
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
import org.netkernel.layer0.representation.IReadableBinaryStreamRepresentation;


/**
 *
 * RDFXML to HTML Accessor.
 *
 * @param  operand  unfiltered rdfxml
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// filter rdf
INKFRequest rdfxml2rdfxmlrequest = aContext.createRequest("active:rdfxml2rdfxml");
rdfxml2rdfxmlrequest.addArgument("operand", "arg:operand");
INKFResponseReadOnly rdfxml2rdfxmlresponse = aContext.issueRequestForResponse(rdfxml2rdfxmlrequest);

Integer vHTTPResponseCode = null;
try {
	vHTTPResponseCode = (Integer)rdfxml2rdfxmlresponse.getHeader("httpResponse:/code");
}
catch (Exception e) {
	// no worries, this is good
}
Object vRDFXML = rdfxml2rdfxmlresponse.getRepresentation();
//

// transform
INKFRequest xsltcrequest = aContext.createRequest("active:xsltc");
xsltcrequest.addArgumentByValue("operand", vRDFXML);
xsltcrequest.addArgument("operator","res:/resources/xsl/rdfxml2html.xsl");
xsltcrequest.setRepresentationClass(IReadableBinaryStreamRepresentation.class);
Object vHTML = aContext.issueRequest(xsltcrequest);
//

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom(vHTML);
vResponse.setMimeType("text/html");
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
if (vHTTPResponseCode != null) {
	// passing a 404 along
	vResponse.setHeader("httpResponse:/code", vHTTPResponseCode.intValue());
}
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//