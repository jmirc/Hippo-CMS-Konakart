package org.onehippo.forge.konakart.hst.beans.compound;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.*;
import org.onehippo.forge.konakart.common.KKCndConstants;

import java.util.ArrayList;
import java.util.List;

@Node(jcrType = KKCndConstants.PRODUCT_DOC_TYPE)
public class Konakart extends HippoItem {

    private Price standardPrice = null;
    private List<ImageSet> images;


    public Long getProductId() {
        return getProperty(KKCndConstants.PRODUCT_ID);
    }

    public Long getLanguageId() {
        return getProperty(KKCndConstants.PRODUCT_LANGUAGE_ID);
    }

    public String getProductSku() {
        return getProperty(KKCndConstants.PRODUCT_SKU);
    }

    public String getProductName() {
        return getProperty(KKCndConstants.PRODUCT_NAME);
    }

    public HippoHtml getProductDescription() {
        return getProperty(KKCndConstants.PRODUCT_DESCRIPTION);
    }

    /**
     * @return the standard compound price values.
     * @throws Exception if the compound price could not be loaded
     */
    public Price getStandardPrice() throws Exception {

        if (standardPrice == null) {
            loadStandardPrice();
        }

        if (standardPrice == null) {
            throw new Exception("Unable to load the compoundPrice child");
        }


        return standardPrice;
    }

    /**
     * @return the list of images associated the product
     */
    public List<ImageSet> getImages() {
        if (images == null) {
            loadImages();
        }
        return images;
    }

    /**
     * @return the main image
     */
    public ImageSet getMainImage() {
        if (images == null) {
            loadImages();
        }
        return images.size() == 0 ? null : images.get(0);
    }

    /**
     * @return the special price. Used for promotion for a defined duration.
     */
    public Double getSpecialPrice() {
        return getProperty(KKCndConstants.PRODUCT_SPECIAL_PRICE);
    }

    /**
     * load the images
     */
    private void loadImages() {
        images = new ArrayList<ImageSet>();
        List<HippoMirror> mirrors = getChildBeansByName(KKCndConstants.PRODUCT_IMAGES);
        for (HippoBean mirror : mirrors) {
            if (mirror instanceof HippoFacetSelect) {
                HippoFacetSelect facetSelect = (HippoFacetSelect) mirror;
                HippoBean referenced = facetSelect.getReferencedBean();
                if (referenced instanceof ImageSet) {
                    ImageSet image = (ImageSet) referenced;
                    images.add(image);
                }
            }
        }
    }

    /**
     * Load the compound price.
     */
    private void loadStandardPrice() {
        // Retrieve Konakart node
        List<Price> priceList = getChildBeans(Price.class);

        // Should not happends
        if ((priceList == null) || (priceList.size() == 0)) {
            return;
        }

        standardPrice = priceList.get(0);
    }
}
