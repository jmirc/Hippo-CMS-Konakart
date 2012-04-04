package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.component.support.forms.FormMap;

import java.util.List;

public interface Processor {

    /**
     * To be implemented by subclasses, ensures each Activity configured in this process
     * is supported.  This method is called by the Processor
     * for each Activity configured.  An implementing subclass should ensure the Activity
     *  type passed in is supported.  Implementors could possibly support multiple
     * types of activities.
     *
     * @param activity - activity instance of the configured activity
     * @return true if the activity is supported
     */
    boolean supports(Activity activity);

    /**
     * Allows the activity to do some business logic processing before rendering
     *
     * @param seedObject - data necessary for the workflow process to start execution
     */
    void doBeforeRender(SeedData seedObject) throws ActivityException;

    /**
     * Allows the component to process actions
     *
     * @param seedObject - data necessary for the workflow process to start execution
     */
    FormMap doAction(SeedData seedObject) throws ActivityException;

    /**
     * Compute the next state that will be executed by the processor
     *
     * @param seedObject - data necessary for the workflow process to compute the next state
     * @return the next state.
     */
    String computeNextState(SeedData seedObject) throws ActivityException;

    /**
     * Sets the collection of Activities to be executed by the Workflow Process
     *
     * @param activities ordered collection (List) of activities to be executed by the processor
     */
    void setActivities(List<Activity> activities);

}