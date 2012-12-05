package org.onehippo.forge.konakart.gogreen.hippo.visitor;

import org.onehippo.forge.konakart.gogreen.database.helper.ProductHelper;
import org.onehippo.forge.konakart.gogreen.database.helper.ReviewHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeTypeDefinition;

/**
 *
 */
public class CreateReviewVisitor implements Visitor {

  public static final Logger log = LoggerFactory.getLogger(CreateReviewVisitor.class);

  @Override
  public void visit(Node node) {
    try {
      NodeTypeDefinition nodeTypeDefinition = node.getPrimaryNodeType();

      if (nodeTypeDefinition.getName().equals("hippogogreen:review")) {

        // Get the name from the handle
        log.debug(node.getName());

        ReviewHelper reviewHelper = new ReviewHelper();


        reviewHelper.setDateAdded(node.getProperty("hippostdpubwf:creationDate").getDate());


        reviewHelper.setReviewText(node.getProperty("hippogogreen:comment").getString());
        reviewHelper.setRating((int) node.getProperty("hippogogreen:rating").getLong());
        reviewHelper.setCustomerName(node.getProperty("hippogogreen:name").getString());
        reviewHelper.setCustomerEmail(node.getProperty("hippogogreen:email").getString());

        // Get the product link
        String id = node.getNode("hippogogreen:productlink").getProperty("hippo:docbase").getString();

        reviewHelper.setProductId(ProductHelper.productsMapping.get(id));

        reviewHelper.process();
      }
    } catch (Exception e) {
      log.error("Failed to insert review", e);
    }
  }

}
