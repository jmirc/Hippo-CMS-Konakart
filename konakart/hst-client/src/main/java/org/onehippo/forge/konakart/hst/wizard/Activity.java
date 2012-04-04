package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.component.support.forms.FormMap;

/**
 * Encapsulate the business logic of a single step in the wizard process
 */
public interface Activity {

    static final String GLOBALMESSAGE = "globalmessage";

    /**
     * @return the spring bean name
     */
    String getBeanName();

    /**
     * Check if the activity accepts this state.
     *
     * @param state the state to check
     * @return true if the state is accepted, false otherwise
     */
    boolean acceptState(String state);

    /**
     * Initialize the activity with the processor context object
     * @param processorContext the processor context
     */
    void initialize(ProcessorContext processorContext);

    /**
     * Compute the next state that will be executed by the processor
     *
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
