/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics.model.base;

import java.util.Comparator;

import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;

public class ChartSeqComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		DataColumnType dct1 = (DataColumnType) o1;
		DataColumnType dct2 = (DataColumnType) o2;
		
		int dct1ChartSeq = (dct1.getChartSeq()!=null ? dct1.getChartSeq().intValue(): -1);
		int dct2ChartSeq = (dct2.getChartSeq()!=null ? dct2.getChartSeq().intValue(): -1);
		
		if (dct1ChartSeq == dct2ChartSeq)
			return 0;
		else if (dct1ChartSeq < 0) // Position columns
															// with seq -1 at
															// the end
			return 1;
		else if (dct2ChartSeq < 0)
			return -1;
		else if (dct1ChartSeq < dct2ChartSeq)
			return -1;
		else
			return 1;
	} // compare

} // ChartSeqComparator
