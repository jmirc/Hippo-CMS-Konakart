package org.onehippo.forge.konakart.hst.componentsinfo;

import org.hippoecm.hst.core.parameters.Parameter;

public interface KKProductsOverviewInfo {

    @Parameter(name = "pageSize", displayName = "Page Size", defaultValue="6")
    int getPageSize();

    @Parameter(name = "limit", displayName = "Limit", defaultValue="100")
    int getLimit();
}
