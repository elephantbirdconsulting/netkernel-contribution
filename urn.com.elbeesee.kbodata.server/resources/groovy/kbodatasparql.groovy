/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/01/22.
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * KBO data sparql Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

String vQuery = aContext.source("httpRequest:/param/query", String.class);

// processing
if (vQuery != null) {
	// protect against injection
	Pattern vInjectionPattern = Pattern.compile("(?i)\\bINSERT|DELETE|LOAD|CLEAR|CREATE|DROP|COPY|MOVE|ADD\\b");
	Matcher vInjectionMatcher = vInjectionPattern.matcher(vQuery);
	Boolean vInjectionFound = vInjectionMatcher.find();

	if (vInjectionFound) {
		vQuery = null;
	}
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

String vAcceptHeader = (String)aContext.source("httpRequest:/header/Accept", String.class);
if (vAcceptHeader == null) {
	if (vConstructFound || vDescribeFound) {
		vAcceptHeader = "application/rdf+xml";
	}
	else if (vAskFound) {
		vAcceptHeader = "text/boolean";
	}
	else {
		vAcceptHeader = "application/sparql-results+xml";
	}
}

INKFRequest sparqlrequest = aContext.createRequest("active:sparql");
sparqlrequest.addArgument("database", "kbodata:database");
if (vQuery != null) {
	sparqlrequest.addArgumentByValue("query", vQuery);
}
else {
	sparqlrequest.addArgument("query", "kbodata:query");
}
sparqlrequest.addArgument("expiry", "kbodata:expiry");
sparqlrequest.addArgument("credentials", "kbodata:credentials");
if (vAcceptHeader.startsWith("text/html")) {
	if (vConstructFound || vDescribeFound) {
		sparqlrequest.addArgumentByValue("accept", "application/rdf+xml");
	}
	else if (vAskFound) {
		sparqlrequest.addArgumentByValue("accept", "text/boolean");
	}
	else {
		sparqlrequest.addArgumentByValue("accept", "application/sparql-results+xml");
	}
}
else {
	sparqlrequest.addArgumentByValue("accept", vAcceptHeader);
}
Object vResult = aContext.issueRequest(sparqlrequest);
//

String vMimetype = null;
if (vAcceptHeader.startsWith("text/html")) {
	if (vConstructFound || vDescribeFound) {
		vMimetype = "application/rdf+xml";
	}
	else if (vAskFound) {
		vMimetype = "text/boolean";
	}
	else {
		vMimetype = "text/html";
	}
}
else {
	vMimetype = vAcceptHeader;
}

// response
INKFResponse vResponse = null;
if (vAcceptHeader.startsWith("text/html")) {

	if (vConstructFound || vDescribeFound || vAskFound) {
		vResponse = aContext.createResponseFrom(vResult);
	}
	else {
		
		INKFRequest xsltrequest = aContext.createRequest("active:xslt2");
		xsltrequest.addArgumentByValue("operand", vResult);
		xsltrequest.addArgument("operator", "res:/resources/xsl/kbosparql.xsl");
		Object vHTML = aContext.issueRequest(xsltrequest);
		
		vResponse = aContext.createResponseFrom(vHTML);
	}
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
	vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin",vCORSOrigin);
}
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//