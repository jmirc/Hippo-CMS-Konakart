package org.onehippo.cms7.hst.hippokart.dbupdate;

import com.konakart.bl.KKCriteria;
import com.konakart.om.BaseProductsPeer;
import com.konakartadmin.app.*;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminProductMgrIf;
import com.konakartadmin.ws.KKAdminEngineMgr;
import com.workingdogs.village.Record;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang.StringUtils;
import org.apache.torque.util.BasePeer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static java.util.Arrays.asList;


public class UpdateImages {

    public static String sessionId = null;

    /** engClassName - Name of the engine to use */
    private static String engClassName = "com.konakartadmin.bl.KKAdmin";

    public static void main(String[] args) throws Exception, IOException, InvocationTargetException, KKAdminException, InstantiationException, IllegalAccessException {


        OptionParser parser = new OptionParser() {
            {
                accepts("env", "local, remote").withRequiredArg().ofType(String.class);
                accepts("u", "admin username").withRequiredArg().ofType(String.class);
                accepts("p", "admin password").withRequiredArg().ofType(String.class);
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


        if (options.hasArgument("env")) {
            environment = options.valueOf("env").toString();
        }

        if (options.hasArgument("u")) {
            username = options.valueOf("u").toString();
        }

        if (options.hasArgument("p")) {
            password = options.valueOf("p").toString();
        }

        String propertiesFile = null;

        if (environment != null) {
            propertiesFile = "konakartadmin-" + environment + ".properties";
        }

        Class.forName("com.mysql.jdbc.Driver");

        // Initialize the database
        createAdminMgrFactory(username, password, "store1", propertiesFile);


        KKCriteria kkCriteria = new KKCriteria();


        kkCriteria.addSelectColumn(BaseProductsPeer.PRODUCTS_ID);
        kkCriteria.addSelectColumn(BaseProductsPeer.PRODUCTS_IMAGE);

        List<Record> results = BasePeer.doSelect(kkCriteria);

        for (Record result : results) {
            int productId = result.getValue(1).asInt();

            String imageName = result.getValue(2).asString();

            String imagePattern = StringUtils.substringBefore(imageName, ".");
            String ext = StringUtils.substringAfter(imageName, ".");

            String imageName0 = imagePattern + "_big." + ext;
            String imageName1 = imagePattern + "_1_big." + ext;
            String imageName2 = imagePattern + "_2_big." + ext;
            String imageName3 = imagePattern + "_3_big." + ext;

            kkCriteria = new KKCriteria();
            kkCriteria.addSelectColumn(BaseProductsPeer.PRODUCTS_ID);
            kkCriteria.add(BaseProductsPeer.PRODUCTS_ID, productId);
            kkCriteria.addForInsert(BaseProductsPeer.PRODUCTS_IMAGE, imageName0);
            kkCriteria.addForInsert(BaseProductsPeer.PRODUCTS_IMAGE2, imageName1);
            kkCriteria.addForInsert(BaseProductsPeer.PRODUCTS_IMAGE3, imageName2);
            kkCriteria.addForInsert(BaseProductsPeer.PRODUCTS_IMAGE4, imageName3);

            BasePeer.doUpdate(kkCriteria);

            System.out.println("Product id " + productId + " has been updated.");

        }

        
        // All done!
        System.out.println("\nAll done!"); 

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
