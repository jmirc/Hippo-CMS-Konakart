package org.onehippo.forge.konakart.cms.valuelistprovider;

import com.konakartadmin.app.AdminTaxClass;
import com.konakartadmin.app.AdminTaxClassSearch;
import com.konakartadmin.app.AdminTaxClassSearchResult;
import com.konakartadmin.blif.AdminTaxMgrIf;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;
import org.onehippo.forge.selection.frontend.provider.IValueListProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TaxClassProvider extends Plugin implements IValueListProvider {

    static final Logger log = LoggerFactory.getLogger(TaxClassProvider.class);


    public TaxClassProvider(IPluginContext context, IPluginConfig config) {
        super(context, config);

        context.registerService(this, config.getString(IValueListProvider.SERVICE));

        if (log.isDebugEnabled()) {
            log.debug(this.getClass().getName() + " registered under " + IValueListProvider.SERVICE);
        }
    }

    @Override
    public ValueList getValueList(IPluginConfig config) {
        return getValueList(config.getString("source", "values"));
    }

    @Override
    public ValueList getValueList(String name) {
        return getValueList(name, null/*locale*/);
    }

    @Override
    public ValueList getValueList(String name, Locale locale) {

        ValueList valueList = new ValueList();

        AdminTaxClassSearch adminTaxClassSearch = new AdminTaxClassSearch();

        try {
            AdminTaxMgrIf adminTaxMgr = KKAdminEngine.getInstance().getFactory().getAdminTaxMgr(true);
            AdminTaxClassSearchResult result = adminTaxMgr.getTaxClasses(adminTaxClassSearch);
            AdminTaxClass[] taxes = result.getTaxClasses();


            for (AdminTaxClass tax : taxes) {
                valueList.add(new ListItem(String.valueOf(tax.getTaxClassId()), tax.getTaxClassTitle()));
            }
        } catch (Exception e) {
            log.error("Failed to retrieve the list of taxes", e);
        }

        return valueList;
    }

    @Override
    public List<String> getValueListNames() {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add("values");
        return list;
    }
}
