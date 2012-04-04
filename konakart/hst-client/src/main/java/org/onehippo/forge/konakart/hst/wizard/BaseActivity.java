package org.onehippo.forge.konakart.hst.wizard;

import org.hippoecm.hst.component.support.forms.FormMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

/**
 * Base class for all activities.
 */
public abstract class BaseActivity implements Activity, BeanNameAware {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private String beanName;
    private String acceptState;
    private boolean acceptEmtpyState = false;
    private String nextLoggedState;
    private String nextNonLoggedState;

    protected ProcessorContext processorContext;
    protected FormMap formMap;


    protected BaseActivity() {
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    /**
     * @param beanName the spring bean name to set
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public void initialize(ProcessorContext processorContext) {
        this.processorContext = processorContext;

        formMap = new FormMap(processorContext.getSeedData().getRequest(), getCheckoutFormMapFields());
    }

    @Override
    public FormMap getFormMap() {
        return formMap;
    }

    @Override
    public boolean acceptState(String state) {
        return (acceptEmtpyState && state == null) && acceptState.equals(state);

    }

    /**
     * Set the step accepted by this activity
     * @param acceptState the step to set
     */
    public void setAcceptState(String acceptState) {
        this.acceptState = acceptState;
    }

    public void setAcceptEmptyState(boolean acceptEmtpyState) {
        this.acceptEmtpyState = acceptEmtpyState;
    }

    @Override
    public String computeNextState() {
        if (processorContext.getSeedData().getKkHstComponent().isGuestCustomer()) {
            return nextNonLoggedState;
        }

        return nextLoggedState;
    }

    public void setNextLoggedState(String nextLoggedState) {
        this.nextLoggedState = nextLoggedState;
    }

    public void setNextNonLoggedState(String nextNonLoggedState) {
        this.nextNonLoggedState = nextNonLoggedState;
    }

    @Override
    public boolean doValidForm() {
        // By default the form is valid.
        return true;
    }

    @Override
    public String[] getCheckoutFormMapFields() {
        return new String[0];
    }

    /**
     * Add a message to the formMap
     * @param name the name
     * @param message the message
     */
    protected void addMessage(String name, String message) {
        formMap.addMessage(name, message);
    }

}
