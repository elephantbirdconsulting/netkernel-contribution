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
}
INKFRequest sparqlrequest = aContext.createRequest("active:kbodatastardogsparql");
sparqlrequest.addArgumentByValue("query", vQuery);
Object vResult = aContext.issueRequest(sparqlrequest);
//


INKFResponse vResponse = aContext.createResponseFrom(vResult);
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//
