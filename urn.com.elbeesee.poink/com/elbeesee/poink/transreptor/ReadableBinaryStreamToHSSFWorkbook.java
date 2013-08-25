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
 * Transreptor ReadableBinaryStream to HSSFWorkbook.
 * 
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 * 
 */

// The usual suspects for a transreptor.
import org.netkernel.layer0.nkf.*;
import org.netkernel.module.standard.endpoint.StandardTransreptorImpl;

// Processing
import java.io.InputStream;

import org.netkernel.layer0.representation.IReadableBinaryStreamRepresentation;

import com.elbeesee.poink.representation.IHSSFWorkbookRepresentation;
import com.elbeesee.poink.representation.HSSFWorkbookImplementation;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;


public class ReadableBinaryStreamToHSSFWorkbook extends StandardTransreptorImpl {
	public ReadableBinaryStreamToHSSFWorkbook() {
	    this.declareThreadSafe();
	    this.declareDescription("create apache poi workbook from binary stream");
	    this.declareFromRepresentation(IReadableBinaryStreamRepresentation.class);
	    this.declareToRepresentation(IHSSFWorkbookRepresentation.class);
	}
	

	public void onTransrept (INKFRequestContext aContext) throws Exception {
		IReadableBinaryStreamRepresentation aRBS = (IReadableBinaryStreamRepresentation)aContext.sourcePrimary(IReadableBinaryStreamRepresentation.class);
		InputStream vInputStream = aRBS.getInputStream();
		HSSFWorkbook vWorkbook = new HSSFWorkbook(vInputStream);
		IHSSFWorkbookRepresentation vRepresentation = new HSSFWorkbookImplementation(vWorkbook);
		
		INKFResponse vResponse = aContext.createResponseFrom(vRepresentation);
		vResponse.setExpiry(INKFResponse.EXPIRY_DEPENDENT);
	}
}
