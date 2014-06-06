/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/05/10.
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
import java.util.List;
import java.util.Arrays;
import com.elbeesee.triplestore.httpclient.MIMEParse;

/**
 *
 * KBO data keyword search Accessor.
 *
 */

// context
INKFRequestContext aContext = (INKFRequestContext)context;
//


String vSearch = aContext.source("httpRequest:/param/search", String.class);
String vLimit = aContext.source("httpRequest:/param/limit", String.class);

// mimetype
String vSupportedMimetypes = aContext.source("kbodata:mimetypes-keywordsearch-allowed", String.class);
String vDefaultMimetype = aContext.source("kbodata:mimetypes-keywordsearch-default", String.class);
List<String> vSupportedMimetypesList = Arrays.asList(vSupportedMimetypes.split(","));
String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);
String vMimetype = MIMEParse.bestMatch(vSupportedMimetypesList, vAcceptHeader);
//

INKFRequest keywordsearchrequest = aContext.createRequest("active:keywordsearch");
keywordsearchrequest.addArgument("database", "kbodata:database");
keywordsearchrequest.addArgument("expiry", "kbodata:expiry");
keywordsearchrequest.addArgument("credentials", "kbodata:credentials");
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
	INKFRequest xsltrequest = aContext.createRequest("active:xslt2");
	xsltrequest.addArgumentByValue("operand", vResult);
	xsltrequest.addArgumentByValue("keyword", vSearch);
	xsltrequest.addArgument("operator", "res:/resources/xsl/kbokeyword.xsl");
	Object vHTML = aContext.issueRequest(xsltrequest);
	
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
	// No CORS verification yet, I just allow the origin
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin","*");
}
vResponse.setHeader("httpResponse:/header/Vary","Accept");
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//