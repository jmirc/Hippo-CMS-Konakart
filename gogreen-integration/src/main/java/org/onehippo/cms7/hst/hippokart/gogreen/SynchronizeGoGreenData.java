package org.onehippo.cms7.hst.hippokart.gogreen;

import com.konakartadmin.app.AdminEngineConfig;
import com.konakartadmin.app.KKAdminException;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.ws.KKAdminEngineMgr;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.onehippo.cms7.hst.hippokart.gogreen.database.CleanDatabase;
import org.onehippo.cms7.hst.hippokart.gogreen.database.InitializeDatabase;
import org.onehippo.cms7.hst.hippokart.gogreen.hippo.HippoHelper;


import java.lang.reflect.InvocationTargetException;

import static java.util.Arrays.asList;

/**
 * Synchronize the products data from Hippo to the Konakart Database.
 * The goal is to add the eCommerce feature to GoGreen
 */
public class SynchronizeGoGreenData {

    public static String sessionId = null;

    /** engClassName - Name of the engine to use */
    private static String engClassName = "com.konakartadmin.bl.KKAdmin";


    public static void main(String[] args) throws Exception {
        OptionParser parser = new OptionParser() {
            {
                accepts("env", "local, remote").withRequiredArg().ofType(String.class);
                accepts("u", "admin username").withRequiredArg().ofType(String.class);
                accepts("p", "admin password").withRequiredArg().ofType(String.class);
                accepts("repositoryUrl", "repository URL").withRequiredArg().ofType(String.class);
                accepts("repositoryUser", "repository user").withRequiredArg().ofType(String.class);
                accepts("repositoryPassword", "repository password").withRequiredArg().ofType(String.class);
                accepts("repositoryPath", "repository path").withRequiredArg().ofType(String.class);
                accepts("imagesLocationPath", "image location path").withRequiredArg().ofType(String.class);

                acceptsAll( asList( "h", "?" ), "show help" );
            }
        };

        OptionSet options = parser.parse(args);

        if (options.hasArgument("?")) {
            parser.printHelpOn(System.out);
        }

        String username = null;
        String password = null;
        String environment = null;
        String repositoryUrl = null;
        String repositoryUser = null;
        String repositoryPassword = null;
        String repositoryPath = null;
        String imagesLocationPath = null;


        if (options.hasArgument("env")) {
            environment = options.valueOf("env").toString();
        }

        if (options.hasArgument("u")) {
            username = options.valueOf("u").toString();
        }

        if (options.hasArgument("p")) {
            password = options.valueOf("p").toString();
        }

        if (options.hasArgument("repositoryUrl")) {
            repositoryUrl = options.valueOf("repositoryUrl").toString();
        }

        if (options.hasArgument("repositoryUser")) {
            repositoryUser = options.valueOf("repositoryUser").toString();
        }

        if (options.hasArgument("repositoryPassword")) {
            repositoryPassword = options.valueOf("repositoryPassword").toString();
        }

        if (options.hasArgument("repositoryPath")) {
            repositoryPath = options.valueOf("repositoryPath").toString();
        }

        if (options.hasArgument("imagesLocationPath")) {
            imagesLocationPath = options.valueOf("imagesLocationPath").toString();
        }

        String propertiesFile = null;

        if (environment != null) {
            propertiesFile = "konakartadmin-" + environment + ".properties";
        }

        Class.forName("com.mysql.jdbc.Driver");


        // Initialize the database
        AdminMgrFactory adminMgrFactory = createAdminMgrFactory(username, password, "store1", propertiesFile);

        // Clean database
        CleanDatabase.execute();

        // Load data
        InitializeDatabase.execute(adminMgrFactory);

        HippoHelper hippoHelper = new HippoHelper(repositoryUrl, repositoryUser, repositoryPassword);

        // Initialize
        hippoHelper.initialize();

        // Synchronize products
        hippoHelper.startProductSynchro(imagesLocationPath);

        // Synchronize reviews
        hippoHelper.startReviewSynchro();
    }

    public static AdminMgrFactory createAdminMgrFactory(String username, String password, String storeName, String propertiesFile) throws KKAdminException, InvocationTargetException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        /*
        * Instantiate a KonaKart Admin Engine instance by name
        */
        KKAdminEngineMgr kkAdminEngMgr = new KKAdminEngineMgr();
        AdminEngineConfig adEngConf = new AdminEngineConfig();
        adEngConf.setMode(0); //
        adEngConf.setStoreId(storeName); //
        adEngConf.setPropertiesFileName(propertiesFile);

        /*
        * This creates a KonaKart Admin Engine by name using the constructor that requires an
        * AdminEngineConfig object. This is the recommended approach.
        */
        KKAdminIf eng = kkAdminEngMgr.getKKAdminByName(engClassName, adEngConf);

        sessionId = eng.login(username, password);

        return new AdminMgrFactory(eng);
    }

}
