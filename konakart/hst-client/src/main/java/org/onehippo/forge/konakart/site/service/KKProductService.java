package org.onehippo.forge.konakart.site.service;

import org.hippoecm.hst.core.component.HstRequest;

import com.konakart.appif.ProductIf;

public interface KKProductService {

    /**
     * Get the latest products added to the catalog. No category will be selected.
     * The description will not be fetched and the invisible products will not be displayed
     *
     * @param hstRequest the hst request
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     * The description will not be fetched and the invisible products will not be displayed
     * By default resulted are sorted by Date Added
     *
     * @param hstRequest the hst request
     * @param categoryId The id of the selected category
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     * By default resulted are sorted by Date Added
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param fetchDescription When set to true, the product description is also fetched.
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, boolean fetchDescription);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     * By default resulted are sorted by Date Added
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param fetchDescription When set to true, the product description is also fetched.
     * @param limit            the maximum number of objects returned. By default limit is equals to DataDescConstants.MAX_ROWS
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, boolean fetchDescription, int limit);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     * By default resulted are sorted by Date Added
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param fetchDescription When set to true, the product description is also fetched.
     * @param showInvisible    Show invisible products
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, boolean fetchDescription, boolean showInvisible);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     * By default resulted are sorted by Date Added
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param fetchDescription When set to true, the product description is also fetched.
     * @param showInvisible    Show invisible products
     * @param limit            the maximum number of objects returned. By default limit is equals to DataDescConstants.MAX_ROWS
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, boolean fetchDescription,
                                 boolean showInvisible, int limit);

    /**
     * Get the latest products added to the catalog for the category whose id is passed in as a parameter.
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param fetchDescription When set to true, the product description is also fetched.
     * @param showInvisible    Show invisible products
     * @param limit            the maximum number of objects returned. By default limit is equals to DataDescConstants.MAX_ROWS
     * @param orderBy          define how results will be displayed
     *
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, boolean fetchDescription,
                                 boolean showInvisible, int limit, String orderBy);


    /**
     * Get the latest products added to the catalog for the category and manufacturer whose ids are passed in as a parameter.
     *
     * @param hstRequest       the hst request
     * @param categoryId       The id of the selected category
     * @param manufacturerId   The id of the selected manufacturer
     * @param fetchDescription When set to true, the product description is also fetched.
     * @param showInvisible    Show invisible products
     * @param limit            the maximum number of objects returned. By default limit is equals to DataDescConstants.MAX_ROWS
     * @param orderBy          define how results will be displayed
     *
     */
    ProductIf[] fetchNewProducts(HstRequest hstRequest, int categoryId, int manufacturerId, boolean fetchDescription,
                                 boolean showInvisible, int limit, String orderBy);
    
}
