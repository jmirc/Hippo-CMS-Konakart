package org.example.beans;

import org.hippoecm.hst.content.beans.Node;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

@Node(jcrType = "myhippoproject:productdocument")
public class ProductDocument extends KKProductDocument {
    
    public String getTitle() {
        return super.getName();
    }
    
    public String getSummary() {
        return "";
    }
    
}