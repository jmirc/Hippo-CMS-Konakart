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
