package org.javamag.helloworld;


/**
*
* Introduction Project.
* 
* @author Tom Geudens. 2013/09/25.
* 
*/

/**
 * Accessor Imports.
 */
import org.netkernel.layer0.nkf.*;
//import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;


/**
 * 
 * Hello World Accessor.
 * 
 */

public class HelloWorldAccessor extends StandardAccessorImpl {
	public HelloWorldAccessor() {
		this.declareThreadSafe();
		this.declareSourceRepresentation(String.class);
	}
	
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		aContext.createResponseFrom("Hello World");
	}
}
