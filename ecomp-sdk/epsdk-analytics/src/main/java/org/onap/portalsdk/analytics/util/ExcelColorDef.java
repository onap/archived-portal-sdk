/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.analytics.util;


import java.util.HashMap;

import org.apache.poi.hssf.util.HSSFColor;

public class ExcelColorDef {

	public static HashMap colors = new HashMap();
	
	public static void initializeExcelColorDef() {
		colors.put("#00FFFF",new Short(HSSFColor.AQUA.index));
		colors.put("#000000",new Short(HSSFColor.BLACK.index));
		colors.put("#0000FF",new Short(HSSFColor.BLUE.index));
		colors.put("#FF00FF",new Short(HSSFColor.PINK.index));
		colors.put("#808080",new Short(HSSFColor.GREY_40_PERCENT.index));
		colors.put("#008000",new Short(HSSFColor.BRIGHT_GREEN.index));
		colors.put("#00FF00",new Short(HSSFColor.LIME.index));
		colors.put("#800000",new Short(HSSFColor.MAROON.index));
		colors.put("#000080",new Short(HSSFColor.ROYAL_BLUE.index));
		colors.put("#808000",new Short(HSSFColor.OLIVE_GREEN.index));
		colors.put("#FF9900",new Short(HSSFColor.ORANGE.index));
		colors.put("#800080",new Short(HSSFColor.VIOLET.index)); 
		colors.put("#FF0000",new Short(HSSFColor.RED.index));
		colors.put("#C0C0C0",new Short(HSSFColor.CORAL.index));
		colors.put("#008080",new Short(HSSFColor.TEAL.index));
		colors.put("#FFFFFF",new Short(HSSFColor.WHITE.index));
		colors.put("#FFFF00",new Short(HSSFColor.YELLOW.index));

	}
	
	public static short getExcelColor( String color) {
		if ((colors != null) && (colors.containsKey(color))) {
			return ((Short) colors.get(color)).shortValue();
		}
          
		return new Short(HSSFColor.WHITE.index).shortValue();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
