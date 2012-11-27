package org.onehippo.cms7.hst.hippokart.gogreen.database;

import com.konakartadmin.bl.AdminMgrFactory;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ProductHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.ReviewHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.helper.TaxClassesHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.loader.*;

/**
 *
 */
public class InitializeDatabase {

    public static void execute(AdminMgrFactory adminMgrFactory) throws Exception {

        // Load Languages
        new LanguageLoader(adminMgrFactory, "data/english/languages.csv").process();

        // Load Categories
        new CategoryLoader(adminMgrFactory, "data/english/categories.csv", 0).process();

        // Load Categories
        new ManufacturerLoader(adminMgrFactory, "data/manufacturers.csv").process();

        // Load Currencies
        new CurrencyLoader(adminMgrFactory, "data/currencies.csv").process();

        // Create default tax class
        TaxClassesHelper.createNoTaxClass(adminMgrFactory);

        // Initialize Products helper
        ProductHelper.setAdminMgrFactory(adminMgrFactory);

        // Initialize Reviews helper
        ReviewHelper.setAdminMgrFactory(adminMgrFactory);




    }
}
