package org.onehippo.forge.konakart.plugins.rating;

import org.apache.wicket.markup.html.basic.Label;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

/**
 * Rating Plugin to display readonly fields for Votes and Average Rating
 *
 */
public class RatingPlugin extends RenderPlugin {

    private static Logger log = LoggerFactory.getLogger(RatingPlugin.class);


    private static final Logger LOGGER = LoggerFactory.getLogger(RatingPlugin.class);
    private static final long serialVersionUID = 1L;

    public RatingPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        final JcrNodeModel nodeModel = (JcrNodeModel) getModel();
        Node productNode = nodeModel.getNode();

        double avgRating = 0;
        long reviewCount = 0;

        try {
//            if (productNode.hasProperty(KKCndConstants.PRODUCT_ID)) {
//
//                long productId = productNode.getProperty(KKCndConstants.PRODUCT_ID).getLong();
//
//                // load the konakart module config.
//                HippoModuleConfig hippoModuleConfig = HippoModuleConfig.getConfig();
//
//                // Try to update Update the product id
//                try {
//                    KKEngine KKEngine = new KKEngine(hippoModuleConfig.getEngineConfig());
//
//
//
//
//
//                } catch (Exception e) {
//                    log.warn("Failed to update the statut for the following UUID product : " + uuid, e);
//                }
//            }

            Node parent = productNode.getParent().getParent();



            if (parent.isNodeType("mix:referenceable")) {
                String query = "select * from konakart:review where konakart:reviewproductlink = '" + parent.getIdentifier() +"'";

               QueryManager queryManager = productNode.getSession().getWorkspace().getQueryManager();
                Query reviewsQuery = queryManager.createQuery(query, Query.SQL);

                QueryResult queryResult = reviewsQuery.execute();

                long totalRating = 0L;
                final NodeIterator iterator = queryResult.getNodes();
                while (iterator.hasNext()) {
                    final Node review = iterator.nextNode();
                    reviewCount++;
                    if (review.hasProperty(KKCndConstants.REVIEW_RATING)) {
                        totalRating += review.getProperty(KKCndConstants.REVIEW_RATING).getLong();
                    }
                }

                if (reviewCount != 0) {
                    avgRating = (double) totalRating / (double) reviewCount;
                }

                IEditor.Mode mode = IEditor.Mode.fromString(config.getString("mode", "view"));
                if (mode == IEditor.Mode.EDIT) {
                    productNode.setProperty(KKCndConstants.REVIEW_RATING, avgRating);
                    productNode.setProperty(KKCndConstants.REVIEW_VOTES, reviewCount);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error occurred whilie initializing rating plugin: " + e.getMessage(), e);
        }

        add(new Label("avgRating", String.valueOf(avgRating)));
        add(new Label("reviewCount", String.valueOf(reviewCount)));
    }
}