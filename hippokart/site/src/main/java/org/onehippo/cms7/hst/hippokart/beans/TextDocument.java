package org.onehippo.cms7.hst.hippokart.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@Node(jcrType="hippokart:textdocument")
public class TextDocument extends BaseDocument{
    
    public String getTitle() {
        return getProperty("hippokart:title");
    }

    public String getSummary() {
        return getProperty("hippokart:summary");
    }
    
    public HippoHtml getHtml(){
        return getHippoHtml("hippokart:body");    
    }

}
