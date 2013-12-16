package org.netkernel.neo4j.embedded.representation;

import java.util.HashMap;
import org.neo4j.graphdb.Node;

public class Neo4jNode {
	private final Node mNode;
	
	public Neo4jNode(Node aNode) {
		mNode = aNode;
	}
	
	public Node getNode() {
		return mNode;
	}

	public HashMap<String, Object> getProperties() {
	    HashMap<String, Object> vProperties;
		vProperties = new HashMap<String, Object>();
		for (String vProperty : mNode.getPropertyKeys()) {
			vProperties.put(vProperty, mNode.getProperty(vProperty));
		}
		return vProperties;
	}

	public String toString() {
		return mNode.toString();
	}
	
	protected void finalize() throws Throwable {
	    try {
	    } finally {
	        super.finalize();
	    }
	}
}