package org.onehippo.forge.konakart.hst.wizard;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.core.component.HstRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all activities.
 */
public abstract class BaseActivity implements Activity{

    protected Logger log = LoggerFactory.getLogger(getClass());

    private String acceptState;
    private boolean acceptEmtpyState = false;
    private String nextLoggedState;
    private String nextNonLoggedState;

    protected ProcessorContext processorContext;
    protected FormMap formMap;
    protected KKAppEng kkAppEng;
    protected HstRequest hstRequest;


    protected BaseActivity() {
    }

    @Override
    public void initialize(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        this.hstRequest = processorContext.getSeedData().getRequest();

        formMap = new FormMap(hstRequest, getCheckoutFormMapFields());
        kkAppEng = processorContext.getSeedData().getKkBaseHstComponent().
                getKKAppEng(processorContext.getSeedData().getRequest());
    }

    @Override
    public FormMap getFormMap() {
        return formMap;
    }

    @Override
    public boolean acceptState(String state) {
        return (acceptEmtpyState && (state == null)) || ((state != null) && acceptState.equals(state));

    }

    /**
     * Set the step accepted by this activity
     * @param acceptState the step to set
     */
    public void setAcceptState(String acceptState) {
        this.acceptState = acceptState;
    }

    public String getAcceptState() {
        return acceptState;
    }

    public void setAcceptEmptyState(boolean acceptEmtpyState) {
        this.acceptEmtpyState = acceptEmtpyState;
    }

    @Override
    public String computeNextState() {
        if (processorContext.getSeedData().getKkBaseHstComponent().isGuestCustomer(hstRequest)) {
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
    public void doAdditionalData() {
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
