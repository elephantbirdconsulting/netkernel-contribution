package org.tomgeudens.rovi;

/**
 * 
 *  Rovi Project.  
 *  @author Tom Geudens. 2014/10/12.
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
import java.util.Arrays;
import java.util.List;

/**
 * Release Accessor.
 * 
 * @param apikey
 *            rovi apikey
 * @param secretkey
 *            rovi secret key
 * @param category
 *            category
 * @param expiry
 *            expiry duration
 * @param upcid
 *            universal product code
 * 
 */

public class ReleaseAccessor extends StandardAccessorImpl {
	public ReleaseAccessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("apikey",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("secretkey",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("category",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("expiry",null,null,new Class[] {Long.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("upcid",null,null,new Class[] {String.class}));
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		// apikey
		String aApikey = null;
		try {
			aApikey = aContext.source("arg:apikey", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - apikey - argument");
		}
		if (aApikey.equals("")) {
			throw new Exception("the request does not have a valid - apikey - argument");
		}		
		//
		
		// secretkey
		String aSecretkey = null;
		try {
			aSecretkey = aContext.source("arg:secretkey", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - secretkey - argument");
		}
		if (aSecretkey.equals("")) {
			throw new Exception("the request does not have a valid - secretkey - argument");
		}		
		//
		
		// expiry
		Long aExpiry = null;
		try {
			aExpiry = aContext.source("arg:expiry", Long.class);
		}
		catch (Exception e){
			try {
				String aExpiryString = aContext.source("arg:expiry", String.class);
				aExpiry = Long.parseLong(aExpiryString);
			}
			catch (Exception e2) {
				throw new Exception("the request does not have a valid - expiry - argument");
			}
		}
		//
		
		// category
		String aCategory = null;
		try {
			aCategory = aContext.source("arg:category", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - category - argument");
		}
		
		List<String> vAllowedCategories = Arrays.asList(aContext.source("rovi:allowedcategories",String.class).split("\\s*,\\s*"));
		if (! vAllowedCategories.contains(aCategory)) {
			throw new Exception("the request does not have a valid - category - argument");
		}		
		//

		// upcid
		String aUpcid = null;
		try {
			aUpcid = aContext.source("arg:upcid", String.class);
		}
		catch (Exception e){
			throw new Exception("the request does not have a valid - upcid - argument");
		}
		if (aUpcid.equals("")) {
			throw new Exception("the request does not have a valid - upcid - argument");
		}		
		//

		// compute signature for the request to Rovi
		INKFRequest rovimd5request = aContext.createRequest("active:rovimd5");
		rovimd5request.addArgumentByValue("apikey", aApikey);
		rovimd5request.addArgumentByValue("secretkey", aSecretkey);
	    rovimd5request.setRepresentationClass(String.class);
	    rovimd5request.setHeader("exclude-dependencies",true); // would always expire result otherwise
	    String vSignature = (String)aContext.issueRequest(rovimd5request);
		//
	    	    
	    
	    // rovi request with 2nd level cache
	    Object vResult = null;
	    
	    INKFRequest incacherequest = aContext.createRequest("pds:/" + aUpcid);
	    incacherequest.setVerb(INKFRequestReadOnly.VERB_EXISTS);
	    incacherequest.setRepresentationClass(Boolean.class);
	    Boolean vInCache = (Boolean)aContext.issueRequest(incacherequest);
	    
	    if (vInCache) {
	    	vResult = aContext.source("pds:/" + aUpcid);
	    }
	    else {
		    String vURL = aContext.source("rovi:" + aCategory + "releaseurl", String.class);
			INKFRequest rovireleaserequest = aContext.createRequest("active:httpGet");
			rovireleaserequest.addArgument("url", vURL + "?upcid=" + aUpcid + "&include=images,tracks&format=xml&apikey=" + aApikey + "&sig=" + vSignature);
	        rovireleaserequest.setHeader("exclude-dependencies",true); // we'll determine cachebility ourselves
	    	vResult = aContext.issueRequest(rovireleaserequest);
	    	
	    	aContext.sink("pds:/" + aUpcid, vResult);
	    }
	    //

        // response
        INKFResponse vResponse = aContext.createResponseFrom(vResult);
        vResponse.setMimeType("application/xml");
		vResponse.setExpiry(INKFResponse.EXPIRY_MIN_CONSTANT_DEPENDENT, System.currentTimeMillis() + aExpiry);
        //
	}
}
