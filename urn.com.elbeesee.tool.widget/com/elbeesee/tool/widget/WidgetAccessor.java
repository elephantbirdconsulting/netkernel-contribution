package com.elbeesee.tool.widget;


/**
 * 
 *  Tools Project.  
 *  @author Tom Geudens. 2014/05/27.
 *     
 */

/**
 * Accessor Imports.
 */
import java.util.Calendar;

import org.netkernel.layer0.nkf.*;
//import org.netkernel.layer0.meta.impl.SourcedArgumentMetaImpl;
import org.netkernel.module.standard.endpoint.StandardAccessorImpl;

/**
 * Widget Accessor.
 * Provides a couple of common widgets.
 * 
 */

public class WidgetAccessor extends StandardAccessorImpl {
	public WidgetAccessor() {
		this.declareThreadSafe();
	}
	
	public void onSource(INKFRequestContext aContext) throws Exception {
		String vActiveType = aContext.getThisRequest().getArgumentValue("activeType");
		
		Long vExpiry = null;
		try {
			vExpiry = aContext.source("widget:expiry" + vActiveType.replaceFirst("widget",""), Long.class);
		}
		catch (Exception e) {
			//
		}
		finally {
			if (vExpiry == null) {
				vExpiry = 0L;
			}
		}
		
		INKFResponse vResponse = null;
		if (vActiveType.equals("widgetCurrentYear")) {
			Calendar vCalendar = Calendar.getInstance();
			long vNow = System.currentTimeMillis();
			vCalendar.setTimeInMillis(vNow);
			vResponse = aContext.createResponseFrom(Integer.toString(vCalendar.get(Calendar.YEAR)));
		}
		else {
			vResponse = aContext.createResponseFrom("TODO " + vActiveType);
		}
		vResponse.setExpiry(INKFResponse.EXPIRY_MIN_CONSTANT_DEPENDENT, System.currentTimeMillis() + vExpiry);

	}
}
