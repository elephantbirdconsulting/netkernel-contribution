/**
 * PoiNK Project : Apache POI for NetKernel.
 * Copyright 2008 by Tohono Consulting LLC. All rights reserved.
 * Copyright 2013 by Elephant Bird Consulting BVBA. All rights reserved.
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
 * Transreptor HSSFRow to XML (string).
 * 
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25. 
 */

// The usual suspects for a transreptor.
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardTransreptorImpl;

// Processing
import java.util.Iterator;

import com.elbeesee.poink.representation.IHSSFRowRepresentation;
import com.elbeesee.poink.representation.HSSFCellImplementation;
import com.elbeesee.poink.representation.IHSSFCellRepresentation;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Cell;


public class HSSFRowToXML extends StandardTransreptorImpl {
	public HSSFRowToXML() {
	    this.declareThreadSafe();
	    this.declareDescription("create valid xml from a row");
	    this.declareFromRepresentation(IHSSFRowRepresentation.class);
	    this.declareToRepresentation(String.class);	    
	}
	
	public void onTransrept (INKFRequestContext aContext) throws Exception {
		IHSSFRowRepresentation aIHSSFRowRepresentation = (IHSSFRowRepresentation)aContext.sourcePrimary(IHSSFRowRepresentation.class);
		HSSFRow vRow = aIHSSFRowRepresentation.getRowReadOnly();
		String vSheetName = vRow.getSheet().getSheetName();

		StringBuilder vRowXML = new StringBuilder();

		vRowXML.append("<row rowIndex=\"");
		vRowXML.append(vRow.getRowNum());
		vRowXML.append("\" sheetIndex=\"");
		vRowXML.append(vRow.getSheet().getWorkbook().getSheetIndex(vSheetName));
		vRowXML.append("\">");
		
		// do the cells
		int i = 0;
		for (Iterator<Cell> vCellIterator = vRow.cellIterator(); vCellIterator.hasNext();) {
			HSSFCell vHSSFCell = (HSSFCell)vCellIterator.next();
			IHSSFCellRepresentation vHSSFCellRepresentation = new HSSFCellImplementation(vHSSFCell);
			String vCellXML = aContext.transrept(vHSSFCellRepresentation, String.class);
			vRowXML.append(vCellXML);
			i = i + 1;
		}
		//
		
		vRowXML.append("</row>");
		
		INKFResponse vResponse = aContext.createResponseFrom(vRowXML.toString());
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
