package org.tomgeudens.helloworld;

/**
 *
 * Hello World.
 * 
 * @author Tom Geudens. 2014/10/11.
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
