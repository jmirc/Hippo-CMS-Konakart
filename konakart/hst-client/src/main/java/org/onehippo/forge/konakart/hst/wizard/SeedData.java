package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKBaseHstComponent;

import java.util.ResourceBundle;

public class SeedData {

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

    public ResourceBundle getBundle() {
        return bundle;
    }
}
