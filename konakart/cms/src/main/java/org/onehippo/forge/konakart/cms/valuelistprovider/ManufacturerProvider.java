package org.onehippo.forge.konakart.cms.valuelistprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;
import org.onehippo.forge.selection.frontend.provider.IValueListProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.konakartadmin.app.AdminManufacturer;
import com.konakartadmin.blif.AdminManufacturerMgrIf;

public class ManufacturerProvider extends Plugin implements IValueListProvider {

    static final Logger log = LoggerFactory.getLogger(ManufacturerProvider.class);


    public ManufacturerProvider(IPluginContext context, IPluginConfig config) {
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

        try {
            AdminManufacturerMgrIf adminManuMgr = KKAdminEngine.getInstance().getFactory().getAdminManuMgr(true);
            AdminManufacturer[] adminManufacturer =  adminManuMgr.getAllManufacturers();

            for (AdminManufacturer manufacturer : adminManufacturer) {
                valueList.add(new ListItem(String.valueOf(manufacturer.getId()), manufacturer.getName()));
            }

        } catch (Exception e) {
            log.error("Failed to retrieve the list of manufacturers", e);
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
