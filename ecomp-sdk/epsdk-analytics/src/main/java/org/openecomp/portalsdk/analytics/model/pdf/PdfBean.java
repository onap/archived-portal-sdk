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
package org.openecomp.portalsdk.analytics.model.pdf;


public class PdfBean {
	
	public static final int NUMBER_IN_HEADER = 0;
	public static final int NUMBER_IN_FOOTER = 1;
	public static final int NUMBER_IN_BOTH = 2;
	
	private boolean alternateColor;
	private boolean isPortrait;
	private boolean isCoverPageIncluded;
	private boolean isDisplayChart;
	private int currentPage;
	private int whereToShowPageNumber;
	private String userId;
	private String timestampPattern;
	private String title;
	private String leftFooter;
	private String pagesize;
	private boolean isAttachmentOfEmail;
	private String logo1Url;
	private Integer logo1Size;
	private String logo2Url;
	private Integer logo2Size;
	private String fullWebContextPath;
	
	/**
	 * @return the leftFooter
	 */
	public String getLeftFooter() {
		return leftFooter;
	}
	/**
	 * @param leftFooter the leftFooter to set
	 */
	public void setLeftFooter(String leftFooter) {
		this.leftFooter = leftFooter;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the alternateColor
	 */
	public boolean isAlternateColor() {
		return alternateColor;
	}
	/**
	 * @param alternateColor the alternateColor to set
	 */
	public void setAlternateColor(boolean alternateColor) {
		this.alternateColor = alternateColor;
	}
	/**
	 * @return the currentPage
	 */
	public int getCurrentPage() {
		return currentPage;
	}
	/**
	 * @param currentPage the currentPage to set
	 */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	/**
	 * @return the isPortrait
	 */
	public boolean isPortrait() {
		return isPortrait;
	}
	/**
	 * @param isPortrait the isPortrait to set
	 */
	public void setPortrait(boolean isPortrait) {
		this.isPortrait = isPortrait;
	}
	/**
	 * @return the timestampPattern
	 */
	public String getTimestampPattern() {
		return timestampPattern;
	}
	/**
	 * @param timestampPattern the timestampPattern to set
	 */
	public void setTimestampPattern(String timestampPattern) {
		this.timestampPattern = timestampPattern;
	}
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the whereToShowPageNummber
	 */
	public int getWhereToShowPageNumber() {
		return whereToShowPageNumber;
	}
	/**
	 * @param whereToShowPageNumber the whereToShowPageNumber to set
	 */
	public void setWhereToShowPageNumber(int whereToShowPageNumber) {
		this.whereToShowPageNumber = whereToShowPageNumber;
	}

	public boolean isPageNumberAtHeader() {
		return  getWhereToShowPageNumber()==NUMBER_IN_BOTH || 
				getWhereToShowPageNumber()==NUMBER_IN_HEADER;
	}
	
	public boolean isPageNumberAtFooter() {
		return  getWhereToShowPageNumber()==NUMBER_IN_FOOTER || 
				getWhereToShowPageNumber()==NUMBER_IN_BOTH;
	}
			
	/**
	 * @return the isCoverPageIncluded
	 */
	public boolean isCoverPageIncluded() {
		return isCoverPageIncluded;
	}
	/**
	 * @param isCoverPageIncluded the isCoverPageIncluded to set
	 */
	public void setCoverPageIncluded(boolean isCoverPageIncluded) {
		this.isCoverPageIncluded = isCoverPageIncluded;
	}
	
	public String toString() {
		return  getTitle()+ " " +
				getCurrentPage() + " " +
				getTimestampPattern() + " " +
				getUserId()+ " " +
				getWhereToShowPageNumber()+ " " +
				isPortrait() + " " + isAlternateColor();
	}
	/**
	 * @return the isDisplayChart
	 */
	public boolean isDisplayChart() {
		return isDisplayChart;
	}
	/**
	 * @param isDisplayChart the isDisplayChart to set
	 */
	public void setDisplayChart(boolean isDisplayChart) {
		this.isDisplayChart = isDisplayChart;
	}
	/**
	 * @return the pagesize
	 */
	public String getPagesize() {
		return pagesize;
	}
	/**
	 * @param pagesize the pagesize to set
	 */
	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}
	
	public String getLogo1Url() {
		return logo1Url;
	}
	public void setLogo1Url(String logo1Url) {
		this.logo1Url = logo1Url;
	}
	
	public String getLogo2Url() {
		return logo2Url;
	}
	public void setLogo2Url(String logo2Url) {
		this.logo2Url = logo2Url;
	}
	
	public void setAttachmentOfEmail(boolean isAttachmentOfEmail) {
		this.isAttachmentOfEmail = isAttachmentOfEmail;
	}
	
	public boolean isAttachmentOfEmail() {
		
		return isAttachmentOfEmail;
	}
	public Integer getLogo1Size() {
		return logo1Size;
	}
	public void setLogo1Size(Integer logo1Size) {
		this.logo1Size = logo1Size;
	}
	public Integer getLogo2Size() {
		return logo2Size;
	}
	public void setLogo2Size(Integer logo2Size) {
		this.logo2Size = logo2Size;
	}
	public String getFullWebContextPath() {
		return fullWebContextPath;
	}
	public void setFullWebContextPath(String fullWebContextPath) {
		this.fullWebContextPath = fullWebContextPath;
	}
	
	
}
