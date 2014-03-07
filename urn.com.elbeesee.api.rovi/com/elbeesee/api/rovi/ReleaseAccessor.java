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
 * Release Accessor.
 *
 * @param  upcid  universal product code
 * 
 */

public class ReleaseAccessor extends StandardAccessorImpl {
	public ReleaseAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("upcid",null,null,new Class[] {String.class}));
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		// upcid
		String aUPCid = null;
		try {
			aUPCid = aContext.source("arg:upcid", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - upcid - argument");
		}
		if (aUPCid.equals("")) {
			throw new Exception("the request does not have a valid - upcid - argument");
		}
		//
		
		// rovi resources
		String vRoviApiKey = null;
		try {
			vRoviApiKey = aContext.source("rovi:apikey", String.class);
		}
		catch (Exception e) {
			throw new Exception("resource - rovi:apikey - is not available");
		}
		
		String vRoviSecretKey = null;
		try {
			vRoviSecretKey = aContext.source("rovi:secretkey", String.class);
		}
		catch (Exception e) {
			throw new Exception("resource - rovi:secretkey - is not available");
		}
				
		Long vRoviExpiry = null;
		try {
			vRoviExpiry = aContext.source("rovi:expiry", Long.class);
		}
		catch (Exception e) {
			throw new Exception("resource - rovi:expiry - is not available");
		}
		
		String vRoviReleaseURL = null;
		try {
			vRoviReleaseURL = aContext.source("rovi:releaseurl", String.class);
		}
		catch (Exception e) {
			throw new Exception("resource - rovi:releaseurl - is not available");
		}
		//
		
		// compute signature for the request to Rovi
		INKFRequest rovimd5request = aContext.createRequest("active:rovimd5");
		rovimd5request.addArgumentByValue("apikey", vRoviApiKey);
		rovimd5request.addArgumentByValue("secretkey", vRoviSecretKey);
	    rovimd5request.setRepresentationClass(String.class);
	    rovimd5request.setHeader("exclude-dependencies",true); // would always expire result otherwise
	    String vSignature = (String)aContext.issueRequest(rovimd5request);
		//
	    
	    // rovi request
		INKFRequest rovireleaserequest = aContext.createRequest("active:httpGet");
		rovireleaserequest.addArgument("url", vRoviReleaseURL + "?upcid=" + aUPCid + "&include=images,tracks&format=xml&apikey=" + vRoviApiKey + "&sig=" + vSignature);
        rovireleaserequest.setHeader("exclude-dependencies",true); // we'll determine cachebility ourselves
    	Object vRoviReleaseResult = aContext.issueRequest(rovireleaserequest);
	    //
	    
		// response
		INKFResponse vResponse = aContext.createResponseFrom(vRoviReleaseResult);
		vResponse.setExpiry(INKFResponse.EXPIRY_MIN_CONSTANT_DEPENDENT, System.currentTimeMillis() + vRoviExpiry);
		//			    
	}
}
