package org.onehippo.forge.konakart.cms.valuelistprovider;

import com.konakartadmin.app.AdminStore;
import com.konakartadmin.app.KKAdminException;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.selection.frontend.model.ListItem;
import org.onehippo.forge.selection.frontend.model.ValueList;
import org.onehippo.forge.selection.frontend.provider.IValueListProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

public class StoreIdProvider extends Plugin implements IValueListProvider {

    static final Logger log = LoggerFactory.getLogger(StoreIdProvider.class);

    public StoreIdProvider(IPluginContext context, IPluginConfig config) {
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
        if (!"values".equals(name)) {
            log.warn("unknown value list name " + name + " was requested, using 'values'");
        }

        ValueList valueList = new ValueList();

        try {
            AdminStore[] adminStores = KKAdminEngine.getInstance().getEngine().getStores();

            for (AdminStore adminStore : adminStores) {
                valueList.add(new ListItem(adminStore.getStoreId(), adminStore.getStoreName()));
            }

        } catch (KKAdminException e) {
            log.error("Failed to retrieve the list of stores", e);
        }

        return valueList;
    }

    @Override
    public List<String> getValueListNames() {
        return null;
    }
}
