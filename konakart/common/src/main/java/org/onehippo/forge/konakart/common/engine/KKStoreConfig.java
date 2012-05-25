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

    public static final Logger log = LoggerFactory.getLogger(KKStoreConfig.class);

    private String nodePath;
    private String contentRoot;
    private String galleryRoot;
    private String productFolder;
    private String reviewFolder;
    private String language;
    private Date lastUpdatedTimeKonakartToRepository = null;
    private Date lastUpdatedTimeRepositoryToKonakart = null;
    private String catalogId;
    private String storeId;
    private String cronExpression;
    private Boolean enabled;
    private boolean initialized;
    private String jobClass;
    private String locale;
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


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getLastUpdatedTimeKonakartToRepository() {
        return lastUpdatedTimeKonakartToRepository;
    }

    public void setLastUpdatedTimeKonakartToRepository(Date lastUpdatedTimeKonakartToRepository) {
        this.lastUpdatedTimeKonakartToRepository = lastUpdatedTimeKonakartToRepository;
    }

    public void updateLastUpdatedTimeKonakartToRepository(Session session) {
        try {
            Node node = session.getNode(getNodePath());

            GregorianCalendar currentTime = new GregorianCalendar();

            lastUpdatedTimeKonakartToRepository = currentTime.getTime();

            node.setProperty(HippoModuleConfig.LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY, currentTime);

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
            Node node = session.getNode(getNodePath());

            GregorianCalendar currentTime = new GregorianCalendar();

            lastUpdatedTimeRepositoryToKonakart = currentTime.getTime();

            node.setProperty(HippoModuleConfig.LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART, currentTime);

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

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getProductFactoryClassName() {
        return productFactoryClassName;
    }

    public void setProductFactoryClassName(String productFactoryClassName) {
        this.productFactoryClassName = productFactoryClassName;
    }
}
