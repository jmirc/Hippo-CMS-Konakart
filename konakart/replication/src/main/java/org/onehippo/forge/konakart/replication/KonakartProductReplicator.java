package org.onehippo.forge.konakart.replication;


import com.konakart.app.*;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.LanguageIf;
import com.konakart.appif.ProductSearchIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.bl.CustomProductMgr;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKEngineConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.replication.config.HippoKonakartMapping;
import org.onehippo.forge.konakart.replication.config.HippoRepoConfig;
import org.onehippo.forge.konakart.replication.factory.DefaultProductFactory;
import org.onehippo.forge.konakart.replication.factory.ProductFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class KonakartProductReplicator {

    private static Logger log = LoggerFactory.getLogger(KonakartProductReplicator.class);

    private javax.jcr.Session jcrSession;
    private HippoRepoConfig hippoRepoConfig;
    private String productFactoryClassName;

    /**
     * the hippo document type which defined a product. This document must contain the konakart:konakart compound
     */
    private String productDocType;

    /**
     * The name of the property associated to the konakart:konaker document.
     */
    private String konakartProductPropertyName;

    /**
     * @param jcrSession the JCR session
     */
    public void setJcrSession(javax.jcr.Session jcrSession) {
        this.jcrSession = jcrSession;
    }

    /**
     * @param hippoRepoConfig some hippo configuration
     */
    public void setHippoRepoConfig(HippoRepoConfig hippoRepoConfig) {
        this.hippoRepoConfig = hippoRepoConfig;
    }

    /**
     * By default the DefaultProductFactory will be used if no factory is set
     *
     * @param productFactoryClassName the product factory class name.
     */
    public void setProductFactory(String productFactoryClassName) {
        this.productFactoryClassName = productFactoryClassName;
    }

    /**
     * @param productDocType the product document type
     */
    public void setProductDocType(String productDocType) {
        this.productDocType = productDocType;
    }

    /**
     * @param konakartProductPropertyName .
     */
    public void setKonakartProductPropertyName(String konakartProductPropertyName) {
        this.konakartProductPropertyName = konakartProductPropertyName;
    }

    /**
     * Start the replication
     */
    public void execute() {
        log.debug("Executing Konakart Products Replicator ...");

        // load the konakart module config.
        HippoModuleConfig config = HippoModuleConfig.load(jcrSession);

        if (!config.isIntialized()) {
            log.warn("Failed to read the configuration from Konakart config module.");
        }

        if (!config.isEnabled()) {
            log.warn("The Konakart replicator is disabled. No replication will be operated.");
            return;
        }

        try {
            boolean isUpdated = updateKonakartProductsToRepository(config);

            if (isUpdated) {
                config.setLastUpdatedTimeToNow(jcrSession);
            }
        } catch (Exception e) {
            log.warn("Failed to update Konakart products to Repository. ", e);
        }
    }

    /**
     * Copy products from Konakart to Hippo
     *
     * @param config the config which contains the Konakart configuration
     * @return true if the product has been updated, false otherwise
     * @throws Exception an exception
     */
    private boolean updateKonakartProductsToRepository(HippoModuleConfig config) throws Exception {

        boolean isUpdated = false;

        KKEngineConfig engineConfig = config.getEngineConfig();
        KKEngine kkengine = new KKEngine(engineConfig);

        // login
        kkengine.getCustomerMgr().login(engineConfig.getUsername(), engineConfig.getPassword());

        // Retrieve the product factory
        CustomProductMgr productMgr = new CustomProductMgr(kkengine.getEngine(), config.getLastUpdatedTime());

        // Get only visible products
        DataDescriptorIf dataDescriptorIf = new DataDescriptor();
        dataDescriptorIf.setShowInvisible(false);
        dataDescriptorIf.setFillDescription(true);

        // Search products from all stores
        ProductSearchIf productSearchIf = new ProductSearch();
        productSearchIf.setSearchAllStores(true);

        // Used default products options
        FetchProductOptions fetchProductOptions = new FetchProductOptions();

        // Retrieve the list of languages defined into konakart.
        LanguageIf[] languages = kkengine.getEngine().getAllLanguages();



        // For each language defined into Konakart we need to add the product under Hippo
        for (LanguageIf language : languages) {

            List<HippoKonakartMapping.MappingByProductType> mapping = hippoRepoConfig.getMapping(language.getLocale()).getByProductTypeList();

            // Synchronized by product types.
            for (HippoKonakartMapping.MappingByProductType mappingByProductType : mapping) {

                String productTypeName = KKCndConstants.PRODUCT_TYPE.ALL.getName();
                
                // Retrieve the list of products by product's type
                if (mappingByProductType.isProductTypeSet()) {
                    productSearchIf.setProductType(mappingByProductType.getProductType());
                    productTypeName = KKCndConstants.PRODUCT_TYPE.findByType(mappingByProductType.getProductType()).getName();
                }

                // Search
                Products products = productMgr.searchForProductsWithOptions(kkengine.getSessionId(), dataDescriptorIf,
                        productSearchIf, language.getId(), fetchProductOptions);

                // Get the Hippo content root
                String contentRoot = mappingByProductType.getHippoContentRoot();

                if (contentRoot == null) {
                    continue;
                }

                if (!isUpdated && products.getProductArray().length > 0) {
                    isUpdated = true;
                }

                // Insert products into konakart
                for (Product product : products.getProductArray()) {
                    ProductFactory productFactory = createProductFactory();

                    if (productFactory == null) {
                        continue;
                    }

                    productFactory.setSession(jcrSession);
                    productFactory.setContentRoot(contentRoot);
                    productFactory.setProductDocType(productDocType);
                    productFactory.setKKProductTypeName(productTypeName);
                    productFactory.setKonakartProductPropertyName(konakartProductPropertyName);

                    // Create the reviews' folder if not exists
                    String reviewName = productFactory.createReviewFolder(mappingByProductType.getReviewFolder());

                    // Create the product
                    String uuid = productFactory.add(product, language);

                    // Set the Hippo Node UUID
                    productMgr.synchronizeHippoKK(product.getId(), uuid, reviewName);
                }
            }
        }

        // Logout.
        kkengine.getCustomerMgr().logout();

        return isUpdated;
    }

    private ProductFactory createProductFactory() {
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
}
