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

package com.elbeesee.poink.representation;

/**
 *
 * Representation Implementation for HSSFCell.
 *  
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 *
 */

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class HSSFCellImplementation implements IHSSFCellRepresentation {
	
	private HSSFCell mCell;
	
	public HSSFCellImplementation (HSSFCell aCell) {
		this.mCell = aCell;
	}

	public HSSFCellImplementation (HSSFRow aRow, int aCellIndex) {
		this.mCell = aRow.getCell(aCellIndex);
	}

	public HSSFCellImplementation (HSSFSheet aSheet, int aRowIndex, int aCellIndex) {
		this.mCell = aSheet.getRow(aRowIndex).getCell(aCellIndex);
	}

	public HSSFCellImplementation (HSSFWorkbook aWorkbook, int aSheetIndex, int aRowIndex, int aCellIndex) {
		this.mCell = aWorkbook.getSheetAt(aSheetIndex).getRow(aRowIndex).getCell(aCellIndex);
	}

	public HSSFCell getCellReadOnly () {
		return mCell;
	}
}
