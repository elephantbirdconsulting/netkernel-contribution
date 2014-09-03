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
import com.elbeesee.triplestore.httpclient.MIMEParse;


/**
 *
 * ID Vlaanderen Keyword Search Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// arguments and parameters
String vSearch = aContext.source("httpRequest:/param/search", String.class);
String vLimit = aContext.source("httpRequest:/param/limit", String.class);
//

// mimetype
String vSupportedMimetypes = aContext.source("idvlaanderen:mimetypes-keywordsearch-allowed", String.class);
String vDefaultMimetype = aContext.source("idvlaanderen:mimetypes-keywordsearch-default", String.class);
List<String> vSupportedMimetypesList = Arrays.asList(vSupportedMimetypes.split(","));
String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);
String vMimetype = MIMEParse.bestMatch(vSupportedMimetypesList, vAcceptHeader);
//

// keywordsearch
INKFRequest keywordsearchrequest = aContext.createRequest("active:keywordsearch");
keywordsearchrequest.addArgument("database", "idvlaanderen:database");
keywordsearchrequest.addArgument("expiry", "idvlaanderen:expiry");
keywordsearchrequest.addArgument("credentials", "idvlaanderen:credentials");
if (vMimetype.equals("text/html")) {
	keywordsearchrequest.addArgumentByValue("accept", vDefaultMimetype);
}
else {
	keywordsearchrequest.addArgumentByValue("accept", vMimetype);
}
if (vSearch != null) {
	keywordsearchrequest.addArgumentByValue("search", vSearch);
}
if (vLimit != null) {
	keywordsearchrequest.addArgumentByValue("limit", vLimit);
}
Object vResult = aContext.issueRequest(keywordsearchrequest);
//

// response
INKFResponse vResponse = null;
if (vMimetype.equals("text/html")) {
	INKFRequest xsltcrequest = aContext.createRequest("active:xsltc");
	xsltcrequest.addArgument("operator", "res:/resources/xsl/keywordsearch.xsl");
	xsltcrequest.addArgumentByValue("operand", vResult);
	xsltcrequest.addArgumentByValue("keyword", vSearch);
	xsltcrequest.addArgument("baseurl","idvlaanderen:baseurl");
	xsltcrequest.addArgument("localurl", "idvlaanderen:localurl");
	Object vHTML = aContext.issueRequest(xsltcrequest);
	
	vResponse = aContext.createResponseFrom(vHTML);
}
else {
	vResponse = aContext.createResponseFrom(vResult);
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
vResponse.setHeader("httpResponse:/header/Vary","Accept");
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//
