package org.onehippo.forge.konakart.hst.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.*;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the base product document class. Must be extended by each product's component.
 */
@Node(jcrType = KKCndConstants.PRODUCT_DOC_TYPE)
public class KKProductDocument extends HippoDocument {

    protected Logger log = LoggerFactory.getLogger(KKProductDocument.class);

    private List<HippoGalleryImageSet> images;

    public int getProductId() {
        Long id = getProperty(KKCndConstants.PRODUCT_ID);

        return id.intValue();
    }

    public String getName() {
        return getProperty(KKCndConstants.PRODUCT_NAME);
    }

    public String getSku() {
        return getProperty(KKCndConstants.PRODUCT_SKU);
    }

    public String getModel() {
        return getProperty(KKCndConstants.PRODUCT_MODEL);
    }

    public String getStoreId() {
        return getProperty(KKCndConstants.PRODUCT_STORE_ID);
    }

    public HippoHtml getAbstractInfo() {
        return getHippoHtml(KKCndConstants.PRODUCT_ABSTRACT);
    }

    public HippoHtml getDescription() {
        return getHippoHtml(KKCndConstants.PRODUCT_DESCRIPTION);
    }

    public Double getSpecialPrice() {
        return getProperty(KKCndConstants.PRODUCT_SPECIAL_PRICE);
    }

    public Double getPrice0() {
        return getProperty(KKCndConstants.PRODUCT_PRICE_0);
    }

    public Double getPrice1() {
        return getProperty(KKCndConstants.PRODUCT_PRICE_1);
    }

    public Double getPrice2() {
        return getProperty(KKCndConstants.PRODUCT_PRICE_2);
    }

    public Double getPrice3() {
        return getProperty(KKCndConstants.PRODUCT_PRICE_3);
    }

    public Long getQuantity() {
        return getProperty(KKCndConstants.PRODUCT_QUANTITY);
    }

    public Double getWeight() {
        return getProperty(KKCndConstants.PRODUCT_WEIGHT);
    }

    public Boolean getCanOrderNotInStock() {
        return getProperty(KKCndConstants.PRODUCT_ORDER_NOT_IN_STOCK);
    }

    /**
     * @return the list of images associated the product
     */
    public List<HippoGalleryImageSet> getImages() {
        if (images == null) {
            loadImages();
        }
        return images;
    }

    /**
     * @return the main image
     */
    public HippoGalleryImageSet getMainImage() {
        if (images == null) {
            loadImages();
        }

        return images.size() == 0 ? null : images.get(0);
    }

    /**
     * load the images
     */
    private void loadImages() {
        images = new ArrayList<HippoGalleryImageSet>();
        List<HippoMirror> mirrors = getChildBeansByName(KKCndConstants.PRODUCT_IMAGES);
        for (HippoBean mirror : mirrors) {
            if (mirror instanceof HippoFacetSelect) {
                HippoFacetSelect facetSelect = (HippoFacetSelect) mirror;
                HippoBean referenced = facetSelect.getReferencedBean();
                if (referenced instanceof HippoGalleryImageSet) {
                    HippoGalleryImageSet image = (HippoGalleryImageSet) referenced;
                    images.add(image);
                }
            }
        }
    }
}
