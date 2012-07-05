package org.onehippo.forge.konakart.hst.tags;

import com.konakart.al.KKAppException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.math.BigDecimal;

public class FormatPriceTag extends KKTagSupport {

    private BigDecimal price;

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public int doStartTag() throws JspException {

        if (price != null) {
            JspWriter writer = pageContext.getOut();
            try {
                writer.write(getKkAppEng().formatPrice(price));
            } catch (IOException e) {
                throw new JspException("IOException while trying to write script tag", e);
            } catch (KKAppException e) {
                throw new JspException("KKAppException while formatting the price.", e);
            }
        }

        return SKIP_BODY;
    }
}
