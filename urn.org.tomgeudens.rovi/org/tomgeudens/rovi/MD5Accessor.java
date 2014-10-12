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

/**
 * MD5 Accessor.
 * 
 * @param apikey
 *            rovi apikey
 * @param secretkey
 *            rovi secret key
 * 
 */

public class MD5Accessor extends StandardAccessorImpl {
	public MD5Accessor() {
		this.declareThreadSafe();
		this.declareArgument(new SourcedArgumentMetaImpl("apikey", null, null,new Class[] { String.class }));
		this.declareArgument(new SourcedArgumentMetaImpl("secretkey", null,null, new Class[] { String.class }));
		this.declareSourceRepresentation(String.class);
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		
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
		
		long vUnixTime = System.currentTimeMillis() / 1000L;
		String vUnixString = Long.toString(vUnixTime);
		String vMD5String = aApikey + aSecretkey + vUnixString;

		INKFRequest md5request = aContext.createRequest("active:md5");
		md5request.addArgumentByValue("operand", vMD5String);
		md5request.setRepresentationClass(String.class);
		String vHash = (String)aContext.issueRequest(md5request);
		
		INKFResponse vResponse = aContext.createResponseFrom(vHash);
		vResponse.setMimeType("text/plain");
		vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);		
	}
}
