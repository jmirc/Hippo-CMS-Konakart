package org.onehippo.forge.konakart.cms.perspective;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.panelperspective.breadcrumb.PanelPluginBreadCrumbPanel;
import org.onehippo.forge.konakart.cms.perspective.panels.KonakartAdminPanel;

public class KonakartAdminPanelPlugin extends KonakartPanelPlugin {

    public KonakartAdminPanelPlugin(IPluginContext context, IPluginConfig config) {
        super(context, config);
    }

    @Override
    public ResourceReference getImage() {
        return new ResourceReference(getClass(), "konakart-perspective-32.png");
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("konakart-admin");
    }

    @Override
    public IModel<String> getHelp() {
        return new ResourceModel("konakart-admin-title-help");
    }

    @Override
    public PanelPluginBreadCrumbPanel create(String componentId, IBreadCrumbModel breadCrumbModel) {

        String konakartUrl = getPluginConfig().getString("konakart-admin-url");

        return new KonakartAdminPanel(componentId, breadCrumbModel, konakartUrl);
    }
}
