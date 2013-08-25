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
 * Transreptor HSSFCell to XML (string).
 * 
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 *  
 */

// The usual suspects for a transreptor.
import org.netkernel.layer0.nkf.*;
import org.netkernel.layer0.util.XMLUtils;
import org.netkernel.module.standard.endpoint.StandardTransreptorImpl;

// Processing
import com.elbeesee.poink.representation.IHSSFCellRepresentation;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
// import org.apache.poi.ss.usermodel.FormulaEvaluator;


public class HSSFCellToXML extends StandardTransreptorImpl {
	public HSSFCellToXML() {
	    this.declareThreadSafe();
	    this.declareDescription("create valid xml from a cell");
	    this.declareFromRepresentation(IHSSFCellRepresentation.class);
	    this.declareToRepresentation(String.class);
	}
	
	public void onTransrept (INKFRequestContext aContext) throws Exception {
		IHSSFCellRepresentation aIHSSFCellRepresentation = (IHSSFCellRepresentation)aContext.sourcePrimary(IHSSFCellRepresentation.class);
		HSSFCell vCell = aIHSSFCellRepresentation.getCellReadOnly();
		String vSheetName = vCell.getSheet().getSheetName();

		StringBuilder vCellXML = new StringBuilder();

		vCellXML.append("<cell columnIndex=\"");
		vCellXML.append(vCell.getColumnIndex());
		vCellXML.append("\" rowIndex=\"");
		vCellXML.append(vCell.getRow().getRowNum());
		vCellXML.append("\" sheetIndex=\"");
		vCellXML.append(vCell.getSheet().getWorkbook().getSheetIndex(vSheetName));
		vCellXML.append("\">");
		
		int vCellType = vCell.getCellType();
		if (vCellType == Cell.CELL_TYPE_FORMULA) {
			vCellType = vCell.getCachedFormulaResultType();
		}
		
		if (vCellType == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(vCell)) {
				vCellXML.append(vCell.getDateCellValue());
			}
			else {
				vCellXML.append(vCell.getNumericCellValue());
			}
		}
		else if (vCellType == Cell.CELL_TYPE_STRING) {
			vCellXML.append(XMLUtils.escape(vCell.getStringCellValue()));
		}
		else if (vCellType == Cell.CELL_TYPE_BOOLEAN) {
			vCellXML.append(vCell.getBooleanCellValue());
		}
		else if (vCellType == Cell.CELL_TYPE_BLANK) {
		}
		else if (vCellType == Cell.CELL_TYPE_ERROR) {
			vCellXML.append(vCell.getErrorCellValue());
		}
		//
		
		vCellXML.append("</cell>");
		
		INKFResponse vResponse = aContext.createResponseFrom(vCellXML.toString());
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
