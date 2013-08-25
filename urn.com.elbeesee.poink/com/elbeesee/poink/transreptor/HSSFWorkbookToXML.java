/**
 * PoiNK Project : Apache POI for NetKernel.
 * Copyright 2008 by Tohono Consulting LLC. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.elbeesee.poink.transreptor;

/**
 *
 * Transreptor HSSFWorkbook to XML (string).
 * 
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 * 
 */

// The usual suspects for a transreptor.
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardTransreptorImpl;

// Processing
import com.elbeesee.poink.representation.IHSSFWorkbookRepresentation;
import com.elbeesee.poink.representation.IHSSFSheetRepresentation;
import com.elbeesee.poink.representation.HSSFSheetImplementation;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class HSSFWorkbookToXML extends StandardTransreptorImpl {
	public HSSFWorkbookToXML() {
	    this.declareThreadSafe();
	    this.declareDescription("create valid xml from a workbook");
	    this.declareFromRepresentation(IHSSFWorkbookRepresentation.class);
	    this.declareToRepresentation(String.class);
	}
	
	public void onTransrept (INKFRequestContext aContext) throws Exception {
		IHSSFWorkbookRepresentation aIHSSFWorkbookRepresentation = (IHSSFWorkbookRepresentation)aContext.sourcePrimary(IHSSFWorkbookRepresentation.class);
		HSSFWorkbook vWorkbook = aIHSSFWorkbookRepresentation.getWorkbookReadOnly();

		StringBuilder vWorkbookXML = new StringBuilder();

		int vNumberOfSheets = vWorkbook.getNumberOfSheets();
		vWorkbookXML.append("<workbook numSheets=\"");
		vWorkbookXML.append(vNumberOfSheets);
		vWorkbookXML.append("\">");
		
		// do the sheets
		for (int i = 0; i < vNumberOfSheets; i++) {
			IHSSFSheetRepresentation vHSSFSheetRepresentation = new HSSFSheetImplementation(vWorkbook.getSheetAt(i));
			String vSheetXML = aContext.transrept(vHSSFSheetRepresentation, String.class);
			vWorkbookXML.append(vSheetXML);
		}
		//
		
		vWorkbookXML.append("</workbook>");
		
		INKFResponse vResponse = aContext.createResponseFrom(vWorkbookXML.toString());
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
