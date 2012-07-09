package org.onehippo.forge.konakart.cms.perspective.panels;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugins.standards.panelperspective.breadcrumb.PanelPluginBreadCrumbPanel;

public class KonakartAdminPanel extends PanelPluginBreadCrumbPanel {

    public KonakartAdminPanel(String id, IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        add(CSSPackageResource.getHeaderContribution(KonakartAdminPanel.class, "KonakartAdmin.css"));
    }



    @Override
    public IModel<String> getTitle(Component component) {
        return new ResourceModel("konakart-admin-info-title");
    }
}
