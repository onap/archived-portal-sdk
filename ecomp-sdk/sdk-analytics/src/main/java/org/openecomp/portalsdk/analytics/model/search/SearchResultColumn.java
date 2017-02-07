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

public class SearchResultColumn extends org.openecomp.portalsdk.analytics.RaptorObject {
	private String columnId    = "";
	private String columnTitle = "";

	private String columnWidth = "";

	private String alignment = "Left";

	private String linkURL = null; // if not null => display link instead of
									// text

	private String linkTitle = null; // if img => hint, otherwise link
										// display text

	private String linkForm = null; // if not null => displays input submit
									// instead of link

	private String linkConfirmMsg = null; // if not null => display conf. box
											// on link click

	private String linkImg = null; // if not null => link shows image, not text

	private String linkImgWidth = null; // optional

	private String linkImgHeight = null; // optional

	private boolean copyLink = false; // optional

	private boolean editLink = false; // optional

	private boolean deleteLink = false; // optional
	
	private boolean scheduleLink = false; // optional

	public SearchResultColumn(String columnId, String columnTitle) {
		super();
        setColumnId(columnId);
		setColumnTitle(columnTitle);
	} // SearchResultColumn

	public SearchResultColumn(String columnId, String columnTitle, String columnWidth, String alignment) {
		super();
		 setColumnId(columnId);
		setColumnTitle(columnTitle);
		setColumnWidth(columnWidth);
		setAlignment(alignment);
	} // SearchResultColumn

	public SearchResultColumn(String columnId, String columnTitle, String columnWidth, String alignment,
			String linkURL, String linkTitle, String linkForm, String linkConfirmMsg,
			String linkImg) {
		super();
		 setColumnId(columnId);
		setColumnTitle(columnTitle);
		setColumnWidth(columnWidth);
		setAlignment(alignment);
		setLinkURL(linkURL);
		setLinkTitle(linkTitle);
		setLinkForm(linkForm);
		setLinkConfirmMsg(linkConfirmMsg);
		setLinkImg(linkImg);
	} // SearchResultColumn

	public SearchResultColumn(String columnId, String columnTitle, String columnWidth, String alignment,
			String linkURL, String linkTitle, String linkForm, String linkConfirmMsg,
			String linkImg, String linkImgWidth, String linkImgHeight) {
		this(columnId, columnTitle, columnWidth, alignment, linkURL, linkTitle, linkForm,
				linkConfirmMsg, linkImg);

		setLinkWidth(linkImgWidth);
		setLinkHeight(linkImgHeight);
	} // SearchResultColumn

	public SearchResultColumn(String columnId, String columnTitle, String columnWidth, String alignment,
			String linkURL, String linkTitle, String linkForm, String linkConfirmMsg,
			String linkImg, String linkImgWidth, String linkImgHeight, boolean copyLink,
			boolean editLink, boolean deleteLink) {
		this(columnId, columnTitle, columnWidth, alignment, linkURL, linkTitle, linkForm,
				linkConfirmMsg, linkImg, linkImgWidth, linkImgHeight);

		setCopyLink(copyLink);
		setEditLink(editLink);
		setDeleteLink(deleteLink);
	} // SearchResultColumn

	public SearchResultColumn(String columnId, String columnTitle, String columnWidth, String alignment,
			String linkURL, String linkTitle, String linkForm, String linkConfirmMsg,
			String linkImg, String linkImgWidth, String linkImgHeight, boolean copyLink,
			boolean editLink, boolean deleteLink, boolean scheduleLink) {
		this(columnId, columnTitle, columnWidth, alignment, linkURL, linkTitle, linkForm,
				linkConfirmMsg, linkImg, linkImgWidth, linkImgHeight);

		setCopyLink(copyLink);
		setEditLink(editLink);
		setDeleteLink(deleteLink);
		setScheduleLink(scheduleLink);
	} // SearchResultColumn
	
	public String getColumnTitle() {
		return columnTitle;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public String getAlignment() {
		return alignment;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public String getLinkTitle() {
		return linkTitle;
	}

	public String getLinkForm() {
		return linkForm;
	}

	public String getLinkConfirmMsg() {
		return linkConfirmMsg;
	}

	public String getLinkImg() {
		return linkImg;
	}

	public String getLinkImgWidth() {
		return linkImgWidth;
	}

	public String getLinkImgHeight() {
		return linkImgHeight;
	}

	public boolean isCopyLink() {
		return copyLink;
	}

	public boolean isEditLink() {
		return editLink;
	}

	public boolean isDeleteLink() {
		return deleteLink;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = nvl(columnTitle);
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = nvl(columnWidth);
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}

	public void setLinkForm(String linkForm) {
		this.linkForm = linkForm;
	}

	public void setLinkConfirmMsg(String linkConfirmMsg) {
		this.linkConfirmMsg = linkConfirmMsg;
	}

	public void setLinkImg(String linkImg) {
		this.linkImg = linkImg;
	}

	public void setLinkWidth(String linkImgWidth) {
		this.linkImgWidth = linkImgWidth;
	}

	public void setLinkHeight(String linkImgHeight) {
		this.linkImgHeight = linkImgHeight;
	}

	public void setCopyLink(boolean copyLink) {
		this.copyLink = copyLink;
	}

	public void setEditLink(boolean editLink) {
		this.editLink = editLink;
	}

	public void setDeleteLink(boolean deleteLink) {
		this.deleteLink = deleteLink;
	}

	public String getColumnTitleHtml() {
		return (columnTitle.length() == 0) ? "&nbsp;" : columnTitle;
	}

	public String getColumnWidthHtml() {
		return (columnWidth.length() == 0) ? "" : (" width=" + columnWidth);
	}

	public String getLinkImgSizeHtml() {
		return ((nvl(linkImgWidth).length() > 0) ? " width=\"" + linkImgWidth + "\"" : "")
				+ ((nvl(linkImgHeight).length() > 0) ? " height=\"" + linkImgHeight + "\""
						: "");
	}

	public boolean isScheduleLink() {
		return scheduleLink;
	}

	public void setScheduleLink(boolean scheduleLink) {
		this.scheduleLink = scheduleLink;
	}

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}

} // SearchResultColumn
