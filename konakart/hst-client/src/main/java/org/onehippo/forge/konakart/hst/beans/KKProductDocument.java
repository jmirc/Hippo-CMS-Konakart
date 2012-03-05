package org.onehippo.forge.konakart.hst.beans;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.DataDescriptor;
import com.konakart.app.KKException;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ReviewIf;
import com.konakart.appif.ReviewsIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.onehippo.forge.konakart.hst.beans.compound.Konakart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * This is the base product document class. Must be extended by each product's component.
 */
public class KKProductDocument extends HippoDocument {

    protected Logger log = LoggerFactory.getLogger(KKProductDocument.class);

    private Konakart konakart;

    private ProductIf product;

    private KKAppEng kkEngine;


    /**
     * Set the Konakart engine
     *
     * @param kkEngine the konakart engine
     */
    public void setKkEngine(KKAppEng kkEngine) {
        this.kkEngine = kkEngine;
        loadProduct();
    }

    /**
     * @return the Konakart product's id
     */
    public int getProductId() {
        return konakart.getProductId().intValue();
    }


    /**
     * @return the product's name
     */
    public String getName() {
        String name = "";

        if (konakart != null) {
            name = konakart.getProductName();
        }

        // Retrieve the name from Konakart
        if (StringUtils.isEmpty(name)) {
            name = product.getName();
        }

        return name;
    }

    /**
     * @return the product's price
     */
    public String getPrice() {
        return getPrice0();
    }

    /**
     * @return the product's price
     */
    public String getPrice0() {
        BigDecimal price = null;

        try {
            if (konakart != null) {
                price = new BigDecimal(konakart.getStandardPrice().gePrice0ExTax());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve the price - {} ", e.toString());
        }

        if (price == null) {
            price = product.getPrice0();
        }

        try {
            return kkEngine.formatPrice(price);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return the product's price
     */
    public String getPrice1() {
        BigDecimal price = null;

        try {
            if (konakart != null) {
                price = new BigDecimal(konakart.getStandardPrice().gePrice1ExTax());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve the price - {} ", e.toString());
        }

        if (price == null) {
            price = product.getPrice1();
        }

        try {
            return kkEngine.formatPrice(price);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return the product's price
     */
    public String getPrice2() {
        BigDecimal price = null;

        try {
            if (konakart != null) {
                price = new BigDecimal(konakart.getStandardPrice().gePrice2ExTax());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve the price - {} ", e.toString());
        }

        if (price == null) {
            price = product.getPrice2();
        }

        try {
            return kkEngine.formatPrice(price);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @return the product's price
     */
    public String getPrice3() {
        BigDecimal price = null;

        try {
            if (konakart != null) {
                price = new BigDecimal(konakart.getStandardPrice().gePrice3ExTax());
            }
        } catch (Exception e) {
            log.warn("Failed to retrieve the price - {} ", e.toString());
        }

        if (price == null) {
            price = product.getPrice3();
        }

        try {
            return kkEngine.formatPrice(price);
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * @return the special product's price
     */
    public String getSpecialPrice() {

        Double specialPrice = konakart.getSpecialPrice();

        if (specialPrice == null) {
            return "";
        }

        try {
            return kkEngine.formatPrice(new BigDecimal(specialPrice));
        } catch (KKAppException e) {
            return "";
        }
    }

    /**
     * @return the folder where the reviews will be saved
     */
    public String getReviewsFolder() {
        return product.getCustom2();
    }


    /**
     * @return the rating value
     */
    public int getNumberOfReviews() {
        return product.getNumberReviews();
    }

    /**
     * @return the rating value
     */
    public Double getRating() {

        if (product == null) {
            return 0D;
        }


        DataDescriptorIf dataDescriptorIf = new DataDescriptor();
        dataDescriptorIf.setShowInvisible(false);

        try {
            ReviewsIf reviewsIf = kkEngine.getEng().getReviewsPerProduct(dataDescriptorIf, product.getId());

            if (reviewsIf.getTotalNumReviews() == 0) {
                return 0D;
            }

            // Retreive the reviews.
            ReviewIf[] reviews = reviewsIf.getReviewArray();

            // Double check...
            if (reviews == null || reviews.length == 0) {
                return 0D;
            }

            double rating = 0;

            for (ReviewIf reviewIf : reviews) {
                rating += reviewIf.getRating();
            }

            return rating / reviews.length;
        } catch (KKException e) {
            return 0D;
        }
    }

    /**
     * Retrieve the konakart product based on the product ID.
     */
    private void loadProduct() {

        // Retrieve Konakart node
        List<Konakart> konakartList = getChildBeans(Konakart.class);

        // Should not happends
        if ((konakartList == null) || (konakartList.size() == 0)) {
            return;
        }

        konakart = konakartList.get(0);

        if (product == null) {
            try {
                // Fetch the product related data from the database
                product = kkEngine.getEng().getProduct(kkEngine.getSessionId(), konakart.getProductId().intValue(), kkEngine.getLangId());
            } catch (KKException e) {
                log.error("Unable to retrieve the product with the id : " + konakart.getProductId());
            }
        }
    }


}
