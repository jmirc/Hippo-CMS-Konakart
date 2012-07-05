/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.app.KKException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.*;
import org.hippoecm.hst.content.beans.standard.facetnavigation.HippoFacetNavigation;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.SearchInputParsingUtils;
import org.hippoecm.hst.utils.BeanUtils;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.utilities.hst.paging.DefaultPagination;
import org.onehippo.forge.utilities.hst.paging.IterablePagination;
import org.onehippo.forge.utilities.hst.paging.Pageable;

import java.lang.reflect.ParameterizedType;

/**
 * This overview component offers methods used to retrieve products information
 *
 */
public abstract class AbstractKKProductsOverview<T extends KKProductDocument> extends KKHstActionComponent {

    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final int DEFAULT_PAGE_SIZE = 6;
    private static final String PARAM_CURRENT_PAGE = "pageNumber";
    private static final int DEFAULT_CURRENT_PAGE = 1;
    private static final String PARAM_ORDER_BY = "orderBy";
    private static final String DEFAULT_ORDER_BY = "hippostdpubwf:lastModificationDate";


    @Override
    final public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKAppEng kkAppEng = getKKAppEng(request);

        // TODO this reset could be move in other place if the selected product should be reset outside
        // the overview page
        // Reset the state of the objects connected to the session. i.e. Selected product etc.
        try {
            kkAppEng.reset();
        } catch (KKException e) {
            log.info("Unable to reset the Konakart Engine - {} " + e.toString());
        }

        HippoBean currentBean = this.getContentBean(request);

        if (currentBean == null) {
            return;
        }

        String pageSizeParam = getPublicRequestParameter(request, PARAM_PAGE_SIZE);
        if (pageSizeParam == null || "".equals(pageSizeParam)) {
            pageSizeParam = getParameter(PARAM_PAGE_SIZE, request);
        }
        int pageSize = KKUtil.parseIntParameter(PARAM_PAGE_SIZE, pageSizeParam, DEFAULT_PAGE_SIZE, log);
        request.setAttribute("pageSize", pageSize);

        String currentPageParam = getPublicRequestParameter(request, PARAM_CURRENT_PAGE);
        int currentPage = KKUtil.parseIntParameter(PARAM_CURRENT_PAGE, currentPageParam, DEFAULT_CURRENT_PAGE, log);

        String orderBy = getParameter(PARAM_ORDER_BY, request);
        if (orderBy == null || "".equals(orderBy)) {
            orderBy = DEFAULT_ORDER_BY;
        }

        String query = this.getPublicRequestParameter(request, "query");
        query = SearchInputParsingUtils.parse(query, false);
        request.setAttribute("query", StringEscapeUtils.escapeHtml(query));

        String order = this.getPublicRequestParameter(request, "order");
        request.setAttribute("order", StringEscapeUtils.escapeHtml(order));

        String from = this.getPublicRequestParameter(request, "from");
        String jsEnabled = getPublicRequestParameter(request, "jsEnabled");

        try {
            HstQuery hstQuery = getQueryManager(request).createQuery(getSiteContentBaseBean(request), getPrimaryNodeTypes());

            if (!StringUtils.isEmpty(query)) {
                Filter f = hstQuery.createFilter();
                Filter f1 = hstQuery.createFilter();
                f1.addContains(".", query);

                f.addOrFilter(f1);

                if (getFreeTextPropertyName() != null) {
                    Filter f2 = hstQuery.createFilter();
                    f2.addContains(getFreeTextPropertyName(), query);
                    f.addOrFilter(f2);
                }

                hstQuery.setFilter(f);
            } else {
                if (!StringUtils.isEmpty(order) && !"relevance".equals(order)) {
                    if ("-lastModificationDate".equals(order)) {
                        hstQuery.addOrderByDescending("hippostdpubwf:lastModificationDate");
                    } else if (order.startsWith("-")) {
                        hstQuery.addOrderByDescending(order.substring(1));
                    } else {
                        hstQuery.addOrderByAscending(order);
                    }
                } else {
                    hstQuery.addOrderByDescending(orderBy);
                }
            }
            if (from != null && Boolean.parseBoolean(jsEnabled)) {
                hstQuery.setOffset(Integer.valueOf(from));
            }

            Pageable pages;
            long resultCount;

            if (!(currentBean instanceof HippoFacetChildNavigationBean || currentBean instanceof HippoFacetNavigation)) {
                final HstQueryResult result = hstQuery.execute();
                pages = new IterablePagination<T>(result.getHippoBeans(), pageSize, currentPage);
                resultCount = result.getSize();
            } else {
                final HippoFacetNavigationBean facNavBean = BeanUtils.getFacetNavigationBean(request, hstQuery, objectConverter);

                if (facNavBean == null) {
                    pages = new DefaultPagination<KKProductDocument>(0);
                    resultCount = 0;
                } else {
                    final HippoResultSetBean result = facNavBean.getResultSet();
                    final HippoDocumentIterator<T> beans = result.getDocumentIterator(getProductDocumentClass());

                    if (hstQuery.getOffset() > 0) {
                        beans.skip(hstQuery.getOffset());
                    }

                    pages = new IterablePagination<T>(beans, facNavBean.getCount().intValue(),
                            KKUtil.getIntConfigurationParameter(request, PARAM_PAGE_SIZE, pageSize),
                            currentPage);
                    resultCount = result.getCount();
                }
            }
            request.setAttribute("products", pages);
            request.setAttribute("count", resultCount);
        } catch (QueryException qe) {
            log.error("Error while getting the documents " + qe.getMessage(), qe);
        }


        doBeforeRender(request, response, currentBean);


    }

    /**
     * This method is called automatically after the overview components retrieves products
     *
     * @param request the HstRequest
     * @param response the HstResponse
     * @param currentBean the current bean
     */
    protected abstract void doBeforeRender(HstRequest request, HstResponse response, HippoBean currentBean);

    /**
     * @return the list of primary node types used
     * by this component to retrieve the list of associated products
     */
    protected String[] getPrimaryNodeTypes() {
        return new String[] {KKCndConstants.PRODUCT_DOC_TYPE};
    }

    /**
     * @return the Product document
     */
    @SuppressWarnings("unchecked")
    protected Class<T> getProductDocumentClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }


    /**
     * By default the search is done on the entire document.
     * <p/>
     * You can overrides this method to specify which property is targeted by the free text search.
     *
     * @return the property's name used with the free search feature.
     */
    protected String getFreeTextPropertyName() {
        return null;
    }
}
