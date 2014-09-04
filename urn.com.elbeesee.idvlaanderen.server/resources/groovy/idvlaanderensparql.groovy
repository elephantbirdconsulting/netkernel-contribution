/**
 *
 * Elephant Bird Consulting - ID Vlaanderen.
 *
 * @author Tom Geudens. 2014/09/03.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * ID Vlaanderen SPARQL Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// arguments and parameters
String vQuery = aContext.source("httpRequest:/param/query", String.class); // need to extend this to handle post
//

if (vQuery != null) {
	// protect against injection
	Pattern vInjectionPattern = Pattern.compile("(?i)\\bINSERT|DELETE|LOAD|CLEAR|CREATE|DROP|COPY|MOVE|ADD\\b");
	Matcher vInjectionMatcher = vInjectionPattern.matcher(vQuery);
	Boolean vInjectionFound = vInjectionMatcher.find();

	if (vInjectionFound) {
		vQuery = aContext.source("idvlaanderen:query",String.class);
	}
}
else {
	vQuery = aContext.source("idvlaanderen:query",String.class);
}

Pattern vConstructPattern = Pattern.compile("(?i)\\bCONSTRUCT\\b");
Matcher vConstructMatcher = vConstructPattern.matcher(vQuery);
Boolean vConstructFound = vConstructMatcher.find();

Pattern vDescribePattern = Pattern.compile("(?i)\\bDESCRIBE\\b");
Matcher vDescribeMatcher = vDescribePattern.matcher(vQuery);
Boolean vDescribeFound = vDescribeMatcher.find();

Pattern vAskPattern = Pattern.compile("(?i)\\bASK\\b");
Matcher vAskMatcher = vAskPattern.matcher(vQuery);
Boolean vAskFound = vAskMatcher.find();


// mimetype
String vSupportedMimetypes = null;
String vDefaultMimetype = null;

if (vConstructFound || vDescribeFound) {
	vSupportedMimetypes = aContext.source("idvlaanderen:mimetypes-sparqlconstruct-allowed", String.class);
	vDefaultMimetype = aContext.source("idvlaanderen:mimetypes-sparqlconstruct-default", String.class);
}
else if (vAskFound){
	vSupportedMimetypes = aContext.source("idvlaanderen:mimetypes-sparqlask-allowed", String.class);
	vDefaultMimetype = aContext.source("idvlaanderen:mimetypes-sparqlask-default", String.class);
}
else {
	vSupportedMimetypes = aContext.source("idvlaanderen:mimetypes-sparqlselect-allowed", String.class);
	vDefaultMimetype = aContext.source("idvlaanderen:mimetypes-sparqlselect-default", String.class);
}
List<String> vSupportedMimetypesList = Arrays.asList(vSupportedMimetypes.split(","));
String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);
String vMimetype = MIMEParse.bestMatch(vSupportedMimetypesList, vAcceptHeader);
//

// keywordsearch
INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "idvlaanderen:database");
sparqlrequest.addArgument("expiry", "idvlaanderen:expiry");
sparqlrequest.addArgument("credentials", "idvlaanderen:credentials");
if (vMimetype.equals("text/html")) {
	sparqlrequest.addArgumentByValue("accept", vDefaultMimetype);
}
else {
	sparqlrequest.addArgumentByValue("accept", vMimetype);
}
sparqlrequest.addArgumentByValue("query", vQuery);
@SuppressWarnings("rawtypes")
INKFResponseReadOnly sparqlresponse = aContext.issueRequestForResponse(sparqlrequest);
String vException = (String)sparqlresponse.getHeader("exception");
Object vResult = sparqlresponse.getRepresentation();
//

// response
INKFResponse vResponse = null;
if (vException.equals("true")) {
	vResponse = aContext.createResponseFrom(vResult);
	vResponse.setMimeType("text/plain");
	vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
}
else {
	if (vMimetype.equals("text/html")) {
		INKFRequest xsltcrequest = aContext.createRequest("active:xsltc");
		xsltcrequest.addArgument("operator", "res:/resources/xsl/sparql.xsl");
		xsltcrequest.addArgumentByValue("operand", vResult);
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
}
//
