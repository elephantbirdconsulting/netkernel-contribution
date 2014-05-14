package com.elbeesee.triplestore.utility;

/**
 *
 * Elephant Bird Consulting - triplestore utility.
 * 
 * @author Tom Geudens. 2014/05/11.
 * 
 */

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
// import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

/**
 * Processing Imports.
 */
import org.netkernel.layer0.representation.IReadableBinaryStreamRepresentation;

/**
 * 
 * RDFXML Transform Accessor.
 *
 * @param  operand  incoming rdfxml
 * 
 */


public class RDFXMLTransformAccessor extends StandardAccessorImpl {
	public RDFXMLTransformAccessor() {
		this.declareThreadSafe();
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		String vActiveType = aContext.getThisRequest().getArgumentValue("activeType");

		INKFRequest jenaparserequest = aContext.createRequest("active:jRDFParseXML");
		jenaparserequest.addArgument("operand","arg:operand");
		Object vJenaParseResult = aContext.issueRequest(jenaparserequest);

		INKFRequest modelemptyrequest = aContext.createRequest("active:jRDFModelIsEmpty");
		modelemptyrequest.addArgumentByValue("operand", vJenaParseResult);
		modelemptyrequest.setRepresentationClass(Boolean.class);
		Boolean vIsModelEmpty = (Boolean)aContext.issueRequest(modelemptyrequest);

		IReadableBinaryStreamRepresentation vRBS = null;
		String vMimetype = null;
		INKFRequest jenaserializerequest = null;
		
		if (vActiveType.equals("rdfxml2rdfxml")) {
			jenaserializerequest = aContext.createRequest("active:jRDFSerializeXML");
			vMimetype = "application/rdf+xml";
		}
		else if (vActiveType.equals("rdfxml2turtle")) {
			jenaserializerequest = aContext.createRequest("active:jRDFSerializeTURTLE");
			vMimetype = "text/turtle";			
		}
		else if (vActiveType.equals("rdfxml2ntriple")) {
			jenaserializerequest = aContext.createRequest("active:jRDFSerializeN-TRIPLE");
			vMimetype = "text/plain";			
		}
		else if (vActiveType.equals("rdfxml2jsonld")) {
			jenaserializerequest = aContext.createRequest("active:jRDFSerializeJSONLD");
			vMimetype = "application/ld+json";			
		}
		else {
			jenaserializerequest = aContext.createRequest("active:jRDFSerializeXML");
			vMimetype = "application/rdf+xml";			
		}
		jenaserializerequest.addArgumentByValue("operand",vJenaParseResult);
		jenaserializerequest.setRepresentationClass(IReadableBinaryStreamRepresentation.class);
		vRBS = (IReadableBinaryStreamRepresentation)aContext.issueRequest(jenaserializerequest);		
		
		// response
		INKFResponse vResponse = null;
		vResponse = aContext.createResponseFrom(vRBS);
		vResponse.setMimeType(vMimetype);
		String vCORSOrigin = null;
		try {
			vCORSOrigin = aContext.source("httpRequest:/header/Origin", String.class);
		}
		catch (Exception e){
			//
		}
		if (vCORSOrigin != null) {
			// No CORS verification yet, I just allow the origin
			vResponse.setHeader("httpResponse:/header/Access-Control-Allow-Origin",vCORSOrigin);
		}
		if (vIsModelEmpty) {
			vResponse.setHeader("httpResponse:/code",404);
		}
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
