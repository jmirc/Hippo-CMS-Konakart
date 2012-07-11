package org.onehippo.forge.konakart.site.service;

import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

import java.util.List;

public interface KKTagsService {

    /**
     * Customer tags
     */
    public static final String TAG_PROD_PAGE_SIZE = "PROD_PAGE_SIZE";

    public static final String TAG_ORDER_PAGE_SIZE = "ORDER_PAGE_SIZE";

    public static final String TAG_REVIEW_PAGE_SIZE = "REVIEW_PAGE_SIZE";

    public static final String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";

    public static final String TAG_CATEGORIES_VIEWED = "CATEGORIES_VIEWED";

    public static final String TAG_MANUFACTURERS_VIEWED = "MANUFACTURERS_VIEWED";

    public static final String TAG_SEARCH_STRING = "SEARCH_STRING";

    public static final String TAG_COUNTRY_CODE = "COUNTRY_CODE";

    public static final String TAG_BIRTH_DATE = "BIRTH_DATE";

    public static final String TAG_IS_MALE = "IS_MALE";

    /**
     * Retrieve the list of products that have been viewed products
     * @param hstRequest the hst request
     * @return the list of products documents
     */
    List<KKProductDocument> getViewingHistory(HstRequest hstRequest);

}
