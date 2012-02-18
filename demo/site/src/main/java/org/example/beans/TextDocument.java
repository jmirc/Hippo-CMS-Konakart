package org.example.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;

@Node(jcrType="myhippoproject:textdocument")
public class TextDocument extends BaseDocument{
    
    public HippoHtml getHtml(){
        return getHippoHtml("myhippoproject:body");    
    }

    public String getSummary() {
        return getProperty("myhippoproject:summary");
    }
 
    public String getTitle() {
        return getProperty("myhippoproject:title");
    }

}
