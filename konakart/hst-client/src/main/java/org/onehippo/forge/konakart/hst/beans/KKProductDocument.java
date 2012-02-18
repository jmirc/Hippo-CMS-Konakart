package org.onehippo.forge.konakart.hst.beans;

import com.konakart.app.KKException;
import com.konakart.appif.ProductIf;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.onehippo.forge.konakart.common.engine.KKEngineIf;
import org.onehippo.forge.konakart.hst.beans.compound.Konakart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This is the base product document class. Must be extended by each product's component.
 */
public abstract class KKProductDocument extends HippoDocument {

    protected Logger log = LoggerFactory.getLogger(KKProductDocument.class);

    private ProductIf product;

    private KKEngineIf kkEngine;


    /**
     * Set the Konakart engine
     * @param kkEngine the konakart engine
     */
    public void setKkEngine(KKEngineIf kkEngine) {
        this.kkEngine = kkEngine;
        loadProduct();
    }


    /**
     * @return the product information
     */
    public ProductIf getProduct() {
        return product;
    }

    /**
     * @return the product's name
     */
    public String getName() {
        if (product == null) {
            return "";
        }

        return product.getName();
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

        Konakart konakart = konakartList.get(0);

        if (product == null) {
            try {
                product = kkEngine.getProductMgr().getProductById(konakart.getProductId().intValue(), konakart.getLanguageId().intValue());
            } catch (KKException e) {
                log.error("Unable to retrieve the product with the id : " + konakart.getProductId());
            }
        }
    }
}
