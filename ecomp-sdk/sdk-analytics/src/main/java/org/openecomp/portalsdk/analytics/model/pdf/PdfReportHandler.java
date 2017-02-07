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
/* ===========================================================================================

 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * PdfReportHandler.java - This class is used to generate reports in PDF using iText 
 * -------------------------------------------------------------------------------------------
 *
 *
 * Changes
 * -------
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL> 
 *                                     <LI> Dashboard reports can be downloaded with each report occupying separate page including its charts. </LI>
 *                                     </UL>   
 *
 */
package org.openecomp.portalsdk.analytics.model.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.ReportLoader;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.model.definition.ReportDefinition;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.HtmlStripper;
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
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.html.simpleparser.HTMLWorker;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


public class PdfReportHandler extends org.openecomp.portalsdk.analytics.RaptorObject{

	/**
	 * 
	 */
	private PdfBean pb;
	private HtmlStripper strip = new HtmlStripper();
	private static final int RetryCreateNewImage = 3;
	private int retryCreateNewImageCount=0;
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PdfReportHandler.class);

	private String FONT_FAMILY = "Arial";
	private int FONT_SIZE = 9;
	
	public PdfReportHandler() {	}

	public void createPdfFileContent(HttpServletRequest request, HttpServletResponse response, int type) throws IOException, RaptorException {

		Document document = new Document();
		ReportHandler rh = new ReportHandler();
        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
		String pdfFName = "";
		String user_id = AppUtils.getUserID(request);
		response.reset();
		response.setContentType("application/pdf");
		OutputStream outStream = response.getOutputStream();
        
        String formattedReportName = "";
        PdfWriter writer = null;
        ReportRuntime firstReportRuntimeObj = null;
        int returnValue = 0;

        ReportRuntime rr = null;
        if(rr==null) rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
        
        boolean isDashboard = false;
        if ((request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null) && ( ((String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID)).equals(rr.getReportID())) ) {
        	isDashboard = true;
        }
		if(isDashboard) {
			try {
					String reportID = (String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID);
					ReportRuntime rrDash = rh.loadReportRuntime(request, reportID, true, 1);
					pb = preparePdfBean(request,rrDash);
		
					// Setting pb Values
			        document.setPageSize(PageSize.getRectangle(pb.getPagesize()));
					if(!pb.isPortrait()) // get this from properties file
						document.setPageSize(document.getPageSize().rotate());
			        
			        //			
					writer = PdfWriter.getInstance(document, response.getOutputStream());
					writer.setPageEvent(new PageEvent(pb));//header,footer,bookmark
					document.open();

					formattedReportName = new HtmlStripper().stripSpecialCharacters(rrDash.getReportName());
			        if(pb.isAttachmentOfEmail())
			        	response.setHeader("Content-disposition", "inline");
			        else
			        	response.setHeader("Content-disposition", "attachment;filename="+ formattedReportName+formattedDate+user_id+".pdf");
					
					pdfFName = "dashboard"+formattedReportName+formattedDate+user_id+".pdf";
					Map reportRuntimeMap = null;
					Map reportDataMap = null;
					Map reportDisplayTypeMap = null;

					reportRuntimeMap 			= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
					reportDataMap 				= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
					reportDisplayTypeMap 		= (TreeMap) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP);

					if(reportRuntimeMap!=null) {
						//ServletOutputStream sos = response.getOutputStream();
						Set setReportRuntime 		= reportRuntimeMap.entrySet();
						Set setReportDataMap 		= reportDataMap.entrySet();
						Set setReportDisplayTypeMap = reportDisplayTypeMap.entrySet();
						
						Iterator iter2 = setReportDataMap.iterator();
						Iterator iter3 = setReportDisplayTypeMap.iterator();
		                int count = 0;
						for(Iterator iter = setReportRuntime.iterator(); iter.hasNext(); ) {
							count++;
							Map.Entry entryData 		= (Entry) iter2.next();
							Map.Entry entry 			= (Entry) iter.next();
							Map.Entry entryCheckChart	= (Entry) iter3.next();
							//String rep_id 				= (String) entry.getKey();
							ReportRuntime rrDashRep 	= (ReportRuntime) entry.getValue();
							
							if(count == 1)  { 
								firstReportRuntimeObj = (ReportRuntime) entry.getValue();
								if(pb.isCoverPageIncluded()) {
									document = paintDashboardCoverPage(document, rrDash, firstReportRuntimeObj, request);
								}
							}
							ReportData rdDashRep 		= (ReportData) entryData.getValue();
					        int col = 0;
					        //pb.setDisplayChart(nvl(rr.getChartType()).trim().length()>0 && rr.getDisplayChart());
							if( ((rrDashRep.getChartType()).trim().length()>0 && rrDashRep.getDisplayChart()) && entryCheckChart.getValue().toString().equals("c")) {
								document.newPage();
								pb.setTitle(nvl(rrDashRep.getReportTitle()).length()>0?rrDashRep.getReportTitle():rrDashRep.getReportName());
								paintPdfImage(request, document,AppUtils.getTempFolderPath()+"cr_"+  pb.getUserId()+"_"+request.getSession().getId()+"_"+rrDashRep.getReportID()+".png", rrDashRep);
							} else {
								document.newPage();
								pb.setTitle(nvl(rrDashRep.getReportTitle()).length()>0?rrDashRep.getReportTitle():rrDashRep.getReportName());
								paintPdfData(request, document,rdDashRep,rrDashRep, "");
							}
						}
					
				}
			} catch (DocumentException dex) {dex.printStackTrace();}
			catch (RaptorException rex) {rex.printStackTrace();}
		} else {

			//ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
			//ReportData    rd = (ReportData)    request.getSession().getAttribute(AppConstants.RI_REPORT_DATA);
			rr = null;
			ReportData    rd = null;
			String parent = "";
			int parentFlag = 0;
			if(!nvl(request.getParameter("parent"), "").equals("N")) parent = nvl(request.getParameter("parent"), "");
			if(parent.startsWith("parent_")) parentFlag = 1;
			if(parentFlag == 1) {
				rr = (ReportRuntime) request.getSession().getAttribute(parent+"_rr");
				rd = (ReportData) request.getSession().getAttribute(parent+"_rd");
			}
			if(rr==null) rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
			if(rd==null) rd = (ReportData)    request.getSession().getAttribute(AppConstants.RI_REPORT_DATA);
			
			pb = preparePdfBean(request,rr);
			FONT_FAMILY = rr.getPDFFont();
			FONT_SIZE = rr.getPDFFontSize();
			//System.out.println(pb);
					
			formattedReportName = new HtmlStripper().stripSpecialCharacters(rr.getReportName());
			
			
			
	        response.setContentType("application/pdf");
	        if(pb.isAttachmentOfEmail())
	        	response.setHeader("Content-disposition", "inline");
	        else
	        	response.setHeader("Content-disposition", "attachment;filename="+ formattedReportName+formattedDate+user_id+".pdf");
		        
			document.setPageSize(PageSize.getRectangle(pb.getPagesize()));
			
			if(!pb.isPortrait()) // get this from properties file
				document.setPageSize(document.getPageSize().rotate());
	
			try {
				
				writer = PdfWriter.getInstance(document, outStream);
				writer.setPageEvent(new PageEvent(pb));//header,footer,bookmark
				document.open();
				
				//System.out.println("Document 1 " + document);
				if(pb.isCoverPageIncluded()) {
					document = paintCoverPage(document, rr, request);
				}
				
				//boolean isImageRotate = false;
				//System.out.println("Document 2 " + document);
	
				if(pb.isDisplayChart()) {
					paintPdfImage(request, document,AppUtils.getTempFolderPath()+"cr_"+  pb.getUserId()+"_"+request.getSession().getId()+"_"+rr.getReportID()+".png", rr);
				}
				//System.out.println("Document 4" + document);
	
				document.newPage();
		        if(type == 3 && rr.getSemaphoreList()==null && !(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) ) { //type = 3 is whole
			        String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
			        returnValue = paintPdfData(request, document, rd, rr, sql_whole);
		        } else if(type == 2) {
		        	returnValue = paintPdfData(request, document, rd, rr, "");
		        } else {
			        //String sql_whole = (String) request.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE);
					int downloadLimit = (rr.getMaxRowsInExcelDownload()>0)?rr.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
					String action = request.getParameter(AppConstants.RI_ACTION);

					if(!(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) && !action.endsWith("session"))
						rd 		= rr.loadReportData(-1, AppUtils.getUserID(request), downloadLimit,request, false /*download*/);
					if(rr.getSemaphoreList()!=null) {
						rd   =  rr.loadReportData(-1, AppUtils.getUserID(request), downloadLimit,request, true);
						returnValue = paintPdfData(request, document, rd, rr, "");
					} else {
						returnValue = paintPdfData(request, document, rd, rr, rr.getWholeSQL());
					}
					
					
		        }
		        
				
				//paintPdfData(document,rd,rr);
		        
			
			} catch (DocumentException de) {
	            de.printStackTrace();
	            //System.err.println("document: " + de.getMessage());
	        }
			
		}
		document.close();
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();		
    	logger.debug(EELFLoggerDelegate.debugLogger, ("##### Heap utilization statistics [MB] #####"));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Used Memory:"
    			+ (runtime.maxMemory() - runtime.freeMemory()) / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Free Memory:"
    			+ runtime.freeMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Total Memory:" + runtime.totalMemory() / mb));
    	logger.debug(EELFLoggerDelegate.debugLogger, ("Max Memory:" + runtime.maxMemory() / mb));

	}
	
	private Document paintCoverPage(Document doc, ReportRuntime rr, HttpServletRequest request) throws IOException, DocumentException {
		
		//System.out.println("PDFREPORTHANDLER STARTED ... " );
		if(nvl(rr.getPdfImg()).length()>0) {
			Image image1 = Image.getInstance(AppUtils.getExcelTemplatePath()+"../../"+AppUtils.getImgFolderURL()+rr.getPdfImg());
			image1.scalePercent(20f, 20f);
			doc.add(image1);
		}
		float firstColumnSize = Globals.getCoverPageFirstColumnSize();
		float[] relativeWidths = {firstColumnSize,1f-firstColumnSize};
		PdfPTable table = new PdfPTable(relativeWidths);
		table.getDefaultCell().setBorderWidth(0);
		addEmptyRows(table,6);
		HTMLWorker worker = new HTMLWorker(doc);
    	StyleSheet style = new StyleSheet();
    	style.loadTagStyle("body", "leading", "16,0");
    	StringBuffer reportDescrBuf = new StringBuffer("");
    	ArrayList descr = HTMLWorker.parseToList(new StringReader(nvl(rr.getReportDescr())), style);
    	ArrayList paraList = null;
    	   if(nvl(rr.getReportTitle()).length()>0) {
   			add2Cells(table,"Report Title 	: ",nvl(rr.getReportTitle()));
    	   } else {
			add2Cells(table,"Report Name 	: ",nvl(rr.getReportName()));
    	   }
			if((descr!=null && descr.size()>0)) {
				paraList = (com.lowagie.text.Paragraph)descr.get(0);
				for (int i=0 ; i<paraList.size(); i++) {
					reportDescrBuf.append(paraList.get(i));
				}
				
			}
			add2Cells(table,"Description 	: ",reportDescrBuf.toString());
		if(Globals.getSessionInfoForTheCoverPage().length()>0) {
			String nameValue[] = Globals.getSessionInfoForTheCoverPage().split(",");
				String name=nameValue[0];
				String value=nameValue[1];
				add2Cells(table,name+" : ",(AppUtils.getRequestNvlValue(request, value).length()>0?AppUtils.getRequestNvlValue(request, value):nvl((String)request.getSession().getAttribute(value))));
		}
		
		if(Globals.isCreatedOwnerInfoNeeded()) {
			add2Cells(table,"Created By 	: ",nvl(AppUtils.getUserName(rr.getCreateID())));
			add2Cells(table,"Owner 			: ",nvl(AppUtils.getUserName(rr.getOwnerID())));
		}
		if(Globals.displayLoginIdForDownloadedBy())
			add2Cells(table,"Downloaded by 	: ",nvl(AppUtils.getUserBackdoorLoginId(request)));
		else
			add2Cells(table,"Downloaded by 	: ",nvl(AppUtils.getUserName(AppUtils.getUserID(request))));
		
		addEmptyRows(table,1);

		boolean isFirstRow = true;
        ArrayList al = rr.getParamNameValuePairsforPDFExcel(request, 1);
        if(al.size()<=0) {
        	al = (ArrayList) request.getSession().getAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
        }

		Iterator it = al.iterator();
		addEmptyRows(table,1);
		//if(!Globals.customizeFormFieldInfo()) {
		if(rr.getFormFieldComments(request).length()<=0) {
			while(it.hasNext()) {
		
				if(isFirstRow) {
			add2Cells(table, "Run-time Criteria : ", " ");
					isFirstRow = false;
				}
					
				IdNameValue value = (IdNameValue)it.next();
				if(!value.getId().trim().equals("BLANK"))
					//System.out.println("PDFREPORTHANDLER " + value.getId()+" : "+value.getName());
					add2Cells(table, value.getId()+" : ",value.getName().replaceAll("~",","));
					//add2Cells(table, rr.getFormFieldComments(request), " ");
			}
			addEmptyRows(table,1);
			doc.add(table);
			
		} else {
	        it = al.iterator();
	        if(it.hasNext()) {
	        	//add2Cells(table, "Run-time Criteria : ", " ");
	        	addEmptyRows(table,1);	        	
	        	doc.add(table);
	        	//com.lowagie.text.html.HtmlParser.parse(doc, new StringReader(rr.getFormFieldComments(request)));
	        	ArrayList p = HTMLWorker.parseToList(new StringReader(rr.getFormFieldComments(request).replaceAll("~",",")), style);
	        	
	        	 for (int k = 0; k < p.size(); ++k){
	        	    doc.add((com.lowagie.text.Element)p.get(k));
	        	 }
	        }
		} 
		
		return doc;		
	}
	

	private Document paintDashboardCoverPage(Document doc, ReportRuntime rrDashRep, ReportRuntime firstReportRuntimeObj, HttpServletRequest request) throws IOException, DocumentException {
		
		//System.out.println("PDFREPORTHANDLER STARTED ... " );
		float firstColumnSize = Globals.getCoverPageFirstColumnSize();
		float[] relativeWidths = {firstColumnSize,1f-firstColumnSize};
		PdfPTable table = new PdfPTable(relativeWidths);
		table.getDefaultCell().setBorderWidth(0);
		addEmptyRows(table,6);

		add2Cells(table,"Report Name : ",rrDashRep.getReportName());
		add2Cells(table,"Description : ",rrDashRep.getReportDescr());
		if(Globals.getSessionInfoForTheCoverPage().length()>0) {
			String nameValue[] = Globals.getSessionInfoForTheCoverPage().split(",");
				String name=nameValue[0];
				String value=nameValue[1];
				add2Cells(table,name+" : ",(AppUtils.getRequestNvlValue(request, value).length()>0?AppUtils.getRequestNvlValue(request, value):nvl((String)request.getSession().getAttribute(value))));
		}
		
		if(Globals.isCreatedOwnerInfoNeeded()) {
			add2Cells(table,"Created By : ",AppUtils.getUserName(rrDashRep.getCreateID()));
			add2Cells(table,"Owner : ",AppUtils.getUserName(rrDashRep.getOwnerID()));
		}
		if(Globals.displayLoginIdForDownloadedBy())
			add2Cells(table,"Downloaded by : ",AppUtils.getUserBackdoorLoginId(request));
		else
			add2Cells(table,"Downloaded by : ",AppUtils.getUserName(request));
		
		addEmptyRows(table,1);

		boolean isFirstRow = true;
		ArrayList al = firstReportRuntimeObj.getParamNameValuePairsforPDFExcel(request, 2);
		Iterator it = al.iterator();
		addEmptyRows(table,1);
		//if(!Globals.customizeFormFieldInfo()) {
		if(firstReportRuntimeObj.getFormFieldComments(request).length()<=0) {
			while(it.hasNext()) {
		
				if(isFirstRow) {
					add2Cells(table, "Run-time Criteria : ", " ");
					isFirstRow = false;
				}
					
				IdNameValue value = (IdNameValue)it.next();
				if(!value.getId().trim().equals("BLANK"))
					//System.out.println("PDFREPORTHANDLER " + value.getId()+" : "+value.getName());
					add2Cells(table, value.getId()+" : ",value.getName());
					//add2Cells(table, rr.getFormFieldComments(request), " ");
			}
			addEmptyRows(table,1);
			doc.add(table);
			
		} else {
	        it = al.iterator();
	        if(it.hasNext()) {
	        	//add2Cells(table, "Run-time Criteria : ", " ");
	        	addEmptyRows(table,1);	        	
	        	doc.add(table);
	        	//com.lowagie.text.html.HtmlParser.parse(doc, new StringReader(rr.getFormFieldComments(request)));
	        	HTMLWorker worker = new HTMLWorker(doc);
	        	StyleSheet style = new StyleSheet();
	        	style.loadTagStyle("body", "leading", "16,0");
	        	ArrayList p = HTMLWorker.parseToList(new StringReader(firstReportRuntimeObj.getFormFieldComments(request)), style);
	        	
	        	 for (int k = 0; k < p.size(); ++k){
	        	    doc.add((com.lowagie.text.Element)p.get(k));
	        	 }
	        }
		} 
		
		return doc;		
	}
	
	
	public static void addEmptyRows(PdfPTable table, int rows) throws DocumentException {
		for (int i=0; i<rows; i++) 
			for(int j=0;j<table.getAbsoluteWidths().length;j++)
				table.addCell(new Paragraph(" "));
		
	}
	
	private void add2Cells(PdfPTable table, String key, String value) {
		
		PdfPCell cell;
		cell = new PdfPCell(new Paragraph(key));
		cell.setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
		cell.setBorderWidth(0f);
		table.addCell(cell);
		
		cell = new PdfPCell(new Paragraph(value));
		cell.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
		cell.setBorderWidth(0f);
		table.addCell(cell);
	}

	private void paintPdfImage(HttpServletRequest request, Document document, String fileName, ReportRuntime rr) 
				throws DocumentException
	{
		
		ArrayList images = getImage(request, fileName,pb.isAttachmentOfEmail()?true:false, rr);
		//Image image = getImage(request, fileName,pb.isAttachmentOfEmail()?true:false);
		PdfPTable table =  null;
		PdfPCell cellValue = null;
		if(images!=null) {
			
	        for (int i = 0; i < images.size(); i++) {
	        	table = new PdfPTable(1);
	        	cellValue = new PdfPCell();
	        	cellValue.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
                Image image = (Image) images.get(i);				
    			image.setAlignment(Image.ALIGN_CENTER);
    			//System.out.println("Document 3 " + document + " i-" + i);
    			if(i%2 ==0)
    			document.newPage();
    			//System.out.println("Document 31 " + document);
    			cellValue.setImage(image);
    			//table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
    			table.addCell(cellValue);
    			//System.out.println("Document 32 " + document + "table  " + table);
    			document.add(table);
       			//System.out.println("Document 33 " + document);    			
			}
		}
	}
	
	private ArrayList getImage(HttpServletRequest request, String fileName, boolean isGenerateNewImage, ReportRuntime rr) {
		ArrayList images = new ArrayList();
		if(!isGenerateNewImage) {
			try {
				Image image = Image.getInstance(fileName);
				images.add(image);
				return images;
			} 
			catch (MalformedURLException e) {
				isGenerateNewImage = true;
				//e.printStackTrace();			
			} 
			catch (BadElementException e) {
				isGenerateNewImage = true;
				//e.printStackTrace();

			} catch (FileNotFoundException e) {
				isGenerateNewImage = true;
				//e.printStackTrace();
			} catch (IOException e) {
				isGenerateNewImage = true;
				//e.printStackTrace();
		    }			
		}
		
		if(isGenerateNewImage && retryCreateNewImageCount<RetryCreateNewImage){
			retryCreateNewImageCount++;
			return generateNewImage(request, rr);
			//return getImage(request,fileName, false);
		}
		
		return null;
			
	}

	private ArrayList generateNewImage(HttpServletRequest request, ReportRuntime rr) {
		ArrayList images = new ArrayList();
		try {
			//ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
			DataSet ds = null;
			if(request.getSession().getAttribute(AppConstants.RI_CHART_DATA)!=null) {
				ds = (DataSet) request.getSession().getAttribute(AppConstants.RI_CHART_DATA);
			} else {
				ds = rr.loadChartData(pb.getUserId(),request);
			}
		    String downloadFileName = "";
			HashMap additionalChartOptionsMap = new HashMap();
			String chartType = nvl(rr.getChartType());
			if(chartType.equals(AppConstants.GT_PIE_MULTIPLE)) {
				additionalChartOptionsMap.put("multiplePieOrderRow", new Boolean((AppUtils.getRequestNvlValue(request, "multiplePieOrder").length()>0?AppUtils.getRequestNvlValue(request, "multiplePieOrder").equals("row"):rr.isMultiplePieOrderByRow())) );
				additionalChartOptionsMap.put("multiplePieLabelDisplay", AppUtils.getRequestNvlValue(request, "multiplePieLabelDisplay").length()>0? AppUtils.getRequestNvlValue(request, "multiplePieLabelDisplay"):rr.getMultiplePieLabelDisplay());
				additionalChartOptionsMap.put("chartDisplay", new Boolean(AppUtils.getRequestNvlValue(request, "chartDisplay").length()>0? AppUtils.getRequestNvlValue(request, "chartDisplay").equals("3D"):rr.isChartDisplayIn3D()));
			} else if (chartType.equals(AppConstants.GT_BAR_3D)) {
				additionalChartOptionsMap.put("chartOrientation", new Boolean((AppUtils.getRequestNvlValue(request, "chartOrientation").length()>0?AppUtils.getRequestNvlValue(request, "chartOrientation").equals("vertical"):rr.isVerticalOrientation())) );
				additionalChartOptionsMap.put("secondaryChartRenderer", AppUtils.getRequestNvlValue(request, "secondaryChartRenderer").length()>0? AppUtils.getRequestNvlValue(request, "secondaryChartRenderer"):rr.getSecondaryChartRenderer());
				additionalChartOptionsMap.put("chartDisplay", new Boolean(AppUtils.getRequestNvlValue(request, "chartDisplay").length()>0? AppUtils.getRequestNvlValue(request, "chartDisplay").equals("3D"):rr.isChartDisplayIn3D()));
				additionalChartOptionsMap.put("lastSeriesALineChart", new Boolean(rr.isLastSeriesALineChart()));		
			} else if (chartType.equals(AppConstants.GT_LINE)) {
				additionalChartOptionsMap.put("chartOrientation", new Boolean((AppUtils.getRequestNvlValue(request, "chartOrientation").length()>0?AppUtils.getRequestNvlValue(request, "chartOrientation").equals("vertical"):rr.isVerticalOrientation())) );
				//additionalChartOptionsMap.put("secondaryChartRenderer", AppUtils.getRequestNvlValue(request, "secondaryChartRenderer").length()>0? AppUtils.getRequestNvlValue(request, "secondaryChartRenderer"):rr.getSecondaryChartRenderer());
				additionalChartOptionsMap.put("chartDisplay", new Boolean(AppUtils.getRequestNvlValue(request, "chartDisplay").length()>0? AppUtils.getRequestNvlValue(request, "chartDisplay").equals("3D"):rr.isChartDisplayIn3D()));
				additionalChartOptionsMap.put("lastSeriesABarChart", new Boolean(rr.isLastSeriesABarChart()));
			} else if (chartType.equals(AppConstants.GT_TIME_DIFFERENCE_CHART)) {
				additionalChartOptionsMap.put("intervalFromDate",AppUtils.getRequestNvlValue(request, "intervalFromDate").length()>0?AppUtils.getRequestNvlValue(request, "intervalFromDate"):rr.getIntervalFromdate());
				additionalChartOptionsMap.put("intervalToDate", AppUtils.getRequestNvlValue(request, "intervalToDate").length()>0? AppUtils.getRequestNvlValue(request, "intervalToDate"):rr.getIntervalTodate());
				additionalChartOptionsMap.put("intervalLabel", AppUtils.getRequestNvlValue(request, "intervalLabel").length()>0? AppUtils.getRequestNvlValue(request, "intervalLabel"):rr.getIntervalLabel());
			} else if (chartType.equals(AppConstants.GT_REGRESSION)) {
				additionalChartOptionsMap.put("regressionType",AppUtils.getRequestNvlValue(request, "regressionType").length()>0?AppUtils.getRequestNvlValue(request, "regressionType"):rr.getLinearRegression());
				additionalChartOptionsMap.put("linearRegressionColor",nvl(rr.getLinearRegressionColor()));
				additionalChartOptionsMap.put("expRegressionColor",nvl(rr.getExponentialRegressionColor()));
				additionalChartOptionsMap.put("maxRegression",nvl(rr.getCustomizedRegressionPoint()));
			} else if (chartType.equals(AppConstants.GT_STACK_BAR) ||chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR) || chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR_LINES)
		           || chartType.equals(AppConstants.GT_STACKED_VERT_BAR) || chartType.equals(AppConstants.GT_STACKED_VERT_BAR_LINES) 	
	    		) {
				additionalChartOptionsMap.put("overlayItemValue",new Boolean(nvl(rr.getOverlayItemValueOnStackBar()).equals("Y")));
			}
			additionalChartOptionsMap.put("legendPosition", nvl(rr.getLegendPosition()));
			additionalChartOptionsMap.put("hideToolTips", new Boolean(rr.hideChartToolTips()));
			additionalChartOptionsMap.put("hideLegend", new Boolean(AppUtils.getRequestNvlValue(request, "hideLegend").length()>0? AppUtils.getRequestNvlValue(request, "hideLegend").equals("Y"):rr.hideChartLegend()));
			additionalChartOptionsMap.put("labelAngle", nvl(rr.getLegendLabelAngle()));
			additionalChartOptionsMap.put("maxLabelsInDomainAxis", nvl(rr.getMaxLabelsInDomainAxis()));
			additionalChartOptionsMap.put("rangeAxisLowerLimit", nvl(rr.getRangeAxisLowerLimit()));
			additionalChartOptionsMap.put("rangeAxisUpperLimit", nvl(rr.getRangeAxisUpperLimit()));
			
		    
			boolean totalOnChart = false;
			totalOnChart = AppUtils.getRequestNvlValue(request, "totalOnChart").equals("Y");
			String filename  = null;
			ArrayList graphURL  = new ArrayList();
			ArrayList chartNames = new ArrayList();
			ArrayList fileNames = new ArrayList(); 
		    List l = rr.getAllColumns();
		    List lGroups = rr.getAllChartGroups();
		    HashMap mapYAxis = rr.getAllChartYAxis(rr.getReportParamValues());
		    String chartLeftAxisLabel = rr.getFormFieldFilled(nvl(rr.getChartLeftAxisLabel()));
    		String chartRightAxisLabel = rr.getFormFieldFilled(nvl(rr.getChartRightAxisLabel()));
		    int displayTotalOnChart = 0;
		    HashMap formValues = Globals.getRequestParamtersMap(request, false);

		    for (Iterator iterC = l.iterator(); iterC.hasNext();) {
				DataColumnType dc = (DataColumnType) iterC.next();
				if(nvl(dc.getColName()).equals(AppConstants.RI_CHART_TOTAL_COL)) {
					displayTotalOnChart = 1;
				}
			}
		    
		    String legendColumnName = (rr.getChartLegendColumn()!=null)?rr.getChartLegendColumn().getDisplayName():"Legend Column";
		    
		    
		    
			if(ds!=null)
			{
				   if(rr.hasSeriesColumn() && chartType.equals(AppConstants.GT_TIME_SERIES) && (lGroups==null || lGroups.size() <= 0)) { /** Check whether Report has only category  columns if so then all the columns will open in seperate chart - sundar**/
		                for (int i=0; i<rr.getChartValueColumnAxisList(AppConstants.CHART_ALL_COLUMNS, formValues).size();i++) {
		                String chartTitle = Globals.getDisplayChartTitle()? rr.getReportName():"";
		                chartTitle = rr.getFormFieldFilled(chartTitle);
						downloadFileName = AppUtils.getTempFolderPath()+"cr_"+pb.getUserId()+"_"+request.getSession().getId()+"_"+rr.getReportID()+"_"+i+".png";
						filename = null;/*(String) ChartGen.generateChart(  chartType,
													request.getSession(),
													ds,
													legendColumnName, 
													chartLeftAxisLabel,
													chartRightAxisLabel,
													rr.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues).subList(i, i+1), 
													rr.getChartColumnColorsList(AppConstants.CHART_ALL_COLUMNS, formValues).subList(i, i+1), 
													rr.getChartValueColumnAxisList(AppConstants.CHART_ALL_COLUMNS, formValues).subList(i, i+1), 
													"",
													chartTitle,
													null,
													rr.getChartWidthAsInt(),
													rr.getChartHeightAsInt(),
								                    rr.getChartValueColumnsList(AppConstants.CHART_ALL_COLUMNS, formValues).subList(i,i+1),
								                    rr.hasSeriesColumn(),
								                    //rr.isChartMultiSeries(),
								                    rr.isMultiSeries(),
								                    rr.getAllColumns(),
					                                downloadFileName,
					                                totalOnChart, 
					                                AppConstants.WEB_VERSION deviceType,
					                                additionalChartOptionsMap,
					                                true
					                );*/
					        	try {
				        			Image image = Image.getInstance(downloadFileName);
			                        images.add(image);
				        		} catch (MalformedURLException e) {
				    				e.printStackTrace();			
				    			} 
				    			catch (BadElementException e) {
				    				e.printStackTrace();

				    			} catch (FileNotFoundException e) {
				    				e.printStackTrace();
				    			} catch (IOException e) {
				    				e.printStackTrace();
				    		    }	
				}
					   
				   } else { /** first check the columns to be opened in new charts and loop around in ChartGen generate chart function  - sundar**/
	                    String tempChartGroupPrev = "";
				        String tempChartGroupCurrent = "";
		                for (int i=0; i<lGroups.size();i++) {
		                	String chartGroupOrg = (String) lGroups.get(i);
		                	String chartYAxis = (String) mapYAxis.get(chartGroupOrg);
		                	//System.out.println("chartGroupOrg " + chartGroupOrg);
		                	if(nvl(chartGroupOrg).length()>0)
		                		tempChartGroupCurrent = chartGroupOrg.substring(0,chartGroupOrg.lastIndexOf("|"));
		                	if(i>0) tempChartGroupPrev = ((String) lGroups.get(i-1)).substring(0,((String) lGroups.get(i-1)).lastIndexOf("|"));
		                	//System.out.println("TEMPCHARTGROUP " + tempChartGroupCurrent + " " + tempChartGroupPrev);
		                	if(tempChartGroupCurrent.equals(tempChartGroupPrev)) continue;
		                	//System.out.println("CHARTGROUPORG " + chartGroupOrg + " " + lGroups) ;
		                	//String chartGroup = chartGroupOrg.substring(0,chartGroupOrg.lastIndexOf("|"));
		                	String chartGroup = chartGroupOrg;
		                	
		                	//System.out.println("$$$$CHARTGROUP in JSP  " +chartGroup+ " "+ chartGroupOrg );
			  				   //System.out.println(" rr.getChartGroupDisplayNamesList(chartGroup) " + rr.getChartGroupDisplayNamesList(chartGroup));
			 				   //System.out.println(" rr.getChartGroupColumnColorsList(chartGroup) " + rr.getChartGroupColumnColorsList(chartGroup));
			 				   //System.out.println(" rr.getChartGroupColumnAxisList(chartGroup) " + rr.getChartGroupColumnAxisList(chartGroup));
			 				   //System.out.println(" rr.getChartGroupValueColumnAxisList(chartGroupOrg) " + rr.getChartGroupValueColumnAxisList(chartGroupOrg));
		                	
							downloadFileName = AppUtils.getTempFolderPath()+"cr_"+pb.getUserId()+"_"+request.getSession().getId()+"_"+rr.getReportID()+"_"+i+".png";
							String chartTitle = (Globals.getDisplayChartTitle()? (chartGroup!=null && chartGroup.indexOf("|") > 0 ?chartGroup.substring(0,chartGroup.lastIndexOf("|")):rr.getReportName()):"");
							chartTitle = rr.getFormFieldFilled(chartTitle);
							String leftAxisLabel = "";
							//if(!rr.isChartMultiSeries()) {
							  if(!rr.isMultiSeries()) {
								leftAxisLabel = ((chartYAxis!=null && chartYAxis.indexOf("|") > 0) ? chartYAxis.substring(0,chartYAxis.lastIndexOf("|")): chartLeftAxisLabel );
							} else {
								leftAxisLabel = chartLeftAxisLabel;
							}

				   	    	filename = null;/*(String) ChartGen.generateChart(  chartType,
																request.getSession(),
																ds,
																legendColumnName,  
																leftAxisLabel,
																chartRightAxisLabel,
																((chartType.indexOf("Stacked")>0 || chartType.equals(AppConstants.GT_PIE_MULTIPLE) || chartType.equals(AppConstants.GT_BAR_3D))?rr.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartGroupDisplayNamesList(chartGroup, formValues)), 
																((chartType.indexOf("Stacked")>0 || chartType.equals(AppConstants.GT_PIE_MULTIPLE) || chartType.equals(AppConstants.GT_BAR_3D))?rr.getChartColumnColorsList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartGroupColumnColorsList(chartGroup, formValues)), 
																((chartType.indexOf("Stacked")>0 || chartType.equals(AppConstants.GT_PIE_MULTIPLE) || chartType.equals(AppConstants.GT_BAR_3D))?rr.getChartValueColumnAxisList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartGroupValueColumnAxisList(chartGroupOrg, formValues)), 
																"",
																chartTitle,
																null,
																rr.getChartWidthAsInt(),
																rr.getChartHeightAsInt(),
																((chartType.indexOf("Stacked")>0 || chartType.equals(AppConstants.GT_PIE_MULTIPLE))?rr.getChartValueColumnsList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues):rr.getChartGroupValueColumnAxisList(chartGroupOrg, formValues)),
											                    rr.hasSeriesColumn(),
											                    //rr.isChartMultiSeries(),
											                    rr.isMultiSeries(),
											                    rr.getAllColumns(),
								                                downloadFileName,
								                                totalOnChart, 
								                                AppConstants.WEB_VERSION deviceType, 
								                                additionalChartOptionsMap,
								                                true
								  );*/
					        	try {
				        			Image image = Image.getInstance(downloadFileName);
			                        images.add(image);
				        		} catch (MalformedURLException e) {
				    				e.printStackTrace();			
				    			} 
				    			catch (BadElementException e) {
				    				e.printStackTrace();

				    			} catch (FileNotFoundException e) {
				    				e.printStackTrace();
				    			} catch (IOException e) {
				    				e.printStackTrace();
				    		    }	
						}
					   
		            if(!chartType.equals(AppConstants.GT_PIE_MULTIPLE)) {    
	                for (int i=0; i<rr.getChartValueColumnAxisList(AppConstants.CHART_NEWCHART_COLUMNS, formValues).size();i++) { 
	  				   //System.out.println(" rr.getChartDisplayNamesList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i, i+1) " + rr.getChartDisplayNamesList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i, i+1));
	 				   //System.out.println(" rr.getChartValueColumnAxisList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i, i+1) " + rr.getChartValueColumnAxisList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i, i+1));
	 				   //System.out.println(" rr.getChartValueColumnsList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i,i+1) " + rr.getChartValueColumnsList(AppConstants.CHART_NEWCHART_COLUMNS).subList(i,i+1));

						downloadFileName = AppUtils.getTempFolderPath()+"cr_"+ pb.getUserId()+"_"+request.getSession().getId()+"_"+rr.getReportID()+"_"+i+".png";
		                String chartTitle = Globals.getDisplayChartTitle()? rr.getReportName():"";
		                chartTitle = rr.getFormFieldFilled(chartTitle);

	   	    	filename = null;/* (String) ChartGen.generateChart(  chartType,
													request.getSession(),
													ds,
													legendColumnName, 
													chartLeftAxisLabel,
													chartRightAxisLabel,
													(chartType.equals(AppConstants.GT_PIE_MULTIPLE))?rr.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartDisplayNamesList(AppConstants.CHART_NEWCHART_COLUMNS, formValues).subList(i, i+1), 
													(chartType.equals(AppConstants.GT_PIE_MULTIPLE))?rr.getChartColumnColorsList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartColumnColorsList(AppConstants.CHART_NEWCHART_COLUMNS, formValues).subList(i, i+1), 
													(chartType.equals(AppConstants.GT_PIE_MULTIPLE))?rr.getChartValueColumnAxisList(AppConstants.CHART_ALL_COLUMNS, formValues):rr.getChartValueColumnAxisList(AppConstants.CHART_NEWCHART_COLUMNS, formValues).subList(i, i+1), 
													"",
													chartTitle,
													null,
													rr.getChartWidthAsInt(),
													rr.getChartHeightAsInt(),
					                                rr.getChartValueColumnsList(AppConstants.CHART_NEWCHART_COLUMNS, formValues).subList(i,i+1),
								                    rr.hasSeriesColumn(),
								                    //rr.isChartMultiSeries(),
								                    rr.isMultiSeries(),
								                    rr.getAllColumns(),
					                                downloadFileName,
					                                totalOnChart, 
					                                AppConstants.WEB_VERSION, 
					                                additionalChartOptionsMap,
					                                true
					  );
*/					        	try {
				        			Image image = Image.getInstance(downloadFileName);
			                        images.add(image);
				        		} catch (MalformedURLException e) {
				    				e.printStackTrace();			
				    			} 
				    			catch (BadElementException e) {
				    				e.printStackTrace();

				    			} catch (FileNotFoundException e) {
				    				e.printStackTrace();
				    			} catch (IOException e) {
				    				e.printStackTrace();
				    		    }	
						}
		            }
	                /** second rest of the columns are merged to one single chart  - sundar**/
	  				  // System.out.println(" rr.getChartDisplayNamesList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS) " + rr.getChartDisplayNamesList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS));
	 				  // System.out.println(" rr.getChartValueColumnAxisList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS) " + rr.getChartValueColumnAxisList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS));
	 				  // System.out.println(" rr.getChartValueColumnsList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS) " + rr.getChartValueColumnsList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS));

	 				  if((!(lGroups!=null && lGroups.size() > 0))) {
	 					  
	 				   if(/*chartType.equals(AppConstants.GT_TIME_SERIES) && */rr.getChartDisplayNamesList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues)!=null && rr.getChartDisplayNamesList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues).size()>0) {
	            	downloadFileName = AppUtils.getTempFolderPath()+"cr_"+  pb.getUserId()+"_"+request.getSession().getId()+"_"+rr.getReportID()+"_All.png";
	                String chartTitle = Globals.getDisplayChartTitle()? rr.getReportName():"";
	                chartTitle = rr.getFormFieldFilled(chartTitle);

			filename = null;/*(String) ChartGen.generateChart(  chartType,
													request.getSession(),
													ds,
													legendColumnName, 
													chartLeftAxisLabel,
													chartRightAxisLabel,
													rr.getChartDisplayNamesList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues), 
													rr.getChartColumnColorsList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues), 
													rr.getChartValueColumnAxisList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues), 
													"",
													chartTitle,
													null,
													rr.getChartWidthAsInt(),
													rr.getChartHeightAsInt(),
					                                rr.getChartValueColumnsList(AppConstants.CHART_WITHOUT_NEWCHART_COLUMNS, formValues),
								                    rr.hasSeriesColumn(),
								                  //rr.isChartMultiSeries(),
								                    rr.isMultiSeries(),
								                    rr.getAllColumns(),
					                                downloadFileName,
					                                totalOnChart, 
					                                AppConstants.WEB_VERSION, 
					                                additionalChartOptionsMap,
					                                true
					  );
*/					        	try {
				        			Image image = Image.getInstance(downloadFileName);
			                        images.add(image);
				        		} catch (MalformedURLException e) {
				    				e.printStackTrace();			
				    			} 
				    			catch (BadElementException e) {
				    				e.printStackTrace();

				    			} catch (FileNotFoundException e) {
				    				e.printStackTrace();
				    			} catch (IOException e) {
				    				e.printStackTrace();
				    		    }					  
	 			     }
	 				  } // Stacked Chart Check   
				   } // else no Series Column

			}// if(ds!=null)
			
		}catch (Exception e) {
				e.printStackTrace();
		}
//		System.out.println("Total Images " + images.size());
		return images.size()>0?images:null;
		
	}

/*
	private boolean isImageRotate(Document doc, Image image) {
		
		System.out.println("image size="+image.getWidthPercentage()+ " "+ image.scaledWidth()+ 
							" "+image.scaledHeight()+" "+image.getXYRatio());
		System.out.println("page size = "+ doc.getPageSize().width() + " " +doc.getPageSize().height() +" "+ 
				   doc.topMargin() + " " +doc.bottomMargin() + " " +   doc.leftMargin() + " " +
				   doc.rightMargin());
		System.out.println(image.scaledWidth()/image.scaledHeight());
		System.out.println((PageEvent.getPageWidth(doc)/PageEvent.getPageHeight(doc)));
//		System.out.println(doc.getPageSize().getRotation());
		
		float image_w = image.scaledWidth();
		float image_h = image.scaledHeight();
		float image_ratio = image_w/image_h;
		
		float page_w = PageEvent.getPageWidth(doc);
		float page_h = PageEvent.getPageHeight(doc);
		float page_ratio = page_w/page_h;
		
		return  (image_w > page_w && image_ratio > page_ratio) ||
				(image_h > page_h && image_ratio < page_ratio);

	}
	
*/
	private final int DEFAULT_PDF_DISPLAY_WIDTH = 10;
	private int paintPdfData(HttpServletRequest request, Document document, ReportData rd, ReportRuntime rr, String sql_whole) throws DocumentException, RaptorException, IOException  {
		
    	int mb = 1024*1024;
    	Runtime runtime = Runtime.getRuntime();
    	int returnValue = 0;
    	sql_whole = rr.getWholeSQL();
        if(rd.getDataRowCount() >= rr.getReportDataSize()) {
        	sql_whole="";
        }
    	float f[] = getRelativeWidths(rd, rr.getReportType().equals(AppConstants.RT_CROSSTAB));
		PdfPTable table = new PdfPTable(f);
		table.setWidthPercentage(100f);
		table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
		table.getDefaultCell().setVerticalAlignment(Rectangle.ALIGN_BOTTOM);
		
		ReportDefinition rdef = (new ReportHandler()).loadReportDefinition(request, rr.getReportID());
		
		List allColumns = rdef.getAllColumns();
		
		float[] repotWidths = new float[rdef.getVisibleColumnCount()];
		int columnIdx = 0;
		float pdfDisplayWidth = 0;
		for(Iterator iter = allColumns.iterator(); iter.hasNext();){
			DataColumnType dct = (DataColumnType) iter.next();
			if(dct.isVisible()) {
			
			if(dct.getPdfDisplayWidthInPxls() == null || dct.getPdfDisplayWidthInPxls().equals("") || dct.getPdfDisplayWidthInPxls().startsWith("null"))
				pdfDisplayWidth = DEFAULT_PDF_DISPLAY_WIDTH;
			else
				pdfDisplayWidth = Float.parseFloat(dct.getPdfDisplayWidthInPxls());
			
			repotWidths [columnIdx++] = pdfDisplayWidth;
			}
		}		
		
		table.setWidths(repotWidths);
		
		//table.setH
		
		//TODO: check title and subtitle
		HttpSession session = request.getSession();
		String drilldown_index = (String) session.getAttribute("drilldown_index");
		int index = 0;
		try {
		 index = Integer.parseInt(drilldown_index);
		} catch (NumberFormatException ex) {
			index = 0;
		}		
		String titleRep = (String) session.getAttribute("TITLE_"+index);
		String subtitle = (String) session.getAttribute("SUBTITLE_"+index);
		
		if(nvl(titleRep).length()>0 && nvl(subtitle).length()>0)
			table.setHeaderRows(3);
		else if (nvl(titleRep).length()>0)
			table.setHeaderRows(2);
		else
			table.setHeaderRows(1);
		table = paintPdfReportHeader(request, document, table, rr, f);
		paintPdfTableHeader(document, rd, table);
		
		int idx = 0;
		int fragmentsize = 30; //for memory management
		
		ResultSet rs = null;
        Connection conn = null;
        Statement st = null;
        ResultSetMetaData rsmd = null;
        rd.reportDataRows.resetNext();
        DataRow dr = rd.reportDataRows.getNext();
			
 		//addRowHeader(table,dr,idx,rd);

 			//addRowColumns(table,dr,idx);
 	    	if(nvl(sql_whole).length() >0 && rr.getReportType().equals(AppConstants.RT_LINEAR)) {
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
 	   	            dr = null;
 	   	            int j = 0;
 	   	            int rowCount = 0;
 	   	            String title = "";
 	   	       		while(rs.next()) {
 	   	       			
/* 		       			if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*Globals.getMemoryThreshold()/100) ) { 
 		       				returnValue = 1;
 		       				String cellValue = Globals.getUserDefinedMessageForMemoryLimitReached() + " "+ rowCount +" records out of " + rr.getReportDataSize() + " were downloaded to PDF.";
 		       				Font cellFont = FontFactory.getFont(Globals.getDataFontFamily(), 
 		       													Globals.getDataFontSize(),
 		       													Font.NORMAL, Color.BLACK);
 		       				PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
 		       				table.addCell(cell);
 		       				document.add(table);
 		       				return returnValue;
 		       			}
*/ 		       			rowCount++;
 		    			colHash = new HashMap();
 		    			for (int i = 1; i <= numberOfColumns; i++) {
 		    				colHash.put(rsmd.getColumnName(i), rs.getString(i));
 		    			}
 		    			rd.reportDataRows.resetNext();
 		    			
 		    			dr = rd.reportDataRows.getNext();
 		    			
 		    			j = 0;
 		    			/*if(rd.reportTotalRowHeaderCols!=null) {
 		    			
	    					HtmlFormatter rfmt = dr.getRowFormatter();

	    					Font cellFont = FontFactory.getFont(Globals.getDataFontFamily(), 
	    														Globals.getDataFontSize(),
	    														Font.NORMAL, Color.BLACK);
	    					if(rfmt != null) {
	    						cellFormatterFont(rfmt,cellFont);
	    					}
	    					
	    					String cellValue = new Integer(rowCount).toString();
	    					PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
	    					
	    					//row background color can be overwritten by cell background color
	    					cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
	    					
    						cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
	    					
    						if(rfmt != null) {
	    						formatterCell(rfmt,cell);
	    					}
	    					table.addCell(cell);
 		    			}*/
 		    			
 		    			for (dr.resetNext(); dr.hasNext();j++) {
			    				DataValue dv = dr.getNext();
		    					/*if(j == 0) {
 			    					HtmlFormatter cfmt = dv.getCellFormatter();
 			    					HtmlFormatter rfmt = dv.getRowFormatter();

 			    					Font cellFont = FontFactory.getFont(Globals.getDataFontFamily(), 
 			    														Globals.getDataFontSize(),
 			    														Font.NORMAL, Color.BLACK);
 			    					if(cfmt!= null) {
 			    						cellFormatterFont(cfmt,cellFont);
 			    					}
 			    					else if(rfmt != null) {
 			    						cellFormatterFont(rfmt,cellFont);
 			    					}
 			    					else {
 			    						if(dv.isBold()) {
 			    							cellFont.setStyle(Font.BOLD);
 			    						}
 			    					}
 			    					
 			    					//String cellValue = strip.stripHtml(value.trim());
 			    					PdfPCell cell = new PdfPCell(new Paragraph(rowCount+"",cellFont));
 			    					
 			    					//row background color can be overwritten by cell background color
 			    					cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
 			    					
 			    					if(nvl(dv.getAlignment()).trim().length()>0)
 			    						cell.setHorizontalAlignment(ElementTags.alignmentValue(dv.getAlignment()));
 			    					else
 			    						cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
 			    					
 			    					if(cfmt!= null) {
 			    						formatterCell(cfmt,cell);
 			    					}
 			    					else if(rfmt != null) {
 			    						formatterCell(rfmt,cell);
 			    					}
 			    					table.addCell(cell);
			    				}*/
 		    				
 			    			//for (chr.resetNext(); chr.hasNext();) {
 			    				//ColumnHeader ch = chr.getNext();
 			    				String value = nvl((String)colHash.get(dv.getColId().toUpperCase()));
 			    				if(dv.isVisible()) {
 			    					
 			    					HtmlFormatter cfmt = dv.getCellFormatter();
 			    					HtmlFormatter rfmt = dv.getRowFormatter();

 			    					Font cellFont = FontFactory.getFont(FONT_FAMILY, 
 			    														FONT_SIZE,
 			    														Font.NORMAL, Color.BLACK);
 			    					if(cfmt!= null) {
 			    						cellFormatterFont(cfmt,cellFont);
 			    					}
 			    					else if(rfmt != null) {
 			    						cellFormatterFont(rfmt,cellFont);
 			    					}
 			    					else {
 			    						if(dv.isBold()) {
 			    							cellFont.setStyle(Font.BOLD);
 			    						}
 			    					}
 			    					
 			    					String cellValue = strip.stripHtml(value.trim());
 			    					PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
 			    					
 			    					//row background color can be overwritten by cell background color
 			    					cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
 			    					
 			    					if(nvl(dv.getAlignment()).trim().length()>0)
 			    						cell.setHorizontalAlignment(ElementTags.alignmentValue(dv.getAlignment()));
 			    					else
 			    						cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
 			    					
 			    					if(cfmt!= null) {
 			    						formatterCell(cfmt,cell);
 			    					}
 			    					else if(rfmt != null) {
 			    						formatterCell(rfmt,cell);
 			    					}
 			    					
 			    					
 			    					
 			    					table.addCell(cell);
 			    				
 			    				}//if isVisible()
 			    				
 			    				
 		    			}
 		    			
 	   	       		}
 		       		if(rd.reportDataTotalRow!=null) {
 						for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();idx++) {
 							dr = rd.reportDataTotalRow.getNext();
 							table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
 							Font rowHeaderFont = FontFactory.getFont(FONT_FAMILY, 
 									FONT_SIZE,
 									Font.NORMAL, Color.BLACK);
 							rowHeaderFont.setStyle(Font.BOLD);
 							rowHeaderFont.setSize(FONT_SIZE+1f);
 							table.getDefaultCell().setBackgroundColor(getRowBackgroundColor(dr, idx));
 								table.addCell(new Paragraph("Total",rowHeaderFont));
 						

 			 	 			addTotalRowColumns(table,dr,idx);
 			 				if (idx % fragmentsize == fragmentsize - 1) {
 			 					document.add(table);
 			 					table.deleteBodyRows();
 			 					table.setSkipFirstHeader(true);
 			 				}
 			
 						}
 		    		} 	   	       		
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
 			
 			
//			if (idx % fragmentsize == fragmentsize - 1) {
//				document.add(table);
//				table.deleteBodyRows();
//				table.setSkipFirstHeader(true);
//			}
 
        //document.add(table);
 	    } else {
 	    	 if(rr.getReportType().equals(AppConstants.RT_LINEAR)) {
 	    	int rowCount = 0;
 			for(rd.reportDataRows.resetNext();rd.reportDataRows.hasNext();idx++)
 			{	
	       		rowCount++;
	       		
	       		/*if(rd.reportTotalRowHeaderCols!=null) { 
					HtmlFormatter rfmt = dr.getRowFormatter();
	
					Font cellFont = FontFactory.getFont(Globals.getDataFontFamily(), 
														Globals.getDataFontSize(),
														Font.NORMAL, Color.BLACK);
					if(rfmt != null) {
						cellFormatterFont(rfmt,cellFont);
					}
					
					//String cellValue = new Integer(rowCount).toString();
					//PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
					
					//row background color can be overwritten by cell background color
					//cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
					
					//cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
					
					//if(rfmt != null) {
						//formatterCell(rfmt,cell);
					//}
					//table.addCell(cell);
	       		}*/
	       		
	       		
	       		
 				if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*Globals.getMemoryThreshold()/100) ) { 
		       				returnValue = 1;
		       			}
 				
 	 			dr = rd.reportDataRows.getNext();
 				
 	 			addRowHeader(table,dr,idx,rd);

 	 			addRowColumns(table,dr,idx);
 	 			
 				if (idx % fragmentsize == fragmentsize - 1) {
 					document.add(table);
 					table.deleteBodyRows();
 					table.setSkipFirstHeader(true);
 				}
 			}
 			
	       		if(rd.reportDataTotalRow!=null) {
					for (rd.reportDataTotalRow.resetNext(); rd.reportDataTotalRow.hasNext();idx++) {
						dr = rd.reportDataTotalRow.getNext();
						table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
						Font rowHeaderFont = FontFactory.getFont(FONT_FAMILY, 
								FONT_SIZE,
								Font.NORMAL, Color.BLACK);
						rowHeaderFont.setStyle(Font.BOLD);
						rowHeaderFont.setSize(FONT_SIZE+1f);
						table.getDefaultCell().setBackgroundColor(getRowBackgroundColor(dr, idx));
							table.addCell(new Paragraph("Total",rowHeaderFont));
					

		 	 			addTotalRowColumns(table,dr,idx);
		 				if (idx % fragmentsize == fragmentsize - 1) {
		 					document.add(table);
		 					table.deleteBodyRows();
		 					table.setSkipFirstHeader(true);
		 				}
		
					}
	    		} 	   	       		

 	    	 } else if (rr.getReportType().equals(AppConstants.RT_CROSSTAB)) {
       		    int rowCount = 0;
     		    List l = rd.getReportDataList();
     		    boolean first = true;
       			for (int dataRow = 0; dataRow < l.size(); dataRow++) {
       				first = true;
       		       		rowCount++;
       		       		dr = (DataRow) l.get(dataRow);
          				Vector<DataValue> rowNames = dr.getRowValues();
          				for(dr.resetNext(); dr.hasNext(); ) {
          					
          				if(first) {
       						HtmlFormatter rfmt = dr.getRowFormatter();
       			       		
       						Font cellFont = FontFactory.getFont(FONT_FAMILY, 
       															FONT_SIZE,
       															Font.NORMAL, Color.BLACK);
       						if(rfmt != null) {
       							cellFormatterFont(rfmt,cellFont);
       						}
       						String cellValue = "";
       						PdfPCell cell = null;
       						//String cellValue = new Integer(rowCount).toString();
       						//PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
       					//row background color can be overwritten by cell background color
       						//cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
       						
       						//cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
       						
       						//if(rfmt != null) {
       							//formatterCell(rfmt,cell);
       					//	}
       						//table.addCell(cell);
    	                    if(rowNames!=null) {
    	                        for(int i=0; i<rowNames.size(); i++) {
    	                        	DataValue dv = rowNames.get(i);
    	       						rfmt = dr.getRowFormatter();
    	       			       		
    	       						cellFont = FontFactory.getFont(FONT_FAMILY, 
    	       															FONT_SIZE,
    	       															Font.NORMAL, Color.BLACK);
    	       						if(rfmt != null) {
    	       							cellFormatterFont(rfmt,cellFont);
    	       						}
    	       						cellValue = dv.getDisplayValue();
    	    	    				if(cellValue.indexOf("|#")!=-1)
    	    	    					cellValue = cellValue.substring(0,cellValue.indexOf("|"));
    	    	    				
    	       						cell = new PdfPCell(new Paragraph(cellValue,cellFont));
    	       					//row background color can be overwritten by cell background color
    	       						cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
    	       						
    	       						cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
    	       						
    	       						if(rfmt != null) {
    	       							formatterCell(rfmt,cell);
    	       						}
    	       						table.addCell(cell);
    	                        }
    	                        }
    	                   }
    					first = false;
       		       		
       	 				if(runtime.freeMemory()/mb <= ((runtime.maxMemory()/mb)*Globals.getMemoryThreshold()/100) ) { 
       			       				returnValue = 1;
       			       			}
       	 				
       	 	 			//addRowHeader(table,dr,idx,rd);

       	 	 			addRowColumns(table,dr,idx);
       	 	 			
       	 				if (idx % fragmentsize == fragmentsize - 1) {
       	 					document.add(table);
       	 					table.deleteBodyRows();
       	 					table.setSkipFirstHeader(true);
       	 				}
       	 			}

       			}
 	    	 }
 	    
				//document.add(table);

 	    }
 	    	
 	    document.add(table);
 	    paintPdfReportFooter(request, document, rr, f);
 	    
 	    return returnValue;	
	}
	
	private void addRowHeader(PdfPTable table, DataRow dr, int idx, ReportData rd) {
		
		table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);	

		for(rd.reportRowHeaderCols.resetNext();rd.reportRowHeaderCols.hasNext();) {
			RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
			if(idx==0) 
				rhc.resetNext();
			RowHeader rh = rhc.getNext();
			//System.out.println(" =============== RowHeader\n "+rh);
			
			Font rowHeaderFont = FontFactory.getFont(FONT_FAMILY, 
													FONT_SIZE,
													Font.NORMAL, Color.BLACK);
			if(rh.isBold()) {
				rowHeaderFont.setStyle(Font.BOLD);
				rowHeaderFont.setSize(FONT_SIZE+1f);
			}
			
			if(rh.getColSpan()>0) {
				table.getDefaultCell().setColspan(rh.getColSpan());
				table.getDefaultCell().setBackgroundColor(getRowBackgroundColor(dr, idx));
				table.addCell(new Paragraph(strip.stripHtml(rh.getRowTitle()),rowHeaderFont));
			}
		}		
	}

	private void addRowColumns(PdfPTable table, DataRow dr, int idx) {
			
		table.getDefaultCell().setColspan(1);
			
		for(dr.resetNext();dr.hasNext();)
		{
			DataValue dv = dr.getNext();
			//System.out.println(columnCount +" --> "+dv);
			if(dv.isVisible()) {
				HtmlFormatter cfmt = dv.getCellFormatter();
				HtmlFormatter rfmt = dv.getRowFormatter();

				Font cellFont = FontFactory.getFont(FONT_FAMILY, 
													FONT_SIZE,
													Font.NORMAL, Color.BLACK);
				if(cfmt!= null) {
					cellFormatterFont(cfmt,cellFont);
				}
				else if(rfmt != null) {
					cellFormatterFont(rfmt,cellFont);
				}
				else {
					if(dv.isBold()) {
						cellFont.setStyle(Font.BOLD);
					}
				}
				
				String cellValue = strip.stripHtml(dv.getDisplayValue().trim());
				PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
				
				//row background color can be overwritten by cell background color
				cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
				
				if(nvl(dv.getAlignment()).trim().length()>0)
					cell.setHorizontalAlignment(ElementTags.alignmentValue(dv.getAlignment()));
				else
					cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
				
				if(cfmt!= null) {
					formatterCell(cfmt,cell);
				}
				else if(rfmt != null) {
					formatterCell(rfmt,cell);
				}
				
				table.addCell(cell);
			
			}//if isVisible()
		}					
	}


	private void addTotalRowColumns(PdfPTable table, DataRow dr, int idx) {
		
		table.getDefaultCell().setColspan(1);
		dr.resetNext();
		dr.getNext();
		for(;dr.hasNext();)
		{
			DataValue dv = dr.getNext();
			//System.out.println(columnCount +" --> "+dv);
			if(dv.isVisible()) {
				HtmlFormatter cfmt = dv.getCellFormatter();
				HtmlFormatter rfmt = dv.getRowFormatter();

				Font cellFont = FontFactory.getFont(FONT_FAMILY, 
													FONT_SIZE,
													Font.NORMAL, Color.BLACK);
				if(cfmt!= null) {
					cellFormatterFont(cfmt,cellFont);
				}
				else if(rfmt != null) {
					cellFormatterFont(rfmt,cellFont);
				}
				else {
					if(dv.isBold()) {
						cellFont.setStyle(Font.BOLD);
					}
				}
				
				String cellValue = strip.stripHtml(dv.getDisplayValue().trim());
				PdfPCell cell = new PdfPCell(new Paragraph(cellValue,cellFont));
				
				//row background color can be overwritten by cell background color
				cell.setBackgroundColor(getRowBackgroundColor(dr, idx));
				
				if(nvl(dv.getAlignment()).trim().length()>0)
					cell.setHorizontalAlignment(ElementTags.alignmentValue(dv.getAlignment()));
				else
					cell.setHorizontalAlignment(Rectangle.ALIGN_CENTER);
				
				if(cfmt!= null) {
					formatterCell(cfmt,cell);
				}
				else if(rfmt != null) {
					formatterCell(rfmt,cell);
				}
				
				table.addCell(cell);
			
			}//if isVisible()
		}					
	}
	
	
	private void formatterCell(HtmlFormatter fmt, PdfPCell cell) {
		
		if(nvl(fmt.getBgColor()).trim().length()>0)
			cell.setBackgroundColor(Color.decode(fmt.getBgColor()));
		if(nvl(fmt.getAlignment()).trim().length()>0)
			cell.setHorizontalAlignment(ElementTags.alignmentValue(fmt.getAlignment()));
	}

	private void cellFormatterFont(HtmlFormatter fmt, Font font) {
		
		if(fmt.isBold()) 
			font.setStyle(Font.BOLD);
		if(fmt.isItalic()) 
			font.setStyle(Font.ITALIC);
		if(fmt.isUnderline()) 
			font.setStyle(Font.UNDERLINE);
		if(fmt.getFontColor().trim().length()>0)
			font.setColor(Color.decode(fmt.getFontColor()));
		if(fmt.getFontSize().trim().length()>0)
			font.setSize(Float.parseFloat(fmt.getFontSize())-Globals.getDataFontSizeOffset());
//		if(fmt.getFontFace().trim().length()>0)
//			cellFont.setFamily()
		
	}

	private Color getRowBackgroundColor(DataRow dr, int idx) {
		
		Color color =  Color.decode(Globals.getDataDefaultBackgroundHexCode());
		
		HtmlFormatter rhf = dr.getRowFormatter();
		if(rhf!=null && nvl(rhf.getBgColor()).trim().length()>0)
			
			color = Color.decode(rhf.getBgColor());
		
		else if(pb.isAlternateColor() && idx%2==0)
			
			color = Color.decode(Globals.getDataBackgroundAlternateHexCode());
		
		return color;		

	}

	private int getTotalVisbleColumns(ReportData rd) {
		
		int totalVisbleColumn = rd.getTotalColumnCount();
		for(rd.reportDataRows.resetNext();rd.reportDataRows.hasNext();)
		{	
 			DataRow dr = rd.reportDataRows.getNext();
			for(dr.resetNext();dr.hasNext();) {
				DataValue dv = dr.getNext();
				if(!dv.isVisible()) totalVisbleColumn--;
			}
			
			break;
		}
		
		return totalVisbleColumn;
	}

	/*
	private int getFirstRowIndex(ReportRuntime rr) {
		return (pb.getCurrentPage()>0)?pb.getCurrentPage()*rr.getPageSize()+1 : 1;
	}
  	*/
	private float[] getRelativeWidths(ReportData rd, boolean crosstab){
		
		int totalColumns = getTotalVisbleColumns(rd);
		/*if(rd.reportTotalRowHeaderCols!=null) {
			totalColumns += 1;
		}*/
		if(crosstab) {
			totalColumns += 1;
		}

		if(totalColumns == 0 )
			totalColumns=1;
		
		float[] relativeWidths = new float[totalColumns];
		//initial widths are even
		for(int i=0; i<relativeWidths.length; i++)
			relativeWidths[i] = 10f;
		
		int index=0;
		boolean firstPass = true;
		
		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) 
		{
			if(firstPass) {
				/*if(rd.reportTotalRowHeaderCols!=null) { 
					String columnWidth = "5";
					
					if(columnWidth != null && columnWidth.trim().endsWith("%"))
						relativeWidths[index] = Float.parseFloat(removeLastCharacter(columnWidth));
					
					index++;
				}*/
				
				for(rd.reportRowHeaderCols.resetNext();rd.reportRowHeaderCols.hasNext();) {
					String columnWidth = rd.reportRowHeaderCols.getNext().getColumnWidth();
					
					if(columnWidth != null && columnWidth.trim().endsWith("%"))
						relativeWidths[index] = Float.parseFloat(removeLastCharacter(columnWidth));
					
					index++;
				}
				firstPass = false;
			}
		
			ColumnHeaderRow chr = rd.reportColumnHeaderRows.getNext();
			for (chr.resetNext(); chr.hasNext();) {
				
				ColumnHeader ch = chr.getNext();

				if(ch.isVisible()) {
					
					String columnWidth = ch.getColumnWidth();
					
					if(ch.getColSpan() <= 1){
						if(columnWidth != null && columnWidth.trim().endsWith("%")) 
							relativeWidths[index] = Float.parseFloat(removeLastCharacter(columnWidth));
					} 
					else {
						for(int i=0; i<ch.getColSpan(); i++) {
							index += i;
							if(columnWidth != null && columnWidth.trim().endsWith("%"))
								relativeWidths[index] = 
									(Float.parseFloat(removeLastCharacter(columnWidth)))/ch.getColSpan();							
						}
					}
					
					index++;
				}
			}
		}
		
		return relativeWidths;
	}
	
	public static String removeLastCharacter(String str) {
		return str.substring(0, str.length()-1);
	}
	
	private PdfPTable paintPdfReportHeader(HttpServletRequest request, Document document, PdfPTable table, ReportRuntime rr, float[] f) 
			throws DocumentException, IOException {
		
		HttpSession session = request.getSession();
		String drilldown_index = (String) session.getAttribute("drilldown_index");
		int index = 0;
		try {
		 index = Integer.parseInt(drilldown_index);
		} catch (NumberFormatException ex) {
			index = 0;
		}
		String title = (String) session.getAttribute("TITLE_"+index);
		String subtitle = (String) session.getAttribute("SUBTITLE_"+index);
		if(nvl(title).length()>0) {
			//PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100f);
			table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Rectangle.ALIGN_BOTTOM);
	        
	
			Font font = FontFactory.getFont(FONT_FAMILY, 
					FONT_SIZE-2f,
					Font.BOLD, 
					Color.BLACK);
		
			//addEmptyRows(table,1);
			table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			//table.getDefaultCell().setBackgroundColor(Color.decode(Globals.getDataTableHeaderBackgroundFontColor()));
		    title = Utils.replaceInString(title, "<BR/>", " ");
		    title = Utils.replaceInString(title, "<br/>", " ");
		    title = Utils.replaceInString(title, "<br>", " ");
			title  = strip.stripHtml(nvl(title).trim());
			//subtitle = Utils.replaceInString(subtitle, "<BR/>", " ");
			//subtitle = Utils.replaceInString(subtitle, "<br/>", " ");
			//subtitle = Utils.replaceInString(subtitle, "<br>", " ");
			//subtitle  = strip.stripHtml(nvl(subtitle).trim());
			StyleSheet styles = new StyleSheet();
			
			HTMLWorker htmlWorker = new HTMLWorker(document); 
			ArrayList cc = new ArrayList(); 
			cc = htmlWorker.parseToList(new StringReader(subtitle), styles); 
			                     	
			Phrase p1 = new Phrase(); 
			for (int i = 0; i < cc.size(); i++){ 
				Element elem = (Element)cc.get(i); 
				ArrayList al  = elem.getChunks();
				for (int j = 0; j < al.size(); j++) {
					Chunk chunk = (Chunk) al.get(j);
					chunk.font().setSize(6.0f);
				}
				p1.add(elem); 
			} 
			//cell = new PdfPCell(p1);
	    	StyleSheet style = new StyleSheet();
	    	style.loadTagStyle("font", "font-size", "3");
	    	style.loadTagStyle("font", "size", "3");
            styles.loadStyle("pdfFont1", "size", "11px");                                 
            styles.loadStyle("pdfFont1", "font-size", "11px"); 
        	/*ArrayList p = HTMLWorker.parseToList(new StringReader(nvl(title)), style);
        	for (int k = 0; k < p.size(); ++k){
        		document.add((com.lowagie.text.Element)p.get(k));
       	 	}*/
            //p1.font().setSize(3.0f);
			PdfPCell titleCell = new PdfPCell(new Phrase(title, font));
			titleCell.setColspan(rr.getVisibleColumnCount());
			PdfPCell subtitleCell = new PdfPCell(p1);
			subtitleCell.setColspan(rr.getVisibleColumnCount());
			titleCell.setHorizontalAlignment(1);
			subtitleCell.setHorizontalAlignment(1);
			table.addCell(titleCell);
			table.addCell(subtitleCell);
			//document.add(table);
		}
		return table;
	}


	private void paintPdfReportFooter(HttpServletRequest request, Document document, ReportRuntime rr, float[] f) 
			throws DocumentException, IOException {
		
		HttpSession session = request.getSession();
		String drilldown_index = (String) session.getAttribute("drilldown_index");
		int index = 0;
		try {
		 index = Integer.parseInt(drilldown_index);
		} catch (NumberFormatException ex) {
			index = 0;
		}

		String title = (String) session.getAttribute("FOOTER_"+index);
		if(nvl(title).length()>0) {
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100f);
			table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Rectangle.ALIGN_BOTTOM);
	        
			Font font = FontFactory.getFont(FONT_FAMILY, 
					FONT_SIZE-3f,
					Font.BOLD, 
					Color.BLACK);
		
			
			//addEmptyRows(table,1);
			table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			//table.getDefaultCell().setBackgroundColor(Color.decode(Globals.getDataTableHeaderBackgroundFontColor()));
		    /*title = Utils.replaceInString(title, "<BR/>", " ");
		    title = Utils.replaceInString(title, "<br/>", " ");
		    title = Utils.replaceInString(title, "<br>", " ");
			title  = strip.stripHtml(nvl(title).trim());*/
	    	StyleSheet style = new StyleSheet();
	    	
			HTMLWorker htmlWorker = new HTMLWorker(document); 
			ArrayList cc = new ArrayList(); 
			cc = htmlWorker.parseToList(new StringReader(title), style); 
			                     	
			Phrase p1 = new Phrase(); 
			for (int i = 0; i < cc.size(); i++){ 
				Element elem = (Element)cc.get(i); 
				ArrayList al  = elem.getChunks();
				for (int j = 0; j < al.size(); j++) {
					Chunk chunk = (Chunk) al.get(j);
					chunk.font().setSize(6.0f);
				}
				p1.add(elem); 
			} 
	    	
/*			
			HTMLWorker.parseToList(new StringReader(nvl(title)), style);*/
			PdfPCell titleCell = new PdfPCell(p1);
			titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			table.addCell(titleCell);
			//table.
			document.add(table);
		}
		//return table;
	}
	
	
	private void paintPdfTableHeader(Document document, ReportData rd, PdfPTable table) 
																throws DocumentException {
			
		Font font = FontFactory.getFont(FONT_FAMILY, 
										FONT_SIZE+1f,
										Font.BOLD, 
										Color.decode(Globals.getDataTableHeaderFontColor()));
		//table.setHeaderRows(1);
		table.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
		table.getDefaultCell().setBackgroundColor(Color.decode(Globals.getDataTableHeaderBackgroundFontColor()));
		String title = "";
		
		boolean firstPass = true;
		
		/*if(rd.reportTotalRowHeaderCols!=null) {
			if(firstPass) {
				table.addCell(new Paragraph("No.", font));
				firstPass = false;
			}
		}*/		
		for (rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext();) 
		{
			if(firstPass) {
				for(rd.reportRowHeaderCols.resetNext();rd.reportRowHeaderCols.hasNext();) {
					/*if(firstPass) {
						table.addCell(new Paragraph("No.", font));
						firstPass = false;
					} else {*/
						RowHeaderCol rhc = rd.reportRowHeaderCols.getNext();
						title = rhc.getColumnTitle();
	    				title = Utils.replaceInString(title,"_nl_", " \n");
						table.addCell(new Paragraph(title,font));
					//}
				}
			}
			
			ColumnHeaderRow chr = rd.reportColumnHeaderRows.getNext();
			for (chr.resetNext(); chr.hasNext();) {
				ColumnHeader ch = chr.getNext();
				//System.out.println(ch);
				if(ch.isVisible()) {
					title = ch.getColumnTitle();
    				title = Utils.replaceInString(title,"_nl_", " \n");
					table.addCell(new Paragraph(title,font));
				}
			}
		}
	}
	
	public static String currentTime(String pattern) {
		try {
	        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
	        Date sysdate = oracleDateFormat.parse(ReportLoader.getSystemDateTime());
	        SimpleDateFormat dtimestamp = new SimpleDateFormat(Globals.getScheduleDatePattern());
	        return dtimestamp.format(sysdate)+" "+Globals.getTimeZone();
	        //paramList.add(new IdNameValue("DATE", dtimestamp.format(sysdate)+" "+Globals.getTimeZone()));
        } catch(Exception ex) {}	 
		
		SimpleDateFormat s = new SimpleDateFormat(pattern);
		s.setTimeZone(TimeZone.getTimeZone(Globals.getTimeZone()));
		//System.out.println("^^^^^^^^^^^^^^^^^^^^ " + Calendar.getInstance().getTime());
		//System.out.println("^^^^^^^^^^^^^^^^^^^^ " + s.format(Calendar.getInstance().getTime()));
		return s.format(Calendar.getInstance().getTime());
	}

	private PdfBean preparePdfBean(HttpServletRequest request,ReportRuntime rr) {
		PdfBean pb = new PdfBean();

		pb.setUserId(AppUtils.getUserID(request));

		pb.setWhereToShowPageNumber(Globals.getPageNumberPosition());
		pb.setAlternateColor(Globals.isDataAlternateColor());
		pb.setTimestampPattern(Globals.getDatePattern());

		int temp = -1;
		try {
			temp = Integer.parseInt(request.getParameter(AppConstants.RI_NEXT_PAGE));
		} catch (NumberFormatException e) {}		
		pb.setCurrentPage(temp);
	
		//pb.setPortrait( trueORfalse(request.getParameter("isPortrait"),true));
		pb.setPortrait(trueORfalse(rr.getPDFOrientation() == "portait"?"true":"false", true));
		//pb.setCoverPageIncluded( trueORfalse(request.getParameter("isCoverPageIncluded"), true));
		//if(Globals.isCoverPageNeeded()) {
			pb.setCoverPageIncluded(Globals.isCoverPageNeeded()?rr.isPDFCoverPage():false);
		//}
		pb.setTitle(nvl(request.getParameter("title")));
		pb.setPagesize(nvls(request.getParameter("pagesize"),"LETTER"));
		
		pb.setLogo1Url(rr.getPDFLogo1());
		pb.setLogo2Url(rr.getPDFLogo2());
		pb.setLogo1Size(rr.getPDFLogo1Size());
		pb.setLogo2Size(rr.getPDFLogo2Size());
		pb.setFullWebContextPath(request.getSession().getServletContext().getRealPath(File.separator));
		

		pb.setDisplayChart(nvl(rr.getChartType()).trim().length()>0 && rr.getDisplayChart());
			
		String id = nvl(request.getParameter("pdfAttachmentKey")).trim();
		String log_id = nvl(request.getParameter("log_id")).trim();
		if(id.length()>0 && log_id.length()>0)
			pb.setAttachmentOfEmail(true);
		
		return pb;
	}
	
	private boolean trueORfalse(String str) {
		return (str != null) && (str.equalsIgnoreCase("true"));
	}
	
	private boolean trueORfalse(String str,boolean b_default) {
		return str==null ? b_default : (str.equalsIgnoreCase("true"));
	}
	
   
}
