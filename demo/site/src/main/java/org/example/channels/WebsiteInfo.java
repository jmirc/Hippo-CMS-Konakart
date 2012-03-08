package org.example.channels;

import org.hippoecm.hst.configuration.channel.ChannelInfo;
import org.hippoecm.hst.core.parameters.FieldGroup;
import org.hippoecm.hst.core.parameters.FieldGroupList;
import org.hippoecm.hst.core.parameters.Parameter;
import org.onehippo.forge.konakart.hst.channel.KonakartSiteInfo;

/**
 * Retrieves the properties of a website channel.
 */
@FieldGroupList({
        @FieldGroup(
                titleKey = "fields.website",
                value = { "headerName" }
        ),
        @FieldGroup(
                titleKey = "fields.konakart",
                value = { "mode", "storeId", "customersShared", "productsShared" }
        )
})
public interface WebsiteInfo extends KonakartSiteInfo {

    @Parameter(name = "headerName", defaultValue = "HST Website")
    String getHeaderName();
}