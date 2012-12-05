package org.onehippo.forge.konakart.hst.tags;

import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.tag.HstTagSupport;
import org.hippoecm.hst.util.HstRequestUtils;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

public class ProductDocumentTag extends HstTagSupport {

  private Logger log = LoggerFactory.getLogger(ProductDocumentTag.class);

  private int productId;
  private String var;

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setVar(String var) {
    this.var = var;
  }

  /* (non-Javadoc)
  * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
  */
  @Override
  public int doStartTag() throws JspException {
    if (var != null) {
      pageContext.removeAttribute(var, PageContext.PAGE_SCOPE);
    }

    return EVAL_BODY_INCLUDE;
  }

  @Override
  public int doEndTag() throws JspException {

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
        HippoBean hippoBean1 = queryResult.getHippoBeans().next();

        int varScope = PageContext.PAGE_SCOPE;
        pageContext.setAttribute(var, hippoBean1, varScope);
      }
    } catch (QueryException e) {
      log.error("Failed to find the Hippo product document for the productId {} - {}", productId, e.toString());
    }


    return super.doStartTag();


  }
}
