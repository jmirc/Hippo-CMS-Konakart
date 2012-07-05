/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

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
