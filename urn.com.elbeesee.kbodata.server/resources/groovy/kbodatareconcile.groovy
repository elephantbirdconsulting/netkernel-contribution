/**
 *
 * Elephant Bird Consulting - KBO data.
 *
 * @author Tom Geudens. 2014/01/06.
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
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * KBO data reconcile Accessor.
 *
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

IHDSNode vHTTPParams = aContext.source("httpRequest:/params", IHDSNode.class);
IHDSNode vQuery = vHTTPParams.getFirstNode("query");
IHDSNode vQueries = vHTTPParams.getFirstNode("queries");
IHDSNode vCallback = vHTTPParams.getFirstNode("callback");

StringBuilder vResult = new StringBuilder("{");

// processing
if ((vQuery == null) && (vQueries == null)) {
	vResult.append(aContext.source("res:/resources/json/metadata.json", String.class));
}
//
else {
	if (vQuery != null) {
		// single query
		JSONObject vReconcileQuery = null;
		try {
			vReconcileQuery = new JSONObject((String)vQuery.getValue());
		}
		catch (JSONException e) {
			INKFRequest buildreconcilequery = aContext.createRequest("active:freemarker");
			buildreconcilequery.addArgument("operator","res:/resources/freemarker/reconcilequery.freemarker");
			buildreconcilequery.addArgumentByValue("value", JSONObject.quote((String)vQuery.getValue()));
			buildreconcilequery.setRepresentationClass(String.class);
			vReconcileQuery = new JSONObject((String)aContext.issueRequest(buildreconcilequery));
		}
		INKFRequest reconcilerequest = aContext.createRequest("active:kbodatastardogreconcile");
		reconcilerequest.addArgumentByValue("reconcilequery", vReconcileQuery);
		reconcilerequest.setRepresentationClass(String.class);
		vResult.append(aContext.issueRequest(reconcilerequest));
	}
	else {
		// multiple queries
		JSONObject vReconcileQueries = null;
		try {
			vReconcileQueries = new JSONObject((String)vQueries.getValue());
		}
		catch (JSONException e) {
			throw new NKFException("resource - httpRequest:/param/queries - is not valid");
		}
		
		HashMap<String, INKFAsyncRequestHandle> vResponseMap = new HashMap<String, INKFAsyncRequestHandle>();
		
		for (String vKey : vReconcileQueries.keys()) {
			JSONObject vReconcileQuery = null;
			
			try {
				vReconcileQuery = vReconcileQueries.get(vKey);
			}
			catch (JSONException e) {
				throw new NKFException("resource - httpRequest:/param/queries - is not valid");
			}

			INKFRequest reconcilerequest = aContext.createRequest("active:kbodatastardogreconcile");
			reconcilerequest.addArgumentByValue("reconcilequery", vReconcileQuery);
			reconcilerequest.setRepresentationClass(String.class);
			
			INKFAsyncRequestHandle vHandle = aContext.issueAsyncRequest(reconcilerequest);
			vResponseMap.put(vKey,vHandle);
		}

		for (Map.Entry<String, INKFAsyncRequestHandle> vEntry : vResponseMap.entrySet()) {
			String vKey = vEntry.getKey();
			INKFAsyncRequestHandle vHandle = vEntry.getValue();
			
			StringBuilder vSingleResult = new StringBuilder("\"" + vKey + "\": {");
			
			Object vResponse = vHandle.join(5000 * vResponseMap.size());
			if (vResponse != null) {
				vSingleResult.append((String)vResponse);
			}
			vSingleResult.append("}");
			
			if (vResult.toString() != '{') {
				vResult.append(",");
			}
			vResult.append(vSingleResult);
		}
	}
}

vResult.append("}");

// response
if (vCallback == null ) {
	INKFResponse vResponse = aContext.createResponseFrom(vResult.toString());
	
	String vCORSOrigin = null;
	try {
		vCORSOrigin = aContext.source("httpRequest:/header/Origin", String.class);
	}
	catch (Exception e){
		//
	}
	if (vCORSOrigin != null) {
		// No CORS verfication yet, I just allow the origin
		vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin",vCORSOrigin);
	}
	vResponse.setMimeType("application/json");
	vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
}
else {
	INKFRequest callback = aContext.createRequest("active:freemarker");
	callback.addArgument("operator", "res:/resources/freemarker/callback.freemarker");
	callback.addArgumentByValue("callback", (String)vCallback.getValue());
	callback.addArgumentByValue("towrap", vResult.toString());
	callback.setRepresentationClass(String.class);
	String vResultCallback = (String)aContext.issueRequest(callback);
	
	INKFResponse vResponse = aContext.createResponseFrom(vResultCallback);
	vResponse.setMimeType("application/javascript");
	vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
}
//
