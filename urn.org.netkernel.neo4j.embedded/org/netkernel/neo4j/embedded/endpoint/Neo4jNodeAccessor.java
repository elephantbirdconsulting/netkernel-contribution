package org.netkernel.neo4j.embedded.endpoint;

// Author: Tom Geudens
// Date  : 2012/09/25

// The usual suspects for an accessor
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

// Processing
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.netkernel.neo4j.embedded.representation.Neo4jNode;
import org.netkernel.neo4j.embedded.representation.Neo4jInstance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Neo4jNodeAccessor extends StandardAccessorImpl {
	protected static ConcurrentHashMap<Long, Node> mNodes;

	class NodeExpiry implements INKFExpiryFunction {
		private Node mNode;
		
		public NodeExpiry(Node aNode) {
			mNode = aNode;
		}

		@Override
		public boolean isExpired(long aNow) {
			if (! mNode.equals(mNodes.get(mNode.getId())) ) {
				return false;
			}
			else {
				mNodes.remove(mNode.getId());
				return true;
			}
		}
	}

	public Neo4jNodeAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(Neo4jNode.class);
		this.declareArgument(new SourcedArgumentMetaImpl("graph",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("entity",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("instance",null,null,new Class[] {Neo4jInstance.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("node",null,null,new Class[] {Neo4jNode.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("properties",null,null,new Class[] {HashMap.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("id",null,null,new Class[] {Long.class}));
		mNodes = new ConcurrentHashMap<Long, Node>();
	}

	public void onNew(INKFRequestContext aContext) throws Exception {
		// NEW requires three arguments, graph, entity and instance
		
		// Verify graph
		String aGraph = null;
		if (aContext.getThisRequest().argumentExists("graph")) {
			aGraph = aContext.getThisRequest().getArgumentValue("graph");
			if (aGraph.equals("pbv:graph")) {
				aGraph = aContext.source("arg:graph",String.class);
			}
		}
		else {
			throw new NKFException("request does not have the required - graph - argument");
		}
		if (aGraph.equals("")) {
			throw new NKFException("request does not have a valid - graph - argument");
		}		

		// Verify entity
		String aEntity = null;
		if (aContext.getThisRequest().argumentExists("entity")) {
			aEntity = aContext.getThisRequest().getArgumentValue("entity");
			if (aEntity.equals("pbv:entity")) {
				aEntity = aContext.source("arg:entity",String.class);
			}
		}
		else {
			throw new NKFException("request does not have the required - entity - argument");
		}
		if (aEntity.equals("")) {
			throw new NKFException("request does not have a valid - entity - argument");
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
		Node vReferenceNode = null;
		try {
			vInstance = aInstanceRepresentation.getInstance();
			vReferenceNode = vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}
		
		// Create Node
		Node vNode = null;
		vNode = vInstance.createNode();

		INKFRequest subrequest = aContext.createRequest("active:neo4jrelationship");
		subrequest.addArgument("name", aGraph);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.addArgumentByValue("outgoing", new Neo4jNode(vReferenceNode));
		subrequest.addArgumentByValue("incoming", new Neo4jNode(vNode));
		subrequest.setVerb(INKFRequestReadOnly.VERB_NEW);
		aContext.issueRequest(subrequest);

		subrequest = aContext.createRequest("active:neo4jrelationship");
		subrequest.addArgument("name", aEntity);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.addArgumentByValue("outgoing", new Neo4jNode(vReferenceNode));
		subrequest.addArgumentByValue("incoming", new Neo4jNode(vNode));
		subrequest.setVerb(INKFRequestReadOnly.VERB_NEW);
		aContext.issueRequest(subrequest);
		
		aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_NODE_CREATE", vNode.getId());

		subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:nodes");
		aContext.issueRequest(subrequest);
		
		subrequest = aContext.createRequest("active:neo4jnode");
		subrequest.addArgumentByValue("id", vNode.getId());
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jNode.class);

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

		// Retrieve Node
		Node vNode = null;
		try {
			if (mNodes.containsKey(aId)) {
				vNode = mNodes.get(aId);
			}
			else {
				vNode = vInstance.getNodeById(aId);
				mNodes.put(aId, vNode);
			}
			INKFRequest subrequest = aContext.createRequest("active:attachGoldenThread");
			subrequest.addArgument("id", "gt:neo4j:nodes");
			aContext.issueRequest(subrequest);

			Neo4jNode vNodeRepresentation = new Neo4jNode(vNode);
			INKFResponse vResponse = aContext.createResponseFrom(vNodeRepresentation);
			vResponse.setExpiry(INKFResponse.EXPIRY_FUNCTION, new NodeExpiry(vNode));
		}
		catch (Exception e) {
			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_NODE_ID_NOT_VALID", aId);
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

		// Retrieve Node
		Neo4jNode vNodeRepresentation = null;
		INKFRequest subrequest = aContext.createRequest("active:neo4jnode");
		subrequest.addArgumentByValue("id", aId);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jNode.class);
		vNodeRepresentation = (Neo4jNode)aContext.issueRequest(subrequest);
		
		if (vNodeRepresentation != null) {
			INKFResponse vResponse = aContext.createResponseFrom(true);
			vResponse.setExpiry(INKFResponse.EXPIRY_FUNCTION, new NodeExpiry(vNodeRepresentation.getNode()));
		}
		else {
			INKFResponse vResponse = aContext.createResponseFrom(false);
			vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
		}
	}

	@SuppressWarnings("unchecked")
	public void onSink(INKFRequestContext aContext) throws Exception {
		// SINK requires one argument, a node
		// and also requires properties

		// Verify node
		Neo4jNode aNodeRepresentation = null;
		if (aContext.getThisRequest().argumentExists("node")) {
			aNodeRepresentation = aContext.source("arg:node",Neo4jNode.class);
		}
		else {
			throw new NKFException("request does not have the required - node - argument");
		}

		Node vNode = null;
		HashMap<String, Object> vProperties = null;
		try {
			vNode = aNodeRepresentation.getNode();
			vProperties = aNodeRepresentation.getProperties();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - node - argument");
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
			vNode.setProperty(vEntry.getKey(),vEntry.getValue());
		}

		for(Map.Entry<String, Object> vEntry: vProperties.entrySet()) {
			if (! aProperties.containsKey(vEntry.getKey()) ) {
				vNode.removeProperty(vEntry.getKey());
			}
		}
		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:nodes");
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

		// Retrieve Node
		Neo4jNode vNodeRepresentation = null;
		INKFRequest subrequest = aContext.createRequest("active:neo4jnode");
		subrequest.addArgumentByValue("id", aId);
		subrequest.addArgumentByValue("instance", aInstanceRepresentation);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jNode.class);
		vNodeRepresentation = (Neo4jNode)aContext.issueRequest(subrequest);

		if (vNodeRepresentation != null) {
			try {
				Node vNode = vNodeRepresentation.getNode();
				for(Relationship vRelationship: vNode.getRelationships()) {
					subrequest = aContext.createRequest("active:neo4jrelationship");
					subrequest.addArgumentByValue("id", vRelationship.getId());
					subrequest.addArgumentByValue("instance", aInstanceRepresentation);
					subrequest.setVerb(INKFRequestReadOnly.VERB_DELETE);
					aContext.issueRequest(subrequest);
				}
				mNodes.remove(vNode.getId());
				vNode.delete();

				subrequest = aContext.createRequest("active:cutGoldenThread");
				subrequest.addArgument("id", "gt:neo4j:nodes");
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