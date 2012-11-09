package org.onehippo.forge.konakart.hst.beans;

import com.konakart.appif.ProductIf;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.*;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.utils.KKBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the base product document class. Must be extended by each product's component.
 */
@Node(jcrType = KKCndConstants.PRODUCT_DOC_TYPE)
public class KKProductDocument extends HippoDocument {

    protected Logger log = LoggerFactory.getLogger(KKProductDocument.class);

    private List<HippoGalleryImageSet> images;
    private ProductIf productIf;
    private boolean shouldIncludeTax;


    public ProductIf getProductIf() {
        if (productIf == null) {
            loadProduct();
        }

        return productIf;
    }

    private void loadProduct() {
        productIf = KKBeanUtils.getProductById(getProductId());

        if (productIf == null) {
            throw new IllegalArgumentException("productIf with the id " + getProductId() + "should not be null");
        }
    }

    public void setShouldIncludeTax(boolean shouldIncludeTax) {
        this.shouldIncludeTax = shouldIncludeTax;
    }

    public int getProductId() {
        Long id = getProperty(KKCndConstants.PRODUCT_ID);

        return id.intValue();
    }

    public BigDecimal getSpecialPrice() {
        if (shouldIncludeTax) {
            return getProductIf().getSpecialPriceIncTax();
        }

        return getProductIf().getSpecialPriceExTax();
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
