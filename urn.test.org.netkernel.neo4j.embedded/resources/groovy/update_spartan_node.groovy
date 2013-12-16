import java.util.HashMap;

import org.netkernel.layer0.nkf.INKFRequest;
import org.netkernel.layer0.nkf.NKFException;
import org.netkernel.layer0.nkf.INKFResponse;
import org.netkernel.layer0.nkf.INKFRequestReadOnly;

import org.netkernel.neo4j.embedded.representation.Neo4jInstance;
import org.netkernel.neo4j.embedded.representation.Neo4jNode;
import org.netkernel.neo4j.embedded.representation.Neo4jTransaction;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

INKFRequest subrequest = context.createRequest("active:neo4jinstance");
subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
subrequest.setRepresentationClass(Neo4jInstance.class);

Neo4jInstance vDBrep = context.issueRequest(subrequest);
GraphDatabaseService vDB = vDBrep.getInstance();

subrequest = context.createRequest("active:neo4jtransaction");
subrequest.setVerb(INKFRequestReadOnly.VERB_NEW);
subrequest.addArgumentByValue("instance",vDBrep);
subrequest.setRepresentationClass(Neo4jTransaction.class);

Neo4jTransaction vTXrep = context.issueRequest(subrequest);
String vTXname = vTXrep.getTransactionName();
Transaction vTX = vTXrep.getTransaction();

Neo4jNode vNodeRepresentation = null;
String vError = null;
HashMap<String, Object> vProperties = new HashMap<String, Object>();

try {
  subrequest = context.createRequest("active:neo4jnode");
  subrequest.setVerb(INKFRequestReadOnly.VERB_NEW);
  subrequest.addArgument("graph","sparta");
  subrequest.addArgument("entity","spartan");
  subrequest.addArgumentByValue("instance",vDBrep);
  subrequest.setRepresentationClass(Neo4jNode.class);

  vNodeRepresentation = context.issueRequest(subrequest);
}
catch (NKFException e) {
	vError = "ERROR " + e.getMessage();
}

if (vNodeRepresentation != null) {
	vProperties.put("name", "leonidas");
	vProperties.put("rank", "king");
	try {
		subrequest = context.createRequest("active:neo4jnode");
		subrequest.setVerb(INKFRequestReadOnly.VERB_SINK);
		subrequest.addArgumentByValue("node", vNodeRepresentation);
		subrequest.addArgumentByValue("properties", vProperties);
	  
		context.issueRequest(subrequest);
	}
	catch (NKFException e) {
		vError = "ERROR " + e.getMessage();
	}  
}

subrequest = context.createRequest("active:neo4jtransaction");
subrequest.setVerb(INKFRequestReadOnly.VERB_SINK);
subrequest.addArgument("name",vTXname);
subrequest.addArgument("status", "success");
context.issueRequest(subrequest);

subrequest = context.createRequest("active:neo4jtransaction");
subrequest.setVerb(INKFRequestReadOnly.VERB_DELETE);
subrequest.addArgument("name",vTXname);
context.issueRequest(subrequest);

if (vError == null) {
	context.createResponseFrom(vProperties);
}
else {
	context.createResponseFrom(vError);
}