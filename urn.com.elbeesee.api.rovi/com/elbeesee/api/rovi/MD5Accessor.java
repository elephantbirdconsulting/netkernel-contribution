package com.elbeesee.api.rovi;

/**
 * 
 * Elephant Bird Consulting - API - Rovi.
 *  
 * @author Tom Geudens. 2014/03/06.
 *
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

/**
 * Processing Imports.
 */

/**
 * 
 * MD5 Accessor.
 *
 * @param  apikey  api key
 * @param  secretkey  secret key
 * 
 */

public class MD5Accessor extends StandardAccessorImpl {
	public MD5Accessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("apikey",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("secretkey",null,null,new Class[] {String.class}));
		this.declareSourceRepresentation(String.class);
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		// apikey
		String aApiKey = null;
		try {
			aApiKey = aContext.source("arg:apikey", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - apikey - argument");
		}
		if (aApiKey.equals("")) {
			throw new Exception("the request does not have a valid - apikey - argument");
		} // no else needed, the grammar enforces the presence of the argument
		//
		
		// secretkey
		String aSecretKey = null;
		try {
			aSecretKey = aContext.source("arg:secretkey", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - secretkey - argument");
		}
		if (aSecretKey.equals("")) {
			throw new Exception("the request does not have a valid - secretkey - argument");
		}
		//

		// md5
        long vUnixTime = System.currentTimeMillis() / 1000L;
        String vUnixString = Long.toString(vUnixTime);
        String vMD5String = aApiKey + aSecretKey + vUnixString;

        INKFRequest md5request = aContext.createRequest("active:md5");
        md5request.addArgumentByValue("operand", vMD5String);
        md5request.setRepresentationClass(String.class);
        
        String vHash = (String)aContext.issueRequest(md5request);
        //

		// response
        // note : any resource using this one 'straight' will always be expired, so taking
        // cache-control is advised (unless you want immediate expiry)
        INKFResponse vResponse = aContext.createResponseFrom(vHash);

        vResponse.setMimeType("text/plain");
        vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
        //        
	}
}
