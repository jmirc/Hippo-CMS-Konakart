package org.onehippo.forge.konakart.cms.taxonomy;

import org.apache.wicket.markup.html.basic.Label;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.taxonomy.api.Taxonomy;
import org.onehippo.taxonomy.plugin.TaxonomyPickerPlugin;

public class CategoriesTaxonomyPickerPlugin extends TaxonomyPickerPlugin {

    public CategoriesTaxonomyPickerPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);

        // Remove the default title
        super.get("title").replaceWith(new Label("title", config.getString("caption")));

        // Synchronize the categories

    }
}
