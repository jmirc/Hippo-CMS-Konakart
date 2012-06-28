package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.component.support.forms.FormMap;

/**
 * Encapsulate the business logic of a single step in the wizard process
 */
public interface Activity {

    static final String GLOBALMESSAGE = "globalmessage";

    /**
     * Set to true if the activity accepts an empty state, otherwise set to false
     *
     * @param acceptEmtpyState the state to set
     */
    void setAcceptEmptyState(boolean acceptEmtpyState);

    /**
     * Set the step accepted by this activity
     *
     * @param acceptState the step to set
     */
    void setAcceptState(String acceptState);

    /**
     * Check if the activity accepts this state.
     *
     * @param state the state to check
     * @return true if the state is accepted, false otherwise
     */
    boolean acceptState(String state);

    /**
     * Initialize the activity with the processor context object
     *
     * @param processorContext the processor context
     */
    void initialize(ProcessorContext processorContext);

    /**
     * Set the next state when the customer is logged
     *
     * @param nextLoggedState the next state to set.
     */
    void setNextLoggedState(String nextLoggedState);

    /**
     * Set the next state when the customer is not logged
     *
     * @param nextNonLoggedState the next state to set.
     */
    void setNextNonLoggedState(String nextNonLoggedState);

    /**
     * Set the template that will be rendered after the execution of the activity
     *
     * @param templateRenderPath the template to set
     */
    void setTemplateRenderPath(String templateRenderPath);

    /**
     * Compute the next state that will be executed by the processor
     * <p/>
     * This method is only called if the activity has accepted the state
     *
     * @return the next state.
     */
    String computeNextState();


    /**
     * Executed before the rendering of the Web page
     */
    void doBeforeRender() throws ActivityException;

    /**
     * Allows the component to process actions
     */
    void doAction() throws ActivityException;

    /**
     * Executed to validate if a form associated with the activity is valid.
     * <p/>
     * If not, the workflow process is stopped and any errors are sent back to the client.
     *
     * @return true if the form is valid, false otherwise
     */
    boolean doValidForm();

    /**
     * Executed when an activity has been previously processed.
     * This method could be used to add into the request information to inform that
     * the activity is already past.
     */
    void doAdditionalData();

    /**
     * Executed to set the template render path to the HstResponse
     */
    void doApplyTemplateRenderPath();

    /**
     * This method must be overrided if you implement a different onepagecheckout
     *
     * @return the onePageCheckout formMapFields
     */
    String[] getCheckoutFormMapFields();


    /**
     * @return the created form map for this activity
     */
    FormMap getFormMap();


}
