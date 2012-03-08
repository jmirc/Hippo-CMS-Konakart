package org.onehippo.forge.konakart.common.jcr;

import org.onehippo.forge.konakart.common.engine.KKEngineConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Date;
import java.util.GregorianCalendar;

public class HippoModuleConfig {

    public static final Logger log = LoggerFactory.getLogger(HippoModuleConfig.class);

    public static final String CONFIG_NODE_PATH = "/hippo:configuration/hippo:modules/konakart/hippo:moduleconfig";

    private static HippoModuleConfig config = new HippoModuleConfig();

    private boolean intialized = false;

    private boolean enabled;
    private Date lastUpdatedTime;
    private KKEngineConfig engineConfig = new KKEngineConfig();

    /**
     * @return the config class
     */
    public static HippoModuleConfig getConfig() {
        return config;
    }

    /**
     * @return the engine config.
     */
    public KKEngineConfig getEngineConfig() {
        return engineConfig;
    }

    /**
     * @param session a JCR session
     * @return an instance of the config
     */
    public static HippoModuleConfig load(Session session) {

        if (session == null) {
            log.error("Failed to load the Konakart config. JCR Session is null");
            throw new RuntimeException("Failed to load the Konakart config. JCR Session is null");
        }

        // load configuration
        config.loadConfiguration(session);

        return config;
    }

    public boolean isIntialized() {
        return intialized;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Date getLastUpdatedTime() {
        return lastUpdatedTime;
    }


    public void setLastUpdatedTimeToNow(Session session) {
        try {
            Node node = session.getNode(CONFIG_NODE_PATH);

            node.setProperty("konakart:lastUpdatedTime", new GregorianCalendar());

            node.getSession().save();
        } catch (RepositoryException e) {
            log.error("Failed to set the upated date time: " + e.toString());
        }
    }


    private void loadConfiguration(Session session) {

        try {
            Node node = session.getNode(CONFIG_NODE_PATH);

            enabled = node.getProperty("konakart:enabled").getBoolean();

            if (node.hasProperty("konakart:lastUpdatedTime")) {
                lastUpdatedTime = node.getProperty("konakart:lastUpdatedTime").getDate().getTime();
            } else {
                lastUpdatedTime = null;
            }

            intialized = true;

        } catch (RepositoryException e) {
            log.error("Failed to load Hippo Module configuration: " + e.toString());
        }
    }
}