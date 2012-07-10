package org.onehippo.forge.konakart.cms.valuelistprovider;

import com.konakartadmin.app.AdminStore;
import com.konakartadmin.blif.AdminMultiStoreMgrIf;
import com.konakartadmin.blif.AdminStoreMgrIf;
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
        ValueList valueList = new ValueList();

        try {
            AdminStore[] adminStores = new AdminStore[0];

            if (!KKAdminEngine.getInstance().isEnterprise()) {
                AdminStoreMgrIf adminStoreMgr = KKAdminEngine.getInstance().getFactory().getAdminCommunityStoreMgr(true);
                adminStores = adminStoreMgr.getStores("");
            } else if (!KKAdminEngine.getInstance().isMultiStore()) {
                AdminStoreMgrIf adminStoreMgr = KKAdminEngine.getInstance().getFactory().getAdminStoreMgr(true);
                adminStores = adminStoreMgr.getStores("");
            } else {
                AdminMultiStoreMgrIf adminStoreMgr = KKAdminEngine.getInstance().getFactory().getAdminMultiStoreMgr(true);
                adminStores = adminStoreMgr.getStores("");
            }

            for (AdminStore adminStore : adminStores) {
                valueList.add(new ListItem(adminStore.getStoreId(), adminStore.getStoreName()));
            }
        } catch (Exception e) {
            log.error("Failed to retrieve the list of stores", e);
        }

        return valueList;
    }

    @Override
    public List<String> getValueListNames() {
        return null;
    }
}
