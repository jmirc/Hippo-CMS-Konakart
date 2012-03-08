package org.onehippo.forge.konakart.hst.channel;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.Parameter;

public interface KonakartSiteInfo extends ChannelInfo {

    @Parameter(name = "headerName", defaultValue = "HST Website")
    String getHeaderName();

    @Parameter(name = "mode", defaultValue = "0")
    String getEngineMode();

    @Parameter(name = "storeId", defaultValue = "store1")
    String getStoreId();

    @Parameter(name = "catalogId", defaultValue = "store1")
    String getCatalogId();

    @Parameter(name = "customersShared", defaultValue = "false")
    Boolean isCustomersShared();

    @Parameter(name = "productsShared", defaultValue = "false")
    Boolean isProductsShared();
}
