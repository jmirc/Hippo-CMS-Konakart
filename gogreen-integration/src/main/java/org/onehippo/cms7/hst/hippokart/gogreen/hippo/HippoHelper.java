package org.onehippo.cms7.hst.hippokart.gogreen.hippo;

import org.onehippo.forge.jcrrunner.JcrHelper;
import org.onehippo.forge.jcrrunner.Runner;
import org.onehippo.forge.jcrrunner.RunnerConfig;

import javax.jcr.RepositoryException;
import java.util.Properties;

/**
 * Helper class used to retrieve a connection to the repository
 */
public class HippoHelper {

    private String repositoryUrl;
    private String repositoryUser;
    private String repositoryPassword;

    public HippoHelper(String repositoryUrl, String repositoryUser, String repositoryPassword) {

        this.repositoryUrl = repositoryUrl;
        this.repositoryUser = repositoryUser;
        this.repositoryPassword = repositoryPassword;
    }


    public void initialize() {
        // register hook for proper shutdown
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        JcrHelper.setServerUrl(repositoryUrl);
        JcrHelper.setUsername(repositoryUser);
        JcrHelper.setPassword(repositoryPassword);
        JcrHelper.connect();
    }


    public void startProductSynchro(String imagesLocationPath) throws RepositoryException {

        Properties properties = new Properties();
        properties.setProperty("plugins.java.createproductsplugin.class", "org.onehippo.cms7.hst.hippokart.gogreen.hippo.plugins.CreateProductsPlugin");
        properties.setProperty("plugins.java.createproductsplugin.imagesLocationPath", imagesLocationPath);
        properties.setProperty("repository.url", repositoryUrl);
        properties.setProperty("repository.user", repositoryUser);
        properties.setProperty("repository.pass", repositoryPassword);
        properties.setProperty("repository.path", "/content/documents/hippogogreen/products/**");

        RunnerConfig config = new RunnerConfig(properties);

        // Synchro the english site
        Runner runner = new Runner();
        runner.registerPlugins(config.getPluginConfigs());
        runner.setPath(config.getRepositoryPath());
        runner.start();
    }

    public void startReviewSynchro() throws RepositoryException {

        Properties properties = new Properties();
        properties.setProperty("plugins.java.createproductsplugin.class", "org.onehippo.cms7.hst.hippokart.gogreen.hippo.plugins.CreateReviewPlugin");
        properties.setProperty("repository.url", repositoryUrl);
        properties.setProperty("repository.user", repositoryUser);
        properties.setProperty("repository.pass", repositoryPassword);
        properties.setProperty("repository.path", "/content/documents/hippogogreen/reviews/**");

        RunnerConfig config = new RunnerConfig(properties);

        // Synchro the english site
        Runner runner = new Runner();
        runner.registerPlugins(config.getPluginConfigs());
        runner.setPath(config.getRepositoryPath());
        runner.start();
    }

    /**
     * Trivial shutdown hook class.
     */
    static class ShutdownHook extends Thread {
        /**
         * Exit properly on shutdown.
         */
        public void run() {
            JcrHelper.disconnect();
        }
    }
}
