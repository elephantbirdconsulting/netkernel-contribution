package org.netkernel.neo4j.embedded.endpoint;

// Author: Tom Geudens
// Date  : 2012/10/04

// The usual suspects for an accessor
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

// Processing
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Relationship;
import org.netkernel.neo4j.embedded.representation.Neo4jRelationship;
import org.netkernel.neo4j.embedded.representation.Neo4jNode;
import org.netkernel.neo4j.embedded.representation.Neo4jInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Neo4jRelationshipAccessor extends StandardAccessorImpl {
	protected static ConcurrentHashMap<Long, Relationship> mRelationships;

	class RelationshipExpiry implements INKFExpiryFunction {
		private Relationship mRelationship;
		
		public RelationshipExpiry(Relationship aRelationship) {
			mRelationship = aRelationship;
		}

		@Override
		public boolean isExpired(long aNow) {
			if (! mRelationship.equals(mRelationships.get(mRelationship.getId())) ) {
				return false;
			}
			else {
				mRelationships.remove(mRelationship.getId());
				return true;
			}
		}
	}

	public Neo4jRelationshipAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(Neo4jRelationship.class);
		this.declareArgument(new SourcedArgumentMetaImpl("name",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("outgoing",null,null,new Class[] {Neo4jNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("incoming",null,null,new Class[] {Neo4jNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("instance",null,null,new Class[] {Neo4jInstance.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("properties",null,null,new Class[] {HashMap.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("relationship",null,null,new Class[] {Neo4jRelationship.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("id",null,null,new Class[] {Long.class}));
		mRelationships = new ConcurrentHashMap<Long, Relationship>();
	}

	public void onNew(INKFRequestContext aContext) throws Exception {
		// NEW requires four arguments, name, incoming, outgoing and instance
		
		// Verify name
		String aName = null;
		if (aContext.getThisRequest().argumentExists("name")) {
			aName = aContext.getThisRequest().getArgumentValue("name");
			if (aName.equals("pbv:name")) {
				aName = aContext.source("arg:name",String.class);
			}
		}
		else {
			throw new NKFException("request does not have the required - name - argument");
		}
		if (aName.equals("")) {
			throw new NKFException("request does not have a valid - name - argument");
		}
		
		// Verify incoming
		Neo4jNode aIncomingNodeRepresentation = null;
		if (aContext.getThisRequest().argumentExists("incoming")) {
			aIncomingNodeRepresentation = aContext.source("arg:incoming",Neo4jNode.class);
		}
		else {
			throw new NKFException("request does not have the required - incoming - argument");
		}
		Node vIncomingNode = null;
		try {
			vIncomingNode = aIncomingNodeRepresentation.getNode();
		}
		catch (Exception e) {
			throw new NKFException("request does not have the required - incoming - argument");			
		}

		// Verify outgoing
		Neo4jNode aOutgoingNodeRepresentation = null;
		if (aContext.getThisRequest().argumentExists("outgoing")) {
			aOutgoingNodeRepresentation = aContext.source("arg:outgoing",Neo4jNode.class);
		}
		else {
			throw new NKFException("request does not have the required - outgoing - argument");
		}
		Node vOutgoingNode = null;
		try {
			vOutgoingNode = aOutgoingNodeRepresentation.getNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have the required - outgoing - argument");			
		}

		// Verify instance
		Neo4jInstance aInstanceRepresentation = null;
		if (aContext.getThisRequest().argumentExists("instance")) {
			aInstanceRepresentation = aContext.source("arg:instance",Neo4jInstance.class);
		}
		else {
			throw new NKFException("request does not have the required - instance - argument");
		}
		@SuppressWarnings("unused")
		GraphDatabaseService vInstance = null;
		// Node vReferenceNode = null;
		try {
			vInstance = aInstanceRepresentation.getInstance();
			// vReferenceNode = vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}
		
		// Create Relationship
		Relationship vRelationship = null;
		
		// Flesh out
		DynamicRelationshipType vRelationshipType = DynamicRelationshipType.withName(aName);
		vRelationship = vOutgoingNode.createRelationshipTo(vIncomingNode, vRelationshipType);

		aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_RELATIONSHIP_CREATE", vRelationship.getId());

		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:relationships");
		aContext.issueRequest(subrequest);
		
		subrequest = aContext.createRequest("active:neo4jrelationship");
		subrequest.addArgumentByValue("id", vRelationship.getId());
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jRelationship.class);

		aContext.createResponseFrom(aContext.issueRequestForResponse(subrequest));
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		// SOURCE requires two arguments, instance and id

		// Verify id
		Long aId = null;
		if (aContext.getThisRequest().argumentExists("id")) {
			aId = aContext.source("arg:id",Long.class);
		}
		else {
			throw new NKFException("request does not have the required - id - argument");
		}
		if (aId == 0) {
			throw new NKFException("request does not have a valid - id - argument");
		}		

		// Verify instance
		Neo4jInstance aInstanceRepresentation = null;
		if (aContext.getThisRequest().argumentExists("instance")) {
			aInstanceRepresentation = aContext.source("arg:instance",Neo4jInstance.class);
		}
		else {
			throw new NKFException("request does not have the required - instance - argument");
		}
		GraphDatabaseService vInstance = null;
		// Node vReferenceNode = null;
		try {
			vInstance = aInstanceRepresentation.getInstance();
			// vReferenceNode = vInstance.getReferenceNode();
			vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}

		// Retrieve Relationship
		Relationship vRelationship = null;
		try {
			if (mRelationships.containsKey(aId)) {
				vRelationship = mRelationships.get(aId);
			}
			else {
				vRelationship = vInstance.getRelationshipById(aId);
				mRelationships.put(aId, vRelationship);
			}
			INKFRequest subrequest = aContext.createRequest("active:attachGoldenThread");
			subrequest.addArgument("id", "gt:neo4j:relationships");
			aContext.issueRequest(subrequest);

			Neo4jRelationship vRelationshipRepresentation = new Neo4jRelationship(vRelationship);
			INKFResponse vResponse = aContext.createResponseFrom(vRelationshipRepresentation);
			vResponse.setExpiry(INKFResponse.EXPIRY_FUNCTION, new RelationshipExpiry(vRelationship));
		}
		catch (Exception e) {
			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_RELATIONSHIP_ID_NOT_VALID", aId);
			INKFResponse vResponse = aContext.createResponseFrom(null);
			vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
		}
	}

	public void onExists(INKFRequestContext aContext) throws Exception {
		// EXIST requires two arguments, instance and id

		// Verify id
		Long aId = null;
		if (aContext.getThisRequest().argumentExists("id")) {
			aId = aContext.source("arg:id",Long.class);
		}
		else {
			throw new NKFException("request does not have the required - id - argument");
		}
		if (aId == 0) {
			throw new NKFException("request does not have a valid - id - argument");
		}		

		// Verify instance
		Neo4jInstance aInstanceRepresentation = null;
		if (aContext.getThisRequest().argumentExists("instance")) {
			aInstanceRepresentation = aContext.source("arg:instance",Neo4jInstance.class);
		}
		else {
			throw new NKFException("request does not have the required - instance - argument");
		}
		GraphDatabaseService vInstance = null;
		// Node vReferenceNode = null;
		try {
			vInstance = aInstanceRepresentation.getInstance();
			// vReferenceNode = vInstance.getReferenceNode();
			vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}

		// Retrieve Relationship
		Neo4jRelationship vRelationshipRepresentation = null;
		INKFRequest subrequest = aContext.createRequest("active:neo4jrelationship");
		subrequest.addArgumentByValue("id", aId);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jRelationship.class);
		vRelationshipRepresentation = (Neo4jRelationship)aContext.issueRequest(subrequest);
		
		if (vRelationshipRepresentation != null) {
			INKFResponse vResponse = aContext.createResponseFrom(true);
			vResponse.setExpiry(INKFResponse.EXPIRY_FUNCTION, new RelationshipExpiry(vRelationshipRepresentation.getRelationship()));
		}
		else {
			INKFResponse vResponse = aContext.createResponseFrom(false);
			vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
		}

	}

	@SuppressWarnings("unchecked")
	public void onSink(INKFRequestContext aContext) throws Exception {
		// SINK requires one argument, a relationship
		// and also requires properties

		// Verify relationship
		Neo4jRelationship aRelationshipRepresentation = null;
		if (aContext.getThisRequest().argumentExists("relationship")) {
			aRelationshipRepresentation = aContext.source("arg:relationship",Neo4jRelationship.class);
		}
		else {
			throw new NKFException("request does not have the required - relationship - argument");
		}

		Relationship vRelationship = null;
		HashMap<String, Object> vProperties = null;
		try {
			vRelationship = aRelationshipRepresentation.getRelationship();
			vProperties = aRelationshipRepresentation.getProperties();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - relationship - argument");
		}	
		
		// Verify properties
		HashMap<String, Object> aProperties = null;
		if (aContext.getThisRequest().argumentExists("properties")) {
			aProperties = aContext.source("arg:properties",HashMap.class);
		}
		else {
			throw new NKFException("request does not have the required - properties - argument");
		}
		
		// Process properties
		for(Map.Entry<String, Object> vEntry: aProperties.entrySet()) {
			vRelationship.setProperty(vEntry.getKey(),vEntry.getValue());
		}

		for(Map.Entry<String, Object> vEntry: vProperties.entrySet()) {
			if (! aProperties.containsKey(vEntry.getKey()) ) {
				vRelationship.removeProperty(vEntry.getKey());
			}
		}
		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:relationships");
		aContext.issueRequest(subrequest);

	}

	public void onDelete(INKFRequestContext aContext) throws Exception {
		// DELETE requires two arguments, instance and id

		// Verify id
		Long aId = null;
		if (aContext.getThisRequest().argumentExists("id")) {
			aId = aContext.source("arg:id",Long.class);
		}
		else {
			throw new NKFException("request does not have the required - id - argument");
		}
		if (aId == 0) {
			throw new NKFException("request does not have a valid - id - argument");
		}		

		// Verify instance
		Neo4jInstance aInstanceRepresentation = null;
		if (aContext.getThisRequest().argumentExists("instance")) {
			aInstanceRepresentation = aContext.source("arg:instance",Neo4jInstance.class);
		}
		else {
			throw new NKFException("request does not have the required - instance - argument");
		}
		GraphDatabaseService vInstance = null;
		// Node vReferenceNode = null;
		try {
			vInstance = aInstanceRepresentation.getInstance();
			// vReferenceNode = vInstance.getReferenceNode();
			vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}

		// Retrieve Relationship
		Neo4jRelationship vRelationshipRepresentation = null;
		INKFRequest subrequest = aContext.createRequest("active:neo4jrelationship");
		subrequest.addArgumentByValue("id", aId);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jRelationship.class);
		vRelationshipRepresentation = (Neo4jRelationship)aContext.issueRequest(subrequest);
		
		if (vRelationshipRepresentation != null) {
			try {
				Relationship vRelationship = vRelationshipRepresentation.getRelationship();
				mRelationships.remove(vRelationship.getId());
				vRelationship.delete();

				subrequest = aContext.createRequest("active:cutGoldenThread");
				subrequest.addArgument("id", "gt:neo4j:relationships");
				aContext.issueRequest(subrequest);
				
				aContext.createResponseFrom(true);
			}
			catch (Exception e) {
				aContext.createResponseFrom(false);
			}
		}
		else {
			aContext.createResponseFrom(false);
		}
	}
}