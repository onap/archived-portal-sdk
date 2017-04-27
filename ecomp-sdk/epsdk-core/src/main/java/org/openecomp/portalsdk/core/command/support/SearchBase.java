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
package org.openecomp.portalsdk.core.command.support;

import java.util.*;

import org.openecomp.portalsdk.core.domain.support.FusionCommand;

public abstract class SearchBase extends FusionCommand {

    public static String SORT_BY_MODIFIER_DESC = "D";
    public static String SORT_BY_MODIFIER_ASC  = "A";
    public static String SORT_BY_MODIFIER_DESC_IMAGE_NAME = "sort_desc.gif";
    public static String SORT_BY_MODIFIER_ASC_IMAGE_NAME  = "sort_asc.gif";


    private String sortBy1 = null;
    private String sortBy2 = null;
    private String sortBy3 = null;

    private String sortBy1Orig = null;
    private String sortBy2Orig = null;
    private String sortBy3Orig = null;

    private String sortByModifier1 = null;
    private String sortByModifier2 = null;
    private String sortByModifier3 = null;

    private String sortByModifier1Orig = null;
    private String sortByModifier2Orig = null;
    private String sortByModifier3Orig = null;

    private String accessType = "WRITE"; //null;

    private String submitAction = "";
    private String masterId = "";
    private String detailId = "";

    private String showResult = "Y";

    private SearchResult searchResult = null;
    private boolean sortingUpdated;

    @SuppressWarnings("rawtypes")
    public SearchBase(List items) {
        searchResult = (items == null) ? (new SearchResult()) : (new SearchResult(items));
    } // SearchBase


    public String getSortBy1() {
        return sortBy1;
    }

    public String getSortBy2() {
        return sortBy2;
    }

    public String getSortBy3() {
        return sortBy3;
    }

    public String getSortBy1Orig() {
        return sortBy1;
    }

    public String getSortBy2Orig() {
        return sortBy2;
    }

    public String getSortBy3Orig() {
        return sortBy3;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public String getMasterId() {
        return masterId;
    }

    public String getDetailId() {
        return detailId;
    }

    public String getShowResult() {
        return showResult;
    }

    //public ArrayList getSortByList()      { return sortByList; }

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public String getSortByModifier1() {
        return sortByModifier1;
    }

    public String getSortByModifier1Orig() {
        return sortByModifier1;
    }

    public String getSortByModifier2() {
        return sortByModifier2;
    }

    public String getSortByModifier2Orig() {
        return sortByModifier2;
    }

    public String getSortByModifier3() {
        return sortByModifier3;
    }

    public String getSortByModifier3Orig() {
        return sortByModifier3;
    }

    public int getPageNo() {
        return (isCriteriaUpdated() || isSortingUpdated()) ? 0 : getSearchResult().getPageNo();
    }

    public int getPageSize() {
        return getSearchResult().getPageSize();
    }

    public int getDataSize() {
        return getSearchResult().getDataSize();
    }

    public int getNewDataSize() {
        return isCriteriaUpdated() ? -1 : getDataSize();
    }


    public void setSortBy1(String sortBy1) {
        this.sortBy1 = sortBy1;
    }

    public void setSortBy2(String sortBy2) {
        this.sortBy2 = sortBy2;
    }

    public void setSortBy3(String sortBy3) {
        this.sortBy3 = sortBy3;
    }

    public void setSortBy1Orig(String sortBy1Orig) {
        this.sortBy1Orig = sortBy1Orig;
    }

    public void setSortBy2Orig(String sortBy2Orig) {
        this.sortBy2Orig = sortBy2Orig;
    }

    public void setSortBy3Orig(String sortBy3Orig) {
        this.sortBy3Orig = sortBy3Orig;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public void setShowResult(String showResult) {
        this.showResult = showResult;
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    public void setSortByModifier1(String sortByModifier1) {
        this.sortByModifier1 = sortByModifier1;
    }

    public void setSortByModifier1Orig(String sortByModifier1Orig) {
        this.sortByModifier1Orig = sortByModifier1Orig;
    }

    public void setSortByModifier2(String sortByModifier2) {
        this.sortByModifier2 = sortByModifier2;
    }

    public void setSortByModifier2Orig(String sortByModifier2Orig) {
        this.sortByModifier2Orig = sortByModifier2Orig;
    }

    public void setSortByModifier3(String sortByModifier3) {
        this.sortByModifier3 = sortByModifier3;
    }

    public void setSortByModifier3Orig(String sortByModifier3Orig) {
        this.sortByModifier3Orig = sortByModifier3Orig;
    }

    public void setSortingUpdated(boolean sortingUpdated) {
        this.sortingUpdated = sortingUpdated;
    }

    public void setPageNo(int pageNo) {
        getSearchResult().setPageNo(pageNo);
    }

    public void setPageSize(int pageSize) {
        getSearchResult().setPageSize(pageSize);
    }

    public void setDataSize(int dataSize) {
        getSearchResult().setDataSize(dataSize);
    }


    public void resetSearch() {
        setSortBy1(null);
        setSortBy2(null);
        setSortBy3(null);
        setSortByModifier1(SearchBase.SORT_BY_MODIFIER_ASC);
        setSortByModifier2(SearchBase.SORT_BY_MODIFIER_ASC);
        setSortByModifier3(SearchBase.SORT_BY_MODIFIER_ASC);
        setPageNo(0);
        setDataSize( -1);
    } // resetSearch


    public abstract boolean isCriteriaUpdated();

    public boolean isSortingUpdated() {
        return (!(Utilities.nvl(sortBy1).equals(Utilities.nvl(sortBy1Orig)) &&
                  Utilities.nvl(sortBy2).equals(Utilities.nvl(sortBy2Orig)) &&
                  Utilities.nvl(sortBy3).equals(Utilities.nvl(sortBy3Orig)) &&
                  Utilities.nvl(sortByModifier1).equals(Utilities.nvl(sortByModifier1Orig)) &&
                  Utilities.nvl(sortByModifier2).equals(Utilities.nvl(sortByModifier2Orig)) &&
                  Utilities.nvl(sortByModifier3).equals(Utilities.nvl(sortByModifier3Orig))));
    } // isSortingUpdated

} // SearchBase
