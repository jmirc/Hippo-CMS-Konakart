package org.onehippo.forge.konakart.workflow;

import com.konakart.al.KKAppEng;
import org.hippoecm.repository.api.Document;
import org.hippoecm.repository.api.WorkflowException;
import org.hippoecm.repository.ext.WorkflowImpl;
import org.hippoecm.repository.standardworkflow.WorkflowEventWorkflow;
import org.onehippo.forge.konakart.common.bl.CustomProductMgr;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jdo.annotations.*;
import java.rmi.RemoteException;
import java.util.Iterator;

/**
 * This event workflow is used to update the product's UUID to Konakart
 * each time the customer updates the product
 */

@PersistenceCapable(identityType = IdentityType.DATASTORE, cacheable = "false", detachable = "false")
@DatastoreIdentity(strategy = IdGeneratorStrategy.NATIVE)
@Inheritance(strategy = InheritanceStrategy.SUBCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class KonakartEventsWorkflowImpl extends WorkflowImpl implements WorkflowEventWorkflow {

    private static Logger log = LoggerFactory.getLogger(KonakartEventsWorkflowImpl.class);

    @Persistent(column = "jcr:uuid")
    private String uuid;

    @Persistent(column = "./myhippoproject:konakart/konakart:id")
    private Long productId;

    @Persistent(column = "hippostd:state")
    private String state;

    public KonakartEventsWorkflowImpl() throws RemoteException {
    }

    @Override
    public void fire() throws WorkflowException, RepositoryException, RemoteException {

        if (productId == null || productId == 0) {
            return;
        }

        // Retrieve the konakart module config.
        HippoModuleConfig config = HippoModuleConfig.getConfig();

        // Try to update Update the product id
        try {
            KKAppEng kkAppEng = KKEngine.get(config.getEngineConfig());

            // Try to retrieve the product by id
            CustomProductMgr productMgr = new CustomProductMgr(kkAppEng.getEng(), config.getLastUpdatedTime());

            // update the product
            boolean publishedState = (state != null) && (state.equals("published"));
            productMgr.updateStatus(productId.intValue(), publishedState);

        } catch (Exception e) {
            log.warn("Failed to update the statut for the following UUID product : " + uuid, e);
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
