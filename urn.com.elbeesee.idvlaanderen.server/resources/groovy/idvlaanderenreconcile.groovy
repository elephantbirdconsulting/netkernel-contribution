/**
 *
 * Elephant Bird Consulting - ID Vlaanderen data.
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
import org.json.JSONObject;
import org.json.JSONException;

/**
 *
 * ID Vlaanderen data reconcile Accessor.
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
		
		String vSearch = null;
		try {
			vSearch = vReconcileQuery.getString("query");
		}
		catch (JSONException e) {
			throw new NKFException("reconcilequery does not contain a valid - query - attribute");
		}
		
		Long vLimit = null;
		try {
			vLimit = vReconcileQuery.getLong("limit");
		}
		catch (JSONException e) {
			// not a problem, limit is not mandatory, it may however have been passed as a url param
			try {
				String aLimit = aContext.source("httpRequest:/param/limit", String.class);
				if (aLimit != null) {
					vLimit = Long.parseLong(aLimit);
				}
			}
			catch (Exception e2) {
				// well, we tried
			}
		}
		
		String vType = null;
		try {
			vType = vReconcileQuery.getString("type");
		}
		catch (JSONException e) {
			// not a problem, type is not mandatory
		}

		INKFRequest reconcilerequest = aContext.createRequest("active:reconcile");
		reconcilerequest.addArgument("database", "idvlaanderen:database");
		reconcilerequest.addArgument("expiry", "idvlaanderen:expiry");
		reconcilerequest.addArgument("credentials", "idvlaanderen:credentials");
		reconcilerequest.addArgumentByValue("search", vSearch);
		reconcilerequest.addArgument("baseurl", "idvlaanderen:baseurl");
		if (vLimit != null) {
			reconcilerequest.addArgumentByValue("limit", vLimit);
		}
		if (vType != null) {
			reconcilerequest.addArgumentByValue("type", vType);
		}				
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
			
			String vSearch = null;
			try {
				vSearch = vReconcileQuery.getString("query");
			}
			catch (JSONException e) {
				throw new NKFException("reconcilequery does not contain a valid - query - attribute");
			}
			
			Long vLimit = null;
			try {
				vLimit = vReconcileQuery.getLong("limit");
			}
			catch (JSONException e) {
				// not a problem, limit is not mandatory
			}
			
			String vType = null;
			try {
				vType = vReconcileQuery.getString("type");
			}
			catch (JSONException e) {
				// not a problem, type is not mandatory
			}
	
			INKFRequest reconcilerequest = aContext.createRequest("active:reconcile");
			reconcilerequest.addArgument("database", "idvlaanderen:database");
			reconcilerequest.addArgument("expiry", "idvlaanderen:expiry");
			reconcilerequest.addArgument("credentials", "idvlaanderen:credentials");
			reconcilerequest.addArgumentByValue("search", vSearch);
			reconcilerequest.addArgument("baseurl", "idvlaanderen:baseurl");
			if (vLimit != null) {
				reconcilerequest.addArgumentByValue("limit", vLimit);
			}
			if (vType != null) {
				reconcilerequest.addArgumentByValue("type", vType);
			}
			reconcilerequest.setRepresentationClass(String.class);
			
			StringBuilder vSingleResult = new StringBuilder("\"" + vKey + "\": {");
			vSingleResult.append(aContext.issueRequest(reconcilerequest));
			vSingleResult.append("}");
			
			if (vResult.toString() != '{') {
				vResult.append(",");
			}
			vResult.append(vSingleResult);
			
			//INKFAsyncRequestHandle vHandle = aContext.issueAsyncRequest(reconcilerequest);
			//vResponseMap.put(vKey,vHandle);
		}

		//for (Map.Entry<String, INKFAsyncRequestHandle> vEntry : vResponseMap.entrySet()) {
		//	String vKey = vEntry.getKey();
		//	INKFAsyncRequestHandle vHandle = vEntry.getValue();
			
		//	println "Joining " + vKey;
		//	StringBuilder vSingleResult = new StringBuilder("\"" + vKey + "\": {");
			
		//	Object vResponse = vHandle.join();
		//	if (vResponse != null) {
		//		vSingleResult.append((String)vResponse);
		//	}
		//	vSingleResult.append("}");
			
		//	if (vResult.toString() != '{') {
		//		vResult.append(",");
		//	}
		//	vResult.append(vSingleResult);
		//}
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
		// No CORS verfication yet, I just allow everything
		vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin","*");
	}
	vResponse.setHeader("httpResponse:/header/Vary","Accept");
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
	vResponse.setHeader("httpResponse:/header/Vary","Accept");
	vResponse.setMimeType("application/javascript");
	vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
}
//
