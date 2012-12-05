package org.onehippo.forge.konakart.cms.replication.factory;

import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Collection;

public interface ProductFactory {

  /**
   * @param jcrSession set the jcr session
   * @throws javax.jcr.RepositoryException .
   */
  void setSession(final Session jcrSession) throws RepositoryException;

  /**
   * Set the Konakart config class. Contains the related information used to add a new product
   */
  void setKKStoreConfig(final KKStoreConfig kkStoreConfig);

  /**
   * Check if this product will be added to Hippo or not. For some reason, some products will not be added
   * to hippo.
   *
   * @param product       the product to add
   * @param language      the language associated to this product
   * @return true if the product will be add, false otherwise.
   */
  boolean shouldAddProduct(final Product product, final LanguageIf language);

  /**
   * Add a product to hippo
   *
   * @param storeId       the store id associated with this product
   * @param product       the product to add
   * @param language      the language associated to this product
   * @param baseImagePath the path where the konakart images are located
   * @throws Exception if any exceptions occurs
   */
  void add(final String storeId, final Product product, final LanguageIf language, final String baseImagePath) throws Exception;

}
