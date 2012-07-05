package org.onehippo.forge.konakart.cms.replication.factory;

import com.konakart.app.Product;

import javax.jcr.Node;

/**
 * The default factory doesn't update any properties.
 * <p/>
 * This factory could be overridden if you want to add or to update properties
 */
public class DefaultProductFactory extends AbstractProductFactory {

    @Override
    protected void updateProperties(Product product, Node node) {
        // do nothing
    }
}
