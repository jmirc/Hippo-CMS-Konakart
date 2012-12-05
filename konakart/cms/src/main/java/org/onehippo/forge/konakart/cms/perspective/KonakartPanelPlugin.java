package org.onehippo.forge.konakart.cms.perspective;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.panelperspective.PanelPlugin;

public abstract class KonakartPanelPlugin extends PanelPlugin {

  public static final String KONAKART_PANEL_SERVICE_ID = "konakart.panel";

  public KonakartPanelPlugin(IPluginContext context, IPluginConfig config) {
    super(context, config);
  }

  @Override
  public String getPanelServiceId() {
    return KONAKART_PANEL_SERVICE_ID;
  }
}
