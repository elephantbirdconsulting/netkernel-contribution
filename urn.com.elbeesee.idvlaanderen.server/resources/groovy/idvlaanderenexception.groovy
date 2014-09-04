/**
 *
 * Elephant Bird Consulting - ID Vlaanderen.
 *
 * @author Tom Geudens. 2014/09/04.
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
 * ID Vlaanderen Exception Accessor.
 * 
 * @param  request  request
 * @param  exception  exception
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom("Exception");
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
vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
//