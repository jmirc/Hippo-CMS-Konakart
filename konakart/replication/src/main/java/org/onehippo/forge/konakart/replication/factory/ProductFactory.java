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
     * @param contentRoot set the content root where the document will be created.
     */
    void setContentRoot(String contentRoot);

    /**
     * @param galleryRoot set the gallery root where the images will be saved
     */
    void setGalleryRoot(String galleryRoot);


    /**
     * @param productFolder set the name of the folder where the product will be created
     */
    void setProductFolder(String productFolder);

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
     * @param baseImagePath the path where the konakart images are located
     * @throws Exception if any exceptions occurs
     */
    void add(Product product, LanguageIf language, String baseImagePath) throws Exception;


}
