package org.onehippo.forge.konakart.gogreen.replication.factory;

import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.cms.replication.factory.DefaultProductFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Collection;

/**
 *
 */
public class GogreenProductFactory extends DefaultProductFactory {

  private static Logger log = LoggerFactory.getLogger(GogreenProductFactory.class);

  public static final String NO_TRANSLATION_YET = "NO TRANSLATION YET";

  @Override
  public boolean shouldAddProduct(Product product, LanguageIf language) {
    return !StringUtils.equalsIgnoreCase(product.getName(), NO_TRANSLATION_YET);
  }

  @Override
  protected String getLanguageToSetToHippoDoc(LanguageIf locale) {
      return locale.getCode();
  }

  @Override
  protected String createProductNodeRoot(Product product) {
    // Get the creation time
    DateTime dateTime = new DateTime(product.getDateAvailable().getTime().getTime());

    return dateTime.getYear() + "/" + dateTime.getMonthOfYear();
  }

  @Override
  protected void uploadImages(LanguageIf language, Node productNode, String baseImagePath, Product product) {

    try {
      String galleryRootNode = getGalleryRoot();

      Collection<String> images = getImagesByLanguage(product, language);

      for (String image : images) {

        String rootImage = galleryRootNode + image;

        try {
          if (session.nodeExists(rootImage)) {
            Node imageNode = session.getNode(rootImage);
            linkImageToProduct(productNode, imageNode);
          }
        } catch (RepositoryException e) {
          log.error("Failed to link the image to the product", e);
        }
      }
    } catch (Exception e) {
      log.error("Failed to add images", e);
    }
  }
}
