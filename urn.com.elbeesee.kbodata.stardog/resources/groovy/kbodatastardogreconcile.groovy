/**
 *
 * Elephant Bird Consulting - KBO data.
 * 
 * @author Tom Geudens. 2014/01/07.
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
 * KBO data Reconcile Accessor.
 *
 * @param  database  database name
 * @param  expiry  expiry 
 * @param  credentials  http credentials
 * 
 * @param  reconcilequery  reconcile query jsonobject
 * @param  baseurl  base url
 * 
 */


// context
INKFRequestContext aContext = (INKFRequestContext)context;
//

// reconcilequery
JSONObject aReconcileQuery = null;
if (aContext.getThisRequest().argumentExists("reconcilequery")) {
	try {
		aReconcileQuery = aContext.source("arg:reconcilequery", JSONObject.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - reconcilequery - argument");
	}
}  // no else needed, the grammar enforces the presence of a reconcilequery argument
//

String vSearch = null;
Long vLimit = null;
String vType = null;

try {
	vSearch = aReconcileQuery.getString("query");
}
catch (JSONException e) {
	throw new NKFException("reconcilequery does not contain a valid - query - attribute");
}
try {
	vLimit = aReconcileQuery.getLong("limit");
}
catch (JSONException e) {
	// not a problem, limit is not mandatory
}
try {
	vType = aReconcileQuery.getString("type");
}
catch (JSONException e) {
	// not a problem, type is not mandatory
}

// database (optional)
String aDatabase = null;
if (aContext.getThisRequest().argumentExists("database")) {
	try {
		aDatabase = aContext.source("arg:database", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - database - argument");
	}
}
else {
	try {
		aDatabase = aContext.source("kbodata:database", String.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:database - does not exist");
	}
}
//

// expiry (optional)
Long aExpiry = null;
if (aContext.getThisRequest().argumentExists("expiry")) {
	try {
		aExpiry = aContext.source("arg:expiry", Long.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - expiry - argument");
	}
}
else {
	try {
		aExpiry = aContext.source("kbodata:expiry", Long.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:expiry - does not exist");
	}
}
//

// credentials (optional)
IHDSNode aCredentials = null;
if (aContext.getThisRequest().argumentExists("credentials")) {
	try {
		aCredentials = aContext.source("arg:credentials", IHDSNode.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - credentials - argument");
	}
}
else {
	try {
		aCredentials = aContext.source("kbodata:credentials", IHDSNode.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:credentials - does not exist");
	}
}

// baseurl (optional)
String aBaseURL = null;
if (aContext.getThisRequest().argumentExists("baseurl")) {
	try {
		aBaseURL = aContext.source("arg:baseurl", String.class);
	}
	catch (Exception e) {
		throw new NKFException("request does not have a valid - baseurl - argument");
	}
}
else {
	try {
		aBaseURL = aContext.source("kbodata:baseurl", String.class);
	}
	catch (Exception e) {
		throw new NKFException("resource - kbodata:baseurl - does not exist");
	}
}
//

INKFRequest stardogreconcilerequest = aContext.createRequest("active:stardogreconcile");
stardogreconcilerequest.addArgumentByValue("database", aDatabase);
stardogreconcilerequest.addArgumentByValue("search",vSearch);
stardogreconcilerequest.addArgumentByValue("baseurl", aBaseURL);
if (vType != null) {
	stardogreconcilerequest.addArgumentByValue("type", vType);
}
if (vLimit != null) {
	stardogreconcilerequest.addArgumentByValue("limit", vLimit);
}
stardogreconcilerequest.addArgumentByValue("expiry", aExpiry);
stardogreconcilerequest.addArgumentByValue("credentials", aCredentials);
Object vResult = aContext.issueRequest(stardogreconcilerequest);

// response
INKFResponse vResponse = null;
vResponse = aContext.createResponseFrom(vResult);
vResponse.setMimeType("text/plain");
vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
//
