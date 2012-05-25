package org.onehippo.forge.konakart.hst.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;

public class KKProductTag extends KKTagSupport {

    private Integer productId;

    public void setProductId(Integer productId) {
        this.productId = productId;
    }




    @Override
    public int doStartTag() throws JspException {

        if (productId != null) {
            JspWriter writer = pageContext.getOut();
            try {
                // getting hold of the link creator
      //          HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

                // The associated Hippo Bean
       //         KKProductDocument document = getProductDocumentById(request, productId);

                // create HstLink
        //        HstLink link = linkCreator.create(document, request.getRequestContext());

                // create the url String
         //       return link.toUrlForm(request.getRequestContext(), false);




                writer.write("TOTOTOT");
            } catch (IOException e) {
                throw new JspException("IOException while trying to write script tag", e);
            } catch (Exception e) {
                throw new JspException("KKAppException while formatting the price.", e);
            }
        }

        return SKIP_BODY;
    }
}
