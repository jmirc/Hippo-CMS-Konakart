package org.onehippo.forge.konakart.replication.factory;

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
     * Set the konakart product name.
     *
     * @see org.onehippo.forge.konakart.common.KKCndConstants.PRODUCT_TYPE enum
     *
     * @param productTypeName the konakart product name
     */
    void setKKProductTypeName(String productTypeName);

    /**
     *
     * @param konakartProductPropertyName .
     */
    void setKonakartProductPropertyName(String konakartProductPropertyName);

    /**
     * Create a review's folder
     * @param reviewFolder the review's folder to create
     * @return the encoded review folder name
     * @throws Exception if the folder has not been created
     */
    String createReviewFolder(String reviewFolder) throws Exception;

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
