package org.onehippo.cms7.hst.hippokart.beans;

import java.util.Calendar;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSetBean;

@Node(jcrType="hippokart:newsdocument")
public class NewsDocument extends BaseDocument{

    public String getTitle() {
        return getProperty("hippokart:title");
    }
    
    public String getSummary() {
        return getProperty("hippokart:summary");
    }
    
    public Calendar getDate() {
        return getProperty("hippokart:date");
    }

    public HippoHtml getHtml(){
        return getHippoHtml("hippokart:body");    
    }

    /**
     * Get the imageset of the newspage
     *
     * @return the imageset of the newspage
     */
    public HippoGalleryImageSetBean getImage() {
        return getLinkedBean("hippokart:image", HippoGalleryImageSetBean.class);
    }


}
