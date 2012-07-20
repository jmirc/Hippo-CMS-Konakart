package org.onehippo.forge.konakart.cms.replication.synchronization.job;

import com.konakart.al.KKAppEng;
import com.konakart.app.*;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ProductSearchIf;
import com.konakart.util.KKConstants;
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
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class KonakartSyncProducts {

    public static final Logger log = LoggerFactory.getLogger(KonakartSyncProducts.class);

    /**
     * Copy products from Konakart to Hippo
     *
     * @param kkStoreConfig the store config
     * @param locales       list of available locales
     * @throws Exception an exception
     */
    public static synchronized void updateRepositoryToKonakart(KKStoreConfig kkStoreConfig, List<? extends ILocaleProvider.HippoLocale> locales, Session jcrSession) throws Exception {
        KKAppEng kkengine = KKEngine.get(KKConstants.KONAKART_DEFAULT_STORE_ID);

        // Retrieve the list of languages defined into konakart.
        LanguageIf[] languages = kkengine.getEng().getAllLanguages();

        // Retrieve the content root
        Node contentRoot = getProductRoot(kkStoreConfig, jcrSession);
        Locale currentLocale = getLocale(contentRoot, locales);

        // For each language defined into Konakart we need to add the product under Hippo
        for (LanguageIf language : languages) {

            String storeId = kkStoreConfig.getStoreId();

            if (currentLocale == null || !StringUtils.equals(currentLocale.toString(), language.getLocale())) {
                log.info("############################################################################");
                log.info("Unable to map the Konakart locale <" + language.getLocale() + "> with any available hippo locale");
                log.info("############################################################################");
                continue;
            }

            // Initialize the KKEngine
            kkengine = KKEngine.get(storeId);

            // Retrieve the product factory
            CustomProductMgr productMgr;

            // In the development mode the entire product lists is retrieved.
            if (kkStoreConfig.isDevelopmentMode()) {
                productMgr = new CustomProductMgr(kkengine.getEng());
            } else {
                productMgr = new CustomProductMgr(kkengine.getEng(), kkStoreConfig.getLastUpdatedTimeKonakartToRepository());
            }

            // Get all products - visible or not
            DataDescriptorIf dataDescriptorIf = new DataDescriptor();
            dataDescriptorIf.setShowInvisible(true);
            dataDescriptorIf.setFillDescription(true);

            // Search products from all stores
            ProductSearchIf productSearchIf = new ProductSearch();

            // Used default products options
            FetchProductOptions fetchProductOptions = new FetchProductOptions();

            if (StringUtils.isNotEmpty(kkStoreConfig.getCatalogId())) {
                fetchProductOptions.setCatalogId(kkStoreConfig.getCatalogId());
            }

            // Retrieve the path where the images are saved
            String baseImagePath = kkengine.getEng().getConfiguration("IMG_BASE_PATH").getValue();

            // Search
            Products products = productMgr.searchForProductsWithOptions(kkengine.getSessionId(), dataDescriptorIf,
                    productSearchIf, language.getId(), fetchProductOptions);

            // Get the Hippo product folder name
            String productFolder = kkStoreConfig.getProductFolder();

            if (productFolder == null) {
                continue;
            }

            // Insert products into konakart
            for (Product product : products.getProductArray()) {

                ProductFactory productFactory = createProductFactory(kkStoreConfig.getProductFactoryClassName());

                // Sync the konakart product
                productFactory.setSession(jcrSession);
                productFactory.setKKStoreConfig(kkStoreConfig);
                productFactory.add(storeId, product, language, baseImagePath);
            }
        }
    }


    /**
     * Synchronize produts status updates and multi-store update
     *
     * @param kkStoreConfig the store config
     * @param jcrSession    JCR Session
     * @throws Exception .
     */
    public static synchronized void updateKonakartToRepository(KKStoreConfig kkStoreConfig, Session jcrSession) throws Exception {

        NodeHelper nodeHelper = new NodeHelper(jcrSession);

        KKAppEng kkengine = KKEngine.get(kkStoreConfig.getStoreId());

        Node seed = getProductRoot(kkStoreConfig, jcrSession);

        if (seed != null) {
            List<SyncProduct> syncProducts = new LinkedList<SyncProduct>();
            findAllProductIdsFromRepository(seed, syncProducts);

            if (syncProducts.size() > 0) {
                if (log.isInfoEnabled()) {
                    log.info(syncProducts.size() + " Hippo product(s) will be synchronized to Konakart");
                }
            }

            for (SyncProduct syncProduct : syncProducts) {
                // Retrieve the product from Konakart
                ProductIf productIf = kkengine.getEng().getProduct(kkengine.getSessionId(), syncProduct.getkProductId(),
                        kkengine.getLangId());

                // Retrieve the hippo node
                Node node = jcrSession.getNodeByIdentifier(syncProduct.getHippoUuid());

                // If the product is null, it means that the product has been removed from the store.
                // So the Hippo document should be unpublished.
                if (productIf == null) {
                    nodeHelper.updateState(node.getParent(), NodeHelper.UNPUBLISHED_STATE);
                }
            }
        }
    }

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


    private static void findAllProductIdsFromRepository(Node seed, List<SyncProduct> syncProducts) throws RepositoryException {
        if (seed.isNodeType("hippo:handle")) {
            seed = seed.getNode(seed.getName());
        }

        if (seed.isNodeType(KKCndConstants.PRODUCT_DOC_TYPE)) {

            SyncProduct syncProduct = new SyncProduct();
            syncProduct.setHippoUuid(seed.getIdentifier());
            syncProduct.setkProductId((int) seed.getProperty(KKCndConstants.PRODUCT_ID).getLong());

            syncProducts.add(syncProduct);

        } else if (seed.isNodeType("hippostd:folder")) {
            for (NodeIterator nodeIt = seed.getNodes(); nodeIt.hasNext(); ) {
                Node child = nodeIt.nextNode();

                if (child != null) {
                    findAllProductIdsFromRepository(child, syncProducts);
                }
            }
        }
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
