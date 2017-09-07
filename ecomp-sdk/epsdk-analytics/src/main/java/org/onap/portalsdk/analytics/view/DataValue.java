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
package org.onap.portalsdk.analytics.view;

public class DataValue extends org.onap.portalsdk.analytics.RaptorObject {
	private String displayValue = "";

	private String displayCalculatedValue = "";

	private String drillDownURL = null;
	
	private boolean drillDowninPoPUp = false;
	
	private String indentation = "";

	private String alignment = "Left";

	private boolean visible = true;
	
	private boolean hidden = false;

	private boolean bold = false;

	private HtmlFormatter cellFormatter = null;

	private HtmlFormatter rowFormatter = null;
	
	private String formatId = null;
	
	private boolean cellFormat = false;
    
    private String colId = null;
    
    private String displayTotal = null;
    
    private String colName = null;
    
 	private String displayName = null;    

    private String nowrap = "False";
    
    private String hyperlinkURL = "";
    
    private String displayType = "";
    
    private String actionImg = "";
    

	public String getColName() {
    
        return colName;
    }

    
    public void setColName(String colName) {
    
        this.colName = colName;
    }

    public DataValue() {
		super();
	}

	public String getFormatId() {
		return formatId;
	}


	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	public void setCellFormat(boolean b) {
		cellFormat = b;
	}
	public boolean isCellFormat() {
		return cellFormat;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public String getDrillDownURL() {
		return drillDownURL;
	}

	public String getAlignment() {
		return alignment;
	}

	public boolean isVisible() {
		return visible;
	}

	public boolean isBold() {
		return bold;
	}

	public HtmlFormatter getCellFormatter() {
		return cellFormatter;
	}

	public HtmlFormatter getRowFormatter() {
		return rowFormatter;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = nvl(displayValue);
	}

	public void setDrillDownURL(String drillDownURL) {
		this.drillDownURL = drillDownURL;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setCellFormatter(HtmlFormatter cellFormatter) {
		this.cellFormatter = cellFormatter;
	}

	public void setRowFormatter(HtmlFormatter rowFormatter) {
		this.rowFormatter = rowFormatter;
	}

	private String getFormattedValue(String value) {
		value = nvl(value.trim()).length()<=0 ? "&nbsp":value;
		if (cellFormatter != null)
			return cellFormatter.formatValue(value);
		else if (rowFormatter != null)
			return rowFormatter.formatValue(value);
		else
			return value;
	} // getFormattedValue

	private String getFormattedLink(String value) {
		if (cellFormatter != null)
			return cellFormatter.formatLink(value, drillDownURL, isDrillDowninPoPUp());
		else if (rowFormatter != null)
			return rowFormatter.formatLink(value, drillDownURL, isDrillDowninPoPUp());
		else {
			if(!isDrillDowninPoPUp()) {
				return "<a href=\"" + drillDownURL + "\">" + value + "</a>";
			} else {
				return "<a href=\"#\"  onClick=\"showDrillDownInPopup('" + drillDownURL + "&noFormFields=Y&isEmbedded=Y&show_back_btn=N"+ "')\">" + value + "</a>";
			}
			
		}
	} // getFormattedValue

	private String getValueHtml() {
		return (nvl(displayValue).trim().length() == 0) ? "&nbsp;" : displayValue;
	}

	public String getDisplayValueHtml() {
		return getFormattedValue(getValueHtml());
	}

	public String getDisplayValueLinkHtml() {
		if (nvl(drillDownURL).length() == 0)
			return getDisplayValueHtml();
		else
			return getFormattedLink(getValueHtml());
		// return getFormattedValue("<a
		// href=\""+drillDownURL+"\">"+getValueHtml()+"</a>");
	} // getDisplayValueLinkHtml

	public String getAlignmentHtml() {
		if (cellFormatter != null && cellFormatter.getAlignment().length() > 0)
			return " align=" + cellFormatter.getAlignment();
		else if (rowFormatter != null && rowFormatter.getAlignment().length() > 0)
			return " align=" + rowFormatter.getAlignment();
		else
			return (alignment.length() == 0) ? "" : (" align=" + alignment);
	} // getAlignmentHtml

	public String getBgColorHtml() {
		if (cellFormatter != null && cellFormatter.getBgColor().length() > 0)
			return " bgcolor=" + cellFormatter.getBgColor();
		else if (rowFormatter != null && rowFormatter.getBgColor().length() > 0)
			return " bgcolor=" + rowFormatter.getBgColor();
		else
			return "";
	} // getBgColorHtml

    
    public String getColId() {
    
        return colId;
    }

    
    public void setColId(String colId) {
    
        this.colId = colId;
    }

    
    public String getDisplayTotal() {
    
        return displayTotal;
    }

    
    public void setDisplayTotal(String displayTotal) {
    
        this.displayTotal = displayTotal;
    }


    
    public String getDisplayName() {
    
        return displayName;
    }


    
    public void setDisplayName(String displayName) {
    
        this.displayName = displayName;
    }


	public boolean isHidden() {
		return hidden;
	}


	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

   public String getNowrap() {
		return nowrap;
	}


	public void setNowrap(String nowrap) {
		this.nowrap = nowrap;
	}

	public boolean isDrillDowninPoPUp() {
		return drillDowninPoPUp;
	}


	public void setDrillDowninPoPUp(boolean drillDowninPoPUp) {
		this.drillDowninPoPUp = drillDowninPoPUp;
	}


	/**
	 * @return the displayCalculatedValue
	 */
	public String getDisplayCalculatedValue() {
		return displayCalculatedValue;
	}


	/**
	 * @param displayCalculatedValue the displayCalculatedValue to set
	 */
	public void setDisplayCalculatedValue(String displayCalculatedValue) {
		this.displayCalculatedValue = displayCalculatedValue;
	}


	/**
	 * @return the indentation
	 */
	public String getIndentation() {
		return indentation;
	}


	/**
	 * @param indentation the indentation to set
	 */
	public void setIndentation(String indentation) {
		this.indentation = indentation;
	}
	

	/**
	 * @return the hyperlinkURL
	 */
	public String getHyperlinkURL() {
		return hyperlinkURL;
	}


	/**
	 * @param hyperlinkURL the hyperlinkURL to set
	 */
	public void setHyperlinkURL(String hyperlinkURL) {
		this.hyperlinkURL = hyperlinkURL;
	}


	/**
	 * @return the displayType
	 */
	public String getDisplayType() {
		return displayType;
	}


	/**
	 * @param displayType the displayType to set
	 */
	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}


	/**
	 * @return the actionImg
	 */
	public String getActionImg() {
		return actionImg;
	}


	/**
	 * @param actionImg the actionImg to set
	 */
	public void setActionImg(String actionImg) {
		this.actionImg = actionImg;
	}
	
} // DataValue
