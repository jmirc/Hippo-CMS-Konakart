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

package org.onehippo.forge.konakart.common.engine;

import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Date;
import java.util.GregorianCalendar;

public class KKStoreConfig {

    public static final String KK_STORE_CONFIG = "KK_STORE_CONFIG";

    public static final Logger log = LoggerFactory.getLogger(KKStoreConfig.class);
    public static final String KONAKART_SYNC_DOC_TYPE = "konakart:sync";

    private String nodePath;
    private String contentRoot;
    private String galleryRoot;
    private String productFolder;
    private String reviewFolder;
    private Date lastUpdatedTimeKonakartToRepository = null;
    private Date lastUpdatedTimeRepositoryToKonakart = null;
    private String catalogId;
    private String storeId;
    private String cronExpression;
    private boolean initialized;
    private String jobClass;
    private String productFactoryClassName;

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }

    public String getContentRoot() {
        return contentRoot;
    }

    public void setContentRoot(String contentRoot) {
        this.contentRoot = contentRoot;
    }

    public String getGalleryRoot() {
        return galleryRoot;
    }

    public void setGalleryRoot(String galleryRoot) {
        this.galleryRoot = galleryRoot;
    }

    public String getProductFolder() {
        return productFolder;
    }

    public void setProductFolder(String productFolder) {
        this.productFolder = productFolder;
    }

    public String getReviewFolder() {
        return reviewFolder;
    }

    public void setReviewFolder(String reviewFolder) {
        this.reviewFolder = reviewFolder;
    }

    public Date getLastUpdatedTimeKonakartToRepository() {
        return lastUpdatedTimeKonakartToRepository;
    }

    public void setLastUpdatedTimeKonakartToRepository(Date lastUpdatedTimeKonakartToRepository) {
        this.lastUpdatedTimeKonakartToRepository = lastUpdatedTimeKonakartToRepository;
    }

    public void updateLastUpdatedTimeKonakartToRepository(Session session) {
        try {
            Node node = getSyncNode(session);

            GregorianCalendar currentTime = new GregorianCalendar();

            lastUpdatedTimeKonakartToRepository = currentTime.getTime();

            node.setProperty(HippoModuleConfig.SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY, currentTime);

            node.getSession().save();
        } catch (RepositoryException e) {
            log.error("Failed to set the upated date time: " + e.toString());
        }
    }


    public Date getLastUpdatedTimeRepositoryToKonakart() {
        return lastUpdatedTimeRepositoryToKonakart;
    }

    public void setLastUpdatedTimeRepositoryToKonakart(Date lastUpdatedTimeRepositoryToKonakart) {
        this.lastUpdatedTimeRepositoryToKonakart = lastUpdatedTimeRepositoryToKonakart;
    }

    public void updateLastUpdatedTimeRepositoryToKonakart(Session session) {
        try {
            Node node = getSyncNode(session);

            GregorianCalendar currentTime = new GregorianCalendar();

            lastUpdatedTimeRepositoryToKonakart = currentTime.getTime();

            node.setProperty(HippoModuleConfig.SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART, currentTime);

            node.getSession().save();
        } catch (RepositoryException e) {
            log.error("Failed to set the upated date time: " + e.toString());
        }
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getProductFactoryClassName() {
        return productFactoryClassName;
    }

    public void setProductFactoryClassName(String productFactoryClassName) {
        this.productFactoryClassName = productFactoryClassName;
    }

    private Node getSyncNode(Session session) throws RepositoryException {
        Node node = session.getNode(getNodePath());

        Node syncNode;
        // Create it
        if (!node.hasNode(KONAKART_SYNC_DOC_TYPE)) {
            syncNode = node.addNode(KONAKART_SYNC_DOC_TYPE, KONAKART_SYNC_DOC_TYPE);
        } else {
            syncNode = node.getNode(KONAKART_SYNC_DOC_TYPE);
        }
        return syncNode;
    }

}
