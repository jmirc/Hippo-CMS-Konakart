package org.onehippo.cms7.hst.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@Node(jcrType="gettingstarted:textdocument")
public class TextDocument extends BaseDocument{
    
    public String getTitle() {
        return getProperty("gettingstarted:title");
    }

    public String getSummary() {
        return getProperty("gettingstarted:summary");
    }
    
    public HippoHtml getHtml(){
        return getHippoHtml("gettingstarted:body");    
    }

}
