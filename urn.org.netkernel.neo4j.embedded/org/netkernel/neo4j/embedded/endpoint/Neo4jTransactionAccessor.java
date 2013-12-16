package org.netkernel.neo4j.embedded.endpoint;

// Author: Tom Geudens
// Date  : 2012/09/26

// The usual suspects for an accessor
import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

// Processing
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.netkernel.neo4j.embedded.representation.Neo4jTransaction;
import org.netkernel.neo4j.embedded.representation.Neo4jInstance;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Neo4jTransactionAccessor extends StandardAccessorImpl {
	protected static ConcurrentHashMap<String, Transaction> mTransactions;

	public Neo4jTransactionAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(Neo4jTransaction.class);
		this.declareArgument(new SourcedArgumentMetaImpl("name",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("status",null,null,new Class[] {String.class}));
		this.declareArgument(new SourcedArgumentMetaImpl("instance",null,null,new Class[] {Neo4jInstance.class}));
		this.declareInhibitCheckForBadExpirationOnMutableResource(); // golden thread takes care of it
		mTransactions = new ConcurrentHashMap<String, Transaction>();
	}

	public void onNew(INKFRequestContext aContext) throws Exception {
		// NEW requires one argument, instance

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

		// Create Transaction
		Transaction vTransaction = null;
		String vName = null;
		
		vTransaction = vInstance.beginTx();
		vName = UUID.randomUUID().toString();

		mTransactions.put(vName, vTransaction);
		
		aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_TRANSACTION_CREATE", vName);

		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:transactions");
		aContext.issueRequest(subrequest);
		
		subrequest = aContext.createRequest("active:neo4jtransaction");
		subrequest.addArgumentByValue("name", vName);
		subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
		subrequest.setRepresentationClass(Neo4jTransaction.class);

		aContext.createResponseFrom(aContext.issueRequestForResponse(subrequest));
	}

	public void onSource(INKFRequestContext aContext) throws Exception {
		// SOURCE requires one argument, name

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

		Transaction vTransaction = null;
		String vName = null;

		if (mTransactions.containsKey(aName)) {
			vName = aName;
			vTransaction = mTransactions.get(aName);

			INKFRequest subrequest = aContext.createRequest("active:attachGoldenThread");
			subrequest.addArgument("id", "gt:neo4j:transactions");
			aContext.issueRequest(subrequest);

			Neo4jTransaction vTransactionRepresentation = new Neo4jTransaction(vName,vTransaction);
			aContext.createResponseFrom(vTransactionRepresentation);
		}
		else {
			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_TRANSACTION_NAME_NOT_VALID", vName);
			INKFResponse vResponse = aContext.createResponseFrom(null);
			vResponse.setExpiry(INKFResponse.EXPIRY_ALWAYS);
		}
	}

	public void onSink(INKFRequestContext aContext) throws Exception {
		// SINK requires two arguments, name and status
		
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

		// Verify status
		String aStatus = null;
		if (aContext.getThisRequest().argumentExists("status")) {
			aStatus = aContext.getThisRequest().getArgumentValue("status");
			if (aStatus.equals("pbv:status")) {
				aStatus = aContext.source("arg:status",String.class);
			}
		}
		else {
			throw new NKFException("request does not have the required - status - argument");
		}
		if (! ( aStatus.equals("success") || aStatus.equals("failure") ) ) {
			throw new NKFException("request does not have a valid - status - argument, must be \"success\" or \"failure\"");
		}

		// Action
		if (aStatus.equals("failure")) {
			mTransactions.get(aName).failure();
		}
		else {
			mTransactions.get(aName).success();
		}
	
		INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
		subrequest.addArgument("id", "gt:neo4j:transactions");
		aContext.issueRequest(subrequest);

		aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_TRANSACTION_UPDATE", aName, aStatus);
	}

	public void onDelete(INKFRequestContext aContext) throws Exception {
		// DELETE requires one argument, name

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

		if ( mTransactions.containsKey(aName) ) {
			mTransactions.get(aName).finish();

			INKFRequest subrequest = aContext.createRequest("active:cutGoldenThread");
			subrequest.addArgument("id", "gt:neo4j:transactions");
			aContext.issueRequest(subrequest);

			aContext.logFormatted(INKFLocale.LEVEL_INFO,"MSG_NEO4J_TRANSACTION_DELETE", aName);

			mTransactions.remove(aName);
			aContext.createResponseFrom(true);
		}
		else {
			aContext.createResponseFrom(false);
		}
	}
}
