/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
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
package org.onap.portalsdk.analytics.model.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.Globals;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

class PageEvent extends PdfPageEventHelper {
	private PdfBean pb;
	private int pageNo = 0;
	private int omit_page_count = 0;
	private int DEFAULT_LOGO_SIZE = 100;

	public PageEvent(PdfBean pb) {
		this.pb = pb;
	}

	private int getWidthEntries(int howManyLogos){
		int widthEntries = 0;
		
		if(howManyLogos == 2)
			widthEntries = 3;
		else
		if(howManyLogos == 1)
			widthEntries = 2;
		else
			widthEntries = 0;
		
		return widthEntries;
	}
	
	private int getHowManyLogos(){
		int howManyLogos = 0;
		
		if(AppUtils.isNotEmpty(pb.getLogo1Url()) && !pb.getLogo1Url().equalsIgnoreCase("<no logo>"))
			howManyLogos ++;
		
		if(AppUtils.isNotEmpty(pb.getLogo2Url()) && !pb.getLogo2Url().equalsIgnoreCase("<no logo>"))
			howManyLogos ++;
		
		return howManyLogos;
	}
	
	private float[] fillWidthsArray(int howManyLogos){
		float[] widthsArray = new float[howManyLogos + 1];
		
		//If one logo, we will need two columns in the header[left log, spacer] 
		if(howManyLogos == 1){
			widthsArray = new float[2];
			widthsArray[0] = 0.1f;
			widthsArray[1] = 0.1f;
		}
		//If two logs, we will need three columns in the header [left log, spacer, right log]
		else
		if(howManyLogos == 2){
			widthsArray = new float[3];
			widthsArray[0] = 0.1f;
			widthsArray[1] = 0.5f;
			widthsArray[2] = 0.1f;
		}
		
		return widthsArray;
	}
	public void onStartPage(PdfWriter writer, Document document) {
		
		Font font = FontFactory.getFont(Globals.getFooterFontFamily(), Globals.getFooterFontSize(), Font.NORMAL, Color.BLACK);
		int howManyLogos = getHowManyLogos();
		
		//No need to draw anything in the header if no logo was set in the report.
		if(howManyLogos == 0)
			return;
		
		float[] widths = fillWidthsArray(howManyLogos);
				
		PdfPTable foot = new PdfPTable(widths);
		
		if(AppUtils.isNotEmpty(pb.getLogo1Url()) && !pb.getLogo1Url().equalsIgnoreCase("<no logo>"))
			addLogo(foot, font, pb.getLogo1Url().substring(pb.getLogo1Url().indexOf("|") + 1).trim(), Cell.ALIGN_LEFT, pb.getLogo1Size() == null ? DEFAULT_LOGO_SIZE : pb.getLogo1Size());
		
		PdfPCell spacingCell = new PdfPCell();
		spacingCell.setBorderColor(Color.WHITE);
		foot.addCell(spacingCell);
		
		//Using logo1 size for now - use logo2 size if it is required to deal it separately.
		if(AppUtils.isNotEmpty(pb.getLogo2Url()) && !pb.getLogo2Url().equalsIgnoreCase("<no logo>"))
			addLogo(foot, font, pb.getLogo2Url().substring(pb.getLogo2Url().indexOf("|") + 1).trim(),  Cell.ALIGN_RIGHT, pb.getLogo2Size() == null ? DEFAULT_LOGO_SIZE : pb.getLogo2Size());

		foot.setTotalWidth(getPageWidth(document));
		foot.writeSelectedRows(0, -1, 36, 600, writer.getDirectContent());
	}

	public void onEndPage(PdfWriter writer, Document document) {

		Font font = FontFactory.getFont(Globals.getFooterFontFamily(), Globals.getFooterFontSize(), Font.NORMAL, Color.BLACK);

		try {

			// footer
			float[] f = { 1f, 0.4f, 1f };
			PdfPTable foot = new PdfPTable(f);
			foot.getDefaultCell().setBorderWidth(0);
			foot.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			foot.getDefaultCell().setVerticalAlignment(Rectangle.ALIGN_BOTTOM);

			foot.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_LEFT);
			addLeftFooter(foot, font);

			foot.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_CENTER);
			addPageNumber(foot, font, pb.isPageNumberAtFooter(), document.getPageNumber());

			foot.getDefaultCell().setHorizontalAlignment(Rectangle.ALIGN_RIGHT);
			foot.getDefaultCell().setNoWrap(true);

			foot.addCell(new Paragraph("                   " + PdfReportHandler.currentTime(pb.getTimestampPattern()), font));

			foot.setTotalWidth(getPageWidth(document));
			foot.writeSelectedRows(0, -1, document.leftMargin(), document.bottomMargin(), writer.getDirectContent());

			// bookmark
			pageNo++;
			PdfContentByte cb = writer.getDirectContent();
			PdfDestination destination = new PdfDestination(PdfDestination.FITH);
			String bookmark = "Data Page " + (pageNo - omit_page_count);
			if (pageNo == 1) {
				if (pb.isCoverPageIncluded()) {
					bookmark = "Cover Page";
					omit_page_count++;
				} else if (pb.isDisplayChart()) {
					bookmark = "Chart";
					omit_page_count++;
				}
			}
			if (pageNo == 2 && pb.isCoverPageIncluded() && pb.isDisplayChart()) {
				bookmark = "Chart";
				omit_page_count++;
			}

			PdfOutline outline = new PdfOutline(cb.getRootOutline(), destination, bookmark);

		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	private void addPageNumber(PdfPTable table, Font font, boolean isAdd, int pageNum) {
		if (isAdd)
			table.addCell(new Paragraph(Globals.getWordBeforePageNumber() + " " + pageNum + " " + Globals.getWordAfterPageNumber(), font));
		else
			table.addCell("");
	}

	private void addLeftFooter(PdfPTable table, Font font) {
		Font font1 = new Font(font);
		font1.setSize(Globals.getPDFFooterFontSize());

		if (isEmpty(pb.getLeftFooter()))
			table.addCell(new Paragraph("                 " + Globals.getPDFFooter(), font1));
		else
			table.addCell(new Paragraph(pb.getLeftFooter(), font));
	}

	private void addHeaderDummy(PdfPTable table, Font font) {
		Font font1 = new Font(font);
		font1.setSize(Globals.getPDFFooterFontSize());

		table.addCell(new Paragraph("Header row", font1));
	}

	private void addLogo(PdfPTable table, Font font, String imgSrc, int alignment, int absoluteSize) {

		Image img = null;
		try {
			img = Image.getInstance(pb.getFullWebContextPath() + AppUtils.getImgFolderURL() + File.separator + imgSrc);
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			img = null;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			img = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			
		}
		if(img == null){
			//log that the input file couldnt be loaded - 
		}
		else{
			//img.scaleAbsolute(absoluteSize, absoluteSize);
			img.scalePercent(absoluteSize, absoluteSize);
			PdfPCell cell = new PdfPCell(img);
			cell.setBorderColor(Color.WHITE);
			cell.setHorizontalAlignment(alignment);
			table.addCell(cell);
		}

	}

	public static float getPageWidth(Document doc) {
		return doc.getPageSize().width() - doc.leftMargin() - doc.rightMargin();
	}

	public static float getPageHeight(Document doc) {
		return doc.getPageSize().height() - doc.topMargin() - doc.bottomMargin();
	}

	private float getHeadTopMargin(Document doc, PdfPTable table) {
		return doc.getPageSize().height() - doc.topMargin() + table.getTotalHeight();
	}

	private boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

} // PageEvent
