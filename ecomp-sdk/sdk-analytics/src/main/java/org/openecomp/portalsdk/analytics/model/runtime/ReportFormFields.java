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
package org.openecomp.portalsdk.analytics.model.runtime;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.DataCache;
import org.openecomp.portalsdk.analytics.model.base.ReportWrapper;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.xmlobj.ColFilterType;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldType;

public class ReportFormFields extends Vector {
	private int nextElemIdx = 0;

	public ReportFormFields(ReportWrapper rw, HttpServletRequest request) throws RaptorException {
		super();

		if (rw.getFormFieldList() != null)
			for (Iterator iter = rw.getFormFieldList().getFormField().iterator(); iter
					.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();

				String fieldName = fft.getFieldId();
				String fieldDisplayName = fft.getFieldName();
				String fieldType = fft.getFieldType();
				String validationType = fft.getValidationType();
				String mandatory = nvl(fft.getMandatory(), "N");
				String defaultValue = fft.getDefaultValue();
				String fieldSQL = fft.getFieldSQL();
                String fieldDefaultSQL = fft.getFieldDefaultSQL();
                String[] reqParameters = Globals.getRequestParams().split(",");
                String[] sessionParameters = Globals.getSessionParams().split(",");
                String[] scheduleSessionParameters = Globals.getSessionParamsForScheduling().split(",");
                javax.servlet.http.HttpSession session = request.getSession();
                String visible = nvl(fft.getVisible(),"Y");
                String dependsOn = nvl(fft.getDependsOn(), "");
                Calendar rangeStartDate = (fft.getRangeStartDate()==null)?null:fft.getRangeStartDate().toGregorianCalendar(); 
                Calendar rangeEndDate = (fft.getRangeEndDate()==null)?null:fft.getRangeEndDate().toGregorianCalendar(); 
				//Calendar rangeEndDate = fft.getRangeEndDate().toGregorianCalendar(); 
				String rangeStartDateSQL = fft.getRangeStartDateSQL(); 
				String rangeEndDateSQL = fft.getRangeEndDateSQL(); 
				String user_id = AppUtils.getUserID(request);
				String multiSelectListSize = fft.getMultiSelectListSize();
				
				//s_logger.debug("ranges are : " + fft.getRangeStartDate() + fft.getRangeEndDate());
				//s_logger.debug("fieldSQL B4" + fieldSQL);
                if(fieldSQL!=null) {
                    for (int i = 0; i < reqParameters.length; i++) {
                        if(!reqParameters[i].startsWith("ff") && (request.getParameter(reqParameters[i].toUpperCase())!=null && request.getParameter(reqParameters[i].toUpperCase()).length() > 0))
                         fieldSQL = Utils.replaceInString(fieldSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                        else if (request.getParameter(reqParameters[i])!=null && request.getParameter(reqParameters[i]).length() > 0)
                         fieldSQL = Utils.replaceInString(fieldSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                    }

	                for (int i = 0; i < scheduleSessionParameters.length; i++) {
	                	//s_logger.debug(" Session " + " scheduleSessionParameters[i] " + scheduleSessionParameters[i].toUpperCase() + " " + request.getParameter(scheduleSessionParameters[i]));
	                	if(request.getParameter(scheduleSessionParameters[i])!=null && request.getParameter(scheduleSessionParameters[i]).trim().length()>0 )
	                		fieldSQL = Utils.replaceInString(fieldSQL, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
	                	if(request.getAttribute(scheduleSessionParameters[i])!=null && ((String)request.getAttribute(scheduleSessionParameters[i])).trim().length()>0 )
	                		fieldSQL = Utils.replaceInString(fieldSQL, "[" + scheduleSessionParameters[i].toUpperCase()+"]", (String) request.getAttribute(scheduleSessionParameters[i]) );

	                }

                    for (int i = 0; i < sessionParameters.length; i++) {
                        //if(!sessionParameters[i].startsWith("ff"))
                         //fieldSQL = Utils.replaceInString(fieldSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                        //else {
                    	if (session.getAttribute(sessionParameters[i])!=null && ((String)session.getAttribute(sessionParameters[i])).length() > 0) {  
                         //s_logger.debug(" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i]));
                         fieldSQL = Utils.replaceInString(fieldSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
                    	}
                       // }
                    }
                    fieldSQL = Utils.replaceInString(fieldSQL, "[USERID]", user_id);
                    fieldSQL = Utils.replaceInString(fieldSQL, "[USER_ID]", user_id);
                    fieldSQL = Utils.replaceInString(fieldSQL, "[LOGGED_USERID]", user_id);
                    
                }

                if(fieldDefaultSQL!=null) {
                    for (int i = 0; i < reqParameters.length; i++) {
                        if(!reqParameters[i].startsWith("ff") && (request.getParameter(reqParameters[i].toUpperCase())!=null && request.getParameter(reqParameters[i].toUpperCase()).length() > 0))
                         fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                        else if (request.getParameter(reqParameters[i])!=null && request.getParameter(reqParameters[i]).length() > 0)
                         fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                    }
	                for (int i = 0; i < scheduleSessionParameters.length; i++) {
	                	//s_logger.debug(" Session " + " scheduleSessionParameters[i] " + scheduleSessionParameters[i].toUpperCase() + " " + request.getParameter(scheduleSessionParameters[i]));
	                	if(request.getParameter(scheduleSessionParameters[i])!=null && request.getParameter(scheduleSessionParameters[i]).trim().length()>0 )
	                		fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
	                	if(request.getAttribute(scheduleSessionParameters[i])!=null && ((String)request.getAttribute(scheduleSessionParameters[i])).trim().length()>0 )
	                		fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + scheduleSessionParameters[i].toUpperCase()+"]", (String) request.getAttribute(scheduleSessionParameters[i]) );
	                	
	                }
                    
                    for (int i = 0; i < sessionParameters.length; i++) {
                        //if(!sessionParameters[i].startsWith("ff"))
                        	//fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                        //else
                    	if (session.getAttribute(sessionParameters[i])!=null && ((String)session.getAttribute(sessionParameters[i])).length() > 0) 
                        	fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );   
                    }
                    
                    fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[USERID]", user_id);
                    fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[USER_ID]", user_id);
                    fieldDefaultSQL = Utils.replaceInString(fieldDefaultSQL, "[LOGGED_USERID]", user_id);
                }
				//s_logger.debug("fieldSQL After" + fieldSQL);
				if(rangeStartDateSQL!=null) {
                    for (int i = 0; i < reqParameters.length; i++) {
                        if(!reqParameters[i].startsWith("ff") && (request.getParameter(reqParameters[i].toUpperCase())!=null && request.getParameter(reqParameters[i].toUpperCase()).length() > 0))
                        	rangeStartDateSQL = Utils.replaceInString(rangeStartDateSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                        else if (request.getParameter(reqParameters[i])!=null && request.getParameter(reqParameters[i]).length() > 0)
                        	rangeStartDateSQL = Utils.replaceInString(rangeStartDateSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                    }
                    for (int i = 0; i < sessionParameters.length; i++) {
                    	if (session.getAttribute(sessionParameters[i])!=null && ((String)session.getAttribute(sessionParameters[i])).length() > 0)
                        rangeStartDateSQL = Utils.replaceInString(rangeStartDateSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );   
                    }                         
                }
				if(rangeEndDateSQL!=null) {
                    for (int i = 0; i < reqParameters.length; i++) {
                        if(!reqParameters[i].startsWith("ff")&& (request.getParameter(reqParameters[i].toUpperCase())!=null && request.getParameter(reqParameters[i].toUpperCase()).length() > 0))
                        	rangeEndDateSQL = Utils.replaceInString(rangeEndDateSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                        else if (request.getParameter(reqParameters[i])!=null && request.getParameter(reqParameters[i]).length() > 0)
                        	rangeEndDateSQL = Utils.replaceInString(rangeEndDateSQL, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                    }
                    for (int i = 0; i < sessionParameters.length; i++) {
                    	if (session.getAttribute(sessionParameters[i])!=null && ((String)session.getAttribute(sessionParameters[i])).length() > 0)
                        rangeEndDateSQL = Utils.replaceInString(rangeEndDateSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );   
                    }                         
                }
				String helpText = fft.getComment();

				List predefinedValues = null;
				if (fft.getPredefinedValueList() != null) {
					predefinedValues = fft.getPredefinedValueList().getPredefinedValue();
					if (predefinedValues.size() == 0)
						predefinedValues = null;
				} // if

				DataColumnType dct = rw.getColumnById(nvl(fft.getColId()));

				boolean basedOnColumn = false;
				if (rw.getReportDefType().equals(AppConstants.RD_SQL_BASED))
					basedOnColumn = (nvl(fft.getColId()).indexOf('.') > 0);
				else
					basedOnColumn = (dct != null);

				if (((!basedOnColumn) && (nvl(fieldSQL).length() == 0))
						|| predefinedValues != null) {
					if (predefinedValues != null)
						if (nvl(defaultValue).equals(AppConstants.FILTER_MAX_VALUE))
							defaultValue = (String) Collections.max(predefinedValues);
						else if (nvl(defaultValue).equals(AppConstants.FILTER_MIN_VALUE))
							defaultValue = (String) Collections.min(predefinedValues);
					add(new FormField(fieldName, fieldDisplayName, fieldType, validationType,
							mandatory.equals("Y"), defaultValue, helpText, predefinedValues,visible.equals("Y"), dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize));
				} else if (nvl(fieldSQL).length() > 0) {
					add(new FormField(fieldName, fieldDisplayName, fieldType, validationType,
							mandatory.equals("Y"), fieldDefaultSQL, helpText, fieldSQL,visible.equals("Y"), dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize));
				} else {
					String lTableName = null;
					String lColumnName = null;
					String lColumnType = null;
					String lColFormat = null;
					if (rw.getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
						String colId = nvl(fft.getColId());
						lTableName = ReportWrapper.getSQLBasedFFTColTableName(colId); // colId.substring(0,
																						// colId.indexOf('.'));
						lColumnName = ReportWrapper.getSQLBasedFFTColColumnName(colId); // colId.substring(colId.lastIndexOf('.')+1);
						lColumnType = AppConstants.CT_CHAR;
						try {
							lColumnType = nvl(DataCache.getReportTableDbColumnType(lTableName,
									lColumnName, rw.getDBInfo()), AppConstants.CT_CHAR);
						} catch (Exception e) {
						}
						lColFormat = lColumnType.equals(AppConstants.CT_DATE) ? nvl(
								ReportWrapper.getSQLBasedFFTColDisplayFormat(colId),
								AppConstants.DEFAULT_DATE_FORMAT) : "";
					} else {
						lTableName = rw.getColumnTableById(dct.getColId()).getTableName(); // should
																							// be
																							// same
																							// as
																							// rw.getTableById(dct.getTableId()).getTableName()
						lColumnName = dct.getColName();
						lColumnType = dct.getColType();
						lColFormat = nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT);
					} // else

					if (nvl(defaultValue).equals(AppConstants.FILTER_MAX_VALUE)
							|| nvl(defaultValue).equals(AppConstants.FILTER_MIN_VALUE))
						try {
							String selectVal = (defaultValue
									.equals(AppConstants.FILTER_MAX_VALUE) ? "MAX" : "MIN")
									+ "(" + lColumnName + ")";
							if (lColumnType.equals(AppConstants.CT_DATE))
								selectVal = "TO_CHAR(" + selectVal + ", '"
										+ AppConstants.DEFAULT_DATE_FORMAT + "')";
							// DataSet ds = DbUtils.executeQuery("SELECT
							// "+selectVal+" FROM "+lTableName);
							DataSet ds = ConnectionUtils.getDataSet("SELECT " + selectVal
									+ " FROM " + lTableName, rw.getDBInfo());
							if (ds.getRowCount() > 0)
								defaultValue = ds.getString(0, 0);
						} catch (Exception e) {
						}

					LookupDBInfo lookupDBInfo = DataCache.getLookupTable(lTableName,
							lColumnName);
					String lookupTable = lookupDBInfo.getLookupTable();
					String lookupIdField = lookupDBInfo.getLookupIdField();
					String lookupNameField = lookupDBInfo.getLookupNameField();
					String lookupSortByField = lookupDBInfo.getLookupNameField();
					if (lColumnType.equals(AppConstants.CT_DATE)) {
						// Expects lookup on DATE fields will have both Id and
						// Name fields with DATE format; if not the case will
						// generate an error
						lookupIdField = "TO_CHAR(" + lookupIdField + ", '"
								+ AppConstants.DEFAULT_DATE_FORMAT + "')";
                                                lookupSortByField = " TO_DATE(TO_CHAR("+ lookupNameField +", '" + AppConstants.DEFAULT_DATE_FORMAT+ "'),'" + AppConstants.DEFAULT_DATE_FORMAT+ "') ";                        
						lookupNameField = "TO_CHAR(" + lookupNameField + ", '" + lColFormat
								+ "')";
						lookupSortByField += " DESC";
					} // if
                    if (fieldDefaultSQL!=null && fieldDefaultSQL.length()>0 && (fieldDefaultSQL.trim().length()>10) && fieldDefaultSQL.substring(0,10).toLowerCase().startsWith("select")) {
                     add(new FormField(fieldName, fieldDisplayName, fieldType, validationType,
                            mandatory.equals("Y"), fieldDefaultSQL, helpText, lookupTable,
                            lookupIdField, lookupNameField, lookupSortByField,visible.equals("Y"),dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize));
                        
                    } else {
					 add(new FormField(fieldName, fieldDisplayName, fieldType, validationType,
							mandatory.equals("Y"), defaultValue, helpText, lookupTable,
							lookupIdField, lookupNameField, lookupSortByField,visible.equals("Y"), dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize));
                    }
				} // else
			} // for

		List reportCols = rw.getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (dct.getColFilterList() != null) {
				int fNo = 0;
				List fList = dct.getColFilterList().getColFilter();
				for (Iterator iterF = fList.iterator(); iterF.hasNext(); fNo++) {
					ColFilterType cft = (ColFilterType) iterF.next();

					if (nvl(cft.getArgType()).equals(AppConstants.AT_FORM)
							&& rw.getFormFieldByDisplayValue(cft.getArgValue()) == null) {
						String fieldName = rw.getFormFieldName(cft);
						String fieldDisplayName = rw.getFormFieldDisplayName(dct, cft);

						LookupDBInfo lookupDBInfo = DataCache.getLookupTable(rw
								.getColumnTableById(dct.getColId()).getTableName(), dct
								.getColName());
						String lookupTable = lookupDBInfo.getLookupTable();
						String lookupIdField = lookupDBInfo.getLookupIdField();
						String lookupNameField = lookupDBInfo.getLookupNameField();
						String lookupSortByField = lookupDBInfo.getLookupNameField();
						Calendar lookupRangeStartDate = rw.getFormFieldRangeStart(cft); 
						Calendar lookupRangeEndDate = rw.getFormFieldRangeEnd(cft); 
						String lookupRangeStartDateSQL = rw.getFormFieldRangeStartSQL(cft); 
						String lookupRangeEndDateSQL = rw.getFormFieldRangeEndSQL(cft); 
						if (dct.getColType().equals(AppConstants.CT_DATE)) {
							// Expects lookup on DATE fields will have both Id
							// and Name fields with DATE format; if not the case
							// will generate an error
							lookupIdField = "TO_CHAR("
									+ lookupIdField
									+ ", '"
									+ nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT)
									+ "')";
							lookupNameField = "TO_CHAR("
									+ lookupNameField
									+ ", '"
									+ nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT)
									+ "')";
							lookupSortByField += " DESC";
						} // if
						add(new FormField(fieldName, fieldDisplayName,
								FormField.FFT_TEXT_W_POPUP, null, false, null, null,
								lookupTable, lookupIdField, lookupNameField, lookupSortByField,null, lookupRangeStartDate, lookupRangeEndDate, lookupRangeStartDateSQL, lookupRangeEndDateSQL, "0"));
					} // if
				} // for
			} // if
		} // for
	} // ReportFormFields

	public int getFieldCount() {
		return size();
	} // getFieldCount

	public FormField getFormField(int fieldIdx) {
		return (FormField) get(fieldIdx);
	} // getFormField

	public FormField getFormField(String fieldName) {
		for (int i = 0; i < getFieldCount(); i++) {
			FormField ff = (FormField) get(i);
			if (ff.getFieldName().equals(fieldName))
				return ff;
		} // for

		return null;
	} // getFormField

	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < size());
	} // hasNext

	public FormField getNext() {
		return hasNext() ? getFormField(nextElemIdx++) : null;
	} // getNext

	/** ************************************************************************************************* */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

} // ReportFormFields
