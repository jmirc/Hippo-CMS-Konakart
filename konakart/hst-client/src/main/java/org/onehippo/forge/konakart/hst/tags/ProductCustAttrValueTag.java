package org.onehippo.forge.konakart.hst.tags;

import com.konakart.appif.ProdCustAttrIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ProductCustAttrValueTag extends TagSupport {

    private Logger log = LoggerFactory.getLogger(ProductCustAttrValueTag.class);

    private KKProductDocument product;
    private int custAttrId = -1;
    private String custAttrName;

    public void setProduct(KKProductDocument product) {
        this.product = product;
    }

    public void setCustAttrId(Integer custAttrId) {
        this.custAttrId = custAttrId;
    }

    public void setCustAttrName(String custAttrName) {
        this.custAttrName = custAttrName;
    }

    @Override
    public int doStartTag() throws JspException {

        if (product == null) {
            log.warn("Cannot get a custom attribute because no product is set");

            return EVAL_PAGE;
        }

        if (custAttrId ==  -1 && StringUtils.isEmpty(custAttrName)) {
            log.warn("Cannot get a custom attribute because no custAttrId or custAttrName is set");

            return EVAL_PAGE;
        }

        String value = null;

        ProdCustAttrIf[] customAttrArray = product.getProductIf().getCustomAttrArray();

        // Use the id to retrieve the custom attribute value
        if (custAttrId != -1) {
            value = customAttrArray[custAttrId].getValue();
        } else { // find by the name of the custom attribute
            for (ProdCustAttrIf prodCustAttrIf : customAttrArray) {
                if (StringUtils.equalsIgnoreCase(prodCustAttrIf.getName(), custAttrName)) {
                    value = prodCustAttrIf.getValue();
                }
            }
        }

        if (value != null) {
            JspWriter writer = pageContext.getOut();
            try {
                writer.write(value);
            } catch (IOException e) {
                throw new JspException("IOException while trying to write script tag", e);
            }
        }

        return SKIP_BODY;
    }
}
