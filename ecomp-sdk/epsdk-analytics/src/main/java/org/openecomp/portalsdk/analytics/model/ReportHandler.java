/*-
 * ================================================================================
 * ECOMP Portal SDK
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
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * ReportHandler.java - This class is used to generate reports in Excel using POI and also to 
 * create ReportRuntime and ReportDefinition object using report id.
 * -------------------------------------------------------------------------------------------
 *
 *
 * Changes
 * -------
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Signature for generating excel method has been changed to add the report name as sheet name. </LI>
 *                                     <LI> Dashboard reports can be downloaded with each report as a separate sheet. </LI>
 *                                     </UL>   
 * 08-Jun-2009 : Version 8.3 (Sundar); <UL><LI> Short datatype is replaced with default integer datatype to create 
 *               row as short is not expoting more than 32768 rows. </LI></UL>						
 *
 */
package org.openecomp.portalsdk.analytics.model;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openecomp.portalsdk.analytics.controller.ErrorHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.model.definition.ReportDefinition;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.ExecuteQuery;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.ExcelColorDef;
import org.openecomp.portalsdk.analytics.util.HtmlStripper;
import org.openecomp.portalsdk.analytics.util.Log;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.view.ColumnHeader;
import org.openecomp.portalsdk.analytics.view.ColumnHeaderRow;
import org.openecomp.portalsdk.analytics.view.DataRow;
import org.openecomp.portalsdk.analytics.view.DataValue;
import org.openecomp.portalsdk.analytics.view.HtmlFormatter;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.analytics.view.RowHeader;
import org.openecomp.portalsdk.analytics.view.RowHeaderCol;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.DataSourceType;
import org.openecomp.portalsdk.analytics.xmlobj.FormatList;
import org.openecomp.portalsdk.analytics.xmlobj.FormatType;
import org.openecomp.portalsdk.analytics.xmlobj.Reports;
import org.openecomp.portalsdk.analytics.xmlobj.SemaphoreList;
import org.openecomp.portalsdk.analytics.xmlobj.SemaphoreType;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPTable;
//import javax.servlet.RequestDispatcher;

public class ReportHandler extends org.openecomp.portalsdk.analytics.RaptorObject {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportHandler.class);

    public ReportHandler() {
	}

	private String SHEET_NAME = "";
	private static final String XML_ENCODING = "UTF-8";
	private static int font_size = 10;
	private static int font_header_title_size = 12;
	private static int font_header_descr_size = 9;
	private static int font_footer_size = 9;
	

	private HashMap loadStyles(ReportRuntime rr, HSSFWorkbook wb) {
		HSSFCellStyle styleDefault = wb.createCellStyle();
        //System.out.println("Load Styles");
		// Style default will be normal with no background
		HSSFFont fontDefault = wb.createFont();
		// The default will be plain .
		fontDefault.setColor((short) HSSFFont.COLOR_NORMAL);
		fontDefault.setFontHeight((short) (font_size / 0.05));
		fontDefault.setFontName("Tahoma");

		styleDefault.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleDefault.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleDefault.setFillPattern(HSSFCellStyle.NO_FILL);
		styleDefault.setFont(fontDefault);
		
		HSSFCellStyle styleRed = wb.createCellStyle();
		styleRed.cloneStyleFrom(styleDefault);
		styleRed.setFillForegroundColor((short)HSSFColor.RED.index);
		styleRed.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont fontRed = wb.createFont();
		fontRed.setColor((short) HSSFColor.WHITE.index);
		fontRed.setFontHeight((short) (font_size / 0.05));
		fontRed.setFontName("Tahoma");
		styleRed.setFont(fontRed);
		
		HSSFCellStyle styleYellow = wb.createCellStyle();
		styleYellow.cloneStyleFrom(styleDefault);
		styleYellow.setFillForegroundColor((short)HSSFColor.YELLOW.index);
		styleYellow.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont fontYellow = wb.createFont();
		fontYellow.setColor((short) HSSFColor.BLACK.index);
		fontYellow.setFontHeight((short) (font_size / 0.05));
		fontYellow.setFontName("Tahoma");
		styleYellow.setFont(fontYellow);
		
		HSSFCellStyle styleGreen = wb.createCellStyle();
		styleGreen.cloneStyleFrom(styleDefault);
		styleGreen.setFillForegroundColor((short)HSSFColor.GREEN.index);
		styleGreen.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont fontGreen = wb.createFont();
		fontGreen.setColor((short) HSSFColor.WHITE.index);
		fontGreen.setFontHeight((short) (font_size / 0.05));
		fontGreen.setFontName("Tahoma");
		styleGreen.setFont(fontGreen);
		
		
        ArrayList semColumnList = new ArrayList();
        List dsList = rr.getDataSourceList().getDataSource();
        for (Iterator iter = dsList.iterator(); iter.hasNext();) {
            DataSourceType element = (DataSourceType) iter.next();
            List dcList = element.getDataColumnList().getDataColumn();
            for (Iterator iterator = dcList.iterator(); iterator.hasNext();) {
                DataColumnType element1 = (DataColumnType) iterator.next();
                semColumnList.add(element1.getSemaphoreId());
                
            }
        }
		SemaphoreList semList = rr.getSemaphoreList();
		HashMap hashMapStyles = new HashMap();
		HashMap hashMapFonts = new HashMap();
		hashMapFonts.put("default", fontDefault);
		hashMapFonts.put("red", fontRed);
		hashMapFonts.put("yellow", fontYellow);
		hashMapFonts.put("green", fontGreen);
		hashMapStyles.put("default", styleDefault);
		hashMapStyles.put("red", styleRed);
		hashMapStyles.put("yellow", styleYellow);
		hashMapStyles.put("green", styleGreen);
		HSSFCellStyle cellStyle = null;
		if (semList == null || semList.getSemaphore() == null) {
			return hashMapStyles;
		} else {
			for (Iterator iter = semList.getSemaphore().iterator(); iter.hasNext();) {
				SemaphoreType sem = (SemaphoreType) iter.next();
                if(!semColumnList.contains(sem.getSemaphoreId())) continue;
                //System.out.println("SemphoreId ----> " + sem.getSemaphoreId());
				FormatList fList = sem.getFormatList();
				List formatList = fList.getFormat();
				for (Iterator fIter = formatList.iterator(); fIter.hasNext();) {
					FormatType fmt = (FormatType) fIter.next();
					if(fmt!=null){
					//if (fmt.getLessThanValue().length() > 0) {
						cellStyle = wb.createCellStyle();
						HSSFFont cellFont = wb.createFont();
                        //System.out.println("Format Id " + fmt.getFormatId());
						if (nvl(fmt.getBgColor()).length() > 0) {
//							 System.out.println("Load Styles " +
//							 fmt.getFormatId()
//							 + " " +fmt.getBgColor() + " " +
//							 ExcelColorDef.getExcelColor(fmt.getBgColor()));
							cellStyle.setFillForegroundColor(ExcelColorDef.getExcelColor(fmt
									.getBgColor()));
							cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
						}
						if (nvl(fmt.getFontColor()).length() > 0) {
							cellFont.setColor(ExcelColorDef.getExcelColor(fmt.getFontColor()));
						} else
							cellFont.setColor((short) HSSFFont.COLOR_NORMAL);
						if (fmt.isBold())
							cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
						if (fmt.isItalic())
							cellFont.setItalic(true);
						if (fmt.isUnderline())
							cellFont.setUnderline(HSSFFont.U_SINGLE);
						if(nvl(fmt.getFontFace()).length()>0)
							cellFont.setFontName(fmt.getFontFace());
						else
							cellFont.setFontName("Tahoma");
						//cellFont.setFontHeight((short) (10 / 0.05));
						
						if(nvl(fmt.getFontSize()).length()>0) {
						  try {	
						    //cellFont.setFontHeight((short) (Integer.parseInt(fmt.getFontSize()) / 0.05));
							  cellFont.setFontHeight((short) (font_size/0.05));
						  } catch(NumberFormatException e){
						   cellFont.setFontHeight((short) (font_size / 0.05));//10
						  }
						}
						else
						  cellFont.setFontHeight((short) (font_size / 0.05));
						cellStyle.setFont(cellFont);
						cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
						cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
						hashMapStyles.put(fmt.getFormatId(), cellStyle);
					} else {
						hashMapStyles.put(fmt.getFormatId(), styleDefault);
						hashMapStyles.put("default", styleDefault);
					}
				}

			}
		}
		return hashMapStyles;
	}

	private void paintExcelParams(HSSFWorkbook wb,int rowNum,int col,ArrayList paramsList, String customizedParamInfo, HSSFSheet sheet, String reportTitle, String reportDescr) throws IOException {
        //HSSFSheet sheet = wb.getSheet(getSheetName());
        int cellNum = 0;
        HSSFRow row = null;
        short s1 = 0, s2 = (short) 1;
        HtmlStripper strip = new HtmlStripper();
        // Name Style
        HSSFCellStyle styleName = wb.createCellStyle();
        //styleName.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
        styleName.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        //styleName.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
        styleName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleName.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleName.setDataFormat((short)0);
        HSSFFont font = wb.createFont();
        font.setFontHeight((short) (font_size / 0.05));
        font.setFontName("Tahoma");
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleName.setFont(font);
        //Data Style
        
        // Create some fonts.
        HSSFFont fontDefault = wb.createFont();
        // Initialize the styles & fonts.
        // The default will be plain .
        fontDefault.setColor((short) HSSFFont.COLOR_NORMAL);
        fontDefault.setFontHeight((short) (font_size / 0.05));
        fontDefault.setFontName("Tahoma");
        fontDefault.setItalic(true);
        // Style default will be normal with no background
        HSSFCellStyle styleValue = wb.createCellStyle();
        styleValue.setDataFormat((short)0);
        styleValue.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleValue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleValue.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleValue.setFillPattern(HSSFCellStyle.NO_FILL);
        styleValue.setFont(fontDefault);
        HSSFCell cell = null;
        HSSFCellStyle styleDescription = wb.createCellStyle();
        styleDescription.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        styleDescription.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderLeft(HSSFCellStyle.BORDER_THIN);        
        HSSFFont fontDescr = wb.createFont();
        fontDescr.setFontHeight((short) (font_size / 0.05)); //14
        fontDescr.setFontName("Tahoma");
        fontDescr.setColor(HSSFColor.BLACK.index);
        fontDescr.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleDescription.setFont(font);
        HSSFCell cellDescr = null;
        int paramSeq = 0;
        HSSFHeader header = sheet.getHeader();
        StringBuffer strBuf = new StringBuffer(); 
        if(!Globals.customizeFormFieldInfo() || customizedParamInfo.length()<=0) {
	        for (Iterator iter = paramsList.iterator(); iter.hasNext();) {
	            IdNameValue value = (IdNameValue) iter.next();
	            //System.out.println("\"" + value.getId() + " = " + value.getName() + "\"");
	            if(nvl(value.getId()).trim().length()>0  && (!nvl(value.getId()).trim().equals("BLANK"))) {
	                paramSeq += 1;
	                if(paramSeq <= 1) {
	                    row = sheet.createRow(++rowNum);
	                    cell = row.createCell((short) 0);
	                    sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
	                    cellDescr = row.createCell((short) 0);
	                    cellDescr.setCellValue("Run-time Parameters");
	                    cellDescr.setCellStyle(styleDescription);
	                    

	                	strBuf.append(reportTitle+"\n"); 
	                	//strBuf.append("Run-time Parameters\n");
	                }
		                row = sheet.createRow(++rowNum);    
		                cellNum = 0;
		                //System.out.println("RowNum " + rowNum + " " + value.getId() + " " +value.getName());
		                cell = row.createCell((short) cellNum);
		                cell.setCellValue(value.getId());
		                cell.setCellStyle(styleName); 
		                cellNum += 1;
		                cell = row.createCell((short) cellNum);
		                cell.setCellValue(value.getName().replaceAll("~",","));
		                cell.setCellStyle(styleValue);

	                	//strBuf.append(value.getId()+": "+ value.getName()+"\n");
	               }
            } //for
        } else {
        	strBuf.append(reportTitle+"\n");
    		Document document = new Document();
    		document.open();        	
            HTMLWorker worker = new HTMLWorker(document);
        	StyleSheet style = new StyleSheet();
        	style.loadTagStyle("body", "leading", "16,0");
        	ArrayList p = HTMLWorker.parseToList(new StringReader(customizedParamInfo), style);
        	String name = "";
        	String token = "";
        	String value = "";
        	String s = "";
        	PdfPTable pdfTable = null;
    	   	 for (int k = 0; k < p.size(); ++k){
    	   		if(p.get(k) instanceof Paragraph) 
    	   			s = ((Paragraph)p.get(k)).toString();
    	   		else { /*if ((p.get(k) instanceof PdfPTable))*/
    	   			pdfTable = ((PdfPTable)p.get(k));
    	   		}
    	   		//todo: Logic for parsing pdfTable should be added after upgrading to iText 5.0.0
    	   		//s = Utils.replaceInString(s, ",", "|");
    	   		s = s.replaceAll(",", "|");	
    	   		s = s.replaceAll("~", ",");
    	 	    if(s.indexOf(":")!= -1) {
    	 	    	//System.out.println("|"+s+"|");
                    row = sheet.createRow(++rowNum); 
                    cell = row.createCell((short) 0);
                    sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
                    cellDescr = row.createCell((short) 0);
                    cellDescr.setCellValue("Run-time Parameters");
                    cellDescr.setCellStyle(styleDescription);   
    	 	    	 
    	 	    	//strBuf.append("Run-time Parameters\n");
    	 	    	StringTokenizer st = new StringTokenizer(s.trim(), "|");
    	 	    	while(st.hasMoreTokens()) {
    	 	    		token = st.nextToken();
    	 	    		token = token.trim();
    	 	    		if (!(token.trim().equals("|") || token.trim().equals("]]") || token.trim().equals("]") || token.trim().equals("[") )) {
    		 	    		if(token.endsWith(":")) {
    		 	    			name = token;
    		 	    			name = name.substring(0, name.length()-1);
    		 	    			if(name.startsWith("[")) 
    		 	    				name = name.substring(1);
    		 	    			value = st.nextToken();    		 	    			
    		 	    			if(nvl(value).endsWith("]"))value = nvl(value).substring(0, nvl(value).length()-1);
    		 	    		} /*else if(name != null && name.length() > 0) {
    		 	    			value = st.nextToken();
    		 	    			if(value.endsWith("]]"))value = value.substring(0, value.length()-1);
    		 	    		}*/
    		 	    		if(name!=null && name.trim().length()>0) {
    		 	    			row = sheet.createRow((short) ++rowNum);
    		 	    			cellNum = 0;
    		 	    			cell = row.createCell((short) cellNum);
    			                cell.setCellValue(name.trim());
    			                cell.setCellStyle(styleName); 
    			                cellNum += 1;
    			                cell = row.createCell((short) cellNum);
    			                cell.setCellValue(value.trim());
    			                cell.setCellStyle(styleValue); 
    		 	    			//strBuf.append(name.trim()+": "+ value.trim()+"\n");
    		 	    		}
/*    		 	    		if(token.endsWith(":") && (value!=null && value.trim().length()<=0) && (name!=null && name.trim().length()>0 && name.endsWith(":"))) {
    		 	    			name = name.substring(0, name.indexOf(":")+1);
    		 	    			//value = token.substring(token.indexOf(":")+1);
    		 	    			row = sheet.createRow((short) ++rowNum);
    		 	    			cellNum = 0;
    		 	    			cell = row.createCell((short) cellNum);
    			                cell.setCellValue(name.trim());
    			                cell.setCellStyle(styleName); 
    			                cellNum += 1;
    			                cell = row.createCell((short) cellNum);
    			                cell.setCellValue(value.trim());
    			                cell.setCellStyle(styleValue);
    			                    			                
    		 	    			//strBuf.append(name.trim()+": "+ value.trim()+"\n");
    			                value = "";
    			                name = "";
    		 	    		}
*/    	 	    		}
    	 				int cw = 0;
    					cw =  name.trim().length() + 12;
    					// if(i!=cellWidth.size()-1)
    					if(sheet.getColumnWidth((short)0)< (short) name.trim().length())
    					sheet.setColumnWidth((short)0, (short) name.trim().length());
    					if(sheet.getColumnWidth((short)1)< (short) value.trim().length())
    					sheet.setColumnWidth((short)1, (short) value.trim().length());
		                name = "";
		                value = "";
    					
    	 	    	}

	                try {
				        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
				        Date sysdate = oracleDateFormat.parse(ReportLoader.getSystemDateTime());
				        SimpleDateFormat dtimestamp = new SimpleDateFormat(Globals.getScheduleDatePattern());
	 	    		
    	 	    		row = sheet.createRow((short) ++rowNum);
	 	    			cellNum = 0;
	 	    			cell = row.createCell((short) cellNum);
		                cell.setCellValue("Report Date/Time");
		                cell.setCellStyle(styleName); 
		                cellNum += 1;
		                cell = row.createCell((short) cellNum);
		                
		                cell.setCellValue(dtimestamp.format(sysdate)+" "+Globals.getTimeZone());
		                cell.setCellStyle(styleValue);
		                
	                } catch(Exception ex) {
	                	//ex.printStackTrace();
	                } 
    	 	    	
    	 	    	
    	 	    }
    	 	 }    	
        	
        	
/*            Iterator iter1 = paramsList.iterator();
            s1 = 0; s2 = (short)10;
            if(iter1.hasNext()) {
            	row = sheet.createRow((short) ++rowNum);
            	cellNum = 0;
            	cell = row.createCell((short) cellNum);
            	sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
            	cell.setCellValue(strip.stripHtml(customizedParamInfo));
            }    
*/
/*             rowNum += 2;
             row = sheet.createRow(rowNum);*/    	   	 
    	   	 } // if
        Iterator iterCheck = paramsList.iterator();
        if(iterCheck.hasNext()) {
            rowNum += 2;
            row = sheet.createRow(rowNum);
        }
        header.setCenter(HSSFHeader.font("Tahoma", "")+ HSSFHeader.fontSize((short) 9)+"  " + strBuf.toString());
        HSSFFooter footer = sheet.getFooter();
        footer.setLeft(HSSFFooter.font("Tahoma", "")+ HSSFFooter.fontSize((short) 9)+ "Page " + HSSFFooter.page() 
        		+ " of " + HSSFFooter.numPages() );
        footer.setCenter(HSSFFooter.font("Tahoma", "")+ HSSFFooter.fontSize((short) 9)+Globals.getFooterFirstLine()+"\n"+Globals.getFooterSecondLine());
        
    }



    private int paintExcelData(HSSFWorkbook wb, int rowNum, int col, ReportData rd,
			HashMap styles, ReportRuntime rr, HSSFSheet sheet, String sql_whole, OutputStream sos, HttpServletRequest request) throws RaptorException {
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	int returnValue = 0;
		// HSSFSheet sheet = wb.getSheetAt(0);
    	HSSFCellStyle styleDefault = wb.createCellStyle();
    	HSSFCellStyle styleNumber = wb.createCellStyle();
        HSSFCellStyle styleDecimalNumber = wb.createCellStyle();
    	HSSFCellStyle styleCurrencyNumber = wb.createCellStyle();
        HSSFCellStyle styleCurrencyDecimalNumber = wb.createCellStyle();
    	HSSFCellStyle styleDate = wb.createCellStyle();
        HtmlStripper strip = new HtmlStripper();
		//HSSFSheet sheet = wb.getSheet(getSheetName());
		HSSFCellStyle styleDataHeader = wb.createCellStyle();
		// style.setFillBackgroundColor(HSSFColor.AQUA.index);
		styleDataHeader.setFillForegroundColor(HSSFColor.GREY_40_PERCENT.index);
		styleDataHeader.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleDataHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleDataHeader.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleDataHeader.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleDataHeader.setBorderRight(HSSFCellStyle.BORDER_THIN);
		styleDataHeader.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		HSSFFont font = wb.createFont();
		font.setFontHeight((short) (font_size / 0.05));
		font.setFontName("Tahoma");
		font.setColor(HSSFColor.BLACK.index);
		styleDataHeader.setFont(font);
		// Column Header
		boolean firstPass = true;
		ArrayList cellWidth = new ArrayList();
		java.util.HashMap dataTypeMap = new java.util.HashMap();
		int cellNum = 0;
		rowNum += 0;
		ColumnHeaderRow chr = null;
		String title = "";
		
//        System.out.println("***************** Size " + rd.reportColumnHeaderRows.size());
//        for (int i = 0; i < rd.reportColumnHeaderRows.size(); i++) {
//            for (int j = 0; j < rd.reportColumnHeaderRows.getColumnHeaderRow(i).size(); j++) {
//                System.out.println("Column Title " + rd.reportColumnHeaderRows.getColumnHeaderRow(i).getColumnHeader(j).getColumnTitle()
//                		+ " " + rd.reportColumnHeaderRows.getColumnHeaderRow(i).getColumnHeader(j).isVisible());
//            }
//        }
/*        List dsList = rr.getDataSourceList().getDataSource();
		HashMap dataColumnTypeHashMap = new HashMap();
        for (Iterator iter = dsList.iterator(); iter.hasNext();) {
        	DataSourceType element = (DataSourceType) iter.next();
            List dcList = element.getDataColumnList().getDataColumn();
            for (Iterator iterator = dcList.iterator(); iterator.hasNext();) {
                DataColumnType element1 = (DataColumnType) iterator.next();
                dataTypeMap.put(element1.getColId(), element1.getColType());
                dataColumnTypeHashMap.put(element1.getColName(), element1);
            }
        }		
*/    	
		int columnRows = rr.getVisibleColumnCount() - 1;
        
		HttpSession session = request.getSession();
		String drilldown_index = (String) session.getAttribute("drilldown_index");
		int index = 0;
		try {
		 index = Integer.parseInt(drilldown_index);
		} catch (NumberFormatException ex) {
			index = 0;
		}
		String header = (String) session.getAttribute("TITLE_"+index);
		String subtitle = (String) session.getAttribute("SUBTITLE_"+index);
		if(nvl(header).length()>0) {
			header = Utils.replaceInString(header, "<BR/>", " ");
			header = Utils.replaceInString(header, "<br/>", " ");
			header = Utils.replaceInString(header, "<br>", " ");
			header  = strip.stripHtml(nvl(header).trim());
			subtitle = Utils.replaceInString(subtitle, "<BR/>", " ");
			subtitle = Utils.replaceInString(subtitle, "<br/>", " ");
			subtitle = Utils.replaceInString(subtitle, "<br>", " ");
			subtitle  = strip.stripHtml(nvl(subtitle).trim());
			HSSFRow row = sheet.createRow(rowNum);
			cellNum = 0;
			row.createCell((short) cellNum).setCellValue(header);
			sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (columnRows)));
			rowNum += 1;
			row = sheet.createRow(rowNum);
			cellNum = 0;
			row.createCell((short) cellNum).setCellValue(subtitle);
			sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (columnRows)));
			rowNum += 1;
		}
		
		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
			HSSFRow row = sheet.createRow(rowNum);
			cellNum = -1;
			/*if(rd.reportTotalRowHeaderCols!=null) { 
				cellNum +=1;
				row.createCell((short) cellNum).setCellValue("Total");
				row.createCell((short) cellNum).setCellStyle(styleDataHeader);
				//row.getCell((short) cellNum).setCellStyle(styleDataHeader);
			}*/
			chr = rd.reportColumnHeaderRows.getNext(); 
			
			if(nvl(sql_whole).length() <= 0 || (!rr.getReportType().equals(AppConstants.RT_LINEAR))) {
				if(rr.getReportType().equals(AppConstants.RT_CROSSTAB))
					rd.reportRowHeaderCols.resetNext(0);
				else
					rd.reportRowHeaderCols.resetNext(1);
	    		
				for (; rd.reportRowHeaderCols.hasNext();) {
	                cellNum += 1;
					RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
				    
					 if (firstPass) {
							title = rhc.getColumnTitle();
							title = Utils.replaceInString(title,"_nl_", " \n");
					row.createCell((short) cellNum).setCellValue(title);
					//commented after bug reported by EPAT 01/17/2015
					//sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum+columnRows, (short) (cellNum)));
	                //System.out.println(" **************** Row Header Title " + rhc.getColumnTitle() + " " + cellNum + " " );
	                //System.out.println(cellNum  + " " + cellWidth.size());
					if (cellWidth.size() > 0 && cellWidth.size() > cellNum) {
						if (((Integer) cellWidth.get(cellNum)).intValue() < rhc
								.getColumnTitle().length())
							cellWidth.set(cellNum, new Integer(title.length()));
					} else
						cellWidth.add(cellNum, new Integer(title.length()));
					 row.getCell((short) cellNum).setCellStyle(styleDataHeader);
					}
					
	                
				} // for

		 }
			
		firstPass = false;
		
/*		for(chr.resetNext(); chr.hasNext(); ) {
			ColumnHeader ch = chr.getNext();
			if(ch.isVisible()) { 
				cellNum += 1;
				row.createCell((short) cellNum).setCellValue(ch.getColumnTitle());
//		<td align="center"<%= ch.getColumnWidthHtml() %><%= ch.getColSpanHtml() %><%= ch.getRowSpanHtml() %>>
//			<b class=rtableheader><%= ch.getColumnTitleHtml() %></b>
//		</td>
			}	// if
		}	// for
*/		
         
            //cellNum = -1;
            

//            Set mapSet = dataTypeMap.entrySet();
//            Map.Entry me;
//            String element, value ;
//            for (Iterator iter = mapSet.iterator(); iter.hasNext();) {
//                me=(Map.Entry)iter.next();
//                element = (String) me.getKey();
//                  value = (String) me.getValue();
//                  System.out.println("DataTypeMap " + element + " " + value);
//            }
    		
			for (chr.resetNext(); chr.hasNext();) {
				ColumnHeader ch = chr.getNext();
				if(ch.isVisible()) {
                                    cellNum += 1;
				
				int colSpan = ch.getColSpan()-1;
				title = ch.getColumnTitle();
				title = Utils.replaceInString(title,"_nl_", " \n");
				row.createCell((short) cellNum).setCellValue(title);
				if(colSpan > 0) {
					for ( int k = 1; k <= colSpan; k++ ) {
						row.createCell((short) cellNum+k);
					}
					sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (cellNum+colSpan)));
				}
				
                
                
/*                if (cellWidth.size() > cellNum) {
					if (((Integer) cellWidth.get(cellNum)).intValue() < ch
							.getColumnTitle().length())
						cellWidth
								.set((cellNum), new Integer(ch.getColumnTitle().length()));
				} else
					cellWidth.add((cellNum), new Integer(ch.getColumnTitle().length()));
*/               	row.getCell((short) (cellNum)).setCellStyle(styleDataHeader);
					for ( int k = 1; k <= colSpan; k++ ) {
						row.getCell((short) (cellNum+k)).setCellStyle(styleDataHeader);
					}

               	if(colSpan > 0)                 
                	cellNum += colSpan;
				}
			} // for

/*			int cw = 0;
			for (int i = 0; i < cellWidth.size(); i++) {
				cw = ((Integer) cellWidth.get(i)).intValue() + 6;
				sheet.setColumnWidth((short) (i), (short) ((cw * 8) / ((double) 1 / 20)));
			}
*/
			rowNum += 1;
		} // for
		

		// Data
		// Create some cell styles.
		//HSSFCellStyle styleDefault = wb.createCellStyle();
    	HSSFCellStyle styleCell = null;

		HSSFCellStyle styleTotal = wb.createCellStyle();
        HSSFCellStyle styleCurrencyTotal = wb.createCellStyle();
        HSSFCellStyle styleDefaultTotal = wb.createCellStyle();
        HSSFCellStyle styleCurrencyDecimalNumberTotal = wb.createCellStyle();
        HSSFCellStyle styleDecimalNumberTotal = wb.createCellStyle();
        HSSFCellStyle styleCurrencyNumberTotal = wb.createCellStyle();
        

		// Create some fonts.
		HSSFFont fontDefault = wb.createFont();
		HSSFFont fontBold = wb.createFont();
		// Initialize the styles & fonts.
		// The default will be plain .
		fontDefault.setColor((short) HSSFFont.COLOR_NORMAL);
		fontDefault.setFontHeight((short) (font_size / 0.05));
		fontDefault.setFontName("Tahoma");

		// The default will be bold black tachoma 10pt text.
		fontBold.setColor((short) HSSFFont.COLOR_NORMAL);
		fontBold.setFontHeight((short) (font_size / 0.05));
		fontBold.setFontName("Tahoma");
		fontBold.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		// Style default will be normal with no background
		styleDefault.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleDefault.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleDefault.setFillPattern(HSSFCellStyle.NO_FILL);
		styleDefault.setFont(fontDefault);
		styleDefault.setWrapText(true);
		//Number
		styleNumber.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleNumber.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleNumber.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleNumber.setFillPattern(HSSFCellStyle.NO_FILL);
		styleNumber.setFont(fontDefault);
		try {
			styleNumber.setDataFormat((short)0x26);//HSSFDataFormat.getBuiltinFormat("(#,##0_);[Red](#,##0)"));
		} catch (Exception e) {
			
		}
		//Decimal Number
        styleDecimalNumber.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleDecimalNumber.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumber.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumber.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumber.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleDecimalNumber.setFillPattern(HSSFCellStyle.NO_FILL);
        styleDecimalNumber.setFont(fontDefault);
        styleDecimalNumber.setDataFormat((short)0x27);//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));

		//Decimal Number
        styleDecimalNumberTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleDecimalNumberTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumberTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumberTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleDecimalNumberTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleDecimalNumberTotal.setFillPattern(HSSFCellStyle.NO_FILL);
        styleDecimalNumberTotal.setFont(fontBold);
        styleDecimalNumberTotal.setDataFormat((short)0x27);//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
        
        //CurrencyNumber
		styleCurrencyDecimalNumber.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleCurrencyDecimalNumber.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumber.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumber.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumber.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleCurrencyDecimalNumber.setFillPattern(HSSFCellStyle.NO_FILL);
		styleCurrencyDecimalNumber.setFont(fontDefault);
		styleCurrencyDecimalNumber.setDataFormat((short)8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
		
		//currency number bold
		styleCurrencyDecimalNumberTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleCurrencyDecimalNumberTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumberTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumberTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleCurrencyDecimalNumberTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleCurrencyDecimalNumberTotal.setFillPattern(HSSFCellStyle.NO_FILL);
		styleCurrencyDecimalNumberTotal.setFont(fontBold);
		styleCurrencyDecimalNumberTotal.setDataFormat((short)8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));


        //CurrencyNumber
        styleCurrencyNumber.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleCurrencyNumber.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumber.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumber.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumber.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleCurrencyNumber.setFillPattern(HSSFCellStyle.NO_FILL);
        styleCurrencyNumber.setFont(fontDefault);
        styleCurrencyNumber.setDataFormat((short) 6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
        

        //CurrencyNumber
        styleCurrencyNumberTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleCurrencyNumberTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumberTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumberTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleCurrencyNumberTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleCurrencyNumberTotal.setFillPattern(HSSFCellStyle.NO_FILL);
        styleCurrencyNumberTotal.setFont(fontBold);
        styleCurrencyNumberTotal.setDataFormat((short) 6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));

        //Date
		styleDate.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleDate.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleDate.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleDate.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleDate.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleDate.setFillPattern(HSSFCellStyle.NO_FILL);
		styleDate.setFont(fontDefault);
		styleDate.setDataFormat((short)0xe);//HSSFDataFormat.getBuiltinFormat("m/d/yy"));
		
		// Style for Total will be Bold with normal font with no background
		styleTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		styleTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		styleTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
		styleTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		styleTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleTotal.setFillPattern(HSSFCellStyle.NO_FILL);
        styleTotal.setDataFormat((short)0x28);//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
		styleTotal.setFont(fontBold);

        styleCurrencyTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleCurrencyTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleCurrencyTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleCurrencyTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleCurrencyTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleCurrencyTotal.setFillPattern(HSSFCellStyle.NO_FILL);
        styleCurrencyTotal.setDataFormat((short)8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
        styleCurrencyTotal.setFont(fontBold);        
		
        styleDefaultTotal.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleDefaultTotal.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleDefaultTotal.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleDefaultTotal.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleDefaultTotal.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleDefaultTotal.setFillPattern(HSSFCellStyle.NO_FILL);
        styleDefaultTotal.setDataFormat((short)0x28);
        ////styleDefaultTotal.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
        styleDefaultTotal.setFont(fontBold);     
        
		firstPass = true;
		// Declare a row object reference.
		HSSFRow row = null;
		// Declare a cell object reference.
		HSSFCell cell = null;
		//HSSFCell cellNumber = null;
		//HSSFCell cellCurrencyNumber = null;
		//HSSFCell cellDate = null;
        
		//All the possible combinations of date format
        SimpleDateFormat MMDDYYYYFormat   = new SimpleDateFormat("MM/dd/yyyy"); 
        SimpleDateFormat YYYYMMDDFormat   = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat MONYYYYFormat    = new SimpleDateFormat("MMM yyyy");
        SimpleDateFormat MMYYYYFormat     = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat MMMMMDDYYYYFormat = new SimpleDateFormat("MMMMM dd, yyyy");
        SimpleDateFormat YYYYMMDDDASHFormat   = new SimpleDateFormat("yyyy-MM-dd"); 
        SimpleDateFormat timestampFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        SimpleDateFormat DDMONYYYYFormat    = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat MONTHYYYYFormat    = new SimpleDateFormat("MMMMM, yyyy");
        SimpleDateFormat MMDDYYYYHHMMSSFormat   = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat MMDDYYYYHHMMFormat   = new SimpleDateFormat("MM/dd/yyyy HH:mm");        
        SimpleDateFormat YYYYMMDDHHMMSSFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat YYYYMMDDHHMMFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat DDMONYYYYHHMMSSFormat    = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        SimpleDateFormat DDMONYYYYHHMMFormat    = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        SimpleDateFormat DDMONYYHHMMFormat    = new SimpleDateFormat("dd-MMM-yy HH:mm");
        SimpleDateFormat MMDDYYFormat   = new SimpleDateFormat("MM/dd/yy");        
        SimpleDateFormat MMDDYYHHMMFormat    = new SimpleDateFormat("MM/dd/yy HH:mm");
        SimpleDateFormat MMDDYYHHMMSSFormat    = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        SimpleDateFormat MMDDYYYYHHMMZFormat    = new SimpleDateFormat("MM/dd/yyyy HH:mm z");
        SimpleDateFormat MMMMMDDYYYYHHMMSS    = new SimpleDateFormat("MMMMM-dd-yyyy HH:mm:ss");
        
        
        
		
		ResultSet rs = null;
        Connection conn = null;
        Statement st = null;
        ResultSetMetaData rsmd = null;
        CreationHelper createHelper = wb.getCreationHelper();

    	if(nvl(sql_whole).length() >0 && rr.getReportType().equals(AppConstants.RT_LINEAR)) {
        try {
	        	conn = ConnectionUtils.getConnection(rr.getDbInfo());
	        	st = conn.createStatement();
	        	System.out.println("************* Map Whole SQL *************");
	        	System.out.println(sql_whole);
	        	System.out.println("*****************************************");
	        	rs = st.executeQuery(sql_whole);
	        	rsmd = rs.getMetaData();
	            int numberOfColumns = rsmd.getColumnCount();
	            HashMap colHash = new HashMap();
	            DataRow dr = null;
	            int j = 0;
	            int rowCount = 0;
	       		while(rs.next()) {
	       			rowCount++;
	       			row = sheet.createRow(rowNum);
	       			cellNum = -1;
	    			colHash = new HashMap();
	    			for (int i = 1; i <= numberOfColumns; i++) {
	    				colHash.put(rsmd.getColumnLabel(i).toUpperCase(), strip.stripHtml(rs.getString(i)));
	    			}
	    			rd.reportDataRows.resetNext();
	    			dr = rd.reportDataRows.getNext();
	    			j = 0;
	    			//if(rowCount%1000 == 0) wb.write(sos);
	    			
	    			/*if(rd.reportTotalRowHeaderCols!=null) {
	    				//cellNum = -1;
	    				//for (rd.reportRowHeaderCols.resetNext(); rd.reportRowHeaderCols.hasNext();) {
	    	                cellNum += 1;
	    					//RowHeaderCol rhc = rd.reportRowHeaderCols.getRowHeaderCol(0);
	    					//if (firstPass)
	    					//	rhc.resetNext();
	    					//RowHeader rh = rhc.getRowHeader(rowCount-1);
	    					row.createCell((short) cellNum).setCellValue(rowCount);
	    					row.getCell((short) cellNum).setCellStyle(styleDefault);
	    					if (firstPass)
	    						cellWidth.add(cellNum, new Integer((rowCount+"").length()));
	    					else
	    						cellWidth.set(cellNum, new Integer((rowCount+"").length()));
	
	    				//} // for
	    			}*/
    				firstPass = false;
    				//cellNum = -1;
	    			for (dr.resetNext(); dr.hasNext();j++) {
	    			//for (chr.resetNext(); chr.hasNext();) {
	    				//ColumnHeader ch = chr.getNext();
	    				styleCell = null;
	    				DataValue dv = dr.getNext();
	    				HtmlFormatter htmlFormat = dv.getCellFormatter();
    					if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) 	    				
    						styleCell = (HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default"));
    					if (htmlFormat != null && dv.getFormatId() != null && styles != null) 
    						styleCell = (HSSFCellStyle) styles.get(nvl(dv.getFormatId(),"default"));
	    				String value = nvl((String)colHash.get(dv.getColId().toUpperCase()));
	                    
	    				boolean bold = false;
	    				
	    				if(dv.isVisible()) {
	    					cellNum += 1;
	    					cell = row.createCell((short) cellNum);
	    	                //System.out.println("Stripping HTML 1");
	    					//cell.setCellValue(strip.stripHtml(dv.getDisplayValue()));
	    					String dataType = (String) (dataTypeMap.get(dv.getColId()));
	    					//System.out.println("Value " + value + " " + (( dataType !=null && dataType.equals("DATE")) || (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date"))) ); 
	    					if (dataType!=null && dataType.equals("NUMBER")){ 
	    						//cellNumber = row.createCell((short) cellNum);
	    						//cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    						//cellNumber.setCellValue(dv.getDisplayValue());
	    						//cellCurrencyNumber = row.createCell((short) cellNum);
	    						int zInt = 0;
	    						if (value.equals("null")){
	    							cell.setCellValue(zInt);
	    						}else{
	    							
	    							if ((value.indexOf("."))!= -1){
	    	                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    									
	    								//if (dv.getDisplayValue().startsWith("$")){
	    									//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    									String tempDollar = dv.getDisplayValue().trim();
	    									tempDollar = tempDollar.replaceAll(" ", "").substring(0);
	    	                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
	    									//System.out.println("SUBSTRING |" + tempDollar);
	    									//System.out.println("Before copy Value |" + tempDollar);
	    									//tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
	    									//System.out.println("After copy Value |" + tempDollar);
	    									if ((tempDollar.indexOf(","))!= -1){
	    										tempDollar = tempDollar.replaceAll(",", "");
	    									}
	    									//System.out.println("The final string 1 is "+tempDollar);
	    	                                double tempDoubleDollar = 0.0;
	    	                                try {
	    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short) 8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleCurrencyDecimalNumber);
	    	                                    cell.setCellValue(tempDoubleDollar);
	    	                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
	    	                                    //cell.setCellStyle(styleDefault);
	    	                                    cell.setCellValue(tempDollar);
	    	                                }                                
	    								}else{
	    									//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                double tempDouble = 0.0;
	    	                                try {
	    	                                  tempDouble = Double.parseDouble(value);
		   	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short)0x28);//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDecimalNumber);
	    	                                  cell.setCellValue(tempDouble);
	    	                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
	    	                                    cell.setCellValue(value);
	    	                                }
	    	
	    								}
	    							}else {
	    								if (!(value.equals(""))){
	    	                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    									//if (dv.getDisplayValue().startsWith("$")){
	    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    										String tempInt = value.trim();
	    										tempInt = tempInt.replaceAll(" ", "").substring(0);
	    	                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
	    										//System.out.println("SUBSTRING |" + tempInt);
	    										//System.out.println("Before copy Value |" + tempInt);
	    										//tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
	    										//System.out.println("After copy Value |" + tempInt);
	    										if ((tempInt.indexOf(","))!= -1){
	    											tempInt = tempInt.replaceAll(",", "");
	    										}
	    										//System.out.println("The final string INT is "+tempInt);
	    	                                    Long tempIntDollar = 0L;
	    	                                    try {
	    	                                        tempIntDollar = Long.parseLong(tempInt);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleCurrencyNumber);
	    	                                        cell.setCellValue(tempIntDollar);
	    	                                     } catch (NumberFormatException ne) {
	 	    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
	    	                                         cell.setCellValue(tempInt);
	    	                                     }									
	    									}else{
	    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    										String tempStr = value.trim();
	    	                                    if ((tempStr.indexOf(","))!= -1){
	    	                                        tempStr = tempStr.replaceAll(",", "");
	    	                                    }
	    	                                    Long temp = 0L;
	    	                                    
	    	                                    try {
	    	                                       temp = Long.parseLong(tempStr);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 0x26);//HSSFDataFormat.getBuiltinFormat("(#,##0_);[Red](#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleNumber);
	    	                                       cell.setCellValue(temp);
	    	                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
	    	                                        cell.setCellValue(tempStr);
	    	                                    }
	    	                                      
	    	                                    
	    									}
	    									//int temp = Integer.parseInt(value.trim());
	    									//	cell.setCellValue(temp);
	    									//}else{
	    									//	cell.setCellValue(strip.stripHtml(value));
	    									//}
	    							}
	    						}
	    						}
	    						
	    					}else if (  ( dataType !=null && dataType.equals("DATE")) || (dv.getDisplayName()!=null && dv.getDisplayName().toLowerCase().endsWith("date")) ||
	    							(dv.getColId()!=null && dv.getColId().toLowerCase().endsWith("date")) ||
	    							 (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date")) ) {
	    						//cellDate = row.createCell((short) cellNum);
	    						//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm/dd/yy"));
	    						
                                if(styleCell!=null) {
                                	styleCell.setDataFormat((short) 0xe);//HSSFDataFormat.getBuiltinFormat("m/d/yy"));
                                	cell.setCellStyle(styleCell);
                                } else
                                	cell.setCellStyle(styleDate);
	    						//String MY_DATE_FORMAT = "yyyy-MM-dd";
                               	//value = nvl(value).length()<=0?nvl(dv.getDisplayValue()):value;
	    	                    Date date = null;
	    	                    int flag = 0;
	    	                    date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                    date = MMDDYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = timestampFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy-m-d h:mm:ss")); //yyyy-MM-dd HH:mm:ss
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMMMMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm/d/yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONTHYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm/yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDDASHFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy-m-d"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMZFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMMMMDDYYYYHHMMSS.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    	
	    	                    if(date!=null) {
      		                      //System.out.println("ExcelDate " + HSSFDateUtil.getExcelDate(date));
	    	                      cell.setCellValue(HSSFDateUtil.getExcelDate(date));
      		                      try {
      		                    	  String str = cell.getStringCellValue();
      		                      } catch (IllegalStateException ex) { /*cell.getCellStyle().setDataFormat((short)0);*/cell.setCellValue(value);}
	    	                    } else {
      		                      /*cell.getCellStyle().setDataFormat((short)0);*/	
	    	                      cell.setCellValue(value);
	    	                    }
	    						//cellDate.setCellValue(date);
      							//cellDate.setCellValue(value);	    						//cellDate.setCellValue(date);
	    						//cellDate.setCellValue(dv.getDisplayValue());
	    	                    
	    					}else if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	    	                    //cellNumber = row.createCell((short) cellNum);
	    	                    //cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    	                    //cellNumber.setCellValue(dv.getDisplayValue());
	    	                    cell = row.createCell((short) cellNum);
	    	                    int zInt = 0;
	    	                    if (value.equals("null")){
	    	                        cell.setCellValue(zInt);
	    	                    }else{
	    	                        
	    	                        if ((value.indexOf("."))!= -1){
	    	                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                                
	    	                            //if (value.startsWith("$")){
	    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    	                                String tempDollar = value.trim();
	    	                                tempDollar = tempDollar.replaceAll(" ", "").substring(0);
	    	                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
	    	                                //System.out.println("SUBSTRING |" + tempDollar);
	    	                                //System.out.println("Before copy Value |" + tempDollar);
	    	                                //tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
	    	                                //System.out.println("After copy Value |" + tempDollar);
	    	                                if ((tempDollar.indexOf(","))!= -1){
	    	                                    tempDollar = tempDollar.replaceAll(",", "");
	    	                                }
	    	                                //System.out.println("The final string 2IF is "+tempDollar);
	    	                                double tempDoubleDollar = 0.0;
	    	                                try {
	    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short) 8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleCurrencyDecimalNumber);	    	                                    
	    	                                    cell.setCellValue(tempDoubleDollar);
	    	                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
	    	                                    cell.setCellValue(tempDollar);
	    	                                }                                
	    	                                
	    	
	    	                            }else{
	    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                String tempDoubleStr = value.trim();
	    	                                tempDoubleStr = tempDoubleStr.replaceAll(" ", "").substring(0);
	    	                                if ((tempDoubleStr.indexOf(","))!= -1){
	    	                                    tempDoubleStr = tempDoubleStr.replaceAll(",", "");
	    	                                }
	    	                                double tempDouble = 0.0;
	    	                                try {
	    	                                  tempDouble = Double.parseDouble(tempDoubleStr);
		   	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short)0x28 );//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDecimalNumber);
		   	                                    cell.setCellValue(tempDouble);
	    	                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
	    	                                    cell.setCellValue(tempDoubleStr);
	    	                                }
	    	                            }
	    	                                
	    	                        }else {
	    	                            if (!(value.equals(""))){
	    	                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                                //if (value.startsWith("$")){
	    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    	                                    String tempInt = value.trim();
	    	                                    tempInt = tempInt.replaceAll(" ", "").substring(0);
	    	                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
	    	                                    //System.out.println("SUBSTRING |" + tempInt);
	    	                                    //System.out.println("Before copy Value |" + tempInt);
	    	                                    //tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
	    	                                    //System.out.println("After copy Value |" + tempInt);
	    	                                    if ((tempInt.indexOf(","))!= -1){
	    	                                        tempInt = tempInt.replaceAll(",", "");
	    	                                    }
	    	                                    //System.out.println("The final string INT 2 is "+tempInt);
	    	                                    
	    	                                    Long tempIntDollar = 0L;
	    	                                    
	    	                                    try {
	    	                                        tempIntDollar = Long.parseLong(tempInt);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleCurrencyNumber);
	    	                                        cell.setCellValue(tempIntDollar);
	    	                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
	    	                                        cell.setCellValue(tempInt);
	    	                                    }                                    
	    	                                }else{
	    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                    String tempStr = value.trim();
	    	                                    if ((tempStr.indexOf(","))!= -1){
	    	                                        tempStr = tempStr.replaceAll(",", "");
	    	                                    }
	    	                                    Long temp = 0L;
	    	                                    
	    	                                    try {
	    	                                       temp = Long.parseLong(tempStr);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 0x26);//HSSFDataFormat.getBuiltinFormat("(#,##0_);[Red](#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleNumber);
	    	                                       cell.setCellValue(temp);
	    	                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
	    	                                        cell.setCellValue(tempStr);
	    	                                    }
	    	                                }
	    	                                //int temp = Integer.parseInt(dv.getDisplayValue().trim());
	    	                                //  cell.setCellValue(temp);
	    	                                //}else{
	    	                                //  cell.setCellValue(strip.stripHtml(dv.getDisplayValue()));
	    	                                //}
	    	                        } else {
	                                    if(styleCell!=null) {
	                                    	styleCell.setWrapText(true);
	                                    	cell.setCellStyle(styleCell);
	                                    } else	    	                                    	
	                                    	cell.setCellStyle(styleDefault);
	    	                        }
	    	                    }
	    	                    }
	    	                    
	    	                     
	    	                 }
	    	                else { 
	    						//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
                                if(styleCell!=null) {
                                	styleCell.setWrapText(true);
                                	cell.setCellStyle(styleCell);
                                } else	    	                                    	
                                	cell.setCellStyle(styleDefault);
    		 				    cell.setCellValue(strip.stripHtml(value));
	    					}
	    	
	    					//if (!(value.equals(""))){
	    					//int temp = Integer.parseInt(value.trim());
	    					//cell.setCellValue(temp);
	    					//}else{
	    					//	cell.setCellValue(strip.stripHtml(value));
	    					//}
	    	                //HSSFCellStyle styleFormat = null;
	    	                //HSSFCellStyle numberStyle = null;
	    	                //HSSFFont formatFont = null;
	    	                //short fgcolor = 0;
	    	                //short fillpattern = 0;
	    					if (cellWidth.size() > cellNum) {
	    						if (((Integer) cellWidth.get(cellNum)).intValue() < dv
	    								.getDisplayValue().length())
	    							cellWidth.set((cellNum),
	    									(value.length()<=Globals.getMaxCellWidthInExcel())?new Integer(value.length()):new Integer(Globals.getMaxCellWidthInExcel()));
	    					} else
	    						cellWidth.add((cellNum), (value.length()<=Globals.getMaxCellWidthInExcel())?new Integer(value.length()):new Integer(Globals.getMaxCellWidthInExcel()));
	    	                //System.out.println("1IF "+ (dv.isBold()) + " "+ value + " " + dv.getDisplayTotal() + " " + dv.getColName() );
	    					if (dv.isBold()) {
	    	                    if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	    	                        if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                            cell.setCellStyle(styleCurrencyTotal);
	    	                        }
	    	                        else {
	    	                            cell.setCellStyle(styleTotal);
	    	                        }
	    	                    } else {
	    	                        cell.setCellStyle(styleDefaultTotal);
	    	                    }
	    						bold = true;
	    					}
	    	                //System.out.println("2IF "+ (dr.isRowFormat()) + " " +  (dv.isCellFormat()) + " " + (styles!=null));
	    					if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) {
	    						  //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default")));
	    						continue;
	    					}
	    	                //System.out.println("3IF "+ (htmlFormat != null) + " " +  (dv.getFormatId() != null) + " " + (bold == false) + " "+ (styles != null));
	    					if (htmlFormat != null && dv.getFormatId() != null && bold == false
	    							&& styles != null) {
	    	                      //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dv.getFormatId(),"default")));
	    					} //else if (bold == false)
	    						//cell.setCellStyle(styleDefault);    					
	    				} // dv.isVisible
	    			}
	    			rowNum += 1;

	       		}
	       		
    			int cw = 0;
    			for (int i = 0; i < cellWidth.size(); i++) {
    				cw = ((Integer) cellWidth.get(i)).intValue() + 12;
    				// if(i!=cellWidth.size()-1)
    				sheet.setColumnWidth((short) (i), (short) ((cw * 8) / ((double) 1 / 20)));
    				// else
    				// sheet.setColumnWidth((short) (i + 1), (short) ((cw * 10) /
    				// ((double) 1 / 20)));
    			}
    			
	       		// To Display Total Values for Linear report
	       		if(rd.reportDataTotalRow!=null) {
		       		row = sheet.createRow(rowNum);
		       		cellNum = -1;
		       		rd.reportTotalRowHeaderCols.resetNext();
		       		//for (rd.reportTotalRowHeaderCols.resetNext(); rd.reportTotalRowHeaderCols.hasNext();) {
		                cellNum += 1;
						RowHeaderCol rhc = rd.reportTotalRowHeaderCols.getNext();
						RowHeader rh = rhc.getRowHeader(0);
						row.createCell((short) cellNum).setCellValue(strip.stripHtml(rh.getRowTitle()));
						row.getCell((short) cellNum).setCellStyle(styleDefaultTotal);
		       		//}
	       			
					rd.reportDataTotalRow.resetNext();
	       			DataRow drTotal = rd.reportDataTotalRow.getNext();
       				//cellNum = -1;
	       			
	       			drTotal.resetNext();
	       			drTotal.getNext();
	       			for (; drTotal.hasNext();) {
	       				cellNum += 1;
    					cell = row.createCell((short) cellNum);
	       				DataValue dv = drTotal.getNext();
	       				String value = dv.getDisplayValue();
	       				cell.setCellValue(value);
	       				boolean bold = false;
	       				if (dv.isBold()) {
	       					if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	       						if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	       							cell.setCellStyle(styleCurrencyTotal);
	       						} else {
	       							cell.setCellStyle(styleTotal);
	       						}
	       					} else {
	       						cell.setCellStyle(styleDefaultTotal);
	       					}
	       					bold = true;
	       				}
	       			}
	       		}
				
		    } catch (SQLException ex) { 
		    	ex.printStackTrace();
		    	throw new RaptorException(ex);
		    } catch (ReportSQLException ex) { 
		    	throw new RaptorException(ex);
		    } catch (Exception ex) {
		    	if(!(ex.getCause() instanceof java.net.SocketException) )
		    		throw new RaptorException (ex);
		    } finally {
	        	try {
	        		if(conn!=null)
	        			conn.close();
	        		if(st!=null)
	        			st.close();
	        		if(rs!=null)
	        			rs.close();
	        	} catch (SQLException ex) {
	        		throw new RaptorException(ex);
	        	}
	        }
        
		/*if(Globals.getShowDisclaimer() && !Globals.disclaimerPositionedTopInCSVExcel()) {
			rowNum += 1;
			row = sheet.createRow(rowNum);
			cellNum = 0;
			String disclaimer = Globals.getFooterFirstLine() + " " + Globals.getFooterSecondLine();
			row.createCell((short) cellNum).setCellValue(disclaimer);
			sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (columnRows)));
			rowNum += 1;
		}*/        
    	  } else {
    		  if(rr.getReportType().equals(AppConstants.RT_LINEAR)) {
    		    int rowCount = 0;
    		    for (rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext();) {
    		    DataRow dr = rd.reportDataRows.getNext();
    		    //List l = rd.getReportDataList();
    			//for (int dataRow = 0; dataRow < l.size(); dataRow++) {
    				rowCount++;
    				
    				
    				//DataRow dr = (DataRow) l.get(dataRow);
    				row = sheet.createRow(rowNum);

    				cellNum = -1;
    				
    				if (rr.getReportType().equals(AppConstants.RT_LINEAR) && rd.reportTotalRowHeaderCols!=null) {
    					rd.reportRowHeaderCols.resetNext(0);
    	    			if(rd.reportTotalRowHeaderCols!=null) {
    	    				//cellNum = -1;
    	    				//for (rd.reportRowHeaderCols.resetNext(); rd.reportRowHeaderCols.hasNext();) {
    	    	                //cellNum += 1;
    	    					//RowHeaderCol rhc = rd.reportRowHeaderCols.getRowHeaderCol(0);
    	    					//if (firstPass)
    	    					//	rhc.resetNext();
    	    					//RowHeader rh = rhc.getRowHeader(rowCount-1);
    	    					//row.createCell((short) cellNum).setCellValue(rowCount);
    	    					//row.getCell((short) cellNum).setCellStyle(styleDefault);
    	    					//if (firstPass)
    	    						//cellWidth.add(cellNum, new Integer((rowCount+"").length()));
    	    					//else
    	    						//cellWidth.set(cellNum, new Integer((rowCount+"").length()));
    	
    	    				//} // for
    	    			}
    					
    				} else {
    					rd.reportRowHeaderCols.resetNext(0);
    				}
    				for (; rd.reportRowHeaderCols.hasNext();) {
    	                cellNum += 1;
    					RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
    					if (firstPass)
    						rhc.resetNext();
    					RowHeader rh = rhc.getNext();
    					row.createCell((short) cellNum).setCellValue(strip.stripHtml(rh.getRowTitle()));
    					row.getCell((short) cellNum).setCellStyle(styleDefault);
    					if (cellWidth.size() > 0) {
    						if (((Integer) cellWidth.get(cellNum)).intValue() < rh.getRowTitle()
    								.length())
    							cellWidth.set(cellNum, new Integer(rh.getRowTitle().length()));
    					} else
    						cellWidth.add(cellNum, new Integer(rh.getRowTitle().length()));

    				} // for
    				firstPass = false;
    	            //cellNum = -1; 
    	            int j = 0;
    	            
    				for (dr.resetNext(); dr.hasNext();j++) {
    					DataValue dv = dr.getNext();
    	                styleCell = null;
    					boolean bold = false;
    					String value = nvl(dv.getDisplayValue());
                       	value = strip.stripHtml(value);
    					HtmlFormatter htmlFormat = dv.getCellFormatter();
    					if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) 	    				
    						styleCell = (HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default"));
    					if (htmlFormat != null && dv.getFormatId() != null && styles != null) 
    						styleCell = (HSSFCellStyle) styles.get(nvl(dv.getFormatId(),"default"));
    					
    					if(dv.isVisible()) {
    		                cellNum += 1;
    						cell = row.createCell((short) cellNum);
    		                //System.out.println("Stripping HTML 1");
    						//cell.setCellValue(strip.stripHtml(value));
    						String dataType = (String) (dataTypeMap.get(dv.getColId()));
    						//System.out.println(" The Display Value is ********"+value + " " + dv.getDisplayTotal() + " " + dv.getColName());
    						
    						if (dataType!=null && dataType.equals("NUMBER")){ 
    							//cellNumber = row.createCell((short) cellNum);
    							//cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    							//cellNumber.setCellValue(value);
    							//cellCurrencyNumber = row.createCell((short) cellNum);
    							int zInt = 0;
    							if (value.equals("null")){
    								cell.setCellValue(zInt);
    							}else{
    								
    								if ((value.indexOf("."))!= -1){
    		                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    										
    									//if (value.startsWith("$")){
    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    										String tempDollar = value.trim();
    										tempDollar = tempDollar.replaceAll(" ", "").substring(0);
    		                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
    										//System.out.println("SUBSTRING |" + tempDollar);
    										//System.out.println("Before copy Value |" + tempDollar);
    										//tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
    										//System.out.println("After copy Value |" + tempDollar);
    										if ((tempDollar.indexOf(","))!= -1){
    											tempDollar = tempDollar.replaceAll(",", "");
    										}
    										//System.out.println("The final string 1 is "+tempDollar);
    		                                double tempDoubleDollar = 0.0;
    		                                try {
    		                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short) 8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleCurrencyDecimalNumber);    		                                    
	    	                                    cell.setCellValue(tempDoubleDollar);
    		                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    		                                    cell.setCellValue(tempDollar);
    		                                }                                
    									}else{
    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    		                                double tempDouble = 0.0;
    		                                try {
    		                                  tempDouble = Double.parseDouble(value);
		   	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short) 0x28);//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDecimalNumber);
    		                                  cell.setCellValue(tempDouble);
    		                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    		                                    cell.setCellValue(value);
    		                                }
    		
    									}
    								}else {
    									if (!(value.equals(""))){
    		                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    										//if (value.startsWith("$")){
    											//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    											String tempInt = value.trim();
    											tempInt = tempInt.replaceAll(" ", "").substring(0);
    		                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
    											//System.out.println("SUBSTRING |" + tempInt);
    											//System.out.println("Before copy Value |" + tempInt);
    											//tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
    											//System.out.println("After copy Value |" + tempInt);
    											if ((tempInt.indexOf(","))!= -1){
    												tempInt = tempInt.replaceAll(",", "");
    											}
    											//System.out.println("The final string INT is "+tempInt);
    		                                    Long tempIntDollar = 0L;
    		                                    try {
    		                                        tempIntDollar = Long.parseLong(tempInt);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short)6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleCurrencyNumber);
    		                                        cell.setCellValue(tempIntDollar);
    		                                     } catch (NumberFormatException ne) {
 		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
    		                                         cell.setCellValue(tempInt);
    		                                     }									
    										}else{
    											//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    											String tempStr = value.trim();
    		                                    if ((tempStr.indexOf(","))!= -1){
    		                                        tempStr = tempStr.replaceAll(",", "");
    		                                    }
    		                                    Long temp = 0L;
    		                                    
    		                                    try {
    		                                       temp = Long.parseLong(tempStr);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short)0x26);//HSSFDataFormat.getBuiltinFormat("(#,##0_);[Red](#,##0)"));
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleNumber);
    		                                       cell.setCellValue(temp);
    		                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
    		                                        cell.setCellValue(tempStr);
    		                                    }
    		                                      
    		                                    
    										}
    										//int temp = Integer.parseInt(value.trim());
    										//	cell.setCellValue(temp);
    										//}else{
    										//	cell.setCellValue(strip.stripHtml(value));
    										//}
    								}
    							}
    							}
    							
	    					}else if (  ( dataType !=null && dataType.equals("DATE")) || (dv.getDisplayName()!=null && dv.getDisplayName().toLowerCase().endsWith("date")) ||
	    							(dv.getColId()!=null && dv.getColId().toLowerCase().endsWith("date")) ||
	    							 (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date")) ) {
    							//cellDate = row.createCell((short) cellNum);
    							//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("mm/dd/yy"));
	    						
                                if(styleCell!=null) {
                                	styleCell.setDataFormat((short)0xe); //HSSFDataFormat.getBuiltinFormat("m/d/yy"));
                                	cell.setCellStyle(styleCell);
                                } else
                                	cell.setCellStyle(styleDate);
    							//String MY_DATE_FORMAT = "yyyy-MM-dd";
    		                    Date date = null;
	    	                    int flag = 0;
	    	                    date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
	    	                        date = MMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
		    	                    date = MMDDYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
	    	                        date = YYYYMMDDFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = timestampFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy-m-d h:mm:ss")); //yyyy-MM-dd HH:mm:ss
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm yyyy"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/yyyy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MMMMMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm/d/yyyy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MONTHYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("mmm/yyyy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = YYYYMMDDHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = YYYYMMDDDASHFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy-m-d"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = YYYYMMDDHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("yyyy/m/d h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = DDMONYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = DDMONYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = DDMONYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = DDMONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("d-mmm-yyyy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yy h:mm:ss"));
	   	                        	flag = 1;
	   	                        }
    		                    if(date==null)
	    	                        date = MMDDYYYYHHMMZFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMMMMDDYYYYHHMMSS.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cell.getCellStyle().setDataFormat(
	   	                        	        createHelper.createDataFormat().getFormat("m/d/yyyy h:mm"));
	   	                        	flag = 1;
	   	                        }
    		                    	
    		                    if(date!=null) {
    		                      //System.out.println("ExcelDate " + HSSFDateUtil.getExcelDate(date));
    		                      cell.setCellValue(HSSFDateUtil.getExcelDate(date));
    		                      try {
    		                    	  String str = cell.getStringCellValue();
      		                      } catch (IllegalStateException ex) { /*cell.getCellStyle().setDataFormat((short)0);*/cell.setCellValue(value);}
      		                    } else {
      		                      /*cell.getCellStyle().setDataFormat((short)0);*/	
    		                      cell.setCellValue(value);
      		                    }
    							//cellDate.setCellValue(date);
    							//cellDate.setCellValue(value);
    		                    
    						}else if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
    		                    //cellNumber = row.createCell((short) cellNum);
    		                    //cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    		                    //cellNumber.setCellValue(value);
    		                    cell = row.createCell((short) cellNum);
    		                    int zInt = 0;
    		                    if (value.equals("null")){
    		                        cell.setCellValue(zInt);
    		                    }else{
    		                        
    		                        if ((value.indexOf("."))!= -1){
    		                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    		                                
    		                            //if (value.startsWith("$")){
    		                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    		                                String tempDollar = value.trim();
    		                                tempDollar = tempDollar.replaceAll(" ", "").substring(0);
    		                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
    		                                //System.out.println("SUBSTRING |" + tempDollar);
    		                                //System.out.println("Before copy Value |" + tempDollar);
    		                                //tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
    		                                //System.out.println("After copy Value |" + tempDollar);
    		                                if ((tempDollar.indexOf(","))!= -1){
    		                                    tempDollar = tempDollar.replaceAll(",", "");
    		                                }
    		                                //System.out.println("The final string 2IF is "+tempDollar);
    		                                double tempDoubleDollar = 0.0;
    		                                try {
    		                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setDataFormat((short)8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleCurrencyDecimalNumber); 
	    	                                    cell.setCellValue(tempDoubleDollar);
    		                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    		                                    cell.setCellValue(tempDollar);
    		                                }                                
    		                                
    		
    		                            }else{
    		                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    		                                String tempDoubleStr = value.trim();
    		                                tempDoubleStr = tempDoubleStr.replaceAll(" ", "").substring(0);
    		                                if ((tempDoubleStr.indexOf(","))!= -1){
    		                                    tempDoubleStr = tempDoubleStr.replaceAll(",", "");
    		                                }
    		                                double tempDouble = 0.0;
    		                                try {
    		                                  tempDouble = Double.parseDouble(tempDoubleStr);
		   	                                    if(styleCell!=null) {
		   	                                    	styleCell.setDataFormat((short) 0x28); // for decimal
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDecimalNumber);
    		                                  cell.setCellValue(tempDouble);
    		                                } catch (NumberFormatException ne) {
	    	                                    if(styleCell!=null) {
	    	                                    	styleCell.setWrapText(true);
	    	                                    	cell.setCellStyle(styleCell);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    		                                    cell.setCellValue(tempDoubleStr);
    		                                }
    		                            }
    		                                
    		                        }else {
    		                            if (!(value.equals(""))){
    		                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    		                                //if (value.startsWith("$")){
    		                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    		                                    String tempInt = value.trim();
    		                                    tempInt = tempInt.replaceAll(" ", "").substring(0);
    		                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
    		                                    //System.out.println("SUBSTRING |" + tempInt);
    		                                    //System.out.println("Before copy Value |" + tempInt);
    		                                    //tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
    		                                    //System.out.println("After copy Value |" + tempInt);
    		                                    if ((tempInt.indexOf(","))!= -1){
    		                                        tempInt = tempInt.replaceAll(",", "");
    		                                    }
    		                                    //System.out.println("The final string INT 2 is "+tempInt);
    		                                    
    		                                    Long tempIntDollar = 0L;
    		                                    
    		                                    try {
    		                                        tempIntDollar = Long.parseLong(tempInt);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 6);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleCurrencyNumber);
    		                                        cell.setCellValue(tempIntDollar);
    		                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
    		                                        cell.setCellValue(tempInt);
    		                                    }                                    
    		                                }else{
    		                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    		                                    String tempStr = value.trim();
    		                                    if ((tempStr.indexOf(","))!= -1){
    		                                        tempStr = tempStr.replaceAll(",", "");
    		                                    }
    		                                    Long temp = 0L;
    		                                    
    		                                    try {
    		                                       temp = Long.parseLong(tempStr);
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setDataFormat((short) 0x26);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	
		    	                                    	cell.setCellStyle(styleNumber);
    		                                       cell.setCellValue(temp);
    		                                    } catch (NumberFormatException ne) {
		    	                                    if(styleCell!=null) {
		    	                                    	styleCell.setWrapText(true);
		    	                                    	cell.setCellStyle(styleCell);
		    	                                    } else	    	                                    	
		    	                                    	cell.setCellStyle(styleDefault);
    		                                        cell.setCellValue(tempStr);
    		                                    }
    		                                }
    		                                //int temp = Integer.parseInt(value.trim());
    		                                //  cell.setCellValue(temp);
    		                                //}else{
    		                                //  cell.setCellValue(strip.stripHtml(value));
    		                                //}
    		                        } else {
	                                    if(styleCell!=null) {
	                                    	styleCell.setWrapText(true);
	                                    	cell.setCellStyle(styleCell);
	                                    } else	    	                                    	
	                                    	cell.setCellStyle(styleDefault);
    		                        }
    		                    }
    		                    }
    		                    
    		                     
    		                 }
    		                else { 
    							//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
                                if(styleCell!=null) {
                                	styleCell.setWrapText(true);
                                	cell.setCellStyle(styleCell);
                                } else	    	                                    	
                                	cell.setCellStyle(styleDefault);
    		 				    cell.setCellValue(strip.stripHtml(value));
    						}
    		
    						//if (!(value.equals(""))){
    						//int temp = Integer.parseInt(value.trim());
    						//cell.setCellValue(temp);
    						//}else{
    						//	cell.setCellValue(strip.stripHtml(value));
    						//}
    		                //HSSFCellStyle styleFormat = null;
    		                //HSSFCellStyle numberStyle = null;
    		                //HSSFFont formatFont = null;
    		                //short fgcolor = 0;
    		                //short fillpattern = 0;
    						if (cellWidth.size() > cellNum) {
    							if (((Integer) cellWidth.get(cellNum)).intValue() < dv
    									.getDisplayValue().length())
    								cellWidth.set((cellNum),
    										(value.length()<=Globals.getMaxCellWidthInExcel())?new Integer(value.length()):new Integer(Globals.getMaxCellWidthInExcel()));
    						} else
    							cellWidth.add((cellNum), (value.length()<=Globals.getMaxCellWidthInExcel())?new Integer(value.length()):new Integer(Globals.getMaxCellWidthInExcel()));
    		                //System.out.println("1IF "+ (dv.isBold()) + " "+ value + " " + dv.getDisplayTotal() + " " + dv.getColName() );
    						if (dv.isBold()) {
    		                    if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
    		                        if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    		                            cell.setCellStyle(styleCurrencyTotal);
    		                        }
    		                        else {
    		                            cell.setCellStyle(styleTotal);
    		                        }
    		                    } else {
    		                        cell.setCellStyle(styleDefaultTotal);
    		                    }
    							bold = true;
    						}
    		                //System.out.println("2IF "+ (dr.isRowFormat()) + " " +  (dv.isCellFormat()) + " " + (styles!=null));
    						if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) {
    							  //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default")));
    							continue;
    						}
    		                //System.out.println("3IF "+ (htmlFormat != null) + " " +  (dv.getFormatId() != null) + " " + (bold == false) + " "+ (styles != null));
    						if (htmlFormat != null && dv.getFormatId() != null && bold == false
    								&& styles != null) {
    		                     // cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dv.getFormatId(),"default")));
    						} //else if (bold == false)
    							//cell.setCellStyle(styleDefault);
    					} // if (dv.isVisible)
    				} // for
    				
    				/*for (int tmp=0; tmp<dataTypeMap.size(); tmp++){
    					String dataTypeStr = (String)(dataTypeMap.get(tmp));
    					if(dataTypeStr.equals("NUMBER")){
    						cell.setCellStyle(styleNumber);
    					}else if (dataTypeStr.equals("VARCHAR2")){
    						cell.setCellStyle(styleDefault);

    					}else if (dataTypeStr.equals("DATE")){
    						cell.setCellStyle(styleDate);
    					}else{
    						
    					}
    	                
    				}*/
    				rowNum += 1;
    				int cw = 0;
    				for (int i = 0; i < cellWidth.size(); i++) {
    					cw = ((Integer) cellWidth.get(i)).intValue() + 12;
    					// if(i!=cellWidth.size()-1)
    					sheet.setColumnWidth((short) (i), (short) ((cw * 8) / ((double) 1 / 20)));
    					// else
    					// sheet.setColumnWidth((short) (i + 1), (short) ((cw * 10) /
    					// ((double) 1 / 20)));
    				}

    			} // for
    			
	       		// To Display Total Values for Linear report
	       		if(rd.reportDataTotalRow!=null) {
		       		row = sheet.createRow(rowNum);
		       		cellNum = -1;
		       		rd.reportTotalRowHeaderCols.resetNext();
		       		//for (rd.reportTotalRowHeaderCols.resetNext(); rd.reportTotalRowHeaderCols.hasNext();) {
		                cellNum += 1;
						RowHeaderCol rhc = rd.reportTotalRowHeaderCols.getNext();
						RowHeader rh = rhc.getRowHeader(0);
						row.createCell((short) cellNum).setCellValue(strip.stripHtml(rh.getRowTitle()));
						row.getCell((short) cellNum).setCellStyle(styleDefaultTotal);
		       		//}
	       			
					rd.reportDataTotalRow.resetNext();
	       			DataRow drTotal = rd.reportDataTotalRow.getNext();
       				//cellNum = -1;
	       			
	       			drTotal.resetNext();
	       			drTotal.getNext();
	       			for (; drTotal.hasNext();) {
	       				cellNum += 1;
    					cell = row.createCell((short) cellNum);
	       				DataValue dv = drTotal.getNext();
	       				String value = dv.getDisplayValue();
	       				cell.setCellValue(value);
	       				boolean bold = false;
	       				if (dv.isBold()) {
	       					if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	       						if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	       							cell.setCellStyle(styleCurrencyTotal);
	       						} else {
	       							cell.setCellStyle(styleTotal);
	       						}
	       					} else {
	       						cell.setCellStyle(styleDefaultTotal);
	       					}
	       					bold = true;
	       				}
	       			}
	       		}
	       		
	       		/*
				if (rr.getReportType().equals(AppConstants.RT_LINEAR) && rd.reportTotalRowHeaderCols!=null) {

	    			for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();) {
	    				rowCount++;
	    				
	    				
	    				DataRow dr = rd.reportDataTotalRow.getNext();
	    				row = sheet.createRow(rowNum);
	    				cellNum = -1;
	    				int j = 0;
	    				cellNum += 1;
	    				cell = row.createCell((short) cellNum);
	    				cell.setCellValue("Total");
	    				cell.setCellStyle(styleTotal);
	    				
	    				for (dr.resetNext(); dr.hasNext();j++) {
	    					DataValue dv = dr.getNext();
	    					if(j==0 || !dv.isVisible()) continue;
	    					cellNum += 1;
	    	                styleCell = null;
	    					boolean bold = true;
	    					String value = nvl(dv.getDisplayValue());
    	                    //cellNumber = row.createCell((short) cellNum);
    	                    //cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
    	                    //cellNumber.setCellValue(dv.getDisplayValue());
    	                    cell = row.createCell((short) cellNum);
    	                    int zInt = 0;
    	                    if (value.equals("null")){
    	                        cell.setCellValue(zInt);
    	                    }else{
    	                        
    	                        if ((value.indexOf("."))!= -1){
    	                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    	                                
    	                            //if (value.startsWith("$")){
    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    	                                String tempDollar = value.trim();
    	                                tempDollar = tempDollar.replaceAll(" ", "").substring(0);
    	                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
    	                                //System.out.println("SUBSTRING |" + tempDollar);
    	                                //System.out.println("Before copy Value |" + tempDollar);
    	                                //tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
    	                                //System.out.println("After copy Value |" + tempDollar);
    	                                if ((tempDollar.indexOf(","))!= -1){
    	                                    tempDollar = tempDollar.replaceAll(",", "");
    	                                }
    	                                //System.out.println("The final string 2IF is "+tempDollar);
    	                                double tempDoubleDollar = 0.0;
    	                                try {
    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
    	                                    if(styleTotal!=null) {
    	                                    	styleTotal.setDataFormat((short) 8);//HSSFDataFormat.getBuiltinFormat("($#,##0.00_);[Red]($#,##0.00)"));
    	                                    	cell.setCellStyle(styleTotal);
    	                                    } else	    	                                    	
    	                                    	cell.setCellStyle(styleCurrencyDecimalNumberTotal);	    	                                    
    	                                    cell.setCellValue(tempDoubleDollar);
    	                                } catch (NumberFormatException ne) {
    	                                    if(styleTotal!=null) {
    	                                    	styleTotal.setWrapText(true);
    	                                    	cell.setCellStyle(styleTotal);
    	                                    } else	    	                                    	
    	                                    	cell.setCellStyle(styleDefault);
    	                                    cell.setCellValue(tempDollar);
    	                                }                                
    	                                
    	
    	                            }else{
    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    	                                String tempDoubleStr = value.trim();
    	                                tempDoubleStr = tempDoubleStr.replaceAll(" ", "").substring(0);
    	                                if ((tempDoubleStr.indexOf(","))!= -1){
    	                                    tempDoubleStr = tempDoubleStr.replaceAll(",", "");
    	                                }
    	                                double tempDouble = 0.0;
    	                                try {
    	                                  tempDouble = Double.parseDouble(tempDoubleStr);
	   	                                    if(styleTotal!=null) {
	   	                                    	styleTotal.setDataFormat((short)0x28 );//HSSFDataFormat.getBuiltinFormat("(#,##0.00_);[Red](#,##0.00)"));
    	                                    	cell.setCellStyle(styleTotal);
    	                                    } else	    	                                    	
    	                                    	cell.setCellStyle(styleDecimalNumberTotal);
	   	                                    cell.setCellValue(tempDouble);
    	                                } catch (NumberFormatException ne) {
    	                                    if(styleTotal!=null) {
    	                                    	styleTotal.setWrapText(true);
    	                                    	cell.setCellStyle(styleTotal);
    	                                    } else	    	                                    	
    	                                    	cell.setCellStyle(styleDefault);
    	                                    cell.setCellValue(tempDoubleStr);
    	                                }
    	                            }
    	                                
    	                        }else {
    	                            if (!(value.equals(""))){
    	                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
    	                                //if (value.startsWith("$")){
    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
    	                                    String tempInt = value.trim();
    	                                    tempInt = tempInt.replaceAll(" ", "").substring(0);
    	                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
    	                                    //System.out.println("SUBSTRING |" + tempInt);
    	                                    //System.out.println("Before copy Value |" + tempInt);
    	                                    //tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
    	                                    //System.out.println("After copy Value |" + tempInt);
    	                                    if ((tempInt.indexOf(","))!= -1){
    	                                        tempInt = tempInt.replaceAll(",", "");
    	                                    }
    	                                    //System.out.println("The final string INT 2 is "+tempInt);
    	                                    
    	                                    Long tempIntDollar = 0L;
    	                                    
    	                                    try {
    	                                        tempIntDollar = Long.parseLong(tempInt);
	    	                                    if(styleTotal!=null) {
	    	                                    	styleTotal.setDataFormat((short) 6);//HSSFDataFormat.getBuiltinFormat("($#,##0_);[Red]($#,##0)"));
	    	                                    	cell.setCellStyle(styleTotal);
	    	                                    } else	
	    	                                    	cell.setCellStyle(styleCurrencyNumberTotal);
    	                                        cell.setCellValue(tempIntDollar);
    	                                    } catch (NumberFormatException ne) {
	    	                                    if(styleTotal!=null) {
	    	                                    	styleTotal.setWrapText(true);
	    	                                    	cell.setCellStyle(styleTotal);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    	                                        cell.setCellValue(tempInt);
    	                                    }                                    
    	                                }else{
    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
    	                                    String tempStr = value.trim();
    	                                    if ((tempStr.indexOf(","))!= -1){
    	                                        tempStr = tempStr.replaceAll(",", "");
    	                                    }
    	                                    Long temp = 0L;
    	                                    
    	                                    try {
    	                                       temp = Long.parseLong(tempStr);
	    	                                    if(styleTotal!=null) {
	    	                                    	styleTotal.setDataFormat((short) 0x26);//HSSFDataFormat.getBuiltinFormat("(#,##0_);[Red](#,##0)"));
	    	                                    	cell.setCellStyle(styleTotal);
	    	                                    } else	
	    	                                    	cell.setCellStyle(styleNumber);
    	                                       cell.setCellValue(temp);
    	                                    } catch (NumberFormatException ne) {
	    	                                    if(styleTotal!=null) {
	    	                                    	styleTotal.setWrapText(true);
	    	                                    	cell.setCellStyle(styleTotal);
	    	                                    } else	    	                                    	
	    	                                    	cell.setCellStyle(styleDefault);
    	                                        cell.setCellValue(tempStr);
    	                                    }
    	                                }
    	                                //int temp = Integer.parseInt(dv.getDisplayValue().trim());
    	                                //  cell.setCellValue(temp);
    	                                //}else{
    	                                //  cell.setCellValue(strip.stripHtml(dv.getDisplayValue()));
    	                                //}
    	                        } else {
                                    if(styleTotal!=null) {
                                    	styleTotal.setWrapText(true);
                                    	cell.setCellStyle(styleTotal);
                                    } else	    	                                    	
                                    	cell.setCellStyle(styleDefault);
    	                        }
    	                    }
    	                    }
    	                    
    	                 	    				
	
	    				
	    			}
				}
			}
    		*/		
    				
    			
    			
    		  } else if (rr.getReportType().equals(AppConstants.RT_CROSSTAB)) { // Linear
      		    int rowCount = 0;
    		    List l = rd.getReportDataList();
    		    boolean first = true;
      			for (int dataRow = 0; dataRow < l.size(); dataRow++) {
      				
      				
      				DataRow dr = (DataRow) l.get(dataRow);
      				row = sheet.createRow(rowNum);

      				cellNum = -1;
      				first = true;
      				Vector<DataValue> rowNames = dr.getRowValues();
      				for(dr.resetNext(); dr.hasNext(); rowCount++ ) {
      				if(first) {
	                    if(rowNames!=null) {
	                        for(int i=0; i<rowNames.size(); i++) {
	                        	DataValue dv = rowNames.get(i);
	                        	cellNum += 1;
	                        	row.createCell((short) cellNum).setCellValue(strip.stripHtml(dv.getDisplayValue()));
	                        	row.getCell((short) cellNum).setCellStyle(styleDefault);
	                        }
	                    }
      				}
					first = false;

					DataValue dv = dr.getNext();
					if(dv.isVisible()) {
						String value = dv.getDisplayValue();
	    				if(value.indexOf("|#")!=-1)
							value = value.substring(0,value.indexOf("|"));

						if(dr.isRowFormat() || nvl(dv.getFormatId()).length()>0) {
							cellNum += 1;
							row.createCell((short) cellNum).setCellValue(strip.stripHtml(dv.getDisplayValue()));
							//row.getCell((short) cellNum).setCellStyle(styleDefault);
							if(nvl(dv.getFormatId()).length()>0)
								row.getCell((short) cellNum).setCellStyle((HSSFCellStyle) styles.get(nvl(dv.getFormatId(),"default")));
							else 
								row.setRowStyle((HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default")));
						} else {
							cellNum += 1;
							row.createCell((short) cellNum).setCellValue(strip.stripHtml(value));
							row.getCell((short) cellNum).setCellStyle(styleDefault);
						} // end
						value = dv.getDisplayValue();
						if(value.indexOf("|#")!=-1) {
							String color = value.substring(value.indexOf("|")+1);
							if(color.equals("#FF0000")) 
								row.getCell((short) cellNum).setCellStyle((HSSFCellStyle) styles.get("red"));
							else if (color.equals("#008000"))
								row.getCell((short) cellNum).setCellStyle((HSSFCellStyle) styles.get("green"));
							else if (color.equals("#FFFF00"))
								row.getCell((short) cellNum).setCellStyle((HSSFCellStyle) styles.get("yellow"));
							else {
								row.getCell((short) cellNum).setCellStyle((HSSFCellStyle) styles.get("default"));
							}
							
						}
      				}
      				}
					rowNum += 1;
      				int cw = 0;
      				for (int i = 0; i < cellWidth.size(); i++) {
      					cw = ((Integer) cellWidth.get(i)).intValue() + 12;
      					// if(i!=cellWidth.size()-1)
      					sheet.setColumnWidth((short) (i), (short) ((cw * 8) / ((double) 1 / 20)));
      					// else
      					// sheet.setColumnWidth((short) (i + 1), (short) ((cw * 10) /
      					// ((double) 1 / 20)));
      				}


      			} // for
      			
    		  }
      			
      			
    		  }
    	
		String footer = (String) session.getAttribute("FOOTER_"+index);
		if(nvl(footer).length()>0) {
		    footer = Utils.replaceInString(footer, "<BR/>", " ");
		    footer = Utils.replaceInString(footer, "<br/>", " ");
		    footer = Utils.replaceInString(footer, "<br>", " ");
			footer  = strip.stripHtml(nvl(footer).trim());
			row = sheet.createRow(rowNum);
			cellNum = 0;
			row.createCell((short) cellNum).setCellValue(footer);
			sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (columnRows)));
			//sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum+columnRows, (short) (cellNum)));
			rowNum += 1;
		}
		
		if(Globals.getShowDisclaimer() && !Globals.disclaimerPositionedTopInCSVExcel()) { 
			
			rowNum += 1;
			row = sheet.createRow(rowNum);
			cellNum = 0;
			String disclaimer = Globals.getFooterFirstLine() + " " + Globals.getFooterSecondLine();
			row.createCell((short) cellNum).setCellValue(disclaimer);
			sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (columnRows)));
			rowNum += 1;
		}
        
    	logger.debug(EELFLoggerDelegate.debugLogger, ("##### Heap utilization statistics [MB] #####"));
        logger.debug(EELFLoggerDelegate.debugLogger, ("Used Memory:"
    			+ (runtime.maxMemory() - runtime.freeMemory()) / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
    			+ runtime.freeMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));
    	return returnValue;
           
	}

	private void paintExcelHeader(HSSFWorkbook wb, int rowNum, int col, String reportTitle,
			String reportDescr, HSSFSheet sheet) {
		short s1 = 0, s2 = (short) (col-1);
        rowNum += 1;
		sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
		HSSFRow row = null, row1 = null;
        
        row = sheet.createRow(rowNum);
		// Header Style
		HSSFCellStyle styleHeader = wb.createCellStyle();
		styleHeader.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = wb.createFont();
		font.setFontHeight((short) (font_header_title_size / 0.05)); //14
		font.setFontName("Tahoma");
		font.setColor(HSSFColor.BLACK.index);
		styleHeader.setFont(font);

		HSSFCell cell = row.createCell((short) 0);
		cell.setCellValue(reportTitle);
		cell.setCellStyle(styleHeader);
		HSSFHeader header = sheet.getHeader();
		header.setCenter(HSSFHeader.font("Tahoma", "")+ HSSFHeader.fontSize((short) 9)+" " + reportTitle);
		
		//header.setCenter(HSSFHeader.font("Tahoma", "")+ HSSFHeader.fontSize((short) 9)+reportTitle+"\n"+((Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0)?reportDescr:""));

		// Report Description
		if (Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0) {
			rowNum += 1;
			sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
			HSSFCellStyle styleDescription = wb.createCellStyle();
			styleDescription.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFFont fontDescr = wb.createFont();
			fontDescr.setFontHeight((short) font_header_descr_size);
			fontDescr.setFontName("Tahoma");
			fontDescr.setColor(HSSFColor.BLACK.index);
			styleDescription.setFont(fontDescr);
			HSSFCell cellDescr = row.createCell((short) 0);
			cellDescr.setCellValue(reportDescr);
			cellDescr.setCellStyle(styleHeader);
		}
		
		if(Globals.disclaimerPositionedTopInCSVExcel()) {
	        rowNum += 1;
	        row = sheet.createRow(rowNum);
			sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
			HSSFCellStyle styleDescription = wb.createCellStyle();
			styleDescription.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			HSSFFont fontDescr = wb.createFont();
	        fontDescr.setFontHeight((short) (font_size / 0.05)); //14
	        fontDescr.setFontName("Tahoma");
	        fontDescr.setColor(HSSFColor.BLACK.index);
	        fontDescr.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
	        styleDescription.setFont(fontDescr);
			HSSFCell cellDescr = row.createCell((short) 0);
			String disclaimer = Globals.getFooterFirstLine() + " " + Globals.getFooterSecondLine();
			cellDescr.setCellValue(disclaimer);
			cellDescr.setCellStyle(styleDescription);
		}

        rowNum += 1;
        row = sheet.createRow(rowNum);
		// System.out.println(" Last Row " + wb.getSheetAt(0).getLastRowNum());
	}

    private void paintExcelFooter(HSSFWorkbook wb, int rowNum, int col, HSSFSheet sheet) {
        logger.debug(EELFLoggerDelegate.debugLogger, ("excel footer"));
        //HSSFSheet sheet = wb.getSheet(getSheetName());
        HSSFFooter footer = sheet.getFooter();
        footer.setLeft(HSSFFooter.font("Tahoma", "")+ HSSFFooter.fontSize((short) font_footer_size)+ "Page " + HSSFFooter.page() 
        		+ " of " + HSSFFooter.numPages() );
        footer.setCenter(HSSFFooter.font("Tahoma", "")+ HSSFFooter.fontSize((short) font_footer_size)+Globals.getFooterFirstLine()+"\n"+Globals.getFooterSecondLine());
        //footer.setCenter(HSSFFooter.font("Tahoma", "Italic")+ HSSFFooter.fontSize((short) 16))+Globals.getFooterSecondLine());
/*        footer.font("Tahoma");
        short s1 = 0, s2 = (short) (col-1);
        rowNum += 1;
        sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
        HSSFRow row = null, row1 = null;
        
        row = sheet.createRow(rowNum);
        // Header Style
        HSSFCellStyle styleFooter = wb.createCellStyle();
        styleFooter.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        HSSFFont font = wb.createFont();
        font.setFontHeight((short) (10 / 0.05));
        font.setFontName("Tahoma");
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleFooter.setFont(font);

        HSSFCell cell = row.createCell((short) 0);
        debugLogger.debug(Globals.getFooterFirstLine());
        cell.setCellValue(Globals.getFooterFirstLine());
        cell.setCellStyle(styleFooter);
        
        rowNum += 1; 
        sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
        row = sheet.createRow(rowNum);
        cell = row.createCell((short) 0);
        debugLogger.debug(Globals.getFooterSecondLine());        
        cell.setCellValue(Globals.getFooterSecondLine());
        cell.setCellStyle(styleFooter);
*/        
        logger.debug(EELFLoggerDelegate.debugLogger, ("Done"));
    }

	public String saveAsExcelFile(HttpServletRequest request, ReportData rd,
			ArrayList reportParamNameValues, String reportTitle, String reportDescr) {
		return saveAsExcelFile(request, rd, reportParamNameValues, reportTitle, reportDescr, 2); //2 denotes ReportRuntime object should be taken from session. 
	}
	public String saveAsExcelFile(HttpServletRequest request, ReportData rd,
			ArrayList reportParamNameValues, String reportTitle, String reportDescr, int requestFlag) {
          setSheetName(Globals.getSheetName());
		try {
			ReportRuntime rr;
			if(requestFlag == 2)
				rr = (ReportRuntime) request.getSession().getAttribute(
					AppConstants.SI_REPORT_RUNTIME);
			else
				rr = (ReportRuntime) request.getAttribute(
						AppConstants.SI_REPORT_RUNTIME);
			HSSFWorkbook wb = new HSSFWorkbook();
			HashMap styles = new HashMap();
			if (rr != null)
				styles = loadStyles(rr, wb);
			String xlsFName = AppUtils.generateUniqueFileName(request, rr.getReportName(), AppConstants.FT_XLS);
			logger.debug(EELFLoggerDelegate.debugLogger, ("Xls File name " +
					  AppUtils.getTempFolderPath()
					 + xlsFName));
			FileOutputStream xlsOut = new FileOutputStream(AppUtils.getTempFolderPath()
					+ xlsFName);
			// BufferedWriter xlsOut = new BufferedWriter(new
			// FileWriter(AppUtils
			// .getTempFolderPath()
			// + xlsFName));

			int col = 0;
            //System.out.println("Row Header Count " + rd.reportRowHeaderCols.getRowCount());
            //System.out.println("Total Count " + rd.getTotalColumnCount());
            
			if (!rd.reportRowHeaderCols.hasNext())
				col = rd.getTotalColumnCount();
			else
				col = rd.getTotalColumnCount();
			int rowNum = 0;
			HSSFSheet sheet = wb.createSheet(getSheetName());
                       
            if (Globals.getPrintTitleInDownload()&& reportTitle != null ) {
                paintExcelHeader(wb, rowNum, col, reportTitle, reportDescr, sheet);
                rowNum = sheet.getLastRowNum();
            } else
                rowNum = 0;
            if (Globals.getPrintParamsInDownload() && rr.getParamNameValuePairsforPDFExcel(request, 1) != null) {
                paintExcelParams(wb,rowNum,col,rr.getParamNameValuePairsforPDFExcel(request, 1), rr.getFormFieldComments(request), sheet, reportTitle, reportDescr);
            } // if
            rowNum = sheet.getLastRowNum();
            //System.out.println(" rowNum after Params " + rowNum); 
            paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, "", xlsOut, request);
            if (Globals.getPrintFooterInDownload() ) {
              rowNum = sheet.getLastRowNum();
              rowNum += 2;
              paintExcelFooter(wb, rowNum, col, sheet);
            }
            //response.setContentType("application/vnd.ms-excel");
            //response.setHeader("Content-disposition", "attachment;filename=download_all_"
             //       + user_id + ".xls");            
			wb.write(xlsOut);
			xlsOut.flush();
			xlsOut.close();
			return xlsFName;
		} catch (Exception e) {
			e.printStackTrace();
			(new ErrorHandler()).processError(request, "Exception saving data to EXCEL file: "
					+ e.getMessage());
			return null;
		}
	} // saveAsExcelFile

	public void createExcelFileContent(Writer out, ReportData rd, ReportRuntime rr, HttpServletRequest request,
			HttpServletResponse response, String user_id, int type) throws IOException, RaptorException {
		// Adding utility for downloading Dashboard reports.
		
		HashMap styles = new HashMap();
		HttpSession session = request.getSession();
		ServletOutputStream sos = null;
		BufferedInputStream buf = null;
		HSSFWorkbook wb = null;
//		if(session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null)
//		ReportRuntime rrDashboard = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
		String formattedDate = "";
		String xlsFName = "";
		int returnValue = 0;
		boolean isDashboard = false;
        if ((session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null) && ( ((String) session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)).equals(rr.getReportID())) ) {
        	isDashboard = true;
        }
		if(isDashboard) {
			try {
		        formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
		        xlsFName = "dashboard"+formattedDate+user_id+".xls";
	        
			FileInputStream xlsIn = null;
			POIFSFileSystem fileSystem = null;
			buf = null;
			FileOutputStream xlsOut = null;

            
/*			try {
				xlsIn = new FileInputStream (AppUtils.getTempFolderPath()
						+ xlsFName);
			}
			catch (FileNotFoundException e) {
				System.out.println ("File not found in the specified path.");
				e.printStackTrace ();
			}
            if(xlsIn != null) {
              fileSystem = new POIFSFileSystem (xlsIn);
              wb = new HSSFWorkbook(fileSystem);
            } else {
    			xlsOut = new FileOutputStream(AppUtils.getTempFolderPath()
    					+ xlsFName);
            	wb = new HSSFWorkbook();
            }
*/            
            
			Map reportRuntimeMap = null;
			Map reportDataMap = null;
			//Map reportDisplayTypeMap = null;
			reportRuntimeMap 	= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
			reportDataMap 		= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
			//reportDisplayTypeMap = (TreeMap) request.getSession().getAttribute(AppConstants.SI);
			HSSFSheet sheet =  null;
			if(reportRuntimeMap!=null) {
				//ServletOutputStream sos = response.getOutputStream();
				Set setReportRuntime = reportRuntimeMap.entrySet();
				Set setReportDataMap = reportDataMap.entrySet();
				Iterator iter2 = setReportDataMap.iterator();
                int count = 0;

				for(Iterator iter = setReportRuntime.iterator(); iter.hasNext(); ) {
					count++;
					try {
						xlsIn = new FileInputStream (AppUtils.getTempFolderPath()
								+ xlsFName);
					}
					catch (FileNotFoundException e) {
						System.out.println ("File not found in the specified path.");
						//e.printStackTrace ();
					}
		            if(xlsIn != null) {
		                fileSystem = new POIFSFileSystem (xlsIn);
		                wb = new HSSFWorkbook(fileSystem);
		      			xlsOut = new FileOutputStream(AppUtils.getTempFolderPath()
		      					+ xlsFName);
		              } else {
		      			xlsOut = new FileOutputStream(AppUtils.getTempFolderPath()
		      					+ xlsFName);
		              	wb = new HSSFWorkbook();
		              }
					
					Map.Entry entryData 		= (Entry) iter2.next();
					Map.Entry entry 			= (Entry) iter.next();
					//String rep_id 				= (String) entry.getKey();
					ReportRuntime rrDashRep 	= (ReportRuntime) entry.getValue();
					ReportData rdDashRep 		= (ReportData) entryData.getValue();
					//styles = loadStyles(rrDashRep, wb);
			        int col = 0;
			        String reportTitle =  (nvl(rrDashRep.getReportTitle()).length()>0?rrDashRep.getReportTitle():rrDashRep.getReportName());
			        String reportDescr = rrDashRep.getReportDescr();
			        if (!rdDashRep.reportRowHeaderCols.hasNext())
			            col = rdDashRep.getTotalColumnCount();
			        else
			            col = rdDashRep.getTotalColumnCount();
			        if(col==0) col=10;
			        int rowNum = 0;
			        String formattedReportName = new HtmlStripper().stripSpecialCharacters(rrDashRep.getReportName());
			        
                    try { 
                    	sheet = wb.createSheet(formattedReportName);
                    	sheet.getPrintSetup().setLandscape(true);
    					styles = loadStyles(rrDashRep, wb);
                    } catch (IllegalArgumentException ex) { wb.write(xlsOut);xlsOut.flush();xlsOut.close();continue;}
			                   
			        if (Globals.getPrintTitleInDownload()&& reportTitle != null ) {
			            paintExcelHeader(wb, rowNum, col, reportTitle, reportDescr, sheet);
			            rowNum = sheet.getLastRowNum();
			        } else
			            rowNum = 0;
			        //getting ReportRuntime object from session
			        if (Globals.getPrintParamsInDownload() && rrDashRep.getParamNameValuePairsforPDFExcel(request, 1) != null) {
			        	if(count > 1 && Globals.showParamsInAllDashboardReports()) 
			        		paintExcelParams(wb,rowNum,col,rrDashRep.getParamNameValuePairsforPDFExcel(request, 1), rrDashRep.getFormFieldComments(request), sheet, reportTitle, reportDescr);
			        	else if (count == 1)
			        		paintExcelParams(wb,rowNum,col,rrDashRep.getParamNameValuePairsforPDFExcel(request, 1), rrDashRep.getFormFieldComments(request), sheet, reportTitle, reportDescr);
			        } // if
			        rowNum = sheet.getLastRowNum();
			        String sql_whole = rrDashRep.getWholeSQL();
		        	returnValue = paintExcelData(wb, rowNum, col, rdDashRep, styles,rrDashRep, sheet, sql_whole, xlsOut, request);
		        	if( returnValue == 0 ) {
				        if (Globals.getPrintFooterInDownload()) {
				          rowNum = sheet.getLastRowNum();
				          rowNum += 2;
				          paintExcelFooter(wb, rowNum, col, sheet);
				        }
						//wb.write(sos);
						wb.write(xlsOut);
				        //TODO Remove comment
						xlsOut.flush();
						xlsOut.close();
						wb = null;
		        	} else {
						//xlsOut.flush();
						//xlsOut.close();
		        		//response.reset();
		        		//response.setContentType("application/vnd.ms-excel");
//		        		RequestDispatcher dispatcher = request.getRequestDispatcher("raptor.htm?r_action=report.message");
//		        		request.setAttribute("message", Globals.getUserDefinedMessageForMemoryLimitReached());
//		        		try {
//		        			dispatcher.forward(request, response);
//		        		} catch (ServletException ex) {}
		        	}
				}
				
		        response.reset();
		        response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-disposition", "attachment;filename="+"dashboard"+formattedDate+user_id+".xls");
				sos = response.getOutputStream();
				xlsIn = new FileInputStream (AppUtils.getTempFolderPath()
						+ xlsFName);
			    buf = new BufferedInputStream(xlsIn);
			    int readBytes = 0;
			    byte [] bOut = new byte [4096];
			    //read from the file; write to the ServletOutputStream
			    //while ((readBytes = buf.read()) != -1)
			    while ((readBytes = buf.read (bOut, 0, 4096))> 0) {
			    	buf.available(); 
			    	sos.write (bOut, 0, readBytes); 
			    }

			    	//sos.write(readBytes);
			    } 
		} catch (IOException ex) { ex.printStackTrace(); throw ex;}
			 
			    finally {
			      if (sos != null)
			        sos.close();
			      if (buf != null)
			        buf.close();
			    }				
				
		        File f = new File (AppUtils.getTempFolderPath()
							+ xlsFName); 
		        if(f.exists()) f.delete();
				
		} else {
        	wb = new HSSFWorkbook();
			// PrintWriter xlsOut = new PrintWriter(out).;
	        setSheetName(Globals.getSheetName());
			//ServletOutputStream sos = response.getOutputStream();
	        //PrintWriter outWriter = response.getWriter();
			if (rr != null)
				styles = loadStyles(rr, wb);
	/*		int col = 0;
			if (!rd.reportRowHeaderCols.hasNext())
				col = rd.getTotalColumnCount();
			else
				col = rd.getTotalColumnCount() + 1;
			int rowNum = 0;
			String reportTitle = rr.getReportName();
			String reportDescr = rr.getReportDescr();
			// if (Globals.getPrintTitleInDownload() && reportTitle != null) {
			HSSFSheet sheet = wb.createSheet(getSheetName());
	        System.out.println(" Title " + Globals.getPrintTitleInDownload());
	        
			if (Globals.getPrintTitleInDownload()&& reportTitle != null ) {
				paintExcelHeader(wb, rowNum, col, reportTitle, reportDescr);
				rowNum = wb.getSheetAt(0).getLastRowNum();
			} else
				rowNum = 0;
	        System.out.println(" Params " + Globals.getPrintParamsInDownload());
	        if (Globals.getPrintParamsInDownload() && rr.getParamNameValuePairs() != null) {
	            paintExcelParams(wb,rowNum,col,rr.getParamNameValuePairs());
	        } // if
			paintExcelData(wb, rowNum, col, rd, styles);
			rowNum = wb.getSheetAt(0).getLastRowNum();
	*/		
	        int col = 0;
	        //System.out.println("Row Header Count " + rd.reportRowHeaderCols.getRowCount());
	        //System.out.println("Total Count " + rd.getTotalColumnCount());
	        String reportTitle =  (nvl(rr.getReportTitle()).length()>0?rr.getReportTitle():rr.getReportName());
	        String reportDescr = rr.getReportDescr();
	        
	        col = getColumnCountForDownloadFile(rr,rd);
	        /*if (!rd.reportRowHeaderCols.hasNext())
	            col = rd.getTotalColumnCount();
	        else
	            col = rd.getTotalColumnCount();
	        */
	        int rowNum = 0;
	        HSSFSheet sheet = wb.createSheet(getSheetName());
	        sheet.getPrintSetup().setLandscape(true);
	                   
	        if (Globals.getPrintTitleInDownload()&& reportTitle != null ) {
	            paintExcelHeader(wb, rowNum, col, reportTitle, reportDescr, sheet);
	            rowNum = sheet.getLastRowNum();
	        } else
	            rowNum = 0;
	        if (Globals.getPrintParamsInDownload() && rr.getParamNameValuePairsforPDFExcel(request, 1) != null) {
                ArrayList paramsList = rr.getParamNameValuePairsforPDFExcel(request, 1);
	            if(paramsList.size()<=0) {
	            	paramsList = (ArrayList) request.getSession().getAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
	            }
	
	            paintExcelParams(wb,rowNum,col,paramsList, rr.getFormFieldComments(request), sheet, reportTitle, reportDescr);
	        } // if
	        rowNum = sheet.getLastRowNum();

	        String formattedReportName = new HtmlStripper().stripSpecialCharacters(rr.getReportName());
	        formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
	        response.reset();
	        response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-disposition", "attachment;filename="+formattedReportName+formattedDate+user_id+".xls");
	        sos = response.getOutputStream();
	        
	        if(type == 3 && rr.getSemaphoreList()==null && !(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) ) { //type = 3 is whole
		        //String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
	        	//String sql_whole = rr.getWholeSQL();
		        String sql_whole = "";
		        sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
		        
		        if(sql_whole==null) {
			    	if (!rr.getReportType().equals(AppConstants.RT_HIVE))
			    		sql_whole = rr.getWholeSQL();
			    	else
			    		sql_whole = rr.getReportSQL();
		        }

		        returnValue = paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, sql_whole, sos, request);
	        } else if(type == 2) {
	        	returnValue = paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, "", sos, request);
	        } else {
		        //String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
				int downloadLimit = (rr.getMaxRowsInExcelDownload()>0)?rr.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
				String action = request.getParameter(AppConstants.RI_ACTION);
				if(!(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) && !action.endsWith("session")) {
					rd 		= rr.loadReportData(-1, AppUtils.getUserID(request), downloadLimit,request, false /*download*/);
				}
				if(rr.getSemaphoreList()!=null) {
					if(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) {
						returnValue = paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, "", sos, request);
					} else {
					   rd   =  rr.loadReportData(-1, AppUtils.getUserID(request), downloadLimit,request, true);
					   returnValue = paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, "", sos, request);
					}
				} else {
			        String sql_whole = "";
			        sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
			        
			        if(sql_whole==null) {
				    	if (!rr.getReportType().equals(AppConstants.RT_HIVE))
				    		sql_whole = rr.getWholeSQL();
				    	else
				    		sql_whole = rr.getReportSQL();
			        }

					returnValue = paintExcelData(wb, rowNum, col, rd, styles,rr, sheet, sql_whole, sos, request);
				}
	        }
	        if( returnValue == 0 ) {
		        if (Globals.getPrintFooterInDownload()) {
		          rowNum = sheet.getLastRowNum();
		          rowNum += 2;
		          paintExcelFooter(wb, rowNum, col, sheet);
		        }
		      //Alternatively:
		        wb.setPrintArea(
		                0, //sheet index
		                0, //start column
		                col, //end column
		                0, //start row
		                rowNum  //end row
		        );
		        //TODO Remove comment
				wb.write(sos);
				sos.flush();
				sos.close();
				wb = null;
	        } else {
				//sos.flush();
				//sos.close();
/*	        	response.reset();
	        	
        		RequestDispatcher dispatcher = request.getRequestDispatcher("/raptor.htm?action=raptor&r_action=report.message");
        		request.setAttribute("message", Globals.getUserDefinedMessageForMemoryLimitReached());
        		try {
        			dispatcher.forward(request, response);
        		} catch (ServletException ex) {}
*/	        	
	        }
		}
	}

    
    public void createFlatFileContent(Writer out, ReportData rd, ReportRuntime rr,
            HttpServletRequest request, HttpServletResponse response, String user_id)
			throws IOException, Exception {
        ReportHandler rephandler = new ReportHandler();
        String reportID = rr.getReportID();
        rr = rephandler.loadReportRuntime(request, reportID);
        String query = rr.getWholeSQL();
        String dbInfo = rr.getDbInfo();
        //File f = new File(request.(arg0)("/"));
        DataSet ds = ConnectionUtils.getDataSet(query, dbInfo);
        
        //Writing Column names to the file
        List l = rr.getAllColumns();
        StringBuffer allColumnsBuffer = new StringBuffer();
        DataColumnType dct = null;
        
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            dct = (DataColumnType) iter.next();
            allColumnsBuffer.append(dct.getDisplayName());
            if(iter.hasNext())
             allColumnsBuffer.append("|");
        }
        rd = rr.loadReportData(-1, user_id, -1,request, true);
		//PrintWriter txtOut = new PrintWriter(out);
        //response.setContentType("application/notepad");
        //response.setHeader("Content-disposition", "attachment;filename=download_all_"+AppUtils.getUserID(request)+".txt"); 
        ServletOutputStream sos = response.getOutputStream();
        
        //No Report Title for flat file. 
//		if (Globals.getPrintTitleInDownload() && reportTitle != null) {
//			txtOut.println();
//			txtOut.println("\"" + reportTitle + "\"");
//			txtOut.println();
//			if (Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0) {
//				txtOut.println("\"" + reportDescr + "\"");
//				txtOut.println();
//			}
//		} // if
        // No Params either
//        int count = 0;
//		if (Globals.getPrintParamsInDownload() && reportParamNameValues != null) {
//			for (Iterator iter = reportParamNameValues.iterator(); iter.hasNext();) {
//                count += 1;
//                if(count == 1) txtOut.println();
//				IdNameValue value = (IdNameValue) iter.next();
//				txtOut.println(value.getId() + " = " + value.getName());
//                if(!iter.hasNext()) txtOut.println();                
//			} // for
//		} // if



		boolean firstPass = true;
		for (rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext();) {
			DataRow dr = rd.reportDataRows.getNext();
			for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
				if (firstPass)
					rhc.resetNext();
				RowHeader rh = rhc.getNext();

				sos.print(rh.getRowTitle());
                if(rhc.hasNext()) sos.print("|");
			} // for
			firstPass = false;

			for (dr.resetNext(); dr.hasNext();) {
				DataValue dv = dr.getNext();

				sos.print( dv.getDisplayValue());
                if(dr.hasNext()) sos.print("|");
			} // for

			sos.println();
		} // for
        //sos.flush();
         sos.close();
	} // createFlatFileContent


    public void createExcel2007FileContent(Writer out, ReportData rd, ReportRuntime rr, HttpServletRequest request,
			HttpServletResponse response, String user_id, int type)
			throws Exception {
    	
        // to check performance
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	
    	logger.debug(EELFLoggerDelegate.debugLogger, ("STARTING.EXCELX DOWNLOAD...."));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("##### Heap utilization statistics [MB] #####"));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Used Memory:"
    			+ (runtime.totalMemory() - runtime.freeMemory()) / mb));
        logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
    			+ runtime.freeMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("##### END #####"));
    	
		// Adding utility for downloading Dashboard reports.
		
		Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
		HttpSession session = request.getSession();
		ServletOutputStream sos = null;
		BufferedInputStream buf = null;
		XSSFWorkbook wb = null;
        String formattedReportName = new HtmlStripper().stripSpecialCharacters(rr.getReportName());
        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
        //Sheet name to be filled is taken from property. How would this be called if it is Dashboard?
        //commented out since application will create and leave it blank.
	    //setSheetName(Globals.getSheetName());
        boolean isDashboard = false;
        if ((session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null) && ( ((String) session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)).equals(rr.getReportID())) ) {
        	isDashboard = true;
        }
        //boolean isDashboard = (session.getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null);
        ArrayList sheetArrayList = new ArrayList();
        
		Map reportRuntimeMap = null;
		Map reportDataMap = null;
		
		ArrayList reportIDList = new ArrayList();
		
		//Map reportDisplayTypeMap = null;
		if(isDashboard) {
			reportRuntimeMap 	= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
			reportDataMap 		= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
			
			if(reportRuntimeMap!=null) {
				Set setReportRuntime = reportRuntimeMap.entrySet();
				for(Iterator iter = setReportRuntime.iterator(); iter.hasNext(); ) {
					Map.Entry entry 			= (Entry) iter.next();
					ReportRuntime rrDashRep 	= (ReportRuntime) entry.getValue();
					reportIDList.add(rrDashRep.getReportID());
				}
			}
		}
		
		


	    int col = 0;
	    String reportTitle =  (nvl(rr.getReportTitle()).length()>0?rr.getReportTitle():rr.getReportName());
	    String reportDescr = rr.getReportDescr();
	    
	    // Total Columns visible in excel
	    //col = getColumnCountForDownloadFile(rr, rd);

	    int rowNum = 0;

	    
	    XSSFSheet sheet = null;
        //save the template
        String filename = "";
        String extension = "";

        String sheetRef = null;
        
        FileOutputStream os = null; //template file
        File templateFile = null; 
        
        if(isDashboard) {
        	if(reportRuntimeMap!=null) {
        		
		        FileInputStream readTemplate = null;
				//Load customized styles
			    int count = 0;  

		        //If template supplied by Application
	        	String templateFilename = rr.getTemplateFile();
	        	extension = templateFilename.substring(templateFilename.lastIndexOf(".")+1);
	        	filename = formattedReportName+formattedDate+user_id;

   				Set setReportRuntimeWB = reportRuntimeMap.entrySet();
    				for(Iterator iter = setReportRuntimeWB.iterator(); iter.hasNext(); ) {
					count++;
    					Map.Entry entry 			= (Entry) iter.next();
    					ReportRuntime rrDashRep 	= (ReportRuntime) entry.getValue();
					os = new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
					
					if(count==1) {
				        if(nvl(rr.getTemplateFile()).length()>0) {
				        	readTemplate = new FileInputStream(org.openecomp.portalsdk.analytics.system.AppUtils.getExcelTemplatePath()+rr.getTemplateFile());
				        	wb=new XSSFWorkbook(readTemplate);
				        } else {
				        	//copy the os file to new file and open new file in below line
				        	wb=new XSSFWorkbook();
				        }
					} else {
						readTemplate = new FileInputStream(AppUtils.getTempFolderPath()+ filename+"."+ nvls(extension, "xlsx"));
			        	wb=new XSSFWorkbook(readTemplate);
					}
					if(rrDashRep!=null)
    						styles = loadXSSFStyles(rrDashRep, wb, styles);
					String reportSheetName = new HtmlStripper().stripSpecialCharacters(rrDashRep.getReportName());
    					if(nvl(reportSheetName).length()>28)
    						reportSheetName = reportSheetName.substring(0, 28);
					sheet = wb.createSheet(count+"-"+reportSheetName);
			        if(!Globals.printExcelInLandscapeMode())
			        	sheet.getPrintSetup().setLandscape(false);
			        else
			        	sheet.getPrintSetup().setLandscape(true);
		            	wb.write(os);
		            	os.flush();
				        if(nvl(rr.getTemplateFile()).length()>0) {
					        readTemplate.close();
				        } 		            	
		            	os.close();
		            	
				        FileInputStream inF 			= new FileInputStream(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
				        FileOutputStream outStream 		= new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"."+ nvls(extension, "xlsx"));
				        copyStream(inF, outStream);
				        outStream.flush();
				        outStream.close();
				        inF.close();
		            	
    				}
        		
        		FileInputStream xlsIn = null;
    			POIFSFileSystem fileSystem = null;
    			buf = null;
    			FileOutputStream xlsOut = null;
		        formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
		        String xlsFName = "dashboard"+formattedDate+user_id+".xls";

				Set setReportRuntime = reportRuntimeMap.entrySet();
				Set setReportDataMap = reportDataMap.entrySet();
				Iterator iter2 = setReportDataMap.iterator();


	        	//filename = templateFilename.substring(0, templateFilename.lastIndexOf("."))+"_"+formattedDate+user_id; 
	        	
                count = 0;
				for(Iterator iter = setReportRuntime.iterator(); iter.hasNext(); ) {
					count++;
			        
			        Map.Entry entry 			= (Entry) iter.next();
			        Map.Entry entryData 		= (Entry) iter2.next();
					ReportRuntime rrDashRep 	= (ReportRuntime) entry.getValue();
					ReportData rdDashRep 		= (ReportData) entryData.getValue();

					String reportSheetName = new HtmlStripper().stripSpecialCharacters(rrDashRep.getReportName());
					if(nvl(reportSheetName).length()>28)
						reportSheetName = reportSheetName.substring(0, 28);
					sheet = wb.getSheet(count+"-"+reportSheetName);
			        sheetRef = sheet.getPackagePart().getPartName().getName();
			        
			        //Step 2. Generate XML file.
			        File tmp = File.createTempFile("sheet", ".xml");
			        FileOutputStream fileOutTemp = new FileOutputStream(tmp);
			        Writer fw = new OutputStreamWriter(fileOutTemp, XML_ENCODING);
			        String sql_whole = rrDashRep.getWholeSQL();
			        
			        SpreadsheetWriter sw = new SpreadsheetWriter(fw);
			        sw.beginSheet();
			        
			        
			        generate(wb, sw, styles, rdDashRep, sql_whole, rrDashRep, request, sheet);
		            

		            sw.endSheet();
		            
		            fw.flush();
			        fw.close();            
		            fileOutTemp.flush();
		            fileOutTemp.close();

		            
			        //Step 3. Substitute the template entry with the generated data
			        
			        FileOutputStream outF = new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"."+ nvls(extension, "xlsx"));
		        	templateFile =  new File(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
			        substitute(templateFile, tmp, sheetRef.substring(1), outF);
			        outF.flush();
			        outF.close();
			        
			        FileInputStream inF 			= new FileInputStream(AppUtils.getTempFolderPath()+ filename+"."+ nvls(extension, "xlsx"));
			        FileOutputStream outStream 		= new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
			        copyStream(inF, outStream);
			        outStream.flush();
			        outStream.close();
			        inF.close();
				}
         }	
       } else {
        //If template supplied by Application
        if(nvl(rr.getTemplateFile()).length()>0) {
        	String templateFilename = rr.getTemplateFile();
        	extension = templateFilename.substring(templateFilename.lastIndexOf(".")+1);
        	filename = formattedReportName+formattedDate+user_id;
        	//filename = templateFilename.substring(0, templateFilename.lastIndexOf("."))+"_"+formattedDate+user_id; 
        } else
        	filename = formattedReportName+formattedDate+user_id;


        if(nvl(rr.getTemplateFile()).length()<=0) {
        	os = new FileOutputStream(AppUtils.getTempFolderPath()+"template"+formattedDate+user_id+".xlsx");
		    wb=new XSSFWorkbook();
			//Load customized styles
		    if (rr != null)
				styles = loadXSSFStyles(rr, wb, styles);
		    //create data sheet
		    if(isDashboard) {
		    	
		    } else {
		    	
		    }
			String reportSheetName = new HtmlStripper().stripSpecialCharacters(rr.getReportName());
			if(nvl(reportSheetName).length()>28)
				reportSheetName = reportSheetName.substring(0, 28);
			sheet = wb.createSheet(reportSheetName);

            //customized mode
		    if(!Globals.printExcelInLandscapeMode())
		      	sheet.getPrintSetup().setLandscape(false);
		    else
		      	sheet.getPrintSetup().setLandscape(true);
		    //get data sheet name     
		    sheetRef = sheet.getPackagePart().getPartName().getName();
		    wb.write(os);
		    os.flush();
		    //wb = null;
		    os.close();
	        	
	     } else {
	        os = new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
	        FileInputStream readTemplate = new FileInputStream(org.openecomp.portalsdk.analytics.system.AppUtils.getExcelTemplatePath()+rr.getTemplateFile());
		    wb=new XSSFWorkbook(readTemplate);
			if (rr != null)
				styles = loadXSSFStyles(rr, wb, styles);
		        sheet = wb.getSheetAt(0);
		        if(!Globals.printExcelInLandscapeMode())
		        	sheet.getPrintSetup().setLandscape(false);
		        else
		        	sheet.getPrintSetup().setLandscape(true);
		        //sheet = wb.getSheet(getSheetName());
		        sheetRef = sheet.getPackagePart().getPartName().getName();
		        wb.write(os);
		        os.flush();
		        readTemplate.close();
		        //wb = null;
		        os.close();
	       }
	       
	        //Step 2. Generate XML file.
	        File tmp = File.createTempFile("sheet", ".xml");
	        FileOutputStream fileOutTemp = new FileOutputStream(tmp);
	        Writer fw = new OutputStreamWriter(fileOutTemp, XML_ENCODING);
	        
	        //String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
	        String sql_whole = "";
	        sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
	        
	        if(sql_whole==null) {
		    	if (!rr.getReportType().equals(AppConstants.RT_HIVE))
		    		sql_whole = rr.getWholeSQL();
		    	else
		    		sql_whole = rr.getReportSQL();
	        }
	        
	        SpreadsheetWriter sw = new SpreadsheetWriter(fw);
	        
	        sw.beginSheet();
	        
	        if((rd.getDataRowCount() >= rr.getReportDataSize()) && !rr.getReportType().equals(AppConstants.RT_HIVE))  {
	        	sql_whole="";
	        }
	        
	        generate(wb, sw, styles, rd, sql_whole, rr, request, sheet);
            
            sw.endSheet();
            
            fw.flush();
	        fw.close();            
            fileOutTemp.flush();
            fileOutTemp.close();

	        
	        //Step 3. Substitute the template entry with the generated data
	        
	        FileOutputStream outF = new FileOutputStream(AppUtils.getTempFolderPath()+ filename+"."+ nvls(extension, "xlsx"));

	        if(nvl(rr.getTemplateFile()).length()>0) {
	        	templateFile =  new File(AppUtils.getTempFolderPath()+ filename+"T."+ nvls(extension, "xlsx"));
	        } else
	        	templateFile = new File(AppUtils.getTempFolderPath()+"template"+formattedDate+user_id+".xlsx");
	        
	        substitute(templateFile, tmp, sheetRef.substring(1), outF);
	        outF.flush();
	        outF.close();
	        
       }
	        //get servlet output stream
	       

	        response.reset();
	        sos = response.getOutputStream();
	        String mime_type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	        if(extension.equals("xlsm"))
	        	mime_type = "application/vnd.ms-excel.sheet.macroEnabled.12";
	        response.setContentType(mime_type);

			response.setHeader("Content-disposition", "attachment;filename="+filename+"."+ nvls(extension, "xlsx"));
			
			buf = new BufferedInputStream(new FileInputStream(AppUtils.getTempFolderPath()+filename + "."+  nvls(extension, "xlsx")));
		    int readBytes = 0;

		    //read from the file; write to the ServletOutputStream
		    while ((readBytes = buf.read()) != -1)
		        sos.write(readBytes);
		      
		    buf.close();
		    sos.flush();
		    sos.close();
		    logger.debug(EELFLoggerDelegate.debugLogger, ("ENDING..DOWNLOADING XLSX..."));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("##### Heap utilization statistics [MB] #####"));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("Used Memory:"
	    			+ (runtime.totalMemory() - runtime.freeMemory()) / mb));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
	    			+ runtime.freeMemory() / mb));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));
		    logger.debug(EELFLoggerDelegate.debugLogger, ("##### END #####"));
    }

	/**
	*
	* @param zipfile the template file
	* @param tmpfile the XML file with the sheet data
	* @param entry the name of the sheet entry to substitute, e.g. xl/worksheets/sheet1.xml
	* @param out the stream to write the result to
	*/
	private static void substitute(File zipfile, File tmpfile, String entry, OutputStream out) throws IOException {
	   ZipFile zip = new ZipFile(zipfile);
	
	   ZipOutputStream zos = new ZipOutputStream(out);	
	
	   @SuppressWarnings("unchecked")
	   Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zip.entries();
	   while (en.hasMoreElements()) {
	       ZipEntry ze = en.nextElement();
	       if(!ze.getName().equals(entry)){
	           zos.putNextEntry(new ZipEntry(ze.getName()));
	           InputStream is = zip.getInputStream(ze);
	           copyStream(is, zos);
	           is.close();
	       }
	   }
	   zos.putNextEntry(new ZipEntry(entry));
	   InputStream is = new FileInputStream(tmpfile);
	   copyStream(is, zos);
	   zos.flush();    
	   zos.close();
	   is.close();
	   zip.close();
	}
	
	private static void copyStream(InputStream in, OutputStream out) throws IOException {
	   byte[] chunk = new byte[1024];
	   int count;
	   while ((count = in.read(chunk)) >=0 ) {
	     out.write(chunk,0,count);
	   }
	}

 
    public void createCSVFileContent(Writer out, ReportData rd,
			ReportRuntime rr, HttpServletRequest request, HttpServletResponse response)
			throws RaptorException {
    	//ArrayList reportParamNameValues = rr.getParamNameValuePairs();
    	//String reportTitle = rr.getReportName();
    	//String reportDescr = rr.getReportDescr();   	
		PrintWriter csvOut = new PrintWriter(out);
		ServletOutputStream sos = null;
		BufferedInputStream buf = null;
		String fileName = "";
		String formattedReportName = new HtmlStripper().stripSpecialCharacters(rr.getReportName());
	    String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
		String fName = formattedReportName+formattedDate+AppUtils.getUserID(request);
		boolean raw = AppUtils.getRequestFlag(request, "raw");
		String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);

		
		String csvFName 			= fName+".csv";		
		String zipFName 			= fName+".zip";
		if(true) {
			try {
				fileName 				= AppUtils.getTempFolderPath()+""+csvFName;
				csvOut					= new PrintWriter(new BufferedWriter(
				        new OutputStreamWriter(
				                new FileOutputStream(fileName), "UTF-8")), false);
			} catch (FileNotFoundException fex) {
				fex.printStackTrace();
			}
			 catch (UnsupportedEncodingException fex1) {
				 fex1.printStackTrace();
			 }
		}
		HtmlStripper strip = new HtmlStripper();
		ResultSet rs = null;
        //OracleConnection conn = null;
        //OracleStatement st = null;
        //Connection conO = null;
        //Statement stO = null;
		Connection conn = null;
		Statement st = null;
        ResultSetMetaData rsmd = null;
        ColumnHeaderRow chr = null;
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	String valueName = "";
    	if(!raw) {
    		 String reportTitle =  (nvl(rr.getReportTitle()).length()>0?rr.getReportTitle():rr.getReportName());
        	csvOut.println();
			csvOut.print("\"" + reportTitle + "\",");        	
        	csvOut.println();

    		if(Globals.disclaimerPositionedTopInCSVExcel()) {
		        if(Globals.getShowDisclaimer()) {
		        	csvOut.println();
	    			csvOut.print("\"" + Globals.getFooterFirstLine() + "\",");
	    			csvOut.println();
	    			csvOut.print("\"" + Globals.getFooterSecondLine() + "\",");
	    			csvOut.println();
	    			csvOut.println();
	    		}
			}
		}
        if (Globals.getPrintParamsInCSVDownload() && !raw) {
            ArrayList paramsList = rr.getParamNameValuePairsforPDFExcel(request, 1);
            if(paramsList.size()<=0) {
            	paramsList = (ArrayList) request.getSession().getAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
            }
            int paramSeq = 0;
	        for (Iterator iter = paramsList.iterator(); iter.hasNext();) {
	            IdNameValue value = (IdNameValue) iter.next();
	            //System.out.println("\"" + value.getId() + " = " + value.getName() + "\"");
	            if(nvl(value.getId()).trim().length()>0  && (!nvl(value.getId()).trim().equals("BLANK"))) {
	                paramSeq += 1;
	                if(paramSeq <= 1) {
	                	csvOut.print("\"" + "Run-time Parameters" + "\"");
	                	csvOut.println();
	                	//strBuf.append("Run-time Parameters\n");
	                }
	                	csvOut.print("\"" + value.getId() +":" + "\",");
	                	valueName = nvl(value.getName());
	                	if(valueName.indexOf("~")!= -1 && valueName.startsWith("(")) {
	                		csvOut.print("\"'" + valueName.replaceAll("~",",")+ "'\",");
	                	} else {
	                		if(valueName.startsWith("(") && valueName.endsWith(")")) {
	                			csvOut.print("\"" + valueName.replaceAll("~",",").substring(1, valueName.length()-1)+ "\",");
	                	} else	                		
	                		csvOut.print("\"" + valueName.replaceAll("~",",")+ "\",");
	                	}
	                	csvOut.println();
	
	                	//strBuf.append(value.getId()+": "+ value.getName()+"\n");
	               }
	        } //for
	        csvOut.println();
	        csvOut.println();
        }
        
    	System.out.println("##### Heap utilization statistics [MB] #####");
    	System.out.println("Used Memory:"
    			+ (runtime.maxMemory() - runtime.freeMemory()) / mb);
    	System.out.println("Free Memory:"
    			+ runtime.freeMemory() / mb);
    	System.out.println("Total Memory:" + runtime.totalMemory() / mb);
    	System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    	
    	sql_whole = "";
        sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
        
        if(sql_whole==null) {
	    	if (!rr.getReportType().equals(AppConstants.RT_HIVE))
	    		sql_whole = rr.getWholeSQL();
	    	else
	    		sql_whole = rr.getReportSQL();
        }
    	
    	
        if(nvl(sql_whole).length()>0) {
	        try {
	        	conn = ConnectionUtils.getConnection(rr.getDbInfo());
	        	st = conn.createStatement();
	        	//conn.setDefaultRowPrefetch(1000);
	        	//st.setFetchDirection(ResultSet.TYPE_FORWARD_ONLY);
	        	//st.setFetchSize(1000);
	        	System.out.println("************* Map Whole SQL *************");
	        	System.out.println(sql_whole);
	        	System.out.println("*****************************************");
	        	rs = st.executeQuery(sql_whole);
	        	//st.setFetchSize(1000);
	        	rsmd = rs.getMetaData();
	            int numberOfColumns = rsmd.getColumnCount();
	            HashMap colHash = new HashMap();
	            String title = "";
	            
	            if(rd!=null) {
	            	
        			/*if(rd.reportTotalRowHeaderCols!=null) { 
        				csvOut.print("\"" + "#" + "\",");
        			}*/
	        			
		    		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
		    			chr = rd.reportColumnHeaderRows.getNext();
		    			for (chr.resetNext(); chr.hasNext();) {
		    				ColumnHeader ch = chr.getNext();
		    				title = ch.getColumnTitle();
		    				title = Utils.replaceInString(title,"_nl_", " \n");
		    				if(ch.isVisible() && nvl(title).length()>0) {
		    					csvOut.print("\"" + title + "\",");
		    					for (int i = 1; i < ch.getColSpan(); i++)
		    						csvOut.print(",");
		    				}
		    			} // for
		
		    			csvOut.println();
		    		} // for
	    		int rowCount = 0;
	    		while(rs.next()) {
/*	    			if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*Globals.getMemoryThreshold()/100) ) {
	    				csvOut.print(Globals.getUserDefinedMessageForMemoryLimitReached() + " " + rowCount +"records out of " + rr.getReportDataSize() + " were downloaded to CSV.");
	    				break;
	    			}
*/	    			rowCount++;
					//if(!raw) {
		    			colHash = new HashMap();
		    			for (int i = 1; i <= numberOfColumns; i++) {
		    				colHash.put(rsmd.getColumnLabel(i).toUpperCase(), rs.getString(i));
		    			}
		    			/*if(rd.reportDataTotalRow!=null) {
		    				csvOut.print("\"" + rowCount + "\",");
		    			}*/
		    			for (chr.resetNext(); chr.hasNext();) {
		    				ColumnHeader ch = chr.getNext();
		    				title = ch.getColumnTitle();
		    				title = Utils.replaceInString(title,"_nl_", " \n");
		    				
		    				if(ch.isVisible() && nvl(title).length()>0) {
		    					csvOut.print("\"" + strip.stripCSVHtml(nvl((String)colHash.get(ch.getColId().toUpperCase()))) + "\",");
		    				}
		    				
		    			}
		    			csvOut.println();
					/*} else {
						for (int i = 1; i <= numberOfColumns; i++) {
							csvOut.print("\"" + strip.stripCSVHtml( rs.getString(i)) + "\",");
						}
						csvOut.println();
					}*/
	    			
	    		}
	    		
	    		if(rd.reportDataTotalRow!=null) {
					for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();) {
						DataRow dr = rd.reportDataTotalRow.getNext();
						csvOut.print("\"" + "Total" + "\",");
						dr.resetNext();dr.getNext();
		    			for (; dr.hasNext();) {
		    				DataValue dv = dr.getNext();
		                    if(dv.isVisible()) {  
		                    	csvOut.print("\"" + strip.stripCSVHtml(dv.getDisplayValue()) + "\",");
		                    }
		    			} // for
		
		    			csvOut.println();
					}
	    		}
	    		
	    		if(rowCount == 0) {
	    			csvOut.print("\"No Data Found \"");
	    		}
	            } else {
	            	csvOut.print("\"No Data Found \"");
	            }
	            
	        } catch (SQLException ex) { 
	        	throw new RaptorException(ex);
	        } catch (ReportSQLException ex) { 
	        	throw new RaptorException(ex);
	        } catch (Exception ex) {
	        	throw new RaptorException (ex);
	        } finally {
	        	try {
	        		if(conn!=null)
	        			conn.close();
	        		if(st!=null)
	        			st.close();
	        		if(rs!=null)
	        			rs.close();
	        	} catch (SQLException ex) {
	        		throw new RaptorException(ex);
	        	}
	        }
	        
    		if(!raw) {
    			if(!Globals.disclaimerPositionedTopInCSVExcel()) {
			        if(Globals.getShowDisclaimer()) {
		    			csvOut.print("\"" + Globals.getFooterFirstLine() + "\",");
		    			csvOut.println();
		    			csvOut.print("\"" + Globals.getFooterSecondLine() + "\",");
		    			csvOut.println();
		    		}
    			}
    		}
	        
	       // csvOut.flush();
        } else {
    		boolean firstPass = true;
    		if(rd!=null) {
    			if(rd.reportTotalRowHeaderCols!=null) { 
    				csvOut.print("\"" + "#" + "\",");
    			}

    		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
    			chr = rd.reportColumnHeaderRows.getNext();
    			for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
    				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();

    				if (firstPass) 
    					csvOut.print("\"" + rhc.getColumnTitle() + "\"");
    				csvOut.print(",");
    			} // for
    			firstPass = false;

    			for (chr.resetNext(); chr.hasNext();) {
    				ColumnHeader ch = chr.getNext();
    				if(ch.isVisible()) {
    					csvOut.print("\"" + ch.getColumnTitle() + "\",");
    					for (int i = 1; i < ch.getColSpan(); i++)
    						csvOut.print(",");
    				}
    			} // for

    			csvOut.println();
    		} // for

    		firstPass = true;
    		int rowCount = 0;
    		for (rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext();) {
    			if(rd.reportDataTotalRow!=null) {
    				rowCount++;
    				csvOut.print("\"" + rowCount + "\",");
    			}
    			
    			DataRow dr = rd.reportDataRows.getNext();

    			for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
    				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
    				if (firstPass)
    					rhc.resetNext();
    				RowHeader rh = rhc.getNext();

    				csvOut.print("\"" + strip.stripCSVHtml(rh.getRowTitle()) + "\",");
    			} // for
    			firstPass = false;

    			for (dr.resetNext(); dr.hasNext();) {
    				DataValue dv = dr.getNext();
                    if(dv.isVisible())  
                    	csvOut.print("\"" + strip.stripCSVHtml(dv.getDisplayValue()) + "\",");
    			} // for

    			csvOut.println();
    		} // for
    		if(rd.reportDataTotalRow!=null) {
				for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();) {
					DataRow dr = rd.reportDataTotalRow.getNext();
					csvOut.print("\"" + "Total" + "\",");
	    			firstPass = false;
	
	    			for (dr.resetNext(); dr.hasNext();) {
	    				DataValue dv = dr.getNext();
	                    if(dv.isVisible())  
	                    	csvOut.print("\"" + strip.stripCSVHtml(dv.getDisplayValue()) + "\",");
	    			} // for
	
	    			csvOut.println();
				}
    		}
    		
    		if(!raw) {
	    		if(!Globals.disclaimerPositionedTopInCSVExcel()) {
			        if(Globals.getShowDisclaimer()) {
		    			csvOut.print("\"" + Globals.getFooterFirstLine() + "\",");
		    			csvOut.println();
		    			csvOut.print("\"" + Globals.getFooterSecondLine() + "\",");
		    			csvOut.println();
		    		}
	    		}
    		}

            //csvOut.flush();
        } else {
        		csvOut.print("\"No Data Found \"");
        }
      } 
        csvOut.flush();
        csvOut.close();
		
/*
		if (Globals.getPrintTitleInDownload() && reportTitle != null) {
			csvOut.println();
			csvOut.println("\"" + reportTitle + "\"");
			csvOut.println();
			if (Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0) {
				csvOut.println("\"" + reportDescr + "\"");
				csvOut.println();
			}
		} // if

		if (Globals.getPrintParamsInDownload() && reportParamNameValues != null) {
			csvOut.println();
			for (Iterator iter = reportParamNameValues.iterator(); iter.hasNext();) {
				IdNameValue value = (IdNameValue) iter.next();
				csvOut.println("\"" + value.getId() + " = " + value.getName() + "\"");
			} // for
			csvOut.println();
		} // if
*/
        if(true && !raw) {
        	try {
               
                //final int BUFFER = 2048;
                
                //fis.read(buf,0,buf.length);
                int size = 0;
                byte[] buffer = new byte[1024];
                
                //CRC32 crc = new CRC32();
        		//PrintStream fos = new PrintStream(new WriterOutputStream(out));
        		//BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
        		//ZipOutputStream s = new ZipOutputStream(dest);                
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(AppUtils.getTempFolderPath()+""+zipFName));
                FileInputStream fis = new FileInputStream(fileName);
                
                //s.setLevel(6);
                
                ZipEntry entry = new ZipEntry(csvFName);
                //crc.reset();
                zos.putNextEntry(entry);
                
    			// read data to the end of the source file and write it to the zip
    			// output stream.
    			while ((size = fis.read(buffer, 0, buffer.length)) > 0) {
    				zos.write(buffer, 0, size);
    			}
    			
    			zos.closeEntry();
    			fis.close();

    			// Finish zip process
    			zos.close();

          } catch(Exception e) {
             e.printStackTrace();
          } 
        }

        response.reset();
		java.io.File file = null;
		
		if(true && !raw) {
			response.setContentType("application/octet-stream");
			response.setHeader("Content-disposition","attachment;filename="+fName+".zip");
			file = new java.io.File(AppUtils.getTempFolderPath()+""+fName+".zip");
		} else {
			response.setContentType("application/csv");
			response.setHeader("Content-disposition","attachment;filename="+fName+".csv");
			file = new java.io.File(AppUtils.getTempFolderPath()+""+fName+".csv");
		}
		
	   
    	FileInputStream fileIn = null; 
    	int c;
    	try {
    		sos = response.getOutputStream();
    		fileIn = new FileInputStream(file);
    		buf = new BufferedInputStream(fileIn);
    		byte [] bOut = new byte [4096];
		    //read from the file; write to the ServletOutputStream
		    //while ((readBytes = buf.read()) != -1)
    		int readBytes = 0;
		    while ((readBytes = buf.read (bOut, 0, 4096))> 0) {
		    	buf.available(); 
		    	sos.write (bOut, 0, readBytes); 
		    }
	    		 
    	}  catch (IOException ex) { 
    		 ex.printStackTrace(); 
    	}
    	catch(Exception e) {
			e.printStackTrace();
    	}  finally {
    		 try {
		      if (sos != null)
			        sos.close();
			      if (buf != null)
			        buf.close();
			    } catch (Exception e1) {
			    	e1.printStackTrace();
			    }
    	}
				
		        File f = new File (AppUtils.getTempFolderPath()
							+ fName); 
		        if(f.exists()) f.delete();
    	System.out.println("##### Heap utilization statistics [MB] #####");
    	System.out.println("Used Memory:"
    			+ (runtime.maxMemory() - runtime.freeMemory()) / mb);
        logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
    			+ runtime.freeMemory() / mb));
        logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
        logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));
        
	} // createCSVFileContent

/*	public String saveCSVPageFile(HttpServletRequest request, ReportData rd,
			ArrayList reportParamNameValues, String reportTitle, String reportDescr) {
		try {
	        String formattedReportName = new HtmlStripper().stripSpecialCharacters(reportTitle);
	        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
			
			String csvFName = formattedReportName+formattedDate+AppUtils.getUserID(request)+".csv";
			//String csvFName = AppUtils.generateFileName(request, AppConstants.FT_CSV);

			BufferedWriter csvOut = new BufferedWriter(new FileWriter(AppUtils
					.getTempFolderPath()
					+ csvFName));
			createCSVFileContent(csvOut, rd, reportParamNameValues, reportTitle, reportDescr);
			csvOut.close();

			return csvFName;
		} catch (Exception e) {
			(new ErrorHandler()).processError(request, "Exception saving data to CSV file: "
					+ e.getMessage());
			return null;
		}
	} // saveCSVPageFile
*/

//	public String saveAsFlatFile(HttpServletRequest request, ReportData rd,
//            ReportRuntime rr, String reportTitle, String reportDescr) {
//		try {
//			String csvFName = AppUtils.generateFileName(request, AppConstants.FT_TXT);
//
//			BufferedWriter txtOut = new BufferedWriter(new FileWriter(AppUtils
//					.getTempFolderPath()
//					+ csvFName));
//			createFlatFileContent(txtOut, rd, rr, reportTitle, reportDescr);
//			txtOut.close();
//
//			return csvFName;
//		} catch (Exception e) {
//			(new ErrorHandler()).processError(request, "Exception saving data to CSV file: "
//					+ e.getMessage());
//			return null;
//		}
//	} // saveCSVPageFile

	public String saveXMLFile(HttpServletRequest request, String reportName, String reportXML) {
		try {
			String xmlFName = AppUtils.generateUniqueFileName(request, reportName, AppConstants.FT_XML);
            
			PrintWriter xmlOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(
					AppUtils.getTempFolderPath() + xmlFName))));
			xmlOut.println(reportXML);
			xmlOut.close();

			//return AppUtils.getTempFolderURL()
			//		+ java.net.URLEncoder.encode(java.net.URLDecoder.decode(xmlFName));
            return java.net.URLEncoder.encode(java.net.URLDecoder.decode(xmlFName));
                
		} catch (Exception e) {
			(new ErrorHandler()).processError(request,
					"Exception saving XML source to file system: " + e.getMessage());
			return null;
		}
	} // saveXMLFile

	public ReportRuntime loadReportRuntime(HttpServletRequest request, String reportID)
			throws RaptorException {
		return loadReportRuntime(request, reportID, true);
	} // loadReportRuntime

	public ReportRuntime loadReportRuntime(HttpServletRequest request, String reportID,
			boolean prepareForExecution) throws RaptorException {
		return loadReportRuntime(request, reportID, true, 2); // where 2 is adding to session
	}
	public ReportRuntime loadReportRuntime(HttpServletRequest request, String reportID,
			boolean prepareForExecution, int requestFlag) throws RaptorException {
        boolean refresh = nvl(request.getParameter(AppConstants.RI_REFRESH)).toUpperCase().startsWith("Y");
		boolean rDisplayContent = AppUtils.getRequestFlag(request,
				AppConstants.RI_DISPLAY_CONTENT)
				|| AppUtils.getRequestFlag(request, "noFormFields");

		ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
		boolean inSchedule = AppUtils.getRequestFlag(request, AppConstants.SCHEDULE_ACTION);		
		if (rr != null ) {
			if(requestFlag == 7) { // DASH
				String reportXML = ReportLoader.loadCustomReportXML(reportID);
				logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report [" + reportID + "]: XML loaded"));
				rr = ReportRuntime.unmarshal(reportXML, reportID, request);
				rr.setParamValues(request, false,refresh);	
				rr.setDisplayFlags(true, true); // show content even at the first time				
				return rr;
			} else {
				logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Load Report Runtime "+ reportID + " " +rr.getReportID() + " " + request.getParameter("refresh") ));
			if (reportID.equals(rr.getReportID()) && (request.getParameter("refresh")==null || !request.getParameter("refresh").equals("Y"))) {
				// The report runtime is already in the session
				if (prepareForExecution) {
					boolean resetParams = AppUtils.getRequestFlag(request,
							AppConstants.RI_RESET_PARAMS);
                    rr.setParamValues(request, resetParams,refresh);
                    
                    if (resetParams)
						rr.resetVisualSettings();
					rr.setDisplayFlags(nvl(request.getParameter(AppConstants.RI_SOURCE_PAGE))
							.length() == 0, rDisplayContent || rr.isDisplayOptionHideForm());
				} // if
                
				return rr;
			} // if
		   }
        }

		/*
		 * Cannot convert the definition => XML file not saved for preview also,
		 * commented code not maintained up to date ReportDefinition rdef =
		 * (ReportDefinition)
		 * request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		 * if(rdef!=null) if(reportID.equals(rdef.getReportID())) { // The
		 * report definition is in the session => create report runtime from it
		 * rr = new ReportRuntime(rdef, request); if(prepareForExecution) {
		 * request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME,
		 * rr);
		 * rr.setDisplayFlags(request.getParameter(AppConstants.RI_SOURCE_PAGE)==null); } //
		 * if return rr; } // if
		 */

		// Report is NOT in the session => load from the database
		String reportXML = ReportLoader.loadCustomReportXML(reportID);
		logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report [" + reportID + "]: XML loaded"));

		rr = ReportRuntime.unmarshal(reportXML, reportID, request);
		if (prepareForExecution) {
			String userID ;
			int flag = 0;
			if(request.getAttribute("schedule_email_userId") != null) {
				userID = (String)request.getAttribute("schedule_email_userId");
				flag = 1;
			}
			else 
				userID = AppUtils.getUserID(request);
			// If it is dashboard type then report can be viewed without specific privilege to report
			String dashboardId = AppUtils.getRequestValue(request, AppConstants.RI_DASHBOARD_ID);
			//System.out.println("USSSSSSSSSSSSERID " + userID);
			//System.out.println("PDF " + AppUtils.getRequestNvlValue(request, "pdfAttachmentKey") );
            if(!rr.isDashboardType() && !(isReportAddedAsDashboard(request, dashboardId, rr.getReportID())))  {
            	if ( AppUtils.getRequestNvlValue(request, "pdfAttachmentKey").length()<=0 )
            		if(flag == 1 )rr.checkUserReadAccess(request, userID);
            		else   rr.checkUserReadAccess(request);
            }
            // TODO ON Demand
			//rr.setXmlFileName(saveXMLFile(request, rr.getReportName(), reportXML));
			if (rDisplayContent) {
            	//System.out.println("In rDisplayContent ");
				rr.setParamValues(request, false,true);
				//if (requestFlag==2)
					request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
			}
			if(inSchedule) {
            	//System.out.println("In inSchedule ");
				rr.setParamValues(request, false,false);
			}
			 if( requestFlag == 7 ) {	// DASH
				 rr.setDisplayFlags(true, true);
			 } else {
				 rr.setDisplayFlags(request.getParameter(AppConstants.RI_SOURCE_PAGE) == null,
						 rDisplayContent || rr.isDisplayOptionHideForm());
			 }
//			System.out.println("Report ID B4 Id in reportHandler " 
//					+ ( request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null?((ReportRuntime)request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID():"Not in session"));
//			System.out.println("requestFlag  " + requestFlag);
            if(requestFlag==2 && !rDisplayContent) {
            	//System.out.println("In Request Flag ");
            	request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
            	rr.setParamValues(request, false, false);
            }
            else if(requestFlag==1) {
            	rr.setParamValues(request, false,refresh);
            	request.setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
            	
            } 
//			System.out.println("Report ID B4 Id in reportHandler " 
//					+ ( request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null?((ReportRuntime)request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID():"Not in session"));
			//request.setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
		} // if

		return rr;
	} // loadReportRuntime

	private boolean isReportAddedAsDashboard(HttpServletRequest request, String dashboardId, String reportId)throws RaptorException {
		if(nvl(dashboardId).length() <= 0)
			return false;
		String reportXML = ReportLoader.loadCustomReportXML(dashboardId);
		ReportDefinition rdef = createReportDefinition(request, dashboardId, reportXML);
		List l = rdef.getDashBoardReports().getReportsList();
        for (Iterator iterator = l.iterator(); iterator.hasNext();) {
			Reports reports = (Reports) iterator.next();
			if(reports.getReportId().equals(reportId)) return true;
			
		}
		return false;
	}
	
	public ReportDefinition createReportDefinition(HttpServletRequest request,
			String reportID, String reportXML) throws RaptorException {
		ReportDefinition rdef = ReportDefinition.unmarshal(reportXML, reportID, request);
		rdef.generateWizardSequence(request);
		return rdef;
	} // createReportDefinition

	public ReportDefinition loadReportDefinition(HttpServletRequest request, String reportID)
			throws RaptorException {
		//System.out.println("********* ReportID  " + reportID);
		boolean isReportIDBlank = (reportID.length() == 0 || reportID.equals("-1"));
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), "");
		String wizardActionKey = nvl(request.getParameter(AppConstants.RI_WIZARD_ACTION), "");
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		if(nvl(actionKey).equals("report.edit")) 
			rdef = null;
		//ReportDefinition rdef = null;
		if (rdef != null)
			if (isReportIDBlank || reportID.equals(rdef.getReportID())) {
				// The report definition is already in the session
				return rdef;
			}

		ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
		if (rr != null)
			if (isReportIDBlank || reportID.equals(rr.getReportID())) {
				// The report runtime is in the session => create report
				// definition from it
				rdef = new ReportDefinition(rr, request);
				String userID = AppUtils.getUserID(request);
				rdef.generateWizardSequence(request);
				// rdef.checkUserWriteAccess(userID);

				request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
				return rdef;
			} // if

		// Report is NOT in the session => load from the database
		if (isReportIDBlank)
			rdef = ReportDefinition.createBlank(request);
		else {
			String reportXML = ReportLoader.loadCustomReportXML(reportID);
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report [" + reportID + "]: XML loaded"));
			rdef = createReportDefinition(request, reportID, reportXML);
		} // else

		request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
		return rdef;
	} // loadReportDefinition

    public void setSheetName( String sheet_name ) {
       SHEET_NAME = sheet_name;    
    }
    
    public String getSheetName() {
        return SHEET_NAME;
    }

   /**
    * Writes spreadsheet data in a Writer.
    * (YK: in future it may evolve in a full-featured API for streaming data in Excel)
    */
   public static class SpreadsheetWriter {
       private final Writer _out;
       private int _rownum;

       public SpreadsheetWriter(Writer out){
           _out = out;
       }

       public void beginSheet() throws IOException {
           _out.write("<?xml version=\"1.0\" encoding=\""+XML_ENCODING+"\"?>" +
                   "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">" );
           _out.write("<sheetData>\n");
       }

       public void endSheet() throws IOException {
           _out.write("</sheetData>");
           _out.write("</worksheet>");
       }

       /**
        * Insert a new row
        *
        * @param rownum 0-based row number
        */
       public void insertRow(int rownum) throws IOException {
           _out.write("<row r=\""+(rownum+1)+"\">\n");
           this._rownum = rownum;
       }

       /**
        * Insert row end marker
        */
       public void endRow() throws IOException {
           _out.write("</row>\n");
       }

       public void createCell(int columnIndex, String value, int styleIndex) throws IOException {
           String ref = new CellReference(_rownum, columnIndex).formatAsString();
           _out.write("<c r=\""+ref+"\" t=\"inlineStr\"");
           if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
           _out.write(">");
           _out.write("<is><t>"+value+"</t></is>");
           _out.write("</c>");
       }

       public void createCell(int columnIndex, String value) throws IOException {
           createCell(columnIndex, value, -1);
       }

       public void createCell(int columnIndex, double value, int styleIndex) throws IOException {
           String ref = new CellReference(_rownum, columnIndex).formatAsString();
           _out.write("<c r=\""+ref+"\" t=\"n\"");
           if(styleIndex != -1) _out.write(" s=\""+styleIndex+"\"");
           _out.write(">");
           _out.write("<v>"+value+"</v>");
           _out.write("</c>");
       }

       public void createCell(int columnIndex, double value) throws IOException {
           createCell(columnIndex, value, -1);
       }

       public void createCell(int columnIndex, Calendar value, int styleIndex) throws IOException {
           createCell(columnIndex, DateUtil.getExcelDate(value, false), styleIndex);
       }
   }

	public int getColumnCountForDownloadFile(ReportRuntime rr, ReportData rd) {
		int columnCount = 0;
		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
			ColumnHeaderRow chr = rd.reportColumnHeaderRows.getNext();
			for(chr.resetNext(); chr.hasNext(); ) {
				ColumnHeader ch = chr.getNext();
				if(ch.isVisible()) { 
					columnCount++;
				}
			}
		}
		if(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) {
			for (rd.reportRowHeaderCols.resetNext(); rd.reportRowHeaderCols.hasNext();) {
				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
					if(rhc.isVisible()) { 
						columnCount++;
					}
			}
		}
		return columnCount;
	}
    

	private Map<String, XSSFCellStyle> loadXSSFStyles(ReportRuntime rr, XSSFWorkbook wb, Map<String, XSSFCellStyle> loadedStyles) {
		XSSFCellStyle styleDefault = wb.createCellStyle();
        //System.out.println("Load Styles");
		// Style default will be normal with no background
		XSSFFont fontDefault = wb.createFont();
		
		XSSFDataFormat xssffmt = wb.createDataFormat();
		// The default will be plain .
		fontDefault.setColor((short) HSSFFont.COLOR_NORMAL);
		fontDefault.setFontHeight((short) (font_size / 0.05));
		fontDefault.setFontName("Tahoma");

		styleDefault.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		styleDefault.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleDefault.setBorderRight(XSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleDefault.setFillPattern(XSSFCellStyle.NO_FILL);
		styleDefault.setFont(fontDefault);
        ArrayList semColumnList = new ArrayList();
        List dsList = rr.getDataSourceList().getDataSource();
        for (Iterator iter = dsList.iterator(); iter.hasNext();) {
            DataSourceType element = (DataSourceType) iter.next();
            List dcList = element.getDataColumnList().getDataColumn();
            for (Iterator iterator = dcList.iterator(); iterator.hasNext();) {
                DataColumnType element1 = (DataColumnType) iterator.next();
                semColumnList.add(element1.getSemaphoreId());
                
            }
        }
		SemaphoreList semList = rr.getSemaphoreList();
		Map<String, XSSFCellStyle> hashMapStyles = new HashMap<String, XSSFCellStyle>();;
		Map<String, XSSFFont> hashMapFonts = new HashMap<String, XSSFFont>();
		hashMapFonts.put("default", fontDefault);
		hashMapStyles.put("default", styleDefault);
		XSSFCellStyle styleLeftDefault = wb.createCellStyle();
		styleLeftDefault.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		styleLeftDefault.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		styleLeftDefault.setBorderTop(XSSFCellStyle.BORDER_THIN);
		styleLeftDefault.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		styleLeftDefault.setBorderRight(XSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
		styleLeftDefault.setFillPattern(XSSFCellStyle.NO_FILL);
		styleLeftDefault.setFont(fontDefault);
		hashMapStyles.put("defaultLeft", styleLeftDefault);
		
		
        XSSFCellStyle styleDate = wb.createCellStyle();
        styleDate.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        styleDate.setDataFormat(xssffmt.getFormat("d-mmm-yy"));
        styleDate.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        styleDate.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        styleDate.setBorderTop(XSSFCellStyle.BORDER_THIN);
        styleDate.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        styleDate.setBorderRight(XSSFCellStyle.BORDER_THIN);
		// styleDefault.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleDate.setFillPattern(XSSFCellStyle.NO_FILL);
        styleDate.setFont(fontDefault);
        hashMapStyles.put("date", styleDate);
        
	    XSSFCellStyle rowHeaderStyle = wb.createCellStyle();
	    XSSFFont headerFont = wb.createFont();
	    headerFont.setBold(true);
	    rowHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    rowHeaderStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
	    rowHeaderStyle.setFont(headerFont);
	    rowHeaderStyle.setBorderTop(XSSFCellStyle.BORDER_THIN); 
	    rowHeaderStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN); 
	    rowHeaderStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); 
	    rowHeaderStyle.setBorderRight(XSSFCellStyle.BORDER_THIN); 
	    hashMapStyles.put("header", rowHeaderStyle);
	       

	    XSSFCellStyle boldStyle = wb.createCellStyle();
	    //headerFont = wb.createFont();
	    //headerFont.setBold(true);
	    boldStyle.setFont(headerFont);
	    boldStyle.setBorderTop(XSSFCellStyle.BORDER_THIN); 
	    boldStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN); 
	    boldStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN); 
	    boldStyle.setBorderRight(XSSFCellStyle.BORDER_THIN); 
	    boldStyle.setAlignment(HorizontalAlignment.CENTER);
	    hashMapStyles.put("title", boldStyle);
	    
	    XSSFCellStyle cellStyle = null;
		if (semList == null || semList.getSemaphore() == null) {
			hashMapStyles.put("default", styleDefault);
		} /*else {
			for (Iterator iter = semList.getSemaphore().iterator(); iter.hasNext();) {
				SemaphoreType sem = (SemaphoreType) iter.next();
                if(!semColumnList.contains(sem.getSemaphoreId())) continue;
                //System.out.println("SemphoreId ----> " + sem.getSemaphoreId());
				FormatList fList = sem.getFormatList();
				List formatList = fList.getFormat();
				for (Iterator fIter = formatList.iterator(); fIter.hasNext();) {
					FormatType fmt = (FormatType) fIter.next();
					if(fmt!=null){
					//if (fmt.getLessThanValue().length() > 0) {
						cellStyle = wb.createCellStyle();
						XSSFFont cellFont = wb.createFont();
                        //System.out.println("Format Id " + fmt.getFormatId());
						if (nvl(fmt.getBgColor()).length() > 0) {
//							 System.out.println("Load Styles " +
//							 fmt.getFormatId()
//							 + " " +fmt.getBgColor() + " " +
//							 ExcelColorDef.getExcelColor(fmt.getBgColor()));
							cellStyle.setFillForegroundColor(ExcelColorDef.getExcelColor(fmt
									.getBgColor()));
							cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
						}
						if (nvl(fmt.getFontColor()).length() > 0) {
							cellFont.setColor(ExcelColorDef.getExcelColor(fmt.getFontColor()));
						} else
							cellFont.setColor((short) HSSFFont.COLOR_NORMAL);
						if (fmt.isBold())
							cellFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
						if (fmt.isItalic())
							cellFont.setItalic(true);
						if (fmt.isUnderline())
							cellFont.setUnderline(HSSFFont.U_SINGLE);
						if(nvl(fmt.getFontFace()).length()>0)
							cellFont.setFontName(fmt.getFontFace());
						else
							cellFont.setFontName("Tahoma");
						//cellFont.setFontHeight((short) (10 / 0.05));
						
						if(nvl(fmt.getFontSize()).length()>0) {
						  try {	
						    cellFont.setFontHeight((short) (Integer.parseInt(fmt.getFontSize()) / 0.05));
						  } catch(NumberFormatException e){
						   cellFont.setFontHeight((short) (font_size / 0.05));
						  }
						}
						else
						  cellFont.setFontHeight((short) (font_size / 0.05));
						cellStyle.setFont(cellFont);
						cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
						cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
						cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
						hashMapStyles.put(fmt.getFormatId(), cellStyle);
					} else {
						//hashMapStyles.put(fmt.getFormatId(), styleDefault);
						hashMapStyles.put("default", styleDefault);
					}
				}

			}
		}*/
		loadedStyles.putAll(hashMapStyles);
		return loadedStyles;
	}
   
	private void generate(XSSFWorkbook wb, SpreadsheetWriter sw, Map<String, XSSFCellStyle> styles, ReportData rd, String sql_whole, ReportRuntime rr, HttpServletRequest request, XSSFSheet sheet) throws Exception {
        HtmlStripper strip = new HtmlStripper();
        XSSFCellStyle styleCell = null;
        XSSFCellStyle styleRowCell = null;
        XSSFCellStyle styleDefaultCell = null;
        
        styleDefaultCell = (XSSFCellStyle) styles.get("default");
        
        
        // to check performance
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	
        int rowNum = 0;
		/*short s1 = 0, s2 = (short) (col-1);
        rowNum += 1;
        sw.insertRow(rowNum);
        int styleIndex = styles.get("header").getIndex();
        sw.createCell(rowNum, reportTitle, styleIndex);
	
		//header.setCenter(HSSFHeader.font("Tahoma", "")+ HSSFHeader.fontSize((short) 9)+reportTitle+"\n"+((Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0)?reportDescr:""));

		// Report Description
		if (Globals.getShowDescrAtRuntime() && nvl(reportDescr).length() > 0) {
			sw.createCell(rowNum, reportDescr, styleIndex);
		}
        rowNum += 2;
        sw.insertRow(rowNum);*/
        int cellNum = 0;
        
        
        ColumnHeaderRow chr = null;
        java.util.HashMap dataTypeMap = new java.util.HashMap();
        boolean firstPass = true;
		int columnRows = rr.getVisibleColumnCount() ;
		
		HttpSession session = request.getSession();
		String drilldown_index = (String) session.getAttribute("drilldown_index");
		int index = 0;
		try {
		 index = Integer.parseInt(drilldown_index);
		} catch (NumberFormatException ex) {
			index = 0;
		}
		String header = (String) session.getAttribute("TITLE_"+index);
		String subtitle = (String) session.getAttribute("SUBTITLE_"+index);
		if(nvl(header).length()>0) {
			header = Utils.replaceInString(header, "<BR/>", " ");
			header = Utils.replaceInString(header, "<br/>", " ");
			header = Utils.replaceInString(header, "<br>", " ");
			header  = strip.stripHtml(nvl(header).trim());
			subtitle = Utils.replaceInString(subtitle, "<BR/>", " ");
			subtitle = Utils.replaceInString(subtitle, "<br/>", " ");
			subtitle = Utils.replaceInString(subtitle, "<br>", " ");
			subtitle  = strip.stripHtml(nvl(subtitle).trim());
			//XSSFRow row = sheet.createRow(rowNum);
			sw.insertRow(rowNum);
			cellNum = 0;
			//XSSFCell cell = row.createCell(cellNum);
			sw.createCell(cellNum, Utils.excelEncode(header));
			for (int i = 1; i <= columnRows; i++) {
				sw.createCell(cellNum+i, "");
			}
			sheet.addMergedRegion(new CellRangeAddress(rowNum+1, rowNum+1, cellNum+1, columnRows));
			sw.endRow();
/*			cell.setCellValue(Utils.excelEncode(header));
			cell.setCellStyle(styles.get("title"));
*/			//sw.createCell(cellNum,Utils.excelEncode(header), styles.get("title").getIndex());
//			sheet.addMergedRegion(new CellRangeAddress(rowNum+1, rowNum+1, cellNum+1, columnRows));
			rowNum += 1;
//			row = sheet.createRow(rowNum);
			sw.insertRow(rowNum);
			cellNum = 0;
/*			cell = row.createCell(cellNum);
			cell.setCellValue(Utils.excelEncode(subtitle));
			cell.setCellStyle(styles.get("title"));
*/			//sw.createCell(cellNum,Utils.excelEncode(header), styles.get("title").getIndex());
			
			sheet.addMergedRegion(new CellRangeAddress(rowNum+1, rowNum+1, cellNum+1, columnRows));
			sw.createCell(cellNum, Utils.excelEncode(subtitle));
			sw.endRow();
			//sheet.addMergedRegion(new CellRangeAddress(rowNum+1, rowNum+1, cellNum+1, columnRows));
/*			sw.insertRow(rowNum);
			cellNum = 0;
			sw.createCell(cellNum,Utils.excelEncode(subtitle), styles.get("title").getIndex());
			sheet.addMergedRegion(new CellRangeAddress(rowNum+1, rowNum+1, cellNum+1, columnRows));
			
*/			rowNum += 1;
		}
		cellNum = 0;
		String title = "";
		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
			 sw.insertRow(rowNum);
			 cellNum = -1;
	    	 /*if(rd.reportTotalRowHeaderCols!=null) { 
				cellNum +=1;
				sw.createCell(cellNum, "No.", styles.get("header").getIndex());
				
				//row.getCell((short) cellNum).setCellStyle(styleDataHeader);
			 }*/
			chr = rd.reportColumnHeaderRows.getNext(); 
			
			if(nvl(sql_whole).length() <= 0 || (!rr.getReportType().equals(AppConstants.RT_LINEAR))) {
	    		
				for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
	                cellNum += 1;
					RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
					title = rhc.getColumnTitle();
					title = Utils.replaceInString(title,"_nl_", " \n");
				    
  				   sw.createCell(cellNum,Utils.excelEncode(title), styles.get("header").getIndex());
						 //sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum+columnRows, (short) (cellNum)));
						 //System.out.println(" **************** Row Header Title " + rhc.getColumnTitle() + " " + cellNum + " " );
						 //System.out.println(cellNum  + " " + cellWidth.size());
				} // for

		 }
			
		firstPass = false;
		for (chr.resetNext(); chr.hasNext();) {
			ColumnHeader ch = chr.getNext();
			if(ch.isVisible()) {
                cellNum += 1;
				int colSpan = ch.getColSpan()-1;
				title = ch.getColumnTitle();
				title = Utils.replaceInString(title,"_nl_", " \n");
				sw.createCell(cellNum, Utils.excelEncode(title), styles.get("header").getIndex());
				if(colSpan > 0) {
					for ( int k = 1; k <= colSpan; k++ ) {
						sw.createCell(cellNum+k, "", styles.get("header").getIndex());
					}
					//sheet.addMergedRegion(new Region(rowNum, (short) cellNum, rowNum, (short) (cellNum+colSpan)));
				}
               	if(colSpan > 0)                 
                	cellNum += colSpan;
				}
			} // for
			rowNum += 1;
		} // for
		
		sw.endRow();
		//All the possible combinations of date format
        CreationHelper createHelper = wb.getCreationHelper();
        HashMap<String, Short> dateFormatMap = new HashMap<String, Short>();

        SimpleDateFormat MMDDYYYYFormat   = new SimpleDateFormat("MM/dd/yyyy"); 
        SimpleDateFormat YYYYMMDDFormat   = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat MONYYYYFormat    = new SimpleDateFormat("MMM yyyy");
        SimpleDateFormat MMYYYYFormat     = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat MMMMMDDYYYYFormat = new SimpleDateFormat("MMMMM dd, yyyy");
        SimpleDateFormat YYYYMMDDDASHFormat   = new SimpleDateFormat("yyyy-MM-dd"); 
        SimpleDateFormat timestampFormat   = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        SimpleDateFormat DDMONYYYYFormat    = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat MONTHYYYYFormat    = new SimpleDateFormat("MMMMM, yyyy");
        SimpleDateFormat MMDDYYYYHHMMSSFormat   = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat MMDDYYYYHHMMFormat   = new SimpleDateFormat("MM/dd/yyyy HH:mm");        
        SimpleDateFormat YYYYMMDDHHMMSSFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat YYYYMMDDHHMMFormat   = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat DDMONYYYYHHMMSSFormat    = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        SimpleDateFormat DDMONYYYYHHMMFormat    = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        SimpleDateFormat DDMONYYHHMMFormat    = new SimpleDateFormat("dd-MMM-yy HH:mm");
        SimpleDateFormat MMDDYYFormat   = new SimpleDateFormat("MM/dd/yy");        
        SimpleDateFormat MMDDYYHHMMFormat    = new SimpleDateFormat("MM/dd/yy HH:mm");
        SimpleDateFormat MMDDYYHHMMSSFormat    = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        SimpleDateFormat MMDDYYYYHHMMZFormat    = new SimpleDateFormat("MM/dd/yyyy HH:mm z");
        SimpleDateFormat MMMMMDDYYYYHHMMSS    = new SimpleDateFormat("MMMMM-dd-yyyy HH:mm:ss");
        
        short dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yyyy");
        dateFormatMap.put("MMDDYYYY", new Short(dateFormat)); 
        dateFormat = createHelper.createDataFormat().getFormat("yyyy/MM/dd");
        dateFormatMap.put("YYYYMMDD", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MMM yyyy");
        dateFormatMap.put("MONYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/yyyy");
        dateFormatMap.put("MMYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MMMMM dd, yyyy");
        dateFormatMap.put("MMMMMDDYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd");
        dateFormatMap.put("YYYYMMDDDASH", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatMap.put("timestamp", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy");
        dateFormatMap.put("MONTHYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MMMMM, yyyy");
        dateFormatMap.put("MMDDYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yyyy HH:mm:ss");
        dateFormatMap.put("MMDDYYYYHHMM", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yyyy HH:mm");
        dateFormatMap.put("MMDDYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("yyyy/MM/dd HH:mm:ss");
        dateFormatMap.put("YYYYMMDDHHMMSS", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("yyyy/MM/dd HH:mm");
        dateFormatMap.put("YYYYMMDDHHMM", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy HH:mm:ss");
        dateFormatMap.put("DDMONYYYYHHMMSS", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy HH:mm");
        dateFormatMap.put("DDMONYYYYHHMM", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yy HH:mm");
        dateFormatMap.put("DDMONYYHHMM", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("dd-MMM-yyyy");
        dateFormatMap.put("DDMONYYYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yy");
        dateFormatMap.put("MMDDYY", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yy HH:mm");
        dateFormatMap.put("MMDDYYHHMM", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yy HH:mm:ss");
        dateFormatMap.put("MMDDYYHHMMSS", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MM/dd/yyyy HH:mm z");
        dateFormatMap.put("MMDDYYYYHHMMZ", new Short(dateFormat));
        dateFormat = createHelper.createDataFormat().getFormat("MMMMM-dd-yyyy HH:mm:ss");
        dateFormatMap.put("MMMMMDDYYYYHHMMSS", new Short(dateFormat));
        
		ResultSet rs = null;
        Connection conn = null;
        Statement st = null;
        ResultSetMetaData rsmd = null;


    	if(nvl(sql_whole).length() >0 && (rr.getReportType().equals(AppConstants.RT_LINEAR) || rr.getReportType().equals(AppConstants.RT_HIVE) )) {
        try {
	        	conn = ConnectionUtils.getConnection(rr.getDbInfo());
	        	st = conn.createStatement();
	        	logger.debug(EELFLoggerDelegate.debugLogger, ("************* Map Whole SQL *************"));
	        	logger.debug(EELFLoggerDelegate.debugLogger, (sql_whole));
	        	logger.debug(EELFLoggerDelegate.debugLogger, ("*****************************************"));
	        	rs = st.executeQuery(sql_whole);
	        	rsmd = rs.getMetaData();
	            int numberOfColumns = rsmd.getColumnCount();
	            HashMap colHash = new HashMap();
	            DataRow dr = null;
	            int j = 0;
	            int rowCount = 0;
	       		while(rs.next()) {
       			
	       			rowCount++;
	       			
	                if(rowCount%10000 == 0) {
	                	// to check performance
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("Performance check for "+rowCount+" starting**************"));
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("##### Heap utilization statistics [MB] #####"));
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("Used Memory:"
	                			+ (runtime.totalMemory() - runtime.freeMemory()) / mb));
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
	                			+ runtime.freeMemory() / mb));
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
	                	logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));
	                	System.out.println(rowCount+"TH ROW****##### END #####");
	                	
	                	//
	                }
	       			sw.insertRow(rowNum);
	       			cellNum = -1;
	    			colHash = new HashMap();
	    			for (int i = 1; i <= numberOfColumns; i++) {
	    				colHash.put(rsmd.getColumnLabel(i).toUpperCase(), strip.stripHtml(rs.getString(i)));
	    			}
	    			rd.reportDataRows.resetNext();
	    			dr = rd.reportDataRows.getNext();
	       			styleRowCell = null;
					if (dr.isRowFormat() && styles != null) 	    				
						styleRowCell = (XSSFCellStyle) styles.get(nvl(/*dr.getFormatId(),*/"","default"));		    			
	    			j = 0;
	    			//if(rowCount%1000 == 0) wb.write(sos);
	    			
	    			/*if(rd.reportTotalRowHeaderCols!=null) {
	    				//cellNum = -1;
	    				//for (rd.reportRowHeaderCols.resetNext(); rd.reportRowHeaderCols.hasNext();) {
	    	                cellNum += 1;
	    					//RowHeaderCol rhc = rd.reportRowHeaderCols.getRowHeaderCol(0);
	    					//if (firstPass)
	    					//	rhc.resetNext();
	    					//RowHeader rh = rhc.getRowHeader(rowCount-1);
	    	                sw.createCell(cellNum, rowCount, styleDefaultCell.getIndex());
	
	    				//} // for
	    			}*/
    				firstPass = false;
    				//cellNum = -1;
	    			for (dr.resetNext(); dr.hasNext();j++) {
	    				styleCell = null;
	    			//for (chr.resetNext(); chr.hasNext();) {
	    				//ColumnHeader ch = chr.getNext();
	    				DataValue dv = dr.getNext();
	    				HtmlFormatter htmlFormat = dv.getCellFormatter();

    					if (htmlFormat != null && dv.getFormatId() != null && styles != null) 
    						styleCell = (XSSFCellStyle) styles.get(nvl(/*dv.getFormatId()*/"","default"));
	    				String value = nvl((String)colHash.get(dv.getColId().toUpperCase()));
	                    
	    				boolean bold = false;
	    				
	    				if(dv.isVisible()) {
	    					cellNum += 1;
	    	                //System.out.println("Stripping HTML 1");
	    					//cell.setCellValue(strip.stripHtml(dv.getDisplayValue()));
	    					String dataType = (String) (dataTypeMap.get(dv.getColId()));
	    					//System.out.println("Value " + value + " " + (( dataType !=null && dataType.equals("DATE")) || (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date"))) ); 
	    					if (dataType!=null && dataType.equals("NUMBER")){ 
	    						//cellNumber = row.createCell((short) cellNum);
	    						//cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    						//cellNumber.setCellValue(dv.getDisplayValue());
	    						//cellCurrencyNumber = row.createCell((short) cellNum);
	    						int zInt = 0;
	    						if (value.equals("null")){
	    							sw.createCell(cellNum,zInt,styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex()); 
	    						}else{
	    							
	    							if ((value.indexOf("."))!= -1){
	    	                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    									
	    								//if (dv.getDisplayValue().startsWith("$")){
	    									//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    									String tempDollar = dv.getDisplayValue().trim();
	    									tempDollar = tempDollar.replaceAll(" ", "").substring(0);
	    	                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
	    									//System.out.println("SUBSTRING |" + tempDollar);
	    									//System.out.println("Before copy Value |" + tempDollar);
	    									//tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
	    									//System.out.println("After copy Value |" + tempDollar);
	    									if ((tempDollar.indexOf(","))!= -1){
	    										tempDollar = tempDollar.replaceAll(",", "");
	    									}
	    									//System.out.println("The final string 1 is "+tempDollar);
	    	                                double tempDoubleDollar = 0.0;
	    	                                try {
	    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleRowCell!=null)
	    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleRowCell.getIndex());
	    	                                    else if (styleCell!=null)
	    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleCell.getIndex());
	    	                                    else
	    	                                    	sw.createCell(cellNum, tempDoubleDollar, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                } catch (NumberFormatException ne) {
	    	                                	 if(styleRowCell!=null)
	    	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleRowCell.getIndex());
	    	                                	 else if (styleCell!=null)
	    	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleCell.getIndex());
	    	                                	 else
	    	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                }                                
	    								}else{
	    									//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                double tempDouble = 0.0;
	    	                                try {
	    	                                  tempDouble = Double.parseDouble(value);
	    	                                  if(styleRowCell!=null)
	    	                                	  sw.createCell(cellNum, tempDouble, styleRowCell.getIndex());
	    	                                  else if (styleCell!=null)
	    	                                	  sw.createCell(cellNum, tempDouble, styleCell.getIndex());
	    	                                  else
	    	                                	  sw.createCell(cellNum, tempDouble, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                } catch (NumberFormatException ne) {
	    	                                	if(styleRowCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(value),styleRowCell.getIndex() );
	    	                                	else if (styleCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(value), styleCell.getIndex());
	    	                                	else
	    	                                		sw.createCell(cellNum, Utils.excelEncode(value), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                }
	    	
	    								}
	    							}else {
	    								if (!(value.equals(""))){
	    	                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    									//if (dv.getDisplayValue().startsWith("$")){
	    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    										String tempInt = value.trim();
	    										tempInt = tempInt.replaceAll(" ", "").substring(0);
	    	                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
	    										//System.out.println("SUBSTRING |" + tempInt);
	    										//System.out.println("Before copy Value |" + tempInt);
	    										//tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
	    										//System.out.println("After copy Value |" + tempInt);
	    										if ((tempInt.indexOf(","))!= -1){
	    											tempInt = tempInt.replaceAll(",", "");
	    										}
	    										//System.out.println("The final string INT is "+tempInt);
	    	                                    Long tempIntDollar = 0L;
	    	                                    try {
	    	                                        tempIntDollar = Long.parseLong(tempInt);
	    	                                        if(styleRowCell!=null)
	    	                                        	sw.createCell(cellNum, tempIntDollar, styleRowCell.getIndex());
	    	                                        else if (styleCell!=null)
	    	                                        	sw.createCell(cellNum, tempIntDollar, styleCell.getIndex());
	    	                                        else
	    	                                        	sw.createCell(cellNum, tempIntDollar, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                     } catch (NumberFormatException ne) {
	    	                                    	 if(styleRowCell!=null)
	    	                                    		 sw.createCell(cellNum, tempInt, styleRowCell.getIndex());
	    	                                    	 else if (styleCell!=null)
	    	                                    		 sw.createCell(cellNum, tempInt, styleCell.getIndex());
	    	                                    	 else
	    	                                    		 sw.createCell(cellNum, tempInt, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                     }									
	    									}else{
	    										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    										String tempStr = value.trim();
	    	                                    if ((tempStr.indexOf(","))!= -1){
	    	                                        tempStr = tempStr.replaceAll(",", "");
	    	                                    }
	    	                                    Long temp = 0L;
	    	                                    
	    	                                    try {
	    	                                       temp = Long.parseLong(tempStr);
	    	                                       if(styleRowCell!=null)
	    	                                    	   sw.createCell(cellNum, temp, styleRowCell.getIndex());
	    	                                       else if (styleCell!=null)
	    	                                    	   sw.createCell(cellNum, temp, styleCell.getIndex());
	    	                                       else
	    	                                    	   sw.createCell(cellNum, temp, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                    } catch (NumberFormatException ne) {
	    	                                    	if(styleRowCell!=null)
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styleRowCell.getIndex());
	    	                                    	else if (styleCell!=null)
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styleCell.getIndex());
	    	                                    	else
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                    }
	    									}
	    							}
	    						}
	    						}
	    						
	    					} else if (  ( dataType !=null && dataType.equals("DATE")) || (dv.getDisplayName()!=null && dv.getDisplayName().toLowerCase().endsWith("date")) ||
	    							(dv.getColId()!=null && dv.getColId().toLowerCase().endsWith("date")) ||
	    							 (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date")) ) {
	    						XSSFCellStyle cellStyle = null;
	    						if(styleRowCell!=null) {
	    							cellStyle = styleRowCell;
	    						} else if (styleCell!=null) {
	    							cellStyle = styleCell;
	    						} else {
	    							cellStyle = styles.get(nvl(/*dv.getFormatId()*/"","date"));
	    						}
	    						
	    						
	    	                    Date date = null;
	    	                    int flag = 0;
	    	                    date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                    date = MMDDYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("YYYYMMDD"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = timestampFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("timestamp"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MONYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMMMMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMMMMDDYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONTHYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MONTHYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("YYYYMMDDHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDDASHFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("YYYYMMDDDASH"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("YYYYMMDDHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("DDMONYYYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("DDMONYYYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("DDMONYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("DDMONYYYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYY"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMM"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMZFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMDDYYYYHHMMZ"));
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMMMMDDYYYYHHMMSS.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	cellStyle.setDataFormat(dateFormatMap.get("MMMMMDDYYYYHHMMSS"));
	   	                        	flag = 1;
	   	                        }
	    	                    	
	    	                    if(date!=null) {
      		                      //System.out.println("ExcelDate " + HSSFDateUtil.getExcelDate(date));
	    	                    	Calendar cal=Calendar.getInstance();
	    	                    	cal.setTime(date);
	    	                    	//sw.createCell(cellNum, cal,styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                    	//if(styleRowCell!=null)
	    	                    		sw.createCell(cellNum, cal, cellStyle.getIndex());
	    	                    	//else if (styleCell!=null)
	    	                    		//sw.createCell(cellNum, cal, cellStyle.getIndex());
	    	                    	//else
	    	                    		//sw.createCell(cellNum, cal, cellStyle.getIndex());
	    	                    } else {
      		                      //cell.getCellStyle().setDataFormat((short)0);
	    	                    	//if(styleRowCell!=null)
	    	                    		sw.createCell(cellNum, Utils.excelEncode(value), cellStyle.getIndex());
	    	                    	//else if (styleCell!=null)
	    	                    		//sw.createCell(cellNum, Utils.excelEncode(value), cellStyle.getIndex());
	    	                    	//else
	    	                    		//sw.createCell(cellNum, Utils.excelEncode(value), cellStyle.getIndex());
	    	                    	
	    	                    }
	    						//cellDate.setCellValue(date);
      							//cellDate.setCellValue(value);	    						//cellDate.setCellValue(date);
	    						//cellDate.setCellValue(dv.getDisplayValue());
	    	                    
	    					} else if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	    	                    //cellNumber = row.createCell((short) cellNum);
	    	                    //cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
	    	                    //cellNumber.setCellValue(dv.getDisplayValue());
	    	                    int zInt = 0;
	    	                    if (value.equals("null")){
	    	                    	if(styleRowCell!=null)
	    	                    		sw.createCell(cellNum, zInt, styleRowCell.getIndex());
	    	                    	else if (styleCell!=null)
	    	                    		sw.createCell(cellNum, zInt, styleCell.getIndex());
	    	                    	else
	    	                    		sw.createCell(cellNum, zInt, styleDefaultCell.getIndex());
	    	                    		
	    	                    } else {
	    	                        
	    	                        if ((value.indexOf("."))!= -1){
	    	                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                                
	    	                            //if (value.startsWith("$")){
	    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    	                                String tempDollar = value.trim();
	    	                                tempDollar = tempDollar.replaceAll(" ", "").substring(0);
	    	                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
	    	                                //System.out.println("SUBSTRING |" + tempDollar);
	    	                                //System.out.println("Before copy Value |" + tempDollar);
	    	                                //tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
	    	                                //System.out.println("After copy Value |" + tempDollar);
	    	                                if ((tempDollar.indexOf(","))!= -1){
	    	                                    tempDollar = tempDollar.replaceAll(",", "");
	    	                                }
	    	                                //System.out.println("The final string 2IF is "+tempDollar);
	    	                                double tempDoubleDollar = 0.0;
	    	                                try {
	    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
	    	                                    if(styleRowCell!=null)
	    	                                    	sw.createCell(cellNum, tempDoubleDollar,styleRowCell.getIndex() );
	    	                                    else if (styleCell!=null)
	    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleCell.getIndex());
	    	                                    else
	    	                                    	sw.createCell(cellNum, tempDoubleDollar, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                } catch (NumberFormatException ne) {
	    	                                	if(styleRowCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleRowCell.getIndex());
	    	                                	else if (styleCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleCell.getIndex());
	    	                                	else
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                }                                
	    	                                
	    	
	    	                            }else{
	    	                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                String tempDoubleStr = value.trim();
	    	                                tempDoubleStr = tempDoubleStr.replaceAll(" ", "").substring(0);
	    	                                if ((tempDoubleStr.indexOf(","))!= -1){
	    	                                    tempDoubleStr = tempDoubleStr.replaceAll(",", "");
	    	                                }
	    	                                double tempDouble = 0.0;
	    	                                try {
	    	                                  tempDouble = Double.parseDouble(tempDoubleStr);
	    	                                  if(styleRowCell!=null)
	    	                                	  sw.createCell(cellNum, tempDouble, styleRowCell.getIndex());
	    	                                  else if (styleCell!=null)
	    	                                	  sw.createCell(cellNum, tempDouble, styleCell.getIndex());
	    	                                  else
	    	                                	  sw.createCell(cellNum, tempDouble, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                } catch (NumberFormatException ne) {
	    	                                	if(styleRowCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styleRowCell.getIndex());
	    	                                	else if (styleCell!=null)
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styleCell.getIndex());
	    	                                	else
	    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                }
	    	                            }
	    	                                
	    	                        }else {
	    	                            if (!(value.equals(""))){
	    	                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                                //if (value.startsWith("$")){
	    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
	    	                                    String tempInt = value.trim();
	    	                                    tempInt = tempInt.replaceAll(" ", "").substring(0);
	    	                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
	    	                                    //System.out.println("SUBSTRING |" + tempInt);
	    	                                    //System.out.println("Before copy Value |" + tempInt);
	    	                                    //tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
	    	                                    //System.out.println("After copy Value |" + tempInt);
	    	                                    if ((tempInt.indexOf(","))!= -1){
	    	                                        tempInt = tempInt.replaceAll(",", "");
	    	                                    }
	    	                                    //System.out.println("The final string INT 2 is "+tempInt);
	    	                                    
	    	                                    Long tempIntDollar = 0L;
	    	                                    
	    	                                    try {
	    	                                        tempIntDollar = Long.parseLong(tempInt);
	    	                                        if(styleRowCell!=null)
	    	                                        	sw.createCell(cellNum, tempIntDollar,styleRowCell.getIndex());
	    	                                        else if (styleCell!=null)
	    	                                        	sw.createCell(cellNum, tempIntDollar,styleCell.getIndex());
	    	                                        else
	    	                                        	sw.createCell(cellNum, tempIntDollar,styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                        
	    	                                    } catch (NumberFormatException ne) {
	    	                                    	if(styleRowCell!=null)
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt), styleRowCell.getIndex());
	    	                                    	else if (styleCell!=null)
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt),styleCell.getIndex());
	    	                                    	else
	    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                    }                                    
	    	                                }else{
	    	                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
	    	                                    String tempStr = value.trim();
	    	                                    if ((tempStr.indexOf(","))!= -1){
	    	                                        tempStr = tempStr.replaceAll(",", "");
	    	                                    }
	    	                                    Long temp = 0L;
	    	                                    
	    	                                    try {
	    	                                       temp = Long.parseLong(tempStr);
	    	                                       if(styleRowCell!=null)
	    	                                    	   sw.createCell(cellNum, temp, styleRowCell.getIndex());
	    	                                       else if (styleCell!=null)
	    	                                    	   sw.createCell(cellNum, temp, styleCell.getIndex());
	    	                                       else
	    	                                    	   sw.createCell(cellNum, temp, styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                    } catch (NumberFormatException ne) {
	    	                                    		if(styleRowCell!=null)
	    	                                    			sw.createCell(cellNum, Utils.excelEncode(tempStr), styleRowCell.getIndex());
		    	                                       else if (styleCell!=null)
		    	                                    	   sw.createCell(cellNum, Utils.excelEncode(tempStr), styleCell.getIndex());
		    	                                       else
		    	                                    	   sw.createCell(cellNum, Utils.excelEncode(tempStr), styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                                    	
	    	                                    }
	    	                                }
	    	                        } else {
	    	                        	 sw.createCell(cellNum, "", styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
	    	                        }
	    	                    }
	    	                    }
	    	                    
	    	                     
	    	                 }
	    	                else { 
	    						//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
	    	                	if(styleRowCell!=null)
	    	                		sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styleRowCell.getIndex());
	    	                	else if (styleCell!=null)
	    	                		sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styleCell.getIndex());
	    	                	else {
	    	                		if(nvl(value).startsWith(" ")) 
	    	                			sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styles.get(nvl(/*dv.getFormatId(),*/"","defaultLeft")).getIndex());
	    	                		else
	    	                			sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
	    	                		
	    	                	}
	    	                		
	    					}
	    	
	    					if (dv.isBold()) {
	    	                    if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	    	                        if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	    	                            //cell.setCellStyle(styleCurrencyTotal);
	    	                        }
	    	                        else {
	    	                            //cell.setCellStyle(styleTotal);
	    	                        }
	    	                    } else {
	    	                        //cell.setCellStyle(styleDefaultTotal);
	    	                    }
	    						bold = true;
	    					}
	    	                //System.out.println("2IF "+ (dr.isRowFormat()) + " " +  (dv.isCellFormat()) + " " + (styles!=null));
	    					if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) {
	    						  //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default")));
	    						continue;
	    					}
	    	                //System.out.println("3IF "+ (htmlFormat != null) + " " +  (dv.getFormatId() != null) + " " + (bold == false) + " "+ (styles != null));
	    					if (htmlFormat != null && dv.getFormatId() != null && bold == false
	    							&& styles != null) {
	    	                      //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(/*dv.getFormatId()*/"","default")));
	    					} //else if (bold == false)
	    						//cell.setCellStyle(styleDefault);    					
	    				} // dv.isVisible

	    			}
	    			rowNum += 1;
	    			sw.endRow();

	       		}
    			if(rd.reportTotalRowHeaderCols!=null) {
    				rowCount++;
    				sw.insertRow(rowNum);
    				cellNum = -1;
    				rd.reportTotalRowHeaderCols.resetNext();
	                cellNum += 1;
					RowHeaderCol rhc = rd.reportTotalRowHeaderCols.getNext();
					RowHeader rh = rhc.getRowHeader(0);
					if (dr.isRowFormat() && styles != null) 	    				
						styleRowCell = (XSSFCellStyle) styles.get(nvl(/*dr.getFormatId(),*/"","default"));		    			
					
                    if(styleRowCell!=null)
                    	sw.createCell(cellNum, strip.stripHtml(rh.getRowTitle()), styleRowCell.getIndex());
                    else
                    	sw.createCell(cellNum, strip.stripHtml(rh.getRowTitle()), styleDefaultCell.getIndex());
                    rd.reportDataTotalRow.resetNext();
					//rd.reportDataTotalRow.getNext();
	       			DataRow drTotal = rd.reportDataTotalRow.getNext();
	       			if(drTotal!=null) {
	       				drTotal.resetNext(); drTotal.getNext();
		       			for (; drTotal.hasNext();) {
		       				DataValue dv = drTotal.getNext();
		       				if(dv.isVisible()) {
			       				cellNum += 1;
			       				styleCell = null;
			       				String value = dv.getDisplayValue();
			       				sw.createCell(cellNum,value,styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex()); 
		       				}
		       			}
	       			}
	       			rowNum += 1;
	    			sw.endRow();
	       		}	       			

       			

	       		
    			
/*	       		// To Display Total Values for Linear report
	       		if(rd.reportDataTotalRow!=null) {
		       		row = sheet.createRow(rowNum);
		       		cellNum = -1;
		       		rd.reportTotalRowHeaderCols.resetNext();
		       		//for (rd.reportTotalRowHeaderCols.resetNext(); rd.reportTotalRowHeaderCols.hasNext();) {
		                cellNum += 1;
						RowHeaderCol rhc = rd.reportTotalRowHeaderCols.getNext();
						RowHeader rh = rhc.getRowHeader(0);
						row.createCell((short) cellNum).setCellValue(strip.stripHtml(rh.getRowTitle()));
						row.getCell((short) cellNum).setCellStyle(styleDefaultTotal);
		       		//}
	       			
	       			DataRow drTotal = rd.reportDataTotalRow.getNext();
       				//cellNum = -1;
	       			for (drTotal.resetNext(); drTotal.hasNext();j++) {
	       				cellNum += 1;
    					cell = row.createCell((short) cellNum);
	       				DataValue dv = drTotal.getNext();
	       				String value = dv.getDisplayValue();
	       				cell.setCellValue(value);
	       				boolean bold = false;
	       				if (dv.isBold()) {
	       					if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
	       						if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
	       							cell.setCellStyle(styleCurrencyTotal);
	       						} else {
	       							cell.setCellStyle(styleTotal);
	       						}
	       					} else {
	       						cell.setCellStyle(styleDefaultTotal);
	       					}
	       					bold = true;
	       				}
	       			}
	       		}*/
				
		    } catch (SQLException ex) { 
		    	throw new RaptorException(ex);
		    } catch (ReportSQLException ex) { 
		    	throw new RaptorException(ex);
		    } catch (Exception ex) {
		    	if(!(ex.getCause() instanceof java.net.SocketException) )
		    		throw new RaptorException (ex);
		    } finally {
	        	try {
	        		if(conn!=null)
	        			conn.close();
	        		if(st!=null)
	        			st.close();
	        		if(rs!=null)
	        			rs.close();
	        	} catch (SQLException ex) {
	        		throw new RaptorException(ex);
	        	}
	        }
        
		String footer = (String) session.getAttribute("FOOTER_"+index);
		if(nvl(footer).length()>0) {
		    footer = Utils.replaceInString(footer, "<BR/>", " ");
		    footer = Utils.replaceInString(footer, "<br/>", " ");
		    footer = Utils.replaceInString(footer, "<br>", " ");
			footer  = strip.stripHtml(nvl(footer).trim());
			rowNum += 1;
			sw.insertRow(rowNum);
			cellNum = 0;
			sw.createCell(cellNum,  footer.replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
			sw.endRow();
			rowNum += 1;
		}        
        
		if(Globals.getShowDisclaimer()) {
			rowNum += 1;
			sw.insertRow(rowNum);
			cellNum = 0;

			sw.createCell(cellNum,  org.openecomp.portalsdk.analytics.system.Globals.getFooterFirstLine().replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
			sw.endRow();
			rowNum += 1;
			sw.insertRow(rowNum);
			cellNum = 0;
			sw.createCell(cellNum,  org.openecomp.portalsdk.analytics.system.Globals.getFooterSecondLine().replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
			sw.endRow();
		}
        
    	  } else {
    		//start data from rd
    		  
  		    int rowCount = 0;
  		    DataRow dr = null;
  			for (rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext();) {
  				rowCount++;
  				
  				
  				dr = rd.reportDataRows.getNext();
  				sw.insertRow(rowNum);

  				cellNum = -1;
  				
  				if (rr.getReportType().equals(AppConstants.RT_LINEAR) && rd.reportTotalRowHeaderCols!=null) {
  					rd.reportRowHeaderCols.resetNext(0);
  	    			if(rd.reportTotalRowHeaderCols!=null) {
  	    				//cellNum = -1;
  	    				//for (rd.reportRowHeaderCols.resetNext(); rd.reportRowHeaderCols.hasNext();) {
  	    	          //a commented to suppress rownum
  	    				 //a cellNum += 1;
  	    					//RowHeaderCol rhc = rd.reportRowHeaderCols.getRowHeaderCol(0);
  	    					//if (firstPass)
  	    					//	rhc.resetNext();
  	    					//RowHeader rh = rhc.getRowHeader(rowCount-1);
	    	                //a sw.createCell(cellNum, rowCount, styleDefaultCell.getIndex());
  	    				//} // for
  	    			}
  					
  				}
  				firstPass = false;
  	            //cellNum = -1; 
  	            int j = 0;
  	            
  				for (dr.resetNext(); dr.hasNext();j++) {
  					DataValue dv = dr.getNext();
  	                styleCell = null;
  					boolean bold = false;
  					String value = nvl(dv.getDisplayValue());
                     	value = strip.stripHtml(value);
  					HtmlFormatter htmlFormat = dv.getCellFormatter();
  					if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) 	    				
  						styleCell = (XSSFCellStyle) styles.get(nvl(/*dr.getFormatId(),*/"","default"));
  					if (htmlFormat != null && dv.getFormatId() != null && styles != null) 
  						styleCell = (XSSFCellStyle) styles.get(nvl(/*dv.getFormatId(),*/"","default"));
  					
  					if(dv.isVisible()) {
  		                cellNum += 1;
  						//cell = row.createCell((short) cellNum);
  		                //System.out.println("Stripping HTML 1");
  						//cell.setCellValue(strip.stripHtml(value));
  						String dataType = (String) (dataTypeMap.get(dv.getColId()));
  						//System.out.println(" The Display Value is ********"+value + " " + dv.getDisplayTotal() + " " + dv.getColName());
  						
  						if (dataType!=null && dataType.equals("NUMBER")){ 
  							//cellNumber = row.createCell((short) cellNum);
  							//cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
  							//cellNumber.setCellValue(value);
  							//cellCurrencyNumber = row.createCell((short) cellNum);
  							int zInt = 0;
  							if (value.equals("null")){
  								sw.createCell(cellNum,zInt,styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex()); 
  							}else{
  								
  								if ((value.indexOf("."))!= -1){
  		                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
  										
  									//if (value.startsWith("$")){
  										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
  										String tempDollar = value.trim();
  										tempDollar = tempDollar.replaceAll(" ", "").substring(0);
  		                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
  										//System.out.println("SUBSTRING |" + tempDollar);
  										//System.out.println("Before copy Value |" + tempDollar);
  										//tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
  										//System.out.println("After copy Value |" + tempDollar);
  										if ((tempDollar.indexOf(","))!= -1){
  											tempDollar = tempDollar.replaceAll(",", "");
  										}
  										//System.out.println("The final string 1 is "+tempDollar);
  		                                double tempDoubleDollar = 0.0;
  		                                try {
  		                                    tempDoubleDollar = Double.parseDouble(tempDollar);
    	                                    if(styleRowCell!=null)
    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleRowCell.getIndex());
    	                                    else if (styleCell!=null)
    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleCell.getIndex());
    	                                    else
    	                                    	sw.createCell(cellNum, tempDoubleDollar, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                } catch (NumberFormatException ne) {
   	                                	 if(styleRowCell!=null)
	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleRowCell.getIndex());
	                                	 else if (styleCell!=null)
	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleCell.getIndex());
	                                	 else
	                                		 sw.createCell(cellNum, Utils.excelEncode(tempDollar), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                }                                
  									}else{
  										//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
  		                                double tempDouble = 0.0;
  		                                try {
  		                                  tempDouble = Double.parseDouble(value);
    	                                  if(styleRowCell!=null)
    	                                	  sw.createCell(cellNum, tempDouble, styleRowCell.getIndex());
    	                                  else if (styleCell!=null)
    	                                	  sw.createCell(cellNum, tempDouble, styleCell.getIndex());
    	                                  else
    	                                	  sw.createCell(cellNum, tempDouble, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                } catch (NumberFormatException ne) {
    	                                	if(styleRowCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(value),styleRowCell.getIndex() );
    	                                	else if (styleCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(value), styleCell.getIndex());
    	                                	else
    	                                		sw.createCell(cellNum, Utils.excelEncode(value), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                }
  		
  									}
  								}else {
  									if (!(value.equals(""))){
  		                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
  										//if (value.startsWith("$")){
  											//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
  											String tempInt = value.trim();
  											tempInt = tempInt.replaceAll(" ", "").substring(0);
  		                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
  											//System.out.println("SUBSTRING |" + tempInt);
  											//System.out.println("Before copy Value |" + tempInt);
  											//tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
  											//System.out.println("After copy Value |" + tempInt);
  											if ((tempInt.indexOf(","))!= -1){
  												tempInt = tempInt.replaceAll(",", "");
  											}
  											//System.out.println("The final string INT is "+tempInt);
  		                                    Long tempIntDollar = 0L;
  		                                    try {
  		                                        tempIntDollar = Long.parseLong(tempInt);
    	                                        if(styleRowCell!=null)
    	                                        	sw.createCell(cellNum, tempIntDollar, styleRowCell.getIndex());
    	                                        else if (styleCell!=null)
    	                                        	sw.createCell(cellNum, tempIntDollar, styleCell.getIndex());
    	                                        else
    	                                        	sw.createCell(cellNum, tempIntDollar, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                     } catch (NumberFormatException ne) {
    	                                    	 if(styleRowCell!=null)
    	                                    		 sw.createCell(cellNum, tempInt, styleRowCell.getIndex());
    	                                    	 else if (styleCell!=null)
    	                                    		 sw.createCell(cellNum, tempInt, styleCell.getIndex());
    	                                    	 else
    	                                    		 sw.createCell(cellNum, tempInt, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                     }									
  										}else{
  											//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
  											String tempStr = value.trim();
  		                                    if ((tempStr.indexOf(","))!= -1){
  		                                        tempStr = tempStr.replaceAll(",", "");
  		                                    }
  		                                    Long temp = 0L;
  		                                    
  		                                    try {
  		                                       temp = Long.parseLong(tempStr);
    	                                       if(styleRowCell!=null)
    	                                    	   sw.createCell(cellNum, temp, styleRowCell.getIndex());
    	                                       else if (styleCell!=null)
    	                                    	   sw.createCell(cellNum, temp, styleCell.getIndex());
    	                                       else
    	                                    	   sw.createCell(cellNum, temp, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    } catch (NumberFormatException ne) {
    	                                    	if(styleRowCell!=null)
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styleRowCell.getIndex());
    	                                    	else if (styleCell!=null)
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styleCell.getIndex());
    	                                    	else
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempStr), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    }
  		                                      
  		                                    
  										}
  										//int temp = Integer.parseInt(value.trim());
  										//	cell.setCellValue(temp);
  										//}else{
  										//	cell.setCellValue(strip.stripHtml(value));
  										//}
  								}
  							}
  							}
  							
	    					}else if (  ( dataType !=null && dataType.equals("DATE")) || (dv.getDisplayName()!=null && dv.getDisplayName().toLowerCase().endsWith("date")) ||
	    							(dv.getColId()!=null && dv.getColId().toLowerCase().endsWith("date")) ||
	    							 (dv.getColName()!=null && dv.getColName().toLowerCase().endsWith("date")) ) {
	    	                    Date date = null;
	    	                    int flag = 0;
	    	                    date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                    date = MMDDYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = timestampFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMMMMDDYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MONTHYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = YYYYMMDDDASHFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = YYYYMMDDHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = DDMONYYYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null) 
	    	                        date = MMDDYYHHMMFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYHHMMSSFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMDDYYYYHHMMZFormat.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    if(date==null)
	    	                        date = MMMMMDDYYYYHHMMSS.parse(value, new ParsePosition(0));
	   	                        if(date != null && flag == 0) {
	   	                        	flag = 1;
	   	                        }
	    	                    	
  		                    	
  		                    if(date!=null) {
    	                    	Calendar cal=Calendar.getInstance();
    	                    	cal.setTime(date);
    	                    	//sw.createCell(cellNum, cal,styles.get(nvl(/*dv.getFormatId()*/"","default")).getIndex());
    	                    	if(styleRowCell!=null)
    	                    		sw.createCell(cellNum, cal, styleRowCell.getIndex());
    	                    	else if (styleCell!=null)
    	                    		sw.createCell(cellNum, cal, styleCell.getIndex());
    	                    	else
    	                    		sw.createCell(cellNum, cal, styles.get(nvl(/*dv.getFormatId()*/"","date")).getIndex());
  		                    	
    		                    } else {
        		                      /*cell.getCellStyle().setDataFormat((short)0);*/	
  	    	                    	if(styleRowCell!=null)
  	    	                    		sw.createCell(cellNum, Utils.excelEncode(value), styleRowCell.getIndex());
  	    	                    	else if (styleCell!=null)
  	    	                    		sw.createCell(cellNum, Utils.excelEncode(value), styleCell.getIndex());
  	    	                    	else
  	    	                    		sw.createCell(cellNum, Utils.excelEncode(value), styles.get(nvl(/*dv.getFormatId(),*/"","date")).getIndex());
  	    	                    	
    		                    }
  							//cellDate.setCellValue(date);
  							//cellDate.setCellValue(value);
  		                    
  						}else if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
  		                    //cellNumber = row.createCell((short) cellNum);
  		                    //cellNumber.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
  		                    //cellNumber.setCellValue(value);
  		                    int zInt = 0;
  		                    if (value.equals("null")){
    	                    	if(styleRowCell!=null)
    	                    		sw.createCell(cellNum, zInt, styleRowCell.getIndex());
    	                    	else if (styleCell!=null)
    	                    		sw.createCell(cellNum, zInt, styleCell.getIndex());
    	                    	else
    	                    		sw.createCell(cellNum, zInt, styleDefaultCell.getIndex());
  		                    } else {
  		                        
  		                        if ((value.indexOf("."))!= -1){
  		                            if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
  		                                
  		                            //if (value.startsWith("$")){
  		                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
  		                                String tempDollar = value.trim();
  		                                tempDollar = tempDollar.replaceAll(" ", "").substring(0);
  		                                tempDollar = tempDollar.replaceAll("\\$", "").substring(0);
  		                                //System.out.println("SUBSTRING |" + tempDollar);
  		                                //System.out.println("Before copy Value |" + tempDollar);
  		                                //tempDollar = String.copyValueOf(tempDollar.toCharArray(), 1, tempDollar.length()-1);
  		                                //System.out.println("After copy Value |" + tempDollar);
  		                                if ((tempDollar.indexOf(","))!= -1){
  		                                    tempDollar = tempDollar.replaceAll(",", "");
  		                                }
  		                                //System.out.println("The final string 2IF is "+tempDollar);
  		                                double tempDoubleDollar = 0.0;
  		                                try {
    	                                    tempDoubleDollar = Double.parseDouble(tempDollar);
    	                                    if(styleRowCell!=null)
    	                                    	sw.createCell(cellNum, tempDoubleDollar,styleRowCell.getIndex() );
    	                                    else if (styleCell!=null)
    	                                    	sw.createCell(cellNum, tempDoubleDollar, styleCell.getIndex());
    	                                    else
    	                                    	sw.createCell(cellNum, tempDoubleDollar, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                } catch (NumberFormatException ne) {
    	                                	if(styleRowCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleRowCell.getIndex());
    	                                	else if (styleCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styleCell.getIndex());
    	                                	else
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDollar), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                }                                
  		                                
  		
  		                            }else{
  		                                //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
  		                                String tempDoubleStr = value.trim();
  		                                tempDoubleStr = tempDoubleStr.replaceAll(" ", "").substring(0);
  		                                if ((tempDoubleStr.indexOf(","))!= -1){
  		                                    tempDoubleStr = tempDoubleStr.replaceAll(",", "");
  		                                }
  		                                double tempDouble = 0.0;
  		                                try {
  		                                  tempDouble = Double.parseDouble(tempDoubleStr);
    	                                  if(styleRowCell!=null)
    	                                	  sw.createCell(cellNum, tempDouble, styleRowCell.getIndex());
    	                                  else if (styleCell!=null)
    	                                	  sw.createCell(cellNum, tempDouble, styleCell.getIndex());
    	                                  else
    	                                	  sw.createCell(cellNum, tempDouble, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                } catch (NumberFormatException ne) {
    	                                	if(styleRowCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styleRowCell.getIndex());
    	                                	else if (styleCell!=null)
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styleCell.getIndex());
    	                                	else
    	                                		sw.createCell(cellNum, Utils.excelEncode(tempDoubleStr), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                }
  		                            }
  		                                
  		                        }else {
  		                            if (!(value.equals(""))){
  		                                if ((value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
  		                                //if (value.startsWith("$")){
  		                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("($#,##0.00);($#,##0.00)"));
  		                                    String tempInt = value.trim();
  		                                    tempInt = tempInt.replaceAll(" ", "").substring(0);
  		                                    tempInt = tempInt.replaceAll("\\$", "").substring(0);
  		                                    //System.out.println("SUBSTRING |" + tempInt);
  		                                    //System.out.println("Before copy Value |" + tempInt);
  		                                    //tempInt = String.copyValueOf(tempInt.toCharArray(), 1, tempInt.length()-1);
  		                                    //System.out.println("After copy Value |" + tempInt);
  		                                    if ((tempInt.indexOf(","))!= -1){
  		                                        tempInt = tempInt.replaceAll(",", "");
  		                                    }
  		                                    //System.out.println("The final string INT 2 is "+tempInt);
  		                                    
  		                                    Long tempIntDollar = 0L;
  		                                    
  		                                    try {
  		                                        tempIntDollar = Long.parseLong(tempInt);
    	                                        if(styleRowCell!=null)
    	                                        	sw.createCell(cellNum, tempIntDollar,styleRowCell.getIndex());
    	                                        else if (styleCell!=null)
    	                                        	sw.createCell(cellNum, tempIntDollar,styleCell.getIndex());
    	                                        else
    	                                        	sw.createCell(cellNum, tempIntDollar,styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    } catch (NumberFormatException ne) {
    	                                    	if(styleRowCell!=null)
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt), styleRowCell.getIndex());
    	                                    	else if (styleCell!=null)
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt),styleCell.getIndex());
    	                                    	else
    	                                    		sw.createCell(cellNum, Utils.excelEncode(tempInt), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    }                                    
  		                                }else{
  		                                    //styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"));
  		                                    String tempStr = value.trim();
  		                                    if ((tempStr.indexOf(","))!= -1){
  		                                        tempStr = tempStr.replaceAll(",", "");
  		                                    }
  		                                    Long temp = 0L;
  		                                    
  		                                    try {
  		                                       temp = Long.parseLong(tempStr);
    	                                       if(styleRowCell!=null)
    	                                    	   sw.createCell(cellNum, temp, styleRowCell.getIndex());
    	                                       else if (styleCell!=null)
    	                                    	   sw.createCell(cellNum, temp, styleCell.getIndex());
    	                                       else
    	                                    	   sw.createCell(cellNum, temp, styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    } catch (NumberFormatException ne) {
	                                    		if(styleRowCell!=null)
	                                    			sw.createCell(cellNum, Utils.excelEncode(tempStr), styleRowCell.getIndex());
    	                                       else if (styleCell!=null)
    	                                    	   sw.createCell(cellNum, Utils.excelEncode(tempStr), styleCell.getIndex());
    	                                       else
    	                                    	   sw.createCell(cellNum, Utils.excelEncode(tempStr), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                                    }
  		                                }
  		                                //int temp = Integer.parseInt(value.trim());
  		                                //  cell.setCellValue(temp);
  		                                //}else{
  		                                //  cell.setCellValue(strip.stripHtml(value));
  		                                //}
  		                        } else {
   	                        	 sw.createCell(cellNum, "", styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  		                        }
  		                    }
  		                    }
  		                    
  		                     
  		                 }
  		                else { 
  							//styleDefault.setDataFormat(HSSFDataFormat.getBuiltinFormat("General"));
    	                	if(styleRowCell!=null)
    	                		sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styleRowCell.getIndex());
    	                	else if (styleCell!=null)
    	                		sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styleCell.getIndex());
    	                	else
    	                		sw.createCell(cellNum, strip.stripHtml(Utils.excelEncode(value)), styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex());
  						}
  		
  						//if (!(value.equals(""))){
  						//int temp = Integer.parseInt(value.trim());
  						//cell.setCellValue(temp);
  						//}else{
  						//	cell.setCellValue(strip.stripHtml(value));
  						//}
  		                //HSSFCellStyle styleFormat = null;
  		                //HSSFCellStyle numberStyle = null;
  		                //HSSFFont formatFont = null;
  		                //short fgcolor = 0;
  		                //short fillpattern = 0;
  		                //System.out.println("1IF "+ (dv.isBold()) + " "+ value + " " + dv.getDisplayTotal() + " " + dv.getColName() );
  						if (dv.isBold()) {
  		                    if((dv.getDisplayTotal()!=null && dv.getDisplayTotal().equals("SUM(")) || (dv.getColName()!=null && dv.getColName().indexOf("999")!=-1)){
  		                        if (value!=null && (value.trim().startsWith("$")) || (value.trim().startsWith("-$") )) {
  		                            //cell.setCellStyle(styleCurrencyTotal);
  		                        }
  		                        else {
  		                            //cell.setCellStyle(styleTotal);
  		                        }
  		                    } else {
  		                        //cell.setCellStyle(styleDefaultTotal);
  		                    }
  							bold = true;
  						}
  		                //System.out.println("2IF "+ (dr.isRowFormat()) + " " +  (dv.isCellFormat()) + " " + (styles!=null));
  						if ((dr.isRowFormat() && !dv.isCellFormat()) && styles != null) {
  							  //cell.setCellStyle((HSSFCellStyle) styles.get(nvl(dr.getFormatId(),"default")));
  							continue;
  						}
  		                //System.out.println("3IF "+ (htmlFormat != null) + " " +  (dv.getFormatId() != null) + " " + (bold == false) + " "+ (styles != null));
  						if (htmlFormat != null && dv.getFormatId() != null && bold == false
  								&& styles != null) {
  		                     // cell.setCellStyle((HSSFCellStyle) styles.get(nvl(/*dv.getFormatId()*/"","default")));
  						} //else if (bold == false)
  							//cell.setCellStyle(styleDefault);
  					} // if (dv.isVisible)
  				} // for
  				
  				/*for (int tmp=0; tmp<dataTypeMap.size(); tmp++){
  					String dataTypeStr = (String)(dataTypeMap.get(tmp));
  					if(dataTypeStr.equals("NUMBER")){
  						cell.setCellStyle(styleNumber);
  					}else if (dataTypeStr.equals("VARCHAR2")){
  						cell.setCellStyle(styleDefault);

  					}else if (dataTypeStr.equals("DATE")){
  						cell.setCellStyle(styleDate);
  					}else{
  						
  					}
  	                
  				}*/
  				rowNum += 1;
                sw.endRow();
  			} // for
  			
				if (rr.getReportType().equals(AppConstants.RT_LINEAR) && rd.reportTotalRowHeaderCols!=null) {

	    			for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();) {
	    				rowCount++;
	    				sw.insertRow(rowNum);
	    				cellNum = -1;
	    				cellNum += 1;
	    				
						RowHeaderCol rhc = rd.reportTotalRowHeaderCols.getNext();
						RowHeader rh = rhc.getRowHeader(0);
						if (dr.isRowFormat() && styles != null) 	    				
							styleRowCell = (XSSFCellStyle) styles.get(nvl(/*dr.getFormatId(),*/"","default"));		    			
	                    if(styleRowCell!=null)
	                    	sw.createCell(cellNum, strip.stripHtml(rh.getRowTitle()), styleRowCell.getIndex());
	                    else
	                    	sw.createCell(cellNum, strip.stripHtml(rh.getRowTitle()), styleDefaultCell.getIndex());
	                    
		       			DataRow drTotal = rd.reportDataTotalRow.getNext();
		       			if(drTotal!=null) {
		       				drTotal.resetNext(); drTotal.getNext();
			       			for (; drTotal.hasNext();) {
			       				cellNum += 1;
			       				styleCell = null;
			       				DataValue dv = drTotal.getNext();
			       				String value = dv.getDisplayValue();
			       				sw.createCell(cellNum,value,styles.get(nvl(/*dv.getFormatId(),*/"","default")).getIndex()); 
			       			}
		       			}

		       			rowNum += 1;
		    			sw.endRow();
		       		}
	    			
	    			
	    			String footer = (String) session.getAttribute("FOOTER_"+index);
	    			if(nvl(footer).length()>0) {
	    			    footer = Utils.replaceInString(footer, "<BR/>", " ");
	    			    footer = Utils.replaceInString(footer, "<br/>", " ");
	    			    footer = Utils.replaceInString(footer, "<br>", " ");
	    				footer  = strip.stripHtml(nvl(footer).trim());
	    				rowNum += 1;
	    				sw.insertRow(rowNum);
	    				cellNum = 0;
	    				sw.createCell(cellNum,  footer.replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
	    				sw.endRow();
	    				rowNum += 1;
	    			}

	    			
	    			if(Globals.getShowDisclaimer()) {
	    				rowNum += 1;
	    				sw.insertRow(rowNum);
	    				cellNum = 0;

	    				sw.createCell(cellNum,  org.openecomp.portalsdk.analytics.system.Globals.getFooterFirstLine().replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
	    				sw.endRow();
	    				rowNum += 1;
	    				sw.insertRow(rowNum);
	    				cellNum = 0;
	    				sw.createCell(cellNum,  org.openecomp.portalsdk.analytics.system.Globals.getFooterSecondLine().replaceAll("&", "&amp;"), styleDefaultCell.getIndex());
	    				sw.endRow();
	    			}

		       			
				}  
    		// end data from rd  
    	  }
        
		// System.out.println(" Last Row " + wb.getSheetAt(0).getLastRowNum());
	}	
	
	private void paintXSSFExcelParams(XSSFWorkbook wb,int rowNum,int col,ArrayList paramsList, String customizedParamInfo, XSSFSheet sheet, String reportTitle, String reportDescr) throws IOException {
        //HSSFSheet sheet = wb.getSheet(getSheetName());
        int cellNum = 0;
        XSSFRow row = null;
        short s1 = 0, s2 = (short) 1;
        HtmlStripper strip = new HtmlStripper();
        // Name Style
        XSSFCellStyle styleName = wb.createCellStyle();
        //styleName.setFillBackgroundColor(HSSFColor.GREY_80_PERCENT.index);
        styleName.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        //styleName.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
        styleName.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleName.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderRight(HSSFCellStyle.BORDER_THIN);
        styleName.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleName.setDataFormat((short)0);
        XSSFFont font = wb.createFont();
        font.setFontHeight((short) (font_size / 0.05));
        font.setFontName("Tahoma");
        font.setColor(HSSFColor.BLACK.index);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleName.setFont(font);
        //Data Style
        
        // Create some fonts.
        XSSFFont fontDefault = wb.createFont();
        // Initialize the styles & fonts.
        // The default will be plain .
        fontDefault.setColor((short) HSSFFont.COLOR_NORMAL);
        fontDefault.setFontHeight((short) (font_size / 0.05));
        fontDefault.setFontName("Tahoma");
        fontDefault.setItalic(true);
        // Style default will be normal with no background
        XSSFCellStyle styleValue = wb.createCellStyle();
        styleValue.setDataFormat((short)0);
        styleValue.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        styleValue.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderTop(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        styleValue.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // styleValue.setFillForegroundColor(HSSFColor.YELLOW.index);
        styleValue.setFillPattern(HSSFCellStyle.NO_FILL);
        styleValue.setFont(fontDefault);
        XSSFCell cell = null;
        XSSFCellStyle styleDescription = wb.createCellStyle();
        styleDescription.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//        styleDescription.setBorderBottom(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderTop(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderRight(HSSFCellStyle.BORDER_THIN);
//        styleDescription.setBorderLeft(HSSFCellStyle.BORDER_THIN);        
        XSSFFont fontDescr = wb.createFont();
        fontDescr.setFontHeight((short) (font_header_descr_size / 0.05));
        fontDescr.setFontName("Tahoma");
        fontDescr.setColor(HSSFColor.BLACK.index);
        fontDescr.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        styleDescription.setFont(font);
        XSSFCell cellDescr = null;
        int paramSeq = 0;
        Header header = sheet.getHeader();
        StringBuffer strBuf = new StringBuffer(); 
        if(!Globals.customizeFormFieldInfo() || customizedParamInfo.length()<=0) {
	        for (Iterator iter = paramsList.iterator(); iter.hasNext();) {
	            IdNameValue value = (IdNameValue) iter.next();
	            //System.out.println("\"" + value.getId() + " = " + value.getName() + "\"");
	            if(nvl(value.getId()).trim().length()>0  && (!nvl(value.getId()).trim().equals("BLANK"))) {
	                paramSeq += 1;
	                if(paramSeq <= 1) {
	                    row = sheet.createRow(++rowNum);
	                    cell = row.createCell((short) 0);
	                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, s1, s2));
	                    cellDescr = row.createCell((short) 0);
	                    cellDescr.setCellValue("Run-time Parameters");
	                    cellDescr.setCellStyle(styleDescription);
	                    

	                	strBuf.append(reportTitle+"\n"); 
	                	//strBuf.append("Run-time Parameters\n");
	                }
		                row = sheet.createRow(++rowNum);    
		                cellNum = 0;
		                //System.out.println("RowNum " + rowNum + " " + value.getId() + " " +value.getName());
		                cell = row.createCell((short) cellNum);
		                cell.setCellValue(value.getId());
		                cell.setCellStyle(styleName); 
		                cellNum += 1;
		                cell = row.createCell((short) cellNum);
		                cell.setCellValue(value.getName().replaceAll("~",","));
		                cell.setCellStyle(styleValue);

	                	//strBuf.append(value.getId()+": "+ value.getName()+"\n");
	               }
            } //for
        } else {
        	strBuf.append(reportTitle+"\n");
    		Document document = new Document();
    		document.open();        	
            HTMLWorker worker = new HTMLWorker(document);
        	StyleSheet style = new StyleSheet();
        	style.loadTagStyle("body", "leading", "16,0");
        	ArrayList p = HTMLWorker.parseToList(new StringReader(customizedParamInfo), style);
        	String name = "";
        	String token = "";
        	String value = "";
        	String s = "";
        	PdfPTable pdfTable = null;
    	   	 for (int k = 0; k < p.size(); ++k){
    	   		if(p.get(k) instanceof Paragraph) 
    	   			s = ((Paragraph)p.get(k)).toString();
    	   		else { /*if ((p.get(k) instanceof PdfPTable))*/
    	   			pdfTable = ((PdfPTable)p.get(k));
    	   		}
    	   		//todo: Logic for parsing pdfTable should be added after upgrading to iText 5.0.0
    	   		//s = Utils.replaceInString(s, ",", "|");
    	   		s = s.replaceAll(",", "|");	
    	   		s = s.replaceAll("~", ",");
    	 	    if(s.indexOf(":")!= -1) {
    	 	    	//System.out.println("|"+s+"|");
                    row = sheet.createRow(++rowNum); 
                    cell = row.createCell((short) 0);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, s1, s2));
                    cellDescr = row.createCell((short) 0);
                    cellDescr.setCellValue("Run-time Parameters");
                    cellDescr.setCellStyle(styleDescription);   
    	 	    	 
    	 	    	//strBuf.append("Run-time Parameters\n");
    	 	    	StringTokenizer st = new StringTokenizer(s.trim(), "|");
    	 	    	while(st.hasMoreTokens()) {
    	 	    		token = st.nextToken();
    	 	    		token = token.trim();
    	 	    		if (!(token.trim().equals("|") || token.trim().equals("]]") || token.trim().equals("]") || token.trim().equals("[") )) {
    		 	    		if(token.endsWith(":")) {
    		 	    			name = token;
    		 	    			name = name.substring(0, name.length()-1);
    		 	    			if(name.startsWith("[")) 
    		 	    				name = name.substring(1);
    		 	    			value = st.nextToken();    		 	    			
    		 	    			if(nvl(value).endsWith("]"))value = nvl(value).substring(0, nvl(value).length()-1);
    		 	    		} /*else if(name != null && name.length() > 0) {
    		 	    			value = st.nextToken();
    		 	    			if(value.endsWith("]]"))value = value.substring(0, value.length()-1);
    		 	    		}*/
    		 	    		if(name!=null && name.trim().length()>0) {
    		 	    			row = sheet.createRow((short) ++rowNum);
    		 	    			cellNum = 0;
    		 	    			cell = row.createCell((short) cellNum);
    			                cell.setCellValue(name.trim());
    			                cell.setCellStyle(styleName); 
    			                cellNum += 1;
    			                cell = row.createCell((short) cellNum);
    			                cell.setCellValue(value.trim());
    			                cell.setCellStyle(styleValue); 
    		 	    			//strBuf.append(name.trim()+": "+ value.trim()+"\n");
    		 	    		}
/*    		 	    		if(token.endsWith(":") && (value!=null && value.trim().length()<=0) && (name!=null && name.trim().length()>0 && name.endsWith(":"))) {
    		 	    			name = name.substring(0, name.indexOf(":")+1);
    		 	    			//value = token.substring(token.indexOf(":")+1);
    		 	    			row = sheet.createRow((short) ++rowNum);
    		 	    			cellNum = 0;
    		 	    			cell = row.createCell((short) cellNum);
    			                cell.setCellValue(name.trim());
    			                cell.setCellStyle(styleName); 
    			                cellNum += 1;
    			                cell = row.createCell((short) cellNum);
    			                cell.setCellValue(value.trim());
    			                cell.setCellStyle(styleValue);
    			                    			                
    		 	    			//strBuf.append(name.trim()+": "+ value.trim()+"\n");
    			                value = "";
    			                name = "";
    		 	    		}
*/    	 	    		}
    	 				int cw = 0;
    					cw =  name.trim().length() + 12;
    					// if(i!=cellWidth.size()-1)
    					if(sheet.getColumnWidth((short)0)< (short) name.trim().length())
    					sheet.setColumnWidth((short)0, (short) name.trim().length());
    					if(sheet.getColumnWidth((short)1)< (short) value.trim().length())
    					sheet.setColumnWidth((short)1, (short) value.trim().length());
		                name = "";
		                value = "";
    					
    	 	    	}

	                try {
				        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
				        Date sysdate = oracleDateFormat.parse(ReportLoader.getSystemDateTime());
				        SimpleDateFormat dtimestamp = new SimpleDateFormat(Globals.getScheduleDatePattern());
	 	    		
    	 	    		row = sheet.createRow((short) ++rowNum);
	 	    			cellNum = 0;
	 	    			cell = row.createCell((short) cellNum);
		                cell.setCellValue("Report Date/Time");
		                cell.setCellStyle(styleName); 
		                cellNum += 1;
		                cell = row.createCell((short) cellNum);
		                
		                cell.setCellValue(dtimestamp.format(sysdate)+" "+Globals.getTimeZone());
		                cell.setCellStyle(styleValue);
		                
	                } catch(Exception ex) {
	                	//ex.printStackTrace();
	                } 
    	 	    	
    	 	    	
    	 	    }
    	 	 }    	
        	
        	
/*            Iterator iter1 = paramsList.iterator();
            s1 = 0; s2 = (short)10;
            if(iter1.hasNext()) {
            	row = sheet.createRow((short) ++rowNum);
            	cellNum = 0;
            	cell = row.createCell((short) cellNum);
            	sheet.addMergedRegion(new Region(rowNum, s1, rowNum, s2));
            	cell.setCellValue(strip.stripHtml(customizedParamInfo));
            }    
*/
/*             rowNum += 2;
             row = sheet.createRow(rowNum);*/    	   	 
    	   	 } // if
        Iterator iterCheck = paramsList.iterator();
        if(iterCheck.hasNext()) {
            rowNum += 2;
            row = sheet.createRow(rowNum);
        }
        header.setCenter(HSSFHeader.font("Tahoma", "")+ HSSFHeader.fontSize((short) font_header_title_size)+strBuf.toString());
    }
	
   // Trying different -->
   public void createHTMLFileContent(Writer out, ReportData rd,
			ReportRuntime rr, String sql_whole, HttpServletRequest request, HttpServletResponse response)
			throws RaptorException, IOException {
    	//response.setContentType("application/vnd.ms-excel");
    	//response.setHeader("Content-disposition",
         //       "attachment; filename=" +
         //       "Example.xls" );
		PrintWriter csvOut = response.getWriter();
		HtmlStripper strip = new HtmlStripper();
		ResultSet rs = null;
        Connection conn = null;
        Statement st = null;
        ResultSetMetaData rsmd = null;
        ColumnHeaderRow chr = null;
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	csvOut.println("<HTML>\n" +
                "<HEAD><TITLE>" + rr.getReportName() + "</TITLE></HEAD>\n" +
                "<BODY>\n" );
    	System.out.println("HTML-Excel Generation Triggered: " + new java.util.Date());
    	csvOut.print("<TABLE>");
        if (Globals.getPrintParamsInCSVDownload()) {
            ArrayList paramsList = rr.getParamNameValuePairsforPDFExcel(request, 1); 
            int paramSeq = 0;
	        for (Iterator iter = paramsList.iterator(); iter.hasNext();) {
	            IdNameValue value = (IdNameValue) iter.next();
	            //System.out.println("\"" + value.getId() + " = " + value.getName() + "\"");
	            if(nvl(value.getId()).trim().length()>0  && (!nvl(value.getId()).trim().equals("BLANK"))) {
	                paramSeq += 1;
	                if(paramSeq <= 1) {
	                	csvOut.println("<TR><TD COLSPAN=\"2\">" + "Run-time Parameters" + "</TD></TR>");
	                	//strBuf.append("Run-time Parameters\n");
	                }
	                	csvOut.println("<TR><TD>" + value.getId() +"</TD>");
	                	csvOut.println("<TD>" + value.getName().replaceAll("~",",")+ "</TD>");
	                	csvOut.println("</TR>");
	
	                	//strBuf.append(value.getId()+": "+ value.getName()+"\n");
	               }
	        } //for
	        csvOut.println("<TR><TD COLSPAN=\"2\"> &nbsp;</TD></TR>");
	        csvOut.println("<TR><TD COLSPAN=\"2\"> &nbsp;</TD></TR>");
	        System.out.println("HTML-Excel: Header Rendering complete " + new java.util.Date());
        }
        int rowCount = 0;
        if(nvl(sql_whole).length()>0) {
	        try {
	        	conn = ConnectionUtils.getConnection(rr.getDbInfo());
	        	st = conn.createStatement();
	        	Log.write("[SQL] " + sql_whole, 4);
	        	int downloadLimit = Globals.getDownloadLimit();
	        	Callable<ResultSet> callable = new ExecuteQuery(st, sql_whole, downloadLimit);
				ExecutorService executor = new ScheduledThreadPoolExecutor(5);
			    System.out.println("Time Started" + new java.util.Date());
				Future<ResultSet> future = executor.submit(callable);
				try {
						rs = future.get(900, TimeUnit.SECONDS);
				} catch (TimeoutException ex) {
			    	System.out.println("Cancelling Query");
			    	st.cancel();
			    	System.out.println("Query Cancelled");
			    	throw new Exception("user requested");
				}
	        	rsmd = rs.getMetaData();
	            int numberOfColumns = rsmd.getColumnCount();
	            HashMap colHash = new HashMap();
	            
	            if(rd!=null) {
		    		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
		    			chr = rd.reportColumnHeaderRows.getNext();
		    			csvOut.println("<TR>");
		    			for (chr.resetNext(); chr.hasNext();) {
		    				ColumnHeader ch = chr.getNext();
		    				if(ch.isVisible()) {
		    					csvOut.print("<TD bgColor=\"8F9381\">" + ch.getColumnTitle() + "</TD>");
		    					//for (int i = 1; i < ch.getColSpan(); i++)
		    					//	csvOut.print(",");
		    					
		    				}
		    			} // for
		    			csvOut.println("</TR>");
		    		} // for
	    		
		    		
	    		while(rs.next()) {
	    			csvOut.println("<TR>");
/*	    			if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*Globals.getMemoryThreshold()/100) ) {
	    				csvOut.print(Globals.getUserDefinedMessageForMemoryLimitReached() + " " + rowCount +"records out of " + rr.getReportDataSize() + " were downloaded to CSV.");
	    				break;
	    			}
*/	    			rowCount++;
	    			colHash = new HashMap();
	    			for (int i = 1; i <= numberOfColumns; i++) {
	    				colHash.put(rsmd.getColumnName(i), rs.getString(i));
	    			}
	    			for (chr.resetNext(); chr.hasNext();) {
	    				ColumnHeader ch = chr.getNext();
	    				if(ch.isVisible()) {
	    					csvOut.println("<TD>" + strip.stripCSVHtml(nvl((String)colHash.get(ch.getLinkColId().toUpperCase()))) + "</TD>");
	    				}
	    				
	    			}
	    			csvOut.println("</TR>");
	    		}
	    		System.out.println("Downloaded Rows in HTML-Excel " + rowCount + " : "+ new java.util.Date());
	    		if(rowCount == 0) {
	    			csvOut.print("<TR><TD COLSPAN=\""+ numberOfColumns + "\">No Data Found</TD></TR>");
	    		} else {
	    		}
	            } else {
	            	csvOut.println("<TR><TD COLSPAN=\""+ numberOfColumns + "\">No Data Found</TD></TR>");
	            }
	            csvOut.println("</TABLE></BODY>\n</HTML>");
	            
	        } catch (SQLException ex) { 
	        	throw new RaptorException(ex);
	        } catch (ReportSQLException ex) { 
	        	throw new RaptorException(ex);
	        } catch (Exception ex) {
	        	throw new RaptorException (ex);
	        } finally {
	        	try {
	        		if(conn!=null)
	        			conn.close();
	        		if(st!=null)
	        			st.close();
	        		if(rs!=null)
	        			rs.close();
	        	} catch (SQLException ex) {
	        		throw new RaptorException(ex);
	        	}
	        }
	        //csvOut.flush();
        } else {
    		boolean firstPass = true;
    		int numberOfColumns = 0;
    		if(rd!=null) {
    		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) {
    			chr = rd.reportColumnHeaderRows.getNext();
    			csvOut.println("<TR>");
    			for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
    				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();

    				if (firstPass) {
    					numberOfColumns++;
    					csvOut.print("<TD bgColor=\"8F9381\">" + rhc.getColumnTitle() + "</TD>");
    				}
    				//csvOut.print(",");
    			} // for
    			

    			for (chr.resetNext(); chr.hasNext();) {
    				ColumnHeader ch = chr.getNext();
    				if(ch.isVisible()) {
    					if(firstPass) numberOfColumns++;
    					csvOut.print("<TD bgColor=\"8F9381\">" + ch.getColumnTitle() + "</TD>");
    					//for (int i = 1; i < ch.getColSpan(); i++)
    						//csvOut.print(",");
    				}
    			} // for
    			firstPass = false; 
    			csvOut.println("</TR>");
    		} // for

    		firstPass = true;
    		for (rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext();) {
    			DataRow dr = rd.reportDataRows.getNext();
    			csvOut.println("<TR>");
    			for (rd.reportRowHeaderCols.resetNext(1); rd.reportRowHeaderCols.hasNext();) {
    				RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
    				if (firstPass)
    					rhc.resetNext();
    				RowHeader rh = rhc.getNext();

    				csvOut.print("<TD bgColor=\"8F9381\">" + strip.stripCSVHtml(rh.getRowTitle()) + "</TD>");
    			} // for
    			firstPass = false;

    			for (dr.resetNext(); dr.hasNext();) {
    				DataValue dv = dr.getNext();
                    if(dv.isVisible())  
                    	csvOut.print("<TD bgColor=\"8F9381\">" + strip.stripCSVHtml(dv.getDisplayValue()) + "</TD>");
    			} // for

    			csvOut.println("</TR>");

    		} // for
            //csvOut.flush();
        } else {
        	csvOut.println("<TR><TD COLSPAN=\""+ numberOfColumns + "\">No Data Found</TD></TR>");
        }
      }
       csvOut.println("</TABLE></BODY>\n</HTML>");
      System.out.println("HTML-Excel Generation: Data Rendering complete " + new java.util.Date());
    	System.out.println("##### Heap utilization statistics [MB] #####");
    	System.out.println("Used Memory:"
    			+ (runtime.maxMemory() - runtime.freeMemory()) / mb);
    	System.out.println("Free Memory:"
    			+ runtime.freeMemory() / mb);
    	System.out.println("Total Memory:" + runtime.totalMemory() / mb);
    	System.out.println("Max Memory:" + runtime.maxMemory() / mb);
        
	} // createCSVFileContent
   
   /**
    * Checking if every row and cell in merging region exists, and create those which are not    
    * @param sheet in which check is performed
    * @param region to check
    * @param cellStyle cell style to apply for whole region
    */
   private void cleanBeforeMergeOnValidCells(XSSFSheet sheet,CellRangeAddress region, XSSFCellStyle cellStyle )
   {
       for(int rowNum =region.getFirstRow();rowNum<=region.getLastRow();rowNum++){
           XSSFRow row= sheet.getRow(rowNum);
           if(row==null){
               sheet.createRow(rowNum);
           }
           for(int colNum=region.getFirstColumn();colNum<=region.getLastColumn();colNum++){
               XSSFCell currentCell = row.getCell(colNum); 
              if(currentCell==null){
                  currentCell = row.createCell(colNum);
              }    

              currentCell.setCellStyle(cellStyle);

           }
       }


   }   
} // ReportHandler

 
/** 
 * Adapter for a Writer to behave like an OutputStream.  
 *
 * Bytes are converted to chars using the platform default encoding.
 * If this encoding is not a single-byte encoding, some data may be lost.
 */
  class WriterOutputStream extends OutputStream {
 
    private final Writer writer;
 
    public WriterOutputStream(Writer writer) {
        this.writer = writer;
    }
 
    public void write(int b) throws IOException {
        // It's tempting to use writer.write((char) b), but that may get the encoding wrong
        // This is inefficient, but it works
        write(new byte[] {(byte) b}, 0, 1);
    }
 
    public void write(byte b[], int off, int len) throws IOException {
        writer.write(new String(b, off, len));
    }
 
    public void flush() throws IOException {
        writer.flush();
    }
 
    public void close() throws IOException {
        writer.close();
    }
}
