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

package org.onehippo.forge.konakart.common.bl;

import com.konakart.app.KKException;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.FetchProductOptionsIf;
import com.konakart.appif.KKEngIf;
import com.konakart.bl.KKCriteria;
import com.konakart.bl.ProductMgr;
import com.konakart.bl.ReviewMgr;
import com.konakart.blif.ProductMgrIf;
import com.konakart.blif.ReviewMgrIf;
import com.konakart.om.BaseProductsPeer;
import com.konakart.om.BaseReviewsPeer;
import com.workingdogs.village.DataSetException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import java.util.Date;

public class CustomReviewMgr extends ReviewMgr implements ReviewMgrIf {

    public static final Integer VISIBLE_STATE = 0;
    public static final Integer INVISIBLE_STATE = 1;
    public static final Integer REJECT_STATE = 2;


    public CustomReviewMgr(KKEngIf kkEngIf) throws Exception {
        super(kkEngIf);
    }

    /**
     * Update the status of a review
     * @param reviewId id of the product to update
     * @param state the state of a review (visible, invisible, reject)
     * @throws Exception if any exception occurs.
     */
    public void updateStatus(Integer reviewId, int state) throws Exception {
        KKCriteria localKKCriteria = getNewCriteria(isMultiStoreShareProducts());
        localKKCriteria.add(BaseReviewsPeer.REVIEWS_ID, reviewId);
        localKKCriteria.add(BaseReviewsPeer.STATE, state);

        BasePeer.doUpdate(localKKCriteria);
    }
}