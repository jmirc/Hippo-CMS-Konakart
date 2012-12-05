package org.onehippo.forge.konakart.gogreen.replication.service;

import com.konakartadmin.app.KKConfiguration;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminConfigurationMgrIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.cms.replication.service.KonakartSynchronizationService;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.gogreen.database.CleanDatabase;
import org.onehippo.forge.konakart.gogreen.database.InitializeDatabase;
import org.onehippo.forge.konakart.gogreen.hippo.HippoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * The goal of this class is to move the products from Hippo to Konakart.
 * First, konakart database will be cleaned and will be initialized with specifics data.
 * Second, all gogreen products will be moved from Hippo to Konakart
 */
public class GogreenKonakartSynchronizationService extends KonakartSynchronizationService {

  public static final Logger log = LoggerFactory.getLogger(GogreenKonakartSynchronizationService.class);

  public static final String GOGREEN_INIT_KEY = "GOGREEN_INIT_KEY";

  public GogreenKonakartSynchronizationService(IPluginContext context, IPluginConfig config) {
    super(context, config);
  }

  @Override
  protected void initializeJobs(Session jcrSession) {

    // Initialize the Konakart engine
    KKEngine.init(jcrSession);
    KKAdminEngine.init(jcrSession);

    // Initialize the database
    AdminMgrFactory adminMgrFactory = KKAdminEngine.getInstance().getFactory();

    try {
      AdminConfigurationMgrIf configurationMgrIf = adminMgrFactory.getAdminConfigMgr(false);

      // Check if the initialization has been already done
      if (!configurationMgrIf.getConfigurationValueAsBool(GOGREEN_INIT_KEY, false)) {

        // Clean database
        CleanDatabase.execute();

        // Load data
        InitializeDatabase.execute(adminMgrFactory);

        HippoHelper hippoHelper = new HippoHelper(jcrSession);

        // Synchronize products
        Node productNode = hippoHelper.startProductSynchro();

        // Synchronize reviews
        Node reviewNode = hippoHelper.startReviewSynchro();

        // remove the products sub folders for him and it's translation folders
        cleanProducts(jcrSession, productNode);

        reviewNode.remove();
        reviewNode.getSession().save();

        // Update konakart to inform that the sync is done
        KKConfiguration kkConfiguration = new KKConfiguration();
        kkConfiguration.setConfigurationGroupId(1);
        kkConfiguration.setConfigurationKey(GOGREEN_INIT_KEY);
        kkConfiguration.setConfigurationTitle("GoGreen synchronization with konakart");
        kkConfiguration.setConfigurationDescription("Set to true when the sync is done");
        kkConfiguration.setConfigurationValue("true");
        configurationMgrIf.insertConfiguration(kkConfiguration);
      }

    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize Konakart database", e);
    }


    // Sync the konakart products to hippo
    super.initializeJobs(jcrSession);
  }

  private void cleanProducts(Session jcrSession, Node productNode) throws RepositoryException {

    log.info("Remove all products sub folders");

    if (productNode.hasNode("hippotranslation:translations")) {
      Node translationsNode = productNode.getNode("hippotranslation:translations");

      NodeIterator productCountriesNode = translationsNode.getNodes();

      while (productCountriesNode.hasNext()) {
        Node productCountryFolder = productCountriesNode.nextNode();

        // Find the real node
        Node realProductCountryNode = jcrSession.getNodeByIdentifier(productCountryFolder.getProperty("hippo:uuid").getString());

        NodeIterator productSubFolders = realProductCountryNode.getNodes();

        while (productSubFolders.hasNext()) {
          Node productSubFolder = productSubFolders.nextNode();

          try {
            String primaryNodeTypeName = productSubFolder.getPrimaryNodeType().getName();

            if (StringUtils.equalsIgnoreCase(primaryNodeTypeName, "hippofacnav:facetnavigation")) {
              productSubFolder.remove();
            }

            if (StringUtils.equalsIgnoreCase(primaryNodeTypeName, "hippostd:folder")) {
              productSubFolder.remove();
            }
          } catch (Exception e) {
            log.error("Failed to remove the node " + productSubFolder.getIdentifier() + " - " + e.toString());
          }


        }
      }

      productNode.getSession().save();
      log.info("All products sub folders have been removed.");
    }
  }
}
