package org.onehippo.forge.konakart.replication.factory;

import com.konakart.app.Language;
import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

public interface ProductFactory {

    /**
     * @param jcrSession set the jcr session
     * @throws javax.jcr.RepositoryException .
     */
    void setSession(Session jcrSession) throws RepositoryException;

    /**
     * @param contentRoot set the location where the product will be created
     */
    void setContentRoot(String contentRoot);


    /**
     * @param productDocType the product document type
     */
    void setProductDocType(String productDocType);

    /**
     *
     * @param konakartProductPropertyName .
     */
    void setKonakartProductPropertyName(String konakartProductPropertyName);

    /**
     * Add a product to hippo
     *
     * @param product the product to add
     * @param language the language associated to this product
     * @throws Exception if any exceptions occurs
     * @return the JCR node UUID
     */
    String add(Product product, LanguageIf language) throws Exception;
}
