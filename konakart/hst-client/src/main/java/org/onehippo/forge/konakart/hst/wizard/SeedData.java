package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKBaseHstComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class SeedData {

    public static final Logger log = LoggerFactory.getLogger(SeedData.class);

    private HstRequest request;
    private HstResponse response;
    private KKBaseHstComponent kkBaseHstComponent;
    private ResourceBundle bundle;

    public HstRequest getRequest() {
        return request;
    }

    public void setRequest(HstRequest request) {
        this.request = request;
        bundle = ResourceBundle.getBundle("messages", request.getLocale());
    }

    public HstResponse getResponse() {
        return response;
    }

    public void setResponse(HstResponse response) {
        this.response = response;
    }

    public KKBaseHstComponent getKkBaseHstComponent() {
        return kkBaseHstComponent;
    }

    public void setKkBaseHstComponent(KKBaseHstComponent kkBaseHstComponent) {
        this.kkBaseHstComponent = kkBaseHstComponent;
    }

    public String getBundleAsString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            log.warn("Failed to retrieve the message with the key " + key + " within any ressources bundles.");
            return "[" + key + "]";
        }
    }
}
