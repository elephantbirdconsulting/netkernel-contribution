package org.netkernel.neo4j.embedded.representation;

import org.neo4j.graphdb.Transaction;

public class Neo4jTransaction {
	private final Transaction mTransaction;
	private final String mName;
	
	public Neo4jTransaction(String aName, Transaction aTransaction) {
		mTransaction = aTransaction;
		mName = aName;
	}
	
	public Transaction getTransaction() {
		return mTransaction;
	}
	
	public String getTransactionName() {
		return mName;
	}

	public String toString() {
		return mTransaction.toString();
	}
	
	protected void finalize() throws Throwable {
	    try {
	    } finally {
	        super.finalize();
	    }
	}
}