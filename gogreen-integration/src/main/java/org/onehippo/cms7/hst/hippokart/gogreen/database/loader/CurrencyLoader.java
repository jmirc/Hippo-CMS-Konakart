package org.onehippo.cms7.hst.hippokart.gogreen.database.loader;

import com.konakart.bl.KKCriteria;
import com.konakart.om.BaseCurrenciesPeer;
import com.konakartadmin.app.AdminCurrency;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminCurrencyMgrIf;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;

public class CurrencyLoader extends BaseLoader {
    public CurrencyLoader(final AdminMgrFactory adminMgrFactory, final String filePath) throws Exception {
        super(adminMgrFactory, filePath);
    }

    @Override
    protected void processRow(String[] csvLine) throws Exception {

        AdminCurrencyMgrIf currencyMgrIf = adminMgrFactory.getAdminCurrMgr(true);

        AdminCurrency adminCurrency = new AdminCurrency();

        adminCurrency.setTitle(csvLine[0]);
        adminCurrency.setCode(csvLine[1]);
        adminCurrency.setSymbolLeft(csvLine[2]);
        adminCurrency.setSymbolRight(csvLine[3]);
        adminCurrency.setDecimalPoint(csvLine[4]);
        adminCurrency.setThousandsPoint(csvLine[5]);
        adminCurrency.setDecimalPlaces(csvLine[6]);
        adminCurrency.setValue(new BigDecimal(csvLine[7]));
        adminCurrency.setSetAsDefault(Boolean.valueOf(csvLine[8]));

        currencyMgrIf.insertCurrency(adminCurrency);

        System.out.println("Currency -" + adminCurrency.getCode() + "- has been added");


    }
}
