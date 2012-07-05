/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.cms.replication.synchronization.job;


import com.konakart.al.KKAppEng;
import com.konakart.app.*;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ProductSearchIf;
import com.konakart.util.KKConstants;
import com.konakartadmin.app.AdminProduct;
import com.konakartadmin.app.KKAdminException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hippoecm.frontend.translation.ILocaleProvider;
import org.hippoecm.repository.api.HippoNodeType;
import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.hippoecm.repository.translation.HippoTranslationNodeType;
import org.onehippo.forge.konakart.cms.replication.factory.DefaultProductFactory;
import org.onehippo.forge.konakart.cms.replication.factory.ProductFactory;
import org.onehippo.forge.konakart.cms.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.cms.replication.service.KonakartSynchronizationService;
import org.onehippo.forge.konakart.cms.replication.synchronization.KonakartResourceScheduler;
import org.onehippo.forge.konakart.cms.replication.utils.Codecs;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.bl.CustomProductMgr;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static org.hippoecm.frontend.translation.ILocaleProvider.*;

public class KonakartSyncProductJob implements Job {

    private static Logger log = LoggerFactory.getLogger(KonakartSyncProductJob.class);


    private javax.jcr.Session jcrSession;

    /**
     * Start the replication
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("Executing Konakart Products Replicator ...");

        // Set the JcrSession
        KonakartResourceScheduler scheduler = (KonakartResourceScheduler) context.getScheduler();
        jcrSession = ((JCRSchedulingContext) scheduler.getCtx()).getSession();
        String storeId = context.getJobDetail().getJobDataMap().getString(KonakartSynchronizationService.KK_STORE_ID);

        @SuppressWarnings("unchecked")
        List<? extends ILocaleProvider.HippoLocale> locales =
                (List<? extends ILocaleProvider.HippoLocale>) context.getJobDetail().getJobDataMap().get(KonakartSynchronizationService.LOCALES);

        KKStoreConfig kkStoreConfig = HippoModuleConfig.getConfig().getStoresConfig().get(storeId);


        if ((kkStoreConfig == null) || !kkStoreConfig.isInitialized()) {
            log.error("The Konakart synchronization service has not well be initialized. Please check the log.");
            return;
        }

        // Load the gallery processor service
        GalleryProcesssorConfig.load(jcrSession);

        try {
            // Initialize the Konakart engine
            KKEngine.init(jcrSession);


            try {
                // Synchronize konakart information
                syncRepositoryToKonakart(kkStoreConfig, locales);
                kkStoreConfig.updateLastUpdatedTimeRepositoryToKonakart(jcrSession);
            } catch (Exception e) {
                log.warn("Failed to update Repository to Konakart. ", e);
            }

            // Synchronize hippo product
            try {
                updateKonakartToRepository(kkStoreConfig);
                kkStoreConfig.updateLastUpdatedTimeKonakartToRepository(jcrSession);
            } catch (Exception e) {
                log.warn("Failed to update Konakart to Repository. ", e);
            }

        } catch (Exception e) {
            log.warn("Failed to initialize Konakart engine. ", e);
        }
    }

    /**
     * Synchronize Konakart information to Hippo
     *
     * @param kkStoreConfig the store config
     * @param locales list of available locales
     * @throws Exception an exception
     */
    private void syncRepositoryToKonakart(KKStoreConfig kkStoreConfig,
                                          List<? extends ILocaleProvider.HippoLocale> locales) throws Exception {

        // Synchronize products
        syncProducts(kkStoreConfig, locales);
    }

    /**
     * Copy products from Konakart to Hippo
     *
     *
     * @param kkStoreConfig the store config
     * @param locales list of available locales
     * @throws Exception an exception
     */
    private void syncProducts(KKStoreConfig kkStoreConfig, List<? extends HippoLocale> locales) throws Exception {
        KKAppEng kkengine = KKEngine.get(KKConstants.KONAKART_DEFAULT_STORE_ID);

        // Retrieve the list of languages defined into konakart.
        LanguageIf[] languages = kkengine.getEng().getAllLanguages();

        // Retrieve the content root
        Node contentRoot = getProductRoot(kkStoreConfig);
        Locale currentLocale = getLocale(contentRoot, locales);

        // For each language defined into Konakart we need to add the product under Hippo
        for (LanguageIf language : languages) {

            String storeId = kkStoreConfig.getStoreId();

            if (!StringUtils.equals(currentLocale.toString(), language.getLocale())) {
                log.info("Unable to map the Konakart locale <" + language.getLocale() +"> with any available hippo locale");
                continue;
            }

            // Initialize the KKEngine
            kkengine = KKEngine.get(storeId);

            // Retrieve the product factory
            CustomProductMgr productMgr = new CustomProductMgr(kkengine.getEng(), kkStoreConfig.getLastUpdatedTimeKonakartToRepository());

            // Get only visible products
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
     * @throws Exception .
     */
    private void updateKonakartToRepository(KKStoreConfig kkStoreConfig) throws Exception {

        NodeHelper nodeHelper = new NodeHelper(jcrSession);

        KKAppEng kkengine = KKEngine.get(kkStoreConfig.getStoreId());

        // Try to retrieve the product by id
        CustomProductMgr productMgr = new CustomProductMgr(kkengine.getEng());

        Node seed = getProductRoot(kkStoreConfig);

        if (seed != null) {
            List<SyncProduct> syncProducts = new LinkedList<SyncProduct>();
            findAllProductIdsFromRepository(seed, syncProducts);

            if (syncProducts.size() > 0) {
                if (log.isInfoEnabled()) {
                    log.info(syncProducts.size() + " Hippo product(s) will be synchronized to Konakart");
                }
            }

            for (SyncProduct syncProduct : syncProducts) {
                // Create a new product
                if (syncProduct.getkProductId() == 0) {
                    insertProduct(syncProduct);
                } else {
                    // Retrieve the product from Konakart
                    ProductIf productIf = kkengine.getEng().getProduct(kkengine.getSessionId(), syncProduct.getkProductId(),
                            kkengine.getLangId());

                    // Retrieve the hippo node
                    Node node = jcrSession.getNodeByIdentifier(syncProduct.getHippoUuid());

                    // If the product is null, it means that the product has been removed from the store.
                    // So the Hippo document should be unpublished.
                    if (productIf == null) {
                        nodeHelper.updateState(node.getParent(), NodeHelper.UNPUBLISHED_STATE);
                    } else {
                        // Synchronize the state of a product on konakart according to the state of the node,
                        // is only available when Konakart does not shared products over Stores.
                        if (!KKAppEng.getEngConf().isProductsShared()) {
                            Date lastUpdatedTimeRepositoryToKonakart = kkStoreConfig.getLastUpdatedTimeRepositoryToKonakart();

                            // Update only newer updated products
                            if (lastUpdatedTimeRepositoryToKonakart == null || syncProduct.getLastModificationDate().after(lastUpdatedTimeRepositoryToKonakart)) {
                                String nodeState = nodeHelper.getNodeState(node);

                                if (nodeState != null) {
                                    if (nodeState.equalsIgnoreCase(NodeHelper.UNPUBLISHED_STATE)) {
                                        productMgr.updateStatus(productIf.getId(), false);
                                    } else {
                                        productMgr.updateStatus(productIf.getId(), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private Node getProductRoot(KKStoreConfig kkStoreConfig) throws Exception {
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
     * @param node Node
     * @param locales list of available locales
     * @return Locale (nullable)
     */
    public Locale getLocale(Node node, List<? extends ILocaleProvider.HippoLocale> locales) {
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

                for (HippoLocale hippoLocale : locales) {
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

    private void insertProduct(SyncProduct syncProduct) throws KKAdminException, RepositoryException {
        // Retrieve the hippo node
        Node node = jcrSession.getNodeByIdentifier(syncProduct.getHippoUuid());

        AdminProduct adminProduct = new AdminProduct();

        if (node.hasProperty(KKCndConstants.PRODUCT_NAME)) {
            adminProduct.setName(node.getProperty(KKCndConstants.PRODUCT_NAME).getString());
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_SKU)) {
            adminProduct.setSku(node.getProperty(KKCndConstants.PRODUCT_SKU).getString());
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_PRICE_0)) {
            adminProduct.setPrice0(new BigDecimal(node.getProperty(KKCndConstants.PRODUCT_PRICE_0).getDouble()));
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_PRICE_1)) {
            adminProduct.setPrice1(new BigDecimal(node.getProperty(KKCndConstants.PRODUCT_PRICE_1).getDouble()));
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_PRICE_2)) {
            adminProduct.setPrice2(new BigDecimal(node.getProperty(KKCndConstants.PRODUCT_PRICE_2).getDouble()));
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_PRICE_3)) {
            adminProduct.setPrice3(new BigDecimal(node.getProperty(KKCndConstants.PRODUCT_PRICE_3).getDouble()));
        }

        if (node.hasProperty(KKCndConstants.PRODUCT_TAX_CLASS)) {
            adminProduct.setTaxClassId(NumberUtils.toInt(node.getProperty(KKCndConstants.PRODUCT_TAX_CLASS).getString()));
        }

        KKAdminEngine.getInstance().getEngine().
                insertProduct(KKAdminEngine.getInstance().getSession(), adminProduct);
    }

    private void findAllProductIdsFromRepository(Node seed, List<SyncProduct> syncProducts) throws RepositoryException {
        if (seed.isNodeType("hippo:handle")) {
            seed = seed.getNode(seed.getName());
        }

        if (seed.isNodeType(KKCndConstants.PRODUCT_DOC_TYPE)) {

            Date lastModificationDate = null;

            if (seed.hasProperty("hippostdpubwf:lastModificationDate")) {
                lastModificationDate = seed.getProperty("hippostdpubwf:lastModificationDate").getDate().getTime();
            }

            SyncProduct syncProduct = new SyncProduct();
            syncProduct.setHippoUuid(seed.getIdentifier());
            syncProduct.setkProductId((int) seed.getProperty(KKCndConstants.PRODUCT_ID).getLong());
            syncProduct.setLastModificationDate(lastModificationDate);

            syncProducts.add(syncProduct);

        } else if (seed.isNodeType("hippostd:folder") || seed.isNodeType(KKCndConstants.PRODUCT_TYPE_MIXIN)) {
            for (NodeIterator nodeIt = seed.getNodes(); nodeIt.hasNext(); ) {
                Node child = nodeIt.nextNode();

                if (child != null) {
                    findAllProductIdsFromRepository(child, syncProducts);
                }
            }
        }
    }


    private ProductFactory createProductFactory(String productFactoryClassName) {
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
        private Date lastModificationDate;

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

        public Date getLastModificationDate() {
            return lastModificationDate;
        }

        public void setLastModificationDate(Date lastModificationDate) {
            this.lastModificationDate = lastModificationDate;
        }
    }
}
