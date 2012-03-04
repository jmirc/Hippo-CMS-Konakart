package org.onehippo.forge.konakart.workflow;

import com.konakart.al.KKAppEng;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.api.Document;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.ext.WorkflowImpl;
import org.hippoecm.repository.standardworkflow.WorkflowEventWorkflow;
import org.onehippo.forge.konakart.common.bl.CustomReviewMgr;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jdo.annotations.*;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * This event workflow is used to update the state of a review
 *
 * It will update the review's state using the following test cases:
 *   1) if hippo:request node exists and the hippostdpubwf:type is equals to publish - do nothing the review has been just added.
 *   1) if hippo:request node exists and the hippostdpubwf:type is equals to rejected - the review has been rejected.
 *   2) if hippo:request node doesn't exists, the review has been approved
 *
 */

@PersistenceCapable(identityType = IdentityType.DATASTORE, cacheable = "false", detachable = "false")
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class ReviewEventsWorkflowImpl extends WorkflowImpl implements WorkflowEventWorkflow {

    private static Logger log = LoggerFactory.getLogger(ReviewEventsWorkflowImpl.class);

    public static final String TYPE_REJECTED = "rejected";
    public static final String TYPE_PUBLISHED = "publish";

    @Persistent(column = "konakart:reviewkonakartid")
    private Long reviewkonakartid;

    @Persistent(column = "../hippo:request/hippostdpubwf:type")
    private String type;

    @Persistent(column = "hippostd:state")
    private String state;

    public ReviewEventsWorkflowImpl() throws RemoteException {
    }

    @Override
    public void fire() throws WorkflowException, RepositoryException, RemoteException {

        if (reviewkonakartid == null || reviewkonakartid == 0) {
            return;
        }

        // load the konakart module config.
        HippoModuleConfig config = HippoModuleConfig.getConfig();

        // Try to update Update the product id
        try {
            KKAppEng kkAppEng = KKEngine.get(config.getEngineConfig());

            CustomReviewMgr customReviewMgr = new CustomReviewMgr(kkAppEng.getEng());

            int reviewId = reviewkonakartid.intValue();

            //update the product
            int status = CustomReviewMgr.INVISIBLE_STATE;

            // Review has been accepted
            if (StringUtils.isEmpty(type)) {
                if (StringUtils.equals(state, "published")) {
                    status = CustomReviewMgr.VISIBLE_STATE;
                } else {
                    status = CustomReviewMgr.INVISIBLE_STATE;
                }
            } else if (StringUtils.equals(type, TYPE_REJECTED)) { // Check if the review has been rejected
                status = CustomReviewMgr.REJECT_STATE;
            }

            customReviewMgr.updateStatus(reviewId, status);

        } catch (Exception e) {
            log.warn("Failed to update the review's statut for the review's id : " + reviewkonakartid, e);
        }
    }

    @Override
    public void fire(Document document) throws WorkflowException, RepositoryException, RemoteException {
        // should not called
    }

    @Override
    public void fire(Iterator<Document> documentIterator) throws WorkflowException, RepositoryException, RemoteException {
        // should not called
    }
}
