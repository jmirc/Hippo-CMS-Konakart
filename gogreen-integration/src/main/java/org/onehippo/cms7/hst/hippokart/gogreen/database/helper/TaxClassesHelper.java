package org.onehippo.cms7.hst.hippokart.gogreen.database.helper;

import com.konakartadmin.app.AdminTaxClass;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminTaxMgrIf;

public class TaxClassesHelper {

    public static int defaultTaxClassId;

    public static void createNoTaxClass(final AdminMgrFactory adminMgrFactory) throws Exception {

        try {
            AdminTaxMgrIf adminTaxMgr = adminMgrFactory.getAdminTaxMgr(true);

            AdminTaxClass adminTaxClass = new AdminTaxClass();
            adminTaxClass.setTaxClassTitle("No tax");
            adminTaxClass.setTaxClassDescription("No tax");
            defaultTaxClassId = adminTaxMgr.insertTaxClass(adminTaxClass);

            System.out.println("No Tax class created.");
        } catch (Exception e) {
            System.out.println("Problem creating No Tax class : " + e.getMessage());
            throw e;
        }
    }
}
