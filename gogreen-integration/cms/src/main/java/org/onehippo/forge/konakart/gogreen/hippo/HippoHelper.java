package org.onehippo.forge.konakart.gogreen.hippo;

import org.onehippo.forge.konakart.cms.replication.utils.Codecs;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.gogreen.hippo.visitor.CreateProductsVisitor;
import org.onehippo.forge.konakart.gogreen.hippo.visitor.CreateReviewVisitor;
import org.onehippo.forge.konakart.gogreen.hippo.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * Helper class used to retrieve a connection to the repository
 */
public class HippoHelper {
  public static final Logger log = LoggerFactory.getLogger(HippoHelper.class);

  public static final String DEFAULT_STORE = "store1";
  public static final String CONTENT_DOCUMENTS_HIPPOGOGREEN_PRODUCTS = "/content/documents/hippogogreen/products";
  public static final String CONTENT_DOCUMENTS_HIPPOGOGREEN_REVIEWS = "/content/documents/hippogogreen/reviews";
  private Session jcrSession;

  public HippoHelper(Session jcrSession) {
    this.jcrSession = jcrSession;
  }


  /**
   * Synchronize produts
   *
   * @throws Exception .
   */
  public Node startProductSynchro() throws Exception {
    KKStoreConfig kkStoreConfig = HippoModuleConfig.getConfig().getStoresConfig().get(DEFAULT_STORE);

    if (jcrSession.nodeExists(CONTENT_DOCUMENTS_HIPPOGOGREEN_PRODUCTS)) {
      // Get the product's root node
      Node root = jcrSession.getNode(CONTENT_DOCUMENTS_HIPPOGOGREEN_PRODUCTS);

      String imagesRoot = kkStoreConfig.getGalleryRoot() + "/" + Codecs.encodeNode(kkStoreConfig.getProductFolder());

      // Visit all nodes.
      CreateProductsVisitor visitor = new CreateProductsVisitor(imagesRoot);
      visitor.visit(root);
      visitAllNodes("hippogogreen:product", root, visitor);

      return root;
    }

    return null;
  }


  public Node startReviewSynchro() throws RepositoryException {

    if (jcrSession.nodeExists(CONTENT_DOCUMENTS_HIPPOGOGREEN_REVIEWS)) {
      Node root = jcrSession.getNode(CONTENT_DOCUMENTS_HIPPOGOGREEN_REVIEWS);

      // Visit all nodes
      CreateReviewVisitor visitor = new CreateReviewVisitor();
      visitor.visit(root);
      visitAllNodes("hippogogreen:review", root, visitor);

      return root;
    }
    return null;
  }

  private void visitAllNodes(String nodeType, Node seed, Visitor visitor) throws RepositoryException {

    try {
      if (seed.isNodeType("hippo:handle")) {
        seed = seed.getNode(seed.getName());
      }

      if (seed.isNodeType(nodeType)) {
        visitor.visit(seed);
      } else if (seed.isNodeType("hippostd:folder")) {
        for (NodeIterator nodeIt = seed.getNodes(); nodeIt.hasNext(); ) {
          Node child = nodeIt.nextNode();

          if (child != null) {
            visitAllNodes(nodeType, child, visitor);
          }
        }
      }
    } catch (Exception e) {
      log.error("Failed to retrieve the list of products");
    }
  }


}
