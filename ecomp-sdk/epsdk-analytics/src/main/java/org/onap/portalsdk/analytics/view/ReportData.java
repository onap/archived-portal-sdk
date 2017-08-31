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
package org.onap.portalsdk.analytics.view;

import java.io.Serializable;
import java.util.*;

import org.onap.portalsdk.analytics.model.runtime.*;
import org.onap.portalsdk.analytics.system.*;
import org.onap.portalsdk.analytics.util.*;
import org.onap.portalsdk.analytics.xmlobj.*;

public class ReportData extends org.onap.portalsdk.analytics.RaptorObject implements Serializable {
	// private static final boolean sortData = true; // sort cross-tab data by
	// row/col name while being added
	private static final int MAX_NUM_COLS = 150; // -1 => no restriction

	private int pageNo = -1;

	private int pageSetNo = -1;

	public ReportColumnHeaderRows reportColumnHeaderRows = null;

	public ReportRowHeaderCols reportRowHeaderCols = null;

	public ReportDataRows reportDataRows = null;
	
	public ReportDataRows reportDataTotalRow = null;
	
	public ReportRowHeaderCols reportTotalRowHeaderCols = null;
	
	public List reportDataList = null;

	private boolean canSortAtRuntime = false;

	private Vector columnVisuals = null; // Contains actual column Ids for
											// linear or last header col values
											// for cross-tab

	public ReportData(int pageNo, boolean canSortAtRuntime) {
		super();

		this.pageNo = pageNo;
		this.canSortAtRuntime = canSortAtRuntime;

		reportColumnHeaderRows = new ReportColumnHeaderRows();
		reportRowHeaderCols = new ReportRowHeaderCols();
		reportDataRows = new ReportDataRows();

		columnVisuals = new Vector();
	} // ReportData

	public int getPageNo() {
		return pageNo;
	} // pageNo

	public int getPageSetNo() {
		return pageSetNo;
	} // pageSetNo
	
	public int getHeaderRowCount() {
		return reportColumnHeaderRows.getRowCount();
	} // getHeaderRowCount

	public int getDataRowCount() {
		if(reportDataRows!=null && reportDataRows.getRowCount()>0)
			return reportDataRows.getRowCount();
		else if(getReportDataList()!=null)
			return getReportDataList().size();
		else
			return 0;
	} // getDataRowCount

	public int getTotalRowCount() {
		return getHeaderRowCount() + getDataRowCount();
	} // getTotalRowCount

	public int getHeaderColumnCount() {
		return reportRowHeaderCols.getColumnCount();
	} // getHeaderColumnCount

	public int getDataColumnCount() {
		int cCount = reportDataRows.getColumnCount();
		if (cCount == 0)
			cCount = reportColumnHeaderRows.getColumnCount();

		return cCount;
	} // getDataColumnCount

	public int getTotalColumnCount() {
		return getHeaderColumnCount() + getDataColumnCount();
	} // getTotalColumnCount

	// Cross-tab data section
	private void insertColumn(int colIdx, Vector colNameValues, Vector colNameSortValues, List dataList, String alignment) {
		int r = 0;
		for (reportColumnHeaderRows.resetNext(); reportColumnHeaderRows.hasNext();) {
			ColumnHeaderRow chr = reportColumnHeaderRows.getNext();
			if(colNameSortValues!=null) {
				ColumnHeader ch = new ColumnHeader(((DataValue) colNameValues.get(r)).getDisplayValue(), Integer.valueOf(((DataValue)colNameSortValues.get(r)).getDisplayValue()) );
				ch.setColumnWidth(chr.getDisplayWidth());
				ch.setAlignment(alignment);
				chr.addColumnHeader(colIdx, ch);
			}
			else {
				ColumnHeader ch = new ColumnHeader(((DataValue) colNameValues.get(r)).getDisplayValue());
				ch.setColumnWidth(chr.getDisplayWidth());
				ch.setAlignment(alignment);
				chr.addColumnHeader(colIdx, ch);
			}
			r++;
		} // for

/*		for (reportDataRows.resetNext(); reportDataRows.hasNext();) {
			DataRow dr = reportDataRows.getNext();
			dr.addDataValue(colIdx, new DataValue());
		} // for
		*/
		for(int j = 0; j < dataList.size(); j++) {
			DataRow dr = (DataRow) dataList.get(j);
			dr.addDataValue(colIdx, new DataValue());
		}
	} // insertColumn

	private boolean prevColNamesChanged(int colIdx, Vector prevColNames) {
		for (int k = 0; k < prevColNames.size(); k++)
			if (!nvl((String) prevColNames.get(k)).equals(
					reportColumnHeaderRows.getColumnHeader(k, colIdx).getColumnTitle()))
				return true;

		return false;
	} // prevColNamesChanged

	private int findColumnIdx(Vector colNameValues, Vector colNameSortValues, CrossTabOrderManager crossTabOrderManager,
			boolean createIfMissing, List dataList) {
		int colIdx = 0;
		try {
		
		boolean nameFound = false;
		// String prevColName = null;
		Vector prevColNames = new Vector();
		int columnSort = -1;
		for (int r = 0; r < colNameValues.size(); r++) {
			//String colName = (String) colNameValues.get(r);
			String colName = ((DataValue) colNameValues.get(r)).getDisplayValue();
			Integer colSortName  = null;
			if(colNameSortValues!=null)
				colSortName =  Integer.valueOf(((DataValue)colNameSortValues.get(r)).getDisplayValue());
			ColumnHeaderRow chr = reportColumnHeaderRows.getColumnHeaderRow(r);

			chr.resetNext(colIdx);
			nameFound = false;
			boolean posFound = false;
			while ((!nameFound) && (!posFound) && chr.hasNext()) {
				ColumnHeader ch = chr.getNext();
				columnSort = ch.getColumnSort();
				if (colName.equals(ch.getColumnTitle())) {
					if (prevColNamesChanged(colIdx, prevColNames))
						posFound = true;
					else
						nameFound = true;
				} else if (crossTabOrderManager != null) {
						if( colNameSortValues!=null && crossTabOrderManager.isAfterColHeaderValue(r, columnSort,
								colSortName))
							posFound = true;
						else if (colNameSortValues == null && crossTabOrderManager.isAfterColHeaderValue(r, ch.getColumnTitle(),
								colName)) {
							posFound = true;
						}
				}
				else if (crossTabOrderManager == null) {
						if( colNameSortValues!=null && colSortName.compareTo(ch.getColumnSort()) < 0)
							posFound = true;
						else if (colNameSortValues==null && colName.compareTo(ch.getColumnTitle()) < 0)
						   posFound = true;
				}
				else // if(prevColName!=null&&(!
						// prevColName.equals(reportColumnHeaderRows.getColumnHeader(r-1,
						// colIdx).getColumnTitle())))
				if (prevColNamesChanged(colIdx, prevColNames))
					posFound = true;

				if ((!nameFound) && (!posFound))
					colIdx++;
			} // while

			if (!nameFound)
				if (createIfMissing && (MAX_NUM_COLS >= 0) && (colIdx <= MAX_NUM_COLS)) {
					insertColumn(colIdx, colNameValues, colNameSortValues, dataList, chr.getAlignment());
					return colIdx;
				} else
					return -1;

			prevColNames.add(r, colName);
		} // for
		if (!nameFound)
			if (createIfMissing && (MAX_NUM_COLS >= 0) && (colIdx <= MAX_NUM_COLS))
				insertColumn(colIdx, colNameValues, colNameSortValues, dataList, "center");
			else
				return -1;
		} catch (Exception ex) {ex.printStackTrace();}
		
		return colIdx;
	} // findColumnIdx

	private void insertRow(int rowIdx, Vector rowNameValues, List dataList, CrossTabOrderManager crossTabOrderManager) {
		int c = 0;
		DataRow dr = new DataRow();
		int dataRowIdx = 0;
		for (reportRowHeaderCols.resetNext(); reportRowHeaderCols.hasNext();) {
			if((c+1)==reportRowHeaderCols.size()) {
				dataRowIdx = crossTabOrderManager.getIndexOfRowHeaderValue(c, ((DataValue) rowNameValues.get(c)).getDisplayValue());
			}
			RowHeaderCol rhc = reportRowHeaderCols.getNext();
			rhc.addRowHeader(rowIdx, new RowHeader(((DataValue) rowNameValues.get(c)).getDisplayValue()));
			c++;
		} // for

		dr.setRowNum(dataRowIdx);
		for (c = 0; c < getDataColumnCount(); c++) {
			dr.addDataValue(new DataValue());
		}
          
		//reportDataRows.addDataRow(rowIdx, dr);
		//dr.setRowNum(dataList.size()==0?1:dataList.size());
		dataList.add(rowIdx, dr);

	} // insertRow

	private boolean prevRowNamesChanged(int rowIdx, Vector prevRowNames) {
		for (int k = 0; k < prevRowNames.size(); k++)
			if (!nvl((String) prevRowNames.get(k)).equals(
					reportRowHeaderCols.getRowHeader(k, rowIdx).getRowTitle()))
				return true;

		return false;
	} // prevRowNamesChanged

	private int findRowIdx(Vector rowNameValues, CrossTabOrderManager crossTabOrderManager,
			boolean createIfMissing, List dataList) {
		int rowIdx = 0;
		boolean nameFound = false;
		Vector prevRowNames = new Vector();
		for (int c = 0; c < rowNameValues.size(); c++) {
			//String rowName = (String) rowNameValues.get(c);
			String rowName = ((DataValue) rowNameValues.get(c)).getDisplayValue();
		
			RowHeaderCol rhc = reportRowHeaderCols.getRowHeaderCol(c);

			rhc.resetNext(rowIdx);
			nameFound = false;
			boolean posFound = false;
			while ((!nameFound) && (!posFound) && rhc.hasNext()) {
				RowHeader rh = rhc.getNext();
				if (rowName.equals(rh.getRowTitle())) {
					if (prevRowNamesChanged(rowIdx, prevRowNames))
						posFound = true;
					else
						nameFound = true;
				} else if (crossTabOrderManager != null
						&& crossTabOrderManager.isAfterRowHeaderValue(c, rh.getRowTitle(),
								rowName))
					posFound = true;
				else if (crossTabOrderManager == null
						&& rowName.compareTo(rh.getRowTitle()) < 0)
					posFound = true;
				else if (prevRowNamesChanged(rowIdx, prevRowNames))
					posFound = true;

				if ((!nameFound) && (!posFound))
					rowIdx++;
			} // while

			if (!nameFound)
				if (createIfMissing) {
					insertRow(rowIdx, rowNameValues, dataList, crossTabOrderManager);
					return rowIdx;
				} else
					return -1;

			prevRowNames.add(c, rowName);
		} // for
		if (!nameFound)
			if (createIfMissing)
				insertRow(rowIdx, rowNameValues, dataList, crossTabOrderManager);
			else
				return -1;

		return rowIdx;
	} // findRowIdx

	public void setDataValue(Vector rowNameValues, Vector colNameValues, Vector colNameSortValues, DataValue value,
			FormatProcessor formatProcessor, CrossTabOrderManager crossTabOrderManager, List dataList) {
		int rowIdx = findRowIdx(rowNameValues, crossTabOrderManager, true, dataList);
		int colIdx = findColumnIdx(colNameValues, colNameSortValues, crossTabOrderManager, true, dataList);

		if ((rowIdx >= 0) && (colIdx >= 0)) {
			//DataRow dr = reportDataRows.getDataRow(rowIdx);
			DataRow dr = (DataRow)dataList.get(rowIdx);
			dr.setRowValues(rowNameValues);
			dr.setDataValue(colIdx, value);
			formatProcessor.setHtmlFormatters(value, dr, false);
		} // if
	} // setDataValue

	public void consolidateColumnHeaders(VisualManager visualManager) {
		// Setting column visuals
		for (int i = 0; i < reportColumnHeaderRows.getColumnHeaderRow(
				reportColumnHeaderRows.getRowCount() - 1).size(); i++) {
			StringBuffer sb = new StringBuffer();

			for (int ir = 0; ir < reportColumnHeaderRows.getRowCount(); ir++) {
				ColumnHeader ch = reportColumnHeaderRows.getColumnHeaderRow(ir)
						.getColumnHeader(i);
				if (sb.length() > 0)
					sb.append('|');
				sb.append(Utils.replaceInString(ch.getColumnTitle(), "|", " "));
			} // for

			String colValue = sb.toString();
			columnVisuals.add(new ColumnVisual(colValue, colValue, visualManager
					.isColumnVisible(colValue), /* visualManager.getSortByColId().equals(colValue)?visualManager.getSortByAscDesc(): */
					null));
		} // for

		// Consolidating column headers
		for (int r = reportColumnHeaderRows.getRowCount() - 1; r >= 0; r--) {
			ColumnHeaderRow chr = reportColumnHeaderRows.getColumnHeaderRow(r);

			if (chr.size() > 0) {
				ColumnHeader baseCH = chr.getColumnHeader(0);
				int c = 1;
				int c_shift = 0;
				while (c < chr.size()) {
					ColumnHeader ch = chr.getColumnHeader(c);

					boolean performMerge = true;
					for (int ir = r; ir >= 0; ir--) {
						ColumnHeaderRow ichr = reportColumnHeaderRows.getColumnHeaderRow(ir);
						ColumnHeader ch0 = ichr.getColumnHeader(c + ((ir == r) ? 0 : c_shift));
						ColumnHeader ch1 = ichr.getColumnHeader(c + ((ir == r) ? 0 : c_shift)
								- 1);
						if (!ch0.getColumnTitle().equals(ch1.getColumnTitle()))
							performMerge = false;
					} // for

					if (performMerge) {
						c_shift++;
						baseCH.setColSpan(baseCH.getColSpan() + 1);
						chr.remove(c);
					} else {
						baseCH = ch;
						c++;
					}
				} // while
			} // if
		} // for
	} // consolidateColumnHeaders

	public void consolidateRowHeaders() {
		for (int c = reportRowHeaderCols.getColumnCount() - 1; c >= 0; c--) {
			RowHeaderCol rhc = reportRowHeaderCols.getRowHeaderCol(c);

			if (rhc.size() > 0) {
				RowHeader baseRH = rhc.getRowHeader(0);
				int r = 1;
				while (r < rhc.size()) {
					RowHeader rh = rhc.getRowHeader(r);

					boolean performMerge = rh.getRowTitle().equals(baseRH.getRowTitle());
					for (int ic = c - 1; ic >= 0; ic--) {
						RowHeaderCol irhc = reportRowHeaderCols.getRowHeaderCol(ic);
						RowHeader rh0 = irhc.getRowHeader(r);
						RowHeader rh1 = irhc.getRowHeader(r - 1);
						if (!rh0.getRowTitle().equals(rh1.getRowTitle()))
							performMerge = false;
					} // for

					if (performMerge)
						rh.setRowTitle(null);
					else
						baseRH = rh;

					r++;
				} // while
			} // if
		} // for
	} // consolidateRowHeaders

	public void addRowNumbers(int pageNo, List dataList) {
		pageNo = 0;
		//int startRowNum = ((pageNo < 0) ? 0 : pageNo) * pageSize + 1;
		int startRowNum = 0;
		RowHeaderCol rhc = new RowHeaderCol();
		reportRowHeaderCols.addRowHeaderCol(0, rhc);
		//rhc.setColumnWidth("5%");
		for (int r = 0; r < dataList.size(); r++) {
			rhc.add(new RowHeader(startRowNum + r+""));
			((DataRow)dataList.get(r)).setRowNum(r);
		}
	} // addRowNumbers

	private void removeRow(int rowIdx) {
		for (reportRowHeaderCols.resetNext(); reportRowHeaderCols.hasNext();) {
			RowHeaderCol rhc = reportRowHeaderCols.getNext();
			rhc.remove(rowIdx);
		} // for

		reportDataRows.remove(rowIdx);
	} // removeRow

	public void truncateData(int startRow, int endRow) {
       if( endRow != -1){ 
		for (int r = getDataRowCount() - 1; r > endRow; r--)
			removeRow(r);
       }

		for (int r = startRow - 1; r >= 0; r--)
			removeRow(r);
	} // truncateData

	/** *********************************************************************************** */

	public void createColumn(String colId, String displayName, String displayWidthInPxls, String alignment, 
			boolean currentlyVisible, String currentSort, boolean isRuntimeColSortDisabled, int level, int start, int colspan, boolean sortable) {
		ColumnHeaderRow chr = null;
		if (getHeaderRowCount() > 0)
			chr = reportColumnHeaderRows.getColumnHeaderRow(0);
		else {
			chr = new ColumnHeaderRow();
			reportColumnHeaderRows.addColumnHeaderRow(chr);
			chr.setRowHeight("30");
		} // if
        /*ColumnHeader ch = new ColumnHeader(displayName, (displayWidth > 100) ? "10%" : (""
				+ displayWidth + "%"), alignment, 1, isRuntimeColSortDisabled ? null : colId);
				*/
		ColumnHeader ch = new ColumnHeader(displayName, displayWidthInPxls, alignment, 1, isRuntimeColSortDisabled ? null : colId);	
		ch.setGroupLevel(level);
		ch.setStartGroup(start);
		ch.setColSpanGroup(colspan);
        ch.setColId(colId);
        ch.setSortable(sortable);
		//chr.addColumnHeader(new ColumnHeader(displayName, (displayWidth > 100) ? "10%" : (""
			//	+ displayWidth + "%"), 1, isRuntimeColSortDisabled ? null : colId));
        
        if (displayName != "Hidden") { 
        
	        chr.addColumnHeader(ch);
			columnVisuals.add(new ColumnVisual(colId, displayName, currentlyVisible, currentSort));
		}
	} // createColumn

	public void columnVisualShowHide(String colId, boolean newVisible) {
		for (int i = 0; i < columnVisuals.size(); i++) {
			ColumnVisual col = (ColumnVisual) columnVisuals.get(i);
			if (col.getColId().equals(colId)) {
				col.setVisible(newVisible);
				applyColumnVisibility(i, newVisible);
				break;
			} // if
		} // for
	} // columnVisualShowHide

	private void applyColumnVisibility(int colIdx, boolean newVisible) {
		boolean isLast = true;
		for (int r = reportColumnHeaderRows.getRowCount() - 1; r >= 0; r--) {
			ColumnHeaderRow chr = reportColumnHeaderRows.getColumnHeaderRow(r);

			if (isLast) {
				chr.getColumnHeader(colIdx).setVisible(newVisible);
				isLast = false;
			} else {
				int curStartIdx = 0;
				for (chr.resetNext(); chr.hasNext();) {
					ColumnHeader ch = chr.getNext();
					if (colIdx >= curStartIdx && colIdx <= curStartIdx + ch.getColSpan() - 1) {
						ch.setVisible(newVisible);
						break;
					} else
						curStartIdx += ch.getColSpan();
				} // for
			} // else
		} // for
        int row = 0;
		for (reportDataRows.resetNext(); reportDataRows.hasNext();) {
			//reportDataRows.getNext().getDataValue(colIdx).setVisible(newVisible);
			DataRow dr = reportDataRows.getNext();
			if(colIdx < dr.getDataValueList().size()) {
				DataValue dv = dr.getDataValue(colIdx);
				dr.getDataValueList().remove(colIdx);
				if(!dv.isHidden())
					dv.setVisible(newVisible);
				else
					dv.setVisible(false);
				dr.addDataValue(colIdx, dv);
			}
			reportDataRows.removeElementAt(row);
			reportDataRows.addDataRow(row, dr);
			row++;			
		}
	} // applyColumnVisibility

	public void applyVisibility() {
		for (int i = 0; i < columnVisuals.size(); i++)
			applyColumnVisibility(i, ((ColumnVisual) columnVisuals.get(i)).isVisible());
	} // applyVisibility

	public void resetVisualSettings() {
		// No need to reset sort - if sort exists, the report data is reloaded
		for (int i = 0; i < columnVisuals.size(); i++)
			((ColumnVisual) columnVisuals.get(i)).setVisible(true);

		applyVisibility();
	} // resetVisualSettings

	/** *********************************************************************************** */

	private int nextVisualIdx = 0;

	public void resetNextVisual() {
		resetNextVisual(0);
	} // resetNext

	public void resetNextVisual(int toPos) {
		nextVisualIdx = toPos;
	} // resetNext

	public boolean hasNextVisual() {
		return (nextVisualIdx < columnVisuals.size());
	} // hasNext

	public String getNextHiddenColLinks(int toPos) {
		resetNextVisual(toPos);
		return getNextHiddenColLinks();
	} // getNextHiddenColLinks

	public String getNextHiddenColLinks() {
		if (!hasNextVisual())
			return "";

		StringBuffer sb = new StringBuffer();
		ColumnVisual col = (ColumnVisual) columnVisuals.get(nextVisualIdx);
		
		while (!col.isVisible()) {
			if(!col.getColDisplay().equals(AppConstants.HIDDEN)) {
				sb.append("<input type=image border=0 src='");
				sb.append(AppUtils.getImgFolderURL());
				sb.append("plus.gif' alt=\"Show column ");
				sb.append(col.getColDisplay());
				sb.append("\" width=11 height=11 onClick=\"document.formd.");
				sb.append(AppConstants.RI_VISUAL_ACTION);
				sb.append(".value='");
				sb.append(AppConstants.VA_SHOW);
				sb.append("';document.formd.");
				sb.append(AppConstants.RI_DETAIL_ID);
				sb.append(".value='");
				sb.append(col.getColId());
				sb.append("';document.formd.submit();\">");
			}

			if (!(nextVisualIdx + 1 < columnVisuals.size()))
				break;
			col = (ColumnVisual) columnVisuals.get(++nextVisualIdx);
		} // while

		return sb.toString();
	} // getNextHiddenColLinks

	public String getNextVisual() {
		if (!hasNextVisual())
			return null;

		ColumnVisual col = (ColumnVisual) columnVisuals.get(nextVisualIdx++);

		StringBuffer sb = new StringBuffer();

		if (!col.isVisible() || col.getColDisplay().equals(AppConstants.HIDDEN))
			return null;

		sb.append("\n\t\t\t<table width=100% border=0 cellspacing=0 cellpadding=0><tr>\n");
		sb
				.append("\t\t\t\t<td style=\"background:#ffffff;\" align=left valign=middle nowrap><input type=image border=0 src='");
		sb.append(AppUtils.getImgFolderURL());
		sb.append("minus.gif' alt=\"Hide column ");
		sb.append(col.getColDisplay());
		sb.append("\" width=11 height=11 onClick=\"document.formd.");
		sb.append(AppConstants.RI_VISUAL_ACTION);
		sb.append(".value='");
		sb.append(AppConstants.VA_HIDE);
		sb.append("';document.formd.");
		sb.append(AppConstants.RI_DETAIL_ID);
		sb.append(".value='");
		sb.append(col.getColId());
		sb.append("';document.formd.submit();");
		sb.append("\"></td>\n");
		sb.append("\t\t\t\t<td style=\"background:#ffffff;\" align=center valign=middle width=95% nowrap>");
		if (col.getSortType() != null) {
			sb.append("<img border=0 src='");
			sb.append(AppUtils.getImgFolderURL());
			if (col.getSortType().equals(AppConstants.SO_ASC))
				sb.append("grnarrowdn.gif");
			else
				sb.append("grnarrowup.gif");
			sb.append("' alt=\"Sorted by column ");
			sb.append(col.getColDisplay());
			if (col.getSortType().equals(AppConstants.SO_ASC))
				sb.append(" ascending");
			else
				sb.append(" descending");
			sb.append("\" width=13 height=11>");
		}
		sb.append("</td>\n");
		sb.append("\t\t\t\t<td style=\"background:#ffffff;\" align=right valign=middle nowrap>");
		sb.append(getNextHiddenColLinks());
		sb.append("</td>\n");
		sb.append("\t\t\t</tr></table>\n\t\t");

		return sb.toString();
	} // getNext

	/** *********************************************************************************** */

	public void setColumnDataTotalsLinear(DataRow colDataTotals, String colTotalLabel) {
		//commented so that we could differentiate data rows with total rows
		//reportDataRows.addDataRow(colDataTotals);
		RowHeader rh = new RowHeader(colTotalLabel, "45", 1, reportRowHeaderCols.getColumnCount(),true);
		if(reportRowHeaderCols.size()>0)
			reportRowHeaderCols.getRowHeaderCol(0).add(rh);

		reportDataTotalRow = new ReportDataRows();
		reportDataTotalRow.addDataRow(colDataTotals);
		RowHeaderCol rhc = new RowHeaderCol();
		rhc.add(rh);
		reportTotalRowHeaderCols = new ReportRowHeaderCols();
		reportTotalRowHeaderCols.addRowHeaderCol(0, rhc);
	} // setColumnDataTotalsLinear

	public void setCrossTabColumnTotalLabel(String colTotalLabel) {
		reportRowHeaderCols.getRowHeaderCol(0).getRowHeader(getDataRowCount() - 1)
				.setRowTitle("");
		reportRowHeaderCols.getRowHeaderCol(0).getRowHeader(getDataRowCount() - 1).setColSpan(
				0);
		reportRowHeaderCols.getRowHeaderCol(1).set(
				getDataRowCount() - 1,
				new RowHeader(colTotalLabel, "45", 1, reportRowHeaderCols.getColumnCount(),
						true));
	} // setCrossTabColumnTotalLabel

	public void setCrossTabColumnTotalLabel(int reportDataSize, String colTotalLabel) {
		
		reportRowHeaderCols.getRowHeaderCol(0).getRowHeader(getDataRowCount() - 1)
				.setRowTitle("");
		reportRowHeaderCols.getRowHeaderCol(0).getRowHeader(getDataRowCount() - 1).setColSpan(
				0);
		reportRowHeaderCols.getRowHeaderCol(1).set(
				getDataRowCount() - 1,
				new RowHeader(colTotalLabel, "45", 1, reportRowHeaderCols.getColumnCount(),
						true));
	} // setCrossTabColumnTotalLabel
	
	public void setColumnDataTotalsCrossTab(Vector colDataTotals, String totalsAlignment,
			String colDisplayTotal, CrossTabOrderManager crossTabOrderManager, List dataList) {
		DataRow totalsDataRow = new DataRow();
		for (int c = 0; c < getDataColumnCount(); c++)
			totalsDataRow.addDataValue(new DataValue());

		for (Iterator iter = colDataTotals.iterator(); iter.hasNext();) {
			CrossTabTotalValue tVal = (CrossTabTotalValue) iter.next();

			int colIdx = findColumnIdx(tVal.getHeaderValues(), null, crossTabOrderManager, false, dataList);
			if (colIdx >= 0) {
				DataValue dataValue = new DataValue();

				String totalValue = tVal.getTotalValue();
				// if(colDisplayTotal.length()>0&&(!
				// colDisplayTotal.equals(AppConstants.TOTAL_SUM_ID)))
				// totalValue =
				// nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(colDisplayTotal))+":
				// "+totalValue;
				dataValue.setDisplayValue(totalValue);
                //added below statement for displaying in excel as number
                dataValue.setDisplayTotal("SUM(");
				dataValue.setAlignment(totalsAlignment);
				dataValue.setBold(true);
				totalsDataRow.setDataValue(colIdx, dataValue);

				//totalsDataRow.addDataValue(dataValue);
				
			} // if
		} // for

		//reportDataRows.addDataRow(totalsDataRow);
		//dr.setRowValues(rowNameValues);
		Vector rowNameValues = new Vector();
		for (int i=0; i < reportRowHeaderCols.size(); i++) {
			if(i==0) {
				DataValue dataValue = new DataValue();
				dataValue.setBold(true);
				dataValue.setAlignment("center");
				dataValue.setDisplayValue("Total");
				rowNameValues.add(dataValue);
				//rowNameValues.add("Total");
			} else {
				//rowNameValues.add("");
				DataValue dataValue = new DataValue();
				dataValue.setDisplayValue("");
				rowNameValues.add(dataValue);
				
			}
		}
		totalsDataRow.setRowValues(rowNameValues);
		totalsDataRow.setRowNum(dataList.size());
		dataList.add(totalsDataRow);		
			//reportRowHeaderCols.getNext().addRowHeader(new RowHeader("", "30", 1, 0));
	} // setColumnDataTotalsCrossTab

	public void setRowDataTotalsCrossTab(Vector rowDataTotals, String totalsAlignment,
			String rowDisplayTotal, CrossTabOrderManager crossTabOrderManager, List dataList) {
		int colIdx = getDataColumnCount();

		boolean isFirst = true;
		for (reportColumnHeaderRows.resetNext(); reportColumnHeaderRows.hasNext();) {
			ColumnHeaderRow chr = reportColumnHeaderRows.getNext();
			if (isFirst) {
				String totalLabel = "Total";
				if (rowDisplayTotal.length() > 0
						&& (!rowDisplayTotal.equals(AppConstants.TOTAL_SUM_ID)))
					totalLabel = nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(rowDisplayTotal));
				chr.addColumnHeader(colIdx, new ColumnHeader(totalLabel, "", 1,
						reportColumnHeaderRows.getRowCount()));
				isFirst = false;
			} else
				chr.addColumnHeader(colIdx, new ColumnHeader("", "", 1, 0));
		} // for

		for (reportDataRows.resetNext(); reportDataRows.hasNext();) {
			DataRow dr = reportDataRows.getNext();
			dr.addDataValue(colIdx, new DataValue());
		} // for

		for (Iterator iter = rowDataTotals.iterator(); iter.hasNext();) {
			CrossTabTotalValue tVal = (CrossTabTotalValue) iter.next();

			int rowIdx = findRowIdx(tVal.getHeaderValues(), crossTabOrderManager, false, dataList);
			if (rowIdx >= 0) {
				DataValue dataValue = new DataValue();

				String totalValue = tVal.getTotalValue();
				// if(rowDisplayTotal.length()>0&&(!
				// rowDisplayTotal.equals(AppConstants.TOTAL_SUM_ID)))
				// totalValue =
				// nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(rowDisplayTotal))+":
				// "+totalValue;
				dataValue.setDisplayValue(totalValue);
                //added below statement for displaying in excel as number
                dataValue.setDisplayTotal("SUM("); 
				dataValue.setAlignment(totalsAlignment);
				dataValue.setBold(true);

				//reportDataRows.getDataRow(rowIdx).getDataValueList().add(colIdx, dataValue);
				((DataRow)dataList.get(rowIdx)).addDataValue(colIdx, dataValue);
			} // if
		} // for
	} // setRowDataTotalsCrossTab

	public void setGrandTotalCrossTab(String totalValue, String totalAlignment,
			String displayTotal, List dataList) {
		DataValue dataValue = new DataValue();

		// if(displayTotal.length()>0&&(!
		// displayTotal.equals(AppConstants.TOTAL_SUM_ID)))
		// totalValue =
		// nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(displayTotal))+":
		// "+totalValue;
		dataValue.setDisplayValue(totalValue);
        //added below statement for displaying in excel as number
        dataValue.setDisplayTotal("SUM("); 
		dataValue.setAlignment(totalAlignment);
		dataValue.setBold(true);

		//int colIdx = getDataColumnCount() - 1;
		int rowIdx = getDataRowCount() - 1;
		//reportDataRows.getDataRow(rowIdx).getDataValueList().add(colIdx, dataValue);
		DataRow dr = (DataRow)dataList.get(rowIdx);
		dr.addDataValue(dr.getDataValueList().size(), dataValue);
	} // setGrandTotalCrossTab

	public List getReportDataList() {
		return reportDataList;
	}

	public void setReportDataList(List reportDataList) {
		this.reportDataList = reportDataList;
	}

} // ReportData
