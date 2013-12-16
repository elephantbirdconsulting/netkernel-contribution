package org.netkernel.neo4j.embedded.representation;

import java.util.HashMap;
import org.neo4j.graphdb.Relationship;

public class Neo4jRelationship {
	private final Relationship mRelationship;
	
	public Neo4jRelationship(Relationship aRelationship) {
		mRelationship = aRelationship;
	}
	
	public Relationship getRelationship() {
		return mRelationship;
	}

	public HashMap<String, Object> getProperties() {
	    HashMap<String, Object> vProperties;
		vProperties = new HashMap<String, Object>();
		for (String vProperty : mRelationship.getPropertyKeys()) {
			vProperties.put(vProperty, mRelationship.getProperty(vProperty));
		}
		return vProperties;
	}

	public String toString() {
		return mRelationship.toString();
	}
	
	protected void finalize() throws Throwable {
	    try {
	    } finally {
	        super.finalize();
	    }
	}
}