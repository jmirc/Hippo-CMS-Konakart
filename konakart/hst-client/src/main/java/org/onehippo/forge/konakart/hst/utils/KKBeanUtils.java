package org.onehippo.forge.konakart.hst.utils;

import com.konakart.al.KKAppEng;
import com.konakart.appif.CategoryIf;
import com.konakart.appif.ProductIf;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class KKBeanUtils {

  public static final Logger LOGGER = LoggerFactory.getLogger(KKBeanUtils.class);

  /**
   * Retrieve the productIf instance based on the product id passed as parameter.
   *
   * @param productId id of the product to retrieve
   * @return the productIf instance
   */
  @Nullable
  public static ProductIf getProductById(Integer productId) {
    try {
      KKAppEng kkAppEng = KKEngine.get();
      return kkAppEng.getEng().getProduct(kkAppEng.getSessionId(), productId, kkAppEng.getLangId());
    } catch (Exception e) {
      LOGGER.error("Failed to retrieve the product id - " + productId, e);
    }

    return null;
  }

  /**
   * Retrieve the category associated to the product
   *
   * @param productIf the product
   * @return the category or null if the product has no category
   */
  public static CategoryIf getCategoryById(ProductIf productIf) {

    if (productIf == null) {
      throw new IllegalArgumentException("productIf should not be null");
    }

    if (productIf.getCategoryId() > 0) {
      try {
        KKAppEng kkAppEng = KKEngine.get();
        return kkAppEng.getEng().getCategory(productIf.getCategoryId(), kkAppEng.getLangId());
      } catch (Exception e) {
        LOGGER.error("Failed to retrieve the category id - " + productIf.getCategoryId(), e);
      }
    }

    return null;
  }

}
