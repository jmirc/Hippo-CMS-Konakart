package org.onehippo.forge.konakart.workflow;

import org.hippoecm.repository.api.Workflow;

public interface KonakartWorkflow extends Workflow {

    public void notify(String event, String documentId, String userId);
}