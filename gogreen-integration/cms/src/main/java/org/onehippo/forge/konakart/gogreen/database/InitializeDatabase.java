package org.onehippo.forge.konakart.gogreen.database;

import com.konakartadmin.bl.AdminMgrFactory;
import org.onehippo.forge.konakart.gogreen.database.helper.ProductHelper;
import org.onehippo.forge.konakart.gogreen.database.helper.ReviewHelper;
import org.onehippo.forge.konakart.gogreen.database.helper.TaxClassesHelper;
import org.onehippo.forge.konakart.gogreen.database.loader.CategoryLoader;
import org.onehippo.forge.konakart.gogreen.database.loader.LanguageLoader;
import org.onehippo.forge.konakart.gogreen.database.loader.ManufacturerLoader;
import org.onehippo.forge.konakart.gogreen.database.loader.OrderStatusLoader;

/**
 *
 */
public class InitializeDatabase {

  public static void execute(AdminMgrFactory adminMgrFactory) throws Exception {

    // Load Languages
    new LanguageLoader(adminMgrFactory, "data/languages.csv").process();

    // Load Categories
    new CategoryLoader(adminMgrFactory, "data/categories.csv", 0).process();

    // Load Categories
    new ManufacturerLoader(adminMgrFactory, "data/manufacturers.csv").process();

    // Load Order Status
    new OrderStatusLoader(adminMgrFactory, "data/orders_status.csv").process();

    // Create default tax class
    TaxClassesHelper.createNoTaxClass(adminMgrFactory);

    // Initialize Products helper
    ProductHelper.setAdminMgrFactory(adminMgrFactory);

    // Initialize Reviews helper
    ReviewHelper.setAdminMgrFactory(adminMgrFactory);
  }
}
