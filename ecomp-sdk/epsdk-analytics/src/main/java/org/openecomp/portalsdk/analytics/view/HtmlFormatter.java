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
package org.openecomp.portalsdk.analytics.view;

import org.openecomp.portalsdk.analytics.RaptorObject;

public class HtmlFormatter extends RaptorObject {
	private boolean bold = false;

	private boolean italic = false;

	private boolean underline = false;

	private String bgColor = "";

	private String fontColor = "";

	private String fontFace = "";

	private String fontSize = "";

	private String alignment = "";
	
	private String formatId = "";

	public HtmlFormatter() {
		super();
	}

	public HtmlFormatter(boolean bold, boolean italic, boolean underline, String bgColor,
			String fontColor, String fontFace, String fontSize) {
		this();

		setBold(bold);
		setItalic(italic);
		setUnderline(underline);
		setBgColor(bgColor);
		setFontColor(fontColor);
		setFontFace(fontFace);
		setFontSize(fontSize);
	} // HtmlFormatter

	public HtmlFormatter(boolean bold, boolean italic, boolean underline, String bgColor,
			String fontColor, String fontFace, String fontSize, String alignment) {
		this(bold, italic, underline, bgColor, fontColor, fontFace, fontSize);
		setAlignment(alignment);
	} // HtmlFormatter

	public boolean isBold() {
		return bold;
	}

	public boolean isItalic() {
		return italic;
	}

	public boolean isUnderline() {
		return underline;
	}

	public String getBgColor() {
		return bgColor;
	}

	public String getFontColor() {
		return fontColor;
	}

	public String getFontFace() {
		return fontFace;
	}

	public String getFontSize() {
		return fontSize;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	public void setUnderline(boolean underline) {
		this.underline = underline;
	}

	public void setBgColor(String bgColor) {
		this.bgColor = nvl(bgColor);
	}

	public void setFontColor(String fontColor) {
		this.fontColor = nvl(fontColor);
	}

	public void setFontFace(String fontFace) {
		this.fontFace = nvl(fontFace);
	}

	public void setFontSize(String fontSize) {
		this.fontSize = nvl(fontSize);
	}

	public void setAlignment(String alignment) {
		this.alignment = nvl(alignment);
	}

	private String generateStyleHtml() {
		StringBuffer sb = new StringBuffer();

		if (isBold())
			sb.append("font-weight:bold;");
		if (isItalic())
			sb.append("font-style:italic;");
		if (isUnderline())
			sb.append("text-decoration:underline;");
		// if(getBgColor().length()>0)
		// sb.append("background-color:"+getBgColor()+";");
		if (getFontColor().length() > 0)
			sb.append("color:" + getFontColor() + ";");
		if (getFontFace().length() > 0)
			sb.append("font-family:" + getFontFace() + ";");
		if (getFontSize().length() > 0)
			sb.append("font-size:" + getFontSize() + "px;");
		if (getAlignment().length() > 0)
			sb.append("text-align:" + getAlignment() + ";");

		if (sb.length() > 0) {
			sb.insert(0, " style=\"");
			sb.append("\"");
		} // if

		return sb.toString();
	} // generateStyleHtml

	public String generateStyleForZK() {
		StringBuffer sb = new StringBuffer();

		if (isBold())
			sb.append("font-weight:bold;");
		if (isItalic())
			sb.append("font-style:italic;");
		if (isUnderline())
			sb.append("text-decoration:underline;");
		// if(getBgColor().length()>0)
		// sb.append("background-color:"+getBgColor()+";");
		if (getFontColor().length() > 0)
			sb.append("color:" + getFontColor() + ";");
		if (getFontFace().length() > 0)
			sb.append("font-family:" + getFontFace() + ";");
		if (getFontSize().length() > 0)
			sb.append("font-size:" + getFontSize() + "px;");
		if (getAlignment().length() > 0)
			sb.append("text-align:" + getAlignment() + ";");

		return sb.toString();
	} // generateStyleHtml

	public String formatValue(String value) {
		String style = generateStyleHtml();
		if (style.length() > 0)
			return "<font" + style + ">" + value + "</font>";
		else
			return value;
	} // formatValue

	public String formatLink(String value, String url, boolean drillDowninPoPUp) {
		if(!drillDowninPoPUp) {
			return "<a href=\"" + url + "\"" + generateStyleHtml() + ">" + value + "</a>";
		} else {
			return "<a href=\"#\"  onClick=\"showDrillDownInPopup('" + url + "&noFormFields=Y&isEmbedded=Y&show_back_btn=N"+ "')\"" + generateStyleHtml() + ">" + value + "</a>";
		}
	} // formatLink
	
	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}	

} // HtmlFormatter
