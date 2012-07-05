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

package org.onehippo.forge.konakart.hst.beans;


import org.hippoecm.hst.content.beans.ContentNodeBinder;
import org.hippoecm.hst.content.beans.ContentNodeBindingException;
import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoMirror;
import org.onehippo.forge.konakart.common.KKCndConstants;

import java.util.Calendar;

import static org.onehippo.forge.konakart.common.KKCndConstants.*;

@Node(jcrType = KKCndConstants.REVIEW_DOC_TYPE)
public class KKReviewDocument extends KKBaseDocument implements ContentNodeBinder {

    private String name;
    private String email;
    private Long rating;
    private Long customerId;
    private String comment;
    private String productUuid;
    private Calendar date;

    public Calendar getDate() {
        return (date == null) ? (Calendar) getProperty(REVIEW_DATE) : date;
    }

    public String getName() {
        return (name == null) ? (String) getProperty(REVIEW_NAME) : name;
    }

    public String getEmail() {
        return (email == null) ? (String) getProperty(REVIEW_EMAIL) : email;
    }

    public Long getRating() {
        return (rating == null) ? (Long) getProperty(REVIEW_RATING) : rating;
    }

    public Long getCustomerId() {
        return (customerId == null) ? (Long) getProperty(REVIEW_CUSTOMER_ID) : customerId;
    }

    public String getComment() {
        return (comment == null) ? (String) getProperty(REVIEW_COMMENT) : comment;
    }

    public String getProductUuid() {
        return productUuid;
    }

    public void setProductUuid(String uuid) {
        this.productUuid = uuid;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public KKProductDocument getProduct() {
        HippoBean bean = getBean(REVIEW_PRODUCT_LINK);
        if (!(bean instanceof HippoMirror)) {
            return null;
        }

        KKProductDocument prdBean = (KKProductDocument) ((HippoMirror) bean).getReferencedBean();

        if (prdBean == null) {
            return null;
        }
        return prdBean;
    }

    @Override
    public boolean bind(Object content, javax.jcr.Node node) throws ContentNodeBindingException {
        if (content instanceof KKReviewDocument) {
            try {
                KKReviewDocument review = (KKReviewDocument) content;
                node.setProperty(REVIEW_NAME, review.getName());
                node.setProperty(REVIEW_EMAIL, review.getEmail());
                node.setProperty(REVIEW_COMMENT, review.getComment());
                node.setProperty(REVIEW_RATING, review.getRating());
                node.setProperty(REVIEW_CUSTOMER_ID, review.getCustomerId());
                node.setProperty(REVIEW_DATE, Calendar.getInstance());

                javax.jcr.Node prdLinkNode;

                if (node.hasNode(REVIEW_PRODUCT_LINK)) {
                    prdLinkNode = node.getNode(REVIEW_PRODUCT_LINK);
                } else {
                    prdLinkNode = node.addNode(REVIEW_PRODUCT_LINK, "hippo:mirror");
                }
                prdLinkNode.setProperty("hippo:docbase", review.getProductUuid());

            } catch (Exception e) {
                log.error("Unable to bind the content to the JCR Node" + e.getMessage(), e);
                throw new ContentNodeBindingException(e);
            }

        }
        return true;
    }

}
