package org.netkernel.neo4j.embedded.endpoint;

// Author: Tom Geudens
// Date  : 2012/09/25

// The usual suspects for an accessor
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

// Processing
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.GraphDatabaseService;
import org.netkernel.neo4j.embedded.representation.Neo4jInstance;
import org.netkernel.layer0.representation.IHDSNode;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Neo4jInstanceAccessor extends StandardAccessorImpl {
	private static ConcurrentHashMap<String, GraphDatabaseService> mInstances;
	private static URL mBaseLocationURL = null;
	
	private String getDBPath(INKFRequestContext aContext) throws Exception {
		String vDBPath = null;
		try {
			IHDSNode vNeo4jConfig = aContext.source("res:/etc/neo4jConfig.xml",IHDSNode.class);
			String vPath = (String)vNeo4jConfig.getFirstValue("/config/path");
			
			if (! vPath.endsWith("/") ) {
				throw new NKFException("DBPath name is invalid");
			}
			
			if (vPath.startsWith("/")) {
				vDBPath = vPath;
			}
			else {
				vDBPath = mBaseLocationURL.getFile() + vPath;
			}
		}
		catch (Exception e){
			vDBPath = mBaseLocationURL.getFile() + "tmp/";
		}
		return vDBPath;
	}

    class InstanceExpiry implements INKFExpiryFunction {
    	private Neo4jInstance mInstanceRepresentation = null;
    	public InstanceExpiry(Neo4jInstance aInstanceRepresentation) {
    		mInstanceRepresentation = aInstanceRepresentation;
    	}
    	
    	public boolean isExpired(long aNow) {
    		try {
    			mInstanceRepresentation.getInstance().getReferenceNode();
    			return true;
    		}
    		catch (Exception e) {
    			return false;
    		}
    	}
    }
    
	public Neo4jInstanceAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(Neo4jInstance.class);		
		mInstances = new ConcurrentHashMap<String, GraphDatabaseService>();
	}
	
	public void postCommission(INKFRequestContext aContext) throws Exception {
		String vBaseURL = aContext.source("netkernel:/config/netkernel.install.path", String.class);
		String vLocationString = vBaseURL + "neo4j/";
		mBaseLocationURL = new URL(vLocationString);
		File vLocationDirectory = new File(mBaseLocationURL.getFile());
		
		if (!(vLocationDirectory.exists() && vLocationDirectory.isDirectory())) {
			if (!vLocationDirectory.mkdir()) {
				throw new NKFException("Can not create [install]/neo4j directory.");
			}
		}
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		String vDBPath = getDBPath(aContext);
		
		if (! mInstances.containsKey(vDBPath)) {
			GraphDatabaseService vInstance = new GraphDatabaseFactory().newEmbeddedDatabase(vDBPath);
			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_START", vDBPath);
			mInstances.put(vDBPath, vInstance);
		}
		else {
			try {
				GraphDatabaseService vInstance = mInstances.get(vDBPath);
				vInstance.getReferenceNode();
			}
			catch (Exception e) {
				aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_WASSHUTRESTART", vDBPath);
				GraphDatabaseService vInstance = new GraphDatabaseFactory().newEmbeddedDatabase(vDBPath);
				aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_START", vDBPath);
				mInstances.put(vDBPath, vInstance);
			}
		}
		Neo4jInstance vRepresentation = new Neo4jInstance(mInstances.get(vDBPath));
		INKFResponse vResponse = aContext.createResponseFrom(vRepresentation);
		vResponse.setExpiry(INKFResponse.EXPIRY_FUNCTION, new InstanceExpiry(vRepresentation));
	}

	public void onDelete(INKFRequestContext aContext) throws Exception {
		String vDBPath = getDBPath(aContext);

		if (mInstances.containsKey(vDBPath)) {
			GraphDatabaseService vInstance = mInstances.get(vDBPath);
			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_SHUT", vDBPath);
			vInstance.shutdown();
			mInstances.remove(vDBPath);

			INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
			subrequest.addArgument("id", "gt:neo4j:nodes");
			subrequest.addArgument("id", "gt:neo4j:transactions");
			aContext.issueRequest(subrequest);
			org.netkernel.neo4j.embedded.endpoint.Neo4jNodeAccessor.mNodes.clear();
			org.netkernel.neo4j.embedded.endpoint.Neo4jTransactionAccessor.mTransactions.clear();

			aContext.createResponseFrom(true);
		}
		else {
			aContext.createResponseFrom(false);
		}
	}

	public void preDecommission(INKFRequestContext aContext) throws Exception {
		for(Map.Entry<String, GraphDatabaseService> e: mInstances.entrySet()) {
			GraphDatabaseService vInstance = e.getValue();
			try {
				vInstance.getReferenceNode();
				vInstance.shutdown();
				aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_SHUT", e.getKey());
			}
			catch (Exception shutException) {
				aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_WASSHUT", e.getKey());
			}
		}
		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:nodes");
		subrequest.addArgument("id", "gt:neo4j:transactions");
		aContext.issueRequest(subrequest);
		org.netkernel.neo4j.embedded.endpoint.Neo4jNodeAccessor.mNodes.clear();
		org.netkernel.neo4j.embedded.endpoint.Neo4jTransactionAccessor.mTransactions.clear();
		
		mInstances = null;
	}
}
