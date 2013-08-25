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

package com.elbeesee.poink.util;

/**
 *
 * PoiNKConstants Interface
 *  
 * @author Tom Hicks. 09/06/2008.
 * @author Tom Geudens. 2013/08/25.
 *  
 */

public interface PoiNKConstants {
	
	public static final short MIN_COLUMN_INDEX = (short) 0;
	
	public static final String MIN_COLUMN_LABEL = "A";

	public static final short MIN_ROW_INDEX = (short) 0;

	public static final String MIN_ROW_LABEL = "1";

	public static final short MIN_SHEET_INDEX = (short) 0;

	public static final String MIN_SHEET_LABEL = "1";

	public static final String WORKBOOK_ADDRESS_PREFIX = "wbAddr";
	
	public static final String WORKBOOK_ADDRESS_SHEET_SEPARATOR = "!";
	
	public static final String VALID_CHARACTERS_COLUMN_LABEL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final String VALID_CHARACTERS_ROW_LABEL = "0123456789";
}
