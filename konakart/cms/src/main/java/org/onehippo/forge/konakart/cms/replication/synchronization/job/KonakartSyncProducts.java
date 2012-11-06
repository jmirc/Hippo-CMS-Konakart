package org.onehippo.forge.konakart.cms.replication.synchronization.job;

import com.konakart.al.KKAppEng;
import com.konakart.app.*;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ProductSearchIf;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminProductMgrIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.translation.ILocaleProvider;
import org.hippoecm.repository.api.HippoNodeType;
import org.hippoecm.repository.translation.HippoTranslationNodeType;
import org.onehippo.forge.konakart.cms.replication.factory.DefaultProductFactory;
import org.onehippo.forge.konakart.cms.replication.factory.ProductFactory;
import org.onehippo.forge.konakart.cms.replication.utils.Codecs;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.bl.CustomProductMgr;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class KonakartSyncProducts {

    private static final int NBR_PRODUCTS_PER_BATCH = 100;

    public static final Logger log = LoggerFactory.getLogger(KonakartSyncProducts.class);

    /**
     * Copy products from Konakart to Hippo
     *
     * @param kkStoreConfig the store config
     * @param locales       list of available locales
     * @throws Exception an exception
     */
    public static synchronized boolean updateKonakartToHippo(KKStoreConfig kkStoreConfig, List<? extends ILocaleProvider.HippoLocale> locales, Session jcrSession) throws Exception {
        KKAppEng kkengine = KKEngine.get(kkStoreConfig.getStoreId());

        // Retrieve the list of languages defined into konakart.
        LanguageIf[] languages = kkengine.getEng().getAllLanguages();

        // Retrieve the content root
        Node contentRoot = getProductRoot(kkStoreConfig, jcrSession);
        Locale currentLocale = getLocale(contentRoot, locales);

        boolean updated = false;

        // For each language defined into Konakart we need to add the product under Hippo
        for (LanguageIf language : languages) {

            // Get the Hippo product folder name
            String storeId = kkStoreConfig.getStoreId();

            if (currentLocale == null || !StringUtils.equals(currentLocale.toString(), language.getLocale())) {
                log.info("############################################################################");
                log.info("##");
                log.info("##");
                log.info("Unable to map the Konakart locale <" + language.getLocale() + "> with any available hippo locale");
                log.info("##");
                log.info("##");
                log.info("############################################################################");
                continue;
            }

            log.info("############################################################################");
            log.info("##");
            log.info("##");
            log.info("Sync the Konakart locale <" + language.getLocale() + ">");
            log.info("##");
            log.info("##");
            log.info("############################################################################");

            updated = true;

            // Initialize the KKEngine
            kkengine = KKEngine.get(storeId);

            // Retrieve the product factory
            CustomProductMgr productMgr;

            // In the development mode the entire product lists is retrieved.
            if (kkStoreConfig.isDevelopmentMode() || !hasProducts(kkStoreConfig, jcrSession)) {
                productMgr = new CustomProductMgr(kkengine.getEng());
            } else {
                productMgr = new CustomProductMgr(kkengine.getEng(), kkStoreConfig.getLastUpdatedTimeKonakartToRepository());
            }

            // Get all products - visible or not
            DataDescriptorIf dataDescriptorIf = new DataDescriptor();
            dataDescriptorIf.setShowInvisible(true);
            dataDescriptorIf.setFillDescription(true);
            dataDescriptorIf.setLimit(NBR_PRODUCTS_PER_BATCH);

            // Search products from all stores
            ProductSearchIf productSearchIf = new ProductSearch();

            // Used default products options
            FetchProductOptions fetchProductOptions = new FetchProductOptions();


            if (StringUtils.isNotEmpty(kkStoreConfig.getCatalogId())) {
                fetchProductOptions.setCatalogId(kkStoreConfig.getCatalogId());
            }

            // Retrieve the path where the images are saved
            // First check if this config has been set within Hippo, if not retrieved from Konakart
            String baseImagePath;

            if (StringUtils.isNotEmpty(kkStoreConfig.getImageBasePath())) {
                baseImagePath = kkStoreConfig.getImageBasePath();
            } else {
                baseImagePath = kkengine.getEng().getConfiguration("IMG_BASE_PATH").getValue();
            }

            ProductFactory productFactory = createProductFactory(kkStoreConfig.getProductFactoryClassName());
            productFactory.setSession(jcrSession);
            productFactory.setKKStoreConfig(kkStoreConfig);

            // We get the products by batches, otherwise it takes too much memory.
            int nbrDone = 0;
            Products products;

            do {
                // set the offset to start the search from
                dataDescriptorIf.setOffset(nbrDone);

                // Search
                products = productMgr.searchForProductsWithOptions(kkengine.getSessionId(),
                        dataDescriptorIf,
                        productSearchIf,
                        language.getId(),
                        fetchProductOptions);
                if (products.getProductArray().length > 0) {
                    nbrDone += products.getProductArray().length;

                    if (log.isInfoEnabled()) {
                        log.info("A batch of " + products.getProductArray().length + " product(s) will be synchronized from Konakart to Hippo.");
                    }

                    // Insert products into konakart
                    for (Product product : products.getProductArray()) {
                        productFactory.add(storeId, product, language, baseImagePath);
                    }

                    // Each batch session, save the inserted products
                    jcrSession.save();
                }

            } while (nbrDone < products.getTotalNumProducts());

        }

        return updated;
    }


    /**
     * Synchronize produts status updates and multi-store update
     *
     * @param kkStoreConfig the store config
     * @param jcrSession    JCR Session
     * @throws Exception .
     */
    public static synchronized void updateHippoToKonakart(KKStoreConfig kkStoreConfig, Session jcrSession) throws Exception {

        NodeHelper nodeHelper = new NodeHelper(jcrSession);

        AdminMgrFactory adminMgrFactory = KKAdminEngine.getInstance().getFactory();

        AdminProductMgrIf productMgrIf = adminMgrFactory.getAdminProdMgr(true);


        Node seed = getProductRoot(kkStoreConfig, jcrSession);

        List<SyncProduct> syncProducts = findAllProductIdsFromRepository(seed, false);

        if (syncProducts.size() > 0) {
            if (log.isInfoEnabled()) {
                log.info(syncProducts.size() + " product(s) will be synchronized from Hippo to Konakart");
            }
        }

        for (SyncProduct syncProduct : syncProducts) {
            // Retrieve the hippo node
            Node node = jcrSession.getNodeByIdentifier(syncProduct.getHippoUuid());

            // Check if the product exists
            if (!productMgrIf.doesProductExist(syncProduct.getkProductId())) {
                nodeHelper.updateState(node, NodeHelper.UNPUBLISHED_STATE);
            }
        }
    }

    /**
     * During the development process, sometimes the sync node contains the sync date but no product has been synchronized.
     * The goal of this method is to validate if the sync dates must be forgot.
     *
     * @param kkStoreConfig the current konakart config
     * @param jcrSession    the JCR session
     * @return true if Hippo repository has products, false otherwise
     */
    private static boolean hasProducts(KKStoreConfig kkStoreConfig, Session jcrSession) {
        try {
            Node seed = getProductRoot(kkStoreConfig, jcrSession);

            List<SyncProduct> syncProducts = findAllProductIdsFromRepository(seed, true);

            return syncProducts != null && syncProducts.size() > 0;
        } catch (Exception e) {
            log.error("Failed to check if at least a product has been synchronized.", e);
        }

        return false;
    }


    @Nonnull
    private static Node getProductRoot(KKStoreConfig kkStoreConfig, Session jcrSession) throws Exception {
        Node seed = null;

        String productRoot = kkStoreConfig.getContentRoot() + "/" + Codecs.encodeNode(kkStoreConfig.getProductFolder());

        if (jcrSession.itemExists(productRoot)) {
            seed = jcrSession.getNode(productRoot);
        }

        if (seed == null) {
            String absPath = kkStoreConfig.getContentRoot() + "/" + kkStoreConfig.getProductFolder();

            NodeHelper nodeHelper = new NodeHelper(jcrSession);
            seed = nodeHelper.createMissingFolders(absPath);
        }

        return seed;
    }

    /**
     * Get the locale of a node representing a translated document, or a compound therein
     *
     * @param node    Node
     * @param locales list of available locales
     * @return Locale (nullable)
     */
    private static Locale getLocale(Node node, List<? extends ILocaleProvider.HippoLocale> locales) {
        if (node == null) {
            return null;
        }

        Locale locale = null;
        try {

            // in case of compounds (that should not be translated), move up the tree
            Node docNode = node;
            while (!docNode.isNodeType(HippoTranslationNodeType.NT_TRANSLATED)) {

                docNode = docNode.getParent();

                // stop at handle level
                if (docNode.isNodeType(HippoNodeType.NT_HANDLE)) {
                    break;
                }
            }

            if (!docNode.isNodeType(HippoTranslationNodeType.NT_TRANSLATED)) {
                log.debug("No translated nodes found for node '{}' and below", HippoTranslationNodeType.LOCALE, docNode.getPath());
                return null;
            }

            final String property = docNode.getProperty(HippoTranslationNodeType.LOCALE).getString();
            if (property.isEmpty()) {
                log.debug("Property '{}' is empty for node '{}'", HippoTranslationNodeType.LOCALE, docNode.getPath());
                return null;
            }

            // create locale
            final String[] parts = property.split("_");

            if (parts.length >= 1) {
                String name = parts[0];

                for (ILocaleProvider.HippoLocale hippoLocale : locales) {
                    if (StringUtils.equals(hippoLocale.getName(), name)) {
                        locale = hippoLocale.getLocale();
                        break;
                    }
                }
            }

        } catch (RepositoryException e) {
            log.warn(e.getMessage(), e);
        }

        return locale;
    }


    private static List<SyncProduct> findAllProductIdsFromRepository(Node seed, Boolean onlyFirstRetrieve) throws RepositoryException {

        List<SyncProduct> syncProducts = new ArrayList<SyncProduct>();

        try {
            if (seed.isNodeType("hippo:handle")) {
                seed = seed.getNode(seed.getName());
            }

            if (seed.isNodeType(KKCndConstants.PRODUCT_DOC_TYPE)) {
                SyncProduct syncProduct = new SyncProduct();
                syncProduct.setHippoUuid(seed.getIdentifier());
                syncProduct.setkProductId((int) seed.getProperty(KKCndConstants.PRODUCT_ID).getLong());

                syncProducts.add(syncProduct);

                if (onlyFirstRetrieve) {
                    return syncProducts;
                }

            } else if (seed.isNodeType("hippostd:folder")) {
                for (NodeIterator nodeIt = seed.getNodes(); nodeIt.hasNext(); ) {
                    Node child = nodeIt.nextNode();

                    if (child != null) {
                        syncProducts.addAll(findAllProductIdsFromRepository(child, false));
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to retrieve the list of products");
        }

        return syncProducts;
    }


    private static ProductFactory createProductFactory(String productFactoryClassName) {
        if (StringUtils.isNotBlank(productFactoryClassName)) {
            try {
                return (ProductFactory) Class.forName(productFactoryClassName).newInstance();

            } catch (InstantiationException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (IllegalAccessException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (ClassNotFoundException e) {
                log.error("Unable to find the extension class: " + e.toString());
            }
        }

        return new DefaultProductFactory();
    }


    public static class SyncProduct {
        private int kProductId;
        private String hippoUuid;

        public int getkProductId() {
            return kProductId;
        }

        public void setkProductId(int kProductId) {
            this.kProductId = kProductId;
        }

        public String getHippoUuid() {
            return hippoUuid;
        }

        public void setHippoUuid(String hippoUuid) {
            this.hippoUuid = hippoUuid;
        }
    }
}
