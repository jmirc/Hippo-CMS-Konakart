package org.onehippo.forge.konakart.plugins.speciaprice;

import org.apache.wicket.markup.html.basic.Label;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.service.IEditor;
import org.hippoecm.frontend.service.render.RenderPlugin;
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
 * @author Vijay Kiran
 */
public class RatingPlugin extends RenderPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(RatingPlugin.class);
    private static final long serialVersionUID = 1L;

    public RatingPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        final JcrNodeModel nodeModel = (JcrNodeModel) getModel();
        Node productNode = nodeModel.getNode();

        double avgRating = 0;
        long reviewCount = 0;

        try {
            Node parent = productNode.getParent();
            if (parent.isNodeType("mix:referenceable")) {
                String query = "//*[(*/@hippo:docbase = '" + parent.getUUID() + "') and (@jcr:primaryType='hippogogreen:review')]";

                QueryManager queryManager = productNode.getSession().getWorkspace().getQueryManager();
                Query reviewsQuery = queryManager.createQuery(query, Query.XPATH);

                QueryResult queryResult = reviewsQuery.execute();

                long totalRating = 0L;
                final NodeIterator iterator = queryResult.getNodes();
                while (iterator.hasNext()) {
                    reviewCount++;
                    final Node review = iterator.nextNode();
                    if (review.hasProperty("hippogogreen:rating")) {
                        totalRating += review.getProperty("hippogogreen:rating").getLong();
                    }
                }

                if (reviewCount != 0) {
                    avgRating = (double) totalRating / (double) reviewCount;
                }

                IEditor.Mode mode = IEditor.Mode.fromString(config.getString("mode", "view"));
                if (mode == IEditor.Mode.EDIT) {
                    productNode.setProperty("hippogogreen:rating", avgRating);
                    productNode.setProperty("hippogogreen:votes", reviewCount);
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Error occurred whilie initializing rating plugin: " + e.getMessage(), e);
        }

        add(new Label("avgRating", String.valueOf(avgRating)));
        add(new Label("reviewCount", String.valueOf(reviewCount)));
    }
}