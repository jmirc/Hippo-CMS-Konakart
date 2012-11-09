package org.onehippo.forge.konakart.hst.tags;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.tag.HstLinkTag;
import org.hippoecm.hst.util.HstRequestUtils;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class ProductLinkTag extends HstLinkTag {

    private Logger log = LoggerFactory.getLogger(ProductLinkTag.class);

    private int productId;

    public void setProductId(int productId) {
        this.productId = productId;
    }

    @Override
    public int doStartTag() throws JspException {

        try {
            HttpServletRequest servletRequest = (HttpServletRequest) pageContext.getRequest();
            HstRequest hstRequest = HstRequestUtils.getHstRequest(servletRequest);
            HstRequestContext requestContext = hstRequest.getRequestContext();

            HstQueryManager queryManager = KKUtil.getQueryManager(requestContext);

            HippoBean scope = KKUtil.getSiteContentBaseBean(hstRequest);

            HstQuery hstQuery = queryManager.createQuery(scope, KKProductDocument.class);
            Filter filter = hstQuery.createFilter();
            filter.addEqualTo(KKCndConstants.PRODUCT_ID, (long) productId);

            hstQuery.setFilter(filter);

            HstQueryResult queryResult = hstQuery.execute();

            // No result
            if (queryResult.getTotalSize() != 0) {
                if (linkForAttributeSet) {
                    log.warn("Incorrect usage of hst:link tag. Not allowed to specifcy two of the attributes 'link', 'hippobean', 'path' or 'siteMapItemRefId' at same time. Ignore the attr hippoBean '{}'", identifiableContentBean.getIdentifier());
                } else {
                    linkForAttributeSet = true;
                    HippoBean hippoBean1 = queryResult.getHippoBeans().next();

                    this.link = requestContext.getHstLinkCreator().create(
                            hippoBean1.getNode(), requestContext, preferSiteMapItem, fallback, navigationStateful);

                }
            }
        } catch (QueryException e) {
            log.error("Failed to find the Hippo product document for the productId {} - {}", productId, e.toString());
        }


        return super.doStartTag();


    }

    /**
     * TagExtraInfo class for HstURLTag.
     */
    public static class TEI extends TagExtraInfo {

        public VariableInfo[] getVariableInfo(TagData tagData) {
            VariableInfo vi[] = null;
            String var = tagData.getAttributeString("var");
            if (var != null) {
                vi = new VariableInfo[1];
                vi[0] =
                        new VariableInfo(var, "java.lang.String", true,
                                VariableInfo.AT_BEGIN);
            }
            return vi;
        }

    }


}
