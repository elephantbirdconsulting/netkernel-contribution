/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/01/10.
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
String vAcceptHeader = aContext.source("httpRequest:/header/Accept", String.class);

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

if (vAcceptHeader == null) {
	// provide sensible default Accept
	Pattern vConstructPattern = Pattern.compile("(?i)\\bCONSTRUCT\\b");
	Matcher vConstructMatcher = vConstructPattern.matcher(vQuery);
	Boolean vConstructFound = vConstructMatcher.find();
	
	if (vConstructFound) {
		vAcceptHeader = "application/rdf+xml";
	}
	else {
		vAcceptHeader = "application/sparql-results+xml";
	}
}

INKFRequest sparqlrequest = aContext.createRequest("active:kbodatastardogsparql");
if (vQuery != null) {
	sparqlrequest.addArgumentByValue("query", vQuery);
}
sparqlrequest.addArgumentByValue("accept", vAcceptHeader);
Object vResult = aContext.issueRequest(sparqlrequest);
//


INKFResponse vResponse = aContext.createResponseFrom(vResult);
vResponse.setMimeType(vAcceptHeader);
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//
