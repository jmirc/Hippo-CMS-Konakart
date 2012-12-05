package org.onehippo.forge.konakart.cms.perspective;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.panelperspective.PanelPluginPerspective;
import org.hippoecm.frontend.service.IconSize;

/**
 * Perspective that will show a set of Panels exposing functionality related to the Konakart Admin
 */
public class KonakartPerspective extends PanelPluginPerspective {


  public KonakartPerspective(IPluginContext context, IPluginConfig config) {
    super(context, config);
  }

  @Override
  public ResourceReference getIcon(IconSize type) {
    return new ResourceReference(KonakartPerspective.class, "konakart-perspective-" + type.getSize() + ".png");
  }

  @Override
  public String getPanelServiceId() {
    return KonakartPanelPlugin.KONAKART_PANEL_SERVICE_ID;
  }
}
