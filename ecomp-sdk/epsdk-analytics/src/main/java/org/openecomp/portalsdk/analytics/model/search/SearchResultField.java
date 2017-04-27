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
package org.openecomp.portalsdk.analytics.model.search;

import org.openecomp.portalsdk.analytics.util.*;

public class SearchResultField extends org.openecomp.portalsdk.analytics.RaptorObject {
	private String columnId;
	private String displayValue = "";

	private String alignment = "Left";

	private String drillDownLink = null;

	private String drillDownImage = null;
	
	private String confirmationText = null;
	
	

	public String getConfirmationText() {
		return confirmationText;
	}

	public void setConfirmationText(String confirmationText) {
		this.confirmationText = confirmationText;
	}

	public String getDrillDownImage() {
		return drillDownImage;
	}

	public void setDrillDownImage(String drillDownImage) {
		this.drillDownImage = drillDownImage;
	}

	public SearchResultField() {
	}

	public SearchResultField(String displayValue, String linkIdValue,
			SearchResultColumn column, boolean isAuthorized) {
		super();

		if (!isAuthorized)
			return;

		setColumnId(column.getColumnId());
		setDisplayValue(displayValue);
		setAlignment(column.getAlignment());
		if(column.getColumnId().equals("edit")) {
//			setDrillDownLink("report_wizard.htm?action=report.edit&c_master="+linkIdValue);
			setDrillDownLink("report#/report_wizard/"+linkIdValue);
			setDrillDownImage(column.getLinkImg());
			setConfirmationText(null);
		} else if(column.getColumnId().equals("copy")) { 
//			setDrillDownLink("report_wizard.htm?action=report.copy&c_master="+linkIdValue);
			setDrillDownLink("report#/report_wizard/copy/"+linkIdValue);			
			setDrillDownImage(column.getLinkImg());
			setConfirmationText(column.getLinkConfirmMsg());	
		} else if(column.getColumnId().equals("delete")) { 
			setDrillDownLink("raptor.htm?action=report.delete&c_master="+linkIdValue);
			setDrillDownImage(column.getLinkImg());
			setConfirmationText(column.getLinkConfirmMsg());			
		} else if(column.getColumnId().equals("schedule")) { 
			setDrillDownLink("report_wizard.htm?action=report.schedule.report.submit_wmenu&c_master="+linkIdValue+"&refresh=Y");
			setDrillDownImage(column.getLinkImg());
			setConfirmationText(null);			
		} else if(column.getColumnId().equals("run")) { 
			setDrillDownLink("raptor.htm?action=report.run.container&c_master="+linkIdValue+"&refresh=Y");
			setDrillDownImage(column.getLinkImg());
			setConfirmationText(null);			
		} else { 
		if (column.getLinkURL() != null) {
			StringBuffer sb = new StringBuffer();

			if (column.getLinkForm() == null) {
				sb.append("<a href=\"");
				sb.append(column.getLinkURL());
				sb.append(nvl(linkIdValue));
				if (column.getLinkConfirmMsg() != null) {
					sb.append(" onClick=\"return confirm('");
					sb.append(column.getLinkConfirmMsg());
					sb.append("');\"");
				}
				sb.append("\">");
				if (column.getLinkImg() != null) {
					sb.append("<img src=\"");
					sb.append(column.getLinkImg());
					sb.append("\"");
					sb.append(column.getLinkImgSizeHtml());
					sb.append(" border=\"0\"");
					sb.append(column.getLinkTitle() != null ? " alt=\""
							+ column.getLinkTitle()/*
													 * +(column.isDeleteLink()?"
													 * "+linkIdValue:"")
													 */
							+ "\"" : "");
					sb.append(">");
				} else
					sb.append(column.getLinkTitle());
				sb.append("</a>");
			} else {
				sb.append("<input type=\"");
				if (column.getLinkImg() != null) {
					sb.append("image\" src=\"");
					sb.append(column.getLinkImg());
					sb.append("\"");
					sb.append(column.getLinkImgSizeHtml());
					sb.append(" border=\"0\"");
					sb.append(column.getLinkTitle() != null ? " alt=\""
							+ column.getLinkTitle()/*
													 * +(column.isDeleteLink()?"
													 * "+linkIdValue:"")
													 */
							+ "\"" : "");
				} else {
					sb.append("submit\" value=\"");
					sb.append(column.getLinkTitle());
					sb.append("\"");
				}
				sb.append(" onClick=\"");
				if (column.getLinkConfirmMsg() != null) {
					sb.append("if(! confirm('");
					sb.append(column.getLinkConfirmMsg());
					sb.append("')) return false; ");
				}
				sb.append(column.getLinkURL());
				sb.append(" document.");
				sb.append(column.getLinkForm());
				sb.append(".");
				sb.append(AppConstants.RI_REPORT_ID);
				sb.append(".value='");
				sb.append(nvl(linkIdValue));
				sb.append("';");
				sb.append(" document.");
				sb.append(column.getLinkForm());
				sb.append(".");
				sb.append("refresh");
				sb.append(".value='");
				sb.append("Y");
				sb.append("';\">");
			} // else

			setDrillDownLink(sb.toString());
		} // if
		}
	} // SearchResultField

	public String getDisplayValue() {
		return displayValue;
	}

	public String getAlignment() {
		return alignment;
	}

	public String getDrillDownLink() {
		return drillDownLink;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = nvl(displayValue);
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public void setDrillDownLink(String drillDownLink) {
		this.drillDownLink = drillDownLink;
	}

	public String getTooltipValue() {
		return (displayValue.length() == 0) ? "&nbsp;" : displayValue;
	}

	/*public String getAlignmentHtml() {
		return (alignment.length() == 0) ? "" : (" align=" + alignment);
	}*/

	/*public String getDisplayValueLinkHtml() {
		if (nvl(drillDownLink).length() == 0)
			return getDisplayValueHtml();
		else
			return getDrillDownLink();
	} // getDisplayValueLinkHtml*/

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

	
} // SearchResultField
