package org.netkernel.neo4j.embedded.endpoint;

// Author: Tom Geudens
// Date  : 2012/10/03

// The usual suspects for an accessor
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

import org.neo4j.graphdb.Direction;
// Processing
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Relationship;
import org.netkernel.neo4j.embedded.representation.Neo4jNode;
import org.netkernel.neo4j.embedded.representation.Neo4jInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Neo4jRetrieveAccessor extends StandardAccessorImpl {

	public Neo4jRetrieveAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(Neo4jNode[].class);
		this.declareArgument(new SourcedArgumentMetaImpl("graph",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("entity",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("properties",null,null,new Class[] {HashMap.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("instance",null,null,new Class[] {Neo4jInstance.class}));
	}

	@SuppressWarnings("unchecked")
	public void onSource(INKFRequestContext aContext) throws Exception {
		// SOURCE requires one argument instance and either the graph or the entity argument.
		// The properties argument is optional.	

		// Verify graph
		String aGraph = null;
		if (aContext.getThisRequest().argumentExists("graph")) {
			aGraph = aContext.getThisRequest().getArgumentValue("graph");
			if (aGraph.equals("pbv:graph")) {
				aGraph = aContext.source("arg:graph",String.class);
			}
		}
		if ((aGraph != null) && (aGraph.equals(""))) {
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
		if ((aEntity != null) && (aEntity.equals(""))) {
			throw new NKFException("request does not have a valid - entity - argument");
		}
		
		// Verify we have either graph or entity
		if (! ( (aEntity == null) ^ (aGraph == null) ) ) {			
			throw new NKFException("request requires either (!) a graph or an entity argument");
		}

		// Verify properties
		HashMap<String, Object> aProperties = null;
		if (aContext.getThisRequest().argumentExists("properties")) {
			aProperties = aContext.source("arg:properties",HashMap.class);
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
			vInstance.getReferenceNode();
		}
		catch (Exception e) {			
			throw new NKFException("request does not have a valid - instance - argument");
		}
		
		DynamicRelationshipType vRelationshipType = DynamicRelationshipType.withName((aGraph != null) ? aGraph : aEntity);
		
		List<Neo4jNode> vResult = new ArrayList<Neo4jNode>();
		for(Relationship vRelationship: vReferenceNode.getRelationships(Direction.OUTGOING, vRelationshipType)) {
			Node vNode = vRelationship.getEndNode();
			
			Boolean vToAdd = true;
			
			if (aProperties != null) {
				for(Map.Entry<String, Object> vEntry: aProperties.entrySet()) {
					if (vNode.hasProperty(vEntry.getKey())) {
						if ( ! vEntry.getValue().equals(vNode.getProperty(vEntry.getKey())) ) {
							vToAdd = false;
							break;
						}
					}
					else {
						vToAdd = false;
						break;
					}
				}
			}			
			if (vToAdd) {
				INKFRequest subrequest = aContext.createRequest("active:neo4jnode");
				subrequest.addArgumentByValue("id", vNode.getId());
				subrequest.addArgumentByValue("instance", aInstanceRepresentation);
				subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
				subrequest.setRepresentationClass(Neo4jNode.class);
				
				vResult.add((Neo4jNode)aContext.issueRequest(subrequest));
			}
		}
		aContext.createResponseFrom((Neo4jNode[])vResult.toArray((new Neo4jNode[0])));
	}
}