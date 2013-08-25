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
 * Transreptor HSSFSheet to XML (string).
 * 
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 * 
 */

// The usual suspects for a transreptor.
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardTransreptorImpl;

// Processing
import java.util.Iterator;

import com.elbeesee.poink.representation.IHSSFSheetRepresentation;
import com.elbeesee.poink.representation.HSSFRowImplementation;
import com.elbeesee.poink.representation.IHSSFRowRepresentation;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Row;

import org.netkernel.layer0.util.XMLUtils;


public class HSSFSheetToXML extends StandardTransreptorImpl {
	public HSSFSheetToXML() {
	    this.declareThreadSafe();
	    this.declareDescription("create valid xml from a sheet");
	    this.declareFromRepresentation(IHSSFSheetRepresentation.class);
	    this.declareToRepresentation(String.class);
	}
	
	public void onTransrept (INKFRequestContext aContext) throws Exception {
		IHSSFSheetRepresentation aIHSSFSheetRepresentation = (IHSSFSheetRepresentation)aContext.sourcePrimary(IHSSFSheetRepresentation.class);
		HSSFSheet vSheet = aIHSSFSheetRepresentation.getSheetReadOnly();
		String vSheetName = vSheet.getSheetName();

		StringBuilder vSheetXML = new StringBuilder();

		vSheetXML.append("<sheet sheetName=\"");
		vSheetXML.append(XMLUtils.escape(vSheetName));
		vSheetXML.append("\" sheetIndex=\"");
		vSheetXML.append(vSheet.getWorkbook().getSheetIndex(vSheetName));
		vSheetXML.append("\" numRows=\"");
		vSheetXML.append(vSheet.getPhysicalNumberOfRows());
		vSheetXML.append("\">");
		
		// do the rows
		int i = 0;
		for (Iterator<Row> vRowIterator = vSheet.rowIterator(); vRowIterator.hasNext();) {
			HSSFRow vHSSFRow = (HSSFRow)vRowIterator.next();
			IHSSFRowRepresentation vHSSFRowRepresentation = new HSSFRowImplementation(vHSSFRow);
			String vRowXML = aContext.transrept(vHSSFRowRepresentation, String.class);
			vSheetXML.append(vRowXML);
			i = i + 1;
		}
		//
		
		vSheetXML.append("</sheet>");
		
		INKFResponse vResponse = aContext.createResponseFrom(vSheetXML.toString());
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
