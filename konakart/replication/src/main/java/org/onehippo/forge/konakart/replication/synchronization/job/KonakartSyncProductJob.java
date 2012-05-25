package org.onehippo.forge.konakart.replication.synchronization.job;


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
import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.bl.CustomProductMgr;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKEngineConfig;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.replication.factory.DefaultProductFactory;
import org.onehippo.forge.konakart.replication.factory.ProductFactory;
import org.onehippo.forge.konakart.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.replication.service.KonakartSynchronizationService;
import org.onehippo.forge.konakart.replication.synchronization.KonakartResourceScheduler;
import org.onehippo.forge.konakart.replication.utils.Codecs;
import org.onehippo.forge.konakart.replication.utils.NodeHelper;
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

public class KonakartSyncProductJob implements Job {

    private static Logger log = LoggerFactory.getLogger(KonakartSyncProductJob.class);
    public static final String KONAKART_IS_PRODUCT_MIXIN = "konakart:isProduct";

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
        String contentRoot =  context.getJobDetail().getJobDataMap().getString(KonakartSynchronizationService.KK_CONTENT_ROOT);
        KKStoreConfig kkStoreConfig = HippoModuleConfig.getConfig().getStoresConfig().get(contentRoot );
        KKEngineConfig engineConfig = HippoModuleConfig.getConfig().getEngineConfig();


        if (!kkStoreConfig.isInitialized()) {
            log.error("The Konakart synchronization service has not well be initialized. Please check the log.");
            return;
        }


        if (!kkStoreConfig.isEnabled()) {
            log.info("The Konakart replicator is disabled. No replication will be operated.");
            return;
        }

        // Load the gallery processor service
        GalleryProcesssorConfig.load(jcrSession);

        try {
            // Initialize the Konakart engine
            KKEngine.init(engineConfig.getEngineMode(), engineConfig.isCustomersShared(),
                    engineConfig.isProductsShared());

            // Update konakart information
            try {
                updateRepositoryToKonakart(kkStoreConfig);
                kkStoreConfig.updateLastUpdatedTimeRepositoryToKonakart(jcrSession);
            } catch (Exception e) {
                log.warn("Failed to update Repository to Konakart. ", e);
            }

            // Update hippo product
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
     * Copy products from Konakart to Hippo
     *
     * @param kkStoreConfig the store config
     * @throws Exception an exception
     */
    private void updateKonakartToRepository(KKStoreConfig kkStoreConfig) throws Exception {

        KKAppEng kkengine = KKEngine.get(KKConstants.KONAKART_DEFAULT_STORE_ID);

        // Retrieve the list of languages defined into konakart.
        LanguageIf[] languages = kkengine.getEng().getAllLanguages();

        // For each language defined into Konakart we need to add the product under Hippo
        for (LanguageIf language : languages) {

            String storeId = kkStoreConfig.getStoreId();

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

                if (productFactory == null) {
                    continue;
                }

                productFactory.setSession(jcrSession);
                productFactory.setContentRoot(kkStoreConfig.getContentRoot());
                productFactory.setGalleryRoot(kkStoreConfig.getGalleryRoot());
                productFactory.setProductFolder(productFolder);

                // Create the reviews' folder if not exists
                String reviewName = productFactory.createReviewFolder(kkStoreConfig.getReviewFolder());

                // Create the product
                product.setStoreId(storeId);
                productFactory.add(product, language, baseImagePath);

                // Set the Review folder
                productMgr.synchronizeHippoKK(product.getId(), reviewName);
            }
        }
    }


    /**
     * Synchronize produts status updates and multi-store update
     *
     * @throws Exception .
     * @param kkStoreConfig the store config
     */
    private void updateRepositoryToKonakart(KKStoreConfig kkStoreConfig) throws Exception {

        NodeHelper nodeHelper = new NodeHelper(jcrSession);

        KKAppEng kkengine = KKEngine.get(kkStoreConfig.getStoreId());

        // Try to retrieve the product by id
        CustomProductMgr productMgr = new CustomProductMgr(kkengine.getEng());

        Node seed = null;

        String productRoot = kkStoreConfig.getContentRoot() + "/" + Codecs.encodeNode(kkStoreConfig.getProductFolder());

        if (jcrSession.itemExists(productRoot)) {
            seed = jcrSession.getNode(productRoot);
        }

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

        } else if (seed.isNodeType("hippostd:folder") || seed.isNodeType(KONAKART_IS_PRODUCT_MIXIN)) {
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
