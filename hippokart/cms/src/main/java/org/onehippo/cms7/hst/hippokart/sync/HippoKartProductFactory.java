package org.onehippo.cms7.hst.hippokart.sync;

import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.cms.replication.factory.AbstractProductFactory;

import javax.jcr.Node;

/**
 *
 */
public class HippoKartProductFactory extends AbstractProductFactory {

    @Override
    protected void updateProperties(String storeId, Product product, Node node, LanguageIf language) {

    }

    @Override
    protected String createProductNodeRoot(Product product) {

        // Get the creation time
        DateTime dateTime = new DateTime(product.getDateAvailable().getTime().getTime());

        return  dateTime.getYear() + "/" + dateTime.getMonthOfYear() +  "/" + product.getName();
    }
}
