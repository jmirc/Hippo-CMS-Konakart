package org.onehippo.forge.konakart.cms.replication.synchronization.job;


import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.onehippo.forge.konakart.cms.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.cms.replication.synchronization.KonakartResourceScheduler;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collection;

public class KonakartSyncJob implements Job {

    private static Logger log = LoggerFactory.getLogger(KonakartSyncJob.class);

    private Session jcrSession;

    /**
     * Start the replication
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("Executing Konakart Products Replicator ...");

        // Set the JcrSession
        KonakartResourceScheduler scheduler = (KonakartResourceScheduler) context.getScheduler();
        jcrSession = ((JCRSchedulingContext) scheduler.getCtx()).getSession();

        // Load the gallery processor service
        GalleryProcesssorConfig.load(jcrSession);

        try {
                // Initialize the Konakart engine
            KKEngine.init(jcrSession);
            KKAdminEngine.init(jcrSession);

            // Synchronize all stores
            Collection<KKStoreConfig> kkStoreConfigs = HippoModuleConfig.getConfig().getStoresConfig().values();

            for (KKStoreConfig kkStoreConfig : kkStoreConfigs) {
                if ((kkStoreConfig == null) || !kkStoreConfig.isInitialized()) {
                    log.error("The Konakart synchronization service has not be initialized. Please check the log.");
                    continue;
                }

                try {
                    // Synchronize konakart information
                    syncKonakartToHippo(kkStoreConfig);
                } catch (Exception e) {
                    log.warn("Failed to update Repository to Konakart. ", e);
                }

                // Synchronize hippo product
                try {
                    syncHippoToKonakart(kkStoreConfig);
                } catch (Exception e) {
                    log.warn("Failed to update Konakart to Repository. ", e);
                }
            }
        } catch (Exception e) {
            log.warn("Failed to initialize Konakart engine. ", e);
        }
    }

    /**
     * Synchronize Konakart information to Hippo
     *
     * @param kkStoreConfig the store config
     * @throws Exception an exception
     */
    protected void syncKonakartToHippo(KKStoreConfig kkStoreConfig) throws Exception {

        // Synchronize products
        if (KonakartSyncProducts.updateKonakartToHippo(kkStoreConfig, jcrSession)) {
            kkStoreConfig.updateLastUpdatedTimeKonakartToRepository(jcrSession);
        }
    }

    /**
     * Synchronize Konakart information to Hippo
     *
     * @param kkStoreConfig the store config
     * @throws Exception an exception
     */
    protected void syncHippoToKonakart(KKStoreConfig kkStoreConfig) throws Exception {

        // Synchronize products
        KonakartSyncProducts.updateHippoToKonakart(kkStoreConfig, jcrSession);
        kkStoreConfig.updateLastUpdatedTimeRepositoryToKonakart(jcrSession);
    }
}
