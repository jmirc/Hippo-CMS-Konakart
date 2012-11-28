package org.onehippo.cms7.hst.hippokart.gogreen.database;

import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ProductHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ReviewHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.TaxClassesHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.loader.*;

/**
 *
 */
public class InitializeDatabase {

    public static void execute(AdminMgrFactory adminMgrFactory) throws Exception {

        // Update languages
        updateLanguages(adminMgrFactory);

        // Load Categories
        new CategoryLoader(adminMgrFactory, "data/english/categories.csv", 0).process();

        // Load Categories
        new ManufacturerLoader(adminMgrFactory, "data/manufacturers.csv").process();

        // Create default tax class
        TaxClassesHelper.createNoTaxClass(adminMgrFactory);

        // Initialize Products helper
        ProductHelper.setAdminMgrFactory(adminMgrFactory);

        // Initialize Reviews helper
        ReviewHelper.setAdminMgrFactory(adminMgrFactory);
    }

    public static void updateLanguages(AdminMgrFactory adminMgrFactory) throws Exception {
        AdminLanguageMgrIf languageMgr = adminMgrFactory.getAdminLanguageMgr(false);

        AdminLanguage[] languages = languageMgr.getAllLanguages();

        for (AdminLanguage language : languages) {
            if (StringUtils.equalsIgnoreCase("en", language.getCode())) {
                language.setLocale("en_US");
                languageMgr.updateLanguage(language);
                System.out.println(language.getCode() + " language has been updated.");
            }
        }
    }

}
