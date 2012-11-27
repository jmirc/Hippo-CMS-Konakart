package org.onehippo.cms7.hst.hippokart.gogreen.hippo.plugins;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.rmi.client.ClientNodeType;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ProductHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ReviewHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.loader.CategoryLoader;
import org.onehippo.cms7.hst.hippokart.gogreen.database.utils.LanguageUtil;
import org.onehippo.cms7.hst.hippokart.gogreen.database.vo.ProductDescriptionVo;
import org.onehippo.forge.jcrrunner.plugins.AbstractRunnerPlugin;

import javax.jcr.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class CreateReviewPlugin extends AbstractRunnerPlugin {

    @Override
    public void visit(Node node) {
        try {
            ClientNodeType clientNodeType = (ClientNodeType) node.getPrimaryNodeType();

            if (clientNodeType.getName().equals("hippogogreen:review")) {

                // Get the name from the handle
                System.out.println(node.getName());

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
        } catch (RepositoryException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
