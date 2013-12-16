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

if (vError == null) {
	context.createResponseFrom(vRetrieveResult.toString());
}
else {
	context.createResponseFrom(vError);
}