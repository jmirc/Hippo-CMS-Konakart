package org.onehippo.forge.konakart.cms.perspective.panels;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.InlineFrame;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.hippoecm.frontend.plugins.standards.panelperspective.breadcrumb.PanelPluginBreadCrumbPanel;

import java.io.Serializable;

public class KonakartAdminPanel extends PanelPluginBreadCrumbPanel {

  public KonakartAdminPanel(String id, IBreadCrumbModel breadCrumbModel, final String konakartUrl) {
    super(id, breadCrumbModel);

    add(new WebMarkupContainer("konakartAdmin") {

      /**
       * Handles this frame's tag.
       *
       * @param tag
       *            the component tag
       * @see org.apache.wicket.Component#onComponentTag(ComponentTag)
       */
      @Override
      protected final void onComponentTag(final ComponentTag tag) {
        checkComponentTag(tag, "iframe");

        // generate the src attribute
        tag.put("src", konakartUrl);

        super.onComponentTag(tag);
      }
    });

  }

  @Override
  public IModel<String> getTitle(Component component) {
    return new ResourceModel("konakart-admin-info-title");
  }


}
