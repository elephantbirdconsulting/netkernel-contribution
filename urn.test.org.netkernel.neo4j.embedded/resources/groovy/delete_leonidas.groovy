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

Neo4jNode[] vRetrieveResult = null;
String vError = null;
HashMap<String, Object> vProperties = new HashMap<String, Object>();
vProperties.put("name", "leonidas");
try {
  subrequest = context.createRequest("active:neo4jretrieve");
  subrequest.setVerb(INKFRequestReadOnly.VERB_SOURCE);
  subrequest.addArgument("entity","spartan");
  subrequest.addArgumentByValue("instance",vDBrep);
  subrequest.addArgumentByValue("properties",vProperties);
  subrequest.setRepresentationClass(Neo4jNode[].class);

  vRetrieveResult = context.issueRequest(subrequest);
}
catch (NKFException e) {
	vError = "ERROR " +  e.getMessage();
}

Boolean vDeleteResult = false;
try {
	for (i = 0; i < vRetrieveResult.length; i++   )  {
		Neo4jNode vNodeRepresentation = vRetrieveResult.getAt(i);
		subrequest = context.createRequest("active:neo4jnode");
		subrequest.setVerb(INKFRequestReadOnly.VERB_DELETE);
		subrequest.addArgumentByValue("id", vNodeRepresentation.getNode().getId());
		subrequest.addArgumentByValue("instance",vDBrep);
		vDeleteResult = context.issueRequest(subrequest);
	}
}
catch (NKFException e) {
	vError = "ERROR " +  e.getMessage();
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
	context.createResponseFrom(vDeleteResult);
}
else {
	context.createResponseFrom(vError);
}