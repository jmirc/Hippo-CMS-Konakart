package org.onehippo.forge.konakart.hst.tags;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.tag.HstTagSupport;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class KKTagSupport extends HstTagSupport {

    /**
     * Retrieve the Konakart App Engine.
     * @return the engine
     * @throws JspException thrown if the engine could not be retrieved
     */
    public KKAppEng getKkAppEng() throws JspException {

        HttpSession session = pageContext.getSession();
        KKAppEng kkAppEng = (KKAppEng) session.getAttribute(KKAppEng.KONAKART_KEY);

        if (kkAppEng == null) {
            throw new JspException("Failed to retrieve the Engine");
        }

        return kkAppEng;
    }
}
