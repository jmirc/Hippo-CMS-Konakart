package org.onehippo.cms7.hst.beans;

import java.util.Calendar;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoGalleryImageSetBean;

@Node(jcrType="gettingstarted:newsdocument")
public class NewsDocument extends BaseDocument{

    public String getTitle() {
        return getProperty("gettingstarted:title");
    }
    
    public String getSummary() {
        return getProperty("gettingstarted:summary");
    }
    
    public Calendar getDate() {
        return getProperty("gettingstarted:date");
    }

    public HippoHtml getHtml(){
        return getHippoHtml("gettingstarted:body");    
    }

    /**
     * Get the imageset of the newspage
     *
     * @return the imageset of the newspage
     */
    public HippoGalleryImageSetBean getImage() {
        return getLinkedBean("gettingstarted:image", HippoGalleryImageSetBean.class);
    }


}
