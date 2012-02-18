package org.onehippo.forge.konakart.demo;

import com.konakart.app.Product;
import org.onehippo.forge.konakart.replication.factory.AbstractProductFactory;

import javax.jcr.Node;

public class MyProductFactory extends AbstractProductFactory {

    @Override
    protected String getProductDocType() {
        return "myhippoproject:productdocument";
    }

    @Override
    protected String getKonakartProductPropertyName() {
        return "myhippoproject:konakart";
    }

    @Override
    protected void updateProperties(Product product, Node node) {

    }
}
