package org.onehippo.forge.konakart.workflow;

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

    @Persistent(column = "./myhippoproject:konakart/konakart:languageid")
    private Long languageId;

    public KonakartEventsWorkflowImpl() throws RemoteException {
    }

    @Override
    public void fire() throws WorkflowException, RepositoryException, RemoteException {

        if (productId == null) {
            return;
        }

        // load the konakart module config.
        HippoModuleConfig config = HippoModuleConfig.getConfig();

        // Try to update Update the product id
        try {
            KKEngine KKEngine = new KKEngine(config.getEngineConfig());

            // Try to retrieve the product by id
            CustomProductMgr productMgr = new CustomProductMgr(KKEngine.getEngine(), config.getLastUpdatedTime());

            // update the UUID
            productMgr.updateUUID(productId.intValue(), uuid);

        } catch (Exception e) {
            log.warn("Failed to synchronize the product for the UUID - " + uuid, e);
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
